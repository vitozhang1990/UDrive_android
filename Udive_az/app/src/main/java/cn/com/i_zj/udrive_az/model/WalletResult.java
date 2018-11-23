package cn.com.i_zj.udrive_az.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wli on 2018/8/12.
 */

public class WalletResult {

  @SerializedName("code")
  public int code;

  @SerializedName("message")
  public String message;

  @SerializedName("data")
  public WalletData data;

  public static class WalletData {
    @SerializedName("userId")
    public int userId;

    @SerializedName("userBalance")
    public long userBalance;

    @SerializedName("preferentialAmount")
    public int preferentialAmount;

    @SerializedName("carIncome")
    public int carIncome;

    @SerializedName("giveBalance")
    public long giveBalance;
  }
}
