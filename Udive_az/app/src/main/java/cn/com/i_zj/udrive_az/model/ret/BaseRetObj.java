package cn.com.i_zj.udrive_az.model.ret;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author JayQiu
 * @create 2018/11/13
 * @Describe
 */
public class BaseRetObj<T> implements Serializable {
    private  int code  ;
    private  String message;
    @SerializedName("data")
    private  T date;

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

    public T getDate() {
        return date;
    }

    public void setDate(T date) {
        this.date = date;
    }
}
