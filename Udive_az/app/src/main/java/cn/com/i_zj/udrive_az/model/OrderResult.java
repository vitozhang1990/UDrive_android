package cn.com.i_zj.udrive_az.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Time:2018/8/13
 * User:lizhen
 * Description:
 */

public class OrderResult {

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public List<OrderItem> orderList;

    public static class OrderItem {
        @SerializedName("startParkId")
        public int startParkId;
        @SerializedName("carColor")
        public  String carColor;
        @SerializedName("brand")
        public  String brand;
        @SerializedName("brandId")
        public  int brandId;

        @SerializedName("startParkName")
        public String startParkName;

        @SerializedName("destinationParkId")
        public int destinationParkId;

        @SerializedName("destinationParkName")
        public String destinationParkName;

        //车牌号码
        @SerializedName("plateNumber")
        public String plateNumber;

        //订单id
        @SerializedName("id")
        public int id;

        //订单号
        @SerializedName("number")
        public String number;

        //车辆id
        @SerializedName("carId")
        public int carId;

        //用户id
        @SerializedName("userId")
        public int userId;

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
    }
}
