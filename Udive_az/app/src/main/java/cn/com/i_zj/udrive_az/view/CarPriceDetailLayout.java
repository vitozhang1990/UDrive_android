package cn.com.i_zj.udrive_az.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.CarResult;

/**
 * Created by wli on 2018/8/12.
 * @Describe 消费详情布局
 */

public class CarPriceDetailLayout extends LinearLayout {

  @BindView(R.id.car_price_detail_tv_mileage_title)
  AppCompatTextView mileageTitleView;

  @BindView(R.id.car_price_detail_tv_mileage)
  AppCompatTextView mileagePriceView;

  @BindView(R.id.car_price_detail_tv_time_title)
  AppCompatTextView timeTitleView;

  @BindView(R.id.car_price_detail_tv_time)
  AppCompatTextView timePriceView;

  @BindView(R.id.car_price_detail_tv_coupon)
  AppCompatTextView couponPriceView;


  private CarResult carResult;

  public CarPriceDetailLayout(Context context) {
    super(context);
    init(context, null);
  }

  public CarPriceDetailLayout(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public CarPriceDetailLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  public void setCarDetail(CarResult carResult) {
    this.carResult = carResult;
//    licenseView.setText(carResult.plateNumber);
//    brandView.setText(carResult.brand);
  }


  private void init(Context context, AttributeSet attrs) {
    LayoutInflater.from(context).inflate(R.layout.layout_car_price_detail, this);
    ButterKnife.bind(this, this);
  }
}
