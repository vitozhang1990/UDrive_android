package cn.com.i_zj.udrive_az.utils.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Window;

import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.web.WebActivity;

public class RechargeDialogActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_recharge);
        setFinishOnTouchOutside(false);

        findViewById(R.id.sure).setOnClickListener(v -> {
            WebActivity.startWebActivity(this, BuildConfig.WEB_URL + "/wallet/recharge/0");
            finish();
        });

        findViewById(R.id.cancel).setOnClickListener(v -> finish());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
