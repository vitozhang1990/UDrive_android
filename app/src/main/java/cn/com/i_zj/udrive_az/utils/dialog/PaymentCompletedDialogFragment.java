package cn.com.i_zj.udrive_az.utils.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.view.CarDetailLayout;
import cn.com.i_zj.udrive_az.view.CarPriceDetailLayout;

/**
 * Created by wli on 2018/8/12.
 */

public class PaymentCompletedDialogFragment extends BottomSheetDialogFragment {

  @BindView(R.id.payment_completed_layout_car_detail)
  CarDetailLayout carDetailLayout;

  @BindView(R.id.payment_completed_layout_car_price_detail)
  CarPriceDetailLayout carPriceLayout;

  @BindView(R.id.ayment_completed_tv_price)
  AppCompatTextView priceView;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View bottomSheetView = inflater.inflate(R.layout.fragment_payment_completed, container, false);
    ButterKnife.bind(this, bottomSheetView);
    return bottomSheetView;
  }

  @Override
  public void onStart() {
    super.onStart();
    ((FrameLayout) getView().getParent()).setBackgroundColor(Color.TRANSPARENT);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return super.onCreateDialog(savedInstanceState);
  }
}