package cn.com.i_zj.udrive_az.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
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
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseFragment;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.constant.ParkType;
import cn.com.i_zj.udrive_az.event.NetWorkEvent;
import cn.com.i_zj.udrive_az.lz.bean.ParkRemark;
import cn.com.i_zj.udrive_az.lz.ui.accountinfo.certification.ActIdentificationDrivingLicense;
import cn.com.i_zj.udrive_az.lz.ui.accountinfo.certification.ActIdentificationIDCard;
import cn.com.i_zj.udrive_az.lz.ui.order.OrderActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.WaitingActivity;
import cn.com.i_zj.udrive_az.map.fragment.BaseFragmentAdapter;
import cn.com.i_zj.udrive_az.map.fragment.CarsFragment;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.AreaInfo;
import cn.com.i_zj.udrive_az.model.AreaTagsResult;
import cn.com.i_zj.udrive_az.model.ParkDetailResult;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.CarVosBean;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.ParkAreaBean;
import cn.com.i_zj.udrive_az.model.ParkKey;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.model.ReserVationBean;
import cn.com.i_zj.udrive_az.model.UnFinishOrderResult;
import cn.com.i_zj.udrive_az.network.UObserver;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.overlay.WalkRouteOverlay;
import cn.com.i_zj.udrive_az.utils.AMapUtil;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.Constants2;
import cn.com.i_zj.udrive_az.utils.ToastUtil;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.dialog.NavigationDialog;
import cn.com.i_zj.udrive_az.utils.dialog.ParkDetailDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

import static android.widget.Toast.makeText;

/**
 * 地图MapFragment
 */
