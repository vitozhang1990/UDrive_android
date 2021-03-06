package cn.com.i_zj.udrive_az.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
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

import org.greenrobot.eventbus.EventBus;
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
import cn.com.i_zj.udrive_az.event.CityUpdateEvent;
import cn.com.i_zj.udrive_az.event.NetWorkEvent;
import cn.com.i_zj.udrive_az.lz.bean.ParkRemark;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.fragment.BaseFragmentAdapter;
import cn.com.i_zj.udrive_az.map.fragment.CarsFragment;
import cn.com.i_zj.udrive_az.model.AreaInfo;
import cn.com.i_zj.udrive_az.model.AreaTagsResult;
import cn.com.i_zj.udrive_az.model.CityListResult;
import cn.com.i_zj.udrive_az.model.ParkDetailResult;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.CarVosBean;
import cn.com.i_zj.udrive_az.model.ParkDetailResult.DataBean.ParkAreaBean;
import cn.com.i_zj.udrive_az.model.ParkKey;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.network.UObserver;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.overlay.WalkRouteOverlay;
import cn.com.i_zj.udrive_az.utils.AMapUtil;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.Constants2;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.dialog.NavigationDialog;
import cn.com.i_zj.udrive_az.utils.dialog.ParkDetailDialog;
import cn.com.i_zj.udrive_az.widget.ViewPagerIndicator;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 地图MapFragment
 *
 * 按钮点击效果：
 *
 *                       refreshBtn              locationBtn
 *
 * park-----selected------refresh-------------reset,location,zoom
 *      |
 *      |
 *       ---normal--------refresh---------------location,zoom
 *
 * area-------------------refresh-----------------location
 *
 * 异地--------------------refresh-----reset,update to currentCity,zoom,park
 *
 */
