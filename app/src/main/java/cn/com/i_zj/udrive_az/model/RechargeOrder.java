package cn.com.i_zj.udrive_az.model;


import com.google.gson.annotations.SerializedName;

public class RechargeOrder {

  @SerializedName("code")
  public int code;

  @SerializedName("message")
  public String message;

  @SerializedName("data")
  public RechargeOrderItem orderItem;

  public static class RechargeOrderItem {

    @SerializedName("id")
    public int id;

    @SerializedName("createTime")
    public String createTime;

    @SerializedName("updateTime")
    public String updateTime;

    @SerializedName("number")
    public String number;

    @SerializedName("userId")
    public int userId;

    @SerializedName("userFundId")
    public int userFundId;

    @SerializedName("amount")
    public int amount;

    @SerializedName("state")
    public int state;

    @SerializedName("payType")
    public int payType;

    @SerializedName("payTime")
    public int payTime;
  }
}
