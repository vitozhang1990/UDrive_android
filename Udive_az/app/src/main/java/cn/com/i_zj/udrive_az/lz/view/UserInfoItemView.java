package cn.com.i_zj.udrive_az.lz.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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

public class UserInfoItemView extends LinearLayout {

    private TextView mTvLeft;
    private TextView mTvRight;
    private ImageView mIvIcon;
    private ImageView mIvNext;
    private LinearLayout mMidLine;

    public UserInfoItemView(Context context) {
        super(context);
    }

    public UserInfoItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UserInfoItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        View layout = LayoutInflater.from(context).inflate(R.layout.view_user_info_item, this, true);
        mMidLine = layout.findViewById(R.id.line_mid);
        mTvLeft = layout.findViewById(R.id.tv_left);
        mTvRight = layout.findViewById(R.id.tv_right);
        mIvIcon = layout.findViewById(R.id.iv_icon);
        mIvNext = layout.findViewById(R.id.iv_next);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.UserInfoItemView);

        if (attributes == null) {

            return;
        }

        int imageSrc = attributes.getResourceId(R.styleable.UserInfoItemView_user_image_src_2, R.mipmap.default_avatar);
        Picasso.with(context).load(imageSrc).placeholder(R.mipmap.default_avatar).error(R.mipmap.default_avatar).into(mIvIcon);
        float leftMargin = attributes.getDimension(R.styleable.UserInfoItemView_user_tv_left_margin, 0);
        LayoutParams layoutParams = (LayoutParams) mTvLeft.getLayoutParams();
        layoutParams.leftMargin = (int) leftMargin;
        mTvLeft.setLayoutParams(layoutParams);

        String leftText = attributes.getString(R.styleable.UserInfoItemView_user_tv_left_text);
        mTvLeft.setText(leftText);
        float leftTextSize = attributes.getDimension(R.styleable.UserInfoItemView_user_tv_left_text_size, 15);
        mTvLeft.setTextSize(leftTextSize);
        int leftTextColor = attributes.getColor(R.styleable.UserInfoItemView_user_tv_left_text_color, Color.GRAY);
        mTvLeft.setTextColor(leftTextColor);


        String rightText = attributes.getString(R.styleable.UserInfoItemView_user_tv_right_text);
        mTvRight.setText(rightText);
        if (TextUtils.isEmpty(rightText)) {
            mIvIcon.setVisibility(VISIBLE);
        } else {
            float rightTextSize = attributes.getDimension(R.styleable.UserInfoItemView_user_tv_right_text_size, 15);
            mTvRight.setTextSize(rightTextSize);
            int rightTextColor = attributes.getColor(R.styleable.UserInfoItemView_user_tv_right_text_color, Color.GRAY);
            mTvRight.setTextColor(rightTextColor);
            mIvIcon.setVisibility(GONE);
        }


        float midMargin = attributes.getDimension(R.styleable.UserInfoItemView_user_tv_mid_margin, 0);
        mMidLine.setPadding(0, 0, (int) midMargin, 0);

        float rightMargin = attributes.getDimension(R.styleable.UserInfoItemView_user_tv_right_margin, 0);
        LayoutParams nextParams = (LayoutParams) mIvNext.getLayoutParams();
        nextParams.rightMargin = (int) rightMargin;
        mIvNext.setLayoutParams(nextParams);


        attributes.recycle();

    }

    public void setRightImageVisible(int visible) {
        mIvNext.setVisibility(visible);
    }

    public void setRightText(String msg) {
        mTvRight.setText(msg);

    }
}
