package cn.com.i_zj.udrive_az.lz.bean;

/**
 * Created by wo on 2018/9/2.
 */

public class PaymentEvent {
    private int type;
    private float count;

    public PaymentEvent(int type, float count) {
        this.type = type;
        this.count = count;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }
}
