package cn.com.i_zj.udrive_az.lz.ui.order;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;
import java.util.Locale;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.OrderResult;
import cn.com.i_zj.udrive_az.utils.Constants;

public class OrderAdapter extends BaseQuickAdapter<OrderResult.OrderItem, BaseViewHolder> {
    public OrderAdapter(int layoutResId, @Nullable List<OrderResult.OrderItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderResult.OrderItem item) {
        helper.setText(R.id.tv_part_name, item.startParkName + "-" + item.destinationParkName)
                .setText(R.id.tv_type, getStatus(item.status))
                .setText(R.id.tv_car_number, item.plateNumber)
                .setText(R.id.tv_car_color, item.carColor)
                .setText(R.id.tv_car_type, item.brand)
                .setText(R.id.tv_time, String.format(Locale.getDefault(), "%tm月%<te日 %<tR", item.startTime))
                .setTextColor(R.id.tv_type, item.status == 0 || item.status == 1 ? Color.RED : Color.GRAY);
        if (item.status == Constants.ORDER_MOVE) {
            helper.setVisible(R.id.tv_money, false);
        } else {
            helper.setVisible(R.id.tv_money, true);
            helper.setText(R.id.tv_money, String.format(Locale.getDefault(), "¥  %.2f", item.realPayAmount / 100f));
        }
        helper.setGone(R.id.iv_oil, item.refuel);
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
