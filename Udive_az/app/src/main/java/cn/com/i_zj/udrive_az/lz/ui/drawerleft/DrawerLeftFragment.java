package cn.com.i_zj.udrive_az.lz.ui.drawerleft;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.MainActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.login.DBSBaseFragment;
import cn.com.i_zj.udrive_az.lz.ui.deposit.DepositActivity;
import cn.com.i_zj.udrive_az.login.GotoLoginDialogEvent;
import cn.com.i_zj.udrive_az.login.LoginSuccessEvent;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.login.WalletActivity;
import cn.com.i_zj.udrive_az.lz.ui.about.AboutActivity;
import cn.com.i_zj.udrive_az.lz.ui.order.OrderActivity;
import cn.com.i_zj.udrive_az.lz.ui.accountinfo.AccountInfoActivity;
import cn.com.i_zj.udrive_az.lz.view.DrawerItemView;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.UnFinishOrderResult;
import cn.com.i_zj.udrive_az.model.UserDepositResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Time:2018/8/11
 * User:lizhen
 * Description:
 */

public class DrawerLeftFragment extends DBSBaseFragment {
    //https://gitee.com/tmac6741/file
    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;

    @BindView(R.id.tv_user_name)
    TextView mTvUserName;

    @BindView(R.id.tv_account_type)
    TextView mTvAccountType;

    @BindView(R.id.di_my_type)
    DrawerItemView mDiMyType;

    @BindView(R.id.di_deposit)
    DrawerItemView mDiDeposit;

    @BindView(R.id.di_money)
    DrawerItemView mDiMoney;

    @BindView(R.id.di_about)
    DrawerItemView mDbAbout;

    @BindView(R.id.share)
    ImageView mDbShare;

