package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;
import java.util.List;

public class AreaTagsResult implements Serializable {

    /**
     * code : 1
     * message : 成功
     * data : [{"id":1,"createTime":0,"updateTime":0,"code":"010001","name":"中和","cityId":2288,"longitude":104.062474,"latitude":30.539228},{"id":2,"createTime":0,"updateTime":0,"code":"010002","name":"华阳","cityId":2288,"longitude":104.0726408,"latitude":30.6907616},{"id":3,"createTime":0,"updateTime":0,"code":"010003","name":"石羊场","cityId":2288,"longitude":0,"latitude":0},{"id":4,"createTime":0,"updateTime":0,"code":"010004","name":"大源","cityId":2288,"longitude":0,"latitude":0},{"id":5,"createTime":0,"updateTime":0,"code":"004001","name":"华西坝","cityId":2282,"longitude":0,"latitude":0},{"id":6,"createTime":0,"updateTime":0,"code":"004002","name":"玉林","cityId":2282,"longitude":104.064736,"latitude":30.634852},{"id":7,"createTime":0,"updateTime":0,"code":"004003","name":"科华路","cityId":2282,"longitude":0,"latitude":0}]
     */

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
        /**
         * id : 1
         * createTime : 0
         * updateTime : 0
         * code : 010001
         * name : 中和
         * cityId : 2288
         * longitude : 104.062474
         * latitude : 30.539228
         */

        private int id;
        private int createTime;
        private int updateTime;
        private String code;
        private String name;
        private int cityId;
        private double longitude;
        private double latitude;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCreateTime() {
            return createTime;
        }

        public void setCreateTime(int createTime) {
            this.createTime = createTime;
        }

        public int getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(int updateTime) {
            this.updateTime = updateTime;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCityId() {
            return cityId;
        }

        public void setCityId(int cityId) {
            this.cityId = cityId;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }
}
