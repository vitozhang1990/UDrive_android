package cn.com.i_zj.udrive_az.step.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.EventPayFailureEvent;
import cn.com.i_zj.udrive_az.event.EventPaySuccessEvent;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.lz.view.PaymentView;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.AliPayOrder;
import cn.com.i_zj.udrive_az.model.AliPayResult;
import cn.com.i_zj.udrive_az.model.CreateDepositResult;
import cn.com.i_zj.udrive_az.model.UserDepositResult;
import cn.com.i_zj.udrive_az.model.WeichatPayOrder;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.ToastUtil;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.yokeyword.fragmentation.SupportFragment;

public class DepositFragment extends SupportFragment {
    private static final int SDK_PAY_FLAG = 1;
    private static final int PAY_ALI = 1;
    private static final int PAY_WECHAT = 2;

    @BindView(R.id.deposit_tv_money)
    AppCompatTextView moneyView;
    @BindView(R.id.pay_alipay)
    PaymentView payAlipay;
    @BindView(R.id.pay_wechat)
    PaymentView payWechat;

    protected Dialog progressDialog;

    public static DepositFragment newInstance() {
        Bundle args = new Bundle();

        DepositFragment fragment = new DepositFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deposit_new, container, false);
        ButterKnife.bind(this, view);

        payWechat.setView(R.mipmap.ic_payment_wechat, "微信", true);
        payAlipay.setView(R.mipmap.ic_payment_alipay, "支付宝", false);
        getUserDeposit();
        return view;
    }

    @OnClick({R.id.pay_alipay, R.id.pay_wechat, R.id.btn_commit})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.pay_alipay:
                select(PAY_ALI);
                break;
            case R.id.pay_wechat:
                select(PAY_WECHAT);
                break;
            case R.id.btn_commit:
                getCreateDepositNumber(payAlipay.isCheck() ? PAY_ALI : PAY_WECHAT);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventPayFailureEvent eventPayFailureEvent) {
        dissmisProgressDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventPaySuccessEvent eventPaySuccessEvent) {
        dissmisProgressDialog();
    }

    private void select(int position) {
        payAlipay.setCheck(position == PAY_ALI);
        payWechat.setCheck(position == PAY_WECHAT);
    }

    private void getUserDeposit() {
        showProgressDialog();
        UdriveRestClient.getClentInstance().userDeposit()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserDepositResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserDepositResult value) {
                        dissmisProgressDialog();
                        if (value.data == null) {
                            ToastUtil.show(getContext(), "获取数据失败");
                        } else {
                            AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
                            if (accountInfo != null) {
                                accountInfo.data.depositState = value.data.payState;
                            }
                            moneyView.setText((int) (value.data.amount / 100) + " 元");
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

    public void showProgressDialog() {
        if (getActivity() == null) {
            return;
        }
        if (null == progressDialog) {
            progressDialog = new Dialog(getActivity(), R.style.MyDialog);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dissmisProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void getCreateDepositNumber(int type) {
        showProgressDialog();
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
                        if (type == PAY_ALI) {
                            getAliYajin(value.data);
                        } else if (type == PAY_WECHAT) {
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
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(getContext(), Constants.WEIXIN_APP_ID, false);
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
            PayTask alipay = new PayTask(getActivity());
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
                        ToastUtil.show(getContext(), "支付成功");
                        dissmisProgressDialog();
                        getActivity().finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtil.show(getContext(), "支付失败");
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
