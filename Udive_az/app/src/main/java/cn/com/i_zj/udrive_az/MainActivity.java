package cn.com.i_zj.udrive_az;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.event.GotoLoginDialogEvent;
import cn.com.i_zj.udrive_az.login.LoginDialogFragment;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.ui.order.OrderActivity;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.ReserveActivity;
import cn.com.i_zj.udrive_az.model.GetReservation;
import cn.com.i_zj.udrive_az.model.UnFinishOrderResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.ScreenManager;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends DBSBaseActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout personalDarwLayout;

    @BindView(R.id.main_tv_tip)
    TextView tipView;
    AlertDialog unfinishedOrderDialog;

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

        Calendar c = Calendar.getInstance();//
        int month = c.get(Calendar.MONTH) + 1;// 获取当前月份
        int day = c.get(Calendar.DAY_OF_MONTH);// 获取当日期

        if (month <= 8 || (month == 9 && day < 5)) {
            tipView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScreenManager.getScreenManager().pushActivity(this);
        if (SessionManager.getInstance().getAuthorization() != null) {

            getReservation();
            getUnfinishedOrder();
        }
    }

    @OnClick(R.id.main_tv_personal_info)
    public void onPersonalInfoClick(View view) {
        personalDarwLayout.openDrawer(Gravity.START);
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
