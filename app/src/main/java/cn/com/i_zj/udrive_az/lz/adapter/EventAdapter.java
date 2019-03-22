package cn.com.i_zj.udrive_az.lz.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.com.i_zj.udrive_az.model.ActivityInfo;

/**
 * @author JayQiu
 * @create 2018/11/15
 * @Describe
 */
public class EventAdapter extends BaseQuickAdapter<ActivityInfo, BaseViewHolder> {

    public EventAdapter(int layoutResId, @Nullable List<ActivityInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ActivityInfo item) {

    }
}
