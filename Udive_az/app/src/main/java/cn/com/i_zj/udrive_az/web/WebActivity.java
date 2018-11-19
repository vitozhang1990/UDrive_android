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
import android.widget.ProgressBar;
import android.widget.SeekBar;

import butterknife.BindView;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.utils.StringUtils;

/**
 * @author JayQiu
 * @create 2018/10/26
 * @Describe
 */
public class WebActivity extends DBSBaseActivity implements UWebViewClient.WebStatusListener {
    @BindView(R.id.commonWebview)
    WebView webView;
    @BindView(R.id.progress_bar)
    SeekBar progressBar;

    private UWebChromeClient uWebChromeClient;
    private UWebViewClient uWebViewClient;
    private String url;
    private String title;
    Toolbar toolbar;
    private  int index=10;

    public static void startWebActivity(Context context, String url, String title) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_web;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = findViewById(R.id.toolbar);
        title = getIntent().getStringExtra("title");
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                finish();
            }
        });
        url = getIntent().getStringExtra("url");
        if (!StringUtils.isEmpty(url)) {
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
        }
        initView();
    }

    private void initView() {
        uWebChromeClient = new UWebChromeClient();
        uWebViewClient = new UWebViewClient();
        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        //设置 缓存模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        //开启 database storage API 功能
        webView.getSettings().setDatabaseEnabled(true);
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        //开启 Application Caches 功能
        webView.getSettings().setAppCacheEnabled(true);
        webView.setWebViewClient(uWebViewClient);
        webView.setWebChromeClient(uWebChromeClient);
        uWebViewClient.setWebStatusListener(this);

        uWebChromeClient.setOnProgressChanged(new UWebChromeClient.onProgressChanged() {
            @Override
            public void onProgressChanged(int progress) {
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setProgress(progress);
                }
            }
        });
    }

    @Override
    public void start() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadFinish(String titleStr) {
        progressBar.setVisibility(View.GONE);
        if(StringUtils.isEmpty(title)){
            toolbar.setTitle(titleStr);
        }
    }
}
