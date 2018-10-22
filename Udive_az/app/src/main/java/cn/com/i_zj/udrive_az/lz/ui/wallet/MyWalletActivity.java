package cn.com.i_zj.udrive_az.lz.ui.wallet;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.DBSBaseActivity;
import cn.com.i_zj.udrive_az.login.EventPaySuccessEvent;
import cn.com.i_zj.udrive_az.login.RechargeActivity;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.util.SpannableStringUtil;
import cn.com.i_zj.udrive_az.lz.view.PaymentView;
import cn.com.i_zj.udrive_az.model.AliPayOrder;
import cn.com.i_zj.udrive_az.model.AliPayResult;
import cn.com.i_zj.udrive_az.model.RechargeOrder;
import cn.com.i_zj.udrive_az.model.WalletResult;
import cn.com.i_zj.udrive_az.model.WeichatPayOrder;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.UIUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyWalletActivity extends DBSBaseActivity {
    private static final int SDK_PAY_FLAG = 1;
    private static final int ALI = 1;
    private static final int WECHAT = 2;

    @BindView(R.id.tv_yu_msg)
    TextView tv_yu_msg;

    @BindView(R.id.tv_zeng_e_msg)
    TextView tv_zeng_e_msg;

    @BindView(R.id.tv_ben_jin_msg)
    TextView tv_ben_jin_msg;

    @BindView(R.id.tv30)
    TextView tv30;
    @BindView(R.id.tv50)
    TextView tv50;
    @BindView(R.id.tv100)
    TextView tv100;
    @BindView(R.id.tv300)
    TextView tv300;
    @BindView(R.id.tv1000)
    TextView tv1000;
    @BindView(R.id.et_custom)
    EditText etCustom;
    private TextView[] tVArray;

//    @BindView(R.id.ed_monty)
//    EditText ed_monty;

    @BindView(R.id.pay_alipay)
    PaymentView pay_alipay;

    @BindView(R.id.pay_wechat)
    PaymentView pay_wechat;

    @BindView(R.id.tv_msg)
    TextView tv_msg;

    private int payMoney = 30;
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("账单");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bind = ButterKnife.bind(this);
        SpannableString spannableString = SpannableStringUtil.setColorAndSizeSpan("点击充值即表示你已同意", Color.GRAY, UIUtils.dp2px(15));
        tv_msg.append(spannableString);
        SpannableString yuan = SpannableStringUtil.setColorAndSizeSpan("<<充值协议>>", Color.BLACK, UIUtils.dp2px(15));
        tv_msg.append(yuan);


        tVArray = new TextView[]{tv30, tv50, tv100, tv300, tv1000, etCustom};


        pay_alipay.setView(R.mipmap.zhifub, "支付宝", true);
        pay_wechat.setView(R.mipmap.weixinzhi, "微信", !pay_alipay.isCheck());

        setSelect(0, 30);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyWallet();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_my_wallet;
    }

    @OnClick(R.id.tv30)
    public void click30(View view) {
        setSelect(0, 30);
    }

    @OnClick(R.id.tv50)
    public void click50(View view) {
        setSelect(1, 50);
    }

    @OnClick(R.id.tv100)
    public void click100(View view) {
        setSelect(2, 100);
    }

    @OnClick(R.id.tv300)
    public void click300(View view) {
        setSelect(3, 300);
    }

    @OnClick(R.id.tv1000)
    public void click1000(View view) {
        setSelect(4, 1000);
    }

    @OnClick(R.id.et_custom)
    public void clickCustom(View view) {
        setSelect(5, -1);
    }

    @OnClick(R.id.pay_alipay)
    public void clickAlipay(View view) {
        selectPay(ALI);
    }

    @OnClick(R.id.pay_wechat)
    public void clickWechat(View view) {
        selectPay(WECHAT);
    }

    @OnClick(R.id.btn_commit)
    public void clickCommit(View view) {
        if (payMoney == -1) {
            String s = etCustom.getText().toString();
            if (TextUtils.isEmpty(s)) {
                Toast.makeText(this, "数据不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            payMoney = Integer.parseInt(s);
        }
        showProgressDialog("正在准备付款", true);
        createOrder(payMoney * 100, pay_alipay.isCheck() ? ALI : WECHAT);
    }

    private void setSelect(int number, int money) {
        payMoney = money;
        for (int i = 0; i < tVArray.length; i++) {
            tVArray[i].setSelected(i == number);
            tVArray[i].setTextColor(i == number ? getResources().getColor(R.color.pink) : getResources().getColor(R.color.black));
        }
        if (number == tVArray.length - 1) {
        }
//        ed_monty.setVisibility(number == tVArray.length - 1 ? View.VISIBLE : View.GONE);
    }

    private void selectPay(int position) {
        pay_alipay.setCheck(position == ALI);
        pay_wechat.setCheck(position == WECHAT);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventPaySuccessEvent eventPaySuccessEvent) {
        if (eventPaySuccessEvent.payMethod == EventPaySuccessEvent.PayMethod.WEICHAT) {
            dissmisProgressDialog();
        }
    }
    //获取押金信息
    private void getMyWallet() {
        showProgressDialog("加载中...", true);
        UdriveRestClient.getClentInstance().myWallet(SessionManager.getInstance().getAuthorization())
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WalletResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WalletResult value) {
                        if (value.data == null) {
                            Toast.makeText(MyWalletActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                        } else {
                            tv_yu_msg.setText(String.format(Locale.getDefault(), "¥ %.2f", Float.parseFloat("" + (value.data.userBalance / 100f + value.data.giveBalance / 100f))));
                            tv_zeng_e_msg.setText(String.format(Locale.getDefault(), "¥ %.2f", Float.parseFloat("" + value.data.giveBalance / 100f)));
                            tv_ben_jin_msg.setText(String.format(Locale.getDefault(), "¥ %.2f", Float.parseFloat("" + value.data.userBalance / 100f)));
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

    private void createOrder(int amount, final int payType) {
        Map<String, Object> map = new HashMap<>();
        map.put("amount", amount);
        map.put("payType", payType);

        String token = SessionManager.getInstance().getAuthorization();
        UdriveRestClient.getClentInstance().createRechargeOrder(token, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RechargeOrder>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RechargeOrder rechargeOrder) {
                        if (1 == payType) {
                            getAliPayOrderInfo(rechargeOrder.orderItem);
                        } else if (2 == payType) {
                            getWeiChatOderInfo(rechargeOrder.orderItem);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    private void getAliPayOrderInfo(RechargeOrder.RechargeOrderItem orderItem) {
        String token = SessionManager.getInstance().getAuthorization();
        UdriveRestClient.getClentInstance().getAliPayOderInfo(token, orderItem.number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AliPayOrder>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AliPayOrder rechargeOrder) {
                        gotoAliPayActivity(rechargeOrder.data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    private void getWeiChatOderInfo(RechargeOrder.RechargeOrderItem orderItem) {
        String token = SessionManager.getInstance().getAuthorization();
        UdriveRestClient.getClentInstance().getWeiChatPayOderInfo(token, orderItem.number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WeichatPayOrder>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WeichatPayOrder rechargeOrder) {
                        if (null != rechargeOrder && null != rechargeOrder.data) {
                            gotoWexinPayActivity(rechargeOrder.data);
                        } else {
                            showToast("创建订单失败，请重试");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        showToast(e.getMessage());
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

    private void gotoAliPayActivity(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(MyWalletActivity.this);
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
                        getMyWallet();
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
