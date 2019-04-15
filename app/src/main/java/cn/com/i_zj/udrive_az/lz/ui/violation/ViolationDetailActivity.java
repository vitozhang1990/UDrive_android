package cn.com.i_zj.udrive_az.lz.ui.violation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.DBSBaseActivity;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.ui.payment.ActConfirmOrder;
import cn.com.i_zj.udrive_az.lz.ui.payment.ActOrderPayment;
import cn.com.i_zj.udrive_az.map.MapUtils;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
import cn.com.i_zj.udrive_az.model.ret.ViolationDetailObj;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.ScreenManager;
import cn.com.i_zj.udrive_az.web.WebActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ViolationDetailActivity extends DBSBaseActivity {

    @BindView(R.id.header_title)
    TextView header_title;
    @BindView(R.id.header_image)
    ImageView header_image;
    @BindView(R.id.viola_order_time)
    TextView orderTime;
    @BindView(R.id.viola_order_pn)
    TextView orderPn;
    @BindView(R.id.viola_order_fromto)
    TextView orderFromTo;
    @BindView(R.id.viola_time)
    TextView time;
    @BindView(R.id.viola_address)
    TextView address;
    @BindView(R.id.viola_action)
    TextView action;
    @BindView(R.id.viola_penalty)
    TextView penalty;
    @BindView(R.id.viola_city)
    TextView city;
    @BindView(R.id.btn_commit)
    Button commitBtn;

    private int id;
    private ViolationDetailObj violationDetail;
    private ViolationDetailObj.OrderInfo orderInfo;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_violation_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapUtils.statusBarColor(this);
        ScreenManager.getScreenManager().pushActivity(this);

        id = getIntent().getIntExtra("id", -1);
        if (id == -1) {
            showToast("获取到错误Id");
            finish();
            return;
        }

        header_title.setText("违章信息");
        header_image.setImageResource(R.mipmap.ic_service);

        getDetail();
    }

    @OnClick({R.id.header_left, R.id.header_right, R.id.viola_order_more, R.id.viola_peccancy, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left:
                finish();
                break;
            case R.id.header_right:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + getResources().getString(R.string.about_phone));
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.viola_order_more:
                if (orderInfo == null) {
                    showToast("尚未获取到订单信息");
                    return;
                }
                if (orderInfo.getState() == Constants.ORDER_WAIT_PAY) {// 待付款
                    Intent intent1 = new Intent(this, ActConfirmOrder.class);
                    intent1.putExtra(ActConfirmOrder.ORDER_NUMBER, orderInfo.getNumber());
                    intent1.putExtra(ActConfirmOrder.ORDER_ID, orderInfo.getOrderId());
                    startActivity(intent1);
                } else if (orderInfo.getState() == Constants.ORDER_FINISH) {// 已完成
                    Intent intent2 = new Intent(this, ActOrderPayment.class);
                    intent2.putExtra(ActOrderPayment.ORDER_NUMBER, orderInfo.getNumber());
                    intent2.putExtra(ActOrderPayment.ORDER_ID, orderInfo.getOrderId());
                    startActivity(intent2);
                }
                break;
            case R.id.viola_peccancy:
                WebActivity.startWebActivity(this, BuildConfig.DOMAIN + "/peccancy");
                break;
            case R.id.btn_commit:
                if (violationDetail == null
                        || violationDetail.getState() == 2
                        || violationDetail.getState() == 4) {
                    return;
                }
                Intent deal = new Intent();
                deal.setClass(this, ViolationDealActivity.class);
                deal.putExtra("data", violationDetail);
                startActivity(deal);
                break;
        }
    }

    private void getDetail() {
        showProgressDialog();
        UdriveRestClient.getClentInstance().getIllegal(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRetObj<ViolationDetailObj>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseRetObj<ViolationDetailObj> violationObjBaseRetObj) {
                        dissmisProgressDialog();
                        if (violationObjBaseRetObj == null || violationObjBaseRetObj.getCode() == 1) {
                            if (violationObjBaseRetObj.getDate() == null) {
                                return;
                            }
                            violationDetail = violationObjBaseRetObj.getDate();
                            if (violationDetail.getOrder() != null) {
                                orderInfo = violationDetail.getOrder();
                                orderTime.setText(String.format(Locale.getDefault(), "%s至%s",
                                        sdf1.format(new Date(orderInfo.getStartTime())),
                                        sdf1.format(new Date(orderInfo.getEndTime()))));
                                orderPn.setText(orderInfo.getPn());
                                orderFromTo.setText(String.format(Locale.getDefault(), "%s-%s",
                                        orderInfo.getStartPark(),
                                        orderInfo.getEndPark()));
                            }
                            if (violationDetail.getFen() > 0 && violationDetail.getMoney() > 0) {
                                penalty.setText(Html.fromHtml("罚款: <font color='#FF7C56'>"
                                        + violationDetail.getMoney() / 100 + "</font> 元 扣分: <font color='#FF7C56'>"
                                        + violationDetail.getFen() + " 分"));
                            } else if (violationDetail.getFen() > 0) {
                                penalty.setText(Html.fromHtml("扣分: <font color='#FF7C56'>" + violationDetail.getFen() + "</font> 分"));
                            } else if (violationDetail.getMoney() > 0) {
                                penalty.setText(Html.fromHtml("罚款: <font color='#FF7C56'>" + violationDetail.getMoney() / 100 + "</font> 元"));
                            } else {
                                penalty.setVisibility(View.GONE);
                            }

                            time.setText(String.format(Locale.getDefault(), "时间：%s",
                                    sdf2.format(new Date(violationDetail.getBreakTime()))));
                            address.setText(String.format(Locale.getDefault(), "地点：%s",
                                    violationDetail.getAddress()));
                            action.setText(String.format(Locale.getDefault(), "行为：%s",
                                    violationDetail.getDescription()));
                            city.setText(violationDetail.getCityName());

                            commitBtn.setEnabled(violationDetail.getState() == 1 || violationDetail.getState() == 3);
                            switch (violationDetail.getState()) {
                                case 1:
                                    commitBtn.setText("去处理");
                                    break;
                                case 2:
                                    commitBtn.setText("处理中");
                                    break;
                                case 3:
                                    commitBtn.setText("重新上传");
                                    break;
                                case 4:
                                    commitBtn.setText("已处理");
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dissmisProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        dissmisProgressDialog();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManager.getScreenManager().popActivity(this);
    }
}
