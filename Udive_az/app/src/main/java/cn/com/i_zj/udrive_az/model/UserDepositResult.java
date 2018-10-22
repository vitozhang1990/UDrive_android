package cn.com.i_zj.udrive_az.model;

import com.google.gson.annotations.SerializedName;

public class UserDepositResult {

    //{"code":1,"message":"成功","data":{"userId":0,"zmScore":0,"type":0,"amount":0,"payState":0,"payType":0,"createTime":0}}
    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public UserData data;

    public static class UserData {
        @SerializedName("userId")
        public int userId;
        @SerializedName("zmScore")
        public float zmScore;
        @SerializedName("type")
        public int type;
        @SerializedName("amount")
        public float amount;
        @SerializedName("payState")
        public int payState;
        @SerializedName("payType")
        public int payType;
        @SerializedName("createTime")
        public long createTime;
        @SerializedName("orderNum")
        public String orderNum;
    }

}
