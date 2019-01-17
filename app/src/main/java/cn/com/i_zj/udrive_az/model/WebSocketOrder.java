package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class WebSocketOrder implements Serializable {

    /**
     * id : 896
     * number : 536942871195559531002
     * carId : 74
     * plateNumber : 川AB34W7
     * carColor :
     * brand :
     * brandId : 0
     * userId : 3957
     * startParkId : 93
     * startParkName : 菁蓉国际停车场
     * destinationParkId : 93
     * destinationParkName : 菁蓉国际停车场
     * startTime : 2019-01-17 19:56:00
     * endTime : 2019-01-17 19:56:38
     * shouldPayAmount : 518
     * realPayAmount : 518
     * ownerEarnAmount : 0
     * payTime : 0
     * payType : 0
     * mileage : 0
     * durationTime : 1
     * deductibleStatus : 1
     * status : 1
     * discountId : 0
     * preferentialId : 0
     * parkFee : 0
     * deductible : 500
     * url :
     * mileageFee : 0
     * durationFee : 18
     * preferentialAmount : 0
     * discountAmount : 0
     * discount : null
     * user : {"userId":3957,"realName":"邱杰","mobile":"18081018114","identityCardNumber":"511023199110165073"}
     * car : {"id":74,"plateNumber":"川AB34W7","brand":"大众宝来","brandId":0,"power":0,"seatNumber":5,"carColor":"白色","carType":0,"carState":0,"mileageFee":128,"timeFee":18}
     * keyboardPassword :
     * bluePassword :
     * backFree : 0
     * stopedFree : 0
     * balance : 0
     * crossCityFree : 0
     */

    private int id;
    private String number;
    private int carId;
    private String plateNumber;
    private String carColor;
    private String brand;
    private int brandId;
    private int userId;
    private int startParkId;
    private String startParkName;
    private int destinationParkId;
    private String destinationParkName;
    private String startTime;
    private String endTime;
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
    private int parkFee;
    private int deductible;
    private String url;
    private int mileageFee;
    private int durationFee;
    private int preferentialAmount;
    private int discountAmount;
    private Object discount;
    private UserBean user;
    private CarBean car;
    private String keyboardPassword;
    private String bluePassword;
    private int backFree;
    private int stopedFree;
    private int balance;
    private int crossCityFree;

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

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
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

    public int getParkFee() {
        return parkFee;
    }

    public void setParkFee(int parkFee) {
        this.parkFee = parkFee;
    }

    public int getDeductible() {
        return deductible;
    }

    public void setDeductible(int deductible) {
        this.deductible = deductible;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMileageFee() {
        return mileageFee;
    }

    public void setMileageFee(int mileageFee) {
        this.mileageFee = mileageFee;
    }

    public int getDurationFee() {
        return durationFee;
    }

    public void setDurationFee(int durationFee) {
        this.durationFee = durationFee;
    }

    public int getPreferentialAmount() {
        return preferentialAmount;
    }

    public void setPreferentialAmount(int preferentialAmount) {
        this.preferentialAmount = preferentialAmount;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Object getDiscount() {
        return discount;
    }

    public void setDiscount(Object discount) {
        this.discount = discount;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public CarBean getCar() {
        return car;
    }

    public void setCar(CarBean car) {
        this.car = car;
    }

    public String getKeyboardPassword() {
        return keyboardPassword;
    }

    public void setKeyboardPassword(String keyboardPassword) {
        this.keyboardPassword = keyboardPassword;
    }

    public String getBluePassword() {
        return bluePassword;
    }

    public void setBluePassword(String bluePassword) {
        this.bluePassword = bluePassword;
    }

    public int getBackFree() {
        return backFree;
    }

    public void setBackFree(int backFree) {
        this.backFree = backFree;
    }

    public int getStopedFree() {
        return stopedFree;
    }

    public void setStopedFree(int stopedFree) {
        this.stopedFree = stopedFree;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getCrossCityFree() {
        return crossCityFree;
    }

    public void setCrossCityFree(int crossCityFree) {
        this.crossCityFree = crossCityFree;
    }

    public static class UserBean {
        /**
         * userId : 3957
         * realName : 邱杰
         * mobile : 18081018114
         * identityCardNumber : 511023199110165073
         */

        private int userId;
        private String realName;
        private String mobile;
        private String identityCardNumber;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getIdentityCardNumber() {
            return identityCardNumber;
        }

        public void setIdentityCardNumber(String identityCardNumber) {
            this.identityCardNumber = identityCardNumber;
        }
    }

    public static class CarBean {
        /**
         * id : 74
         * plateNumber : 川AB34W7
         * brand : 大众宝来
         * brandId : 0
         * power : 0
         * seatNumber : 5
         * carColor : 白色
         * carType : 0
         * carState : 0
         * mileageFee : 128
         * timeFee : 18
         */

        private int id;
        private String plateNumber;
        private String brand;
        private int brandId;
        private int power;
        private int seatNumber;
        private String carColor;
        private int carType;
        private int carState;
        private int mileageFee;
        private int timeFee;

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

        public int getBrandId() {
            return brandId;
        }

        public void setBrandId(int brandId) {
            this.brandId = brandId;
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

        public int getMileageFee() {
            return mileageFee;
        }

        public void setMileageFee(int mileageFee) {
            this.mileageFee = mileageFee;
        }

        public int getTimeFee() {
            return timeFee;
        }

        public void setTimeFee(int timeFee) {
            this.timeFee = timeFee;
        }
    }
}
