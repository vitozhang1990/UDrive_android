package cn.com.i_zj.udrive_az;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import cn.com.i_zj.udrive_az.event.EmptyEvent;

public abstract class DBSBaseFragment extends RxFragment {

  protected ProgressDialog progressDialog;

  protected abstract int getLayoutResource();

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(getLayoutResource(), container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  public void startActivity(Class<?> cls) {
    Intent intent = new Intent(getContext(), cls);
    startActivity(intent);
  }

  public void showProgressDialog(String content) {
    if (null == progressDialog) {
      progressDialog = new ProgressDialog(getActivity());
      progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      progressDialog.setCancelable(false);
    }
    progressDialog.setMessage(content);
    if (!progressDialog.isShowing()) {
      progressDialog.show();
    }
  }
  public void showProgressDialog(String content,boolean touchCancel) {
    if (null == progressDialog) {
      progressDialog = new ProgressDialog(getActivity());
      progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      progressDialog.setMessage(content);
      progressDialog.setCancelable(touchCancel);
    }
    progressDialog.setCanceledOnTouchOutside(touchCancel);
    if (!progressDialog.isShowing()) {
      progressDialog.show();
    }
  }
  public void dissmisProgressDialog() {
    if (null != progressDialog && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  public void startActivity(Class<?> cls, int requestCode) {
    Intent intent = new Intent(getContext(), cls);
    startActivityForResult(intent, requestCode);
  }

  public void showToast(String content) {
    Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
  }

  public void showToast(int resId) {
    showToast(getString(resId));
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
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.getDefault().register(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEvent(EmptyEvent emptyEvent) {
  }
}
