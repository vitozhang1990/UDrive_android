package cn.com.i_zj.udrive_az.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.blankj.utilcode.util.LogUtils;
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
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.ui.payment.ActConfirmOrder;
import cn.com.i_zj.udrive_az.lz.ui.payment.PaymentActivity;
import cn.com.i_zj.udrive_az.map.adapter.ChooseParkActivity;
import cn.com.i_zj.udrive_az.map.adapter.PictureAfterActivity;
import cn.com.i_zj.udrive_az.map.adapter.PictureBeforeActivity;
import cn.com.i_zj.udrive_az.model.AreaInfo;
import cn.com.i_zj.udrive_az.model.CarInfoEntity;
import cn.com.i_zj.udrive_az.model.CheckCarResult;
import cn.com.i_zj.udrive_az.model.CreateOderBean;
import cn.com.i_zj.udrive_az.model.DoorBean;
import cn.com.i_zj.udrive_az.model.GetReservation;
import cn.com.i_zj.udrive_az.model.OrderDetailResult;
import cn.com.i_zj.udrive_az.model.ParkDetailResult;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.CarVosBean;
import cn.com.i_zj.udrive_az.model.ParkKey;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.model.UnFinishOrderResult;
import cn.com.i_zj.udrive_az.model.ret.RetParkObj;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.overlay.DrivingRouteOverlay;
import cn.com.i_zj.udrive_az.overlay.WalkRouteOverlay;
import cn.com.i_zj.udrive_az.utils.AMapUtil;
import cn.com.i_zj.udrive_az.utils.CarTypeImageUtils;
import cn.com.i_zj.udrive_az.utils.Constants2;
import cn.com.i_zj.udrive_az.utils.ScreenManager;
import cn.com.i_zj.udrive_az.utils.dialog.NavigationDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 预约中&行程中
 * <p>
 * 通过type来区分不同的进入本页面方式
 * 预约中显示停车场范围及预约车辆
 * <p>
 * Marker类：
 * 停车场Marker不用变化，仅可能更加状态转为【终】
 * 预约中->行程中，停车场范围需要remove
 * <p>
 * 导航：
 * 1。预定中，需要Walk导航到起始点
 * 2。行程中，需要Drive导航到终点
 * <p>
 * <p>
 * ⚠️
 * 创建订单的时候使用起始停车场作为目的地停车场
 */
