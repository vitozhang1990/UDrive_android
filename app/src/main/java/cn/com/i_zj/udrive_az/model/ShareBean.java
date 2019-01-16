package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class ShareBean implements Serializable {

    private String shareUrl;
    private String shareImage;
    private String shareTitle;
    private String shareDescr;

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareImage() {
        return shareImage;
    }

    public void setShareImage(String shareImage) {
        this.shareImage = shareImage;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareDescr() {
        return shareDescr;
    }

    public void setShareDescr(String shareDescr) {
        this.shareDescr = shareDescr;
    }
}
