package cn.com.i_zj.udrive_az.lz.ui.order;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;
import java.util.Locale;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.OrderResult;
import cn.com.i_zj.udrive_az.utils.Constants;

public class OrderAdapter extends BaseQuickAdapter<OrderResult.OrderItem, BaseViewHolder> {
    public OrderAdapter(@Nullable List<OrderResult.OrderItem> data) {
        super(R.layout.item_order, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderResult.OrderItem item) {
        helper.setText(R.id.tv_time, String.format(Locale.getDefault(), "%tY-%<tm-%<td %<tR", item.startTime))
                .setText(R.id.tv_type, getStatus(item.status))
                .setTextColor(R.id.tv_type, item.status == 0 || item.status == 1 ? Color.parseColor("#FD4C0E") : Color.parseColor("#AFAFAF"))
                .setText(R.id.tv_start_address, String.format(Locale.getDefault(), "起：%s", item.startParkName))
                .setText(R.id.tv_end_address, String.format(Locale.getDefault(), "终：%s", item.destinationParkName));
        if (item.status == Constants.ORDER_MOVE) {
            helper.setVisible(R.id.tv_money, false);
        } else {
            helper.setVisible(R.id.tv_money, true);
            helper.setText(R.id.tv_money, String.format(Locale.getDefault(), "%.2f 元", item.realPayAmount / 100f));
        }
        helper.setGone(R.id.ll_desc, item.refuel || item.illegal);
        if (item.refuel && item.illegal) {
            helper.setText(R.id.tv_desc, "* 此订单有加油记录与违章记录");
        } else if (item.refuel) {
            helper.setText(R.id.tv_desc, "* 此订单有加油记录");
        } else if (item.illegal) {
            helper.setText(R.id.tv_desc, "* 此订单有违章记录");
        }
    }

    private String getStatus(int number) {
        if (number == Constants.ORDER_MOVE) {
            return mContext.getString(R.string.order_move);
        } else if (number == Constants.ORDER_WAIT_PAY) {
            return mContext.getString(R.string.order_wait_pay);
        } else if (number == Constants.ORDER_FINISH) {
            return mContext.getString(R.string.order_finish);
        } else {
            return "";
        }

    }
}
