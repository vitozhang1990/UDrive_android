package cn.com.i_zj.udrive_az.lz.ui.drawerleft;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.DBSBaseFragment;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.GotoLoginDialogEvent;
import cn.com.i_zj.udrive_az.event.LoginSuccessEvent;
import cn.com.i_zj.udrive_az.event.OrderFinishEvent;
import cn.com.i_zj.udrive_az.event.PayFinishEvent;
import cn.com.i_zj.udrive_az.login.AccountInfoManager;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.ui.about.AboutActivity;
import cn.com.i_zj.udrive_az.lz.ui.accountinfo.AccountInfoActivity;
import cn.com.i_zj.udrive_az.lz.ui.deposit.DepositActivity;
import cn.com.i_zj.udrive_az.lz.ui.order.OrderActivity;
import cn.com.i_zj.udrive_az.lz.ui.violation.ViolationActivity;
import cn.com.i_zj.udrive_az.lz.view.DrawerItemView;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.ActivityResult;
import cn.com.i_zj.udrive_az.model.UnFinishOrderResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import cn.com.i_zj.udrive_az.web.WebActivity;
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

    private AccountInfoResult accountInfo;
    private ActivityResult activityResultInfo;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_drawer_left;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppView();
    }

    @Override
    public void onResume() {
        super.onResume();
        dissmisProgressDialog();
        if (SessionManager.getInstance().isLogin()) {
            //获取用户信息
            getUserInfo();

            getUnfinishedOrder();
        }
        AccountInfoResult accountInfo = AccountInfoManager.getInstance().getAccountInfo();
        if (accountInfo != null) {
            if (accountInfo.data != null && !StringUtils.isEmpty(accountInfo.data.username) && accountInfo.data.username.length() >= 11) {
                StringBuilder sb = new StringBuilder(accountInfo.data.username);
                sb.replace(3, 7, "****");
                mTvUserName.setText(sb.toString());
            }
            parseIdType(accountInfo.data.idCardState);
            mDiMoney.setRightText(String.format(Locale.getDefault(), "%.2f 元", (accountInfo.data.balance / 100f + accountInfo.data.giveBalance / 100f)));
            parseMoney(accountInfo);
        } else {
            mTvUserName.setText("未登录");
            mTvAccountType.setText("未认证用户");
            mDiMoney.setRightText("");
            mDiDeposit.setRightText("");
        }
    }

    @OnClick({R.id.rl_head, R.id.di_my_type, R.id.di_money, R.id.di_deposit, R.id.di_violation, R.id.di_about, R.id.share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_head:  //我的信息
                if (SessionManager.getInstance().isLogin()) {
                    startActivity(AccountInfoActivity.class);
                } else {
                    EventBus.getDefault().post(new GotoLoginDialogEvent(GotoLoginDialogEvent.NextJump.ACCOUNT_INFO_ACTIVITY));
                }
                break;
            case R.id.di_my_type: //我的订单
                if (SessionManager.getInstance().isLogin()) {
                    startActivity(OrderActivity.class);
                } else {
                    EventBus.getDefault().post(new GotoLoginDialogEvent(GotoLoginDialogEvent.NextJump.ORDER_ACTIVITY));
                }
                break;
            case R.id.di_money:  //我的钱包
                if (SessionManager.getInstance().isLogin()) {
                    WebActivity.startWebActivity(getActivity(), BuildConfig.WEB_URL + "/wallet");
                } else {
                    EventBus.getDefault().post(new GotoLoginDialogEvent(GotoLoginDialogEvent.NextJump.MONEY_ACTIVITY));
                }
                break;
            case R.id.di_deposit: //我的押金
                if (SessionManager.getInstance().isLogin()) {
                    startActivity(DepositActivity.class);
                } else {
                    EventBus.getDefault().post(new GotoLoginDialogEvent(GotoLoginDialogEvent.NextJump.MONEY_ACTIVITY));
                }
                break;
            case R.id.di_violation:
                if (SessionManager.getInstance().isLogin()) {
                    startActivity(ViolationActivity.class);
                } else {
                    EventBus.getDefault().post(new GotoLoginDialogEvent(GotoLoginDialogEvent.NextJump.MONEY_ACTIVITY));
                }
                break;
            case R.id.di_about: //关于
                startActivity(AboutActivity.class);
                break;
            case R.id.share: //底部分享
                if (activityResultInfo != null) {
                    WebActivity.startWebActivity(getActivity(), activityResultInfo.getData().get(0).getViewUrl());
                }
                break;
        }

    }

    private void getAppView() {
        UdriveRestClient.getClentInstance().getAppView()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ActivityResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ActivityResult activityResult) {
                        if (activityResult == null) {
                            return;
                        }
                        if (activityResult.getCode() != 1) {
                            ToastUtils.showShort(activityResult.getMessage());
                            return;
                        }
                        if (activityResult.getData() != null && activityResult.getData().size() > 0) {
                            activityResultInfo = activityResult;
                            mDbShare.setVisibility(View.VISIBLE);
                            Glide.with(getActivity()).load(activityResult.getData().get(0).getImgUrl()).into(mDbShare);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginSuccessEvent event) {
        //获取用户信息
        getUserInfo();

        //获取我的行程
        getUnfinishedOrder();
    }

    /**
     * 获取用户的个人信息
     */
    private void getUserInfo() {
        UdriveRestClient.getClentInstance().getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AccountInfoResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AccountInfoResult value) {
                        if (null == value || null == value.data) {
//                            ToastUtils.showShort(R.string.lz_get_user_info_fail);
                            return;
                        }
                        accountInfo = value;
                        AccountInfoManager.getInstance().cacheAccount(value);
                        if (value.data != null && !StringUtils.isEmpty(value.data.username) && value.data.username.length() >= 11) {
                            StringBuilder sb = new StringBuilder(value.data.username);
                            sb.replace(3, 7, "****");
                            mTvUserName.setText(sb.toString());
                        }

                        parseIdType(value.data.idCardState);
                        parseMoney(value);
                        double balance = (value.data.balance + value.data.giveBalance) / (double) 100;
                        mDiMoney.setRightText(String.format(Locale.getDefault(), "%.2f 元", balance));

//                        mDiDeposit.setRightText(value.data.depositState == 0 ? getString(R.string.lz_un_jiaona) : getString(R.string.lz_jiaona));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
//                        AccountInfoManager.getInstance().clearAccount();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OrderFinishEvent event) {
        if (event.isAbc()) {
            mDiMyType.setRightText(getString(R.string.lz_have_no_complete_order));
        } else {
            mDiMyType.setRightText("");
            getUnfinishedOrder();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PayFinishEvent event) {
        mDiMyType.setRightText("");
    }

    /**
     * 获取我的行程状态
     */
    private void getUnfinishedOrder() {
        UdriveRestClient.getClentInstance().getUnfinishedOrder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UnFinishOrderResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UnFinishOrderResult value) {
                        if (mDiMyType == null) {
                            return;
                        }
                        if (value == null || value.getCode() != 1
                                || value.getData() == null) {
                            return;
                        }
                        if (value.getData().getId() > 0 && value.getData().getStatus() != null) {
                            switch (value.getData().getStatus()) {
                                case Constants.ORDER_MOVE:
                                    mDiMyType.setRightText(getString(R.string.lz_have_no_complete_order));
                                    break;
                                case Constants.ORDER_WAIT_PAY:
                                    mDiMyType.setRightText(getString(R.string.order_wait_pay));
                                    break;
                                default:
                                    mDiMyType.setRightText("");
                            }
                        } else {
                            mDiMyType.setRightText("");
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