public class ReserveActivity extends DBSBaseActivity implements AMapLocationListener, RouteSearch.OnRouteSearchListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_canel)
    TextView tvCanel;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.tv_address_type)
    TextView tv_address_type;
    @BindView(R.id.tv_carnum)
    TextView tvCarnum;
    @BindView(R.id.tv_color)
    TextView tvColor;
    @BindView(R.id.tv_gonglishu)
    TextView tvgonglishu;
    @BindView(R.id.iv_car)
    ImageView mIvCar;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.operate_btn_layout)
    LinearLayout operateBtnLayout;
    @BindView(R.id.time_down_layout)
    LinearLayout timeDownLayout;
    @BindView(R.id.btn_yuding)
    Button btnYuding;
    @BindView(R.id.map)
    MapView mMapView;

    private AMap mAmap;
    private AMapLocationClient mLocationClient;
    private boolean isFirstLoc = true;
    private RouteSearch mRouteSearch;
    private Map<ParkKey, Marker> markerMap = new HashMap();
    private LatLng mobileLocation;
    private DrivingRouteOverlay drivingRouteOverlay;
    private WalkRouteOverlay walkRouteOverlay;
    private Circle circle;//停车场范围圆
    private Polygon polygon;//停车场范围多边形
    private Marker carMarker;

    //判断从那个界面进来
    private String type; //1：预约  2：重新进入APP 进入预约界面  3：行程中
    //从预约界面进来的值
    private CarVosBean bunldBean;  //车辆信息
    private ParksResult.DataBean fromPark;  //停车场信息
    //预约信息bean
    private GetReservation reservationBean;  //预约信息
    //行程中bean
    private UnFinishOrderResult orderBean;  //订单信息
    private ParksResult.DataBean toPark;

    private int state = 0; //0：预约  1：行程中
    private String reservationID;

    //订单相关信息
    private String carId;
    private String oderId;
    private String orderNum;

    //倒计时相关
    private int minute = 15;
    private int second = 0;
    private long startTime = 0;
    private CountDownTimer countDownTimer;

    //TODO 为啥要全局变量
    private ArrayList<ParksResult.DataBean> dataBeans = new ArrayList<>(); //所有停车场信息

    private void startTimerCount() {
        if (null != countDownTimer) {
            countDownTimer.cancel();
        }
        long time = System.currentTimeMillis() - startTime;
        if (time > 1000 * 60 * 15) {
            ToastUtils.showShort("预约结束");
            startActivity(new Intent(ReserveActivity.this, MainActivity.class));
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
                    startActivity(new Intent(ReserveActivity.this, MainActivity.class));
                }
            }
        };
        countDownTimer.start();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_reserve;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenManager.getScreenManager().pushActivity(this);
        MapUtils.statusBarColor(this);
        initViewstMap(savedInstanceState);
        initViews();
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(this, "您必须授予我们定位权限才可以正常使用", 101, Manifest.permission.ACCESS_FINE_LOCATION);
        }
        tv_time.setText("00 : " + minute + " : " + second);
        fetchParks();
        if (state == 0) { //预约中显示停车场范围及预约车辆
            parkDetail();
        }
    }

    private void initViews() {
        final Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
            if (type.equals("1")) {// 预约
                bunldBean = (CarVosBean) intent.getSerializableExtra("bunld");
                fromPark = (ParksResult.DataBean) intent.getSerializableExtra("bunldPark");
                if (fromPark.getParkID() == 0) {
                    fromPark.setParkID(fromPark.getId());
                }
                reservationID = intent.getStringExtra("id");
                if (bunldBean != null && fromPark != null) {
                    tvCarnum.setText(bunldBean.getPlateNumber());
                    tvColor.setText(bunldBean.getCarColor());
                    tvgonglishu.setText("" + bunldBean.getMaxDistance());
                    tv_address.setText(fromPark.getName());
                    startTime = System.currentTimeMillis();
                    Glide.with(ReserveActivity.this).load(CarTypeImageUtils.getCarImageByBrand(bunldBean.getBrand(), bunldBean.getCarColor())).into(mIvCar);
                    startTimerCount();
                }
            } else if (type.equals("2")) {// 重新进入APP 进入预约界面
                reservationBean = (GetReservation) intent.getSerializableExtra("bunld");
                reservationID = intent.getStringExtra("id");
                if (reservationBean != null) {
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
                    Glide.with(ReserveActivity.this).load(CarTypeImageUtils.getCarImageByBrand(reservationBean.getData().getBrand(), reservationBean.getData().getCarColor())).into(mIvCar);
                    startTimerCount();
                }
            } else if (type.equals("3")) {// 行程中
                orderBean = (UnFinishOrderResult) intent.getSerializableExtra("bunld");
                if (orderBean != null) {
                    state = 1;
                    fromPark = orderBean.getData().getFromPark();
                    if (fromPark.getId() == 0) {
                        fromPark.setId(fromPark.getParkID());
                    }
                    ivBack.setVisibility(View.INVISIBLE);
                    operateBtnLayout.setVisibility(View.VISIBLE);
                    timeDownLayout.setVisibility(View.GONE);
                    tvTitle.setText("行程中");
                    tvCanel.setVisibility(View.GONE);
                    btnYuding.setText("结束行程");
                    carId = String.valueOf(orderBean.getData().getCarId());
                    oderId = String.valueOf(orderBean.getData().getId());
                    orderNum = String.valueOf(orderBean.getData().getNumber());
                    if (orderBean.getData().getToPark() != null) {
                        toPark = orderBean.getData().getToPark();
                        if (toPark.getId() == 0) {
                            toPark.setId(toPark.getParkID());
                        }
                        if (toPark.getLongitude() == 0) {
                            toPark.setLongitude(toPark.getLongtitude());
                        }
                        tv_address.setText(toPark.getName().isEmpty() ? "" : toPark.getName());
                        tv_address_type.setText("还车点");
                    }
                    if (orderBean.getData().getCar() != null) {
                        CarInfoEntity car = orderBean.getData().getCar();
                        tvCarnum.setText(car.getPlateNumber());
                        tvColor.setText(car.getCarColor());
                        tvgonglishu.setText("" + car.getMaxDistance());
                        Glide.with(ReserveActivity.this).load(CarTypeImageUtils.getCarImageByBrand(car.getBrand(), car.getCarColor())).into(mIvCar);
                    }
                }
            }

            drawRoute();
        }
    }

    @OnClick({R.id.iv_kefu, R.id.btn_yuding, R.id.iv_back, R.id.tv_canel, R.id.rl_kaisuo, R.id.rl_suoding, R.id.rl_xunche,
            R.id.tv_address, R.id.iv_na})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_kefu:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + getResources().getString(R.string.about_phone));
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.btn_yuding:
                if (state == 0) {// 预定界面
                    Intent intent1 = new Intent();
                    intent1.setClass(this, PictureBeforeActivity.class);
                    intent1.putExtra("destinationParkId", String.valueOf(fromPark.getId()));
                    startActivityForResult(intent1, 102);
                } else {// 行程中
                    if (toPark == null) {
                        ToastUtils.showShort("请先设置还车点");
                        return;
                    }
                    checkCar();
                }
                break;
            case R.id.iv_back:
                if (state == 1) { // 行程中
                    ToastUtils.showShort("您还有未完成的订单");
                } else {
                    cancelReservationDialog();
                }
                break;
            case R.id.tv_canel:
                cancelReservationDialog();
                break;
            case R.id.rl_kaisuo:
                opencloseDoor("0");
                break;
            case R.id.rl_suoding:
                opencloseDoor("1");
                break;
            case R.id.rl_xunche:
                opencloseDoor("2");
                break;
            case R.id.tv_address://更换地址提示
                if (state == 1) {//行程中才能点击
                    Intent intent1 = new Intent(ReserveActivity.this, ChooseParkActivity.class);
                    intent1.putExtra("fromPark", fromPark);
                    startActivityForResult(intent1, 101);
                }
                break;
            case R.id.iv_na:
                if (!new File("/data/data/com.baidu.BaiduMap").exists()
                        && !new File("/data/data/com.autonavi.minimap").exists()) {
                    ToastUtils.showShort("尚未安装高德或百度地图");
                    return;
                }
                if (state == 1) {
                    if (toPark != null) {
                        NavigationDialog navigationDialog = new NavigationDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("lng", String.valueOf(toPark.getLongitude()));
                        bundle.putString("lat", String.valueOf(toPark.getLatitude()));
                        navigationDialog.setArguments(bundle);
                        navigationDialog.show(getSupportFragmentManager(), "navigation");
                    } else {
                        ToastUtils.showShort("尚未选取还车点");
                    }
                } else {
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
                }
                break;
        }
    }

    //初始化地图
    private void initViewstMap(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        if (state == 0) {
//            cancelReservationDialog();
//        } else {
//            ToastUtils.showShort("您还有未完成的订单");
//        }
    }

    private void cancelReservationDialog() {
        new AlertDialog.Builder(ReserveActivity.this)
                .setTitle("取消预定")
                .setMessage("确定要取消预定么？")
                .setCancelable(false)
                .setNegativeButton("先不取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("取消预定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelReservation();
                    }
                })
                .create().show();
    }

    private void cancelReservation() {
        Map<String, String> map = new HashMap<>();
        map.put("reservationId", reservationID.trim());

        String token = SessionManager.getInstance().getAuthorization();
        showProgressDialog();
        UdriveRestClient.getClentInstance().cancelReservation(token, map)
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
                                startActivity(new Intent(ReserveActivity.this, MainActivity.class));
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
                                    if (center.isEmpty()) {
                                        return;
                                    }
                                    LatLng latLng = new LatLng(Double.parseDouble(center.split(",")[1]), Double.parseDouble(center.split(",")[0]));
                                    circle = mAmap.addCircle(new CircleOptions()
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
                                    polygon = mAmap.addPolygon(new PolygonOptions()
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
                            carMarker = mAmap.addMarker(markerOptions);
                            carMarker.setClickable(false);
                            carMarker.setRotateAngle(bunldBean.getDirection());
                        }
                        drawRoute();
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

    /**
     * 开始用车
     *
     * @param status 0.不需要不计免赔,1.需要不计免赔
     */
    private void createOder(String status) {
        Map<String, String> map = new HashMap<>();
        map.put("destinationParkId", String.valueOf(fromPark.getId()));//TODO 结束位置暂时设置为起始停车场
        map.put("deductibleStatus", status);
        String token = SessionManager.getInstance().getAuthorization();
        showProgressDialog();
        UdriveRestClient.getClentInstance().createOder(token, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CreateOderBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CreateOderBean bean) {
                        dissmisProgressDialog();
                        if (bean != null) {
                            if (bean.getCode() == 1) {
                                state = 1;
                                carId = String.valueOf(bean.getData().getCarId());
                                oderId = String.valueOf(bean.getData().getId());
                                orderNum = String.valueOf(bean.getData().getNumber());
                                //1.改变UI
                                timeDownLayout.setVisibility(View.GONE);
                                operateBtnLayout.setVisibility(View.VISIBLE);
                                tvTitle.setText("行程中");
                                tvCanel.setVisibility(View.GONE);
                                btnYuding.setText("结束行程");
                                tv_address.setText("");
                                tv_address_type.setText("还车点");
                                ivBack.setVisibility(View.INVISIBLE);
                                for (Map.Entry<ParkKey, Marker> entry : markerMap.entrySet()) {
                                    entry.getValue().setVisible(true);
                                }
                                //2.移除起始停车场范围
                                if (circle != null) {
                                    circle.remove();
                                    circle = null;
                                }
                                if (polygon != null) {
                                    polygon.remove();
                                    polygon = null;
                                }
                                if (carMarker != null) {
                                    carMarker.remove();
                                    carMarker = null;
                                }
                                //3.画到目的地的导航线路
                                drawRoute();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    private void opencloseDoor(String status) {
        Map<String, String> map = new HashMap<>();
        map.put("orderId", oderId);
        map.put("carId", carId);
        String token = SessionManager.getInstance().getAuthorization();
        LogUtils.e(oderId + "--" + carId);

        if (status.equals("0")) {
            showProgressDialog();
            UdriveRestClient.getClentInstance().openCar(token, map)
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
                                    ToastUtils.showShort("开锁成功");
                                } else {
                                    ToastUtils.showShort(bean.getMessage());
                                }
                            } else {
                                ToastUtils.showShort("开锁失败");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            dissmisProgressDialog();
                            ToastUtils.showShort("开锁失败");
                        }

                        @Override
                        public void onComplete() {
                            dissmisProgressDialog();
                        }
                    });
        } else if (status.equals("1")) {
            showProgressDialog();
            UdriveRestClient.getClentInstance().lockCar(token, map)
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
                                    ToastUtils.showShort("锁车成功");
                                } else {
                                    ToastUtils.showShort(bean.getMessage());
                                }
                            } else {
                                ToastUtils.showShort("锁车失败");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            dissmisProgressDialog();
                            ToastUtils.showShort("锁车失败");
                        }

                        @Override
                        public void onComplete() {
                            dissmisProgressDialog();
                        }
                    });
        } else {
            showProgressDialog();
            UdriveRestClient.getClentInstance().searchCarBySound(token, map)
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

    }

    private void finishOder() {
        showProgressDialog();
        String token = SessionManager.getInstance().getAuthorization();
        UdriveRestClient.getClentInstance().completeTripOrder(token, orderNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderDetailResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(OrderDetailResult bean) {
                        dissmisProgressDialog();
                        if (bean != null) {
                            if (bean.code == 1) {
                                ToastUtils.showShort("还车成功");
                                Intent intent1 = new Intent(ReserveActivity.this, ActConfirmOrder.class);
                                intent1.putExtra(PaymentActivity.ORDER_NUMBER, orderNum);
                                startActivity(intent1);
                                finish();
                            } else if (bean.code == 1002) {
                                ToastUtils.showShort(bean.message);
                            } else {
                                ToastUtils.showShort(bean.message);
                            }
                        } else {
                            ToastUtils.showShort("还车失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dissmisProgressDialog();
                        e.printStackTrace();
                        ToastUtils.showShort("还车失败了");
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    private void checkCar() {
        if (TextUtils.isEmpty(carId)) {
            ToastUtils.showShort("未获取到车辆ID");
            return;
        }
        String token = SessionManager.getInstance().getAuthorization();
        UdriveRestClient.getClentInstance().checkCar(token, carId, toPark.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CheckCarResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CheckCarResult checkCarResult) {
                        if (checkCarResult == null || checkCarResult.getCode() == null) {
                            ToastUtils.showShort("请求返回错误");
                            return;
                        }
                        if (checkCarResult.getCode() != 1) {
                            ToastUtils.showShort(checkCarResult.getMessage());
                            return;
                        }
                        if (checkCarResult.getData() != null) {
                            if (checkCarResult.getData().isExist()) {
                                Intent intent2 = new Intent();
                                intent2.setClass(ReserveActivity.this, PictureAfterActivity.class);
                                intent2.putExtra("orderNum", orderNum);
                                startActivity(intent2);
                            } else {
                                ToastUtils.showShort("当前车辆不在停车场");
                            }
                        } else {
                            ToastUtils.showShort("data null");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.showShort("请求错误");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 获取停车场信息
     */
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
                        List<ParksResult.DataBean> dataBeans = result.getData();
                        for (int i = 0; i < dataBeans.size(); i++) {
                            ParksResult.DataBean dataBean = dataBeans.get(i);
                            ParkKey parkKey = new ParkKey(dataBean.getId(), dataBean.getLongitude(), dataBean.getLatitude());
                            if (markerMap.containsKey(parkKey)) {
                                ParksResult.DataBean temp = (ParksResult.DataBean) markerMap.get(parkKey).getObject();
                                if (temp.getValidCarCount() == dataBean.getValidCarCount()) {
                                    continue;
                                }
                            }
                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(dataBean.getLatitude(), dataBean.getLongitude()));
                            int bitmapId = dataBean.getCooperate() > 0 ? R.mipmap.ic_cheweishu_monthly : R.mipmap.ic_cheweishu_llinshi;
                            StringBuilder sb = new StringBuilder();
                            boolean showMaker = false;
                            if (fromPark != null && dataBean.getId() == fromPark.getParkID()) {
                                if (toPark != null && fromPark.getParkID() == toPark.getParkID()) {
                                    sb.append("终");
                                } else {
                                    sb.append("起");
                                    showMaker = true;
                                }
                            } else if (toPark != null && dataBean.getId() == toPark.getParkID()) {
                                sb.append("终");
                            } else {
                                sb.append("P");
                            }
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithShortCut(ReserveActivity.this, bitmapId, sb.toString(), String.valueOf(dataBean.getParkCountBalance()))));

                            Marker marker = mAmap.addMarker(markerOptions);
                            marker.setObject(dataBean);
                            if (state == 0) {
                                marker.setVisible(showMaker);
                            }
                            markerMap.put(parkKey, marker);
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

    private void updateDestinationPark(ParksResult.DataBean dataBean) {
        if (dataBean == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("orderNum", orderNum);
        map.put("destinationParkId", dataBean.getId() + "");
        showProgressDialog();
        String token = SessionManager.getInstance().getAuthorization();
        UdriveRestClient.getClentInstance().updateDestinationPark(token, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetParkObj>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(RetParkObj retParkObj) {
                        dissmisProgressDialog();
                        if (retParkObj.getCode() == 1 && retParkObj.getDate() != null) {
                            //1.取消之前的终
                            if (toPark != null) {
                                if (fromPark.getId() != toPark.getId()) { //起点终点不是同一个地方则恢复终点为P
                                    ParkKey parkKey = new ParkKey(toPark.getId(), toPark.getLongitude(), toPark.getLatitude());
                                    if (markerMap.containsKey(parkKey)) {
                                        markerMap.get(parkKey).remove();//清除
                                        markerMap.remove(parkKey);

                                        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(toPark.getLatitude(), toPark.getLongitude()));
                                        int bitmapId = toPark.getCooperate() > 0 ? R.mipmap.ic_cheweishu_monthly : R.mipmap.ic_cheweishu_llinshi;
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithShortCut(
                                                ReserveActivity.this, bitmapId, "P", String.valueOf(toPark.getValidCarCount()))));
                                        Marker marker = mAmap.addMarker(markerOptions);
                                        marker.setObject(toPark);
                                        markerMap.put(parkKey, marker);
                                    }
                                } else {
                                    if (toPark.getId() != retParkObj.getDate().getId()) { //相同则判断是否新点同终点一样
                                        ParkKey parkKey = new ParkKey(toPark.getId(), toPark.getLongitude(), toPark.getLatitude());
                                        if (markerMap.containsKey(parkKey)) {
                                            markerMap.get(parkKey).remove();//清除
                                            markerMap.remove(parkKey);

                                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(toPark.getLatitude(), toPark.getLongitude()));
                                            int bitmapId = toPark.getCooperate() > 0 ? R.mipmap.ic_cheweishu_monthly : R.mipmap.ic_cheweishu_llinshi;
                                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithShortCut(
                                                    ReserveActivity.this, bitmapId, "起", String.valueOf(toPark.getValidCarCount()))));
                                            Marker marker = mAmap.addMarker(markerOptions);
                                            marker.setObject(toPark);
                                            markerMap.put(parkKey, marker);
                                        }
                                    }
                                }
                            }
                            toPark = retParkObj.getDate();
                            if (toPark.getLongitude() == 0) {
                                toPark.setLongitude(toPark.getLongtitude());
                            }
                            //2.更新界面地址
                            tv_address.setText(toPark.getName());
                            //3.更新图标
                            ParkKey parkKey1 = new ParkKey(toPark.getId(), toPark.getLongitude(), toPark.getLatitude());
                            if (markerMap.containsKey(parkKey1)) {
                                markerMap.get(parkKey1).remove();//清除
                                markerMap.remove(parkKey1);

                                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(toPark.getLatitude(), toPark.getLongitude()));
                                int bitmapId = toPark.getCooperate() > 0 ? R.mipmap.ic_cheweishu_monthly : R.mipmap.ic_cheweishu_llinshi;
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithShortCut(
                                        ReserveActivity.this, bitmapId, "终", String.valueOf(toPark.getValidCarCount()))));
                                Marker marker = mAmap.addMarker(markerOptions);
                                marker.setObject(toPark);
                                markerMap.put(parkKey1, marker);
                            }
                            //4.更新路线
                            drawRoute();
                            ToastUtils.showShort(retParkObj.getMessage());
                        } else {
                            ToastUtils.showShort(retParkObj.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        ToastUtils.showShort("更新换车点失败了");
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
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
                mAmap.moveCamera(CameraUpdateFactory.zoomTo(Constants2.LocationZoom));
                //将地图移动到定位点
                mAmap.moveCamera(CameraUpdateFactory.changeLatLng(mobileLocation));
                isFirstLoc = false;
                drawRoute();
            } else {
                mLocationClient.stopLocation();
                mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(mobileLocation, Constants2.LocationZoom));
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 101:
                ParksResult.DataBean toPark = (ParksResult.DataBean) data.getSerializableExtra("pickPark");
                if (state == 1) {
                    updateDestinationPark(toPark);
                }
                break;
            case 102:
                CreateOderBean createOderBean = (CreateOderBean) data.getSerializableExtra("createOderBean");
                if (createOderBean != null) {
                    if (createOderBean.getCode() == 1) {
                        state = 1;
                        carId = String.valueOf(createOderBean.getData().getCarId());
                        oderId = String.valueOf(createOderBean.getData().getId());
                        orderNum = String.valueOf(createOderBean.getData().getNumber());
                        //1.改变UI
                        timeDownLayout.setVisibility(View.GONE);
                        operateBtnLayout.setVisibility(View.VISIBLE);
                        tvTitle.setText("行程中");
                        tvCanel.setVisibility(View.GONE);
                        btnYuding.setText("结束行程");
                        tv_address.setText("");
                        tv_address_type.setText("还车点");
                        ivBack.setVisibility(View.INVISIBLE);
                        for (Map.Entry<ParkKey, Marker> entry : markerMap.entrySet()) {
                            entry.getValue().setVisible(true);
                        }
                        //2.移除起始停车场范围
                        if (circle != null) {
                            circle.remove();
                            circle = null;
                        }
                        if (polygon != null) {
                            polygon.remove();
                            polygon = null;
                        }
                        if (carMarker != null) {
                            carMarker.remove();
                            carMarker = null;
                        }
                        //3.画到目的地的导航线路
                        drawRoute();
                    }
                }
                break;
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int errorCode) {
        if (errorCode == 1000) {
            if (drivingRouteOverlay != null) {
                drivingRouteOverlay.removeFromMap();
                drivingRouteOverlay = null;
            }
            drivingRouteOverlay = new DrivingRouteOverlay(this, mAmap, driveRouteResult.getPaths().get(0),
                    driveRouteResult.getStartPos(), driveRouteResult.getTargetPos(), null);
            drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
            drivingRouteOverlay.addToMap();
            drivingRouteOverlay.zoomToSpan();
        }
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
            walkRouteOverlay.zoomToSpan();
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    private void drawRoute() {
        if (drivingRouteOverlay != null) {
            drivingRouteOverlay.removeFromMap();
            drivingRouteOverlay = null;
        }
        if (walkRouteOverlay != null) {
            walkRouteOverlay.removeFromMap();
            walkRouteOverlay = null;
        }
        if (mobileLocation == null) {
            return;
        }
        if (state == 1) {
            if (toPark != null) {
                mRouteSearch = new RouteSearch(ReserveActivity.this);
                mRouteSearch.setRouteSearchListener(ReserveActivity.this);
                final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(AMapUtil.convertToLatLonPoint(mobileLocation), new LatLonPoint(toPark.getLatitude(), toPark.getLongitude()));
                RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");
                mRouteSearch.calculateDriveRouteAsyn(query);
            }
        } else {
            if (fromPark != null) {
                mRouteSearch = new RouteSearch(ReserveActivity.this);
                mRouteSearch.setRouteSearchListener(ReserveActivity.this);
                final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(AMapUtil.convertToLatLonPoint(mobileLocation), new LatLonPoint(fromPark.getLatitude(), fromPark.getLongitude()));
                RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
                mRouteSearch.calculateWalkRouteAsyn(query);
            }
        }
    }
}