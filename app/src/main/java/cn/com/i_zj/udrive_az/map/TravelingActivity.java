package cn.com.i_zj.udrive_az.map;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
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
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.MainActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.WebSocketEvent;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.map.adapter.ChooseParkActivity;
import cn.com.i_zj.udrive_az.map.adapter.PictureAfterActivity;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.CarBean;
import cn.com.i_zj.udrive_az.model.CheckCarResult;
import cn.com.i_zj.udrive_az.model.DoorBean;
import cn.com.i_zj.udrive_az.model.FromParkBean;
import cn.com.i_zj.udrive_az.model.ParkKey;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.model.ToParkBean;
import cn.com.i_zj.udrive_az.model.UnFinishOrderResult;
import cn.com.i_zj.udrive_az.model.WebSocketPrice;
import cn.com.i_zj.udrive_az.model.ret.RetParkObj;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.overlay.DrivingRouteOverlay;
import cn.com.i_zj.udrive_az.utils.AMapUtil;
import cn.com.i_zj.udrive_az.utils.CarTypeImageUtils;
import cn.com.i_zj.udrive_az.utils.Constants2;
import cn.com.i_zj.udrive_az.utils.ScreenManager;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.dialog.AmountDialog;
import cn.com.i_zj.udrive_az.utils.dialog.NavigationDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 行程中界面
 */
