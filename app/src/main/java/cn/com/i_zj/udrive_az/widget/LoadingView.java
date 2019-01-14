package cn.com.i_zj.udrive_az.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class LoadingView extends AppCompatImageView {
    private AnimationDrawable animationDrawable;

    public LoadingView(Context context) {
        super(context);
        initView();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        animationDrawable = (AnimationDrawable) getBackground();
        if (animationDrawable != null) {
            animationDrawable.start();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if (getVisibility() != visibility) {
            super.setVisibility(visibility);
            if (visibility == GONE || visibility == INVISIBLE) {
                stopAnim();
            } else {
                startAnim();
            }
        }
    }

    private void startAnim() {
        if (animationDrawable == null) {
            initView();
        }
        if (animationDrawable != null) {
            animationDrawable.start();
        }
    }

    private void stopAnim() {
        if (animationDrawable == null) {
            initView();
        }
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
    }
}
