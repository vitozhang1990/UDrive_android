package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

/**
 * Created by liuwei on 2018/8/20.
 */

public class CreateOderBean implements Serializable {


    /**
     * code : 1
     * message : 成功
     * data : {"id":14,"number":"637338","carId":28,"plateNumber":"川A8888","userId":6,"startParkId":17,"startParkName":"天府停车场","destinationParkId":18,"destinationParkName":"华阳停车场","startTime":1531921748000,"endTime":0,"shouldPayAmount":0,"realPayAmount":1,"ownerEarnAmount":1,"payTime":1532103191000,"payType":2,"mileage":0,"durationTime":0,"deductibleStatus":0,"status":2,"discountId":0,"preferentialId":0}
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

    public static class DataBean implements Serializable {
        /**
         * id : 14
         * number : 637338
         * carId : 28
         * plateNumber : 川A8888
         * userId : 6
         * startParkId : 17
         * startParkName : 天府停车场
         * destinationParkId : 18
         * destinationParkName : 华阳停车场
         * startTime : 1531921748000
         * endTime : 0
         * shouldPayAmount : 0
         * realPayAmount : 1
         * ownerEarnAmount : 1
         * payTime : 1532103191000
         * payType : 2
         * mileage : 0
         * durationTime : 0
         * deductibleStatus : 0
         * status : 2
         * discountId : 0
         * preferentialId : 0
         */

        private int id;
        private String number;
        private int carId;
        private String plateNumber;
        private int userId;
        private int startParkId;
        private String startParkName;
        private int destinationParkId;
        private String destinationParkName;
        private long startTime;
        private int endTime;
        private int shouldPayAmount;
        private int realPayAmount;
        private int ownerEarnAmount;
        private long payTime;
        private int payType;
        private int mileage;
        private int durationTime;
        private int deductibleStatus;
        private int status;
        private int discountId;
        private int preferentialId;

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

        public String getPlateNumber() {
            return plateNumber;
        }

        public void setPlateNumber(String plateNumber) {
            this.plateNumber = plateNumber;
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

        public String getStartParkName() {
            return startParkName;
        }

        public void setStartParkName(String startParkName) {
            this.startParkName = startParkName;
        }

        public int getDestinationParkId() {
            return destinationParkId;
        }

        public void setDestinationParkId(int destinationParkId) {
            this.destinationParkId = destinationParkId;
        }

        public String getDestinationParkName() {
            return destinationParkName;
        }

        public void setDestinationParkName(String destinationParkName) {
            this.destinationParkName = destinationParkName;
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

        public long getPayTime() {
            return payTime;
        }

        public void setPayTime(long payTime) {
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
    }
}
