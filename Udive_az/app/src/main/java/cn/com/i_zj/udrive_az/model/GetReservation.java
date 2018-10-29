package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

/**
 * Created by liuwei on 2018/9/7.
 */

public class GetReservation implements Serializable {


    /**
     * code : 1
     * message : 成功
     * data : {"id":28,"plateNumber":"川A8888","brand":"baoma","power":0,"seatNumber":5,"carColor":"红色","carType":1,"carState":6,"orderNum":"","orderType":0,"reservationId":21,"userId":4,"name":"新通惠酒店地面停车场","address":"成都市青羊区青华路15号附8号","longitude":104.0380932,"latitude":30.668815,"createTime":1534649271000}
     */

    private int code;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean  implements Serializable{
        /**
         * id : 28
         * plateNumber : 川A8888
         * brand : baoma
         * power : 0
         * seatNumber : 5
         * carColor : 红色
         * carType : 1
         * carState : 6
         * orderNum :
         * orderType : 0
         * reservationId : 21
         * userId : 4
         * name : 新通惠酒店地面停车场
         * address : 成都市青羊区青华路15号附8号
         * longitude : 104.0380932
         * latitude : 30.668815
         * createTime : 1534649271000
         */

        private int id;
        private String plateNumber;
        private String brand;
        private int power;
        private int seatNumber;
        private String carColor;
        private int carType;
        private int carState;
        private String orderNum;
        private int orderType;
        private int reservationId;
        private int userId;
        private String name;
        private String address;
        private double longitude;
        private double latitude;
        private long createTime;
        private int remainderRange;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPlateNumber() {
            return plateNumber;
        }

        public void setPlateNumber(String plateNumber) {
            this.plateNumber = plateNumber;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }

        public int getSeatNumber() {
            return seatNumber;
        }

        public void setSeatNumber(int seatNumber) {
            this.seatNumber = seatNumber;
        }

        public String getCarColor() {
            return carColor;
        }

        public void setCarColor(String carColor) {
            this.carColor = carColor;
        }

        public int getCarType() {
            return carType;
        }

        public void setCarType(int carType) {
            this.carType = carType;
        }

        public int getCarState() {
            return carState;
        }

        public void setCarState(int carState) {
            this.carState = carState;
        }

        public String getOrderNum() {
            return orderNum;
        }

        public void setOrderNum(String orderNum) {
            this.orderNum = orderNum;
        }

        public int getOrderType() {
            return orderType;
        }

        public void setOrderType(int orderType) {
            this.orderType = orderType;
        }

        public int getReservationId() {
            return reservationId;
        }

        public void setReservationId(int reservationId) {
            this.reservationId = reservationId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getRemainderRange() {
            return remainderRange;
        }

        public void setRemainderRange(int remainderRange) {
            this.remainderRange = remainderRange;
        }
    }
}
