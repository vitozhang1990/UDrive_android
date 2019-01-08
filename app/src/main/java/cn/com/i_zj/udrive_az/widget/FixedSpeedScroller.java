package cn.com.i_zj.udrive_az.widget;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;
/**
 * 
 * @author jayqiu
 * @description  广告viewpager 滑动时间拦截
 * @CreationTime
 */
public class FixedSpeedScroller extends Scroller {  
    private int mDuration = 0;  
  
    public FixedSpeedScroller(Context context) {  
        super(context);  
    }  
  
    public FixedSpeedScroller(Context context, Interpolator interpolator) {  
        super(context, interpolator);  
    }  
  
    public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {  
        super(context, interpolator, flywheel);  
    }  
  
  
    @Override  
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {  
        super.startScroll(startX, startY, dx, dy, mDuration);  
    }  
  
    @Override  
    public void startScroll(int startX, int startY, int dx, int dy) {  
        super.startScroll(startX, startY, dx, dy, mDuration);  
    }

	public int getmDuration() {
		return mDuration;
	}

	public void setmDuration(int mDuration) {
		this.mDuration = mDuration;
	}  
    
    
}