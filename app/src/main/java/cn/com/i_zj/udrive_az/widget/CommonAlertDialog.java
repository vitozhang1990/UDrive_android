package cn.com.i_zj.udrive_az.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;

import cn.com.i_zj.udrive_az.R;

public class CommonAlertDialog {

    private Context mContext;
    private Dialog mDialog;
    private boolean mImageTitle, mShowNegBtn, mCancelAble;
    private String mTitle, mMsg, mPos, mNeg;
    private OnClickListener mPosListener, mNegListener;

    private CommonAlertDialog(Builder builder) {
        this.mContext = builder.context;
        this.mShowNegBtn = builder.showNegBtn;
        this.mCancelAble = builder.cancelAble;
        this.mImageTitle = builder.imageTitle;
        this.mTitle = builder.title;
        this.mMsg = builder.msg;
        this.mPos = builder.pos;
        this.mNeg = builder.neg;
        this.mPosListener = builder.posListener;
        this.mNegListener = builder.negListener;
    }

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    public void show() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.weight_dialog_alert, null);
        setLayout(view);
        mDialog = new Dialog(mContext, R.style.AlertDialogStyle);
        mDialog.setContentView(view);
//        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//        Display display = windowManager.getDefaultDisplay();
//        view.setLayoutParams(new FrameLayout.LayoutParams((int) (display
//                .getWidth() * 0.75), FrameLayout.LayoutParams.WRAP_CONTENT));
        mDialog.setCanceledOnTouchOutside(mCancelAble);
        mDialog.show();
    }

    public boolean isShowing() {
        if (mDialog != null && mDialog.isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private void setLayout(View view) {
        if (mImageTitle) {
            ViewStub imageStub = view.findViewById(R.id.stub_image);
            imageStub.inflate();
            if(!TextUtils.isEmpty(mTitle)) {
                TextView imageTitle = view.findViewById(R.id.alertImageTitle);
                imageTitle.setText(mTitle);
            }
        } else {
            ViewStub textStub = view.findViewById(R.id.stub_text);
            textStub.inflate();
            if(!TextUtils.isEmpty(mTitle)) {
                TextView textTitle = view.findViewById(R.id.alertTitle);
                textTitle.setText(mTitle);
            }
        }
        TextView txt_msg = view.findViewById(R.id.txt_msg);
        Button btn_neg = view.findViewById(R.id.btn_neg);
        Button btn_pos = view.findViewById(R.id.btn_pos);

        txt_msg.setText(mMsg);
        btn_pos.setText(mPos);
        btn_pos.setOnClickListener(v -> {
            if (mPosListener != null) {
                mPosListener.onClick(v);
            }
            dismiss();
        });
        if (mShowNegBtn) {
            btn_neg.setText(mNeg);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setOnClickListener(v -> {
                if (mNegListener != null) {
                    mNegListener.onClick(v);
                }
                dismiss();
            });
        } else {
            btn_neg.setVisibility(View.GONE);
        }
    }

    public static final class Builder {
        private Context context;
        private boolean imageTitle;
        private boolean showMsg, showPosBtn, showNegBtn, cancelAble;
        private String title, msg, pos, neg;
        private OnClickListener posListener, negListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setImageTitle(boolean imageTitle) {
            this.imageTitle = imageTitle;
            return this;
        }

        public Builder setTitle(String title) {
            if (TextUtils.isEmpty(title)) {
                throw new NullPointerException("Title can not be empty");
            }
            this.title = title;
            return this;
        }

        public Builder setMsg(String msg) {
            if (TextUtils.isEmpty(msg)) {
                throw new NullPointerException("Msg can not be empty");
            }
            this.showMsg = true;
            this.msg = msg;
            return this;
        }

        public Builder setPositiveButton(String text, OnClickListener listener) {
            if (TextUtils.isEmpty(text)) {
                throw new NullPointerException("Pos can not be empty");
            }
            this.pos = text;
            this.posListener = listener;
            this.showPosBtn = true;
            return this;
        }

        public Builder setNegativeButton(String text, OnClickListener listener) {
            if (TextUtils.isEmpty(text)) {
                throw new NullPointerException("Neg can not be empty");
            }
            this.neg = text;
            this.negListener = listener;
            this.showNegBtn = true;
            return this;
        }

        public Builder setCancelable(boolean cancel) {
            this.cancelAble = cancel;
            return this;
        }

        public CommonAlertDialog build() {
            if (!showMsg) {
                throw new NullPointerException("Msg can not be empty");
            }
            if (!showPosBtn) {
                throw new NullPointerException("Pos can not be empty");
            }
            return new CommonAlertDialog(this);
        }
    }
}