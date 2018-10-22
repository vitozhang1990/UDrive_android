package cn.com.i_zj.udrive_az.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CouponResult implements Serializable {


    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public OrderDetailResult.OrderItem data;

    public static class OrderItem {
        @SerializedName("id")
        public int id;

        @SerializedName("number")
        public String number;

        @SerializedName("carId")
        public int carId;

        @SerializedName("plateNumber")
        public String plateNumber;

        //车牌号码
        @SerializedName("userId")
        public int userId;

        //订单id
        @SerializedName("startParkId")
        public int startParkId;

        //订单号
        @SerializedName("startParkName")
        public String startParkName;

        //车辆id
        @SerializedName("destinationParkId")
        public int destinationParkId;

        //用户id
        @SerializedName("destinationParkName")
        public String destinationParkName;

        @SerializedName("startTime")
        public long startTime;

        @SerializedName("endTime")
        public long endTime;

        @SerializedName("shouldPayAmount")
        public int shouldPayAmount;

        @SerializedName("realPayAmount")
        public int realPayAmount;

        @SerializedName("ownerEarnAmount")
        public int ownerEarnAmount;

        @SerializedName("payTime")
        public long payTime;

        @SerializedName("payType")
        public int payType;

        @SerializedName("mileage")
        public int mileage;

        @SerializedName("durationTime")
        public long durationTime;

        @SerializedName("deductibleStatus")
        public int deductibleStatus;

        @SerializedName("status")
        public int status;

        @SerializedName("discountId")
        public int discountId;

        @SerializedName("preferentialId")
        public int preferentialId;

        @SerializedName("parkFee")
        public int parkFee;

        @SerializedName("deductible")
        public int deductible;

        @SerializedName("url")
        public String url;
    }


}
