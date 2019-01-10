package cn.com.i_zj.udrive_az.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;

public class PictureTipDialog extends Dialog {
    private Context mContext;
    private CheckBox checkbox;

    public PictureTipDialog(Context context) {
        super(context, R.style.UpdateDialogStytle);
        mContext = context;
        initView();
    }

    private void initView() {
        setCanceledOnTouchOutside(false);
        View view = View.inflate(mContext, R.layout.dialog_picture_tips, null);
        checkbox = view.findViewById(R.id.checkbox);
        view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox.isChecked()) {
                    LocalCacheUtils.savePersistentSettingBoolean(Constants.SP_GLOBAL_NAME, Constants.SP_NOT_SHOW_PICTURE, true);
                }
                dismiss();
            }
        });
        setContentView(view);
    }
}
