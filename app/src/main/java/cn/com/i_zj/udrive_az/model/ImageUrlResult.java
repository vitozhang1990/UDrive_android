package cn.com.i_zj.udrive_az.model;

import com.google.gson.annotations.SerializedName;

/**
 * Time:2018/8/16
 * User:lizhen
 * Description:
 */

public class ImageUrlResult {

  @SerializedName("code")
  public int code;

  @SerializedName("message")
  public String message;

  @SerializedName("data")
  public String data;
}
