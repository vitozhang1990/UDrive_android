package cn.com.i_zj.udrive_az.lz.ui.payment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.EventPaySuccessEvent;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.bean.CouponPayEvent;
import cn.com.i_zj.udrive_az.lz.bean.PaymentEvent;
import cn.com.i_zj.udrive_az.lz.util.SpannableStringUtil;
import cn.com.i_zj.udrive_az.lz.view.CarDetailItemView;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.AliPayOrder;
import cn.com.i_zj.udrive_az.model.AliPayResult;
import cn.com.i_zj.udrive_az.model.OrderDetailResult;
import cn.com.i_zj.udrive_az.model.PayOrderByBlanceResult;
import cn.com.i_zj.udrive_az.model.UnUseCouponResult;
import cn.com.i_zj.udrive_az.model.WeichatPayOrder;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import cn.com.i_zj.udrive_az.utils.UIUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PaymentActivity extends DBSBaseActivity implements View.OnClickListener {

    public static final String TITLE = "title";
    public static final String MODE_WAIT = "待付款";
    public static final String MODE_FINISH = "已完成";
    public static final String MODE_MOVE = "行程中";
    public static final String ORDER_NUMBER = "order_number";

    private ImageView ivCar;
    private TextView tvCarNumber;
    private TextView tvCarColor;
    private TextView tvCarType;
    private CarDetailItemView carDistance;
    private CarDetailItemView carTime;
    private CarDetailItemView carCoupon;
    private TextView tvMoney;
    private Button btnPay;
    private ImageView ivKeFu;
    private PaymentDialogFragment paymentDialogFragment;
    private String orderNumber;

    private OrderDetailResult mOrderItem;
    private String title;
    private ImageView ivTrajectory;
    private CouponDialogFragment couponDialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = getIntent().getStringExtra(TITLE);
        orderNumber = getIntent().getStringExtra(ORDER_NUMBER);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivTrajectory = findViewById(R.id.iv_trajectory);
        ivCar = findViewById(R.id.iv_car);
        tvCarNumber = findViewById(R.id.tv_car_number);
        tvCarColor = findViewById(R.id.tv_car_color);
        tvCarType = findViewById(R.id.tv_car_type);
        ivKeFu = findViewById(R.id.iv_ke_fu);
        carDistance = findViewById(R.id.car_distance);
        carTime = findViewById(R.id.car_time);
        carCoupon = findViewById(R.id.car_coupon);
        tvMoney = findViewById(R.id.tv_money);
        btnPay = findViewById(R.id.btn_pay);

        carDistance.setVisibleBottom(View.GONE);
        carTime.setVisibleBottom(View.GONE);

        carCoupon.setOnClickListener(this);
        btnPay.setOnClickListener(this);
        SpannableString spannableString = SpannableStringUtil.setColorAndSizeSpan("0.0", Color.RED, UIUtils.dp2px(30));
        tvMoney.append(spannableString);
        SpannableString yuan = SpannableStringUtil.setColorAndSizeSpan("元", Color.GRAY, UIUtils.dp2px(12));
        tvMoney.append(yuan);

        ivKeFu.setOnClickListener(this);
        if (MODE_FINISH.equals(title)) {
            btnPay.setVisibility(View.GONE);
        } else {
            btnPay.setVisibility(View.VISIBLE);
        }

        findTripOrders();
//        findUnUsePreferential();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_wait_for_payment;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_ke_fu:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + getResources().getString(R.string.about_phone));
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.btn_pay:
                handlePay();
                break;
            case R.id.car_coupon:
                if (TextUtils.equals(title, MODE_WAIT)) {
                    if (couponDialogFragment != null && !couponDialogFragment.isHidden()) {
                        couponDialogFragment.dismiss();
                    }
                    couponDialogFragment = new CouponDialogFragment();
                    couponDialogFragment.show(getSupportFragmentManager(), "coupon");
                }
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PaymentEvent paymentEvent) {
        if (paymentEvent.getType() == 0) {
            yuePay(mOrderItem.data.number);
        } else if (paymentEvent.getType() == 1) {
            getAliTripOrder(mOrderItem.data.number);
        } else if (paymentEvent.getType() == 2) {
            getWechatTripOrder(mOrderItem.data.number);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CouponPayEvent couponPayEvent) {
        if (couponDialogFragment != null) {
            couponDialogFragment.dismiss();
        }

        if (mOrderItem == null || mOrderItem.data == null) {
            Toast.makeText(PaymentActivity.this, "数据请求失败,请重新请求", Toast.LENGTH_SHORT).show();
            dissmisProgressDialog();
            return;
        }
        UnUseCouponResult.DataBean result = couponPayEvent.getResult();
        payAmount(mOrderItem.data.number + "", result.getId() + "");

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventPaySuccessEvent eventPaySuccessEvent) {
        if (eventPaySuccessEvent.payMethod == EventPaySuccessEvent.PayMethod.WEICHAT) {
            dissmisProgressDialog();
        }
    }

    private void handlePay() {
        if (paymentDialogFragment != null && !paymentDialogFragment.isHidden()) {
            paymentDialogFragment.dismiss();
        }
        if (mOrderItem == null) {
            Toast.makeText(this, "无法支付", Toast.LENGTH_SHORT).show();
            return;
        }
        paymentDialogFragment = PaymentDialogFragment.getInstance(mOrderItem);
        paymentDialogFragment.show(getSupportFragmentManager(), "payment");
    }

    public void findTripOrders() {
        showProgressDialog(true);
        UdriveRestClient.getClentInstance().tripOrderDetail(SessionManager.getInstance().getAuthorization(), orderNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderDetailResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(OrderDetailResult value) {
                        if (value == null || value.data == null) {
                            Toast.makeText(PaymentActivity.this, "数据请求失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dissmisProgressDialog();
                        handleDetail(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != e) {
                            Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void payAmount(String orderId, String pId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("preferentialId", pId);
        map.put("orderId", orderId);
        showProgressDialog(true);
        UdriveRestClient.getClentInstance().payAmount(SessionManager.getInstance().getAuthorization(), map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderDetailResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(OrderDetailResult value) {
                        dissmisProgressDialog();
                        if (value == null || value.data == null) {
                            Toast.makeText(PaymentActivity.this, "数据请求失败,请重新请求", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mOrderItem = value;
                        tvMoney.setText("");
                        SpannableString spannableString = SpannableStringUtil.setColorAndSizeSpan(String.format(Locale.getDefault(), "¥  %.2f", value.data.realPayAmount / 100f) + "", Color.RED, UIUtils.dp2px(30));
                        tvMoney.append(spannableString);
                        SpannableString yuan = SpannableStringUtil.setColorAndSizeSpan("元", Color.GRAY, UIUtils.dp2px(12));
                        tvMoney.append(yuan);
                        float v = (value.data.realPayAmount - value.data.shouldPayAmount) / 100f;
                        if (v == 0) {
                            carCoupon.setText("优惠券", "");
                        } else {
                            carCoupon.setText("优惠券", String.format(Locale.getDefault(), "%+.2f元", v));
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != e) {
                            Toast.makeText(PaymentActivity.this, "数据请求失败,请重新请求", Toast.LENGTH_SHORT).show();
                        }


                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void handleDetail(OrderDetailResult value) {
        mOrderItem = value;
        tvCarNumber.setText(value.data.plateNumber + "");
        if (!StringUtils.isEmpty(value.data.url)) {
            Picasso.with(this).load(value.data.url).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(ivTrajectory);
        }

        carDistance.setText("里程(" + value.data.mileage + "公里)", Float.parseFloat(value.data.mileage + "") + "元");

        if (value.data.durationTime > 60) {
            carTime.setText(String.format(Locale.getDefault(), "时长(%.1f小时)", value.data.durationTime / 60f), Float.parseFloat(value.data.realPayAmount / 100f + "") + "元");
        } else {
            carTime.setText("时长(" + (value.data.durationTime) + "分钟)", Float.parseFloat(value.data.realPayAmount / 100f + "") + "元");
        }
        tvMoney.setText("");
        SpannableString spannableString = SpannableStringUtil.setColorAndSizeSpan(String.format(Locale.getDefault(), "¥  %.2f", value.data.realPayAmount / 100f) + "", Color.RED, UIUtils.dp2px(30));
        tvMoney.append(spannableString);
        SpannableString yuan = SpannableStringUtil.setColorAndSizeSpan("元", Color.GRAY, UIUtils.dp2px(12));
        tvMoney.append(yuan);
        float v = (value.data.realPayAmount - value.data.shouldPayAmount) / 100f;
        if (v == 0) {
            carCoupon.setText("优惠券", "");
        } else {
            carCoupon.setText("优惠券", String.format(Locale.getDefault(), "%+.2f元", v));
        }
    }

    private void yuePay(String orderNumber) {
        AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
        if (accountInfo == null) {
            Toast.makeText(this, "无法获取用户信息,支付失败", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("orderNum", orderNumber);
        UdriveRestClient.getClentInstance().payOrderByBalance(SessionManager.getInstance().getAuthorization(), hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PayOrderByBlanceResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PayOrderByBlanceResult value) {
                        Toast.makeText(PaymentActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != e) {
                            Toast.makeText(PaymentActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getAliTripOrder(String orderNumber) {
        showProgressDialog();
        UdriveRestClient.getClentInstance().getAliPayTripOrder(SessionManager.getInstance().getAuthorization(), orderNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AliPayOrder>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AliPayOrder value) {
                        gotoAliPayActivity(value.data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void getWechatTripOrder(String orderNumber) {
        showProgressDialog();
        UdriveRestClient.getClentInstance().getWechatTripOrder(SessionManager.getInstance().getAuthorization(), orderNumber + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WeichatPayOrder>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WeichatPayOrder value) {
                        gotoWexinPayActivity(value.data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void gotoWexinPayActivity(WeichatPayOrder.WeiChatPayDetail payInfo) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_APP_ID, false);
        iwxapi.registerApp(Constants.WEIXIN_APP_ID);

        PayReq payReq = new PayReq();
        payReq.appId = payInfo.appid;
        payReq.partnerId = payInfo.partnerid;
        payReq.prepayId = payInfo.prepayid;
        payReq.packageValue = payInfo.packageValue;
        payReq.nonceStr = payInfo.noncestr;
        payReq.timeStamp = payInfo.timestamp;
        payReq.sign = payInfo.sign;
        boolean result = iwxapi.sendReq(payReq);


        Log.e("*****", "**" + result + "***");
    }

    private static final int SDK_PAY_FLAG = 1;

    private void gotoAliPayActivity(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PaymentActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    AliPayResult payResult = new AliPayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        showToast("支付成功");
                        btnPay.setVisibility(View.GONE);
                        dissmisProgressDialog();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showToast("支付失败");
                        dissmisProgressDialog();
                    }
                    break;
                }
                default:
                    break;
//      }
            }
        }
    };


}
