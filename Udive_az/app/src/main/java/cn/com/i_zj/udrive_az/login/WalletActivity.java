package cn.com.i_zj.udrive_az.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.ui.coupon.CouponListActivity;
import cn.com.i_zj.udrive_az.lz.ui.wallet.MyWalletActivity;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.UnUseCouponResult;
import cn.com.i_zj.udrive_az.model.WalletResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wli on 2018/8/12.
 */

public class WalletActivity extends DBSBaseActivity {

    public static final int PAY_BALANCE = 1001;

    @BindView(R.id.wallet_tv_balance)
    AppCompatTextView balanceView;

    @BindView(R.id.wallet_tv_coupon)
    AppCompatTextView couponeView;

    private WalletResult walletResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.activity_wallet);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
        fetchBalance(authorization);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_wallet;
    }


    @OnClick(R.id.wallet_layout_balance)
    public void onBalanceClick(View view) {
        startActivityForResult(MyWalletActivity.class, PAY_BALANCE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAY_BALANCE && RESULT_OK == resultCode) {
            String authorization = SessionManager.getInstance().getAuthorization();
            fetchBalance(authorization);
        }
    }

    @OnClick(R.id.wallet_layout_coupon)
    public void onCouponClick(View view) {
        startActivity(new Intent(this, CouponListActivity.class));
    }

    private void fetchBalance(String authorization) {
        UdriveRestClient.getClentInstance().myWallet(authorization)
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
            balanceView.setText(String.format(Locale.getDefault(),"%.2f 元",(walletResult.data.userBalance / 100f + walletResult.data.giveBalance / 100f)));
//            couponeView.setText((int) walletResult.data.preferentialAmount + " 张");
        }
    }
}
