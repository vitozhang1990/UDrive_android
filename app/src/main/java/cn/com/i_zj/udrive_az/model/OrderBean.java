package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class OrderBean implements Serializable {
    /**
     * number : 536942869194621393000
     * totalAmount : 806
     * reservationId : reservation_car:3972
     * orderId : 856
     * mileageAmount : 0
     * deductible : 500
     * powerFlag : 0
     * timeAmount : 306
     * amountFlag : 0
     */

    private String number;
    private float totalAmount;
    private String reservationId;
    private int orderId;
    private float mileageAmount;
    private float deductible;
    private int powerFlag;
    private float timeAmount;
    private int amountFlag;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public float getDeductible() {
        return deductible;
    }

    public void setDeductible(float deductible) {
        this.deductible = deductible;
    }

    public int getPowerFlag() {
        return powerFlag;
    }

    public void setPowerFlag(int powerFlag) {
        this.powerFlag = powerFlag;
    }

    public int getAmountFlag() {
        return amountFlag;
    }

    public void setAmountFlag(int amountFlag) {
        this.amountFlag = amountFlag;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
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
}
