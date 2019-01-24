package cn.com.i_zj.udrive_az.event;

public class OrderFinishEvent {
    private boolean abc;

    public OrderFinishEvent() {
    }

    public OrderFinishEvent(boolean abc) {
        this.abc = abc;
    }

    public boolean isAbc() {
        return abc;
    }

    public void setAbc(boolean abc) {
        this.abc = abc;
    }
}
