package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class AddressInfo implements Serializable {
    private String title;
    private String name;
    private double lng;
    private double lat;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public boolean same(AddressInfo info) {
        if (lng == info.getLng() && lat == info.getLat()) {
            return true;
        }
        return false;
    }
}
