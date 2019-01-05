package cn.com.i_zj.udrive_az.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.com.i_zj.udrive_az.BaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.fragment.BaseFragmentAdapter;
import cn.com.i_zj.udrive_az.map.fragment.CarsFragment;
import cn.com.i_zj.udrive_az.model.CarInfoResult;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.Constants2;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

@Deprecated
public class MapActivity extends BaseActivity implements AMapLocationListener, EasyPermissions.PermissionCallbacks {
  private MapView mMapView;
  private AMap mAmap;
  //声明AMapLocationClient类对象
  public AMapLocationClient mLocationClient = null;
  private boolean isFirstLoc = true;

  private ViewPager mViewPager;
  private List<Fragment> fragments;
  private CarsFragment myFragment1;
  private CarsFragment myFragment2;
  private TabLayout tabLayout;
  private RelativeLayout rl_where;
  private LinearLayout ll_info;
  private ArrayList<ParksResult.DataBean> dataBeans;
  private ArrayList<CarInfoResult.DataBean> carBeans;
  private TextView tv_pname;
  private TextView tv_adress;
  private CarInfoResult.DataBean bunldBean;
  private ParksResult.DataBean buldParkBean;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    BarUtils.setStatusBarAlpha(this, 0);
    MapUtils.statusBarColor(this);
    dataBeans = new ArrayList<>();
    initFragments();
    initViews();
    initViewstMap(savedInstanceState);
    if (!EasyPermissions.hasPermissions(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
      EasyPermissions.requestPermissions(this, "您必须授予我们定位权限才可以正常使用", 101, android.Manifest.permission.ACCESS_FINE_LOCATION);
    }
    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
//                switch (position){
////                    case 0:
////                        myFragment1.refresh(0);
////                        break;
////                    case 1:
////                        myFragment2.refresh(1);
//                        break;
//                }
        bunldBean = carBeans.get(position);
        CarsFragment carsFragment = (CarsFragment) fragments.get(position);
//        carsFragment.refresh(carBeans.get(position));
        tv_pname.setText(carBeans.get(position).getParkName());
        tv_adress.setText(carBeans.get(position).getParkAddress());
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
    UdriveRestClient.getClentInstance().getParks()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(new Observer<ParksResult>() {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(ParksResult result) {
          dataBeans.addAll(result.getData());
          for (int i = 0; i < dataBeans.size(); i++) {
            Marker marker = mAmap.addMarker(new MarkerOptions()
              .position(new LatLng(dataBeans.get(i).getLatitude(), dataBeans.get(i).getLongitude()))
              .icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(String.valueOf(dataBeans.get(i).getValidCarCount())))));
            marker.setObject(dataBeans.get(i).getId());
          }
//                        LatLng latLng1=new LatLng(Double.parseDouble("34.18984"),Double.parseDouble("109.002943"));
//                        LatLng latLng2=new LatLng(Double.parseDouble("34.190585"),Double.parseDouble("109.003823"));
//                        LatLng latLng3=new LatLng(Double.parseDouble("34.189538"),Double.parseDouble("109.003823"));
//
//                        Marker marker1= mAmap.addMarker(new MarkerOptions()
//                                .position(latLng1)
//                                .icon(BitmapDescriptorFactory.fromBitmap(bitmapWithCenterText("99"))));
////        localMarker.setObject(3);
////
//                        Marker marker2=mAmap.addMarker(new MarkerOptions()
//                                .position(latLng2)
//                                .icon(BitmapDescriptorFactory.fromBitmap(bitmapWithCenterText("13"))));
//
//                        Marker marker3=mAmap.addMarker(new MarkerOptions()
//                                .position(latLng3)
//                                .icon(BitmapDescriptorFactory.fromBitmap(bitmapWithCenterText("5"))));
        }

        @Override
        public void onError(Throwable e) {
          e.printStackTrace();
        }

        @Override
        public void onComplete() {

        }
      });


  }

  private void initViews() {
    mMapView = findViewById(R.id.map);
    mViewPager = findViewById(R.id.viewpager);
    tabLayout = findViewById(R.id.tblayout);
    rl_where = findViewById(R.id.rl_where);
    ll_info = findViewById(R.id.ll_info);
    tv_pname = findViewById(R.id.tv_pname);
    tv_adress = findViewById(R.id.tv_paradress);
    findViewById(R.id.btn_yuding).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (buldParkBean == null) {
        }
        if (bunldBean != null && buldParkBean != null) {
          Intent intent = new Intent(MapActivity.this, ReserveActivity.class);
          intent.putExtra(Constants.INTENT_KEY_RESERVE_DATA, new Gson().toJson(bunldBean));
          intent.putExtra(Constants.INTENT_KEY_BUNLD_PARK, new Gson().toJson(buldParkBean));
          startActivity(intent);
        } else {
          ToastUtils.showShort("请先选择车辆");
        }

      }
    });

  }

  private void initFragments() {

  }

  //初始化地图
  private void initViewstMap(Bundle savedInstanceState) {
    mMapView.onCreate(savedInstanceState);// 此方法必须重写
    mAmap = mMapView.getMap();
//    MapUtils.setMapCustomStyleFile(this, mAmap);
    //初始化定位
    mLocationClient = new AMapLocationClient(getApplicationContext());
    //设置定位回调监听
    mLocationClient.setLocationListener(this);
    //启动定位
    mLocationClient.startLocation();
    //测试marker


    mAmap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        rl_where.setVisibility(View.VISIBLE);
        for (int i = 0; i < dataBeans.size(); i++) {
          if (dataBeans.get(i).getId() == Integer.parseInt(marker.getObject().toString())) {
            buldParkBean = dataBeans.get(i);
            break;
          }
        }

        UdriveRestClient.getClentInstance().getCarInfo("/mobile/car/getReservationList/" + marker.getObject())
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Observer<CarInfoResult>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(CarInfoResult result) {
              if (null != result.getData() && result.getData().size() != 0) {
                carBeans = new ArrayList<>();
                carBeans.addAll(result.getData());
                if (fragments != null) {
                  for (int i = 0; i < fragments.size(); i++) {
                    fragments.get(i).onDestroy();
                  }
                  if (fragments.size() > 0) {
                    fragments.clear();
                  }
                }

                //初始化list
                fragments = new ArrayList<>();
                ArrayList<Integer> imgs = new ArrayList<>();
                //for循环给list增加fragment
                for (int i = 0; i < result.getData().size(); i++) {
//                  CarsFragment carsFragment = CarsFragment.newInstance(i, result.getData().get(i));
//                  fragments.add(carsFragment);
                  imgs.add(R.drawable.view_selector);

                }
                bunldBean = result.getData().get(0);
//                                    EventBus.getDefault().post(result.getData().get(0));
                //初始化adapter
                BaseFragmentAdapter myPagerAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragments, imgs, MapActivity.this);
                mViewPager.setAdapter(myPagerAdapter);

                tabLayout.setupWithViewPager(mViewPager);
                //增加指示器个数
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                  TabLayout.Tab tab = tabLayout.getTabAt(i);
                  if (tab != null) {
                    tab.setCustomView(myPagerAdapter.getTabView(i));
                  }
                }
                tv_pname.setText(result.getData().get(0).getParkName());
                tv_adress.setText(result.getData().get(0).getParkAddress());
                ll_info.setVisibility(View.VISIBLE);
              } else {
                ll_info.setVisibility(View.GONE);
                rl_where.setVisibility(View.GONE);
                ToastUtils.showShort("该停车场暂无车辆信息");
              }

            }

            @Override
            public void onError(Throwable e) {
              e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
          });
        return false;
      }
    });
  }

  @Override
  public void onLocationChanged(AMapLocation aMapLocation) {
    if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
      if (isFirstLoc) {
//                这段代码是修改样式去掉阴影圆圈地图的
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 自定义精度范围的圆形边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明的时候就可以去掉园区区域了
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        mAmap.setMyLocationStyle(myLocationStyle);
        mAmap.setMyLocationEnabled(true);
        //去掉放大缩小 增加回到当前位置
        UiSettings uiSettings = mAmap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);

        mLocationClient.stopLocation();
        mAmap.moveCamera(CameraUpdateFactory.zoomTo(Constants2.LocationZoom));
        //将地图移动到定位点
        mAmap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
        isFirstLoc = false;
      }
    }
  }

  @Override
  public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    ToastUtils.showShort("获取成功");
  }

  @Override
  public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
    ToastUtils.showShort("您拒绝了我们必要的一些权限,请在设置中授予定位权限！");
  }

  @Override
  protected void onResume() {
    super.onResume();
    //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
    mMapView.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
    mMapView.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mLocationClient.stopLocation();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
    mMapView.onDestroy();
    mLocationClient.onDestroy();
  }

  //在marker上绘制文字
  protected Bitmap getMyBitmap(String pm_val) {

    Bitmap bitmap = BitmapDescriptorFactory.fromResource(
      R.mipmap.ic_cheweishu_llinshi).getBitmap();
    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
      bitmap.getHeight());
    Canvas canvas = new Canvas(bitmap);
    TextPaint textPaint = new TextPaint();
    textPaint.setAntiAlias(true);
    textPaint.setTextSize(12);
    textPaint.setColor(getResources().getColor(R.color.white));
    canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
    canvas.drawLine(canvas.getWidth() / 2, 0, canvas.getWidth() / 2, 0, textPaint);
    float baseLineY = Math.abs(textPaint.ascent() + textPaint.descent()) / 2;
    float textWidth = textPaint.measureText(pm_val);
    canvas.drawText(pm_val, -textWidth / 2, baseLineY - 5, textPaint);// 设置bitmap上面的文字位置
    return bitmap;
  }

  public static Bitmap drawableToBitmap(Drawable drawable) {
    // 取 drawable 的长宽
    int w = drawable.getIntrinsicWidth();
    int h = drawable.getIntrinsicHeight();

    // 取 drawable 的颜色格式
    Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
      : Bitmap.Config.RGB_565;
    // 建立对应 bitmap
    Bitmap bitmap = Bitmap.createBitmap(w, h, config);
    // 建立对应 bitmap 的画布
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, w, h);
    // 把 drawable 内容画到画布中
    drawable.draw(canvas);
    return bitmap;
  }
}
