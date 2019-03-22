package cn.com.i_zj.udrive_az.lz.ui.payment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.bean.PaymentEvent;
import cn.com.i_zj.udrive_az.lz.util.SpannableStringUtil;
import cn.com.i_zj.udrive_az.lz.view.PaymentView;
import cn.com.i_zj.udrive_az.model.OrderDetailResult;
import cn.com.i_zj.udrive_az.utils.UIUtils;

/**
 * 付款PaymentDialogFragment
 */
public class PaymentDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    public static final int PAY_NONE = 0;
    public static final int PAY_YU_E = 1;
    public static final int PAY_ALI = 2;
    public static final int PAY_WECHAT = 3;


    private ImageView ivClose;
    private TextView tvTitle;
    private TextView tvSubMoneyCount;
    private TextView tvCoupon;
    private TextView tvMoneyCount;
    private PaymentView payYuE;
    private PaymentView payAliPay;
    private PaymentView payWechat;
    private Button btmCommit;

    private static final String ORDER_DETAIL = "order_detail";
    private static final String COUPON_DETAIL = "coupon_detail";

    public static PaymentDialogFragment getInstance(OrderDetailResult value) {
        PaymentDialogFragment paymentDialogFragment = new PaymentDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ORDER_DETAIL, value);
        paymentDialogFragment.setArguments(bundle);
        return paymentDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_lz_pay_ment_dialog, container, true);

        ivClose = layout.findViewById(R.id.iv_close);
        tvTitle = layout.findViewById(R.id.tv_title);
        tvSubMoneyCount = layout.findViewById(R.id.tv_sub_money_count);
        tvCoupon = layout.findViewById(R.id.tv_coupon);
        tvMoneyCount = layout.findViewById(R.id.tv_money_count);
        payYuE = layout.findViewById(R.id.pay_yu_e);
        payAliPay = layout.findViewById(R.id.pay_alipay);
        payWechat = layout.findViewById(R.id.pay_wechat);
        btmCommit = layout.findViewById(R.id.btn_commit);

        payYuE.setOnClickListener(this);
        payAliPay.setOnClickListener(this);
        payWechat.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        btmCommit.setOnClickListener(this);

        payYuE.setView(R.mipmap.ic_payment_yue, "余额", true);
        payAliPay.setView(R.mipmap.ic_payment_alipay, "支付宝", !payYuE.isCheck());
        payWechat.setView(R.mipmap.ic_payment_wechat, "微信", !payYuE.isCheck() && !payAliPay.isCheck());

        Bundle arguments = getArguments();
        OrderDetailResult orderDetailResult = (OrderDetailResult) arguments.getSerializable(ORDER_DETAIL);
        handleCoupon(orderDetailResult);
        return layout;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.pay_yu_e:
                select(PAY_YU_E);
                break;
            case R.id.pay_alipay:
                select(PAY_ALI);
                break;
            case R.id.pay_wechat:
                select(PAY_WECHAT);
                break;
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.btn_commit:
                handlePay(payMoney);
                break;
            default:
                break;
        }
    }

    private void handlePay(float count) {

        if (payYuE.isCheck()) {
            EventBus.getDefault().post(new PaymentEvent(0, count));
            dismiss();
        } else if (payAliPay.isCheck()) {
            EventBus.getDefault().post(new PaymentEvent(1, count));
            dismiss();
        } else if (payWechat.isCheck()) {
            EventBus.getDefault().post(new PaymentEvent(2, count));
            dismiss();
        } else {
            Toast.makeText(getContext(), "没有选择支付", Toast.LENGTH_SHORT).show();
        }

    }

    private void select(int position) {
        payYuE.setCheck(position == PAY_YU_E);
        payAliPay.setCheck(position == PAY_ALI);
        payWechat.setCheck(position == PAY_WECHAT);
    }
private float payMoney=0;
    private void handleCoupon(OrderDetailResult orderItem) {
        tvTitle.setText("支付 : " + orderItem.data.number);
        tvSubMoneyCount.setText(String.format(Locale.getDefault(), "%.2f 元", orderItem.data.shouldPayAmount / 100f));
        tvMoneyCount.setText("");
        SpannableString spannableString = SpannableStringUtil.setColorAndSizeSpan(String.format(Locale.getDefault(), "%.2f", orderItem.data.realPayAmount / 100f), Color.RED, UIUtils.dp2px(30));
        tvMoneyCount.append(spannableString);
        SpannableString yuan = SpannableStringUtil.setColorAndSizeSpan("元", Color.GRAY, UIUtils.dp2px(12));
        tvMoneyCount.append(yuan);
        tvCoupon.setText(String.format(Locale.getDefault(), "%.2f 元", (orderItem.data.realPayAmount - orderItem.data.shouldPayAmount) / 100f));
        payMoney = orderItem.data.realPayAmount;
    }


}
