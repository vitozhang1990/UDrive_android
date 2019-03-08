package cn.com.i_zj.udrive_az.model.req;

import java.io.Serializable;

/**
 * @author JayQiu
 * @create 2018/11/1
 * @Describe
 */
public class AddDriverCardInfo implements Serializable {
    private String driverLicencePhotoMaster;                //类型：String  必有字段  备注：驾驶证正本URL
    private String driverLicencePhotoSlave;              //类型：String  必有字段  备注：驾驶证副本URL
    private String driverLicencePhotoMasterLocal;                //类型：String  必有字段  备注：驾驶证正本URL
    private String driverLicencePhotoSlaveLocal;              //类型：String  必有字段  备注：驾驶证副本URL
    private String driverType;            //类型：String  可有字段  备注：驾照类型：C1,C2
    private String validatTime;                 //类型：String  可有字段  备注：有效期开始时间:20100808
    private String expireTime;           //类型：String  可有字段  备注：有效期结束时间:20100808
    private String incTime;            //类型：String  可有字段  备注：驾照有效时长：一些驾照扫描只能返回时长不能返回有效期结束时间（如果有结束日期返回就不需要返回这个字段）
    private String issueTime;                  //类型：String  可有字段  备注：初次领证时间:20100808
    private String address;               //类型：String  可有字段  备注：驾照地址
    private String name;            //类型：String  可有字段  备注：姓名
    private String driverLicenceNumber;               //类型：String  可有字段  备注：驾驶证编号
    private String sex;              //类型：String  可有字段  备注：性别
    private String archiveNo;                 //类型：String  可有字段  备注：档案号

    public String getDriverLicencePhotoMaster() {
        return driverLicencePhotoMaster;
    }

    public void setDriverLicencePhotoMaster(String driverLicencePhotoMaster) {
        this.driverLicencePhotoMaster = driverLicencePhotoMaster;
    }

    public String getDriverLicencePhotoSlave() {
        return driverLicencePhotoSlave;
    }

    public void setDriverLicencePhotoSlave(String driverLicencePhotoSlave) {
        this.driverLicencePhotoSlave = driverLicencePhotoSlave;
    }

    public String getDriverLicencePhotoMasterLocal() {
        return driverLicencePhotoMasterLocal;
    }

    public void setDriverLicencePhotoMasterLocal(String driverLicencePhotoMasterLocal) {
        this.driverLicencePhotoMasterLocal = driverLicencePhotoMasterLocal;
    }

    public String getDriverLicencePhotoSlaveLocal() {
        return driverLicencePhotoSlaveLocal;
    }

    public void setDriverLicencePhotoSlaveLocal(String driverLicencePhotoSlaveLocal) {
        this.driverLicencePhotoSlaveLocal = driverLicencePhotoSlaveLocal;
    }

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    public String getValidatTime() {
        return validatTime;
    }

    public void setValidatTime(String validatTime) {
        this.validatTime = validatTime;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getIncTime() {
        return incTime;
    }

    public void setIncTime(String incTime) {
        this.incTime = incTime;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDriverLicenceNumber() {
        return driverLicenceNumber;
    }

    public void setDriverLicenceNumber(String driverLicenceNumber) {
        this.driverLicenceNumber = driverLicenceNumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getArchiveNo() {
        return archiveNo;
    }

    public void setArchiveNo(String archiveNo) {
        this.archiveNo = archiveNo;
    }
}
