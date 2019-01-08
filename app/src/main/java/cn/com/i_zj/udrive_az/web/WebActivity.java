package cn.com.i_zj.udrive_az.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

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
    @BindView(R.id.tv_title)
    TextView tv_title;
    private Disposable disposable;
    private UWebChromeClient uWebChromeClient;
    private UWebViewClient uWebViewClient;
    private String url;
    private String title;
    private int index = 10;

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
        MapUtils.statusBarColor(this);
        title = getIntent().getStringExtra("title");
        tv_title.setText(title);
        url = getIntent().getStringExtra("url");
        if (!StringUtils.isEmpty(url)) {
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
        }
        initView();
    }

    @OnClick(R.id.iv_back)
    void back(View view) {
        finish();
    }

    private void initView() {
        progressBar.setProgress(0);
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

                nextPor(progress);

            }
        });
    }

    @Override
    public void start() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadFinish(String titleStr) {
        nextPor(100);
        if (StringUtils.isEmpty(title)) {
            tv_title.setText(titleStr);
        }
    }

    private void nextPor(final int progress) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        Observable.interval(0, 10, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Object o) {
                        int oldPro = progressBar.getProgress();
                        Log.e("====>", oldPro + "============");
                        if (oldPro <= progress) {
                            oldPro = oldPro + 2;
                            progressBar.setProgress(oldPro);
                        }
                        if (oldPro >= 100) {
                            progressBar.setVisibility(View.GONE);
                            disposable.dispose();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
