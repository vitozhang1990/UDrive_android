package cn.com.i_zj.udrive_az.login;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;

import cn.com.i_zj.udrive_az.R;


/**
 * Created by wli on 2018/8/11.
 */

public class VerificationCodeEditText extends android.support.v7.widget.AppCompatEditText implements TextWatcher {

  private int mFigures = 6;//需要输入的位数
  private int mVerCodeMargin = 20;//验证码之间的间距
  private int mBottomSelectedColor;//底部选中的颜色
  private int mBottomNormalColor;//未选中的颜色
  private float mBottomLineHeight = 2;//底线的高度
  private int mSelectedBackgroundColor;//选中的背景颜色

  private int mCurrentPosition = 0;
  private int mEachRectLength = 0;//每个矩形的边长
  private Paint mSelectedBackgroundPaint;
  private Paint mNormalBackgroundPaint;
  private Paint mBottomSelectedPaint;
  private Paint mBottomNormalPaint;

  public VerificationCodeEditText(Context context) {
    this(context, null);
  }

  public VerificationCodeEditText(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VerificationCodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));//防止出现下划线
    initPaint();
    setFocusableInTouchMode(true);
    super.addTextChangedListener(this);
  }

  /**
   * 初始化paint
   */
  private void initPaint() {

    mBottomSelectedColor = getColor(R.color.colorAccent);
    mBottomNormalColor = getColor(R.color.colorPrimary);
    mBottomLineHeight = 2;
    mSelectedBackgroundColor = Color.TRANSPARENT;


    mSelectedBackgroundPaint = new Paint();
    mSelectedBackgroundPaint.setColor(mSelectedBackgroundColor);
    mNormalBackgroundPaint = new Paint();
    mNormalBackgroundPaint.setColor(getColor(android.R.color.transparent));

    mBottomSelectedPaint = new Paint();
    mBottomNormalPaint = new Paint();
    mBottomSelectedPaint.setColor(mBottomSelectedColor);
    mBottomNormalPaint.setColor(mBottomNormalColor);
    mBottomSelectedPaint.setStrokeWidth(mBottomLineHeight);
    mBottomNormalPaint.setStrokeWidth(mBottomLineHeight);
  }


  @Override
  final public void setCursorVisible(boolean visible) {
    super.setCursorVisible(false);//隐藏光标的显示
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthResult = 0, heightResult = 0;
    //最终的宽度
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    if (widthMode == MeasureSpec.EXACTLY) {
      widthResult = widthSize;
    } else {
      widthResult = getScreenWidth(getContext());
    }
    //每个矩形形的宽度
    mEachRectLength = (widthResult - (mVerCodeMargin * (mFigures - 1))) / mFigures;
    //最终的高度
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    if (heightMode == MeasureSpec.EXACTLY) {
      heightResult = heightSize;
    } else {
      heightResult = mEachRectLength;
    }
    setMeasuredDimension(widthResult, heightResult);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      requestFocus();
      setSelection(getText().length());
      return false;
    }
    return super.onTouchEvent(event);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    mCurrentPosition = getText().length();
    int width = mEachRectLength - getPaddingLeft() - getPaddingRight();
    int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    for (int i = 0; i < mFigures; i++) {
      canvas.save();
      int start = width * i + i * mVerCodeMargin;
      int end = width + start;
      //画一个矩形
      if (i == mCurrentPosition) {//选中的下一个状态
        canvas.drawRect(start, 0, end, height, mSelectedBackgroundPaint);
      } else {
        canvas.drawRect(start, 0, end, height, mNormalBackgroundPaint);
      }
      canvas.restore();
    }
    //绘制文字
    String value = getText().toString();
    for (int i = 0; i < value.length(); i++) {
      canvas.save();
      int start = width * i + i * mVerCodeMargin;
      float x = start + width / 2;
      TextPaint paint = getPaint();
      paint.setTextAlign(Paint.Align.CENTER);
      paint.setColor(getCurrentTextColor());
      Paint.FontMetrics fontMetrics = paint.getFontMetrics();
      float baseline = (height - fontMetrics.bottom + fontMetrics.top) / 2
        - fontMetrics.top;
      canvas.drawText(String.valueOf(value.charAt(i)), x, baseline, paint);
      canvas.restore();
    }
    //绘制底线
    for (int i = 0; i < mFigures; i++) {
      canvas.save();
      float lineY = height - mBottomLineHeight / 2;
      int start = width * i + i * mVerCodeMargin;
      int end = width + start;
      if (i < mCurrentPosition) {
        canvas.drawLine(start, lineY, end, lineY, mBottomSelectedPaint);
      } else {
        canvas.drawLine(start, lineY, end, lineY, mBottomNormalPaint);
      }
      canvas.restore();
    }
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    mCurrentPosition = getText().length();
    postInvalidate();
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    mCurrentPosition = getText().length();
    postInvalidate();
  }

  @Override
  public void afterTextChanged(Editable s) {
    mCurrentPosition = getText().length();
    postInvalidate();
    if (getText().length() == mFigures) {
    } else if (getText().length() > mFigures) {
      getText().delete(mFigures, getText().length());
    }
  }

  /**
   * 返回颜色
   */
  private int getColor(@ColorRes int color) {
    return ContextCompat.getColor(getContext(), color);
  }

  /**
   * 获取手机屏幕的宽度
   */
  static int getScreenWidth(Context context) {
    DisplayMetrics metrics = new DisplayMetrics();
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    wm.getDefaultDisplay().getMetrics(metrics);
    return metrics.widthPixels;
  }
}
