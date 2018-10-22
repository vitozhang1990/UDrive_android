package cn.com.i_zj.udrive_az.lz.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import cn.com.i_zj.udrive_az.R;

/**
 * Time:2018/8/11
 * User:lizhen
 * Description:
 */

public class DrawerBottomItemView extends LinearLayout {

    private ImageView mImageView;
    private TextView mTextView;

    public DrawerBottomItemView(Context context) {
        super(context);
    }

    public DrawerBottomItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DrawerBottomItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        View layout = LayoutInflater.from(context).inflate(R.layout.view_drawer_bottom_item, this, true);
        mImageView = layout.findViewById(R.id.image);
        mTextView = layout.findViewById(R.id.tv);


        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.DrawerBottomItemView);

        if (attributes == null) {
            return;
        }

        int imageSrc = attributes.getResourceId(R.styleable.DrawerBottomItemView_bottom_image_src, R.mipmap.ic_launcher);
        Picasso.with(context).load(imageSrc).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(mImageView);
        String tvText = attributes.getString(R.styleable.DrawerBottomItemView_bottom_tv_text);
        mTextView.setText(tvText);
        float tvTextSize = attributes.getDimension(R.styleable.DrawerBottomItemView_bottom_tv_text_size, 15);
        mTextView.setTextSize(tvTextSize);
        int tvTextColor = attributes.getColor(R.styleable.DrawerBottomItemView_bottom_tv_text_color, Color.BLACK);
        mTextView.setTextColor(tvTextColor);
        int tvTopMargin = (int) attributes.getDimension(R.styleable.DrawerBottomItemView_bottom_tv_top_margin, 10);
        LayoutParams layoutParams = (LayoutParams) mTextView.getLayoutParams();
        layoutParams.topMargin = tvTopMargin;
        mTextView.setLayoutParams(layoutParams);
        attributes.recycle();
    }
}
