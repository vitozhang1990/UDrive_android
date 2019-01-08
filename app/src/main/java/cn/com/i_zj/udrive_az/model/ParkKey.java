package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class ParkKey implements Serializable {

    private int id;
    private double longtitude;
    private double latitude;

    public ParkKey(int id, double longtitude, double latitude) {
        this.id = id;
        this.longtitude = longtitude;
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ParkKey)) {
            return false;
        }
        ParkKey parkKey = (ParkKey) obj;
        return parkKey.id == id && parkKey.longtitude == longtitude && parkKey.latitude == latitude;
    }
}
