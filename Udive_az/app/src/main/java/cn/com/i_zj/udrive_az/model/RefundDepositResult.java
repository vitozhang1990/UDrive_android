package cn.com.i_zj.udrive_az.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RefundDepositResult {

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public String data;
}
