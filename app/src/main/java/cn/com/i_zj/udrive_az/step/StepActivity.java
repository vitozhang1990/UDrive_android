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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.StepEvent;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.AuthResult;
import cn.com.i_zj.udrive_az.step.fragment.DepositFragment;
import cn.com.i_zj.udrive_az.step.fragment.DetectionFragment;
import cn.com.i_zj.udrive_az.step.fragment.DriveCardFragment;
import cn.com.i_zj.udrive_az.step.fragment.IdCardFragment;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;

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

    private boolean loadFragment;

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
        initStepBar();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StepEvent event) {
        switch (event.getStep()) {
            case 1:
                if (event.getAddIdCardInfo() == null) {
                    return;
                }
                text1.setBackground(getResources().getDrawable(R.drawable.bg_circle_black));
                text1.setTextColor(Color.parseColor("#FFFFFF"));
                line1.setBackgroundColor(Color.parseColor("#33333D"));
                line21.setBackgroundColor(Color.parseColor("#33333D"));
                name1.setTextColor(Color.parseColor("#33333D"));

                text2.setBackground(getResources().getDrawable(R.drawable.bg_circle_black1));
                text1.setTextColor(Color.parseColor("#33333D"));
                line22.setBackgroundColor(Color.parseColor("#CCCCCC"));
                line31.setBackgroundColor(Color.parseColor("#CCCCCC"));
                name2.setTextColor(Color.parseColor("#33333D"));

                start(DetectionFragment.newInstance(event.getAddIdCardInfo()));
                break;
            case 2:
                if (event.isSuccess()) {
                    text2.setBackground(getResources().getDrawable(R.drawable.bg_circle_black));
                    text2.setTextColor(Color.parseColor("#FFFFFF"));
                    line22.setBackgroundColor(Color.parseColor("#33333D"));
                    line31.setBackgroundColor(Color.parseColor("#33333D"));
                    name2.setTextColor(Color.parseColor("#33333D"));

                    if (authResult.getData().getDriver().getState() != 2
                            && authResult.getData().getDriver().getState() != 1) {
                        text3.setBackground(getResources().getDrawable(R.drawable.bg_circle_black1));
                        text3.setTextColor(Color.parseColor("#33333D"));
                        name3.setTextColor(Color.parseColor("#33333D"));

                        startWithPop(DriveCardFragment.newInstance());
                    } else if (authResult.getData().getDeposit().getState() != 2) {
                        text4.setBackground(getResources().getDrawable(R.drawable.bg_circle_black1));
                        text4.setTextColor(Color.parseColor("#33333D"));
                        name4.setTextColor(Color.parseColor("#33333D"));

                        startWithPop(DepositFragment.newInstance());
                    } else {
                        showToast("已完成，待审批");
                        finish();
                    }
                } else {
                    text1.setBackground(getResources().getDrawable(R.drawable.bg_circle_black1));
                    text1.setTextColor(Color.parseColor("#33333D"));
                    line1.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    line21.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    name1.setTextColor(Color.parseColor("#33333D"));

                    text2.setBackground(getResources().getDrawable(R.drawable.bg_circle_grey));
                    line22.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    line31.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    name2.setTextColor(Color.parseColor("#CCCCCC"));

                    pop();
                }
                break;
            case 3:
                text3.setBackground(getResources().getDrawable(R.drawable.bg_circle_black));
                text3.setTextColor(Color.parseColor("#FFFFFF"));
                line32.setBackgroundColor(Color.parseColor("#33333D"));
                line4.setBackgroundColor(Color.parseColor("#33333D"));
                name3.setTextColor(Color.parseColor("#33333D"));

                if (authResult.getData().getDeposit().getState() != 2) {
                    text4.setBackground(getResources().getDrawable(R.drawable.bg_circle_black1));
                    text4.setTextColor(Color.parseColor("#33333D"));
                    name4.setTextColor(Color.parseColor("#33333D"));

                    startWithPop(DepositFragment.newInstance());
                } else {
                    showToast("已完成，待审批");
                    finish();
                }
                break;
            case 4:
                text4.setBackground(getResources().getDrawable(R.drawable.bg_circle_black));
                text4.setTextColor(Color.parseColor("#FFFFFF"));
                name4.setTextColor(Color.parseColor("#33333D"));

                showToast("已完成");
                finish();
                break;
        }
    }

    private void initStepBar() {
        // 0：未认证 1：审核中 2：已认证 3：认证失败
        if (authResult.getData().getIdcard().getState() == 2
                || authResult.getData().getIdcard().getState() == 1) {
            //第一个圈及后面的线
            text1.setBackground(getResources().getDrawable(R.drawable.bg_circle_black));
            line1.setBackgroundColor(Color.parseColor("#33333D"));
            line21.setBackgroundColor(Color.parseColor("#33333D"));
            name1.setTextColor(Color.parseColor("#33333D"));
            //第二个圈及后面的线
            text2.setBackground(getResources().getDrawable(R.drawable.bg_circle_black));
            line22.setBackgroundColor(Color.parseColor("#33333D"));
            line31.setBackgroundColor(Color.parseColor("#33333D"));
            name2.setTextColor(Color.parseColor("#33333D"));
        } else {
            text1.setBackground(getResources().getDrawable(R.drawable.bg_circle_black1));
            text1.setTextColor(Color.parseColor("#33333D"));
            line1.setBackgroundColor(Color.parseColor("#CCCCCC"));
            line21.setBackgroundColor(Color.parseColor("#CCCCCC"));
            name1.setTextColor(Color.parseColor("#33333D"));

            text2.setBackground(getResources().getDrawable(R.drawable.bg_circle_grey));
            line22.setBackgroundColor(Color.parseColor("#CCCCCC"));
            line31.setBackgroundColor(Color.parseColor("#CCCCCC"));
            name2.setTextColor(Color.parseColor("#CCCCCC"));

            if (!loadFragment) {
                loadRootFragment(R.id.fl_container, IdCardFragment.newInstance());
                loadFragment = true;
            }
        }
        if (authResult.getData().getDriver().getState() == 2
                || authResult.getData().getDriver().getState() == 1) {
            text3.setBackground(getResources().getDrawable(R.drawable.bg_circle_black));
            text3.setTextColor(Color.parseColor("#FFFFFF"));
            line32.setBackgroundColor(Color.parseColor("#33333D"));
            line4.setBackgroundColor(Color.parseColor("#33333D"));
            name3.setTextColor(Color.parseColor("#33333D"));
        } else {
            if (!loadFragment) {
                text3.setBackground(getResources().getDrawable(R.drawable.bg_circle_black1));
                text3.setTextColor(Color.parseColor("#33333D"));
                line32.setBackgroundColor(Color.parseColor("#CCCCCC"));
                line4.setBackgroundColor(Color.parseColor("#CCCCCC"));
                name3.setTextColor(Color.parseColor("#33333D"));

                loadRootFragment(R.id.fl_container, DriveCardFragment.newInstance());
                loadFragment = true;
            }
        }
        if (authResult.getData().getDeposit().getState() == 2) {
            text4.setBackground(getResources().getDrawable(R.drawable.bg_circle_black));
            text4.setTextColor(Color.parseColor("#FFFFFF"));
            name4.setTextColor(Color.parseColor("#33333D"));
        } else {
            if (!loadFragment) {
                text4.setBackground(getResources().getDrawable(R.drawable.bg_circle_black1));
                text4.setTextColor(Color.parseColor("#33333D"));
                name4.setTextColor(Color.parseColor("#33333D"));

                loadRootFragment(R.id.fl_container, DepositFragment.newInstance());
                loadFragment = true;
            }
        }
    }

    @OnClick({R.id.iv_back})
    void onClick(View view) {
        finish();
    }

    @Override
    public void onBackPressedSupport() {
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
