package cn.com.i_zj.udrive_az.lz.bean;

public class CameraEvent {

    private int code;//-1:错误,0:正面,1:反面
    private String path;

    public CameraEvent(int code, String path) {
        this.code = code;
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
