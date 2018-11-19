package cn.com.i_zj.udrive_az.widget.urefresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * @author JayQiu
 * @create 2018/11/7
 * @Describe
 */
public class URefreshHeader extends LinearLayout implements RefreshHeader {
    private URefreshView uRefreshView;

    public URefreshHeader(Context context) {
        super(context);
    }
    public URefreshHeader(Context context, @Nullable AttributeSet attrs) {
        super(context,attrs);
        uRefreshView=new URefreshView(context);
        addView(uRefreshView);
    }
    @Override
    public void onPullingDown(float percent, int offset, int headerHeight, int extendHeight) {
        Log.d("下拉距离",offset+"");
        uRefreshView.setDistance(offset);
    }

    @Override
    public void onReleasing(float percent, int offset, int headerHeight, int extendHeight) {

    }


    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {

    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        return 900;
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        Log.e("===onStateChanged==","newState=="+newState+""+"=====oldState===="+oldState);
        switch (newState){
            case Refreshing:
                    uRefreshView.setViewStatus(ViewStatus.REFRESHING);
                break;
            case RefreshFinish:
                if (oldState== RefreshState.Refreshing) {
                    uRefreshView.setViewStatus(ViewStatus.END_REFRESHING);
                }
                break;
            case None:
                if (oldState== RefreshState.RefreshFinish) {
                    uRefreshView.setViewStatus(ViewStatus.END);
                }
                break;
        }
    }

}
