package cn.com.i_zj.udrive_az.step;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraNativeHelper;
import com.baidu.ocr.ui.camera.CameraView;

import butterknife.BindView;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.AuthResult;
import cn.com.i_zj.udrive_az.step.fragment.DetectionFragment;
import cn.com.i_zj.udrive_az.step.fragment.IdCardFragment;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;
import me.yokeyword.fragmentation.ISupportFragment;

public class StepActivity extends DBSBaseActivity {

    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.line1)
    View line1;
    @BindView(R.id.name1)
    TextView name1;

    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.line21)
    View line21;
    @BindView(R.id.line22)
    View line22;
    @BindView(R.id.name2)
    TextView name2;

    @BindView(R.id.text3)
    TextView text3;
    @BindView(R.id.line31)
    View line31;
    @BindView(R.id.line32)
    View line32;
    @BindView(R.id.name3)
    TextView name3;

    @BindView(R.id.text4)
    TextView text4;
    @BindView(R.id.line4)
    View line4;
    @BindView(R.id.name4)
    TextView name4;

    private AuthResult authResult;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_step;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        authResult = (AuthResult) getIntent().getSerializableExtra("data");
        if (authResult == null) {
            showToast("错误的参数");
            finish();
            return;
        }

        initAccessTokenWithAkSk();
        ISupportFragment firstFragment = findFragment(IdCardFragment.class);
        if (firstFragment == null) {
            loadRootFragment(R.id.fl_container, IdCardFragment.newInstance());
        }
    }

    private void initStepBar() {
        // 0：未认证 1：审核中 2：已认证 3：认证失败
        if (authResult.getData().getIdcard().getState() == 2) {
            //第一个圈及后面的线
            text1.setBackground(getResources().getDrawable(R.drawable.bg_circle_black));
            line1.setBackgroundColor(Color.parseColor("#33333D"));
            line1.setBackgroundColor(Color.parseColor("#33333D"));
            line21.setBackgroundColor(Color.parseColor("#33333D"));
            name1.setTextColor(Color.parseColor("#33333D"));
            //第二个圈及后面的线
            text2.setBackground(getResources().getDrawable(R.drawable.bg_circle_black));
            line22.setBackgroundColor(Color.parseColor("#33333D"));
            line31.setBackgroundColor(Color.parseColor("#33333D"));
            name2.setTextColor(Color.parseColor("#33333D"));
        } else {
            text1.setBackground(getResources().getDrawable(R.drawable.bg_circle_grey));
            line1.setBackgroundColor(Color.parseColor("#CCCCCC"));
            line21.setBackgroundColor(Color.parseColor("#CCCCCC"));
            name1.setTextColor(Color.parseColor("#33333D"));

            text2.setBackground(getResources().getDrawable(R.drawable.bg_circle_grey));
            line22.setBackgroundColor(Color.parseColor("#CCCCCC"));
            line31.setBackgroundColor(Color.parseColor("#CCCCCC"));
            name2.setTextColor(Color.parseColor("#CCCCCC"));
        }
        if (authResult.getData().getDriver().getState() == 2) {
            text3.setBackground(getResources().getDrawable(R.drawable.bg_circle_black));
            line32.setBackgroundColor(Color.parseColor("#33333D"));
            line4.setBackgroundColor(Color.parseColor("#33333D"));
            name3.setTextColor(Color.parseColor("#33333D"));
        } else {
            text3.setBackground(getResources().getDrawable(R.drawable.bg_circle_grey));
            line32.setBackgroundColor(Color.parseColor("#CCCCCC"));
            line4.setBackgroundColor(Color.parseColor("#CCCCCC"));
            name3.setTextColor(Color.parseColor("#CCCCCC"));
        }
        if (authResult.getData().getDeposit().getState() == 2) {
            text4.setBackground(getResources().getDrawable(R.drawable.bg_circle_black));
            name4.setTextColor(Color.parseColor("#33333D"));
        } else {
            text4.setBackground(getResources().getDrawable(R.drawable.bg_circle_grey));
            name4.setTextColor(Color.parseColor("#CCCCCC"));
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
