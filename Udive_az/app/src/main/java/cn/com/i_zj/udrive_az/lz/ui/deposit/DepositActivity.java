package cn.com.i_zj.udrive_az.lz.ui.deposit;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.login.AliPayEvent;
import cn.com.i_zj.udrive_az.login.DBSBaseActivity;
import cn.com.i_zj.udrive_az.login.EventPaySuccessEvent;
import cn.com.i_zj.udrive_az.login.PayDialogFragment;
import cn.com.i_zj.udrive_az.login.RechargeActivity;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.login.WeixinPayEvent;
import cn.com.i_zj.udrive_az.lz.bean.AliYajinEvent;
import cn.com.i_zj.udrive_az.lz.bean.WechatYajinEvent;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.AliPayOrder;
import cn.com.i_zj.udrive_az.model.AliPayResult;
import cn.com.i_zj.udrive_az.model.CreateDepositResult;
import cn.com.i_zj.udrive_az.model.DepositAmountResult;
import cn.com.i_zj.udrive_az.model.RefundDepositResult;
import cn.com.i_zj.udrive_az.model.UserDepositResult;
import cn.com.i_zj.udrive_az.model.WeichatPayOrder;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DepositActivity extends DBSBaseActivity {

    private static final int SDK_PAY_FLAG = 1;
    @BindView(R.id.deposit_tv_money)
    AppCompatTextView moneyView;

    @BindView(R.id.deposit_tv_status)
    AppCompatTextView depositTvStatus;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_deposit;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("押金");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserDeposit();
    }


    @OnClick(R.id.deposit_btn_recharge)
    public void onRechargeClick(View view) {
        DepositDialogFragment payDialogFragment = new DepositDialogFragment();
        payDialogFragment.show(getSupportFragmentManager(), "pay");

    }

    @OnClick(R.id.deposit_btn_withdraw)
    public void onWithDrawClick(View view) {
        getDepositOrderNumber();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AliYajinEvent aliPayEvent) {
        if (userDepositResult != null) {
            if (!TextUtils.isEmpty(userDepositResult.data.orderNum) && userDepositResult.data.payState == 1) {
                getAliYajin(userDepositResult.data.orderNum);
            } else {
                getCreateDepositNumber(1);
            }
        } else {
            getCreateDepositNumber(1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WechatYajinEvent weixinPayEvent) {
        if (userDepositResult != null) {
            if (!TextUtils.isEmpty(userDepositResult.data.orderNum) && userDepositResult.data.payState == 1) {
                getWechatYajin(userDepositResult.data.orderNum);

            } else {
                getCreateDepositNumber(2);
            }
        } else {
            getCreateDepositNumber(2);
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventPaySuccessEvent eventPaySuccessEvent) {
        if (eventPaySuccessEvent.payMethod == EventPaySuccessEvent.PayMethod.WEICHAT) {
            dissmisProgressDialog();
        }
    }
    private void getDepositOrderNumber() {
        if (userDepositResult == null) {
            Toast.makeText(DepositActivity.this, "获取信息失败,请稍后再试!", Toast.LENGTH_SHORT).show();
        } else {
            if (userDepositResult != null && userDepositResult.data.payState == 2) {
                new AlertDialog.Builder(this)
                        .setTitle("确定退押金吗")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                getDepositMoney(userDepositResult.data.orderNum);
                            }
                        })
                        .setNegativeButton("不退", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            } else {
                Toast.makeText(DepositActivity.this, "当前状态无法退押金,请稍后再试!", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void getDepositMoney(String orderNumber) {
        showProgressDialog("加载中...",true);
        UdriveRestClient.getClentInstance().refundMoney(SessionManager.getInstance().getAuthorization(), orderNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RefundDepositResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RefundDepositResult value) {
                        dissmisProgressDialog();
                        moneyView.setText("0 元");
                        Toast.makeText(DepositActivity.this, "退还押金成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dissmisProgressDialog();
                        System.out.println(e.getMessage());
                        Toast.makeText(DepositActivity.this, "退还押金失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    private UserDepositResult userDepositResult;

    //获取押金信息
    private void getUserDeposit() {
        showProgressDialog("加载中...",true);
        UdriveRestClient.getClentInstance().userDeposit(SessionManager.getInstance().getAuthorization())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserDepositResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserDepositResult value) {
                        if (value.data == null) {
                            showToast("获取数据失败");
                        } else {
                            AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
                            if (accountInfo != null) {
                                accountInfo.data.depositState = value.data.payState;
                            }
                            moneyView.setText((int) (value.data.amount / 100) + " 元");
                            parseDepositStatus(value);
                            userDepositResult = value;
                        }
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

    //获取押金金额
    private void getDepositAmount() {
        UdriveRestClient.getClentInstance().getDepositAmount(SessionManager.getInstance().getAuthorization())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DepositAmountResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DepositAmountResult value) {
                        moneyView.setText((int) (value.data / 100) + " 元");
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

    private void parseDepositStatus(UserDepositResult value) {
        if (value.data.payState == 1) {
            depositTvStatus.setText("待缴纳");
            dissmisProgressDialog();
        } else if (value.data.payState == 2) {
            depositTvStatus.setText("已缴纳");
            dissmisProgressDialog();
        } else if (value.data.payState == 3) {
            depositTvStatus.setText("退款中");
            dissmisProgressDialog();
        } else if (value.data.payState == 4) {
            depositTvStatus.setText("已退款");
            getDepositAmount();
        } else {
            depositTvStatus.setText("未缴纳");
            getDepositAmount();
        }
    }

    private void getCreateDepositNumber(final int type) {
        showProgressDialog("加载中...",true);
        UdriveRestClient.getClentInstance().createDeposit(SessionManager.getInstance().getAuthorization())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CreateDepositResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CreateDepositResult value) {
                        System.out.println("============ " + value.data);
                        if (type == 1) {
                            getAliYajin(value.data);
                        } else if (type == 2) {
                            getWechatYajin(value.data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("============ " + e.getMessage());
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("============ onComplete");
                    }
                });
    }

    private void getAliYajin(String orderNumber) {
        UdriveRestClient.getClentInstance().getAliPayYajinOderInfo(SessionManager.getInstance().getAuthorization(), orderNumber)
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

    private void getWechatYajin(String orderNumber) {
        UdriveRestClient.getClentInstance().getWechatYajinOderInfo(SessionManager.getInstance().getAuthorization(), orderNumber + "")
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

    private void gotoAliPayActivity(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(DepositActivity.this);
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
                        dissmisProgressDialog();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showToast("支付失败");dissmisProgressDialog();
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
