package cn.com.i_zj.udrive_az.map.adapter;

import com.chad.library.adapter.base.BaseViewHolder;

/**
 * Created by liuwei on 2018/4/19 2017/12/25.
 */

public interface OnGlobalListener {
    <T> void logic(BaseViewHolder helper, T item);
}
