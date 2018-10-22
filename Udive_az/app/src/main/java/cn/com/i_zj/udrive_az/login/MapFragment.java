package cn.com.i_zj.udrive_az.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.ui.idregister.IDRegisterActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.ReserveActivity;
import cn.com.i_zj.udrive_az.map.fragment.BaseFragmentAdapter;
import cn.com.i_zj.udrive_az.map.fragment.CarsFragment;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.CarInfoResult;
import cn.com.i_zj.udrive_az.model.GetReservation;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.model.ReserVationBean;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.ToastUtil;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import pub.devrel.easypermissions.EasyPermissions;

import static android.widget.Toast.makeText;

public class MapFragment extends DBSBaseFragment implements AMapLocationListener, EasyPermissions.PermissionCallbacks {


  @BindView(R.id.map)
  MapView mMapView;

  @BindView(R.id.viewpager)
  ViewPager mViewPager;

  @BindView(R.id.tblayout)
  TabLayout tabLayout;

  @BindView(R.id.rl_where)
  RelativeLayout rl_where;

  @BindView(R.id.rl_info)
  RelativeLayout rl_info;

  @BindView(R.id.tv_pname)
  TextView tv_pname;

  @BindView(R.id.tv_paradress)
  TextView tv_adress;
  @BindView(R.id.tv_city)
  TextView tv_centerName;
  @BindView(R.id.tv_dis)
  TextView tv_dis;
  private AMap mAmap;
  //声明AMapLocationClient类对象
  public AMapLocationClient mLocationClient = null;
  private boolean isFirstLoc = true;

  private List<Fragment> fragments;
  //TODO 为啥要全局变量
  private ArrayList<ParksResult.DataBean> dataBeans = new ArrayList<>();

