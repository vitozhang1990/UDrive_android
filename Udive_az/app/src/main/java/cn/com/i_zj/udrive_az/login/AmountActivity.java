package cn.com.i_zj.udrive_az.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;

/**
 * 费用说明
 */
public class AmountActivity extends DBSBaseActivity {
    @BindView(R.id.commonWebview)
    WebView webView;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_protocol;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        webView.setWebViewClient(new WebViewController());
        webView.loadUrl("http://zzbcjj.com/costdetail");
    }

    public class WebViewController extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
