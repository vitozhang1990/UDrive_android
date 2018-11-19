package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

/**
 * @author JayQiu
 * @create 2018/11/13
 * @Describe
 */
public class ActivityInfo implements Serializable {
    private String id;
    private String title;
    private  String bgImg;
    private  String href;
    private  String remark;
    private  long startTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
