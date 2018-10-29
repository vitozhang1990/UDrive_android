package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

/**
 * Created by wo on 2018/9/2.
 */

public class PayOrderByBlanceResult implements Serializable {
    private int code;
    private String message;

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
}
