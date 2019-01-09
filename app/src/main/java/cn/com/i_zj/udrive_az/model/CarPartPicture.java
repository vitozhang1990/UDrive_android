package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

/**
 * photoName 上传七牛云的name
 * photoPath 本地路径
 * hasPhoto 是否有图片
 */
public class CarPartPicture implements Serializable {
    private String key;
    private int requestCode;
    private String photoName;
    private String photoPath;
    private boolean hasPhoto;

    public CarPartPicture(String key, int requestCode) {
        this.key = key;
        this.requestCode = requestCode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public boolean hasPhoto() {
        return hasPhoto;
    }

    public void setHasPhoto(boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }
}
