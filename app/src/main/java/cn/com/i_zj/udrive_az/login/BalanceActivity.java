package cn.com.i_zj.udrive_az.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.WalletResult;
import cn.com.i_zj.udrive_az.utils.Constants;

/**
 * 账户余额
 */
public class BalanceActivity extends DBSBaseActivity {

  @BindView(R.id.balacce_tv_balance)
  AppCompatTextView balanceView;

  WalletResult walletResult;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getIntent().hasExtra(Constants.INTENT_KET_COMMENT_DATA)) {
      String data = getIntent().getStringExtra(Constants.INTENT_KET_COMMENT_DATA);
      if (!TextUtils.isEmpty(data)) {
        walletResult = new Gson().fromJson(data, WalletResult.class);
        if (null != walletResult && null != walletResult.data) {
          balanceView.setText("¥ " + walletResult.data.userBalance);
        }
      }
    }
  }

  @Override
  protected int getLayoutResource() {
    return R.layout.activity_balance;
  }

  @OnClick(R.id.balacce_btn_recharge)
  public void onRechargeClick(View view) {
    startActivity(RechargeActivity.class);
  }

  @OnClick(R.id.balacce_btn_withdraw)
  public void onWithdrawClick(View view) {
    showToast("暂未支持");
  }
}
