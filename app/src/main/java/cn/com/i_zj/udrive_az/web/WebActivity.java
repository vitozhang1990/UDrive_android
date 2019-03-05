package cn.com.i_zj.udrive_az.web;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.ToastUtils;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.MainActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.EventPayFailureEvent;
import cn.com.i_zj.udrive_az.event.EventPaySuccessEvent;
import cn.com.i_zj.udrive_az.event.LoginSuccessEvent;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.login.LoginDialogFragment;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.ui.wallet.MyWalletActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.AliPayOrder;
import cn.com.i_zj.udrive_az.model.AliPayResult;
import cn.com.i_zj.udrive_az.model.RechargeOrder;
import cn.com.i_zj.udrive_az.model.ShareBean;
import cn.com.i_zj.udrive_az.model.Token;
import cn.com.i_zj.udrive_az.model.WeichatPayOrder;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * @author JayQiu
 * @create 2018/10/26
 * @Describe
 */
public class WebActivity extends DBSBaseActivity {
    private static final int SDK_PAY_FLAG = 1;
    private static final int ALI = 1;
    private static final int WECHAT = 2;

    @BindView(R.id.commonWebview)
    BridgeWebView webView;
    @BindView(R.id.progress_bar)
    SeekBar progressBar;
    @BindView(R.id.tv_title)
    TextView tv_title;

    private Context mContext;
    private Disposable disposable;
    private String url;
    private int index = 10;
    private CallBackFunction callBackFunction;

