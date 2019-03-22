package cn.com.i_zj.udrive_az;

import android.view.MotionEvent;

import com.bugtags.library.Bugtags;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

public class BaseActivity extends RxAppCompatActivity {
//  public  final PublishSubject<ActivityLifeC>
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
