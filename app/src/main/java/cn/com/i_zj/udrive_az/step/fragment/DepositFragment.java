package cn.com.i_zj.udrive_az.step.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.view.PaymentView;
import me.yokeyword.fragmentation.SupportFragment;

public class DepositFragment extends SupportFragment {

    @BindView(R.id.pay_alipay)
    PaymentView payAlipay;
    @BindView(R.id.pay_wechat)
    PaymentView payWechat;

    public static DepositFragment newInstance() {
        Bundle args = new Bundle();

        DepositFragment fragment = new DepositFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deposit_new, container, false);
        ButterKnife.bind(this, view);

        payWechat.setView(R.mipmap.ic_payment_wechat, "微信", true);
        payAlipay.setView(R.mipmap.ic_payment_alipay, "支付宝", false);
        return view;
    }

}
