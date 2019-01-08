package cn.com.i_zj.udrive_az.lz.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import cn.com.i_zj.udrive_az.R;

public class ScanBackgroundView extends View {

    private Paint paint;
    private int widthPixels;
    private int heightPixels;

    private int length;
    private int height;
    private int left;
    private int top;
    private int bottom;

    public ScanBackgroundView(Context context) {
        super(context);
    }

    public ScanBackgroundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScanBackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        widthPixels = getMeasuredWidth();
        heightPixels = getMeasuredHeight();
        left = widthPixels / 8;
        top = heightPixels / 6;
        bottom = heightPixels / 15;

        length = heightPixels / 30;
        height = heightPixels / 100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(getResources().getColor(R.color.scan_bg));
        canvas.drawRect(0, 0, widthPixels, top, paint);
        canvas.drawRect(0, top, left, heightPixels - top - bottom, paint);
        canvas.drawRect(widthPixels - left, top, widthPixels, heightPixels - top - bottom, paint);
        canvas.drawRect(0, heightPixels - top - bottom, widthPixels, heightPixels, paint);
        paint.setColor(Color.RED);
        //左上角
        canvas.drawRect(left, top, left + length, top + height, paint);
        canvas.drawRect(left, top, left + height, top + length, paint);
        //右上角
        canvas.drawRect(widthPixels - left - length, top, widthPixels - left, top + height, paint);
        canvas.drawRect(widthPixels - left - height, top, widthPixels - left, top + length, paint);
        //左下角
        canvas.drawRect(left, heightPixels - top - height - bottom, left + length, heightPixels - top - bottom, paint);
        canvas.drawRect(left, heightPixels - top - length - bottom, left + height, heightPixels - top - bottom, paint);
        //右下角
        canvas.drawRect(widthPixels - left - length, heightPixels - top - height - bottom, widthPixels - left, heightPixels - top - bottom, paint);
        canvas.drawRect(widthPixels - left - height, heightPixels - top - length - bottom, widthPixels - left, heightPixels - top - bottom, paint);
    }
}
