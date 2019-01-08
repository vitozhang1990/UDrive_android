package cn.com.i_zj.udrive_az.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import butterknife.BindView;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;

/**
 * 用户协议
 */
public class ProtocolActivity extends DBSBaseActivity {



  @BindView(R.id.commonWebview)
  WebView webView;

  @Override
  protected int getLayoutResource() {
    return R.layout.activity_protocol;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    webView.loadUrl("file:///android_asset/udrive_protocol.html");
  }
}
