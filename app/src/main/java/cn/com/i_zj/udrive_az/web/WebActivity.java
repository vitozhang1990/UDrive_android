package cn.com.i_zj.udrive_az.web;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.github.lzyzsd.jsbridge.BridgeHandler;
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
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.MainActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.EventPayFailureEvent;
import cn.com.i_zj.udrive_az.event.EventPaySuccessEvent;
import cn.com.i_zj.udrive_az.event.LoginSuccessEvent;
import cn.com.i_zj.udrive_az.login.LoginDialogFragment;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.map.MapUtils;
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
    @BindView(R.id.commonWebview)
    BridgeWebView webView;
    @BindView(R.id.progress_bar)
    SeekBar progressBar;
    @BindView(R.id.tv_title)
    TextView tv_title;

    private Context mContext;
    private Disposable disposable;
    private String url;
    private String title;
    private int index = 10;
    private CallBackFunction callBackFunction;

    public static void startWebActivity(Context context, String url, String title) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
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

        title = getIntent().getStringExtra("title");
        tv_title.setText(title);
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
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventPaySuccessEvent eventPaySuccessEvent) {
        if (eventPaySuccessEvent.payMethod == EventPaySuccessEvent.PayMethod.WEICHAT) {
            Token token = new Token();
            token.setResult("1");
            if (callBackFunction != null) {
                callBackFunction.onCallBack(new Gson().toJson(token));
            }
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

        webView.registerHandler("JS_UserLogin", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (BuildConfig.DEBUG) {
                    String msg = "JS_UserLogin指定接收到js的数据：" + data;
                    Toast.makeText(WebActivity.this, msg, Toast.LENGTH_LONG).show();
                }

                callBackFunction = function;
                LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
                loginDialogFragment.setListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                loginDialogFragment.show(getSupportFragmentManager(), "login");
            }
        });

        webView.registerHandler("JS_Phone", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (BuildConfig.DEBUG) {
                    String msg = "JS_Phone指定接收到js的数据：" + data;
                    Toast.makeText(WebActivity.this, msg, Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri uri = Uri.parse("tel:" + getResources().getString(R.string.about_phone));
                intent.setData(uri);
                startActivity(intent);
            }
        });

        webView.registerHandler("JS_UsingCar", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (BuildConfig.DEBUG) {
                    String msg = "JS_UsingCar指定接收到js的数据：" + data;
                    Toast.makeText(WebActivity.this, msg, Toast.LENGTH_LONG).show();
                }

                startActivity(MainActivity.class);
                finish();
            }
        });

        webView.registerHandler("JS_Pay", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (BuildConfig.DEBUG) {
                    String msg = "JS_Pay指定接收到js的数据：" + data;
                    Toast.makeText(WebActivity.this, msg, Toast.LENGTH_LONG).show();
                }

                JsonObject circleObject = (JsonObject) new JsonParser().parse(data);
                String orderNum = circleObject.get("orderNum").getAsString();
                if (TextUtils.isEmpty(orderNum)) {
                    function.onCallBack("0");
                    return;
                }

                callBackFunction = function;
                UdriveRestClient.getClentInstance().getWechatRentApp(SessionManager.getInstance().getAuthorization(), orderNum)
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
            }
        });

        webView.registerHandler("JS_WXShare", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (BuildConfig.DEBUG) {
                    String msg = "JS_WXShare指定接收到js的数据：" + data;
                    Toast.makeText(WebActivity.this, msg, Toast.LENGTH_LONG).show();
                }

                callBackFunction = function;
                Gson gson = new GsonBuilder().create();
                ShareBean shareInfo = gson.fromJson(data, ShareBean.class);

                UMWeb web = new UMWeb(shareInfo.getShareUrl());
                web.setTitle(shareInfo.getShareTitle());
                web.setThumb(new UMImage(mContext, shareInfo.getShareImage()));
                web.setDescription(shareInfo.getShareDescr());

                ShareAction shareAction = new ShareAction(WebActivity.this);
                shareAction.withMedia(web);
                shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);
                shareAction.setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        ToastUtils.showShort("成功");
                        Token token = new Token();
                        token.setResult("1");
                        if (callBackFunction != null) {
                            callBackFunction.onCallBack(new Gson().toJson(token));
                        }
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        ToastUtils.showShort("分享失败");
                        Token token = new Token();
                        token.setResult("0");
                        if (callBackFunction != null) {
                            callBackFunction.onCallBack(new Gson().toJson(token));
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        ToastUtils.showShort("分享取消");
                        Token token = new Token();
                        token.setResult("0");
                        if (callBackFunction != null) {
                            callBackFunction.onCallBack(new Gson().toJson(token));
                        }
                    }
                });
                shareAction.open();
            }
        });
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
        webView.callHandler("App_TokenParameters", gsonString.toString(), new CallBackFunction() {
            @Override
            public void onCallBack(String data) { //处理js回传的数据
                if (BuildConfig.DEBUG) {
                    Toast.makeText(WebActivity.this, "App_TokenParameters ==" + data, Toast.LENGTH_LONG).show();
                }
            }
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
                        Log.e("====>", oldPro + "============");
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
            finish();
            return true;
        }
    }
}