public class MapFragment extends DBSBaseFragment implements AMapLocationListener
        , RouteSearch.OnRouteSearchListener, EasyPermissions.PermissionCallbacks
        , AMap.OnMarkerClickListener, ViewPager.OnPageChangeListener
        , AMap.OnMapClickListener, AMap.OnCameraChangeListener {

    @BindView(R.id.map)
    MapView mMapView;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.indicator_line)
    ViewPagerIndicator mIndicatorCircleLine;

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
    private List<ParksResult.DataBean> parkBeans = new ArrayList<>();
    private Map<ParkKey, Marker> parkMarkerMap = new HashMap(); //所有停车场
    private Map<ParkKey, Marker> areaMarkerMap = new HashMap();//所有区域

    private CarVosBean bunldBean; //当前选中的车辆
    private int position; //当前选中的position
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

    private CityListResult mCityInfo;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_map;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        if (!EasyPermissions.hasPermissions(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(this, "您必须授予我们定位权限才可以正常使用", 101, Manifest.permission.ACCESS_FINE_LOCATION);
        }
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
        initViewstMap();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.requestNetwork(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    if (mAmap.getCameraPosition().zoom > Constants2.AreaMarkerZoom) {
                        if (parkMarkerMap.isEmpty()) {
                            updateParkOnly();
                        }
                    } else {
                        if (buldParkBean == null && areaMarkerMap.size() == 0) {
                            updateAreaOnly();
                        }
                    }
                }
            });
        }
    }

    private void initViewstMap() {
        mAmap = mMapView.getMap();
        MapUtils.setMapCustomStyleFile(getContext(), mAmap);
        UiSettings uiSettings = mAmap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        mAmap.moveCamera(CameraUpdateFactory.zoomTo(Constants2.AreaClickZoom));
        //设置定位图标样式
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 自定义精度范围的圆形边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明的时候就可以去掉园区区域了
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_arrow));
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        mAmap.setMyLocationStyle(myLocationStyle);
        mAmap.setMyLocationEnabled(true);

        mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();

        mAmap.setOnCameraChangeListener(this);
        mAmap.setOnMapClickListener(this);
        mAmap.setOnMarkerClickListener(this);
    }

    private void showArea(boolean show) {
        if (show) {
            for (Map.Entry<ParkKey, Marker> entry : parkMarkerMap.entrySet()) {
                entry.getValue().setVisible(!show);
            }
            for (Map.Entry<ParkKey, Marker> entry : areaMarkerMap.entrySet()) {
                entry.getValue().setVisible(show);
            }
        } else {
            for (Map.Entry<ParkKey, Marker> entry : areaMarkerMap.entrySet()) {
                entry.getValue().setVisible(show);
            }
            for (Map.Entry<ParkKey, Marker> entry : parkMarkerMap.entrySet()) {
                entry.getValue().setVisible(!show);
            }
            if (buldParkBean != null) {
                ParkKey parkKey = new ParkKey(buldParkBean.getId(), buldParkBean.getLongitude(), buldParkBean.getLatitude());
                if (parkMarkerMap.containsKey(parkKey)) {
                    parkMarkerMap.get(parkKey).setVisible(false);
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
                if (mAmap.getCameraPosition().zoom > Constants2.AreaMarkerZoom || buldParkBean != null) {
                    fetchParks();
                } else {
                    fetchAreas();
                }
                break;
            case R.id.iv_mylocation:
                if (buldParkBean != null) {
                    reset();
                }
                if (mLocationClient != null) mLocationClient.startLocation();
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
                Intent intent = new Intent();
                intent.setClass(getActivity(), PackageActivity.class);
                intent.putExtra("list", carBeans);
                intent.putExtra("park", buldParkBean);
                intent.putExtra("position", position);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
//                reservationVerify();
                break;
            case R.id.btn_yongche:
                if (SessionManager.getInstance().getAuthorization() == null) {
                    LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
                    loginDialogFragment.show(getChildFragmentManager(), "login");
                    return;
                }
                useCar();
                break;
            case R.id.park_explain:
                startActivity(ParkExplainActivity.class);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetWorkEvent netWorkEvent) {
        if (mAmap.getCameraPosition().zoom > Constants2.AreaMarkerZoom) {
            if (parkMarkerMap.isEmpty()) {
                updateParkOnly();
            }
        } else {
            if (buldParkBean == null && areaMarkerMap.size() == 0) {
                updateAreaOnly();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CityListResult cityInfo) {
        if (mCityInfo == null ||
                (!mCityInfo.getAreaCode().equals(cityInfo.getAreaCode())
                        && !mCityInfo.getAreaName().equals(cityInfo.getAreaName()))) {
            mCityInfo = cityInfo;
            updateCity();
        }
    }

    //若是当前城市，则定位到手机位置，否则按给到的点显示
    private void updateCity() {
        try {
            float longitude = Float.valueOf(mCityInfo.getCenter().split(",")[0]);
            float latitude = Float.valueOf(mCityInfo.getCenter().split(",")[1]);
            LatLng latLng = new LatLng(latitude, longitude);
            mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f));
            reset();

            fetchAreas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void useCar() {
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
            if (parkMarkerMap.containsKey(parkKey)) {
                onMarkerClick(parkMarkerMap.get(parkKey));
            }
        } else {
            ToastUtils.showShort("3公里内无可用车辆");
        }
    }

    private void fetchAreas() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
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
                        for (AreaTagsResult.DataBean dataBean : result.getData()) {
                            LatLng latLng = new LatLng(dataBean.getLatitude(), dataBean.getLongitude());
                            if (!AMapUtil.isLatLngValid(latLng)) {
                                continue;
                            }
                            ParkKey parkKey = new ParkKey(dataBean.getId(), latLng.longitude, latLng.latitude);
                            if (areaMarkerMap.containsKey(parkKey)) {
                                continue;
                            }

                            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithCenterText(getActivity(), R.mipmap.ic_area, dataBean.getName(), 13)));

                            Marker marker = mAmap.addMarker(markerOptions);
                            marker.setObject(dataBean);
                            areaMarkerMap.put(parkKey, marker);
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
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
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
                            LatLng latLng = new LatLng(dataBean.getLatitude(), dataBean.getLongitude());
                            if (!AMapUtil.isLatLngValid(latLng)) {
                                continue;
                            }
                            ParkKey parkKey = new ParkKey(dataBean.getId(), latLng.longitude, latLng.latitude);
                            if (parkMarkerMap.containsKey(parkKey)) {
                                ParksResult.DataBean temp = (ParksResult.DataBean) parkMarkerMap.get(parkKey).getObject();
                                if (temp.getValidCarCount() == dataBean.getValidCarCount()) {
                                    continue;
                                } else {
                                    parkMarkerMap.get(parkKey).remove();
                                    parkMarkerMap.remove(parkKey);
                                }
                            }
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                            int bitmapId = dataBean.getCooperate() > 0 ? R.mipmap.ic_cheweishu_monthly : R.mipmap.ic_cheweishu_llinshi;
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithCenterText(getActivity(), bitmapId, String.valueOf(dataBean.getValidCarCount()))));

                            Marker marker = mAmap.addMarker(markerOptions);
                            marker.setObject(dataBean);
                            parkMarkerMap.put(parkKey, marker);
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
                        if (buldParkBean != null) {
                            ParkKey parkKey = new ParkKey(buldParkBean.getId(), buldParkBean.getLongitude(), buldParkBean.getLatitude());
                            if (parkMarkerMap.containsKey(parkKey)) {
                                parkMarkerMap.get(parkKey).setVisible(false);
                            }
                        }
                        //画区域
                        ParkAreaBean parkAreaBean = parkDetailResult.getData().getParkArea();
                        if (parkAreaBean != null) {
                            switch (parkAreaBean.getParkType()) {
                                case ParkType.Circle:
                                    JsonObject circleObject = (JsonObject) new JsonParser().parse(parkAreaBean.getArea());
                                    String center = circleObject.get("center").getAsString();
                                    if (TextUtils.isEmpty(center)) {
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
                            for (CarVosBean bean : carVosBeans) {
                                CarsFragment carsFragment = CarsFragment.newInstance(bean);
                                fragments.add(carsFragment);
                                imgs.add(R.drawable.view_selector);
                            }

                            bunldBean = carVosBeans.get(0);
                            carid = String.valueOf(bunldBean.getId());
                            BaseFragmentAdapter myPagerAdapter = new BaseFragmentAdapter(getFragmentManager(), fragments);
                            mViewPager.setAdapter(myPagerAdapter);
                            mIndicatorCircleLine.setViewPager(mViewPager, fragments.size());

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
        if (aMapLocation == null || aMapLocation.getErrorCode() != 0) {
            return;
        }
        mLocationClient.stopLocation();
        if (TextUtils.isEmpty(aMapLocation.getAdCode())) {
            mLocationClient.startLocation();
            return;
        }

        mobileLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        CityListResult cityInfo = new CityListResult();
        cityInfo.setAreaCode(aMapLocation.getAdCode());
        cityInfo.setAreaName(aMapLocation.getCity().replace("市", ""));
        Log.e("zaijian", "222__》" + cityInfo.getAreaName());
        LocalCacheUtils.saveDeviceData(Constants.SP_GLOBAL_NAME, Constants.SP_CITY, cityInfo);

        EventBus.getDefault().post(new CityUpdateEvent());
        boolean changed = false;
        if (mCityInfo != null && !mCityInfo.getAreaName().equals(cityInfo.getAreaName())) {
            for (Map.Entry<ParkKey, Marker> entry : areaMarkerMap.entrySet()) {
                entry.getValue().remove();
            }
            areaMarkerMap.clear();
            changed = true;
        }
        mCityInfo = cityInfo;

        if (mAmap.getCameraPosition().zoom > Constants2.AreaMarkerZoom || buldParkBean != null) {
            mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(mobileLocation, Constants2.LocationZoom));
        } else {
            if (changed) {
                mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(mobileLocation, 11f));
            } else {
                mAmap.moveCamera(CameraUpdateFactory.newLatLng(mobileLocation));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mAmap.getCameraPosition().zoom > Constants2.AreaMarkerZoom || first) {
            updateParkOnly();
        } else {
            if (buldParkBean == null) {
                updateAreaOnly();
            }
        }
        first = false;
    }

    /**
     * 移除marker选中的一些东西
     * 包含停车场范围，停车场车辆，导航线路
     */
    private void removeParkDetailOverlay() {
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
        carMarkers.clear();
        allLatLngs.clear();
        buldParkBean = null;
    }

    private void clearParkOverlayAndRefreshPark() {
        removeParkDetailOverlay();
        fetchParks();
        showArea(false);
        showYongche();
    }

    private void updateParkOnly() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                try {
                    fetchParks();
                    showArea(false);
                    if (btn_yuding.getVisibility() != View.VISIBLE && btn_yongche.getVisibility() != View.VISIBLE) {
                        btn_yongche.startAnimation(showAnim);
                        btn_yongche.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            try {
                fetchParks();
                showArea(false);
                if (btn_yuding.getVisibility() != View.VISIBLE && btn_yongche.getVisibility() != View.VISIBLE) {
                    btn_yongche.startAnimation(showAnim);
                    btn_yongche.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateAreaOnly() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                try {
                    showArea(true);
                    btn_yongche.setVisibility(View.GONE);
                    fetchAreas();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            try {
                showArea(true);
                btn_yongche.setVisibility(View.GONE);
                fetchAreas();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 重置到没有任何东西的状态
     */
    private void reset() {
        removeParkDetailOverlay();
        for (Map.Entry<ParkKey, Marker> entry : areaMarkerMap.entrySet()) {
            entry.getValue().remove();
        }
        for (Map.Entry<ParkKey, Marker> entry : parkMarkerMap.entrySet()) {
            entry.getValue().remove();
        }
        areaMarkerMap.clear();
        parkMarkerMap.clear();

        ll_info.setVisibility(View.GONE);
        btn_yuding.setVisibility(View.GONE);
        btn_yongche.setVisibility(View.GONE);
    }

    private void resetAndShowYongChe() {
        removeParkDetailOverlay();
        for (Map.Entry<ParkKey, Marker> entry : areaMarkerMap.entrySet()) {
            entry.getValue().remove();
        }
        for (Map.Entry<ParkKey, Marker> entry : parkMarkerMap.entrySet()) {
            entry.getValue().remove();
        }
        areaMarkerMap.clear();
        parkMarkerMap.clear();
        showYongche();
    }

    private void showYongche() {
        ll_info.setVisibility(View.GONE);
        btn_yuding.setVisibility(View.GONE);
        btn_yongche.startAnimation(showAnim);
        btn_yongche.setVisibility(View.VISIBLE);
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
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        mMapView.onDestroy();
        mLocationClient.onDestroy();
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
        removeParkDetailOverlay();
        if (marker.getObject() != null) {
            if (marker.getObject() instanceof AreaTagsResult.DataBean) {
                AreaTagsResult.DataBean dataBean = (AreaTagsResult.DataBean) marker.getObject();
                mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dataBean.getLatitude(), dataBean.getLongitude()), Constants2.AreaClickZoom));
                return false;
            }
        }
        ParksResult.DataBean dataBean = (ParksResult.DataBean) marker.getObject();
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
        position = i;
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
            clearParkOverlayAndRefreshPark();
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (cameraPosition.zoom > Constants2.AreaMarkerZoom) { //显示小图标
            updateParkOnly();
        } else {//显示区域
            if (buldParkBean == null) {
                updateAreaOnly();
            }
        }
    }
}

