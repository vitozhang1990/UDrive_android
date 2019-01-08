package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

/**
 * @author JayQiu
 * @create 2018/11/6
 * @Describe
 */
public class AppversionEntity implements Serializable {
    private String appVersion;
    private String appUrl;
    private String content;
    private int state;

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
