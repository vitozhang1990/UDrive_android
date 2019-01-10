package cn.com.i_zj.udrive_az.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

public class MaskPierceView extends View {
    private int mScreenWidth;   // 屏幕的宽
    private int mScreenHeight;  // 屏幕的高
    private Paint mDstPaint, mSrcPaint, mStrokePaint, mSolidlinePaint;

    private int viewWidth;
    private int viewHeight;
    private float startX = 150;
    private float startY = 300;
    private float endX = 0;
    private float endY = 0;

    private RectF rectF, rectFStroke;
    private int color = Color.BLACK;
    private boolean showStroke;

    public MaskPierceView(Context context) {
        this(context, null);
    }

    public MaskPierceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);

        if (mScreenWidth == 0) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            mScreenWidth = dm.widthPixels;
            mScreenHeight = dm.heightPixels;
        }

        mDstPaint = new Paint();
        mSrcPaint = new Paint();
        mStrokePaint = new Paint();
        mSolidlinePaint = new Paint();

        mDstPaint.setColor(Color.YELLOW);

        mSrcPaint.setColor(Color.BLUE);
        mSrcPaint.setAlpha(160);
        mSrcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));

        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStrokeWidth(6);
        mStrokePaint.setColor(Color.parseColor("#7A7A7A"));
        mStrokePaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        mSolidlinePaint.setStyle(Paint.Style.STROKE);
        mSolidlinePaint.setAntiAlias(true);
        mSolidlinePaint.setStrokeWidth(6);
        mSolidlinePaint.setColor(Color.parseColor("#7A7A7A"));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;

        endX = viewWidth - 150;
        endY = viewHeight - 300;
        rectF = new RectF(startX, startY, endX, endY);
        rectFStroke = new RectF(startX, startY, endX, endY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(rectF, 50, 50, mDstPaint);
        //构建遮罩
        Bitmap bm = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        canvas1.drawRect(new RectF(0, 0, mScreenWidth, mScreenHeight), paint);

        canvas.drawBitmap(bm, 0, 0, mSrcPaint);

        if (showStroke) {
            canvas.drawRoundRect(rectFStroke, 50, 50, mStrokePaint);
        } else {
            canvas.drawRoundRect(rectFStroke, 50, 50, mSolidlinePaint);
        }
    }

    public void black(boolean black) {
        if (black) {
            mSrcPaint.setAlpha(255);
            showStroke = true;
            color = Color.parseColor("#1C1C1C");
        } else {
            mSrcPaint.setAlpha(160);
            showStroke = false;
            color = Color.BLACK;
        }
        invalidate();
    }
}