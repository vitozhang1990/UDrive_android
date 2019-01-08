package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuwei on 2018/8/12.
 */

public class ParksResult implements Serializable {

    /**
     * code : 1
     * message : 成功
     * data : [{"id":17,"address":"成都市青羊区青华路15号附8号","longitude":104.0380932,"latitude":30.668815,"parkCountBalance":5,"validCarCount":2,"name":"新通惠酒店地面停车场"},{"id":18,"address":"成都市青羊区白家塘街8号","longitude":104.0769518,"latitude":30.6797436,"parkCountBalance":5,"validCarCount":1,"name":"百家塘停车场地面停车场"},{"id":19,"address":"成都市青羊区一环路西一段175号","longitude":104.0477871,"latitude":30.6625405,"parkCountBalance":4,"validCarCount":2,"name":"百花潭公园地面停车场"},{"id":39,"address":"成都市青羊区下同仁路53号","longitude":104.0575287,"latitude":30.6702085,"parkCountBalance":6,"validCarCount":0,"name":"宽窄巷子地面停车场 "},{"id":40,"address":"青羊区西御街65号附2号","longitude":104.0683302,"latitude":30.6625827,"parkCountBalance":6,"validCarCount":0,"name":"西御街65号地面停车场"},{"id":41,"address":"成华区嘉陵江路8号","longitude":104.1505765,"latitude":30.6355895,"parkCountBalance":5,"validCarCount":0,"name":"龙之梦大酒店地面停车场"},{"id":42,"address":"成都市青羊区人民中路二段22号","longitude":104.0719488,"latitude":30.6741737,"parkCountBalance":5,"validCarCount":0,"name":"友豪罗曼大酒店停车场"}]
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
         * id : 17
         * address : 成都市青羊区青华路15号附8号
         * longitude : 104.0380932
         * latitude : 30.668815
         * parkCountBalance : 5
         * validCarCount : 2
         * name : 新通惠酒店地面停车场
         */

        private int id;
        private String address;

        private double longitude;
        // ------适配错误字段
        private double longtitude;
        private double latitude;
        private int parkCountBalance;
        private int validCarCount;
        private String name;
        private int areaTagId;
        private int cooperate;
        private int stoped;
        private int stopedAmount;
        private int stopInAmount;
        private int parkID;//可以忽略

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
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

        public int getParkCountBalance() {
            return parkCountBalance;
        }

        public void setParkCountBalance(int parkCountBalance) {
            this.parkCountBalance = parkCountBalance;
        }

        public int getValidCarCount() {
            return validCarCount;
        }

        public void setValidCarCount(int validCarCount) {
            this.validCarCount = validCarCount;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getLongtitude() {
            return longtitude;
        }

        public void setLongtitude(double longtitude) {
            this.longtitude = longtitude;
        }

        public int getAreaTagId() {
            return areaTagId;
        }

        public void setAreaTagId(int areaTagId) {
            this.areaTagId = areaTagId;
        }

        public int getCooperate() {
            return cooperate;
        }

        public void setCooperate(int cooperate) {
            this.cooperate = cooperate;
        }

        public int getStoped() {
            return stoped;
        }

        public void setStoped(int stoped) {
            this.stoped = stoped;
        }

        public int getStopedAmount() {
            return stopedAmount;
        }

        public void setStopedAmount(int stopedAmount) {
            this.stopedAmount = stopedAmount;
        }

        public int getStopInAmount() {
            return stopInAmount;
        }

        public void setStopInAmount(int stopInAmount) {
            this.stopInAmount = stopInAmount;
        }

        public int getParkID() {
            return parkID;
        }

        public void setParkID(int parkID) {
            this.parkID = parkID;
        }
    }
}
