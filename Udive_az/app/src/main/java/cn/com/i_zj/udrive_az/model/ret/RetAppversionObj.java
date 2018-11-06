package cn.com.i_zj.udrive_az.model.ret;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import cn.com.i_zj.udrive_az.model.AppversionEntity;

/**
 * @author JayQiu
 * @create 2018/11/6
 * @Describe
 */
public class RetAppversionObj implements Serializable {
    private int code;
    private  String message;
    @SerializedName("data")
    private AppversionEntity data;

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

    public AppversionEntity getData() {
        return data;
    }

    public void setData(AppversionEntity data) {
        this.data = data;
    }
}
