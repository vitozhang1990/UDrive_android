package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class WebSocketPark implements Serializable {

    private int id;
    private String name;
    private double longitude;
    private double latitude;
    private int parkCountBalance;
    private int status;
    private long time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public int getParkCountBalance() {
        return parkCountBalance;
    }

    public void setParkCountBalance(int parkCountBalance) {
        this.parkCountBalance = parkCountBalance;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
