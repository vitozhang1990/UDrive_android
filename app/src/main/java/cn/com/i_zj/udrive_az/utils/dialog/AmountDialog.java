package cn.com.i_zj.udrive_az.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.map.adapter.GlobalAdapter;
import cn.com.i_zj.udrive_az.map.adapter.OnGlobalListener;
import cn.com.i_zj.udrive_az.map.adapter.RecyclerViewUtils;
import cn.com.i_zj.udrive_az.model.WebSocketPrice;

public class AmountDialog extends Dialog implements OnGlobalListener {

    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.amount_recycler)
    RecyclerView recycler;

    private Context mContext;
    private GlobalAdapter mAdapter;
    private List<KeyValue> keyValueList = new ArrayList<>();

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

        mAdapter = RecyclerViewUtils.initLiner(
                mContext, recycler,
                R.layout.item_amount, keyValueList,
                this, null);
    }

    public void setAmount(WebSocketPrice price) {
        if (total == null) {
            return;
        }
        keyValueList.clear();
        if (price.getPackageAmount() == 0) {
            keyValueList.add(new KeyValue("时长费",
                    String.format(Locale.getDefault(), "%.2f 元", price.getTimeAmount() / 100f)));
            keyValueList.add(new KeyValue("里程费",
                    String.format(Locale.getDefault(), "%.2f 元", price.getMileageAmount() / 100f)));
            if (price.getDeductible() != 0)
                keyValueList.add(new KeyValue("不计免赔",
                        String.format(Locale.getDefault(), "%.2f 元", price.getDeductible() / 100f)));
        } else {
            keyValueList.add(new KeyValue(price.getPackageName(),
                    String.format(Locale.getDefault(), "%.2f 元", price.getPackageAmount() / 100f)));
            if (price.getMileageAmount() != 0)
                keyValueList.add(new KeyValue("套餐外里程",
                        String.format(Locale.getDefault(), "%.2f 元", price.getMileageAmount() / 100f)));
            if (price.getTimeAmount() != 0)
                keyValueList.add(new KeyValue("套餐外时长",
                        String.format(Locale.getDefault(), "%.2f 元", price.getTimeAmount() / 100f)));
            if (price.getDeductible() != 0)
                keyValueList.add(new KeyValue("不计免赔",
                        String.format(Locale.getDefault(), "%.2f 元", price.getDeductible() / 100f)));
        }
        total.setText(String.format(Locale.getDefault(), "费用预估 %.2f 元", price.getTotalAmount() / 100f));
        if (mAdapter != null) mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.sure)
    void onClick() {
        dismiss();
    }

    @Override
    public <T> void logic(BaseViewHolder helper, T item) {
        KeyValue keyValue = (KeyValue) item;
        helper.setText(R.id.amount_key, keyValue.name);
        helper.setText(R.id.amount_value, keyValue.value);
    }

    private class KeyValue {
        public String name;
        public String value;

        KeyValue(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
