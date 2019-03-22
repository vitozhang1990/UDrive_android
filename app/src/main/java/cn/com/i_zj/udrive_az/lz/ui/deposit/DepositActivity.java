package cn.com.i_zj.udrive_az.lz.ui.deposit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.EventPaySuccessEvent;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.lz.bean.AliYajinEvent;
import cn.com.i_zj.udrive_az.lz.bean.WechatYajinEvent;
import cn.com.i_zj.udrive_az.map.MapUtils;
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
import cn.com.i_zj.udrive_az.widget.CommonAlertDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 押金
 */
public class DepositActivity extends DBSBaseActivity {

    private static final int SDK_PAY_FLAG = 1;
    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.deposit_tv_money)
    AppCompatTextView moneyView;

    @BindView(R.id.deposit_tv_status)
    AppCompatTextView depositTvStatus;
    @BindView(R.id.deposit_btn_withdraw)
    AppCompatButton depositBtnWithdraw;
    @BindView(R.id.deposit_btn_recharge)
    AppCompatButton depositBtnRecharge;

    private UserDepositResult userDepositResult;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_deposit;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        header_title.setText("押金");
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserDeposit();
    }

    @OnClick({R.id.header_left, R.id.deposit_btn_recharge, R.id.deposit_btn_withdraw})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left:
                finish();
                break;
            case R.id.deposit_btn_recharge:
                DepositDialogFragment payDialogFragment = new DepositDialogFragment();
                payDialogFragment.show(getSupportFragmentManager(), "pay");
                break;
            case R.id.deposit_btn_withdraw:
                getDepositOrderNumber();
                break;
        }
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
                CommonAlertDialog.builder(this)
                        .setMsg("确定退押金吗")
                        .setMsgCenter(true)
                        .setPositiveButton("不退", null)
                        .setNegativeButton("确定", v -> getDepositMoney(userDepositResult.data.orderNum))
                        .build()
                        .show();
            } else {
                Toast.makeText(DepositActivity.this, "当前状态无法退押金,请稍后再试!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getDepositMoney(String orderNumber) {
        showProgressDialog(true);
        UdriveRestClient.getClentInstance().refundMoney(orderNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RefundDepositResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RefundDepositResult value) {
                        dissmisProgressDialog();
                        if (value == null) {
                            ToastUtils.showShort("退还押金失败");
                            return;
                        }
                        if (value.code == 1) {
                            ToastUtils.showShort("操作成功");
                            getUserDeposit();
                        } else {
                            ToastUtils.showShort(value.message);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dissmisProgressDialog();
                        ToastUtils.showShort("退还押金失败");
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    //获取押金信息
    private void getUserDeposit() {
        showProgressDialog(true);
        UdriveRestClient.getClentInstance().userDeposit()
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
        UdriveRestClient.getClentInstance().getDepositAmount()
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
            depositBtnWithdraw.setEnabled(false);// 提现
            depositBtnRecharge.setEnabled(true);
            dissmisProgressDialog();
        } else if (value.data.payState == 2) {
            depositTvStatus.setText("已缴纳");
            depositBtnWithdraw.setEnabled(true);
            depositBtnRecharge.setEnabled(false);// 不可充值
            dissmisProgressDialog();
        } else if (value.data.payState == 3) {
            depositTvStatus.setText("退款中");
            dissmisProgressDialog();
            depositBtnWithdraw.setEnabled(false);
            depositBtnRecharge.setEnabled(false);
        } else if (value.data.payState == 4) {
            depositTvStatus.setText("已退款");
            depositBtnWithdraw.setEnabled(false);
            depositBtnRecharge.setEnabled(true);
            getDepositAmount();
        } else {
            depositTvStatus.setText("未缴纳");
            depositBtnWithdraw.setEnabled(false);
            depositBtnRecharge.setEnabled(true);
            getDepositAmount();
        }
    }

    private void getCreateDepositNumber(final int type) {
        showProgressDialog(true);
        UdriveRestClient.getClentInstance().createDeposit()
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
        UdriveRestClient.getClentInstance().getAliPayYajinOderInfo(orderNumber)
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
        UdriveRestClient.getClentInstance().getWechatYajinOderInfo(orderNumber)
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
    }

    private void gotoAliPayActivity(final String orderInfo) {
        Runnable payRunnable = () -> {
            PayTask alipay = new PayTask(DepositActivity.this);
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
            }
        }
    };
}
