package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class WebSocketPrice implements Serializable {

    private int orderId;
    private float mileageAmount;
    private float timeAmount;
    private float totalAmount;
    private int deductible;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public float getMileageAmount() {
        return mileageAmount;
    }

    public void setMileageAmount(float mileageAmount) {
        this.mileageAmount = mileageAmount;
    }

    public float getTimeAmount() {
        return timeAmount;
    }

    public void setTimeAmount(float timeAmount) {
        this.timeAmount = timeAmount;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getDeductible() {
        return deductible;
    }

    public void setDeductible(int deductible) {
        this.deductible = deductible;
    }
}
