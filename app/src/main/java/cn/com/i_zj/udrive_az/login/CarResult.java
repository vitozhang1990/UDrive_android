package cn.com.i_zj.udrive_az.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wli on 2018/8/12.
 */

public class CarResult {

  @SerializedName("plateNumber")
  public String plateNumber;

  @SerializedName("brand")
  public String brand;

  @SerializedName("power")
  public int power;

  @SerializedName("seatNumber")
  public int seatNumber;

  @SerializedName("carColor")
  public String carColor;

  @SerializedName("carType")
  public int carType;

  @SerializedName("mileagePrice")
  public String mileagePrice;

  @SerializedName("timeFee")
  public String timeFee;

  @SerializedName("maxDistance")
  public String maxDistance;
}
