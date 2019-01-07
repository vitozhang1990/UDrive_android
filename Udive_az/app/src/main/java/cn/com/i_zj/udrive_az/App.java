package cn.com.i_zj.udrive_az;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.blankj.utilcode.util.Utils;
import com.bugtags.library.Bugtags;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import cn.com.i_zj.udrive_az.utils.image.ImagePipelineConfigFactory;
import cn.jpush.android.api.JPushInterface;

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
    }
}