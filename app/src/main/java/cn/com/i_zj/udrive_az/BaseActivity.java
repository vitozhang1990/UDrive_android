package cn.com.i_zj.udrive_az;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.bugtags.library.Bugtags;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import cn.com.i_zj.udrive_az.utils.SystemUtils;

public class BaseActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUtils.setScreenVertical(this);
    }

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
