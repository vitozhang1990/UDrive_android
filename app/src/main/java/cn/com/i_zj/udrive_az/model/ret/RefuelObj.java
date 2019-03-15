package cn.com.i_zj.udrive_az.model.ret;

import java.io.Serializable;

public class RefuelObj implements Serializable {
    private int id;
    private String number;
    private String orderNumber;
    private String refuelBeforePhoto;
    private String refuelAfterPhoto;
    private String receiptPhoto;
    private String pnPhoto;
    private int state;
    private String remark;
    private int amount;
    private float fuel;
    private String pn;
    private String auditResult;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getRefuelBeforePhoto() {
        return refuelBeforePhoto;
    }

    public void setRefuelBeforePhoto(String refuelBeforePhoto) {
        this.refuelBeforePhoto = refuelBeforePhoto;
    }

    public String getRefuelAfterPhoto() {
        return refuelAfterPhoto;
    }

    public void setRefuelAfterPhoto(String refuelAfterPhoto) {
        this.refuelAfterPhoto = refuelAfterPhoto;
    }

    public String getReceiptPhoto() {
        return receiptPhoto;
    }

    public void setReceiptPhoto(String receiptPhoto) {
        this.receiptPhoto = receiptPhoto;
    }

    public String getPnPhoto() {
        return pnPhoto;
    }

    public void setPnPhoto(String pnPhoto) {
        this.pnPhoto = pnPhoto;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public float getFuel() {
        return fuel;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }
}
