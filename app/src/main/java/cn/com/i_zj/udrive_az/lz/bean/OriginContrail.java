package cn.com.i_zj.udrive_az.lz.bean;

import java.io.Serializable;

/**
 * @author jayqiu.
 * @description
 * @Created time 2018/12/17
 */
public class OriginContrail implements Serializable {

    /**
     * longitude : 104.1178316
     * latitude : 30.4174709
     * speed : 44
     * direction : 302
     * time : 1542952318000
     */

    private double longitude;
    private double latitude;
    private int speed;
    private int direction;
    private long time;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
