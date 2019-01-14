package cn.com.i_zj.udrive_az.lz.ui.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.model.CarInfoEntity;
import cn.com.i_zj.udrive_az.model.OrderDetailResult;
import cn.com.i_zj.udrive_az.network.UdriveRestAPI;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.CarTypeImageUtils;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import cn.com.i_zj.udrive_az.utils.ToolsUtils;
import cn.com.i_zj.udrive_az.web.WebActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author JayQiu
 * @create 2018/10/26
 * @Describe 订单支付---完成
 */
public class ActOrderPayment extends DBSBaseActivity {
    public static final String TITLE = "title";
    public static final String ORDER_NUMBER = "order_number";
    @BindView(R.id.iv_car)
    ImageView ivCar;
    @BindView(R.id.tv_car_number)
    TextView tvCarNumber;
    @BindView(R.id.tv_car_color)
    TextView tvCarColor;
    @BindView(R.id.tv_car_type)
    TextView tvCarType;
    @BindView(R.id.iv_ke_fu)
    ImageView ivKeFu;
    @BindView(R.id.tv_mileage)
    TextView tvMileage;
    @BindView(R.id.tv_duration_time)
    TextView tvDurationTime;
    @BindView(R.id.tv_real_pay_amount)
    TextView tvRealPayAmount;
    @BindView(R.id.tv_detail)
    TextView tvDetail;
    @BindView(R.id.iv_imag)
    ImageView mIvImage;
    private String title;
    private String orderNumber;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_order_payment;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getIntent().getStringExtra(TITLE);
        orderNumber = getIntent().getStringExtra(ORDER_NUMBER);
        initView();
        findTripOrders();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void findTripOrders() {
        showProgressDialog(true);
        UdriveRestClient.getClentInstance().tripOrderDetail(SessionManager.getInstance().getAuthorization(), orderNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderDetailResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(OrderDetailResult value) {
                        dissmisProgressDialog();
                        if (value == null || value.data == null) {
                            Toast.makeText(ActOrderPayment.this, "数据请求失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        showDate(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != e) {
                            ToastUtils.showShort(e.getMessage());
                        }
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showDate(OrderDetailResult value) {
        if (value.data != null) {

            if (!StringUtils.isEmpty(value.data.url)) {
                Glide.with(ActOrderPayment.this).load(value.data.url).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ToolsUtils.getWindowWidth(ActOrderPayment.this),
                                ToolsUtils.getWindowWidth(ActOrderPayment.this));
                        mIvImage.setLayoutParams(layoutParams);
                        return false;
                    }
                }).error(R.mipmap.pic_dingdan_complete).into(mIvImage);
            }


            tvRealPayAmount.setText((value.data.realPayAmount) / 100f + "");
            if (value.data.durationTime > 60) {
                tvDurationTime.setText(String.format(Locale.getDefault(), "时长(%.1f小时)", value.data.durationTime / 60f));
            } else {
                tvDurationTime.setText("时长(" + (value.data.durationTime) + "分钟)");
            }
            tvMileage.setText("里程(" + value.data.mileage + "公里)");
            tvCarNumber.setText(value.data.plateNumber + "");
            CarInfoEntity carInfoEntity = value.data.getCar();
            if (carInfoEntity != null) {
                tvCarType.setText(carInfoEntity.getBrand());
                tvCarColor.setText(carInfoEntity.getCarColor());
                Glide.with(ActOrderPayment.this).load(CarTypeImageUtils.getCarImageByBrand(carInfoEntity.getBrand(), carInfoEntity.getCarColor())).into(ivCar);
            }

        }

    }

    @OnClick(R.id.tv_detail)
    public void onClick() {
        WebActivity.startWebActivity(ActOrderPayment.this, UdriveRestAPI.DETAIL_URL + orderNumber, "费用明细");
    }
}