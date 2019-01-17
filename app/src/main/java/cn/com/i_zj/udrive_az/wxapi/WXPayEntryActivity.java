package cn.com.i_zj.udrive_az.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import cn.com.i_zj.udrive_az.event.EventPayFailureEvent;
import cn.com.i_zj.udrive_az.event.EventPaySuccessEvent;
import cn.com.i_zj.udrive_az.utils.Constants;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

  private IWXAPI iwxapi;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    iwxapi = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_APP_ID, false);
    iwxapi.handleIntent(getIntent(), this);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    iwxapi.handleIntent(intent, this);
  }

  @Override
  public void onReq(BaseReq req) {
    Log.e("onReq:", "req=" + req);
  }

  @Override
  public void onResp(BaseResp resp) {
    Log.e("onResp:", "onResp=" + resp);
    if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

      if (resp.errCode == 0) {
        if (resp instanceof PayResp) {
          if (Constants.WEIXIN_PAY_TYPE_BALANCE.equals(((PayResp) resp).extData)) {
            EventBus.getDefault().post(new EventPaySuccessEvent(EventPaySuccessEvent.PayType.BALANCE, EventPaySuccessEvent.PayMethod.WEICHAT));
          }
        }
        finish();
      } else if (resp.errCode == -2) {
        Toast.makeText(this, "您已取消付款!", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new EventPayFailureEvent());
        finish();
      } else {
        Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new EventPayFailureEvent());
        finish();
      }
    } else {
      finish();
    }
  }
}