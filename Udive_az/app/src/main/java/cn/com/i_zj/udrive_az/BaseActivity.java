package cn.com.i_zj.udrive_az;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.bugtags.library.Bugtags;

public class BaseActivity extends AppCompatActivity {

  @Override
  protected void onResume() {
    super.onResume();
    Bugtags.onResume(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    Bugtags.onPause(this);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    Bugtags.onDispatchTouchEvent(this, event);
    return super.dispatchTouchEvent(event);
  }

}
