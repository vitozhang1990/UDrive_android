package cn.com.i_zj.udrive_az;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
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
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.map.ReserveActivity;
import cn.com.i_zj.udrive_az.model.GetReservation;
import cn.com.i_zj.udrive_az.model.UnFinishOrderResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends DBSBaseActivity {

  @BindView(R.id.drawer_layout)
  DrawerLayout personalDarwLayout;

  @BindView(R.id.main_tv_tip)
  TextView tipView;

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
    if(SessionManager.getInstance().getAuthorization()!=null){
        getReservation();
    }
    Calendar c = Calendar.getInstance();//
    int month = c.get(Calendar.MONTH) + 1;// 获取当前月份
    int day = c.get(Calendar.DAY_OF_MONTH);// 获取当日期

    if (month <= 8 || (month == 9 && day < 5)) {
      tipView.setVisibility(View.VISIBLE);
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
  }
  private void getReservation(){
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
                if(result!=null){
                    LogUtils.e("22");
                  if(result.getCode()==1){
                      LogUtils.e("333");
                      if(result.getData().getOrderType()==1){
                          LogUtils.e("44");
                        getUnfinishedOrder(result);

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
  private void getUnfinishedOrder(final GetReservation bean){
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
                if(result!=null){
                    LogUtils.e("66");
                  if(result.getCode()==1){
                      LogUtils.e("77");
                    if(!result.getData().equals("")&&result.getData()!=null){
                        if(result.getData().getStatus()==0){
                            LogUtils.e("88");
                          Intent intent=new Intent(MainActivity.this,ReserveActivity.class);
                          intent.putExtra("type","3");
                          intent.putExtra("bunld",result);
                          startActivity(intent);
                        }else {
                            LogUtils.e("99");
                          Intent intent=new Intent(MainActivity.this,ReserveActivity.class);
                          intent.putExtra("type","2");
                          intent.putExtra("bunld",bean);
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
}