    @BindView(R.id.rl_head)
    RelativeLayout mRlHead;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_drawer_left;
    }

    @Override
    public void onResume() {
        super.onResume();
        dissmisProgressDialog();
        if (SessionManager.getInstance().isLogin()) {
            //获取用户信息
            getUserInfo();

            //获取我的行程
            getUnfinishedOrder();
        }
        AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
        if (accountInfo != null) {
            mTvUserName.setText(accountInfo.data.username);
            parseIdType(accountInfo.data.idCardState);
            mDiMoney.setRightText(String.format(Locale.getDefault(),"%.2f 元",(accountInfo.data.balance / 100f + accountInfo.data.giveBalance / 100f) ));
            parseMoney(accountInfo);
        } else {
            mTvUserName.setText("");
            mTvAccountType.setText("");
            mDiMoney.setRightText("");
            mDiDeposit.setRightText("");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @OnClick(R.id.rl_head)
    public void onHeadClick(View view) {
        if (SessionManager.getInstance().isLogin()) {
            startActivity(AccountInfoActivity.class);
        } else {
            EventBus.getDefault().post(new GotoLoginDialogEvent(GotoLoginDialogEvent.NextJump.ACCOUNT_INFO_ACTIVITY));
        }
    }

    @OnClick(R.id.di_my_type)
    public void onMyTypeClick(View view) {
        if (SessionManager.getInstance().isLogin()) {
            startActivity(OrderActivity.class);
        } else {
            EventBus.getDefault().post(new GotoLoginDialogEvent(GotoLoginDialogEvent.NextJump.ORDER_ACTIVITY));
        }
    }


    @OnClick(R.id.di_deposit)
    public void onMyCarClick(View view) {
//    if (SessionManager.getInstance().isLogin()) {
//      startActivity(MyCarActivity.class);
//    } else {
//      EventBus.getDefault().post(new GotoLoginDialogEvent(GotoLoginDialogEvent.NextJump.MY_CAR_ACTIVITY));
//    }
    }

    @OnClick(R.id.di_money)
    public void onMoneyClick(View view) {
        if (SessionManager.getInstance().isLogin()) {
            startActivity(WalletActivity.class);
        } else {
            EventBus.getDefault().post(new GotoLoginDialogEvent(GotoLoginDialogEvent.NextJump.MONEY_ACTIVITY));
        }
    }

    @OnClick(R.id.di_deposit)
    public void onDepositClick(View view) {
        if (SessionManager.getInstance().isLogin()) {
            startActivity(DepositActivity.class);
        } else {
            EventBus.getDefault().post(new GotoLoginDialogEvent(GotoLoginDialogEvent.NextJump.MONEY_ACTIVITY));
        }
    }

    @OnClick(R.id.di_about)
    public void onAboutClick(View view) {
        startActivity(AboutActivity.class);
    }

    @OnClick(R.id.share)
    public void onShareClick(View view) {
        System.out.println("====================================================");
        String url = "http://www.zzbcjj.com?ref?referral=";
        if (SessionManager.getInstance().isLogin()) {
            AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
            if (accountInfo != null) {
                url = url + accountInfo.data.username;
            }
        } else {
            url = url + "notLogin";
        }
        UMWeb web = new UMWeb(url);
        web.setTitle("快来和我一起使用你行你开");//标题
        web.setThumb(new UMImage(getContext(), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));  //缩略图
        web.setDescription("新用户注册可以获得360元超大礼包哦!");//描述
        new ShareAction((MainActivity) getContext())
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        if (TextUtils.equals(share_media.getName(), "wxtimeline")) {
                            showProgressDialog("准备分享到朋友圈", true);
                        } else if (TextUtils.equals(share_media.getName(), "wxsession")) {
                            showProgressDialog("准备分享给朋友", true);
                        } else if (TextUtils.equals(share_media.getName(), "wxfavorite")) {
                            showProgressDialog("准备收藏", true);
                        }

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        Toast.makeText(getContext(), "成功", Toast.LENGTH_SHORT).show();
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        Toast.makeText(getContext(), "分享失败", Toast.LENGTH_SHORT).show();
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        Toast.makeText(getContext(), "分项取消", Toast.LENGTH_SHORT).show();
                        dissmisProgressDialog();
                    }
                }).open();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginSuccessEvent event) {
        //获取用户信息
        getUserInfo();

        //获取我的行程
//        getUnfinishedOrder();
    }

    /**
     * 获取用户的个人信息
     */
    private void getUserInfo() {
        UdriveRestClient.getClentInstance().getUserInfo(SessionManager.getInstance().getAuthorization())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AccountInfoResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AccountInfoResult value) {
                        if (null == value || null == value.data) {
                            Toast.makeText(getContext(), R.string.lz_get_user_info_fail, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        AccountInfoManager.getInstance().cacheAccount(value);
                        mTvUserName.setText(value.data.username);
                        parseIdType(value.data.idCardState);
                        parseMoney(value);
                        mDiMoney.setRightText(String.format(Locale.getDefault(),"%.2f 元",(value.data.balance / 100f + value.data.giveBalance / 100f) ));

//                        mDiDeposit.setRightText(value.data.depositState == 0 ? getString(R.string.lz_un_jiaona) : getString(R.string.lz_jiaona));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), R.string.lz_get_user_info_fail, Toast.LENGTH_SHORT).show();
                        AccountInfoManager.getInstance().clearAccount();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void parseMoney(AccountInfoResult value) {
        if (value.data.depositState == 1) {
            mDiDeposit.setRightText("待缴纳");
        } else if (value.data.depositState == 2) {
            mDiDeposit.setRightText("已缴纳");
        } else if (value.data.depositState == 3) {
            mDiDeposit.setRightText("退款中");
        } else if (value.data.depositState == 4) {
            mDiDeposit.setRightText("已退款");
        } else {
            mDiDeposit.setRightText("未缴纳");
        }

    }

    private void parseIdType(int code) {
        switch (code) {
            case Constants.ID_UN_AUTHORIZED:
                mTvAccountType.setText(R.string.lz_un_authorized);
                break;
            case Constants.ID_UNDER_REVIEW:
                mTvAccountType.setText(R.string.lz_under_revier);
                break;
            case Constants.ID_AUTHORIZED_SUCCESS:
                mTvAccountType.setText(R.string.lz_authorize_success);
                break;
            case Constants.ID_AUTHORIZED_FAIL:
                mTvAccountType.setText(R.string.lz_authorized_fail);
                break;

            default:
                mTvAccountType.setText("");
                break;
        }
    }

    /**
     * 获取我的行程状态
     */
    private void getUnfinishedOrder() {


        UdriveRestClient.getClentInstance().getUnfinishedOrder(SessionManager.getInstance().getAuthorization())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UnFinishOrderResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UnFinishOrderResult value) {
                        if (value == null || value.getData() == null ) {

                        } else {
//                            mDiMyType.setRightText(getString(R.string.lz_have_no_complete_order));
//                            mDiMyType.setRightTextColor(Color.RED);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions((Activity) getContext(), mPermissionList, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getContext()).onActivityResult(requestCode, resultCode, data);
    }
}
