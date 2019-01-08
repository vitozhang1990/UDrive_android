package cn.com.i_zj.udrive_az.lz.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.i_zj.udrive_az.R;

public class CarDetailItemView extends LinearLayout {

    private TextView tvTop;
    private TextView tvBottom;

    public CarDetailItemView(Context context) {
        super(context);
    }

    public CarDetailItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CarDetailItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {

        View layout = LayoutInflater.from(context).inflate(R.layout.view_car_detail_item, this, true);
        tvTop = layout.findViewById(R.id.tv_top);
        tvBottom = layout.findViewById(R.id.tv_bottom);
    }

    public void setText(String top, String bottom) {
        if (!TextUtils.isEmpty(top)) {
            tvTop.setText(top);
        }
        if (!TextUtils.isEmpty(bottom)) {
            tvBottom.setText(bottom);
            tvBottom.setVisibility(VISIBLE);
        } else {
            tvBottom.setVisibility(GONE);
        }
    }

    public void setColor(int top, int bottom) {
        if (top != -1) {
            tvTop.setTextColor(top);
        }
        if (bottom != -1) {
            tvBottom.setTextColor(bottom);
        }
    }

    public void setVisibleBottom(int visible) {
        tvBottom.setVisibility(visible);

    }
}
