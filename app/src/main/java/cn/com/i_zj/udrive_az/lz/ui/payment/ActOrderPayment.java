package cn.com.i_zj.udrive_az.lz.ui.payment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.bean.OriginContrail;
import cn.com.i_zj.udrive_az.lz.ui.violation.ViolationActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.CarInfoEntity;
import cn.com.i_zj.udrive_az.model.OrderDetailResult;
import cn.com.i_zj.udrive_az.model.ShareInfo;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
import cn.com.i_zj.udrive_az.network.UdriveRestAPI;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.refuel.RefuelHistoryActivity;
import cn.com.i_zj.udrive_az.utils.AMapUtil;
import cn.com.i_zj.udrive_az.utils.CarTypeImageUtils;
import cn.com.i_zj.udrive_az.utils.dialog.ShareCouponDialog;
import cn.com.i_zj.udrive_az.web.WebActivity;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author JayQiu
 * @create 2018/10/26
 * @Describe 订单支付---完成
 */
public class ActOrderPayment extends DBSBaseActivity {
    public static final String ORDER_NUMBER = "order_number";
    public static final String ORDER_ID = "order_id";
    private final String TAG = getClass().getSimpleName();

    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.header_image)
    ImageView header_image;

    @BindView(R.id.iv_car)
    ImageView ivCar;
    @BindView(R.id.tv_car_number)
    TextView tvCarNumber;
    @BindView(R.id.tv_car_color)
    TextView tvCarColor;
    @BindView(R.id.tv_car_type)
    TextView tvCarType;
    @BindView(R.id.iv_ke_fu)
    ImageView ivKeFu;
    @BindView(R.id.tv_mileage)
    TextView tvMileage;
    @BindView(R.id.tv_duration_time)
    TextView tvDurationTime;
    @BindView(R.id.tv_real_pay_amount)
    TextView tvRealPayAmount;
    @BindView(R.id.tv_detail)
    TextView tvDetail;
    @BindView(R.id.iv_imag)
    ImageView mIvImage;
    @BindView(R.id.mv_map)
    MapView mMapView;
    @BindView(R.id.tv_oil_detail)
    TextView mOilDetail;
    @BindView(R.id.tv_illegal_detail)
    TextView mIllegalDetail;

    private AMap mAmap;
    private Context mContext;
    private String orderNumber;
    private int orderId;
    private String shareUrl;
    private ArrayList<Polyline> mDrawnLines = new ArrayList<>(); //所有的line集合

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_order_payment;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        mContext = this;
        orderNumber = getIntent().getStringExtra(ORDER_NUMBER);
        orderId = getIntent().getIntExtra(ORDER_ID, 0);

        header_title.setText("已完成");
        initViewstMap(savedInstanceState);
        findTripOrders();
        getShareUrl();
    }

    private void initViewstMap(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        mAmap = mMapView.getMap();
        UiSettings uiSettings = mAmap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        MapUtils.setMapCustomStyleFile(this, mAmap);
    }

    public void getShareUrl() {
        UdriveRestClient.getClentInstance().getShareUrl(orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRetObj<ShareInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseRetObj<ShareInfo> shareInfoBaseRetObj) {
                        if (shareInfoBaseRetObj != null && shareInfoBaseRetObj.getCode() == 1 &&
                                shareInfoBaseRetObj.getDate() != null && !TextUtils.isEmpty(shareInfoBaseRetObj.getDate().url)) {
                            shareUrl = shareInfoBaseRetObj.getDate().url;
                            header_image.setImageResource(R.drawable.ic_enjoy);
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

    public void findTripOrders() {
        showProgressDialog(true);
        UdriveRestClient.getClentInstance().tripOrderDetail(orderNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(result -> {
                    if (result == null || result.data == null) {
                        ToastUtils.showShort("数据请求失败");
                        return false;
                    }
                    return true;
                })
                .flatMap((Function<OrderDetailResult, ObservableSource<BaseRetObj<List<OriginContrail>>>>)
                        orderDetailResult -> {
                            showDate(orderDetailResult);
                            return UdriveRestClient.getClentInstance()
                                    .originContrail(orderDetailResult.data.id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread());
                        })
                .filter(result -> {
                    if (result == null || result.getDate() == null
                            || result.getDate().size() == 0) {
                        return false;
                    }
                    return true;
                })
                .map(listBaseRetObj -> {
                    List<TraceLocation> locations = new ArrayList<>();
                    for (OriginContrail originContrail : listBaseRetObj.getDate()) {
                        TraceLocation location = new TraceLocation();
                        location.setLatitude(originContrail.getLatitude());
                        location.setLongitude(originContrail.getLongitude());
                        location.setSpeed(originContrail.getSpeed());
                        location.setBearing(originContrail.getDirection());
                        location.setTime(originContrail.getTime());
                        locations.add(location);
                    }
                    return locations;
                })
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<List<TraceLocation>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<TraceLocation> traceLocations) {
                        LBSTraceClient.getInstance(mContext).queryProcessedTrace(1, traceLocations, 0, new TraceListener() {
                            @Override
                            public void onRequestFailed(int i, String s) {
                                Log.d(TAG, "onRequestFailed");
                            }

                            @Override
                            public void onTraceProcessing(int i, int i1, List<LatLng> list) {
                                Log.d(TAG, "onTraceProcessing");
                            }

                            @Override
                            public void onFinished(int i, List<LatLng> list, int i1, int i2) {
                                Log.d(TAG, "onFinished");
                                if (list == null) {
                                    return;
                                }
                                drawTrace(list);
                                mMapView.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    private void drawTrace(List<LatLng> latLngs) {
        PolylineOptions ooPolyline = new PolylineOptions().width(13)
                .color(0xFF707070).addAll(latLngs);
        Polyline mColorfulPolyline = mAmap.addPolyline(ooPolyline);
        if (mColorfulPolyline != null) {
            mDrawnLines.add(mColorfulPolyline);
        }
        drawMarker(latLngs.get(0), latLngs.get(latLngs.size() - 1));
        //调整视角
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs) {
            boundsBuilder.include(latLng);
        }
        mAmap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 250));
    }

    private void drawMarker(LatLng start, LatLng end) {
        MarkerOptions startMarkerOptions = new MarkerOptions().position(start);
        startMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithShortCut(mContext, R.mipmap.ic_cheweishu_monthly, "起", "0")));
        MarkerOptions endMarkerOptions = new MarkerOptions().position(end);
        endMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(AMapUtil.bitmapWithShortCut(mContext, R.mipmap.ic_cheweishu_monthly, "终", "0")));
        mAmap.addMarker(startMarkerOptions);
        mAmap.addMarker(endMarkerOptions);
    }

    private void showDate(OrderDetailResult value) {
        if (value.data != null) {
            tvRealPayAmount.setText((value.data.realPayAmount) / 100f + "");
            if (value.data.durationTime > 60) {
                tvDurationTime.setText("时长(" + value.data.durationTime / 60 + "小时" + (value.data.durationTime % 60 > 0 ? value.data.durationTime % 60 + "分钟)" : ")"));
            } else {
                tvDurationTime.setText("时长(" + (value.data.durationTime) + "分钟)");
            }
            tvMileage.setText("里程(" + value.data.mileage + "公里)");
            tvCarNumber.setText(value.data.plateNumber + "");
            CarInfoEntity carInfoEntity = value.data.getCar();
            if (carInfoEntity != null) {
                tvCarType.setText(carInfoEntity.getBrand());
                tvCarColor.setText(carInfoEntity.getCarColor());
                Glide.with(ActOrderPayment.this).load(CarTypeImageUtils.getCarImageByBrand(carInfoEntity.getBrand(), carInfoEntity.getCarColor())).into(ivCar);
            }
            mOilDetail.setVisibility(value.data.refuel ? View.VISIBLE : View.GONE);
            mIllegalDetail.setVisibility(value.data.illegal ? View.VISIBLE : View.GONE);
        }
    }

    @OnClick({R.id.header_left, R.id.header_right, R.id.tv_detail, R.id.tv_oil_detail, R.id.tv_illegal_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left:
                finish();
                break;
            case R.id.header_right:
                if (TextUtils.isEmpty(shareUrl)) {
                    return;
                }
                ShareCouponDialog dialog = new ShareCouponDialog(this);
                dialog.setListener(() -> {
                    UMWeb web = new UMWeb(shareUrl);
                    web.setTitle("【你行你开】领取红包，出行更便宜");
                    web.setThumb(new UMImage(mContext, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
                    web.setDescription("我刚用完车，快来和我一起领取红包吧");
                    showShareDialog(web);
                });
                dialog.show();
                break;
            case R.id.tv_detail:
                WebActivity.startWebActivity(ActOrderPayment.this, UdriveRestAPI.DETAIL_URL + orderNumber);
                break;
            case R.id.tv_oil_detail:
                RefuelHistoryActivity.startActivity(this, orderNumber);
                break;
            case R.id.tv_illegal_detail:
                Intent intent = new Intent();
                intent.setClass(this, ViolationActivity.class);
                intent.putExtra("number", orderNumber);
                startActivity(intent);
                break;
        }
    }

    private void showShareDialog(UMWeb web) {
        View view = LayoutInflater.from(this).inflate(R.layout.customshare_layout, null);
        final Dialog dialog = new Dialog(this, R.style.UpdateDialogStytle);
        dialog.setContentView(view);
        dialog.show();
        View.OnClickListener listener = v -> {
            switch (v.getId()) {
                case R.id.view_share_weixin:
                    // 分享到微信
                    share(web, SHARE_MEDIA.WEIXIN);
                    break;
                case R.id.view_share_oyq:
                    // 分享到朋友圈
                    share(web, SHARE_MEDIA.WEIXIN_CIRCLE);
                    break;
                case R.id.share_cancel_btn:
                    // 取消
                    break;
            }
            dialog.dismiss();
        };
        ViewGroup mViewWeixin = view.findViewById(R.id.view_share_weixin);
        ViewGroup mViewPengyou = view.findViewById(R.id.view_share_oyq);
        TextView mBtnCancel = view.findViewById(R.id.share_cancel_btn);
        mViewWeixin.setOnClickListener(listener);
        mViewPengyou.setOnClickListener(listener);
        mBtnCancel.setOnClickListener(listener);

        // 设置相关位置，一定要在 show()之后
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    private void share(UMWeb web, SHARE_MEDIA type) {
        new ShareAction(this)
                .withMedia(web)
                .setPlatform(type)
                .share();
    }
}