package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;
import java.util.List;

public class ActivityResult implements Serializable {

    private int code;
    private String message;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        private String imgUrl;
        private String viewUrl;

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getViewUrl() {
            return viewUrl;
        }

        public void setViewUrl(String viewUrl) {
            this.viewUrl = viewUrl;
        }
    }
}
