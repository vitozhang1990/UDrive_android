package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class OrderBean implements Serializable {
    /**
     * number : 537008417094507287001
     * totalAmount : 600
     * reservationId : reservation_car:3987
     * orderId : 1202
     * mileageAmount : 0
     * deductible : 500
     * packageId : 2
     * powerFlag : 0
     * timeAmount : 0
     * packageName : 全国吉普6小时套餐
     * amountFlag : 0
     * packageAmount : 100
     */

    private String number;
    private int totalAmount;
    private String reservationId;
    private int orderId;
    private int mileageAmount;
    private int deductible;
    private int packageId;
    private int powerFlag;
    private int timeAmount;
    private String packageName;
    private int amountFlag;
    private int packageAmount;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
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

    public int getMileageAmount() {
        return mileageAmount;
    }

    public void setMileageAmount(int mileageAmount) {
        this.mileageAmount = mileageAmount;
    }

    public int getDeductible() {
        return deductible;
    }

    public void setDeductible(int deductible) {
        this.deductible = deductible;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public int getPowerFlag() {
        return powerFlag;
    }

    public void setPowerFlag(int powerFlag) {
        this.powerFlag = powerFlag;
    }

    public int getTimeAmount() {
        return timeAmount;
    }

    public void setTimeAmount(int timeAmount) {
        this.timeAmount = timeAmount;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getAmountFlag() {
        return amountFlag;
    }

    public void setAmountFlag(int amountFlag) {
        this.amountFlag = amountFlag;
    }

    public int getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(int packageAmount) {
        this.packageAmount = packageAmount;
    }
}
