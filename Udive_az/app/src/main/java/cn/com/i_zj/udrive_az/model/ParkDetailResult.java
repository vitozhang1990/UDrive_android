package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;
import java.util.List;

import cn.com.i_zj.udrive_az.constant.ParkType;

public class ParkDetailResult implements Serializable {

    /**
     * code : 1
     * message : 成功
     * data : {"parkArea":{"id":49,"createTime":0,"updateTime":0,"parkId":93,"parkType":"rectangle","area":"[{\"lng\":104.061783,\"lat\":30.538231},{\"lng\":104.062942,\"lat\":30.538231},{\"lng\":104.062942,\"lat\":30.539008},{\"lng\":104.061783,\"lat\":30.539008}]","creator":0,"updator":0},"carVos":[{"id":74,"plateNumber":"川AB34W7","brand":"大众宝来","brandId":0,"power":0,"seatNumber":5,"carColor":"白色","carType":0,"carState":0,"mileagePrice":128,"timeFee":18,"maxDistance":0,"parkId":93,"parkName":"","parkAddress":"","deviceId":"211800110035","trafficControl":false,"longitude":104.0623462,"latitude":30.5383105,"direction":0},{"id":78,"plateNumber":"川AA1B36","brand":"大众宝来","brandId":0,"power":0,"seatNumber":5,"carColor":"白色","carType":0,"carState":0,"mileagePrice":128,"timeFee":18,"maxDistance":0,"parkId":93,"parkName":"成都欧尚华阳停车场（楼顶）","parkAddress":"剑南大道欧尚华阳店楼顶停车场","deviceId":"221800110025-1","trafficControl":false,"longitude":104.0623623,"latitude":30.5384045,"direction":0},{"id":84,"plateNumber":"川AW49G5","brand":"大众宝来","brandId":0,"power":0,"seatNumber":5,"carColor":"白色","carType":0,"carState":0,"mileagePrice":128,"timeFee":18,"maxDistance":0,"parkId":93,"parkName":"","parkAddress":"","deviceId":"221800110024-1","trafficControl":false,"longitude":104.0624104,"latitude":30.5385746,"direction":0},{"id":105,"plateNumber":"川AD04466","brand":"北汽LITE","brandId":0,"power":1,"seatNumber":2,"carColor":"红色","carType":0,"carState":0,"mileagePrice":0,"timeFee":40,"maxDistance":0,"parkId":93,"parkName":"","parkAddress":"","deviceId":"117512300005787-1","trafficControl":false,"longitude":104.0622731,"latitude":30.5384464,"direction":258},{"id":107,"plateNumber":"川AD18623","brand":"北汽LITE","brandId":0,"power":1,"seatNumber":2,"carColor":"黄色","carType":0,"carState":0,"mileagePrice":0,"timeFee":40,"maxDistance":0,"parkId":93,"parkName":"","parkAddress":"","deviceId":"117512300005427-1","trafficControl":false,"longitude":104.0511677,"latitude":30.4998314,"direction":0}]}
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
         * parkArea : {"id":49,"createTime":0,"updateTime":0,"parkId":93,"parkType":"rectangle","area":"[{\"lng\":104.061783,\"lat\":30.538231},{\"lng\":104.062942,\"lat\":30.538231},{\"lng\":104.062942,\"lat\":30.539008},{\"lng\":104.061783,\"lat\":30.539008}]","creator":0,"updator":0}
         * carVos : [{"id":74,"plateNumber":"川AB34W7","brand":"大众宝来","brandId":0,"power":0,"seatNumber":5,"carColor":"白色","carType":0,"carState":0,"mileagePrice":128,"timeFee":18,"maxDistance":0,"parkId":93,"parkName":"","parkAddress":"","deviceId":"211800110035","trafficControl":false,"longitude":104.0623462,"latitude":30.5383105,"direction":0},{"id":78,"plateNumber":"川AA1B36","brand":"大众宝来","brandId":0,"power":0,"seatNumber":5,"carColor":"白色","carType":0,"carState":0,"mileagePrice":128,"timeFee":18,"maxDistance":0,"parkId":93,"parkName":"成都欧尚华阳停车场（楼顶）","parkAddress":"剑南大道欧尚华阳店楼顶停车场","deviceId":"221800110025-1","trafficControl":false,"longitude":104.0623623,"latitude":30.5384045,"direction":0},{"id":84,"plateNumber":"川AW49G5","brand":"大众宝来","brandId":0,"power":0,"seatNumber":5,"carColor":"白色","carType":0,"carState":0,"mileagePrice":128,"timeFee":18,"maxDistance":0,"parkId":93,"parkName":"","parkAddress":"","deviceId":"221800110024-1","trafficControl":false,"longitude":104.0624104,"latitude":30.5385746,"direction":0},{"id":105,"plateNumber":"川AD04466","brand":"北汽LITE","brandId":0,"power":1,"seatNumber":2,"carColor":"红色","carType":0,"carState":0,"mileagePrice":0,"timeFee":40,"maxDistance":0,"parkId":93,"parkName":"","parkAddress":"","deviceId":"117512300005787-1","trafficControl":false,"longitude":104.0622731,"latitude":30.5384464,"direction":258},{"id":107,"plateNumber":"川AD18623","brand":"北汽LITE","brandId":0,"power":1,"seatNumber":2,"carColor":"黄色","carType":0,"carState":0,"mileagePrice":0,"timeFee":40,"maxDistance":0,"parkId":93,"parkName":"","parkAddress":"","deviceId":"117512300005427-1","trafficControl":false,"longitude":104.0511677,"latitude":30.4998314,"direction":0}]
         */

