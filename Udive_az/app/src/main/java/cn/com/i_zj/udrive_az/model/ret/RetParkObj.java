package cn.com.i_zj.udrive_az.model.ret;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import cn.com.i_zj.udrive_az.model.ParksResult;

/**
 * @author JayQiu
 * @create 2018/10/26
 * @Describe
 */
public class RetParkObj implements Serializable {
    private  int code;
    private  String message;
    @SerializedName("data")
    private  ParksResult.DataBean date;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ParksResult.DataBean getDate() {
        return date;
    }

    public void setDate(ParksResult.DataBean date) {
        this.date = date;
    }

}