  private ArrayList<CarInfoResult.DataBean> carBeans = new ArrayList<>();
  private CarInfoResult.DataBean bunldBean;
  private ParksResult.DataBean buldParkBean;
  private LatLng myLocationlatLng;
  String parkid;
  String carid;
  @Override
  protected int getLayoutResource() {
//    MapUtils.setStatusBar(getActivity());

    return R.layout.activity_map;
  }


  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    init(savedInstanceState);
    fetchParks();
  }

  private void init(Bundle savedInstanceState) {

    initViewstMap(savedInstanceState);
    bunldBean=new CarInfoResult.DataBean();
    buldParkBean=new ParksResult.DataBean();
    if (!EasyPermissions.hasPermissions(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
      EasyPermissions.requestPermissions(this, "您必须授予我们定位权限才可以正常使用", 101, android.Manifest.permission.ACCESS_FINE_LOCATION);
    }
    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        bunldBean=carBeans.get(position);
        carid=String.valueOf(bunldBean.getId());
        CarsFragment carsFragment = (CarsFragment) fragments.get(position);
        carsFragment.refresh(carBeans.get(position));
//        tv_pname.setText(carBeans.get(position).getParkName());
//        tv_adress.setText(carBeans.get(position).getParkAddress());
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });


  }

  private void fetchParks() {
    UdriveRestClient.getClentInstance().getParks()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<ParksResult>() {
              @Override
              public void onSubscribe(Disposable d) {
              }

              @Override
              public void onNext(ParksResult result) {
                dataBeans.clear();
                dataBeans.addAll(result.getData());
                for (int i = 0; i < dataBeans.size(); i++) {
                  Marker marker = mAmap.addMarker(new MarkerOptions()
                          .position(new LatLng(dataBeans.get(i).getLatitude(), dataBeans.get(i).getLongitude()))
                          .icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(String.valueOf(dataBeans.get(i).getValidCarCount())))));
                  marker.setObject(dataBeans.get(i).getId());
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
  }
  @BindView(R.id.btn_yuding)
  Button btn_yuding;
  @OnClick(R.id.btn_yuding)
  public void onYudingClick(View view) {
    if(buldParkBean==null){
    }
    if(bunldBean!=null&&buldParkBean!=null){

      if(SessionManager.getInstance().getAuthorization()!=null){
        AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();

        if(accountInfo.data.idCardState == Constants.ID_AUTHORIZED_SUCCESS&&accountInfo.data.driverState==Constants.ID_AUTHORIZED_SUCCESS){
          if(accountInfo.data.depositState==2){
            yuyue();
          }else {
            ToastUtil.show(getActivity(),"请先缴纳押金");
          }

        }else {
          if (accountInfo.data.idCardState == Constants.ID_UNDER_REVIEW) {
            makeText(getActivity(), "正在审核中", Toast.LENGTH_SHORT).show();
            return;
          } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage("您没还没有实名认证")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {

                      }
                    })
                    .setPositiveButton("立即认证", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), IDRegisterActivity.class);
                        intent.putExtra(Constants.INTENT_TITLE, Constants.INTENT_REGISTER_ID);
                        startActivity(intent);
                      }
                    })
                    .create().show();
          }
          if (accountInfo.data.driverState == Constants.ID_UNDER_REVIEW) {
            makeText(getActivity(), "正在审核中", Toast.LENGTH_SHORT).show();
            return;
          } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage("您还没有绑定驾驶证")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {

                      }
                    })
                    .setPositiveButton("立即绑定", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), IDRegisterActivity.class);
                        intent.putExtra(Constants.INTENT_TITLE, Constants.INTENT_DRIVER_INFO);
                        startActivity(intent);
                      }
                    })
                    .create().show();
          }
        }

      }else {
        LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
        loginDialogFragment.show(getChildFragmentManager(), "login");
      }

    }else {
      ToastUtils.showShort("请先选择车辆");
    }
  }
  private void yuyue() {
    LogUtils.e("222");
    Map<String,String> map=new HashMap<>();
    map.put("carId",carid);
    map.put("startParkId",parkid);
    String token=SessionManager.getInstance().getAuthorization();
    LogUtils.e(token);
    LogUtils.e(carid+"--"+parkid);
    UdriveRestClient.getClentInstance().reservation(token,map)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<ReserVationBean>() {
              @Override
              public void onSubscribe(Disposable d) {
              }

              @Override
              public void onNext(ReserVationBean result) {
                if(result!=null){
                  if(result.getCode()==1){
                    if(!result.getData().equals("")){
                      Intent intent=new Intent(getActivity(),ReserveActivity.class);
                      intent.putExtra("type","1");
                      intent.putExtra("bunld",bunldBean);
                      intent.putExtra("bunldPark",buldParkBean);
                      intent.putExtra("id",result.getData().getId()+"");
                      startActivity(intent);
                    }

                  }
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
  }
  //初始化地图
  private void initViewstMap(Bundle savedInstanceState) {
    mMapView.onCreate(savedInstanceState);// 此方法必须重写
    mAmap = mMapView.getMap();
//    MapUtils.setMapCustomStyleFile(getContext(), mAmap);
    //初始化定位
    mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
    //设置定位回调监听
    mLocationClient.setLocationListener(this);
    //启动定位
    mLocationClient.startLocation();
    //测试marker


    mAmap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        rl_where.setVisibility(View.GONE);
        for (int i = 0; i < dataBeans.size(); i++) {
          if(dataBeans.get(i).getId()==Integer.parseInt(marker.getObject().toString())){
            parkid=String.valueOf(dataBeans.get(i).getId());
            buldParkBean=dataBeans.get(i);
            tv_pname.setText(dataBeans.get(i).getName());
            tv_adress.setText(dataBeans.get(i).getName());
            float distance = AMapUtils.calculateLineDistance(
                    myLocationlatLng,
                    new LatLng(dataBeans.get(i).getLatitude(),dataBeans.get(i).getLongitude())
            );
            int dis= (int) (distance/1000);
            tv_dis.setText(dis+"km");
            break;
          }
        }
        LogUtils.e(SessionManager.getInstance().getAuthorization());
        UdriveRestClient.getClentInstance().getCarInfo("/mobile/car/getReservationList/" + marker.getObject())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CarInfoResult>() {
                  @Override
                  public void onSubscribe(Disposable d) {
                  }

                  @Override
                  public void onNext(CarInfoResult result) {

                    if (result.getData().size() != 0) {
                      carBeans.clear();
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
                        LogUtils.e(result.getData().get(i).getPlateNumber());
                        CarsFragment carsFragment = CarsFragment.newInstance(i, result.getData().get(i));
                        fragments.add(carsFragment);
                        imgs.add(R.drawable.view_selector);

                      }

                      bunldBean=result.getData().get(0);
                      carid=String.valueOf(bunldBean.getId());
                      BaseFragmentAdapter myPagerAdapter = new BaseFragmentAdapter(getFragmentManager(), fragments, imgs, getContext());
                      mViewPager.setAdapter(myPagerAdapter);

                      tabLayout.setupWithViewPager(mViewPager);
                      //增加指示器个数
                      for (int i = 0; i < tabLayout.getTabCount(); i++) {
                        TabLayout.Tab tab = tabLayout.getTabAt(i);
                        if (tab != null) {
                          tab.setCustomView(myPagerAdapter.getTabView(i));
                        }
                      }


                      CarsFragment carsFragment = (CarsFragment) fragments.get(0);
                      carsFragment.refresh(result.getData().get(0));
                      rl_info.setVisibility(View.VISIBLE);
                      btn_yuding.setVisibility(View.VISIBLE);
                    } else {
                      rl_info.setVisibility(View.GONE);
                      rl_where.setVisibility(View.GONE);
                      btn_yuding.setVisibility(View.GONE);
                      ToastUtils.showShort("该停车场暂无车辆信息");
                    }

                  }

                  @Override
                  public void onError(Throwable e) {
                    LogUtils.e("111");
                    e.printStackTrace();
                  }

                  @Override
                  public void onComplete() {
                    LogUtils.e("222");

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
        mAmap.moveCamera(CameraUpdateFactory.zoomTo(17));
        //将地图移动到定位点
        tv_centerName.setText(aMapLocation.getCity());
        myLocationlatLng=new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
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
  public void onResume() {
    super.onResume();
    //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
    mMapView.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
    mMapView.onPause();
  }

  @Override
  public void onStop() {
    super.onStop();
    mLocationClient.stopLocation();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
    mMapView.onDestroy();
    mLocationClient.onDestroy();
  }

  //在marker上绘制文字
  protected Bitmap getMyBitmap(String pm_val) {

    Bitmap bitmap = BitmapDescriptorFactory.fromResource(
            R.drawable.marker).getBitmap();
    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
            bitmap.getHeight());
    Canvas canvas = new Canvas(bitmap);
    TextPaint textPaint = new TextPaint();
    textPaint.setAntiAlias(true);
    textPaint.setTextSize(30f);
    textPaint.setColor(getResources().getColor(R.color.white));
    canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
    canvas.drawLine(canvas.getWidth() / 2, 0, canvas.getWidth() / 2, 0, textPaint);
    float baseLineY = Math.abs(textPaint.ascent() + textPaint.descent()) / 2;
    float textWidth = textPaint.measureText(pm_val);
    canvas.drawText(pm_val, -textWidth / 2, baseLineY - 5, textPaint);// 设置bitmap上面的文字位置
    return bitmap;
  }
}

