package cn.com.i_zj.udrive_az.model.ret;

import java.io.Serializable;

import cn.com.i_zj.udrive_az.model.OrderResult;

public class ViolationDetailObj implements Serializable {

    private int id;
    private String pn;
    private int fen;
    private int money;
    private String address;
    private String description;
    private int state;
    private String remark;
    private String processSheetPhoto;
    private OrderResult.OrderItem order;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public int getFen() {
        return fen;
    }

    public void setFen(int fen) {
        this.fen = fen;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getProcessSheetPhoto() {
        return processSheetPhoto;
    }

    public void setProcessSheetPhoto(String processSheetPhoto) {
        this.processSheetPhoto = processSheetPhoto;
    }

    public OrderResult.OrderItem getOrder() {
        return order;
    }

    public void setOrder(OrderResult.OrderItem order) {
        this.order = order;
    }
}
