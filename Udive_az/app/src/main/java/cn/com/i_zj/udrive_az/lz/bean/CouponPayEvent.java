package cn.com.i_zj.udrive_az.lz.bean;

import cn.com.i_zj.udrive_az.model.UnUseCouponResult;

/**
 * Created by wo on 2018/9/2.
 */

public class CouponPayEvent {
    private UnUseCouponResult.DataBean result;

    public CouponPayEvent(UnUseCouponResult.DataBean result) {
        this.result = result;
    }

    public UnUseCouponResult.DataBean getResult() {
        return result;
    }

    public void setResult(UnUseCouponResult.DataBean result) {
        this.result = result;
    }
}
