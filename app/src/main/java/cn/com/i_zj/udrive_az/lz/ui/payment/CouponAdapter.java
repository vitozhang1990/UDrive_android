package cn.com.i_zj.udrive_az.lz.ui.payment;

import android.support.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.model.UnUseCouponResult;

/**
 * Time:2018/8/31
 * User:lizhen
 * Description:
 */

public class CouponAdapter extends BaseQuickAdapter<UnUseCouponResult.DataBean, BaseViewHolder> {
    public CouponAdapter(int layoutResId, @Nullable List<UnUseCouponResult.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UnUseCouponResult.DataBean item) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long time = 0;
        try {
            time = simpleDateFormat.parse(item.getDistribute_time()).getTime();
            time = time + item.getValidity() * 24 * 60 * 60 * 1000l;
            LogUtils.e( simpleDateFormat.format(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        helper.setText(R.id.tv_name, item.getName())
                .setText(R.id.tv_time, "有效期: "+item.getDistribute_time()+"至" + simpleDateFormat.format(time) + "")
                .setText(R.id.tv_msg, item.getDiscription())
                .setText(R.id.tv_money, String.format(Locale.getDefault(), "%2.2f 元", (item.getPreferential_amount() / 100f)));

    }
}
