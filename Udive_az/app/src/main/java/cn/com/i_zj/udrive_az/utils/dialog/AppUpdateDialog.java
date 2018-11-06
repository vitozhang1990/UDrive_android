package cn.com.i_zj.udrive_az.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.AppversionEntity;

/**
 * @author JayQiu
 * @create 2018/11/6
 * @Describe App 更新提示
 */
public class AppUpdateDialog extends Dialog {
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_canel)
    Button tvCanel;
    @BindView(R.id.tv_ok)
    Button tvOk;
    @BindView(R.id.v_line)
    View vLine;



    private Context mContext;
    private AppversionEntity appversionEntity;

    private OnClickListener onClickListener;

    public AppUpdateDialog(Context context) {
        super(context, R.style.UpdateDialogStytle);
        mContext = context;
        View view = View.inflate(context, R.layout.dialog_update, null);
        setContentView(view);
        ButterKnife.bind(this, view);
    }

    public void setAppversion(AppversionEntity appversion) {
        if(appversion!=null){
            tvTitle.setText("发现新版本 V"+appversion.getAppVersion());
            tvMsg.setText(appversion.getContent());
            if(appversion.getState()==1){
                setCancelable(false);
                tvCanel.setVisibility(View.GONE);
                vLine.setVisibility(View.GONE);
            }
        }


    }



    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void show() {
        super.show();
        Window window = this.getWindow();
        WindowManager m = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = m.getDefaultDisplay(); // 获取屏幕宽
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值、高用
        p.width = (int) (display.getWidth() * 0.8); // 宽度设置为屏幕的
        window.setAttributes(p);
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
    }

    @OnClick({R.id.tv_canel, R.id.tv_ok})
    public void onClick(View view) {
        if(onClickListener!=null){
            onClickListener.onClick(view);
        }

    }
    public  interface  OnClickListener{
        void onClick(View view);
    }
}
