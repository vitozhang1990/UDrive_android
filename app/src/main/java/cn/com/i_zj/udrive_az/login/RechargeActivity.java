package cn.com.i_zj.udrive_az.login;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.AliPayEvent;
import cn.com.i_zj.udrive_az.event.WeixinPayEvent;
import cn.com.i_zj.udrive_az.model.AliPayOrder;
import cn.com.i_zj.udrive_az.model.AliPayResult;
import cn.com.i_zj.udrive_az.model.RechargeOrder;
import cn.com.i_zj.udrive_az.model.WeichatPayOrder;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 充值Activity
 */
public class RechargeActivity extends DBSBaseActivity {

  @BindView(R.id.charge_et_price)
  EditText priceView;

  private static final int SDK_PAY_FLAG = 1;


  @Override
  protected int getLayoutResource() {
    return R.layout.activity_recharge;
  }

  @OnClick(R.id.charge_tv_charge)
  public void onChargeClick(View view) {
    PayDialogFragment payDialogFragment = new PayDialogFragment();
    payDialogFragment.show(getSupportFragmentManager(), "pay");
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEvent(AliPayEvent aliPayEvent) {
    if (checkEmpty(priceView, "充值金额不能为空")) {
      int amount = Integer.parseInt(priceView.getText().toString());
      createOrder(amount, 1);
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEvent(WeixinPayEvent weixinPayEvent) {
    if (checkEmpty(priceView, "充值金额不能为空")) {
      int amount = Integer.parseInt(priceView.getText().toString());
      createOrder(amount, 2);
    }
  }

  private void createOrder(int amount, final int payType) {
    Map<String, Object> map = new HashMap<>();
    map.put("amount", amount);
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
    Runnable payRunnable = new Runnable() {

      @Override
      public void run() {
        PayTask alipay = new PayTask(RechargeActivity.this);
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
            finish();
          } else {
            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
            showToast("支付失败");
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