package cn.com.i_zj.udrive_az.lz.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.i_zj.udrive_az.R;

public class PaymentView extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    private ImageView image;
    private TextView tvMsg;
    private CheckBox cb;
    private boolean unCheck;

    public PaymentView(Context context) {
        super(context);
    }

    public PaymentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PaymentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        View layout = LayoutInflater.from(context).inflate(R.layout.view_pay_ment, this, true);
        image = layout.findViewById(R.id.image);
        tvMsg = layout.findViewById(R.id.tvMsg);
        cb = layout.findViewById(R.id.cb);
        cb.setClickable(false);
        cb.setFocusable(false);

        cb.setOnCheckedChangeListener(this);
    }

    public void setView(int resource, String msg, boolean check) {
        image.setImageResource(resource);
        tvMsg.setText(msg);
        if (unCheck) {
            cb.setChecked(false);
        } else {
            cb.setChecked(check);
        }

    }

    public void setUnClick() {
        unCheck = true;
        tvMsg.setTextColor(Color.RED);
        cb.setChecked(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
    }

    public void setCheck(boolean b) {
        if (!unCheck) {
            cb.setChecked(b);
        }
    }

    public boolean isCheck() {
        return cb.isChecked();
    }

}
