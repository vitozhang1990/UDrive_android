package cn.com.i_zj.udrive_az.model.ret;

import java.io.Serializable;

public class Violation1 implements Serializable {

    /**
     * id : 1
     * createTime : 2019-03-26 09:30:39
     * updateTime : 0
     * pn : 1
     * address : 1
     * breakTime : 1553592634000
     * fen : 1
     * money : 1
     * description :
     * state : 2
     * remark :
     * auditTime : 0
     * auditor : 0
     * processSheetPhoto :
     * cityName :
     * manualEntry : false
     */

    private int id;
    private String createTime;
    private int updateTime;
    private String pn;
    private String address;
    private long breakTime;
    private int fen;
    private int money;
    private String description;
    private int state;
    private String remark;
    private int auditTime;
    private int auditor;
    private String processSheetPhoto;
    private String cityName;
    private boolean manualEntry;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(long breakTime) {
        this.breakTime = breakTime;
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

    public int getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(int auditTime) {
        this.auditTime = auditTime;
    }

    public int getAuditor() {
        return auditor;
    }

    public void setAuditor(int auditor) {
        this.auditor = auditor;
    }

    public String getProcessSheetPhoto() {
        return processSheetPhoto;
    }

    public void setProcessSheetPhoto(String processSheetPhoto) {
        this.processSheetPhoto = processSheetPhoto;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public boolean isManualEntry() {
        return manualEntry;
    }

    public void setManualEntry(boolean manualEntry) {
        this.manualEntry = manualEntry;
    }
}
