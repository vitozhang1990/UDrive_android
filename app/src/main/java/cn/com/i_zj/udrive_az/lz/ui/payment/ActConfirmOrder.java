package cn.com.i_zj.udrive_az.lz.ui.payment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.MainActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.EventPaySuccessEvent;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.lz.bean.CouponPayEvent;
import cn.com.i_zj.udrive_az.lz.ui.order.OrderActivity;
import cn.com.i_zj.udrive_az.lz.util.SpannableStringUtil;
import cn.com.i_zj.udrive_az.lz.view.PaymentView;
import cn.com.i_zj.udrive_az.map.TravelingActivity;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.AliPayOrder;
import cn.com.i_zj.udrive_az.model.AliPayResult;
import cn.com.i_zj.udrive_az.model.DiscountEntity;
import cn.com.i_zj.udrive_az.model.OrderDetailResult;
import cn.com.i_zj.udrive_az.model.PayOrderByBlanceResult;
import cn.com.i_zj.udrive_az.model.UnUseCouponResult;
import cn.com.i_zj.udrive_az.model.WeichatPayOrder;
import cn.com.i_zj.udrive_az.network.UObserver;
import cn.com.i_zj.udrive_az.network.UdriveRestAPI;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.ScreenManager;
import cn.com.i_zj.udrive_az.utils.SizeUtils;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import cn.com.i_zj.udrive_az.web.WebActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author JayQiu
 * @create 2018/10/24
 * @Describe 确认订单页面---未付款
 */
