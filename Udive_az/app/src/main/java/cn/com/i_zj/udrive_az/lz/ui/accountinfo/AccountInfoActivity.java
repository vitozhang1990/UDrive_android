package cn.com.i_zj.udrive_az.lz.ui.accountinfo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.zjcx.face.camera.CameraActivity;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.ui.accountinfo.certification.ActIdentificationDrivingLicense;
import cn.com.i_zj.udrive_az.lz.ui.accountinfo.certification.ActIdentificationIDCard;
import cn.com.i_zj.udrive_az.lz.view.UserInfoItemView;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.FileUtil;

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

        if (accountInfo != null && (accountInfo.data.idCardState == Constants.ID_UN_AUTHORIZED || accountInfo.data.idCardState == Constants.ID_AUTHORIZED_FAIL)) {
            Intent intent = new Intent(this, ActIdentificationIDCard.class);
            startActivity(intent);
        } else if (accountInfo != null && accountInfo.data.idCardState == Constants.ID_UNDER_REVIEW) {
            Toast.makeText(this, R.string.under_reving, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.account_info_btn_exit)
    public void onExitClick(View view) {
        new AlertDialog.Builder(this).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SessionManager.getInstance().clearSession();
                        finish();
                        dialog.dismiss();
                    }
                }).setMessage("确定要退出么？")
                .create().show();

    }

    @OnClick(R.id.ui_driver_license)
    public void onDriverLicense(View view) {

        Intent intent = new Intent(this, ActIdentificationDrivingLicense.class);
        startActivity(intent);
//        AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
//        if (accountInfo == null) {
//            Toast.makeText(this, "无法获取用户信息", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (accountInfo.data.idCardState == Constants.ID_AUTHORIZED_SUCCESS || accountInfo.data.idCardState == Constants.ID_UNDER_REVIEW) {
//            if (accountInfo.data.driverState == Constants.ID_AUTHORIZED_SUCCESS) {
//                Toast.makeText(this, "驾驶证已认证", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (accountInfo.data.driverState == Constants.ID_UNDER_REVIEW) {
//                Toast.makeText(this, "正在审核中", Toast.LENGTH_SHORT).show();
//            } else {
//                Intent intent = new Intent(this, ActIdentificationDrivingLicense.class);
//                startActivity(intent);
//            }
//        } else {
//            Toast.makeText(this, "请先去实名认证", Toast.LENGTH_SHORT).show();
//        }
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
}
