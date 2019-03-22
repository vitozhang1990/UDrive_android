package cn.com.i_zj.udrive_az.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;

public class ShareCouponDialog extends Dialog {

    private Context mContext;
    private OnShareClick listener;

    public ShareCouponDialog(Context context) {
        super(context, R.style.UpdateDialogStytle);
        setCancelable(false);
        mContext = context;
        initView();
    }

    private void initView() {
        View view = View.inflate(mContext, R.layout.dialog_share_coupon, null);
        setContentView(view);
        ButterKnife.bind(this, view);
    }

    @OnClick({R.id.btn_share, R.id.iv_close})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_share:
                dismiss();
                if (listener != null) {
                    listener.shareClick();
                }
                break;
            case R.id.iv_close:
                dismiss();
                break;
        }
    }

    public void setListener(OnShareClick listener) {
        this.listener = listener;
    }

    public interface OnShareClick {
        void shareClick();
    }
}
