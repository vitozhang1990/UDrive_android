package cn.com.i_zj.udrive_az.lz.ui.violation;

import android.support.annotation.Nullable;
import android.text.Html;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.ret.Violation;

public class ViolationAdapter extends BaseQuickAdapter<Violation, BaseViewHolder> {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    ViolationAdapter(@Nullable List<Violation> data) {
        super(R.layout.item_violation, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Violation item) {
        helper.setText(R.id.viola_item_pn, item.getPn());
        if (item.getFen() > 0 && item.getMoney() > 0) {
            helper.setText(R.id.viola_item_penalty, Html.fromHtml("罚款: <font color='#FF7C56'>"
                    + item.getMoney() / 100 + "</font> 元 扣分: <font color='#FF7C56'>"
                    + item.getFen() + " 分"));
        } else if (item.getFen() > 0) {
            helper.setText(R.id.viola_item_penalty, Html.fromHtml("扣分: <font color='#FF7C56'>" + item.getFen() + "</font> 分"));
        } else if (item.getMoney() > 0) {
            helper.setText(R.id.viola_item_penalty, Html.fromHtml("罚款: <font color='#FF7C56'>" + item.getMoney() / 100 + "</font> 元"));
        } else {
            helper.setGone(R.id.viola_item_penalty, false);
        }
        if (item.getBreakTime() > 0) {
            helper.setText(R.id.viola_item_time, sdf.format(new Date(item.getBreakTime())));
        } else {
            helper.setGone(R.id.viola_item_time, false);
        }
        switch (item.getState()) {
            case 1:
                helper.setText(R.id.viola_item_status, "待处理");
                break;
            case 2:
                helper.setText(R.id.viola_item_status, "处理中");
                break;
            case 3:
                helper.setText(R.id.viola_item_status, "重新上传");
                break;
            case 4:
                helper.setText(R.id.viola_item_status, "已处理");
                break;
            default:
                helper.setText(R.id.viola_item_status, "");
        }
        helper.setGone(R.id.recycler_top, helper.getLayoutPosition() == 0);
    }
}
