package cn.com.i_zj.udrive_az.refuel;

import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.OilHistoryEntity;

public class RefuelAdapter extends BaseQuickAdapter<OilHistoryEntity, BaseViewHolder> {
    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd  HH:mm");
    public RefuelAdapter(List<OilHistoryEntity> data) {
        super(R.layout.item_refuel, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OilHistoryEntity item) {
        helper.setText(R.id.refuel_history_pn, item.getPn());
        if (item.getCreateTime() > 0) {
            helper.setText(R.id.refuel_history_time, sdf.format(new Date(item.getCreateTime())));
        } else {
            helper.setText(R.id.refuel_history_time, "00-00  00:00");
        }
        switch (item.getState()) {
            case 1:
                helper.setText(R.id.refuel_history_amount, "审核中");
                helper.setTextColor(R.id.refuel_history_amount, Color.parseColor("#AFAFAF"));
                break;
            case 2:
                helper.setTextColor(R.id.refuel_history_amount, Color.parseColor("#030303"));
                helper.setText(R.id.refuel_history_amount, String.format(Locale.getDefault(), "%.2f 元", item.getAmount() / 100f));
                break;
            case 3:
                helper.setText(R.id.refuel_history_amount, "失败");
                helper.setTextColor(R.id.refuel_history_amount, Color.parseColor("#AFAFAF"));
                break;
        }
    }
}
