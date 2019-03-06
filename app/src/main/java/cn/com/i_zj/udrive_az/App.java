package cn.com.i_zj.udrive_az;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.baidu.ocr.ui.camera.CameraView;
import com.blankj.utilcode.util.Utils;
import com.bugtags.library.Bugtags;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;
import cn.com.i_zj.udrive_az.utils.image.ImagePipelineConfigFactory;
import cn.jpush.android.api.JPushInterface;
import me.yokeyword.fragmentation.Fragmentation;

/**
 * Created by wli on 2018/8/11.
 */

public class App extends MultiDexApplication {

    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        Bugtags.start("d6aeaf5636259046f115c36522316c61", this, Bugtags.BTGInvocationEventNone);
        Utils.init(this);
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, "5b814289b27b0a7e080000b4"
                , "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
        Fresco.initialize(this, ImagePipelineConfigFactory.getOkHttpImagePipelineConfig(this));
//        PlatformConfig.setWeixin("wx573f46942b7cffbf", "2cae5e3a7044b8185faa2c37e37e27b3");
        PlatformConfig.setWeixin("wx573f46942b7cffbf", "2cae5e3a7044b8185faa2c37e37e27b3");
//        PlatformConfig.setWeixin("wxb94b5f6267204d83", "812f019cfa82d289cf38a1f1a8f3c323");
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);

        Fragmentation.builder()
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(BuildConfig.DEBUG)
                .handleException(e -> {
                })
                .install();

        initAccessToken();
    }

    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                initLicense();
                LocalCacheUtils.savePersistentSettingString(Constants.SP_GLOBAL_NAME, Constants.SP_Access_Token, new Gson().toJson(result));
            }
            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                LocalCacheUtils.removePersistentSetting(Constants.SP_GLOBAL_NAME, Constants.SP_Access_Token);
            }
        }, getApplicationContext());
    }

    private void initLicense() {
        CameraNativeHelper.init(this, OCR.getInstance(this).getLicense(),
                (errorCode, e) -> {
                    final String msg;
                    switch (errorCode) {
                        case CameraView.NATIVE_SOLOAD_FAIL:
                            msg = "加载so失败，请确保apk中存在ui部分的so";
                            break;
                        case CameraView.NATIVE_AUTH_FAIL:
                            msg = "授权本地质量控制token获取失败";
                            break;
                        case CameraView.NATIVE_INIT_FAIL:
                            msg = "本地质量控制";
                            break;
                        default:
                            msg = String.valueOf(errorCode);
                    }
                });
    }
}