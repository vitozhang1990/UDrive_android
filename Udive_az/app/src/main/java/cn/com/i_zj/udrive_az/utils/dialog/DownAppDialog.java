package cn.com.i_zj.udrive_az.utils.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.utils.SizeUtils;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;

/**
 * @author JayQiu
 * @create 2018/11/6
 * @Describe
 */
public class DownAppDialog extends AlertDialog implements View.OnClickListener {

    private TextView txPercentage;
    private TextView txSize;
    private ProgressBar progressBar;
    private TextView txback;
    private View view;
    private LinearLayout item;
    private TextView xian;

    /**
     * @param context
     * @param theme   加载框style
     */
    public DownAppDialog(Context context, int theme) {
        super(context, theme);
        show();
        view = getLayoutInflater().inflate(R.layout.dialog_apk_update, null);
        setContentView(view,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        init();

        int width = ToolsUtils.getWindowWidth(context);
        item.setLayoutParams(new LinearLayout.LayoutParams(width -SizeUtils.dp2px(context, 60),
                LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private void init() {

        txback = (TextView) view.findViewById(R.id.down_dissmiss);
        txPercentage = (TextView) view.findViewById(R.id.down_percentage);
        txSize = (TextView) view.findViewById(R.id.down_size);
        item = (LinearLayout) view.findViewById(R.id.down_item);
        progressBar = (ProgressBar) view.findViewById(R.id.down_progress);
        xian = (TextView)view.findViewById(R.id.down_xian);
        setCanceledOnTouchOutside(false);
        txback.setOnClickListener(this);
        showdd();

    }

    /**
     * @param isForce 是否强制更新
     */
    public void isForce(boolean isForce) {
        if (isForce && txback != null) {
            txback.setVisibility(View.GONE);
        }
        if (xian != null) {
            xian.setVisibility(View.GONE);

        }
    }

    /**
     * 禁止返回键使用
     */
    private void showdd() {

        this.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    // 此处把dialog dismiss掉，然后把本身的activity finish掉
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == txback.getId()) {

            dismiss();
        }
    }

    public void setInfo(String size, int progress) {

        txSize.setText(size + "");
        txPercentage.setText(progress + "%");
        progressBar.setProgress(progress);
    }
}
