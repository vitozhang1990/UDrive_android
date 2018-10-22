package cn.com.i_zj.udrive_az.login;

/**
 * Created by wli on 2018/8/12.
 */

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.i_zj.udrive_az.R;

public class CarDetailLayout extends LinearLayout {

  @BindView(R.id.car_detail_tv_picture)
  AppCompatImageView pictureView;

  @BindView(R.id.car_detail_tv_license)
  AppCompatTextView licenseView;

  @BindView(R.id.car_detail_tv_color)
  AppCompatTextView colorView;

  @BindView(R.id.car_detail_tv_brand)
  AppCompatTextView brandView;

  @BindView(R.id.car_detail_layout_service)
  LinearLayout serviceView;

  private CarResult carResult;

  public CarDetailLayout(Context context) {
    super(context);
    init(context, null);
  }

  public CarDetailLayout(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public CarDetailLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  public void setCarDetail(CarResult carResult) {
    this.carResult = carResult;
    licenseView.setText(carResult.plateNumber);
    brandView.setText(carResult.brand);
  }


  private void init(Context context, AttributeSet attrs) {
    LayoutInflater.from(context).inflate(R.layout.layout_car_detail, this);
    ButterKnife.bind(this, this);
  }
}
