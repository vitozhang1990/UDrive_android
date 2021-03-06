package cn.com.i_zj.udrive_az.map.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.constant.ParkType;
import cn.com.i_zj.udrive_az.lz.bean.ParkRemark;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.AddressInfo;
import cn.com.i_zj.udrive_az.model.AreaInfo;
import cn.com.i_zj.udrive_az.model.CityListResult;
import cn.com.i_zj.udrive_az.model.FromParkBean;
import cn.com.i_zj.udrive_az.model.ParkDetailResult;
import cn.com.i_zj.udrive_az.model.ParkKey;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.model.WebSocketPark;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
import cn.com.i_zj.udrive_az.network.UObserver;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.AMapUtil;
import cn.com.i_zj.udrive_az.utils.Constants2;
import cn.com.i_zj.udrive_az.utils.dialog.ParkDetailDialog;
import cn.com.i_zj.udrive_az.web.WebActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChooseParkActivity extends DBSBaseActivity implements
        AMapLocationListener, AMap.OnMarkerClickListener {

    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.park_pick_layout)
    LinearLayout parkPickLayout;
    @BindView(R.id.ed_search)
    EditText ed_search;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.stop_amount)
    TextView stop_amount;
    @BindView(R.id.city_amount)
    TextView city_amount;

    @BindView(R.id.tv_city)
    TextView city;
    @BindView(R.id.rl_head)
    LinearLayout toolBar;
    @BindView(R.id.city_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.mengceng)
    View mengceng;
    @BindView(R.id.city_checkbox)
    CheckBox checkBox;

    private AMap mAmap;
    public AMapLocationClient mLocationClient = null;
    private Map<ParkKey, Marker> markerMap = new HashMap();
    private Circle circle;
    private Polygon polygon;

    private ParksResult.DataBean pickPark;
    private FromParkBean fromPark;
    private int orderId;

    private ParkDetailDialog parkDetailDialog;
    private CityListResult cityInfo;
    private ArrayList<CityListResult> mCityList = new ArrayList<>();
    private GlobalAdapter mAdapter;
    private boolean pickModel;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_park;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        initViewstMap(savedInstanceState);

        mAdapter = RecyclerViewUtils.initRecycler(
                this
                , mRecyclerView
                , new GridLayoutManager(this, 3)
                , R.layout.item_city, mCityList
                , new OnGlobalListener() {
                    @Override
                    public <T> void logic(BaseViewHolder helper, T item) {
                        CityListResult ai = (CityListResult) item;
                        helper.setText(R.id.city_name, ai.getAreaName());
                        if (cityInfo != null &&
                                (cityInfo.getAreaCode().equals(ai.getAreaCode()))
                                || cityInfo.getAreaName().equals(ai.getAreaName())) {
                            ((TextView) helper.getView(R.id.city_name)).setTypeface(Typeface.DEFAULT_BOLD);
                        } else {
                            ((TextView) helper.getView(R.id.city_name)).setTypeface(Typeface.DEFAULT);
                        }
                        Glide.with(ChooseParkActivity.this).load(ai.getImg()).crossFade().into((ImageView) helper.getView(R.id.city_pic));
                    }
                }
                , new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        parkPickLayout.setVisibility(View.GONE);
                        cityInfo = mCityList.get(position);
                        pickModel = false;
                        updateUi();
                        fetchParks(cityInfo.getAreaCode());

                        float longitude = Float.valueOf(cityInfo.getCenter().split(",")[0]);
                        float latitude = Float.valueOf(cityInfo.getCenter().split(",")[1]);
                        LatLng latLng = new LatLng(latitude, longitude);
                        mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants2.AreaMarkerZoom));
                    }
                }, R.layout.item_city_empty);
        updateUi();
        getSysCity(orderId);
    }

    private void updateUi() {
        checkBox.setChecked(pickModel);
        mengceng.setVisibility(pickModel ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility(pickModel ? View.VISIBLE : View.GONE);
        if (pickModel) {
            toolBar.setBackgroundResource(R.drawable.bg_map_top1);
        } else {
            toolBar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        if (cityInfo != null) {
            city.setText(cityInfo.getAreaName());
        }
    }

    private void initViewstMap(Bundle savedInstanceState) {
        fromPark = (FromParkBean) getIntent().getSerializableExtra("fromPark");
        String idStr = getIntent().getStringExtra("oderId");
        orderId = Integer.valueOf(idStr);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        mAmap = mMapView.getMap();
        UiSettings uiSettings = mAmap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        MapUtils.setMapCustomStyleFile(this, mAmap);

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 自定义精度范围的圆形边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明的时候就可以去掉园区区域了
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        mAmap.setMyLocationStyle(myLocationStyle);
        mAmap.setMyLocationEnabled(true);

        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();
        mAmap.setOnMarkerClickListener(this);
    }

    @OnClick({R.id.iv_back, R.id.ed_search, R.id.btn_pick, R.id.iv_mylocation, R.id.park_detail
            , R.id.amount, R.id.city_layout, R.id.mengceng})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ed_search:
                startActivityForResult(ChooseStartEndActivity.class, 102);
                break;
            case R.id.btn_pick:
                Intent intent = getIntent();
                intent.putExtra("pickPark", pickPark);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.iv_mylocation:
                if (mLocationClient != null) {
                    mLocationClient.startLocation();
                }
                parkPickLayout.setVisibility(View.GONE);
                break;
            case R.id.park_detail:
                getParkRemark();
                break;
            case R.id.amount:
                WebActivity.startWebActivity(ChooseParkActivity.this, BuildConfig.SHARE_AMOUNT);
                break;
            case R.id.city_layout:
                pickModel = !pickModel;
                mAdapter.notifyDataSetChanged();
                updateUi();
                break;
            case R.id.mengceng:
                pickModel = false;
                updateUi();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == RESULT_OK) {
            AddressInfo poiItem = (AddressInfo) data.getSerializableExtra("poiItem");
            if (poiItem == null) {
                return;
            }
            ed_search.setText(poiItem.getName());
            mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(poiItem.getLat(), poiItem.getLng())
                    , Constants2.AreaShowZoom));
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(WebSocketPark park) {
        ParkKey parkKey = new ParkKey(park.getId(), park.getLongitude(), park.getLatitude());
        ParksResult.DataBean dataBean;
        if (markerMap.containsKey(parkKey)) {
            try {
                dataBean = (ParksResult.DataBean) markerMap.get(parkKey).getObject();
                markerMap.get(parkKey).remove();//清除
                markerMap.remove(parkKey);

                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(park.getLatitude(), park.getLongitude()));
                int bitmapId = R.mipmap.ic_cheweishu_monthly;
                StringBuilder sb = new StringBuilder();
                if (fromPark != null && park.getId() == fromPark.getParkID()) {
                    sb.append("起");
                } else {
                    sb.append("P");
                }
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithShortCut(ChooseParkActivity.this, bitmapId, sb.toString(), String.valueOf(park.getParkCountBalance()), true)));

                Marker marker = mAmap.addMarker(markerOptions);
                marker.setObject(dataBean);
                markerMap.put(parkKey, marker);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getSysCity(int orderId) {
        showProgressDialog();
        UdriveRestClient.getClentInstance().getSysCity(orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRetObj<List<CityListResult>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseRetObj<List<CityListResult>> listBaseRetObj) {
                        dissmisProgressDialog();
                        if (listBaseRetObj == null || listBaseRetObj.getCode() != 1
                                || listBaseRetObj.getDate() == null) {
                            return;
                        }
                        mCityList.clear();
                        mCityList.addAll(listBaseRetObj.getDate());
                        if (mapLocation != null) {
                            doSomeThing();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void fetchParks(String areaCode) {
        UdriveRestClient.getClentInstance().getParks(areaCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ParksResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ParksResult result) {
                        for (Map.Entry<ParkKey, Marker> entry : markerMap.entrySet()) {
                            entry.getValue().remove();
                        }
                        markerMap.clear();
                        List<ParksResult.DataBean> dataBeans = result.getData();
                        for (int i = 0; i < dataBeans.size(); i++) {
                            ParksResult.DataBean dataBean = dataBeans.get(i);
                            ParkKey parkKey = new ParkKey(dataBean.getId(), dataBean.getLongitude(), dataBean.getLatitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(dataBean.getLatitude(), dataBean.getLongitude()));
                            int bitmapId = dataBean.getCooperate() > 0 ? R.mipmap.ic_cheweishu_monthly : R.mipmap.ic_cheweishu_llinshi;
                            StringBuilder sb = new StringBuilder();
                            if (fromPark != null && dataBean.getId() == fromPark.getParkID()) {
                                sb.append("起");
                            } else {
                                sb.append("P");
                            }
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithShortCut(ChooseParkActivity.this, bitmapId, sb.toString(), String.valueOf(dataBean.getParkCountBalance()), true)));

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

    private void getParkRemark() {
        UdriveRestClient.getClentInstance().getParkRemark(String.valueOf(pickPark.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UObserver<ParkRemark>() {
                    @Override
                    public void onSuccess(ParkRemark response) {
                        if (parkDetailDialog != null && parkDetailDialog.isShowing()) {
                            return;
                        }
                        response.setName(pickPark.getName());
                        parkDetailDialog = new ParkDetailDialog(ChooseParkActivity.this);
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
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (circle != null) {
            circle.remove();
            circle = null;
        }
        if (polygon != null) {
            polygon.remove();
            polygon = null;
        }
        pickPark = (ParksResult.DataBean) marker.getObject();
        mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pickPark.getLatitude(), pickPark.getLongitude()), Constants2.MarkerClickZoom));
        parkPickLayout.setVisibility(View.VISIBLE);
        tv_name.setText(pickPark.getName());
        tv_address.setText(pickPark.getAddress());
        if (pickPark.getCooperate() > 0) {
            if (cityInfo.getAmount() > 0) {
                city_amount.setVisibility(View.VISIBLE);
                city_amount.setText(Html.fromHtml("异地还车费 <font color='#000000'>" + cityInfo.getAmount() / 100 + "</font> 元"));
            } else {
                city_amount.setVisibility(View.GONE);
            }
            if (pickPark.getStopedAmount() > 0) {
                stop_amount.setText(Html.fromHtml("该还车点无可用免费车位时将收取 <font color='#000000'>" + pickPark.getStopedAmount() / 100 + "</font> 元超停费"));
            } else {
                stop_amount.setText("该还车点不收取停车费");
            }
        } else {
            if (pickPark.getStopInAmount() > 0) {
                stop_amount.setText(Html.fromHtml("该还车点将收取 <font color='#000000'>" + pickPark.getStopInAmount() / 100 + "</font> 元停车费"));
            } else {
                stop_amount.setText("该还车点不收取停车费");
            }
        }
        UdriveRestClient.getClentInstance().getParkDetail(pickPark.getId())
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

    private AMapLocation mapLocation;

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation == null || aMapLocation.getErrorCode() != 0) {
            return;
        }
        mLocationClient.stopLocation();
        mapLocation = aMapLocation;
        if (mCityList == null || mCityList.size() == 0) {
            mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mapLocation.getLatitude(), mapLocation.getLongitude()), Constants2.AreaMarkerZoom));
            return;
        }
        doSomeThing();
    }

    private void doSomeThing() {
        //逻辑：没有则正常请求，若在白名单内，则使用当前定位请求，否则使用白名单第一个城市请求
        CityListResult cityInfo = new CityListResult();
        cityInfo.setAreaCode(mapLocation.getAdCode());
        cityInfo.setAreaName(mapLocation.getCity().replace("市", ""));
        cityInfo.setAmount(0);
        LatLng mobileLocation = new LatLng(mapLocation.getLatitude(), mapLocation.getLongitude());
        if (mCityList == null || mCityList.size() == 0) {
            fetchParks(cityInfo.getAreaCode());
            this.cityInfo = cityInfo;
            updateUi();
            mAdapter.notifyDataSetChanged();
            mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(mobileLocation, Constants2.AreaMarkerZoom));
            return;
        }
        for (CityListResult bean : mCityList) {
            if (bean.getAreaName().equals(cityInfo.getAreaName())
                    || bean.getAreaCode().equals(cityInfo.getAreaCode())) {
                this.cityInfo = bean;
                fetchParks(cityInfo.getAreaCode());
                updateUi();
                mAdapter.notifyDataSetChanged();
                mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(mobileLocation, Constants2.AreaMarkerZoom));
                return;
            }
        }
        this.cityInfo = mCityList.get(0);
        fetchParks(mCityList.get(0).getAreaCode());
        float longitude = Float.valueOf(mCityList.get(0).getCenter().split(",")[0]);
        float latitude = Float.valueOf(mCityList.get(0).getCenter().split(",")[1]);
        LatLng latLng = new LatLng(latitude, longitude);
        updateUi();
        mAdapter.notifyDataSetChanged();
        mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants2.AreaMarkerZoom));
    }
}
