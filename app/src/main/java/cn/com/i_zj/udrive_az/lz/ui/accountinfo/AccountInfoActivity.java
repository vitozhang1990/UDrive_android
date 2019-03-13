package cn.com.i_zj.udrive_az.lz.ui.accountinfo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebStorage;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.OrderFinishEvent;
import cn.com.i_zj.udrive_az.event.WebSocketCloseEvent;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.view.UserInfoItemView;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.network.UObserver;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.step.StepDriveCardActivity;
import cn.com.i_zj.udrive_az.step.StepIdCardActivity;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.widget.CommonAlertDialog;
import cn.jpush.android.api.JPushInterface;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 账户信息
 */
public class AccountInfoActivity extends DBSBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ui_head)
    UserInfoItemView mUiHead;

    @BindView(R.id.ui_phone)
    UserInfoItemView mUiPhone;

    @BindView(R.id.ui_register)
    UserInfoItemView mUiRegister;

    @BindView(R.id.ui_driver_license)
    UserInfoItemView mUiDriverLicense;
    private AccountInfoResult accountInfo;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_account_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        mUiHead.setRightImageVisible(View.INVISIBLE);
        mUiPhone.setRightImageVisible(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        accountInfo = AccountInfoManager.getInstance().getAccountInfo();
        if (null != accountInfo) {
            StringBuilder sb = new StringBuilder(accountInfo.data.username);
            sb.replace(3, 7, "****");
            mUiPhone.setRightText(sb.toString());
            mUiRegister.setRightText(parseIdType(accountInfo.data.idCardState));
            mUiDriverLicense.setRightText(parseIdType(accountInfo.data.driverState));
        }
    }

    @OnClick(R.id.ui_register)
    public void onRegisterClick(View view) {
        if (accountInfo == null) {
            Toast.makeText(this, "无法获取用户信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (accountInfo.data.idCardState == Constants.ID_AUTHORIZED_SUCCESS) {
            Toast.makeText(this, "实名已认证", Toast.LENGTH_SHORT).show();
            return;
        }
        if (accountInfo.data.idCardState == Constants.ID_UNDER_REVIEW) {
            Toast.makeText(this, "正在审核中", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(StepIdCardActivity.class);
    }

    @OnClick(R.id.account_info_btn_exit)
    public void onExitClick(View view) {
        CommonAlertDialog.builder(this)
                .setMsg("确定要退出么？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", v -> {
                    String regId = JPushInterface.getRegistrationID(AccountInfoActivity.this);
                    registrationDown(regId);
                    removeCache();
                })
                .build()
                .show();
    }

    @OnClick(R.id.ui_driver_license)
    public void onDriverLicense(View view) {
        AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
        if (accountInfo == null) {
            Toast.makeText(this, "无法获取用户信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (accountInfo.data.idCardState == Constants.ID_AUTHORIZED_SUCCESS || accountInfo.data.idCardState == Constants.ID_UNDER_REVIEW) {
            if (accountInfo.data.driverState == Constants.ID_AUTHORIZED_SUCCESS) {
                Toast.makeText(this, "驾驶证已认证", Toast.LENGTH_SHORT).show();
                return;
            }
            if (accountInfo.data.driverState == Constants.ID_UNDER_REVIEW) {
                Toast.makeText(this, "正在审核中", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, StepDriveCardActivity.class);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "请先去实名认证", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeCache() {
        try {
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.removeSessionCookies(null);
                cookieManager.removeAllCookies(null);
                cookieManager.flush();
            } else {
                cookieManager.removeAllCookie();
                CookieSyncManager.getInstance().sync();
            }
            WebStorage.getInstance().deleteAllData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parseIdType(int code) {
        // 0未认证，1审核中 2已审核 3认证失败
        String msg = "";
        switch (code) {
            case Constants.ID_UN_AUTHORIZED:
                msg = getResources().getString(R.string.lz_un_authorized);
                break;
            case Constants.ID_UNDER_REVIEW:
                msg = getResources().getString(R.string.lz_under_revier);
                break;
            case Constants.ID_AUTHORIZED_SUCCESS:
                msg = getResources().getString(R.string.lz_authorize_success);
                break;
            case Constants.ID_AUTHORIZED_FAIL:
                msg = getResources().getString(R.string.lz_authorized_fail);
                break;
            default:
                msg = getResources().getString(R.string.lz_un_authorized);
                break;
        }
        return msg;
    }

    public void registrationDown(String regid) {
        Map<String, Object> map = new HashMap<>();
        map.put("regId", regid);
        UdriveRestClient.getClentInstance().registrationDown(map).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        showProgressDialog();
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.e("=====", "==============================");
                        SessionManager.getInstance().clearSession();
                        EventBus.getDefault().post(new OrderFinishEvent());
                        EventBus.getDefault().post(new WebSocketCloseEvent());
                        finish();
                    }

                    @Override
                    public void onException(int code, String message) {
                        showToast(message);
                    }

                    @Override
                    public void onFinish() {
                        dissmisProgressDialog();
                    }
                });
    }
}