public class MapFragment extends DBSBaseFragment implements AMapLocationListener
        , RouteSearch.OnRouteSearchListener, EasyPermissions.PermissionCallbacks
        , AMap.OnMarkerClickListener, ViewPager.OnPageChangeListener
        , AMap.OnMapClickListener {

    @BindView(R.id.map)
    MapView mMapView;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.tblayout)
    TabLayout tabLayout;

    @BindView(R.id.ll_info)
    LinearLayout ll_info;
    @BindView(R.id.ll_info1)
    LinearLayout ll_info1;

    @BindView(R.id.rl_car_info)
    RelativeLayout rlCarinfo;

    @BindView(R.id.tv_pname)
    TextView tv_pname;

    @BindView(R.id.tv_paradress)
    TextView tv_adress;
    @BindView(R.id.tv_city)
    TextView tv_centerName;
    @BindView(R.id.tv_dis)
    TextView tv_dis;
    @BindView(R.id.iv_refresh)
    ImageView ivRefresh;

    @BindView(R.id.tv_park_detail)
    TextView tvParkDetail;

    @BindView(R.id.btn_yuding)
    Button btn_yuding;
    @BindView(R.id.btn_yongche)
    Button btn_yongche;

    private AMap mAmap;
    private AMapLocationClient mLocationClient;
    private RouteSearch mRouteSearch;
    private WalkRouteOverlay walkRouteOverlay;
    private boolean isFirstLoc = true;
    private int paddingSize = 200;
    private List<LatLng> allLatLngs = new ArrayList<>();

    private Circle circle;//停车场范围圆
    private Polygon polygon;//停车场范围多边形
    private Disposable disposable;
    private List<Marker> carMarkers = new ArrayList<>();//停车场小车

    private List<Fragment> fragments;
    private List<AreaTagsResult.DataBean> areaBeans = new ArrayList<>();
    private List<ParksResult.DataBean> parkBeans = new ArrayList<>();
    private Marker clickMarker;//选中的marker
    private Map<ParkKey, Marker> markerMap = new HashMap(); //所有停车场
    private List<Marker> areaMarkers = new ArrayList<>();//所有区域

    private CarVosBean bunldBean; //当前选中的车辆
    private ParksResult.DataBean buldParkBean; //选中的停车场
    private ArrayList<CarVosBean> carBeans = new ArrayList<>(); //车辆列表
    private LatLng mobileLocation;
    private String parkid;
    private String carid;

    private TranslateAnimation showAnim;
    private Animation animRefresh;
    private ParkDetailDialog parkDetailDialog;
    private DecimalFormat df = new DecimalFormat("0.0");
    private boolean first = true;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_map;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!EasyPermissions.hasPermissions(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(this, "您必须授予我们定位权限才可以正常使用", 101, Manifest.permission.ACCESS_FINE_LOCATION);
        }

        initViewstMap(savedInstanceState);
        init();
    }

    private void init() {
        bunldBean = new CarVosBean();

        showAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        showAnim.setDuration(500);


        animRefresh = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_home_refresh);
        animRefresh.setInterpolator(new LinearInterpolator());//设置动画匀速运动

        mViewPager.addOnPageChangeListener(this);
    }

    private void initViewstMap(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        mAmap = mMapView.getMap();
        mAmap.setOnMapClickListener(this);
        UiSettings uiSettings = mAmap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        MapUtils.setMapCustomStyleFile(getContext(), mAmap);
        mAmap.moveCamera(CameraUpdateFactory.zoomTo(Constants2.AreaClickZoom));
        mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();
        mAmap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                if (cameraPosition.zoom > Constants2.AreaMarkerZoom) { //显示小图标
                    fetchParks();
                    showArea(false);
                    if (btn_yuding.getVisibility() != View.VISIBLE && btn_yongche.getVisibility() != View.VISIBLE) {
                        btn_yongche.startAnimation(showAnim);
                        btn_yongche.setVisibility(View.VISIBLE);
                    }
                } else {//显示区域
                    if (buldParkBean == null) {
                        showArea(true);
                        btn_yongche.setVisibility(View.GONE);
                        if (areaMarkers.size() == 0) {
                            fetchAreas();
                        }
                    }
                }
            }
        });
        mAmap.setOnMarkerClickListener(this);
    }

    private void showArea(boolean show) {
        if (show) {
            for (Map.Entry<ParkKey, Marker> entry : markerMap.entrySet()) {
                entry.getValue().setVisible(!show);
            }
            for (Marker marker : areaMarkers) {
                marker.setVisible(show);
            }
        } else {
            for (Marker marker : areaMarkers) {
                marker.setVisible(show);
            }
            for (Map.Entry<ParkKey, Marker> entry : markerMap.entrySet()) {
                entry.getValue().setVisible(!show);
            }
            if (buldParkBean != null) {
                ParkKey parkKey = new ParkKey(buldParkBean.getId(), buldParkBean.getLongitude(), buldParkBean.getLatitude());
                if (markerMap.containsKey(parkKey)) {
                    markerMap.get(parkKey).setVisible(false);
                }
            }
        }
    }

    @OnClick({R.id.iv_refresh, R.id.iv_mylocation, R.id.tv_park_detail, R.id.rl1,
            R.id.btn_yuding, R.id.btn_yongche, R.id.park_explain})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_refresh:
                if (animRefresh != null) {
                    ivRefresh.startAnimation(animRefresh);
                }
                buldParkBean = null;
                for (Marker marker : areaMarkers) {
                    marker.remove();
                }
                areaMarkers.clear();
                if (clickMarker != null) {
                    clickMarker.setVisible(true);
                }
                removeAllOtherMarker();
                fetchParks();
                showArea(false);
                if (mLocationClient != null) {
                    mLocationClient.startLocation();
                }
                break;
            case R.id.iv_mylocation:
                buldParkBean = null;
                if (clickMarker != null) {
                    clickMarker.setVisible(true);
                }
                removeAllOtherMarker();
                fetchParks();
                showArea(false);
                if (mLocationClient != null) {
                    mLocationClient.startLocation();
                }
                break;
            case R.id.tv_park_detail:
                getParkRemark();
                break;
            case R.id.rl1:
                if (buldParkBean != null) {
                    NavigationDialog navigationDialog = new NavigationDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("lng", String.valueOf(buldParkBean.getLongitude()));
                    bundle.putString("lat", String.valueOf(buldParkBean.getLatitude()));
                    navigationDialog.setArguments(bundle);
                    navigationDialog.show(getChildFragmentManager(), "navigation");
                }
                break;
            case R.id.btn_yuding:
                if (bunldBean == null || buldParkBean == null) {
                    ToastUtils.showShort("请先选择车辆");
                    return;
                }
                reservationVerify();
                break;
            case R.id.btn_yongche:
                ParksResult.DataBean bestPark = null;
                float bestDistance = 1000000;
                for (ParksResult.DataBean dataBean : parkBeans) {
                    float distance = AMapUtils.calculateLineDistance(mobileLocation,
                            new LatLng(dataBean.getLatitude(), dataBean.getLongitude()));
                    if (dataBean.getValidCarCount() > 0 && distance < bestDistance) {
                        bestPark = dataBean;
                        bestDistance = distance;
                    }
                }
                if (bestPark != null && bestDistance < 3000) {
                    ParkKey parkKey = new ParkKey(bestPark.getId(), bestPark.getLongitude(), bestPark.getLatitude());
                    if (markerMap.containsKey(parkKey)) {
                        onMarkerClick(markerMap.get(parkKey));
                    }
                } else {
                    ToastUtils.showShort("3公里内无可用车辆");
                }
                break;
            case R.id.park_explain:
                startActivity(ParkExplainActivity.class);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetWorkEvent netWorkEvent) {
        if (mAmap.getCameraPosition().zoom > Constants2.AreaMarkerZoom) {
            if (markerMap.isEmpty()) {
                fetchParks();
                showArea(false);
                if (btn_yuding.getVisibility() != View.VISIBLE && btn_yongche.getVisibility() != View.VISIBLE) {
                    btn_yongche.startAnimation(showAnim);
                    btn_yongche.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (buldParkBean == null && areaBeans.size() == 0) {
                showArea(true);
                btn_yongche.setVisibility(View.GONE);
                if (areaMarkers.size() == 0) {
                    fetchAreas();
                }
            }
        }
    }

    private void fetchAreas() {
        if (disposable != null) {
            disposable.isDisposed();
            disposable = null;
        }
        UdriveRestClient.getClentInstance().getAreaTags()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AreaTagsResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(AreaTagsResult result) {
                        if (result == null) {
                            return;
                        }
                        if (result.getCode() != 1) {
                            return;
                        }
                        areaBeans.clear();
                        areaBeans.addAll(result.getData());
                        for (int i = 0; i < areaBeans.size(); i++) {
                            LatLng latLng = new LatLng(areaBeans.get(i).getLatitude(), areaBeans.get(i).getLongitude());
                            if (!AMapUtil.isLatLngValid(latLng)) {
                                continue;
                            }
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithCenterText(getActivity(), R.mipmap.ic_area, areaBeans.get(i).getName(), 13)));

                            Marker marker = mAmap.addMarker(markerOptions);
                            marker.setObject(latLng);
                            areaMarkers.add(marker);
                        }
                        if (animRefresh != null) {
                            ivRefresh.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ivRefresh.clearAnimation();
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (animRefresh != null) {
                            ivRefresh.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ivRefresh.clearAnimation();
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void fetchParks() {
        if (disposable != null) {
            disposable.isDisposed();
            disposable = null;
        }
        UdriveRestClient.getClentInstance().getParks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ParksResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(ParksResult result) {
                        if (result == null) {
                            return;
                        }
                        if (result.getCode() != 1) {
                            return;
                        }
                        parkBeans.clear();
                        parkBeans.addAll(result.getData());
                        for (ParksResult.DataBean dataBean : parkBeans) {
                            ParkKey parkKey = new ParkKey(dataBean.getId(), dataBean.getLongitude(), dataBean.getLatitude());
                            if (markerMap.containsKey(parkKey)) {
                                ParksResult.DataBean temp = (ParksResult.DataBean) markerMap.get(parkKey).getObject();
                                if (temp.getValidCarCount() == dataBean.getValidCarCount()) {
                                    continue;
                                }
                            }
                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(dataBean.getLatitude(), dataBean.getLongitude()));
                            int bitmapId = dataBean.getCooperate() > 0 ? R.mipmap.ic_cheweishu_monthly : R.mipmap.ic_cheweishu_llinshi;
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithCenterText(getActivity(), bitmapId, String.valueOf(dataBean.getValidCarCount()))));

                            Marker marker = mAmap.addMarker(markerOptions);
                            marker.setObject(dataBean);
                            markerMap.put(parkKey, marker);
                        }
                        if (animRefresh != null) {
                            ivRefresh.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ivRefresh.clearAnimation();
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (animRefresh != null) {
                            ivRefresh.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ivRefresh.clearAnimation();
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void reservationVerify() {
        if (SessionManager.getInstance().getAuthorization() == null) {
            LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
            loginDialogFragment.show(getChildFragmentManager(), "login");
            return;
        }
        AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
        if (accountInfo == null) {
            return;
        }
        if (accountInfo.data.idCardState != Constants.ID_AUTHORIZED_SUCCESS) {
            if (accountInfo.data.idCardState == Constants.ID_UNDER_REVIEW) {
                makeText(getActivity(), "实名认证正在审核中", Toast.LENGTH_SHORT).show();
                return;
            }
            showIdCardStateDialog();
            return;
        }
        if (accountInfo.data.driverState != Constants.ID_AUTHORIZED_SUCCESS) {
            if (accountInfo.data.driverState == Constants.ID_UNDER_REVIEW) {
                makeText(getActivity(), "驾照认证正在审核中", Toast.LENGTH_SHORT).show();
                return;
            }
            showDriverStateDialog();
            return;
        }
        if (accountInfo.data.depositState != 2) {
            ToastUtil.show(getActivity(), "请先缴纳押金");
            return;
        }
        getUnPayOrder();
    }

    private void getUnPayOrder() {
        UdriveRestClient.getClentInstance().getUnfinishedOrder(SessionManager.getInstance().getAuthorization())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UnFinishOrderResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UnFinishOrderResult result) {
                        if (result != null && result.getCode() == 1
                                && result.getData() != null && result.getData().getId() > 0
                                && result.getData().getStatus() == 1) {
                            showUnfinishedOrderDialog();
                            return;
                        }
                        if (bunldBean.isTrafficControl()) {
                            showTrafficControlDialog();
                        } else if (buldParkBean != null && buldParkBean.getCooperate() == 0
                                && buldParkBean.getStopInAmount() > 0) { //只有非合作停车场才会有出场费
                            showParkOutAmountDialog(buldParkBean.getStopInAmount() / 100);
                        } else {
                            reservation();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (bunldBean.isTrafficControl()) {
                            showTrafficControlDialog();
                        } else if (buldParkBean != null && buldParkBean.getCooperate() == 0
                                && buldParkBean.getStopInAmount() > 0) { //只有非合作停车场才会有出场费
                            showParkOutAmountDialog(buldParkBean.getStopInAmount() / 100);
                        } else {
                            reservation();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void reservation() {
        Map<String, String> map = new HashMap<>();
        map.put("carId", carid);
        map.put("startParkId", parkid);
        String token = SessionManager.getInstance().getAuthorization();
        showProgressDialog();
        UdriveRestClient.getClentInstance().reservation(token, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReserVationBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ReserVationBean result) {
                        dissmisProgressDialog();
                        if (result != null) {
                            if (result.getCode() == 1) {
                                if (!"".equals(result.getData()) && result.getData() != null) {
                                    if (result.getData().getOrderType() != 0) {
                                        showUnfinshOrder();
                                    } else {
                                        Intent intent = new Intent(getActivity(), WaitingActivity.class);
                                        intent.putExtra("type", "1");
                                        intent.putExtra("bunld", bunldBean);
                                        intent.putExtra("bunldPark", buldParkBean);
                                        intent.putExtra("id", result.getData().getReservationId() + "");
                                        startActivity(intent);
                                        reset();
                                    }
                                }
                            } else {
                                ToastUtils.showShort(result.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        ToastUtils.showShort("预约失败了");
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    private void getParkRemark() {
        UdriveRestClient.getClentInstance().getParkRemark(parkid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UObserver<ParkRemark>() {
                    @Override
                    public void onSuccess(ParkRemark response) {
                        if (parkDetailDialog != null && parkDetailDialog.isShowing()) {
                            return;
                        }
                        response.setName(buldParkBean.getName());// 只能这样去取
                        parkDetailDialog = new ParkDetailDialog(getActivity());
                        parkDetailDialog.showData(response);
                        parkDetailDialog.show();
                    }


                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onException(int code, String message) {
                        showToast(message);
                    }
                });
    }

    private void parkDetail(int parkId) {
        showProgressDialog();
        UdriveRestClient.getClentInstance().getParkDetail(parkId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ParkDetailResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ParkDetailResult parkDetailResult) {
                        dissmisProgressDialog();
                        if (parkDetailResult.getCode() != 1) {
                            return;
                        }
                        //隐藏停车场marker

                        clickMarker.setVisible(false);
                        //画区域
                        ParkAreaBean parkAreaBean = parkDetailResult.getData().getParkArea();
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
                                    allLatLngs.add(latLng);
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
                                    polygon = mAmap.addPolygon(new PolygonOptions()
                                            .addAll(latLngs)
                                            .fillColor(Color.parseColor("#FFCBE2FF"))
                                            .strokeColor(Color.parseColor("#FF0075FF"))
                                            .strokeWidth(1));
                                    break;
                            }
                        }
                        //画小车
                        List<CarVosBean> carVosBeans = parkDetailResult.getData().getCarVos();
                        if (carVosBeans != null && carVosBeans.size() > 0) {
                            carBeans.clear();
                            carBeans.addAll(carVosBeans);
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
                            for (int i = 0; i < carVosBeans.size(); i++) {
                                CarsFragment carsFragment = CarsFragment.newInstance(i, carVosBeans.get(i));
                                fragments.add(carsFragment);
                                imgs.add(R.drawable.view_selector);

                            }

                            bunldBean = carVosBeans.get(0);
                            carid = String.valueOf(bunldBean.getId());
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
                            carsFragment.refresh(carVosBeans.get(0));
                            ll_info.setVisibility(View.VISIBLE);
                            rlCarinfo.setVisibility(View.VISIBLE);
                            btn_yongche.setVisibility(View.GONE);
                            btn_yuding.setVisibility(View.VISIBLE);
                            btn_yuding.setEnabled(true);
                            btn_yuding.setText(Utils.getApp().getResources().getString(R.string.mashangyuding));

                            for (CarVosBean bean : carVosBeans) {
                                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(bean.getLatitude(), bean.getLongitude()));
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pic_bora));
                                Marker carMarker = mAmap.addMarker(markerOptions);
                                carMarker.setClickable(false);
                                carMarker.setRotateAngle(bean.getDirection());
                                carMarkers.add(carMarker);
                            }
                        } else {
                            ll_info.setVisibility(View.VISIBLE);
                            rlCarinfo.setVisibility(View.GONE);
                            btn_yongche.setVisibility(View.GONE);
                            btn_yuding.setVisibility(View.VISIBLE);
                            btn_yuding.setText("暂无车辆");
                            btn_yuding.setEnabled(false);
                        }
                        float distance = AMapUtils.calculateLineDistance(
                                mobileLocation, new LatLng(buldParkBean.getLatitude(), buldParkBean.getLongitude()));
                        if (distance < 3000) {
                            drawRoute();
                        } else {
                            updateCamera();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dissmisProgressDialog();
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
                myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(
                        R.mipmap.ic_location_arrow));
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
                mAmap.setMyLocationStyle(myLocationStyle);
                mAmap.setMyLocationEnabled(true);

                mLocationClient.stopLocation();
                mAmap.moveCamera(CameraUpdateFactory.zoomTo(Constants2.LocationZoom));
                //将地图移动到定位点
                tv_centerName.setText(aMapLocation.getCity());
                mAmap.moveCamera(CameraUpdateFactory.changeLatLng(mobileLocation));
                isFirstLoc = false;
            } else {
                //定位成功回调信息，设置相关消息
                mLocationClient.stopLocation();
                //然后可以移动到定位点,使用animateCamera就有动画效果
                mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(mobileLocation, Constants2.LocationZoom));
                ll_info.setVisibility(View.GONE);
                btn_yuding.setVisibility(View.GONE);
                btn_yongche.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mAmap.getCameraPosition().zoom > Constants2.AreaMarkerZoom || first) {
            fetchParks();
            showArea(false);
            if (btn_yuding.getVisibility() != View.VISIBLE && btn_yongche.getVisibility() != View.VISIBLE) {
                btn_yongche.startAnimation(showAnim);
                btn_yongche.setVisibility(View.VISIBLE);
            }
        } else {
            if (buldParkBean == null) {
                showArea(true);
                btn_yongche.setVisibility(View.GONE);
                if (areaMarkers.size() == 0) {
                    fetchAreas();
                }
            }
        }
        if (clickMarker != null) {
            clickMarker.setVisible(true);
        }
        first = false;
    }

    private void reset() {
        removeAllOtherMarker();
        clickMarker = null;
        buldParkBean = null;
        ll_info.setVisibility(View.GONE);
        btn_yuding.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
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
        mMapView.onDestroy();
        mLocationClient.onDestroy();
    }

    //驾照Dialog
    private void showDriverStateDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage("您还没有绑定驾驶证")
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("立即绑定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), ActIdentificationDrivingLicense.class);
                        intent.putExtra(Constants.INTENT_TITLE, Constants.INTENT_DRIVER_INFO);
                        startActivity(intent);
                    }
                })
                .create().show();
    }

    //实名Dialog
    private void showIdCardStateDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage("您没还没有实名认证")
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("立即认证", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), ActIdentificationIDCard.class);
                        intent.putExtra(Constants.INTENT_TITLE, Constants.INTENT_REGISTER_ID);
                        startActivity(intent);
                    }
                })
                .create();
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    //限行Dialog
    private void showTrafficControlDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("限行提示")
                .setMessage("该车辆今日限行！因限行引起的违章费用将由您自行负责，请确认是否继续使用该车辆？")
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("继续使用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (buldParkBean != null && buldParkBean.getCooperate() == 0
                                && buldParkBean.getStopInAmount() > 0) { //只有非合作停车场才会有出场费
                            showParkOutAmountDialog(buldParkBean.getStopInAmount() / 100);
                        } else {
                            reservation();
                        }
                    }
                })
                .create().show();
    }

    //未完成订单Dialog
    private void showUnfinshOrder() {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("您还有未完成的订单，请完成订单")
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("去完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(OrderActivity.class);
                    }
                })
                .create().show();
    }

    private void showUnfinishedOrderDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("通知")
                .setMessage("您有未付款的订单")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("去付款", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), OrderActivity.class);
                        startActivityForResult(intent, 103);
                    }
                }).setCancelable(false)
                .create().show();
    }

    //停车费Dialog
    private void showParkOutAmountDialog(int cost) {
        new AlertDialog.Builder(getActivity())
                .setTitle("支付提示")
                .setMessage("该车辆出停车场时可能需要付费" + cost + "元，待订单结束后返还至账户余额")
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reservation();
                    }
                })
                .create().show();
    }

    private void drawRoute() {
        if (walkRouteOverlay != null) {
            walkRouteOverlay.removeFromMap();
        }
        if (mobileLocation == null) {
            return;
        }
        //调整视角
        allLatLngs.add(mobileLocation);
        updateCamera();

        if (buldParkBean != null) {
            mRouteSearch = new RouteSearch(getActivity());
            mRouteSearch.setRouteSearchListener(this);
            final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                    AMapUtil.convertToLatLonPoint(mobileLocation),
                    new LatLonPoint(buldParkBean.getLatitude(), buldParkBean.getLongitude()));
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
            mRouteSearch.calculateWalkRouteAsyn(query);
        }
    }

    private void updateCamera() {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLng : allLatLngs) {
            boundsBuilder.include(latLng);
        }
        int bottomPadding = ToolsUtils.getWindowHeight(getActivity()) - ll_info1.getTop() + paddingSize;
        if (ToolsUtils.checkDeviceHasNavigationBar(getActivity())) {
            bottomPadding += ToolsUtils.getNavigationBarHeight(getActivity());
        }
        mAmap.animateCamera(CameraUpdateFactory.newLatLngBoundsRect(boundsBuilder.build(), paddingSize, paddingSize, paddingSize, bottomPadding));
    }

    private void removeAllOtherMarker() {
        if (circle != null) {
            circle.remove();
            circle = null;
        }
        if (polygon != null) {
            polygon.remove();
            polygon = null;
        }
        if (walkRouteOverlay != null) {
            walkRouteOverlay.removeFromMap();
            walkRouteOverlay = null;
        }
        for (Marker marker : carMarkers) {
            marker.remove();
        }
        allLatLngs.clear();
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
    public boolean onMarkerClick(Marker marker) {
        removeAllOtherMarker();
        if (clickMarker != null && !clickMarker.equals(marker)) {
            clickMarker.setVisible(true);
        }
        if (marker.getObject() != null) {
            if (marker.getObject() instanceof LatLng) {
                mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom((LatLng) marker.getObject(), Constants2.AreaClickZoom));
                return false;
            }
        }
        ParksResult.DataBean dataBean = (ParksResult.DataBean) marker.getObject();
        clickMarker = marker;
        carMarkers.clear();

        mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dataBean.getLatitude(), dataBean.getLongitude()), Constants2.MarkerClickZoom));
        buldParkBean = dataBean;
        parkid = String.valueOf(dataBean.getId());
        tv_pname.setText(dataBean.getName());
        tv_adress.setText(dataBean.getAddress());
        float distance = AMapUtils.calculateLineDistance(
                mobileLocation, new LatLng(dataBean.getLatitude(), dataBean.getLongitude()));
        if (distance > 1000) {
            tv_dis.setText(df.format(distance / 1000) + "km");
        } else {
            tv_dis.setText(((int) distance) + "m");
        }
        // 刷新停车场信息
        parkDetail(dataBean.getId());
        return false;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        bunldBean = carBeans.get(i);
        carid = String.valueOf(bunldBean.getId());
        CarsFragment carsFragment = (CarsFragment) fragments.get(i);
        carsFragment.refresh(carBeans.get(i));
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        if (i == 1000) {
            if (walkRouteOverlay != null) {
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay = null;
            }
            walkRouteOverlay = new WalkRouteOverlay(getActivity(), mAmap, walkRouteResult.getPaths().get(0),
                    walkRouteResult.getStartPos(), walkRouteResult.getTargetPos(), null);
            walkRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
            walkRouteOverlay.addToMap();
//            walkRouteOverlay.zoomToSpan();
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        boolean inPolygon = true;
        if (circle != null) {
            inPolygon = circle.contains(latLng);
        }
        if (polygon != null) {
            inPolygon = polygon.contains(latLng);
        }
        if (!inPolygon) {
            buldParkBean = null;
            if (clickMarker != null) {
                clickMarker.setVisible(true);
            }
            removeAllOtherMarker();
            fetchParks();
            showArea(false);
            if (mLocationClient != null) {
                mLocationClient.startLocation();
            }
        }
    }
}

