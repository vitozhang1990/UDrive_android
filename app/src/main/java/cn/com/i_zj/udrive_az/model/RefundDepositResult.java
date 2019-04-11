package cn.com.i_zj.udrive_az.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;

import cn.com.i_zj.udrive_az.JsonDeserializeTest;

public class RefundDepositResult {

    private int code;
    private String message;
    @JsonAdapter(value = JsonDeserializeTest.class)
    private JsonObject data;

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

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }
}
