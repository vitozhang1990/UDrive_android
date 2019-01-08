package cn.com.i_zj.udrive_az.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.event.NumberClickEvent;

/**
 * Created by wli on 2018/8/11.
 * 数字键盘
 */

public class NumberSoftInputLayout extends LinearLayout {

  private Integer[] numberIds = new Integer[]{R.id.number_soft_tv_0,
    R.id.number_soft_tv_1,
    R.id.number_soft_tv_2,
    R.id.number_soft_tv_3,
    R.id.number_soft_tv_4,
    R.id.number_soft_tv_5,
    R.id.number_soft_tv_6,
    R.id.number_soft_tv_7,
    R.id.number_soft_tv_8,
    R.id.number_soft_tv_9};

  public NumberSoftInputLayout(Context context) {
    super(context);
    init(context, null);
  }

  public NumberSoftInputLayout(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public NumberSoftInputLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }


  private void init(Context context, AttributeSet attrs) {
    LayoutInflater.from(context).inflate(R.layout.layout_number_softinput, this);
    ButterKnife.bind(this, this);

    for (int i = 0; i < numberIds.length; i++) {
      findViewById(numberIds[i]).setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          EventBus.getDefault().post(new NumberClickEvent(((AppCompatTextView) view).getText().toString()));
        }
      });
    }

    findViewById(R.id.number_soft_tv_delete).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        EventBus.getDefault().post(new NumberClickEvent(true));
      }
    });
  }

}
