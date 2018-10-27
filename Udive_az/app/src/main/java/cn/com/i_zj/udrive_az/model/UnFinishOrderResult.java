package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class UnFinishOrderResult implements Serializable {


    /**
     * code : 1
     * message : 成功
     * data : {"id":2,"number":"20180001","carId":26,"userId":20180001,"startParkId":2,"destinationParkId":11,"startTime":1531039787000,"endTime":0,"shouldPayAmount":0,"realPayAmount":0,"ownerEarnAmount":0,"payTime":0,"payType":0,"mileage":0,"durationTime":0,"deductibleStatus":0,"status":0,"discountId":0,"preferentialId":0}
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

    public static class DataBean implements Serializable{
        /**
         * id : 2
         * number : 20180001
         * carId : 26
         * userId : 20180001
         * startParkId : 2
         * destinationParkId : 11
         * startTime : 1531039787000
         * endTime : 0
         * shouldPayAmount : 0
         * realPayAmount : 0
         * ownerEarnAmount : 0
         * payTime : 0
         * payType : 0
         * mileage : 0
         * durationTime : 0
         * deductibleStatus : 0
         * status : 0
         * discountId : 0
         * preferentialId : 0
         */

        private int id;
        private String number;
        private int carId;
        private int userId;
        private int startParkId;
        private int destinationParkId;
        private long startTime;
        private int endTime;
        private int shouldPayAmount;
        private int realPayAmount;
        private int ownerEarnAmount;
        private int payTime;
        private int payType;
        private int mileage;
        private int durationTime;
        private int deductibleStatus;
        private int status;
        private int discountId;
        private int preferentialId;
        private CarInfoEntity car;
        private  ParksResult.DataBean toPark;
        private  ParksResult.DataBean fromPark;
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public int getCarId() {
            return carId;
        }

        public void setCarId(int carId) {
            this.carId = carId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getStartParkId() {
            return startParkId;
        }

        public void setStartParkId(int startParkId) {
            this.startParkId = startParkId;
        }

        public int getDestinationParkId() {
            return destinationParkId;
        }

        public void setDestinationParkId(int destinationParkId) {
            this.destinationParkId = destinationParkId;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        public int getShouldPayAmount() {
            return shouldPayAmount;
        }

        public void setShouldPayAmount(int shouldPayAmount) {
            this.shouldPayAmount = shouldPayAmount;
        }

        public int getRealPayAmount() {
            return realPayAmount;
        }

        public void setRealPayAmount(int realPayAmount) {
            this.realPayAmount = realPayAmount;
        }

        public int getOwnerEarnAmount() {
            return ownerEarnAmount;
        }

        public void setOwnerEarnAmount(int ownerEarnAmount) {
            this.ownerEarnAmount = ownerEarnAmount;
        }

        public int getPayTime() {
            return payTime;
        }

        public void setPayTime(int payTime) {
            this.payTime = payTime;
        }

        public int getPayType() {
            return payType;
        }

        public void setPayType(int payType) {
            this.payType = payType;
        }

        public int getMileage() {
            return mileage;
        }

        public void setMileage(int mileage) {
            this.mileage = mileage;
        }

        public int getDurationTime() {
            return durationTime;
        }

        public void setDurationTime(int durationTime) {
            this.durationTime = durationTime;
        }

        public int getDeductibleStatus() {
            return deductibleStatus;
        }

        public void setDeductibleStatus(int deductibleStatus) {
            this.deductibleStatus = deductibleStatus;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getDiscountId() {
            return discountId;
        }

        public void setDiscountId(int discountId) {
            this.discountId = discountId;
        }

        public int getPreferentialId() {
            return preferentialId;
        }

        public void setPreferentialId(int preferentialId) {
            this.preferentialId = preferentialId;
        }

        public CarInfoEntity getCar() {
            return car;
        }

        public void setCar(CarInfoEntity car) {
            this.car = car;
        }

        public ParksResult.DataBean getToPark() {
            return toPark;
        }

        public void setToPark(ParksResult.DataBean toPark) {
            this.toPark = toPark;
        }

        public ParksResult.DataBean getFromPark() {
            return fromPark;
        }

        public void setFromPark(ParksResult.DataBean fromPark) {
            this.fromPark = fromPark;
        }
    }
}
