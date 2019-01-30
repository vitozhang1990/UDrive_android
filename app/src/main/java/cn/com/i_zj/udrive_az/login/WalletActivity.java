package cn.com.i_zj.udrive_az.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.ui.coupons.CouponsActivity;
import cn.com.i_zj.udrive_az.lz.ui.wallet.MyWalletActivity;
import cn.com.i_zj.udrive_az.lz.util.SpannableStringUtil;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.WalletResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.SizeUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wli on 2018/8/12.
 * 我的钱包
 */

public class WalletActivity extends DBSBaseActivity {

    public static final int PAY_BALANCE = 1001;
    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.wallet_tv_balance)
    AppCompatTextView balanceView;

    @BindView(R.id.wallet_tv_coupon)
    AppCompatTextView couponeView;

    private WalletResult walletResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        header_title.setText(R.string.activity_wallet);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String authorization = SessionManager.getInstance().getAuthorization();
        if (TextUtils.isEmpty(authorization)) {
            showToast("尚未登录，请重新登录");
            finish();
            return;
        }
        fetchBalance();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_wallet;
    }


    @OnClick({R.id.header_left, R.id.wallet_layout_balance})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left:
                finish();
                break;
            case R.id.wallet_layout_balance:
                startActivityForResult(MyWalletActivity.class, PAY_BALANCE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAY_BALANCE && RESULT_OK == resultCode) {
            fetchBalance();
        }
    }

    @OnClick(R.id.wallet_layout_coupon)
    public void onCouponClick(View view) {
        startActivity(new Intent(this, CouponsActivity.class));
    }

    private void fetchBalance() {
        UdriveRestClient.getClentInstance().myWallet()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WalletResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WalletResult walletResult) {

                        bindData(walletResult);
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

    private void bindData(WalletResult walletResult) {
        this.walletResult = walletResult;
        if (null != walletResult && null != walletResult.data) {
            double balance = (walletResult.data.userBalance + walletResult.data.giveBalance) / (double) 100;
            balanceView.setText(String.format(Locale.getDefault(), "%.2f 元", balance));
            couponeView.setText("");
            SpannableString spannableString = SpannableStringUtil.setColorAndSizeSpan(walletResult.data.preferentialAmount + "", Color.BLACK, SizeUtils.sp2px(WalletActivity.this, 14));
            couponeView.append(spannableString);
            SpannableString spannableString1 = SpannableStringUtil.setColorAndSizeSpan(" 张", Color.GRAY, SizeUtils.sp2px(WalletActivity.this, 14));
            couponeView.append(spannableString1);
        }
    }
}
