package cn.com.i_zj.udrive_az;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import cn.com.i_zj.udrive_az.event.EmptyEvent;
import cn.com.i_zj.udrive_az.event.GotoLoginDialogEvent1;
import cn.com.i_zj.udrive_az.login.LoginDialogFragment;

/**
 * Created by wli on 2018/8/12.
 */

public abstract class DBSBaseActivity extends BaseActivity {

    protected Dialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(getLayoutResource());
        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    protected abstract int getLayoutResource();

    public void showProgressDialog() {
        showProgressDialog(false);
    }

    public void showProgressDialog(boolean touchCancel) {
        if (null == progressDialog) {
            progressDialog = new Dialog(this, R.style.MyDialog);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.setCancelable(touchCancel);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dissmisProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int res) {
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
    }

    public void startActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    public void startActivityForResult(Class<?> activity, int requestCode) {
        Intent intent = new Intent(this, activity);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
//    Bugtags.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//    Bugtags.onPause(this);
    }

    LoginDialogFragment loginDialogFragment;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GotoLoginDialogEvent1 loginEvent) {
        if (loginDialogFragment == null) {
            loginDialogFragment = new LoginDialogFragment();
            loginDialogFragment.show(getSupportFragmentManager(), "login");
        }
    }

    public boolean checkEmpty(EditText editText, String errorContent) {
        final String content = editText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            showToast(errorContent);
            return false;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EmptyEvent emptyEvent) {
    }
}
