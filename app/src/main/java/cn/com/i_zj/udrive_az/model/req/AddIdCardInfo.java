package cn.com.i_zj.udrive_az.model.req;

import java.io.Serializable;

/**
 * @author JayQiu
 * @create 2018/11/1
 * @Describe  上传身份信息
 */
public class AddIdCardInfo implements Serializable {
    private  String realName;//必有字段  备注：名称
    private String handCardPhoto;  //类型：String  必有字段  备注：手持URL
    private  String identityCardNumber;   //类型：String  必有字段  备注：身份证编码
    private  String identityCardPhotoFront;   //类型：String  必有字段  备注：正面URL
    private  String identityCardPhotoBehind;   //类型：String  必有字段  备注：反面URL
    private  String validaTime;   //类型：String  可有字段  备注：身份证有效期开始时间：20100808

    private  String expireTime; //类型：String  可有字段  备注：身份证有效期结束时间：20200808
    private  String address;    //类型：String  可有字段  备注：地址
    private  String sex;  //类型：String  可有字段  备注：性别：男|女
    private  String identityTime;   //类型：String  可有字段  备注：生日：20100808
    private  String nationality; //类型：String  可有字段  备注：民族
    private  String issue;   //类型：String  可有字段  备注：发证机关

    private String frontPic;  //本地的身份证前照片
    private String backPic;   //本地的身份证后照片
    private String detectionPic;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    public void setIdentityCardNumber(String identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
    }

    public String getIdentityCardPhotoFront() {
        return identityCardPhotoFront;
    }

    public void setIdentityCardPhotoFront(String identityCardPhotoFront) {
        this.identityCardPhotoFront = identityCardPhotoFront;
    }

    public String getIdentityCardPhotoBehind() {
        return identityCardPhotoBehind;
    }

    public void setIdentityCardPhotoBehind(String identityCardPhotoBehind) {
        this.identityCardPhotoBehind = identityCardPhotoBehind;
    }

    public String getValidaTime() {
        return validaTime;
    }

    public void setValidaTime(String validaTime) {
        this.validaTime = validaTime;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdentityTime() {
        return identityTime;
    }

    public void setIdentityTime(String identityTime) {
        this.identityTime = identityTime;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getHandCardPhoto() {
        return handCardPhoto;
    }

    public void setHandCardPhoto(String handCardPhoto) {
        this.handCardPhoto = handCardPhoto;
    }

    public String getFrontPic() {
        return frontPic;
    }

    public void setFrontPic(String frontPic) {
        this.frontPic = frontPic;
    }

    public String getBackPic() {
        return backPic;
    }

    public void setBackPic(String backPic) {
        this.backPic = backPic;
    }

    public String getDetectionPic() {
        return detectionPic;
    }

    public void setDetectionPic(String detectionPic) {
        this.detectionPic = detectionPic;
    }

}
