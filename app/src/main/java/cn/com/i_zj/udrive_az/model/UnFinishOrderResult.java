package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class UnFinishOrderResult implements Serializable {

    /**
     * code : 1
     * message : 成功
     * data : {"preferentialAmount":null,"parkFee":null,"payTime":null,"discount":null,"discountAmount":null,"number":"536942869194621393000","shouldPayAmount":null,"carColor":null,"durationFee":null,"payType":null,"balance":null,"car":{"carColor":"白色","carType":0,"maxDistance":0,"plateNumber":"川AB34W7","brand":"大众宝来","carID":74},"backFree":0,"mileageFee":null,"deductibleStatus":1,"realPayAmount":null,"startTime":1547552781000,"id":856,"brand":null,"mileage":null,"order":{"number":"536942869194621393000","totalAmount":806,"reservationId":"reservation_car:3972","orderId":856,"mileageAmount":0,"deductible":500,"powerFlag":0,"timeAmount":306,"amountFlag":0},"ownerEarnAmount":null,"preferentialId":null,"durationTime":null,"bluePassword":"08215123","keyboardPassword":"87399208","plateNumber":"川AB34W7","userId":3972,"url":null,"carId":74,"destinationParkName":"菁蓉国际停车场","toPark":{"latitude":30.538637,"name":"菁蓉国际停车场","longtitude":104.062329,"parkID":93},"startParkName":"菁蓉国际停车场","stopedFree":null,"brandId":null,"deductible":null,"destinationParkId":93,"fromPark":{"latitude":30.538637,"name":"菁蓉国际停车场","longtitude":104.062329,"parkID":93},"endTime":null,"discountId":null,"startParkId":93,"user":{"realName":null,"identityCardNumber":null,"mobile":"13880008296","userId":3972},"status":0}
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
         * preferentialAmount : null
         * parkFee : null
         * payTime : null
         * discount : null
         * discountAmount : null
         * number : 536942869194621393000
         * shouldPayAmount : null
         * carColor : null
         * durationFee : null
         * payType : null
         * balance : null
         * car : {"carColor":"白色","carType":0,"maxDistance":0,"plateNumber":"川AB34W7","brand":"大众宝来","carID":74}
         * backFree : 0
         * mileageFee : null
         * deductibleStatus : 1
         * realPayAmount : null
         * startTime : 1547552781000
         * id : 856
         * brand : null
         * mileage : null
         * order : {"number":"536942869194621393000","totalAmount":806,"reservationId":"reservation_car:3972","orderId":856,"mileageAmount":0,"deductible":500,"powerFlag":0,"timeAmount":306,"amountFlag":0}
         * ownerEarnAmount : null
         * preferentialId : null
         * durationTime : null
         * bluePassword : 08215123
         * keyboardPassword : 87399208
         * plateNumber : 川AB34W7
         * userId : 3972
         * url : null
         * carId : 74
         * destinationParkName : 菁蓉国际停车场
         * toPark : {"latitude":30.538637,"name":"菁蓉国际停车场","longtitude":104.062329,"parkID":93}
         * startParkName : 菁蓉国际停车场
         * stopedFree : null
         * brandId : null
         * deductible : null
         * destinationParkId : 93
         * fromPark : {"latitude":30.538637,"name":"菁蓉国际停车场","longtitude":104.062329,"parkID":93}
         * endTime : null
         * discountId : null
         * startParkId : 93
         * user : {"realName":null,"identityCardNumber":null,"mobile":"13880008296","userId":3972}
         * status : 0
         */

        private float preferentialAmount;
        private Object parkFee;
        private long payTime;
        private Object discount;
        private float discountAmount;
        private String number;
        private float shouldPayAmount;
        private Object carColor;
        private Object durationFee;
        private Object payType;
        private Object balance;
        private CarBean car;
        private int backFree;
        private Object mileageFee;
        private int deductibleStatus;
        private float realPayAmount;
        private long startTime;
        private int id;
        private Object brand;
        private float mileage;
        private OrderBean order;
        private float ownerEarnAmount;
        private int preferentialId;
        private long durationTime;
        private String bluePassword;
        private String keyboardPassword;
        private String plateNumber;
        private int userId;
        private String url;
        private int carId;
        private String destinationParkName;
        private ToParkBean toPark;
        private String startParkName;
        private Object stopedFree;
        private int brandId;
        private Object deductible;
        private int destinationParkId;
        private FromParkBean fromPark;
        private long endTime;
        private int discountId;
        private int startParkId;
        private UserBean user;
        private int status;

        public float getPreferentialAmount() {
            return preferentialAmount;
        }

        public void setPreferentialAmount(float preferentialAmount) {
            this.preferentialAmount = preferentialAmount;
        }

        public Object getParkFee() {
            return parkFee;
        }

        public void setParkFee(Object parkFee) {
            this.parkFee = parkFee;
        }

        public long getPayTime() {
            return payTime;
        }

        public void setPayTime(long payTime) {
            this.payTime = payTime;
        }

        public Object getDiscount() {
            return discount;
        }

        public void setDiscount(Object discount) {
            this.discount = discount;
        }

        public float getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(float discountAmount) {
            this.discountAmount = discountAmount;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public float getShouldPayAmount() {
            return shouldPayAmount;
        }

        public void setShouldPayAmount(float shouldPayAmount) {
            this.shouldPayAmount = shouldPayAmount;
        }

        public Object getCarColor() {
            return carColor;
        }

        public void setCarColor(Object carColor) {
            this.carColor = carColor;
        }

        public Object getDurationFee() {
            return durationFee;
        }

        public void setDurationFee(Object durationFee) {
            this.durationFee = durationFee;
        }

        public Object getPayType() {
            return payType;
        }

        public void setPayType(Object payType) {
            this.payType = payType;
        }

        public Object getBalance() {
            return balance;
        }

        public void setBalance(Object balance) {
            this.balance = balance;
        }

        public CarBean getCar() {
            return car;
        }

        public void setCar(CarBean car) {
            this.car = car;
        }

        public int getBackFree() {
            return backFree;
        }

        public void setBackFree(int backFree) {
            this.backFree = backFree;
        }

        public Object getMileageFee() {
            return mileageFee;
        }

        public void setMileageFee(Object mileageFee) {
            this.mileageFee = mileageFee;
        }

        public int getDeductibleStatus() {
            return deductibleStatus;
        }

        public void setDeductibleStatus(int deductibleStatus) {
            this.deductibleStatus = deductibleStatus;
        }

        public float getRealPayAmount() {
            return realPayAmount;
        }

        public void setRealPayAmount(float realPayAmount) {
            this.realPayAmount = realPayAmount;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Object getBrand() {
            return brand;
        }

        public void setBrand(Object brand) {
            this.brand = brand;
        }

        public float getMileage() {
            return mileage;
        }

        public void setMileage(float mileage) {
            this.mileage = mileage;
        }

        public OrderBean getOrder() {
            return order;
        }

        public void setOrder(OrderBean order) {
            this.order = order;
        }

        public float getOwnerEarnAmount() {
            return ownerEarnAmount;
        }

        public void setOwnerEarnAmount(float ownerEarnAmount) {
            this.ownerEarnAmount = ownerEarnAmount;
        }

        public int getPreferentialId() {
            return preferentialId;
        }

        public void setPreferentialId(int preferentialId) {
            this.preferentialId = preferentialId;
        }

        public long getDurationTime() {
            return durationTime;
        }

        public void setDurationTime(long durationTime) {
            this.durationTime = durationTime;
        }

        public String getBluePassword() {
            return bluePassword;
        }

        public void setBluePassword(String bluePassword) {
            this.bluePassword = bluePassword;
        }

        public String getKeyboardPassword() {
            return keyboardPassword;
        }

        public void setKeyboardPassword(String keyboardPassword) {
            this.keyboardPassword = keyboardPassword;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getCarId() {
            return carId;
        }

        public void setCarId(int carId) {
            this.carId = carId;
        }

        public String getDestinationParkName() {
            return destinationParkName;
        }

        public void setDestinationParkName(String destinationParkName) {
            this.destinationParkName = destinationParkName;
        }

        public ToParkBean getToPark() {
            return toPark;
        }

        public void setToPark(ToParkBean toPark) {
            this.toPark = toPark;
        }

        public String getStartParkName() {
            return startParkName;
        }

        public void setStartParkName(String startParkName) {
            this.startParkName = startParkName;
        }

        public Object getStopedFree() {
            return stopedFree;
        }

        public void setStopedFree(Object stopedFree) {
            this.stopedFree = stopedFree;
        }

        public int getBrandId() {
            return brandId;
        }

        public void setBrandId(int brandId) {
            this.brandId = brandId;
        }

        public Object getDeductible() {
            return deductible;
        }

        public void setDeductible(Object deductible) {
            this.deductible = deductible;
        }

        public int getDestinationParkId() {
            return destinationParkId;
        }

        public void setDestinationParkId(int destinationParkId) {
            this.destinationParkId = destinationParkId;
        }

        public FromParkBean getFromPark() {
            return fromPark;
        }

        public void setFromPark(FromParkBean fromPark) {
            this.fromPark = fromPark;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public int getDiscountId() {
            return discountId;
        }

        public void setDiscountId(int discountId) {
            this.discountId = discountId;
        }

        public int getStartParkId() {
            return startParkId;
        }

        public void setStartParkId(int startParkId) {
            this.startParkId = startParkId;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
