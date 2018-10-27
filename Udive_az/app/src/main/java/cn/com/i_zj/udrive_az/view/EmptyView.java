package cn.com.i_zj.udrive_az.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.i_zj.udrive_az.R;

/**
 * @author JayQiu
 * @create 2018/10/24
 * @Describe
 */
public class EmptyView extends FrameLayout {
    ImageView ivImag;
    TextView tvNullMsg;

    public EmptyView(Context context, ViewGroup viewGroup) {
        super(context);
        initView(context, viewGroup);
    }

    private void initView(Context context, ViewGroup viewGroup) {
        View view= inflate(context, R.layout.layout_empty, this);
        ivImag = (ImageView) view.findViewById(R.id.iv_imag);
        tvNullMsg = (TextView) view.findViewById(R.id.tv_null_msg);
    }

    public void setImage(@DrawableRes int resId) {
        ivImag.setImageResource(resId);
    }

    public void setMsg(String msg) {
        tvNullMsg.setText(msg);
    }
}