        private ParkAreaBean parkArea;
        private List<CarVosBean> carVos;

        public ParkAreaBean getParkArea() {
            return parkArea;
        }

        public void setParkArea(ParkAreaBean parkArea) {
            this.parkArea = parkArea;
        }

        public List<CarVosBean> getCarVos() {
            return carVos;
        }

        public void setCarVos(List<CarVosBean> carVos) {
            this.carVos = carVos;
        }

        public static class ParkAreaBean implements Serializable {
            /**
             * id : 49
             * createTime : 0
             * updateTime : 0
             * parkId : 93
             * parkType : rectangle
             * area : [{"lng":104.061783,"lat":30.538231},{"lng":104.062942,"lat":30.538231},{"lng":104.062942,"lat":30.539008},{"lng":104.061783,"lat":30.539008}]
             * creator : 0
             * updator : 0
             */

            private int id;
            private int createTime;
            private int updateTime;
            private int parkId;
            private @ParkType
            String parkType;
            private String area;
            private int creator;
            private int updator;

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

            public int getParkId() {
                return parkId;
            }

            public void setParkId(int parkId) {
                this.parkId = parkId;
            }

            @ParkType
            public String getParkType() {
                return parkType;
            }

            public void setParkType(@ParkType String parkType) {
                this.parkType = parkType;
            }

            public String getArea() {
                return area;
            }

            public void setArea(String area) {
                this.area = area;
            }

            public int getCreator() {
                return creator;
            }

            public void setCreator(int creator) {
                this.creator = creator;
            }

            public int getUpdator() {
                return updator;
            }

            public void setUpdator(int updator) {
                this.updator = updator;
            }
        }

        public static class CarVosBean implements Serializable {
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
             * mileagePrice : 128
             * timeFee : 18
             * maxDistance : 0
             * parkId : 93
             * parkName :
             * parkAddress :
             * deviceId : 211800110035
             * trafficControl : false
             * longitude : 104.0623462
             * latitude : 30.5383105
             * direction : 0
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
            private int mileagePrice;
            private int timeFee;
            private int maxDistance;
            private int parkId;
            private String parkName;
            private String parkAddress;
            private String deviceId;
            private boolean trafficControl;
            private double longitude;
            private double latitude;
            private int direction;

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

            public int getMileagePrice() {
                return mileagePrice;
            }

            public void setMileagePrice(int mileagePrice) {
                this.mileagePrice = mileagePrice;
            }

            public int getTimeFee() {
                return timeFee;
            }

            public void setTimeFee(int timeFee) {
                this.timeFee = timeFee;
            }

            public int getMaxDistance() {
                return maxDistance;
            }

            public void setMaxDistance(int maxDistance) {
                this.maxDistance = maxDistance;
            }

            public int getParkId() {
                return parkId;
            }

            public void setParkId(int parkId) {
                this.parkId = parkId;
            }

            public String getParkName() {
                return parkName;
            }

            public void setParkName(String parkName) {
                this.parkName = parkName;
            }

            public String getParkAddress() {
                return parkAddress;
            }

            public void setParkAddress(String parkAddress) {
                this.parkAddress = parkAddress;
            }

            public String getDeviceId() {
                return deviceId;
            }

            public void setDeviceId(String deviceId) {
                this.deviceId = deviceId;
            }

            public boolean isTrafficControl() {
                return trafficControl;
            }

            public void setTrafficControl(boolean trafficControl) {
                this.trafficControl = trafficControl;
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

            public int getDirection() {
                return direction;
            }

            public void setDirection(int direction) {
                this.direction = direction;
            }
        }
    }
}
