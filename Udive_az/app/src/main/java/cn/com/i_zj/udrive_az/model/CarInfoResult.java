package cn.com.i_zj.udrive_az.model;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuwei on 2018/8/12.
 */

public class CarInfoResult implements Serializable{


  /**
   * code : 1
   * message : 成功
   * data : [{"id":31,"plateNumber":"川A7777","brand":"兰博基尼","power":1,"seatNumber":5,"carColor":"蓝色","carType":1,"carState":0,"mileagePrice":0,"timeFee":0,"maxDistance":0,"parkId":18,"parkName":"华阳停车场","parkAddress":"天府公园旁"}]
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
     * id : 31
     * plateNumber : 川A7777
     * brand : 兰博基尼
     * power : 1
     * seatNumber : 5
     * carColor : 蓝色
     * carType : 1
     * carState : 0
     * mileagePrice : 0
     * timeFee : 0
     * maxDistance : 0
     * parkId : 18
     * parkName : 华阳停车场
     * parkAddress : 天府公园旁
     */

    private int id;
    private String plateNumber;
    private String brand;
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
    private boolean trafficControl;

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

    public boolean isTrafficControl() {
      return trafficControl;
    }

    public void setTrafficControl(boolean trafficControl) {
      this.trafficControl = trafficControl;
    }
  }
}
