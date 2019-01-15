package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class UserBean implements Serializable {
    /**
     * realName : null
     * identityCardNumber : null
     * mobile : 13880008296
     * userId : 3972
     */

    private String realName;
    private String identityCardNumber;
    private String mobile;
    private int userId;

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
