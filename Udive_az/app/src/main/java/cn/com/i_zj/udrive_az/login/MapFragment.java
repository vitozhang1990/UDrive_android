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
import cn.com.i_zj.udrive_az.map.ReserveActivity;
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
import cn.com.i_zj.udrive_az.network.UObserver;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.overlay.WalkRouteOverlay;
import cn.com.i_zj.udrive_az.utils.AMapUtil;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.Constants2;
import cn.com.i_zj.udrive_az.utils.ToastUtil;
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
public class MapFragment extends DBSBaseFragment implements AMapLocationListener, RouteSearch.OnRouteSearchListener,
        EasyPermissions.PermissionCallbacks, AMap.OnMarkerClickListener, ViewPager.OnPageChangeListener {


    @BindView(R.id.map)
    MapView mMapView;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.tblayout)
    TabLayout tabLayout;

    @BindView(R.id.rl_where)
    RelativeLayout rl_where;

    @BindView(R.id.ll_info)
    LinearLayout ll_info;

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

    private Circle circle;//停车场范围圆
    private Polygon polygon;//停车场范围多边形
    private List<Marker> carMarkers = new ArrayList<>();//停车场小车

    private List<Fragment> fragments;
    private List<AreaTagsResult.DataBean> areaBeans = new ArrayList<>();
    private List<ParksResult.DataBean> parkBeans = new ArrayList<>();
    private Marker clickMarker;//选中的marker
    private Map<ParkKey, Marker> markerMap = new HashMap();

    private CarVosBean bunldBean; //当前选中的车辆
    private ParksResult.DataBean buldParkBean; //选中的停车场
    private ArrayList<CarVosBean> carBeans = new ArrayList<>(); //车辆列表
    private LatLng mobileLocation;
    private String parkid;
    private String carid;

    private TranslateAnimation showAnim;
    private Animation animRefresh;
    private ParkDetailDialog parkDetailDialog;
    private boolean showArea = false;//当前是否为地区marker
    private DecimalFormat df = new DecimalFormat("0.0");

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
                    if (btn_yuding.getVisibility() != View.VISIBLE && btn_yongche.getVisibility() != View.VISIBLE) {
                        btn_yongche.startAnimation(showAnim);
                        btn_yongche.setVisibility(View.VISIBLE);
                    }
                } else {//显示区域
                    if (buldParkBean == null) {
                        fetchAreas();
                        btn_yongche.setVisibility(View.GONE);
                    }
                }
            }
        });
        mAmap.setOnMarkerClickListener(this);
    }

    @OnClick({R.id.iv_refresh, R.id.iv_mylocation, R.id.tv_park_detail, R.id.rl1,
            R.id.btn_yuding, R.id.btn_yongche})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_refresh:
                if (animRefresh != null) {
                    ivRefresh.startAnimation(animRefresh);
                }
                buldParkBean = null;
                if (clickMarker != null) {
                    clickMarker.setVisible(true);
                }
                removeAllOtherMarker();
                fetchParks();
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
                if (bunldBean != null && buldParkBean != null) {
                    if (bunldBean.isTrafficControl()) {
                        showTrafficControlDialog();
                    } else {
                        reservationVerify();
                    }
                } else {
                    ToastUtils.showShort("请先选择车辆");
                }
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
                if (bestPark != null) {
                    ParkKey parkKey = new ParkKey(bestPark.getId(), bestPark.getLongitude(), bestPark.getLatitude());
                    if (markerMap.containsKey(parkKey)) {
                        onMarkerClick(markerMap.get(parkKey));
                    }
                } else {
                    ToastUtils.showShort("尚未找到可用车辆");
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetWorkEvent netWorkEvent) {
        if (mAmap.getCameraPosition().zoom > Constants2.AreaMarkerZoom) {
            if (markerMap.isEmpty()) {
                fetchParks();
                if (btn_yuding.getVisibility() != View.VISIBLE && btn_yongche.getVisibility() != View.VISIBLE) {
                    btn_yongche.startAnimation(showAnim);
                    btn_yongche.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (buldParkBean == null && areaBeans.size() == 0) {
                fetchAreas();
                btn_yongche.setVisibility(View.GONE);
            }
        }
    }

    private void fetchAreas() {
        if (showArea) {
            return;
        }
        UdriveRestClient.getClentInstance().getAreaTags()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AreaTagsResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(AreaTagsResult result) {
                        showArea = true;
                        markerMap.clear();
                        areaBeans.clear();
                        areaBeans.addAll(result.getData());
                        mAmap.clear();
                        for (int i = 0; i < areaBeans.size(); i++) {
                            LatLng latLng = new LatLng(areaBeans.get(i).getLatitude(), areaBeans.get(i).getLongitude());
                            if (!AMapUtil.isLatLngValid(latLng)) {
                                continue;
                            }
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithCenterText(getActivity(), R.mipmap.ic_area, areaBeans.get(i).getName())));

                            Marker marker = mAmap.addMarker(markerOptions);
                            marker.setObject(latLng);
                        }
                        if (animRefresh != null) {
                            ivRefresh.clearAnimation();
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
                        if (showArea) {//如果之前是地区，则需要全部刷新
                            showArea = false;
                            mAmap.clear();
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
                            ivRefresh.clearAnimation();
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

    private void reservationVerify() {
        if (SessionManager.getInstance().getAuthorization() != null) {
            AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
            if (accountInfo == null) {
                return;
            }
            if (accountInfo.data.idCardState == Constants.ID_AUTHORIZED_SUCCESS && accountInfo.data.driverState == Constants.ID_AUTHORIZED_SUCCESS) {
                if (accountInfo.data.depositState == 2) {
                    if (buldParkBean != null && buldParkBean.getCooperate() == 0
                            && buldParkBean.getStopInAmount() > 0) { //只有非合作停车场才会有出场费
                        showParkOutAmountDialog(buldParkBean.getStopInAmount() / 100);
                    } else {
                        reservation();
                    }
                } else {
                    ToastUtil.show(getActivity(), "请先缴纳押金");
                }
            } else {
                // 先认证身份证 成功后再认证驾照
                if (accountInfo.data.idCardState == Constants.ID_UNDER_REVIEW) {
                    makeText(getActivity(), "实名认证正在审核中", Toast.LENGTH_SHORT).show();
                    return;
                } else if (accountInfo.data.idCardState == Constants.ID_AUTHORIZED_SUCCESS) {// 认证成功
                    //开始认证认证驾照
                    if (accountInfo.data.driverState == Constants.ID_UNDER_REVIEW) {
                        makeText(getActivity(), "驾照认证正在审核中", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        showDriverStateDialog();
                    }
                } else {
                    showIdCardStateDialog();

                }

            }

        } else {
            LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
            loginDialogFragment.show(getChildFragmentManager(), "login");
        }
    }

    private void reservation() {
        Map<String, String> map = new HashMap<>();
        map.put("carId", carid);
        map.put("startParkId", parkid);
        String token = SessionManager.getInstance().getAuthorization();
        showProgressDialog("正在预约");
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
                                        reset();
                                        Intent intent = new Intent(getActivity(), ReserveActivity.class);
                                        intent.putExtra("type", "1");
                                        intent.putExtra("bunld", bunldBean);
                                        intent.putExtra("bunldPark", buldParkBean);
                                        intent.putExtra("id", result.getData().getReservationId() + "");
                                        startActivity(intent);
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
        showProgressDialog("正在查询");
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
                            rl_where.setVisibility(View.GONE);
                            btn_yongche.setVisibility(View.GONE);
                            btn_yuding.setVisibility(View.VISIBLE);
                            btn_yuding.setText("暂无车辆");
                            btn_yuding.setEnabled(false);
                        }
                        float distance = AMapUtils.calculateLineDistance(
                                mobileLocation, new LatLng(buldParkBean.getLatitude(), buldParkBean.getLongitude()));
                        if (distance < 3000) {
                            drawRoute();
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
                rl_where.setVisibility(View.GONE);
                btn_yuding.setVisibility(View.GONE);
                btn_yongche.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mAmap.getCameraPosition().zoom > Constants2.AreaMarkerZoom) {
            fetchParks();
            if (btn_yuding.getVisibility() != View.VISIBLE && btn_yongche.getVisibility() != View.VISIBLE) {
                btn_yongche.startAnimation(showAnim);
                btn_yongche.setVisibility(View.VISIBLE);
            }
        } else {
            if (buldParkBean == null) {
                fetchAreas();
                btn_yongche.setVisibility(View.GONE);
            }
        }
        if (clickMarker != null) {
            clickMarker.setVisible(true);
        }
    }

    private void reset() {
        removeAllOtherMarker();
        ll_info.setVisibility(View.GONE);
        rl_where.setVisibility(View.GONE);
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
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
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
                .setNegativeButton("继续使用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reservationVerify();
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    //未完成订单Dialog
    private void showUnfinshOrder() {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("您还有未完成的订单，请完成订单")
                .setNegativeButton("去完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(OrderActivity.class);
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    //停车费Dialog
    private void showParkOutAmountDialog(int cost) {
        new AlertDialog.Builder(getActivity())
                .setTitle("支付提示")
                .setMessage("该车辆出停车场时可能需要付费" + cost + "元，待订单结束后返还至账户余额")
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
        rl_where.setVisibility(View.GONE);

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
            walkRouteOverlay.zoomToSpan();
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}

