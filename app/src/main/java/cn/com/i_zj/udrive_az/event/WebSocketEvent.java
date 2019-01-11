package cn.com.i_zj.udrive_az.event;

public class WebSocketEvent {

    private int userId;

    public WebSocketEvent() {

    }

    public WebSocketEvent(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
