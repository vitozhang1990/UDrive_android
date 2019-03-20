package cn.com.i_zj.udrive_az.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.utils.UIUtils;

public class CommonAlertDialog {

    private Context mContext;
    private Dialog mDialog;
    private int mImageResource;
    private boolean mImageTitle, mShowNegBtn, mCancelAble, mMsgCenter;
    private String mTitle, mMsg, mPos, mNeg;
    private OnClickListener mPosListener, mNegListener;

    private CommonAlertDialog(Builder builder) {
        this.mContext = builder.context;
        this.mShowNegBtn = builder.showNegBtn;
        this.mCancelAble = builder.cancelAble;
        this.mImageTitle = builder.imageTitle;
        this.mImageResource = builder.imageResource;
        this.mMsgCenter = builder.msgCenter;
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

    public CommonAlertDialog show() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.weight_dialog_alert, null);
        setLayout(view);
        mDialog = new Dialog(mContext, R.style.AlertDialogStyle);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(mCancelAble);
        mDialog.show();
        return this;
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
        TextView txt_msg = view.findViewById(R.id.txt_msg);
        Button btn_neg = view.findViewById(R.id.btn_neg);
        Button btn_pos = view.findViewById(R.id.btn_pos);

        if (mImageTitle) {
            ViewStub imageStub = view.findViewById(R.id.stub_image);
            imageStub.inflate();
            if(!TextUtils.isEmpty(mTitle)) {
                TextView imageTitle = view.findViewById(R.id.alertImageTitle);
                imageTitle.setText(mTitle);
            }
            ImageView alertImage = view.findViewById(R.id.alertImage);
            if (mImageResource != 0) {
                Glide.with(mContext).load(mImageResource).error(R.drawable.pic_exchange_fail).into(alertImage);
            }
        } else {
            ViewStub textStub = view.findViewById(R.id.stub_text);
            textStub.inflate();
            if(!TextUtils.isEmpty(mTitle)) {
                TextView textTitle = view.findViewById(R.id.alertTitle);
                textTitle.setText(mTitle);
            }
            txt_msg.setMinHeight(UIUtils.dp2px(80));
        }

        txt_msg.setText(mMsg);
        txt_msg.setGravity(mMsgCenter ? Gravity.CENTER : Gravity.LEFT | Gravity.TOP);
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
        private int imageResource;
        private boolean showMsg, showPosBtn, showNegBtn, cancelAble, msgCenter;
        private String title, msg, pos, neg;
        private OnClickListener posListener, negListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setImageTitle(boolean imageTitle) {
            this.imageTitle = imageTitle;
            return this;
        }

        public Builder setImageTitle(boolean imageTitle, int resource) {
            this.imageTitle = imageTitle;
            this.imageResource = resource;
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

        public Builder setMsgCenter(boolean center) {
            this.msgCenter = center;
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