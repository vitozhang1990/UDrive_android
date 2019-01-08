package cn.com.i_zj.udrive_az.widget.urefresh;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.com.i_zj.udrive_az.R;

/**
 * @author JayQiu
 * @create 2018/11/7
 * @Describe
 */
public class URefreshView extends LinearLayout {
    private ImageView imageView;
    //当前的下拉距离
    int distance = 0;
    private int viewSizeHeight;
    private int viewSizeWidth;
    private ViewStatus viewStatus;
    int leftAngle = -0;
    int rightAngle = Math.abs(leftAngle) - 180;
    private ArrayList<Integer> ids = new ArrayList<>();
    private ArrayList<Integer> refreshIds = new ArrayList<>();
    private ArrayList<Integer> refreshEndIds = new ArrayList<>();
    private AnimationDrawable animationDrawable;
    private AnimationDrawable animationEnd;

    public URefreshView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        animationDrawable = new AnimationDrawable();
        animationEnd = new AnimationDrawable();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_refresh_header, this);
        imageView = view.findViewById(R.id.iv_image);
        viewStatus = ViewStatus.START;
        measure(0, 0);
        for (int i = 0; i <= 65; i++) {
            String name = "refresh_";
            if (i <= 9) {
                name = name + "0000" + i;
            } else {
                name = name + "000" + i;
            }
            int id = getResources().getIdentifier(name, "mipmap", context.getPackageName());
            if (i >= 0 && i <= 19) {
                ids.add(id);
            } else if (i > 19 && i <= 49) {
                refreshIds.add(i);
                Drawable drawable = getResources().getDrawable(id);
                animationDrawable.addFrame(drawable, 33);
                animationDrawable.setOneShot(false);
            } else if (i > 49 && i <= 65) {
                refreshEndIds.add(i);
                Drawable drawable = getResources().getDrawable(id);
                animationEnd.addFrame(drawable, 33);
                animationEnd.setOneShot(true);
            }


        }

    }



    public void setDistance(int distance) {
        this.distance = distance;
        if (distance< (viewSizeHeight / 3)) {
            return;
        }
        int animOffset =viewSizeHeight-(viewSizeHeight-  (distance - viewSizeHeight / 3))  ;
        if (viewStatus == ViewStatus.START && animOffset > 0) {
            int id = animOffset / (viewSizeHeight/3*2/20);

            Log.e("====>", id + "====");
            if (id >= 0 && id <= 19) {
                imageView.setImageResource(ids.get(id));
            }
        } else if (viewStatus == ViewStatus.START && animOffset <= 0) {
            restoreView();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewSizeHeight = getMeasuredHeight();
        viewSizeWidth = getMeasuredWidth();
    }

    //设置是否正在刷新
    public void setViewStatus(ViewStatus viewStatus) {
        this.viewStatus = viewStatus;
        if (viewStatus == ViewStatus.REFRESHING) {

            refresh();
        } else if (viewStatus == ViewStatus.END_REFRESHING) {
            endRefresh();
            end();
        } else if (viewStatus == ViewStatus.END) {
//            end();
            this.viewStatus = ViewStatus.START;
        }
    }

    private void refresh() {
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            imageView.setImageDrawable(animationDrawable);
            animationDrawable.start();
        }
    }

    private void endRefresh() {
        if (animationDrawable != null && animationDrawable.isRunning()) {

            animationDrawable.stop();
        }

    }

    public void end() {
        if (animationEnd != null && !animationEnd.isRunning()) {
            imageView.setImageDrawable(animationEnd);
            animationEnd.start();
        }
    }

    //刷新完毕 重置view的状态
    public void restoreView() {
        viewStatus = ViewStatus.START;
        leftAngle = 0;
        rightAngle = -180;
        distance = 0;
        invalidate();
    }
}
