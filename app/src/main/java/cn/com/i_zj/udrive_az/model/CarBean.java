package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class CarBean implements Serializable {
    /**
     * carColor : 白色
     * carType : 0
     * maxDistance : 0
     * plateNumber : 川AB34W7
     * brand : 大众宝来
     * carID : 74
     */

    private String carColor;
    private int carType;
    private int maxDistance;
    private String plateNumber;
    private String brand;
    private int carID;

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

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
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

    public int getCarID() {
        return carID;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }
}