    public static void startWebActivity(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_web;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        mContext = this;

        url = getIntent().getStringExtra("url");
        if (!StringUtils.isEmpty(url)) {
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dissmisProgressDialog();
    }

    @OnClick(R.id.iv_back)
    void back() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    private void initView() {
        progressBar.setProgress(0);
        webView.setDefaultHandler(new DefaultHandler());
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!TextUtils.isEmpty(title)) {
                    tv_title.setText(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                nextPor(newProgress);
            }
        });
        webView.loadUrl(url);
        registerHandler();
        sendToken();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginSuccessEvent event) {
        Token token = null;
        if (!TextUtils.isEmpty(SessionManager.getInstance().getAuthorization())) {
            token = new Token();
            token.setAccessToken(SessionManager.getInstance().getAccess());
            token.setRefreshToken(SessionManager.getInstance().getRefresh());
        }
        StringBuilder gsonString = new StringBuilder();
        if (token != null) {
            gsonString.append(new Gson().toJson(token));
        }
        if (callBackFunction != null) {
            callBackFunction.onCallBack(gsonString.toString());
        } else {
            sendToken();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventPaySuccessEvent eventPaySuccessEvent) {
        Token token = new Token();
        token.setResult("1");
        if (callBackFunction != null) {
            callBackFunction.onCallBack(new Gson().toJson(token));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventPayFailureEvent eventPayFailureEvent) {
        Token token = new Token();
        token.setResult("0");
        if (callBackFunction != null) {
            callBackFunction.onCallBack(new Gson().toJson(token));
        }
    }

    private void registerHandler() {
        //默认接收
        webView.setDefaultHandler(new DefaultHandler());

        webView.registerHandler("JS_Loading", (data, function) -> {
            showProgressDialog();
        });

        webView.registerHandler("JS_Dismiss", (data, function) -> {
            dissmisProgressDialog();
        });

        webView.registerHandler("JS_UserLogin", (data, function) -> {
            callBackFunction = function;
            LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
            loginDialogFragment.setListener(dialog -> finish());
            loginDialogFragment.show(getSupportFragmentManager(), "login");
        });

        webView.registerHandler("JS_RefreshToken", (data, function) -> {
            callBackFunction = function;
            SessionManager.getInstance().refreshToken(true);
        });

        webView.registerHandler("JS_Phone", (data, function) -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri uri = Uri.parse("tel:" + getResources().getString(R.string.about_phone));
            intent.setData(uri);
            startActivity(intent);
        });

        webView.registerHandler("JS_UsingCar", (data, function) -> {
            startActivity(MainActivity.class);
            finish();
        });

        webView.registerHandler("JS_Charge", (data, function) -> {
            JsonObject circleObject = (JsonObject) new JsonParser().parse(data);
            String chargeType = circleObject.get("type").getAsString();
            String chargeAmount = circleObject.get("amount").getAsString();
            if (TextUtils.isEmpty(chargeType) || TextUtils.isEmpty(chargeAmount)) {
                function.onCallBack("0");
                return;
            }

            callBackFunction = function;
            createOrder(chargeAmount, chargeType.equals("0") ? WECHAT : ALI);
        });

        webView.registerHandler("JS_Pay", (data, function) -> {
            JsonObject circleObject = (JsonObject) new JsonParser().parse(data);
            String orderNum = circleObject.get("orderNum").getAsString();
            if (TextUtils.isEmpty(orderNum)) {
                function.onCallBack("0");
                return;
            }

            callBackFunction = function;
            UdriveRestClient.getClentInstance().getWechatRentApp(orderNum)
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
                                IWXAPI iwxapi = WXAPIFactory.createWXAPI(mContext, Constants.WEIXIN_APP_ID, false);
                                iwxapi.registerApp(Constants.WEIXIN_APP_ID);
                                PayReq payReq = new PayReq();
                                payReq.appId = value.data.appid;
                                payReq.partnerId = value.data.partnerid;
                                payReq.prepayId = value.data.prepayid;
                                payReq.packageValue = value.data.packageValue;
                                payReq.nonceStr = value.data.noncestr;
                                payReq.timeStamp = value.data.timestamp;
                                payReq.sign = value.data.sign;
                                boolean result = iwxapi.sendReq(payReq);
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
        });

        webView.registerHandler("JS_WXShare", (data, function) -> {
            callBackFunction = function;
            Gson gson = new GsonBuilder().create();
            ShareBean shareInfo = gson.fromJson(data, ShareBean.class);

            UMWeb web = new UMWeb(shareInfo.getShareUrl().replace("{userName}",
                    SessionManager.getInstance().isLogin() && AccountInfoManager.getInstance().getAccountInfo() != null
                            ? AccountInfoManager.getInstance().getAccountInfo().data.username : "notLogin"));
            web.setTitle(shareInfo.getShareTitle());
            web.setThumb(new UMImage(mContext, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
            web.setDescription(shareInfo.getShareDescr());

            showShareDialog(web);
        });
    }

    private void createOrder(String amount, int payType) {
        Map<String, Object> map = new HashMap<>();
        map.put("amount", Integer.valueOf(amount) * 100);
        map.put("payType", payType);

        UdriveRestClient.getClentInstance().createRechargeOrder(map)
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
        UdriveRestClient.getClentInstance().getAliPayOderInfo(orderItem.number)
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
        UdriveRestClient.getClentInstance().getWeiChatPayOderInfo(orderItem.number)
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
        Runnable payRunnable = () -> {
            PayTask alipay = new PayTask(WebActivity.this);
            Map<String, String> result = alipay.payV2(orderInfo, true);
            Log.i("msp", result.toString());

            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
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
                        Token token = new Token();
                        token.setResult("1");
                        if (callBackFunction != null) {
                            callBackFunction.onCallBack(new Gson().toJson(token));
                        }
                        dissmisProgressDialog();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showToast("支付失败");
                        Token token = new Token();
                        token.setResult("0");
                        if (callBackFunction != null) {
                            callBackFunction.onCallBack(new Gson().toJson(token));
                        }
                        dissmisProgressDialog();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void share(UMWeb web, SHARE_MEDIA type) {
        new ShareAction(WebActivity.this)
                .withMedia(web)
                .setPlatform(type)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        Token token = new Token();
                        if (TextUtils.equals(share_media.getName(), "wxtimeline")) {
                            token.setPlatformType("0");
                        } else if (TextUtils.equals(share_media.getName(), "wxsession")) {
                            token.setPlatformType("1");
                        }
                        if (callBackFunction != null) {
                            callBackFunction.onCallBack(new Gson().toJson(token));
                        }
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        Token token = new Token();
                        token.setPlatformType("1");
                        if (callBackFunction != null) {
                            callBackFunction.onCallBack(new Gson().toJson(token));
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                })
                .share();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void sendToken() {
        Token token = null;
        if (!TextUtils.isEmpty(SessionManager.getInstance().getAuthorization())) {
            token = new Token();
            token.setAccessToken(SessionManager.getInstance().getAccess());
            token.setRefreshToken(SessionManager.getInstance().getRefresh());
        }
        StringBuilder gsonString = new StringBuilder();
        if (token != null) {
            gsonString.append(new Gson().toJson(token));
        }
        webView.callHandler("App_TokenParameters", gsonString.toString(), data -> { //处理js回传的数据
            Toast.makeText(this, data, Toast.LENGTH_LONG).show();
        });
    }

    private void nextPor(final int progress) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        Observable.interval(0, 10, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Object o) {
                        int oldPro = progressBar.getProgress();
                        if (oldPro <= progress) {
                            oldPro = oldPro + 2;
                            progressBar.setProgress(oldPro);
                        }
                        if (oldPro >= 100) {
                            progressBar.setVisibility(View.GONE);
                            disposable.dispose();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
