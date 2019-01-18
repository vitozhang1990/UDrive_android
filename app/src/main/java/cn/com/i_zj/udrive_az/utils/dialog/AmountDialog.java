package cn.com.i_zj.udrive_az.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.WebSocketPrice;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;

public class AmountDialog extends Dialog {

    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.shichang)
    TextView shichang;
    @BindView(R.id.licheng)
    TextView licheng;
    @BindView(R.id.mianpei)
    TextView mianpei;
    @BindView(R.id.mianpei_layout)
    View mianpeiLayout;

    private Context mContext;
    private WebSocketPrice price;

    public AmountDialog(Context context) {
        super(context, R.style.UpdateDialogStytle);
        setCancelable(false);
        mContext = context;
        initView();
    }

    private void initView() {
        setCanceledOnTouchOutside(false);
        View view = View.inflate(mContext, R.layout.dialog_amount, null);
        ButterKnife.bind(this, view);
        setContentView(view);
    }

    public void setAmount(WebSocketPrice price) {
        total.setText("费用预估 " + price.getTotalAmount() / 100 + " 元");
        shichang.setText(price.getTimeAmount() / 100 + " 元");
        licheng.setText(price.getMileageAmount() / 100 + " 元");
        if (price.getDeductible() == 0) {
            mianpeiLayout.setVisibility(View.GONE);
        } else {
            mianpei.setText(price.getDeductible() / 100 + " 元");
        }
    }

    @OnClick(R.id.sure)
    void onClick() {
        dismiss();
    }
}
