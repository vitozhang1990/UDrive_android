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

public class DrawerItemView extends LinearLayout {

    private ImageView mImageView;
    private TextView mTvMid;
    private TextView mTvRight;
    private Context mContext;

    public DrawerItemView(Context context) {
        super(context);
    }

    public DrawerItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DrawerItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        mContext = context;
        View layout = LayoutInflater.from(mContext).inflate(R.layout.view_drawer_item, this, true);
        mImageView = layout.findViewById(R.id.iv);
        mTvMid = layout.findViewById(R.id.tv_mid);
        mTvRight = layout.findViewById(R.id.tv_right);


        TypedArray attributes = mContext.obtainStyledAttributes(attrs, R.styleable.DrawerItemView);

        if (attributes == null) {

            return;
        }
        //设置左边图片的资源文件
        int imageSrc = attributes.getResourceId(R.styleable.DrawerItemView_item_image_src, R.mipmap.ic_launcher);
        Picasso.with(context).load(imageSrc).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(mImageView);

        int imageLeftMargin = (int) attributes.getDimension(R.styleable.DrawerItemView_item_image_left_margin, 0);
        LayoutParams imageParams = (LayoutParams) mImageView.getLayoutParams();
        imageParams.leftMargin = imageLeftMargin;
        mImageView.setLayoutParams(imageParams);

        //设置中间文本
        String tvMidText = attributes.getString(R.styleable.DrawerItemView_item_tv_mid_text);
        mTvMid.setText(tvMidText);
        float tvMidTextSize = attributes.getDimension(R.styleable.DrawerItemView_item_tv_mid_text_size, 15);
        mTvMid.setTextSize(tvMidTextSize);
        int tvMidTextColor = attributes.getColor(R.styleable.DrawerItemView_item_tv_mid_text_color, Color.GRAY);
        mTvMid.setTextColor(tvMidTextColor);

        int tvMidLeftMargin = (int) attributes.getDimension(R.styleable.DrawerItemView_item_tv_mid_left_margin, 0);
        LayoutParams tvMidParams = (LayoutParams) mTvMid.getLayoutParams();
        tvMidParams.leftMargin = tvMidLeftMargin;
        mTvMid.setLayoutParams(tvMidParams);


        //设置右边文本
        String tvRightText = attributes.getString(R.styleable.DrawerItemView_item_tv_right_text);
        mTvRight.setText(tvRightText);
        float tvRightTextSize = attributes.getDimension(R.styleable.DrawerItemView_item_tv_right_text_size, 15);
        mTvRight.setTextSize(tvRightTextSize);
        int tvRightTextColor = attributes.getColor(R.styleable.DrawerItemView_item_tv_right_text_color, Color.GRAY);
        mTvRight.setTextColor(tvRightTextColor);

        int tvRightRightMargin = (int) attributes.getDimension(R.styleable.DrawerItemView_item_tv_right_right_margin, 0);
        LayoutParams tvRightParams = (LayoutParams) mTvRight.getLayoutParams();
        tvRightParams.rightMargin = tvRightRightMargin;
        mTvRight.setLayoutParams(tvRightParams);

        attributes.recycle();
    }

    public void setRightText(String msg) {
        mTvRight.setText(msg);
    }
    public void setRightTextColor(int color) {
        mTvRight.setTextColor(color);
    }
}
