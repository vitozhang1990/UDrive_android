package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class ToParkBean implements Serializable {
    /**
     * latitude : 30.538637
     * name : 菁蓉国际停车场
     * longtitude : 104.062329
     * parkID : 93
     */

    private double latitude;
    private String name;
    private double longtitude;
    private int parkID;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public int getParkID() {
        return parkID;
    }

    public void setParkID(int parkID) {
        this.parkID = parkID;
    }
}
