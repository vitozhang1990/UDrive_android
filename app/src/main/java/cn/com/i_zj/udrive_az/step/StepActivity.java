package cn.com.i_zj.udrive_az.step;

import android.os.Bundle;
import android.text.TextUtils;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.baidu.ocr.ui.camera.CameraView;

import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.step.fragment.DetectionFragment;
import cn.com.i_zj.udrive_az.step.fragment.IdCardFragment;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;
import me.yokeyword.fragmentation.ISupportFragment;

public class StepActivity extends DBSBaseActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_step;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        initAccessTokenWithAkSk();
        ISupportFragment firstFragment = findFragment(IdCardFragment.class);
        if (firstFragment == null) {
            loadRootFragment(R.id.fl_container, IdCardFragment.newInstance());
        }
    }

    private void initAccessTokenWithAkSk() {
        String token = LocalCacheUtils.getPersistentSettingString(Constants.SP_GLOBAL_NAME, Constants.SP_Access_Token, "");
        if (!TextUtils.isEmpty(token)) {
            runOnUiThread(() -> showToast("已经认证过了哈"));
            return;
        }
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                initLicense();
                runOnUiThread(() -> showToast("初始化认证成功"));
            }
            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                runOnUiThread(() -> showToast("初始化认证失败,请检查 key"));
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
                    runOnUiThread(() -> showToast("本地质量控制初始化错误，错误原因： " + msg));
                });
    }
}
