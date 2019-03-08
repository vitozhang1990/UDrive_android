package cn.com.i_zj.udrive_az.step;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.baidu.ocr.ui.camera.CameraView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.StepEvent;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.step.fragment.DetectionFragment;
import cn.com.i_zj.udrive_az.step.fragment.IdCardFragment;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;

public class StepIdCardActivity extends DBSBaseActivity {

    @BindView(R.id.layout1)
    View layout1;
    @BindView(R.id.layout2)
    View layout2;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_step_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);

        if (savedInstanceState == null) {
            loadRootFragment(R.id.fl_container, IdCardFragment.newInstance());
        }
        initAccessTokenWithAkSk();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StepEvent event) {
        switch (event.getStep()) {
            case 1:
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);

                start(DetectionFragment.newInstance(event.getAddIdCardInfo()));
                break;
            case 2:
                if (event.isSuccess()) {
                    showToast("已完成，待审批");
                    finish();
                } else {
                    layout1.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.GONE);
                    pop();
                }
                break;
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            finish();
        }
    }

    @OnClick({R.id.iv_back})
    void onClick(View view) {
        finish();
    }

    private void initAccessTokenWithAkSk() {
        String token = LocalCacheUtils.getPersistentSettingString(Constants.SP_GLOBAL_NAME, Constants.SP_Access_Token, "");
        if (!TextUtils.isEmpty(token)) {
            return;
        }
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                initLicense();
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                runOnUiThread(() -> showToast("初始化认证失败"));
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
//                    runOnUiThread(() -> showToast("本地质量控制初始化错误，错误原因： " + msg));
                });
    }
}
