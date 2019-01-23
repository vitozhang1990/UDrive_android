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
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.event.GotoLoginDialogEvent;
import cn.com.i_zj.udrive_az.event.NetWorkEvent;
import cn.com.i_zj.udrive_az.login.LoginDialogFragment;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.ui.msg.ActMsg;
import cn.com.i_zj.udrive_az.lz.ui.order.OrderActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.TravelingActivity;
import cn.com.i_zj.udrive_az.map.WaitingActivity;
import cn.com.i_zj.udrive_az.model.ActivityInfo;
import cn.com.i_zj.udrive_az.model.AppversionEntity;
import cn.com.i_zj.udrive_az.model.GetReservation;
import cn.com.i_zj.udrive_az.model.HomeActivityEntity;
import cn.com.i_zj.udrive_az.model.UnFinishOrderResult;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
import cn.com.i_zj.udrive_az.model.ret.RetAppversionObj;
import cn.com.i_zj.udrive_az.network.UObserver;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.service.BackService;
import cn.com.i_zj.udrive_az.utils.AppDownloadManager;
import cn.com.i_zj.udrive_az.utils.DownloadApk;
import cn.com.i_zj.udrive_az.utils.ScreenManager;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.utils.dialog.AppUpdateDialog;
import cn.com.i_zj.udrive_az.utils.dialog.HomeAdvDialog;
import cn.com.i_zj.udrive_az.web.WebActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends DBSBaseActivity implements EasyPermissions.PermissionCallbacks {
    @BindView(R.id.drawer_layout)
    DrawerLayout personalDrawerLayout;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.rl_note)
    CardView rlNote;

    private AlertDialog unfinishedOrderDialog;
    private AppUpdateDialog appUpdateDialog;
    private HomeAdvDialog homeAdvDialog;
    private ActivityInfo homeNote;

    private long time = 0;
    private boolean hasRequest; //网络变化后只请求一次
    private UnFinishOrderResult unFinishOrderResult;

    @Override
    protected int getLayoutResource() {
        MapUtils.statusBarColor(this);
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenManager.getScreenManager().pushActivity(MainActivity.this);
        checkPermission();
        versionCheck();
        getActivity();

        if (SessionManager.getInstance().getAuthorization() != null) {
            getUnfinishedOrder();
        }
        startService(new Intent(this, BackService.class));
    }

    private void checkPermission() {
        boolean external = EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA);

        if (!external) {
            EasyPermissions.requestPermissions(this, getString(R.string.lz_request_permission), 1, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SessionManager.getInstance().getAuthorization() == null) {
            return;
        }
        if (hasRequest) {
            getUnfinishedOrder1();
        }
        getReservation();
    }

    @OnClick({R.id.main_tv_msg, R.id.rl_note, R.id.main_tv_personal_info})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_tv_msg:
                startActivity(ActMsg.class);
                break;
            case R.id.rl_note:
                if (homeNote != null) {
                    WebActivity.startWebActivity(MainActivity.this, homeNote.getHref(), homeNote.getTitle());
                } else if (unFinishOrderResult != null) {
                    startActivity(TravelingActivity.class);
                }
                break;
            case R.id.main_tv_personal_info:
                personalDrawerLayout.openDrawer(Gravity.START);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GotoLoginDialogEvent loginEvent) {
        if (personalDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            personalDrawerLayout.closeDrawer(GravityCompat.START);
        }
        LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
        loginDialogFragment.show(getSupportFragmentManager(), "login");
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(NetWorkEvent netWorkEvent) {
        if (!hasRequest) {
            if (SessionManager.getInstance().getAuthorization() != null) {
                hasRequest = true;
                getReservation();
            }
        }
    }

    private void getReservation() {
        UdriveRestClient.getClentInstance().getReservation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetReservation>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(GetReservation result) {
                        if (result != null) {
                            hasRequest = true;
                            if (result.getCode() == 1) {
                                // 0没有 1 有
                                if (result.getData().getOrderType() == 0 && result.getData().getReservationId() > 0) {
                                    long time = System.currentTimeMillis() - result.getData().getCreateTime();//
                                    if (time < 1000 * 60 * 15) {
                                        if (homeAdvDialog != null && homeAdvDialog.isShowing()) {
                                            homeAdvDialog.dismiss();
                                            homeAdvDialog = null;
                                        }
                                        Intent intent = new Intent(MainActivity.this, WaitingActivity.class);
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

    private void getUnfinishedOrder1() {
        if (homeNote != null) {
            return;
        }
        UdriveRestClient.getClentInstance().getUnfinishedOrder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UnFinishOrderResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UnFinishOrderResult result) {
                        if (result == null || result.getCode() != 1) {
                            return;
                        }
                        if (result.getData() != null && result.getData().getId() > 0) {
                            rlNote.setVisibility(View.VISIBLE);
                            tvMsg.setText("您有一个订单正在进行中，点击进入");
                            unFinishOrderResult = result;
                        } else {
                            rlNote.setVisibility(View.GONE);
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
        UdriveRestClient.getClentInstance().getUnfinishedOrder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UnFinishOrderResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UnFinishOrderResult result) {
                        if (result != null) {
                            hasRequest = true;
                            if (result.getCode() == 1) {
                                if (result.getData() != null && result.getData().getId() > 0) {
                                    if (result.getData().getStatus() == 0) {//行程中
                                        if (homeAdvDialog != null && homeAdvDialog.isShowing()) {
                                            homeAdvDialog.dismiss();
                                            homeAdvDialog = null;
                                        }
                                        startActivity(TravelingActivity.class);
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
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void versionCheck() {
        try {
            String version = ToolsUtils.getVersionName(MainActivity.this);
            UdriveRestClient.getClentInstance().appversionCheck(version)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<RetAppversionObj>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(RetAppversionObj result) {
                            if (result != null && result.getCode() == 1) {
                                if (result.getData() != null) {// 有更新
                                    showUpdateAppDialog(result.getData());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getActivity() {
        UdriveRestClient.getClentInstance().activity()
                .delay(500, TimeUnit.MILLISECONDS)
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
                                if (homeAdvDialog == null) {
                                    homeAdvDialog = new HomeAdvDialog(MainActivity.this);
                                }
                                homeAdvDialog.setData(homeActivityEntity.getActivitys());
                                if (!homeAdvDialog.isShowing()) {
                                    homeAdvDialog.show();
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
                        }
                    }).setCancelable(false)
                    .create();
        }
        if (!unfinishedOrderDialog.isShowing()) {
            unfinishedOrderDialog.show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        ToastUtils.showShort(R.string.permission_request_fail);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == 103) {
            if (SessionManager.getInstance().getAuthorization() != null) {
                getReservation();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (personalDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            personalDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            stopService(new Intent(MainActivity.this, BackService.class));
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - time > 1000) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                time = System.currentTimeMillis();
            } else {
                stopService(new Intent(MainActivity.this, BackService.class));

                ScreenManager.getScreenManager().popAllActivityExceptOne();
                System.exit(0);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
