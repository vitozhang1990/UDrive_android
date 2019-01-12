package cn.com.i_zj.udrive_az.map.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
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
import cn.com.i_zj.udrive_az.model.ParkDetailResult;
import cn.com.i_zj.udrive_az.model.ParkKey;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.model.WebSocketPark;
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

    private AMap mAmap;
    public AMapLocationClient mLocationClient = null;
    private boolean isFirstLoc = true;
    private Map<ParkKey, Marker> markerMap = new HashMap();
    private Circle circle;
    private Polygon polygon;

    private ParksResult.DataBean pickPark;
    private ParksResult.DataBean fromPark;

    private ParkDetailDialog parkDetailDialog;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_park;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        initViewstMap(savedInstanceState);
        fetchParks();
    }

    private void initViewstMap(Bundle savedInstanceState) {
        fromPark = (ParksResult.DataBean) getIntent().getSerializableExtra("fromPark");
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

        //测试marker
        mAmap.setOnMarkerClickListener(this);
    }

    @OnClick({R.id.iv_back, R.id.ed_search, R.id.btn_pick, R.id.iv_mylocation, R.id.park_detail, R.id.amount})
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
                break;
            case R.id.park_detail:
                getParkRemark();
                break;
            case R.id.amount:
                WebActivity.startWebActivity(ChooseParkActivity.this, BuildConfig.SHARE_AMOUNT, "费用说明");
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
            ed_search.setText(poiItem.getTitle());
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
            if (pickPark.getStopedAmount() > 0) {
                stop_amount.setText("该还车点无可用免费车位时将收取 " + pickPark.getStopedAmount() / 100 + " 元超停费");
            } else {
                stop_amount.setText("该还车点不收取停车费");
            }
        } else {
            if (pickPark.getStopInAmount() > 0) {
                stop_amount.setText("该还车点将收取 " + pickPark.getStopInAmount() / 100 + " 元停车费");
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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            LatLng mobileLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            if (isFirstLoc) {
//                这段代码是修改样式去掉阴影圆圈地图的
                MyLocationStyle myLocationStyle = new MyLocationStyle();
                myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 自定义精度范围的圆形边框颜色
                myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));//圆圈的颜色,设为透明的时候就可以去掉园区区域了
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
                mAmap.setMyLocationStyle(myLocationStyle);
                mAmap.setMyLocationEnabled(true);

                mLocationClient.stopLocation();
                mAmap.moveCamera(CameraUpdateFactory.zoomTo(Constants2.LocationZoom));
                //将地图移动到定位点
                mAmap.moveCamera(CameraUpdateFactory.changeLatLng(mobileLocation));
                isFirstLoc = false;
            } else {
                mLocationClient.stopLocation();
                mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(mobileLocation, Constants2.LocationZoom));
            }
        }
    }
}
