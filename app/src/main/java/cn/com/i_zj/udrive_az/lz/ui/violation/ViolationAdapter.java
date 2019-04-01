package cn.com.i_zj.udrive_az.lz.ui.violation;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.com.i_zj.udrive_az.R;

public class ViolationAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    ViolationAdapter(@Nullable List<String> data) {
        super(R.layout.item_violation, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setGone(R.id.recycler_top, helper.getLayoutPosition() == 0);
    }
}
