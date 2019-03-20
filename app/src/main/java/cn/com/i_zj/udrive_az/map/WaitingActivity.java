package cn.com.i_zj.udrive_az.map;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.MainActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.constant.ParkType;
import cn.com.i_zj.udrive_az.map.adapter.PictureBeforeActivity;
import cn.com.i_zj.udrive_az.model.AreaInfo;
import cn.com.i_zj.udrive_az.model.DoorBean;
import cn.com.i_zj.udrive_az.model.GetReservation;
import cn.com.i_zj.udrive_az.model.ParkDetailResult;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.CarVosBean;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.overlay.WalkRouteOverlay;
import cn.com.i_zj.udrive_az.utils.AMapUtil;
import cn.com.i_zj.udrive_az.utils.CarTypeImageUtils;
import cn.com.i_zj.udrive_az.utils.Constants2;
import cn.com.i_zj.udrive_az.utils.ScreenManager;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.dialog.NavigationDialog;
import cn.com.i_zj.udrive_az.widget.CommonAlertDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 等待用车界面
 */
public class WaitingActivity extends DBSBaseActivity implements AMapLocationListener, RouteSearch.OnRouteSearchListener {

    @BindView(R.id.header_left)
    LinearLayout header_left;
    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.header_image)
    ImageView header_image;

    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.iv_car)
    ImageView mIvCar;
    @BindView(R.id.tv_carnum)
    TextView tvCarnum;
    @BindView(R.id.tv_color)
    TextView tvColor;
    @BindView(R.id.tv_gonglishu)
    TextView tvgonglishu;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.rl_dengdai)
    View mapBottom;

    private AMap mAmap;
    private LatLng mobileLocation;
    private RouteSearch mRouteSearch;
    private boolean isFirstLoc = true;
    private AMapLocationClient mLocationClient;
    private WalkRouteOverlay walkRouteOverlay;
    private int paddingSize = 150;
    private List<LatLng> allLatLngs = new ArrayList<>();

    //从预约界面进来的值
    private CarVosBean bunldBean;  //车辆信息
    private ParksResult.DataBean fromPark;  //停车场信息
    //预约信息bean
    private GetReservation reservationBean;  //预约信息

    //倒计时相关
    private int minute = 15;
    private int second = 0;
    private long startTime = 0;
    private CountDownTimer countDownTimer;

    private void startTimerCount() {
        if (null != countDownTimer) {
            countDownTimer.cancel();
        }
        long time = System.currentTimeMillis() - startTime;
        if (time > 1000 * 60 * 15) {
            ToastUtils.showShort("预约结束");
            startActivity(new Intent(WaitingActivity.this, MainActivity.class));
            finish();
            return;
        }
        countDownTimer = new CountDownTimer(1000 * 60 * 15 - time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (null != tv_time) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("00 : mm : ss");//12小时制
                    Date date = new Date();
                    date.setTime(millisUntilFinished);
                    tv_time.setText(simpleDateFormat.format(date));
                }
            }

            @Override
            public void onFinish() {
                if (null != tv_time) {
                    finish();// 预约时间结束
                    startActivity(new Intent(WaitingActivity.this, MainActivity.class));
                }
            }
        };
        countDownTimer.start();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_waiting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenManager.getScreenManager().pushActivity(this);
        MapUtils.statusBarColor(this);
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(this, "您必须授予我们定位权限才可以正常使用", 101, Manifest.permission.ACCESS_FINE_LOCATION);
        }

        mMapView.onCreate(savedInstanceState);
        initViews();
        parkDetail();
    }

    private void initViews() {
        header_left.setVisibility(View.INVISIBLE);
        header_title.setText("等待用车");
        header_image.setImageResource(R.mipmap.ic_service);

        reservationBean = (GetReservation) getIntent().getSerializableExtra("bunld");
        if (reservationBean == null) {
            ToastUtils.showShort("预约信息获取失败");
            startActivity(MainActivity.class);
            finish();
            return;
        }
        fromPark = new ParksResult.DataBean();
        fromPark.setId(reservationBean.getData().getParkId());
        fromPark.setParkID(reservationBean.getData().getParkId());
        fromPark.setLongitude(reservationBean.getData().getLongitude());
        fromPark.setLatitude(reservationBean.getData().getLatitude());
        tvCarnum.setText(reservationBean.getData().getPlateNumber());
        tvColor.setText(reservationBean.getData().getCarColor());
        tvgonglishu.setText("" + reservationBean.getData().getRemainderRange());
        tv_address.setText(reservationBean.getData().getName());
        startTime = reservationBean.getData().getCreateTime();
        Glide.with(WaitingActivity.this).load(CarTypeImageUtils.getCarImageByBrand(reservationBean.getData().getBrand(), reservationBean.getData().getCarColor())).into(mIvCar);
        startTimerCount();
        tv_time.setText("00 : " + minute + " : " + second);
        initMap();
    }

    private void initMap() {
        mAmap = mMapView.getMap();
        UiSettings uiSettings = mAmap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        MapUtils.setMapCustomStyleFile(this, mAmap);
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //启动定位
        mLocationClient.startLocation();
    }

    @OnClick({R.id.header_right, R.id.btn_yuding, R.id.iv_na, R.id.rl_cancel, R.id.rl_xunche})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_right:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + getResources().getString(R.string.about_phone));
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.btn_yuding:
                Intent intent1 = new Intent();
                intent1.setClass(this, PictureBeforeActivity.class);
                intent1.putExtra("destinationParkId", String.valueOf(fromPark.getId()));
                startActivity(intent1);
                break;
            case R.id.iv_na:
                if (!new File("/data/data/com.baidu.BaiduMap").exists()
                        && !new File("/data/data/com.autonavi.minimap").exists()) {
                    ToastUtils.showShort("尚未安装高德或百度地图");
                    return;
                }
                if (fromPark != null) {
                    NavigationDialog navigationDialog = new NavigationDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("lng", String.valueOf(fromPark.getLongitude()));
                    bundle.putString("lat", String.valueOf(fromPark.getLatitude()));
                    navigationDialog.setArguments(bundle);
                    navigationDialog.show(getSupportFragmentManager(), "navigation");
                } else {
                    ToastUtils.showShort("尚未选取取车点");
                }
                break;
            case R.id.rl_cancel:
                CommonAlertDialog.builder(this)
                        .setTitle("取消预定")
                        .setMsg("确定要取消预定么？")
                        .setMsgCenter(true)
                        .setPositiveButton("先不", null)
                        .setNegativeButton("取消", v -> cancelReservation())
                        .build()
                        .show();
                break;
            case R.id.rl_xunche:
                searchCarBySound();
                break;
        }
    }

    private void cancelReservation() {
        showProgressDialog();
        Map<String, String> map = new HashMap<>();
        map.put("reservationId", reservationBean.getData().getReservationId() + "");
        UdriveRestClient.getClentInstance().cancelReservation(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DoorBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DoorBean bean) {
                        dissmisProgressDialog();
                        if (bean != null) {
                            if (bean.getCode() == 1) {
                                ToastUtils.showShort("取消订单成功");
                                startActivity(new Intent(WaitingActivity.this, MainActivity.class));
                                finish();
                            } else {
                                ToastUtils.showShort(bean.getMessage());
                            }
                        } else {
                            ToastUtils.showShort("取消订单失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        ToastUtils.showShort("取消订单失败");
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    private void searchCarBySound() {
        showProgressDialog();
        UdriveRestClient.getClentInstance().searchCarByReservation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DoorBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DoorBean bean) {
                        dissmisProgressDialog();
                        if (bean != null) {
                            if (bean.getCode() == 1) {
                                ToastUtils.showShort("寻车成功");
                            } else {
                                ToastUtils.showShort(bean.getMessage());
                            }
                        } else {
                            ToastUtils.showShort("寻车失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        ToastUtils.showShort("寻车失败");
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    private void parkDetail() {
        if (fromPark == null) {
            return;
        }
        UdriveRestClient.getClentInstance().getParkDetail(fromPark.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ParkDetailResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ParkDetailResult parkDetailResult) {
                        if (parkDetailResult.getCode() != 1) {
                            return;
                        }
                        //画区域
                        ParkDetailResult.DataBean.ParkAreaBean parkAreaBean = parkDetailResult.getData().getParkArea();
                        if (parkAreaBean != null) {
                            switch (parkAreaBean.getParkType()) {
                                case ParkType.Circle:
                                    JsonObject circleObject = (JsonObject) new JsonParser().parse(parkAreaBean.getArea());
                                    String center = circleObject.get("center").getAsString();
                                    if (TextUtils.isEmpty(center)) {
                                        return;
                                    }
                                    LatLng latLng = new LatLng(Double.parseDouble(center.split(",")[1]), Double.parseDouble(center.split(",")[0]));
                                    allLatLngs.add(latLng);
                                    mAmap.addCircle(new CircleOptions()
                                            .center(latLng)
                                            .radius(circleObject.get("radius").getAsInt())
                                            .fillColor(Color.parseColor("#FFCBE2FF"))
                                            .strokeColor(Color.parseColor("#FF0075FF"))
                                            .strokeWidth(1));
                                    break;
                                case ParkType.Polygon:
                                case ParkType.Rectangle:
                                    List<AreaInfo> areaInfos = new Gson().fromJson(parkAreaBean.getArea(), new TypeToken<List<AreaInfo>>() {
                                    }.getType());
                                    if (areaInfos == null || areaInfos.size() == 0) {
                                        return;
                                    }
                                    List<LatLng> latLngs = new ArrayList<>();
                                    for (AreaInfo info : areaInfos) {
                                        latLngs.add(new LatLng(info.getLat(), info.getLng()));
                                    }
                                    allLatLngs.addAll(latLngs);
                                    mAmap.addPolygon(new PolygonOptions()
                                            .addAll(latLngs)
                                            .fillColor(Color.parseColor("#FFCBE2FF"))
                                            .strokeColor(Color.parseColor("#FF0075FF"))
                                            .strokeWidth(1));
                                    break;
                            }
                        }
                        //画小车
                        if (bunldBean != null) {
                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(bunldBean.getLatitude(), bunldBean.getLongitude()));
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pic_bora));
                            Marker carMarker = mAmap.addMarker(markerOptions);
                            carMarker.setClickable(false);
                            carMarker.setRotateAngle(bunldBean.getDirection());
                        }
                        drawMap();
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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            mobileLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            if (isFirstLoc) {
                //这段代码是修改样式去掉阴影圆圈地图的
                MyLocationStyle myLocationStyle = new MyLocationStyle();
                myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 自定义精度范围的圆形边框颜色
                myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明的时候就可以去掉园区区域了
                myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_arrow));
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
                mAmap.setMyLocationStyle(myLocationStyle);
                mAmap.setMyLocationEnabled(true);

                mLocationClient.stopLocation();
                //将地图移动到定位点
                mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(mobileLocation, Constants2.LocationZoom));
                isFirstLoc = false;
                drawMap();
            } else {
                mLocationClient.stopLocation();
                mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(mobileLocation, Constants2.LocationZoom));
            }
        }
    }

    private void drawMap() {
        if (walkRouteOverlay != null) {
            walkRouteOverlay.removeFromMap();
            walkRouteOverlay = null;
        }
        if (mobileLocation == null) {
            return;
        }
        //调整视角
        allLatLngs.add(mobileLocation);
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLng : allLatLngs) {
            boundsBuilder.include(latLng);
        }
        int bottomPadding = ToolsUtils.getWindowHeight(this) - mapBottom.getTop() + paddingSize;
        if (ToolsUtils.checkDeviceHasNavigationBar(this)) {
            bottomPadding += ToolsUtils.getNavigationBarHeight(this);
        }
        mAmap.animateCamera(CameraUpdateFactory.newLatLngBoundsRect(boundsBuilder.build(), paddingSize, paddingSize, paddingSize, bottomPadding));
        //绘制线路
        if (fromPark != null) {
            mRouteSearch = new RouteSearch(WaitingActivity.this);
            mRouteSearch.setRouteSearchListener(WaitingActivity.this);
            final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(AMapUtil.convertToLatLonPoint(mobileLocation), new LatLonPoint(fromPark.getLatitude(), fromPark.getLongitude()));
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
            mRouteSearch.calculateWalkRouteAsyn(query);
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int errorCode) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        if (i == 1000) {
            if (walkRouteOverlay != null) {
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay = null;
            }
            walkRouteOverlay = new WalkRouteOverlay(this, mAmap, walkRouteResult.getPaths().get(0),
                    walkRouteResult.getStartPos(), walkRouteResult.getTargetPos(), null);
            walkRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
            walkRouteOverlay.addToMap();
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        mMapView.onDestroy();
        mLocationClient.onDestroy();

        if (null != countDownTimer) {
            countDownTimer.cancel();
        }
    }
}
