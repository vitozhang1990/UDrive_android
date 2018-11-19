package cn.com.i_zj.udrive_az.web;

import android.graphics.Bitmap;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author JayQiu
 * @create 2018/11/19
 * @Describe
 */
public class UWebViewClient extends WebViewClient {
    private WebStatusListener webStatusListener;

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        String title = view.getTitle();

        if (webStatusListener != null) {
            webStatusListener.start();
        }
        super.onPageStarted(view, url, favicon);

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        String title = view.getTitle();
        if (webStatusListener != null) {
            webStatusListener.loadFinish(title);
        }
    }

    public void setWebStatusListener(WebStatusListener webStatusListener) {
        this.webStatusListener = webStatusListener;
    }

    public interface WebStatusListener {

        void start();

        void loadFinish(String title);

    }
}
