package cn.com.i_zj.udrive_az.login;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blankj.utilcode.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.LoginSuccessEvent;
import cn.com.i_zj.udrive_az.event.NumberClickEvent;
import cn.com.i_zj.udrive_az.model.NetworkResult;
import cn.com.i_zj.udrive_az.model.SessionResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.DeviceUtils;
import cn.com.i_zj.udrive_az.utils.PushUtil;
import cn.com.i_zj.udrive_az.utils.RegexUtils;
import cn.com.i_zj.udrive_az.web.WebActivity;
import cn.com.i_zj.udrive_az.widget.VerificationCodeEditText;
import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wli on 2018/8/12.
 * 用户登录DialogFragment
 */

public class LoginDialogFragment extends BottomSheetDialogFragment {

    @BindView(R.id.login_rbtn_licence)
    AppCompatCheckBox licenceView;

    @BindView(R.id.login_et_phone)
    AppCompatEditText phoneView;

    @BindView(R.id.login_layout_phone)
    LinearLayout phoneLayout;

    @BindView(R.id.login_layout_code)
    LinearLayout codeLayout;

    @BindView(R.id.login_tv_second)
    AppCompatTextView condeSecondView;

    @BindView(R.id.login_tv_code_tip)
    AppCompatTextView codeTipView;

    @BindView(R.id.login_et_code)
    VerificationCodeEditText codeView;

    private boolean isPhonePanel = true;

    protected Dialog progressDialog;
    private CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bottomSheetView = inflater.inflate(R.layout.dialog_login_bottom, container, false);
        ButterKnife.bind(this, bottomSheetView);

        setShowSoftInputOnFocus(phoneView);
        setShowSoftInputOnFocus(codeView);

        setCancelable(false);
        return bottomSheetView;
    }

    private void setShowSoftInputOnFocus(EditText editText) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            editText.setInputType(InputType.TYPE_NULL);
        } else {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setSoftInputShownOnFocus;
                setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setSoftInputShownOnFocus.setAccessible(true);
                setSoftInputShownOnFocus.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showPanel(true);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout)
                        dialog.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0);
            }
        });
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (cancelListener != null) {
            cancelListener.onCancel(dialog);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDismiss(dialog);
        }
    }

    private DialogInterface.OnCancelListener cancelListener;
    private DialogInterface.OnDismissListener dismissListener;

    public void setListener(DialogInterface.OnCancelListener listener) {
        this.cancelListener = listener;
    }

    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    @OnClick(R.id.login_tv_licence)
    public void onLicenceClick(View view) {
        WebActivity.startWebActivity(getContext(), BuildConfig.DOMAIN + "/agreement/");
    }

    @OnClick(R.id.login_btn_up)
    public void onUpClick(View view) {
        showPanel(true);
    }

    @OnClick(R.id.login_btn_close)
    public void onCloseClick(View view) {
        this.dismiss();
        if (cancelListener != null) {
            cancelListener.onCancel(new DialogInterface() {
                @Override
                public void cancel() {

                }

                @Override
                public void dismiss() {

                }
            });
        }
    }

    @OnClick(R.id.login_tv_second)
    public void onSecondClick(View view) {
        String phone = phoneView.getText().toString();
        sendVerifyCode(phone, false);
    }

    @OnClick(R.id.login_btn_next)
    public void onNextClick(View view) {
        String phone = phoneView.getText().toString();
        if (isPhonePanel) {
            if (!licenceView.isChecked()) {
                showToast("请先阅读并同意协议");
                return;
            }

            if (!RegexUtils.isNewMobileExact(phone)) {
                showToast("手机号格式错误");
                return;
            }
            sendVerifyCode(phone, true);
        } else {
            String code = codeView.getText().toString();
            if (TextUtils.isEmpty(code)) {
                showToast("请输入验证码");
                return;
            }
            if (code.length() < 6) {
                showToast("请输入完整验证码");
                return;
            }
            login(phone, code);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void login(String phone, String code) {
        showProgressDialog();
        UdriveRestClient.getClentInstance().login(DeviceUtils.getDeviceId(), phone, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SessionResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SessionResult sessionResult) {
                        if (null != sessionResult) {
                            showToast("登录成功");
                            countDownTimer.cancel();
                            SessionManager.getInstance().cacheSession(sessionResult);
                            String regId = JPushInterface.getRegistrationID(LoginDialogFragment.this.getContext());
                            PushUtil.registPush(regId);
                            EventBus.getDefault().post(new LoginSuccessEvent());
                            dismiss();
                        } else {
                            showToast("请求失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    public void sendVerifyCode(final String phone, final boolean isJump) {
        showProgressDialog();
        UdriveRestClient.getClentInstance().requestSms(DeviceUtils.getDeviceId(), phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NetworkResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NetworkResult networkResult) {
                        if (networkResult != null && networkResult.getCode() == 1) {
                            if (isJump) {
                                showPanel(false);
                            }
                            codeTipView.setText("验证码已发送至" + phone);
                            startTimerCount();
                        } else {
                            if (networkResult != null && !TextUtils.isEmpty(networkResult.getMessage())) {
                                showToast(networkResult.getMessage());
                            } else {
                                showToast("验证码发送错误" + networkResult != null ? String.valueOf(networkResult.getCode()) : "");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    private void showPanel(boolean isPhone) {
        isPhonePanel = isPhone;
        codeView.setText("");
        phoneLayout.setVisibility(isPhone ? View.VISIBLE : View.GONE);
        codeLayout.setVisibility(!isPhone ? View.VISIBLE : View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNumberClickEvent(NumberClickEvent event) {
        if (isPhonePanel) {
            String content = phoneView.getText().toString();
            if (!event.isDelete) {
                phoneView.append(event.number);
            } else if (content.length() > 0) {
                phoneView.setText(content.substring(0, content.length() - 1));
                phoneView.setSelection(content.length() - 1);
            }
        } else {
            String content = codeView.getText().toString();
            if (!event.isDelete) {
                codeView.append(event.number);
            } else if (content.length() > 0) {
                codeView.setText(content.substring(0, content.length() - 1));
            }
        }
    }

    private void startTimerCount() {
        if (null != countDownTimer) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (null != condeSecondView) {
                    condeSecondView.setTextColor(Utils.getApp().getResources().getColor(R.color.text_gray_color));
                    condeSecondView.setEnabled(false);
                    condeSecondView.setText((millisUntilFinished / 1000) + "秒后重发");
                }
            }

            @Override
            public void onFinish() {
                if (null != condeSecondView) {
                    condeSecondView.setTextColor(Color.BLACK);
                    condeSecondView.setEnabled(true);
                    condeSecondView.setText("重新发送");
                }
            }
        };
        countDownTimer.start();
    }

    //TODO
    public void showToast(String content) {
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog() {
        if (getActivity() == null) {
            return;
        }
        if (null == progressDialog) {
            progressDialog = new Dialog(getActivity(), R.style.MyDialog);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dissmisProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()
                && getActivity() != null && !getActivity().isFinishing()) {
            progressDialog.dismiss();
        }
    }
}