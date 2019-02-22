package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class ParkOutAmount implements Serializable {

    private int code;
    private String message;
    private Integer data;

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

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }
}