public class TravelingActivity extends DBSBaseActivity implements AMapLocationListener, RouteSearch.OnRouteSearchListener {

    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.header_image)
    ImageView header_image;

    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.tv_carnum)
    TextView tvCarnum;
    @BindView(R.id.tv_color)
    TextView tvColor;
    @BindView(R.id.tv_gonglishu)
    TextView tvgonglishu;
    @BindView(R.id.tv_amount)
    TextView tv_amount;
    @BindView(R.id.iv_car)
    ImageView mIvCar;
    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.rl_dengdai)
    View mapBottom;

    private AMap mAmap;
    private AMapLocationClient mLocationClient;
    private boolean isFirstLoc = true;
    private RouteSearch mRouteSearch;
    private Map<ParkKey, Marker> markerMap = new HashMap();
    private LatLng mobileLocation;
    private DrivingRouteOverlay drivingRouteOverlay;
    private int paddingSize = 150;
    private List<LatLng> allLatLngs = new ArrayList<>();

    private FromParkBean fromPark;
    private ToParkBean toPark;
    private UnFinishOrderResult unFinishOrderBean;  //订单信息

    //订单相关信息
    private String carId;
    private String oderId;
    private String orderNum;

    //TODO 为啥要全局变量
    private ArrayList<ParksResult.DataBean> dataBeans = new ArrayList<>(); //所有停车场信息

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_traveling;
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
        fetchParks();

        //发送创建web socket event
        AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
        EventBus.getDefault().post(new WebSocketEvent(accountInfo.data.userId));
    }

    private void initViews() {
        header_title.setText("行程中");
        header_image.setImageResource(R.mipmap.ic_service);
        initMap();
        getOrder();
    }

    private void getOrder() {
        showProgressDialog();
        UdriveRestClient.getClentInstance().getUnfinishedOrder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UnFinishOrderResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UnFinishOrderResult result) {
                        dissmisProgressDialog();
                        if (result == null || result.getCode() != 1) {
                            ToastUtils.showShort("行程信息获取失败");
                            startActivity(MainActivity.class);
                            finish();
                            return;
                        }
                        if (result.getData() != null && result.getData().getId() > 0) {
                            unFinishOrderBean = result;
                            if (unFinishOrderBean == null) {
                                return;
                            }
                            if (unFinishOrderBean.getData().getOrder() != null
                                    && unFinishOrderBean.getData().getOrder().getPowerFlag() == 1) { //直接显示断电信息
//                                startActivity(OffPowerDialogActivity.class);
                            }

                            carId = String.valueOf(unFinishOrderBean.getData().getCarId());
                            oderId = String.valueOf(unFinishOrderBean.getData().getId());
                            orderNum = String.valueOf(unFinishOrderBean.getData().getNumber());

                            fromPark = unFinishOrderBean.getData().getFromPark();

                            if (unFinishOrderBean.getData().getToPark() != null) {
                                toPark = unFinishOrderBean.getData().getToPark();
                                allLatLngs.add(new LatLng(toPark.getLatitude(), toPark.getLongtitude()));
                                tv_address.setText(toPark.getName().isEmpty() ? "" : toPark.getName());
                            }
                            if (unFinishOrderBean.getData().getCar() != null) {
                                CarBean car = unFinishOrderBean.getData().getCar();
                                tvCarnum.setText(car.getPlateNumber());
                                tvColor.setText(car.getCarColor());
                                tvgonglishu.setText("" + car.getMaxDistance());
                                Glide.with(TravelingActivity.this).load(CarTypeImageUtils.getCarImageByBrand(car.getBrand(), car.getCarColor())).into(mIvCar);
                            }
                            tv_amount.setText(String.format(Locale.getDefault(), "%.2f", unFinishOrderBean.getData().getOrder().getTotalAmount() / 100f));
                            drawMap();
                        } else {
                            startActivity(MainActivity.class);
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        startActivity(MainActivity.class);
                        finish();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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

    @OnClick({R.id.header_left, R.id.header_right, R.id.btn_yuding, R.id.rl_kaisuo, R.id.rl_suoding, R.id.rl_xunche,
            R.id.tv_address, R.id.iv_na, R.id.amount_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left:
                startActivity(MainActivity.class);
                finish();
                break;
            case R.id.header_right:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + getResources().getString(R.string.about_phone));
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.btn_yuding:
                if (toPark == null) {
                    ToastUtils.showShort("请先设置还车点");
                    return;
                }
                checkCar();
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
                Intent intent1 = new Intent(TravelingActivity.this, ChooseParkActivity.class);
                intent1.putExtra("fromPark", fromPark);
                intent1.putExtra("oderId", oderId);
                startActivityForResult(intent1, 101);
                break;
            case R.id.iv_na:
                if (!new File("/data/data/com.baidu.BaiduMap").exists()
                        && !new File("/data/data/com.autonavi.minimap").exists()) {
                    ToastUtils.showShort("尚未安装高德或百度地图");
                    return;
                }
                if (toPark != null) {
                    NavigationDialog navigationDialog = new NavigationDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("lng", String.valueOf(toPark.getLongtitude()));
                    bundle.putString("lat", String.valueOf(toPark.getLatitude()));
                    navigationDialog.setArguments(bundle);
                    navigationDialog.show(getSupportFragmentManager(), "navigation");
                } else {
                    ToastUtils.showShort("尚未选取还车点");
                }
                break;
            case R.id.amount_detail:
                WebSocketPrice price = new WebSocketPrice();
                price.setTotalAmount(unFinishOrderBean.getData().getOrder().getTotalAmount());
                price.setMileageAmount(unFinishOrderBean.getData().getOrder().getMileageAmount());
                price.setTimeAmount(unFinishOrderBean.getData().getOrder().getTimeAmount());
                price.setDeductible(unFinishOrderBean.getData().getOrder().getDeductible());
                AmountDialog dialog = new AmountDialog(this);
                dialog.setAmount(price);
                dialog.show();
                break;
        }
    }

    private void opencloseDoor(String status) {
        Map<String, String> map = new HashMap<>();
        map.put("orderId", oderId);
        map.put("carId", carId);
        LogUtils.e(oderId + "--" + carId);

        if (status.equals("0")) {
            showProgressDialog();
            UdriveRestClient.getClentInstance().openCar(map)
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
            UdriveRestClient.getClentInstance().lockCar(map)
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
            UdriveRestClient.getClentInstance().searchCarBySound(map)
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

    private void checkCar() {
        if (TextUtils.isEmpty(carId)) {
            ToastUtils.showShort("未获取到车辆ID");
            return;
        }
        showProgressDialog();
        UdriveRestClient.getClentInstance().checkCar(carId, toPark.getParkID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CheckCarResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CheckCarResult checkCarResult) {
                        dissmisProgressDialog();
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
                                intent2.setClass(TravelingActivity.this, PictureAfterActivity.class);
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
                        dissmisProgressDialog();
                        ToastUtils.showShort("请求错误");
                    }

                    @Override
                    public void onComplete() {

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
                        List<ParksResult.DataBean> dataBeans = result.getData();
                        for (int i = 0; i < dataBeans.size(); i++) {
                            ParksResult.DataBean dataBean = dataBeans.get(i);
                            ParkKey parkKey = new ParkKey(dataBean.getId(), dataBean.getLongitude(), dataBean.getLatitude());
                            if (markerMap.containsKey(parkKey)) {
                                ParksResult.DataBean temp = (ParksResult.DataBean) markerMap.get(parkKey).getObject();
                                if (temp.getValidCarCount() == dataBean.getValidCarCount()) {
                                    continue;
                                } else {
                                    markerMap.get(parkKey).remove();
                                    markerMap.remove(parkKey);
                                }
                            }
                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(dataBean.getLatitude(), dataBean.getLongitude()));
                            int bitmapId = dataBean.getCooperate() > 0 ? R.mipmap.ic_cheweishu_monthly : R.mipmap.ic_cheweishu_llinshi;
                            StringBuilder sb = new StringBuilder();
                            if (fromPark != null && dataBean.getId() == fromPark.getParkID()) {
                                if (toPark != null && fromPark.getParkID() == toPark.getParkID()) {
                                    sb.append("终");
                                } else {
                                    sb.append("起");
                                }
                            } else if (toPark != null && dataBean.getId() == toPark.getParkID()) {
                                sb.append("终");
                            } else {
                                sb.append("P");
                            }
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithShortCut(TravelingActivity.this, bitmapId, sb.toString(), String.valueOf(dataBean.getParkCountBalance()))));

                            Marker marker = mAmap.addMarker(markerOptions);
                            marker.setObject(dataBean);
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
        UdriveRestClient.getClentInstance().updateDestinationPark(map)
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
                                if (fromPark.getParkID() != toPark.getParkID()) { //起点终点不是同一个地方则恢复终点为P
                                    ParkKey parkKey = new ParkKey(toPark.getParkID(), toPark.getLongtitude(), toPark.getLatitude());
                                    if (markerMap.containsKey(parkKey)) {
                                        markerMap.get(parkKey).remove();//清除
                                        markerMap.remove(parkKey);

                                        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(toPark.getLatitude(), toPark.getLongtitude()));
                                        int bitmapId = R.mipmap.ic_cheweishu_monthly;//TODO 直接使用合作停车场的图标
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithShortCut(
                                                TravelingActivity.this, bitmapId, "P", String.valueOf("0"))));
                                        Marker marker = mAmap.addMarker(markerOptions);
                                        marker.setObject(toPark);
                                        markerMap.put(parkKey, marker);
                                    }
                                } else {
                                    if (toPark.getParkID() != retParkObj.getDate().getId()) { //相同则判断是否新点同终点一样
                                        ParkKey parkKey = new ParkKey(toPark.getParkID(), toPark.getLongtitude(), toPark.getLatitude());
                                        if (markerMap.containsKey(parkKey)) {
                                            markerMap.get(parkKey).remove();//清除
                                            markerMap.remove(parkKey);

                                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(toPark.getLatitude(), toPark.getLongtitude()));
                                            int bitmapId = R.mipmap.ic_cheweishu_monthly;
                                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithShortCut(
                                                    TravelingActivity.this, bitmapId, "起", String.valueOf("0"))));
                                            Marker marker = mAmap.addMarker(markerOptions);
                                            marker.setObject(toPark);
                                            markerMap.put(parkKey, marker);
                                        }
                                    }
                                }
                            }
                            toPark = new ToParkBean();
                            toPark.setLatitude(retParkObj.getDate().getLatitude());
                            toPark.setLongtitude(retParkObj.getDate().getLongitude() == 0
                                    ? retParkObj.getDate().getLongtitude()
                                    : retParkObj.getDate().getLongitude());
                            toPark.setParkID(retParkObj.getDate().getId());
                            toPark.setName(retParkObj.getDate().getName());
                            allLatLngs.clear();
                            allLatLngs.add(new LatLng(toPark.getLatitude(), toPark.getLongtitude()));
                            //2.更新界面地址
                            tv_address.setText(toPark.getName());
                            //3.更新图标
                            ParkKey parkKey1 = new ParkKey(toPark.getParkID(), toPark.getLongtitude(), toPark.getLatitude());
                            if (markerMap.containsKey(parkKey1)) {
                                markerMap.get(parkKey1).remove();//清除
                                markerMap.remove(parkKey1);

                                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(toPark.getLatitude(), toPark.getLongtitude()));
                                int bitmapId = R.mipmap.ic_cheweishu_monthly;
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithShortCut(
                                        TravelingActivity.this, bitmapId, "终", "0")));
                                Marker marker = mAmap.addMarker(markerOptions);
                                marker.setObject(toPark);
                                markerMap.put(parkKey1, marker);
                            }
                            //4.更新路线
                            drawMap();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebSocketPrice event) {
        if (event != null) {
            if (unFinishOrderBean == null || unFinishOrderBean.getData() == null) {
                return;
            }
            if (unFinishOrderBean.getData().getId() == event.getOrderId()) {
                unFinishOrderBean.getData().getOrder().setMileageAmount(event.getMileageAmount());
                unFinishOrderBean.getData().getOrder().setTimeAmount(event.getTimeAmount());
                unFinishOrderBean.getData().getOrder().setTotalAmount(event.getTotalAmount());
                unFinishOrderBean.getData().getOrder().setDeductible(event.getDeductible());
                tv_amount.setText(String.format(Locale.getDefault(), "%.2f", event.getTotalAmount() / 100f));
            }
        }
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
                mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(mobileLocation, Constants2.LocationZoom));
                isFirstLoc = false;
                drawMap();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        ParksResult.DataBean toPark = (ParksResult.DataBean) data.getSerializableExtra("pickPark");
        updateDestinationPark(toPark);
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
            if (drivingRouteOverlay.getPoints() != null) {
                allLatLngs.addAll(drivingRouteOverlay.getPoints());
                updateCamera();
            }
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    private void drawMap() {
        if (drivingRouteOverlay != null) {
            drivingRouteOverlay.removeFromMap();
            drivingRouteOverlay = null;
        }
        if (mobileLocation == null) {
            return;
        }
        //调整视角
        allLatLngs.add(mobileLocation);
        updateCamera();

        if (toPark != null) {
            mRouteSearch = new RouteSearch(TravelingActivity.this);
            mRouteSearch.setRouteSearchListener(TravelingActivity.this);
            final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(AMapUtil.convertToLatLonPoint(mobileLocation), new LatLonPoint(toPark.getLatitude(), toPark.getLongtitude()));
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");
            mRouteSearch.calculateDriveRouteAsyn(query);
        }
    }

    private void updateCamera() {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLng : allLatLngs) {
            boundsBuilder.include(latLng);
        }
        int bottomPadding = ToolsUtils.getWindowHeight(this) - mapBottom.getTop() + paddingSize;
        if (ToolsUtils.checkDeviceHasNavigationBar(this)) {
            bottomPadding += ToolsUtils.getNavigationBarHeight(this);
        }
        mAmap.animateCamera(CameraUpdateFactory.newLatLngBoundsRect(boundsBuilder.build(), paddingSize, paddingSize, paddingSize, bottomPadding));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(MainActivity.class);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
