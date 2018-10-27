package cn.com.i_zj.udrive_az.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;

/**
 * @author JayQiu
 * @create 2018/10/26
 * @Describe
 */
public class WebActivity extends DBSBaseActivity {
    @BindView(R.id.commonWebview)
    WebView webView;
    public  static  void startWebActivity(Context context ,String url,String title){
        Intent intent= new Intent(context,WebActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("title",title);
        context.startActivity(intent);
    }
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_web;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("title"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                finish();
            }
        });
        webView.loadUrl(getIntent().getStringExtra("url"));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        //设置 缓存模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
}
