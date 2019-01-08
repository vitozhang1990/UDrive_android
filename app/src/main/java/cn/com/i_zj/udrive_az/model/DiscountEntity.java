package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

/**
 * @author JayQiu
 * @create 2018/10/26
 * @Describe  折扣信息
 */
public class DiscountEntity implements Serializable {

    /**
     * id : 1
     * createTime : 0
     * updateTime : 0
     * name : 7.5折扣卡
     * discountAmount : 0.75
     * validity : 0
     * remark :
     * state : 0
     * creator : 0
     * updator : 0
     */

    private int id;
    private int createTime;
    private int updateTime;
    private String name;
    private float discountAmount;
    private int validity;
    private String remark;
    private int state;
    private int creator;
    private int updator;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(float discountAmount) {
        this.discountAmount = discountAmount;
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public int getUpdator() {
        return updator;
    }

    public void setUpdator(int updator) {
        this.updator = updator;
    }
}
