package cn.com.i_zj.udrive_az.web;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * @author JayQiu
 * @create 2018/11/19
 * @Describe
 */
public class UWebChromeClient extends WebChromeClient {
    private  onProgressChanged onProgressChanged;
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if(onProgressChanged!=null){
            onProgressChanged.onProgressChanged(newProgress);
        }
    }

    public void setOnProgressChanged(UWebChromeClient.onProgressChanged onProgressChanged) {
        this.onProgressChanged = onProgressChanged;
    }

    public  interface  onProgressChanged{
        void onProgressChanged(int progress);
    }
}
