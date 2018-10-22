package cn.com.i_zj.udrive_az.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccountInfoResult {

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public AccountData data;

    public static class AccountData {

        @SerializedName("userId")
        public int userId;

        @SerializedName("username")
        public String username;

        @SerializedName("depositState")
        public int depositState;

        @SerializedName("balance")
        public float balance;

        @SerializedName("idCardState")
        public int idCardState;

        @SerializedName("giveBalance")
        public int giveBalance;
        @SerializedName("driverState")
        public int driverState;
    }
}
