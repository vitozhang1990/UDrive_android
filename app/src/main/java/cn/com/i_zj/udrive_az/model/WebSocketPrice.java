package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class WebSocketPrice implements Serializable {

    /**
     * orderId : 1202
     * packageId : 2
     * packageName : 全国吉普6小时套餐
     * packageMileage : 50
     * packageDurationTime : 360
     * packageStartTime :
     * packageEndTime :
     * mileageAmount : 0
     * timeAmount : 0
     * packageAmount : 100
     * totalAmount : 600
     * deductible : 500
     * time : 1550713860205
     */

    private int orderId;
    private int packageId;
    private String packageName;
    private int packageMileage;
    private int packageDurationTime;
    private String packageStartTime;
    private String packageEndTime;
    private int mileageAmount;
    private int timeAmount;
    private int packageAmount;
    private int totalAmount;
    private int deductible;
    private long time;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPackageMileage() {
        return packageMileage;
    }

    public void setPackageMileage(int packageMileage) {
        this.packageMileage = packageMileage;
    }

    public int getPackageDurationTime() {
        return packageDurationTime;
    }

    public void setPackageDurationTime(int packageDurationTime) {
        this.packageDurationTime = packageDurationTime;
    }

    public String getPackageStartTime() {
        return packageStartTime;
    }

    public void setPackageStartTime(String packageStartTime) {
        this.packageStartTime = packageStartTime;
    }

    public String getPackageEndTime() {
        return packageEndTime;
    }

    public void setPackageEndTime(String packageEndTime) {
        this.packageEndTime = packageEndTime;
    }

    public int getMileageAmount() {
        return mileageAmount;
    }

    public void setMileageAmount(int mileageAmount) {
        this.mileageAmount = mileageAmount;
    }

    public int getTimeAmount() {
        return timeAmount;
    }

    public void setTimeAmount(int timeAmount) {
        this.timeAmount = timeAmount;
    }

    public int getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(int packageAmount) {
        this.packageAmount = packageAmount;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getDeductible() {
        return deductible;
    }

    public void setDeductible(int deductible) {
        this.deductible = deductible;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
