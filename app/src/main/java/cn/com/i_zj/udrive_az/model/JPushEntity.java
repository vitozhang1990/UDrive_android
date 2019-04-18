package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

/**
 * @author JayQiu
 * @create 2018/11/16
 * @Describe
 */
public class JPushEntity implements Serializable {
    public static final int NOT_REDIRECT = 0;
    public static final int URL_REDIRECT = 1;
    public static final int APP_REDIRECT = 2;

    public static final String INDEX = "1000";
    public static final String ORDER = "1010";
    public static final String EVENT = "1020";
    public static final String VIOLA = "1070";

    private String redirect;
    private int redirectType;

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public int getRedirectType() {
        return redirectType;
    }

    public void setRedirectType(int redirectType) {
        this.redirectType = redirectType;
    }
}
