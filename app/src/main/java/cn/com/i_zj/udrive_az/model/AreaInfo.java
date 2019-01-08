package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class AreaInfo implements Serializable {

    private double lng;
    private double lat;

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
