package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class AddressInfo implements Serializable {
    private String name;
    private String address;
    private double lng;
    private double lat;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
