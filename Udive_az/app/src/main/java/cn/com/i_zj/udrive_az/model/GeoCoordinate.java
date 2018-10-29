package cn.com.i_zj.udrive_az.model;

import java.util.List;

/**
 * @author JayQiu
 * @create 2018/10/29
 * @Describe
 */
public class GeoCoordinate
{
    private double latitude;
    private double longitude;

    public GeoCoordinate() {
    }

    public GeoCoordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}

