package cn.com.i_zj.udrive_az;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.event.GotoLoginDialogEvent;
import cn.com.i_zj.udrive_az.login.LoginDialogFragment;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.ui.msg.ActMsg;
import cn.com.i_zj.udrive_az.lz.ui.order.OrderActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.ReserveActivity;
import cn.com.i_zj.udrive_az.model.ActivityInfo;
import cn.com.i_zj.udrive_az.model.AppversionEntity;
import cn.com.i_zj.udrive_az.model.GetReservation;
import cn.com.i_zj.udrive_az.model.HomeActivityEntity;
import cn.com.i_zj.udrive_az.model.UnFinishOrderResult;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
import cn.com.i_zj.udrive_az.model.ret.RetAppversionObj;
import cn.com.i_zj.udrive_az.network.UObserver;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.AppDownloadManager;
import cn.com.i_zj.udrive_az.utils.DownloadApk;
import cn.com.i_zj.udrive_az.utils.ScreenManager;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.dialog.AppUpdateDialog;
import cn.com.i_zj.udrive_az.utils.dialog.HomeAdvDiaog;
import cn.com.i_zj.udrive_az.web.WebActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends DBSBaseActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.drawer_layout)
    DrawerLayout personalDarwLayout;

    @BindView(R.id.main_tv_tip)
    TextView tipView;
    AlertDialog unfinishedOrderDialog;
    AppUpdateDialog appUpdateDialog;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.rl_note)
    RelativeLayout rlNote;

    private ActivityInfo homeNote;
    private HomeAdvDiaog homeAdvDilog;

    private boolean isFirst = true;

    @Override
    protected int getLayoutResource() {
        MapUtils.setStatusBar(this);
        return R.layout.activity_main;
    }

    @Override
    public void onBackPressed() {
        if (personalDarwLayout.isDrawerOpen(GravityCompat.START)) {
            personalDarwLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenManager.getScreenManager().pushActivity(MainActivity.this);
        Calendar c = Calendar.getInstance();//
        int month = c.get(Calendar.MONTH) + 1;// 获取当前月份
        int day = c.get(Calendar.DAY_OF_MONTH);// 获取当日期

        if (month <= 8 || (month == 9 && day < 5)) {
            tipView.setVisibility(View.VISIBLE);
        }
        checkPermission();
    }

    /**
     * 检测权限
     */
    private void checkPermission() {
        boolean external = EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA);

        if (!external) {
            EasyPermissions.requestPermissions(this, getString(R.string.lz_request_permission), 1, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (perms.size() > 0) {
            ToastUtils.showShort(R.string.permission_success);
        } else {
            ToastUtils.showShort(R.string.permission_file);
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ToastUtils.showShort(R.string.permission_request_fail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appversionCheck();
    }

    @OnClick(R.id.main_tv_personal_info)
    public void onPersonalInfoClick(View view) {
        personalDarwLayout.openDrawer(Gravity.START);
    }

    @OnClick({R.id.main_tv_msg, R.id.rl_note})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_tv_msg:
                startActivity(ActMsg.class);
                break;
            case R.id.rl_note:
                if (homeNote != null) {
                    WebActivity.startWebActivity(MainActivity.this, homeNote.getHref(), homeNote.getTitle());
                }
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GotoLoginDialogEvent loginEvent) {
        if (personalDarwLayout.isDrawerOpen(GravityCompat.START)) {
            personalDarwLayout.closeDrawer(GravityCompat.START);
        }
        LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
        loginDialogFragment.show(getSupportFragmentManager(), "login");
    }


    /**
     * 获取预约状态
     */
    private void getReservation() {
        UdriveRestClient.getClentInstance().getReservation(SessionManager.getInstance().getAuthorization())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetReservation>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(GetReservation result) {
                        LogUtils.e("11");
                        if (result != null) {
                            LogUtils.e("22");
                            if (result.getCode() == 1) {
                                LogUtils.e("333");
                                // 0没有 1 有
                                if (result.getData().getOrderType() == 0 && result.getData().getReservationId() > 0) {
                                    long time = System.currentTimeMillis() - result.getData().getCreateTime();//
                                    if (time < 1000 * 60 * 15) {
                                        LogUtils.e("44");
//                                    getUnfinishedOrder(result);
                                        Intent intent = new Intent(MainActivity.this, ReserveActivity.class);
                                        intent.putExtra("type", "2");
                                        intent.putExtra("bunld", result);
                                        intent.putExtra("id", result.getData().getReservationId() + "");
                                        startActivity(intent);
                                    }

                                }
                            }
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

    private void getUnfinishedOrder() {
        UdriveRestClient.getClentInstance().getUnfinishedOrder(SessionManager.getInstance().getAuthorization())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UnFinishOrderResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UnFinishOrderResult result) {
                        LogUtils.e("55");
                        if (result != null) {
                            LogUtils.e("66");
                            if (result.getCode() == 1) {
                                LogUtils.e("77");
                                if (result.getData() != null && result.getData().getId() > 0) {
                                    if (result.getData().getStatus() == 0) {//行程中
                                        LogUtils.e("88");
                                        Intent intent = new Intent(MainActivity.this, ReserveActivity.class);
                                        intent.putExtra("type", "3");
                                        intent.putExtra("bunld", result);
                                        startActivity(intent);
                                    } else if (result.getData().getStatus() == 1) {
                                        showUnfinishedOrderDialog();
                                    }
                                }
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        LogUtils.e("==============>" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void appversionCheck() {
        String version = "";
        try {
            version = ToolsUtils.getVersionName(MainActivity.this);
        } catch (Exception e) {

        }

        UdriveRestClient.getClentInstance().appversionCheck(version)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetAppversionObj>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(RetAppversionObj result) {
                        LogUtils.e("55");
                        if (result != null && result.getCode() == 1) {
                            if (result.getData() != null) {// 有更新
                                showUpdateAppDialog(result.getData());
                            } else {
                                if (SessionManager.getInstance().getAuthorization() != null) {
                                    getReservation();
                                    getUnfinishedOrder();
                                }

                                if (isFirst) {
                                    isFirst = false;
                                    getActivity();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        LogUtils.e("==============>" + e.getMessage());
                        if (isFirst) {
                            isFirst = false;
                            getActivity();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getActivity() {

        UdriveRestClient.getClentInstance().activity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<BaseRetObj<HomeActivityEntity>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new UObserver<HomeActivityEntity>() {
                    @Override
                    public void onSuccess(HomeActivityEntity homeActivityEntity) {

                        if (homeActivityEntity != null) {
                            homeNote = homeActivityEntity.getNote();
                            if (homeNote != null) {
                                rlNote.setVisibility(View.VISIBLE);
                                tvMsg.setText(homeNote.getTitle());
                            } else {
                                rlNote.setVisibility(View.GONE);
                            }
                            if (!StringUtils.isEmpty(homeActivityEntity.getActivitys())) {
                                if (homeAdvDilog == null) {
                                    homeAdvDilog = new HomeAdvDiaog(MainActivity.this);
                                }
                                homeAdvDilog.setData(homeActivityEntity.getActivitys());
                                if (!homeAdvDilog.isShowing()) {
                                    homeAdvDilog.show();
                                }
                            }

                        } else {
                            if (rlNote.getVisibility() == View.VISIBLE) {
                                rlNote.setVisibility(View.GONE);
                                homeNote = null;
                            }
                        }


                    }

                    @Override
                    public void onException(int code, String message) {
//                        showToast(message);
                    }

                    @Override
                    public void onFinish() {

                    }
                });
    }

    private void showUpdateAppDialog(final AppversionEntity appversionEntity) {
        if (appUpdateDialog == null) {
            appUpdateDialog = new AppUpdateDialog(MainActivity.this);
        }
        appUpdateDialog.setAppversion(appversionEntity);
        appUpdateDialog.setOnClickListener(new AppUpdateDialog.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_canel:
                        appUpdateDialog.dismiss();
                        break;
                    case R.id.tv_ok:
                        appUpdateDialog.dismiss();

                        if (appversionEntity.getState() == 1) {
                            DownloadApk downloadApk = new DownloadApk(MainActivity.this);
                            downloadApk.downloadApk(appversionEntity.getAppUrl());
                        } else {
                            AppDownloadManager appDownloadManager = new AppDownloadManager();
                            appDownloadManager.downloadManager(MainActivity.this, appversionEntity.getAppUrl());
                        }
                        break;
                }
            }
        });
        if (!appUpdateDialog.isShowing()) {
            appUpdateDialog.show();
        }

    }

    private void showUnfinishedOrderDialog() {
        if (unfinishedOrderDialog == null) {
            unfinishedOrderDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("通知")
                    .setMessage("您有未付款的订单")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("去付款", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                            startActivityForResult(intent, 103);
                            LogUtils.e("111");
                        }
                    }).setCancelable(false)
                    .create();
        }
        if (!unfinishedOrderDialog.isShowing()) {
            unfinishedOrderDialog.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == 103) {
            if (SessionManager.getInstance().getAuthorization() != null) {
                LogUtils.e("0.1");
                getReservation();
            }
        }
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ScreenManager.getScreenManager().popAllActivityExceptOne();
//            moveTaskToBack(true);
            System.exit(0);
        }
        return false;
    }


}
