package cn.com.i_zj.udrive_az.lz.ui.deposit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.lz.bean.AliYajinEvent;
import cn.com.i_zj.udrive_az.lz.bean.WechatYajinEvent;

/**
 * 支付DialogFragment
 */
public class DepositDialogFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bottomSheetView = inflater.inflate(R.layout.layout_pay_dialog, container, false);
        ButterKnife.bind(this, bottomSheetView);
        return bottomSheetView;
    }

    @OnClick(R.id.pay_dialog_iv_weixin)
    public void onWeichatPayClick(View view) {
        EventBus.getDefault().post(new WechatYajinEvent());
        dismiss();
    }

    @OnClick(R.id.pay_dialog_iv_alipay)
    public void onAliPayClick(View view) {
        EventBus.getDefault().post(new AliYajinEvent());
        dismiss();
    }

}
