package cn.com.i_zj.udrive_az.model;

import com.google.gson.annotations.SerializedName;

public class WeichatPayOrder {

  @SerializedName("code")
  public int code;

  @SerializedName("message")
  public String message;

  @SerializedName("data")
  public WeiChatPayDetail data;

  public static class WeiChatPayDetail {
    @SerializedName("timestamp")
    public String timestamp;

    @SerializedName("prepayid")
    public String prepayid;

    @SerializedName("appid")
    public String appid;

    @SerializedName("partnerid")
    public String partnerid;

    @SerializedName("noncestr")
    public String noncestr;

    @SerializedName("sign")
    public String sign;

    @SerializedName("package")
    public String packageValue;
  }
}
