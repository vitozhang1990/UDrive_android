package cn.com.i_zj.udrive_az.login;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.i_zj.udrive_az.MainActivity;
import cn.com.i_zj.udrive_az.R;

public class LaunchActivity extends DBSBaseActivity {

  @Override
  protected int getLayoutResource() {
    return R.layout.activity_launch;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        startActivity(MainActivity.class);
        finish();
      }
    }, 800);

    SessionManager.getInstance().refreshToken(false);
  }
}