public class ActConfirmOrder extends DBSBaseActivity {
    public static final String TITLE = "title";
    public static final String ORDER_NUMBER = "order_number";
    public static final int PAY_YU_E = 1;
    public static final int PAY_ALI = 2;
    public static final int PAY_WECHAT = 3;
    @BindView(R.id.tv_sub_money_count)
    TextView tvSubMoneyCount;
    @BindView(R.id.tv_coupon)
    TextView tvCoupon;
    @BindView(R.id.tv_money_count)
    TextView tvMoneyCount;
    @BindView(R.id.tv_vip_money_count)
    TextView tvVipMoneyCount;
    @BindView(R.id.tv_real_pay_amount)
    TextView tvRealPayAmount;
    @BindView(R.id.pay_yu_e)
    PaymentView payYuE;
    @BindView(R.id.pay_alipay)
    PaymentView payAlipay;
    @BindView(R.id.pay_wechat)
    PaymentView payWechat;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.tv_vip_money_name)
    TextView mTvVipMoneyName;
    private DiscountEntity discountEntity;
    private String title;
    private String orderNumber;
    private OrderDetailResult.OrderItem mOrderItem;
    private CouponsDialogFragment couponDialogFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_confirm_order;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getIntent().getStringExtra(TITLE);
        orderNumber = getIntent().getStringExtra(ORDER_NUMBER);
        initView();

        findTripOrders();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.confirm_order);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if (!ScreenManager.getScreenManager().isHaveActivity(OrderActivity.class)
                        || ScreenManager.getScreenManager().isHaveActivity(TravelingActivity.class)) {
                    startActivity(MainActivity.class);
                }
                finish();
            }
        });

        payYuE.setView(R.mipmap.ic_payment_yue, "余额", true);
        payAlipay.setView(R.mipmap.ic_payment_alipay, "支付宝", !payYuE.isCheck());
        payWechat.setView(R.mipmap.ic_payment_wechat, "微信", !payYuE.isCheck() && !payAlipay.isCheck());

    }

    @OnClick({R.id.tv_coupon, R.id.btn_commit, R.id.pay_yu_e, R.id.pay_alipay, R.id.pay_wechat, R.id.tv_vip_money_name, R.id.tv_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_coupon:
                if (mOrderItem == null) {
                    return;
                }

                if (couponDialogFragment != null && !couponDialogFragment.isHidden()) {
                    couponDialogFragment.dismiss();
                }
                couponDialogFragment = new CouponsDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("orderNumber", mOrderItem.id + "");
                couponDialogFragment.setArguments(bundle);
                couponDialogFragment.show(getSupportFragmentManager(), "coupon");
                break;
            case R.id.btn_commit:
                if (mOrderItem != null) {
                    if (payYuE.isCheck()) {
                        yuePay(mOrderItem.number);
                    } else if (payAlipay.isCheck()) {
                        getAliTripOrder(mOrderItem.number);
                    } else if (payWechat.isCheck()) {
                        getWechatTripOrder(mOrderItem.number);
                    } else {
                        Toast.makeText(ActConfirmOrder.this, "没有选择支付", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.pay_yu_e:
                select(PAY_YU_E);
                break;
            case R.id.pay_alipay:
                select(PAY_ALI);
                break;
            case R.id.pay_wechat:
                select(PAY_WECHAT);
                break;
            case R.id.tv_vip_money_name:
                showVipDialog();
                break;
            case R.id.tv_detail:
                WebActivity.startWebActivity(ActConfirmOrder.this, UdriveRestAPI.DETAIL_URL + orderNumber);
                break;
        }
    }

    private void select(int position) {
        payYuE.setCheck(position == PAY_YU_E);
        payAlipay.setCheck(position == PAY_ALI);
        payWechat.setCheck(position == PAY_WECHAT);
    }

    private void showVipDialog() {
        new AlertDialog.Builder(ActConfirmOrder.this)
                .setTitle("专享折扣")
                .setMessage("VIP专享折扣。订单结算时，先全额抵扣优惠券，剩余部分再折算折扣金额。特殊活动不参与折扣。不计免赔和停车费不参与折扣。")
                .setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create().show();
    }

    public void findTripOrders() {
        showProgressDialog(true);
        UdriveRestClient.getClentInstance().tripOrderDetail(orderNumber)
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
                            Toast.makeText(ActConfirmOrder.this, "数据请求失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        showDate(value);
                        findUnUsePreferential();

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != e) {
                            ToastUtils.showShort(e.getMessage());
                        }
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showDate(OrderDetailResult value) {
        if (value.data != null) {

            mOrderItem = value.data;

            discountEntity = mOrderItem.getDiscount();
            btnCommit.setVisibility(View.VISIBLE);
            OrderDetailResult.OrderItem orderItem = value.data;
            tvSubMoneyCount.setText((value.data.shouldPayAmount) / 100f + "元");
            AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
            if (accountInfo != null) {

                double balance = (accountInfo.data.balance + accountInfo.data.giveBalance) / (double) 100;
                payYuE.setView(R.mipmap.ic_payment_yue, "余额  " + String.format(Locale.getDefault(), "（%.2f 元）", balance), true);
            }

            if (value.data.preferentialAmount > 0) {
                tvCoupon.setText("");
                SpannableString spannableString1 = SpannableStringUtil.setColorAndSizeSpan("-" + value.data.preferentialAmount / 100f + "元", Color.RED, SizeUtils.sp2px(ActConfirmOrder.this, 14));
                tvCoupon.append(spannableString1);
            } else {
                tvCoupon.setText("不使用优惠券");
            }
            tvMoneyCount.setText("合计" + (orderItem.shouldPayAmount - value.data.preferentialAmount) / 100f + "元");


            if (discountEntity != null) {
                mTvVipMoneyName.setText(discountEntity.getName());

                tvVipMoneyCount.setText("-" + (value.data.discountAmount) / 100f + "元");
                btnCommit.setText("确认支付" + (value.data.realPayAmount) / 100f + "元");
                tvRealPayAmount.setText((value.data.realPayAmount) / 100f + "");
            } else {
                btnCommit.setText("确认支付" + (orderItem.realPayAmount) / 100f + "元");
                tvRealPayAmount.setText((value.data.realPayAmount) / 100f + "");
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CouponPayEvent couponPayEvent) {
        if (couponDialogFragment != null) {
            couponDialogFragment.dismiss();
        }

        if (mOrderItem == null) {
            Toast.makeText(ActConfirmOrder.this, "数据请求失败,请重新请求", Toast.LENGTH_SHORT).show();
            dissmisProgressDialog();
            return;
        }
        UnUseCouponResult.DataBean result = couponPayEvent.getResult();
        if (result != null) {
            payAmount(orderNumber, result.getId() + "");
        } else {
            payAmount(orderNumber, "0");
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
        UdriveRestClient.getClentInstance().payOrderByBalance(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PayOrderByBlanceResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PayOrderByBlanceResult value) {
                        dissmisProgressDialog();
                        if (value != null && value.getCode() == 1) {
                            ActPaySucc.startActPaySucc(ActConfirmOrder.this);
                            finish();
                        }
                        Toast.makeText(ActConfirmOrder.this, value.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != e) {
                            Toast.makeText(ActConfirmOrder.this, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 优惠券
     *
     * @param orderId
     * @param pId
     */
    public void payAmount(String orderId, String pId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("preferentialId", pId);
        map.put("orderId", orderId);
        showProgressDialog(true);
        UdriveRestClient.getClentInstance().payAmount(map)
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
                            Toast.makeText(ActConfirmOrder.this, "数据请求失败,请重新请求", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (value.code == 1) {
                            showDate(value);
                        } else {
                            Toast.makeText(ActConfirmOrder.this, value.message, Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != e) {
                            Toast.makeText(ActConfirmOrder.this, "数据请求失败,请重新请求", Toast.LENGTH_SHORT).show();
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
        UdriveRestClient.getClentInstance().getAliPayTripOrder(orderNumber)
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
        UdriveRestClient.getClentInstance().getWechatTripOrder(orderNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WeichatPayOrder>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WeichatPayOrder value) {
                        dissmisProgressDialog();
                        if (value != null && value.code == 1 || value.code == 1012) {
                            gotoWexinPayActivity(value.data);
                        } else {
                            ToastUtils.showShort("微信支付失败了");
                        }


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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventPaySuccessEvent eventPaySuccessEvent) {
        if (eventPaySuccessEvent.payMethod == EventPaySuccessEvent.PayMethod.WEICHAT) {
            btnCommit.setVisibility(View.GONE);
            dissmisProgressDialog();
            ActPaySucc.startActPaySucc(ActConfirmOrder.this);
            finish();
        }
    }

    private static final int SDK_PAY_FLAG = 1;

    private void gotoAliPayActivity(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(ActConfirmOrder.this);
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
                        btnCommit.setVisibility(View.GONE);
                        dissmisProgressDialog();
                        ActPaySucc.startActPaySucc(ActConfirmOrder.this);
                        finish();
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

    public void findUnUsePreferential() {
        AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
        if (accountInfo == null) {
            Toast.makeText(ActConfirmOrder.this, "数据请求失败", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mOrderItem == null) {
            return;
        }
        UdriveRestClient.getClentInstance().v1FindUnUsePreferential(mOrderItem.id + "", accountInfo.data.userId + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UObserver<List<UnUseCouponResult.DataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<UnUseCouponResult.DataBean> value) {
                        if (!StringUtils.isEmpty(value)) {
                            tvCoupon.setText("");
                            SpannableString spannableString = SpannableStringUtil.setColorAndSizeSpan("可用优惠券", Color.GRAY, SizeUtils.sp2px(ActConfirmOrder.this, 14));
                            tvCoupon.append(spannableString);
                            SpannableString spannableString1 = SpannableStringUtil.setColorAndSizeSpan(value.size() + "", Color.RED, SizeUtils.sp2px(ActConfirmOrder.this, 14));
                            tvCoupon.append(spannableString1);
                            SpannableString spannableString2 = SpannableStringUtil.setColorAndSizeSpan("张", Color.GRAY, SizeUtils.sp2px(ActConfirmOrder.this, 14));
                            tvCoupon.append(spannableString2);
                        } else {
                            tvCoupon.setText("没有可用的优惠券");
                        }
                    }

                    @Override
                    public void onException(int code, String message) {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!ScreenManager.getScreenManager().isHaveActivity(OrderActivity.class)
                    || ScreenManager.getScreenManager().isHaveActivity(TravelingActivity.class)) {
                startActivity(MainActivity.class);
            }
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
