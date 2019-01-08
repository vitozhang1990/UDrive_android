package cn.com.i_zj.udrive_az.utils;

import java.util.List;

import cn.com.i_zj.udrive_az.model.GeoCoordinate;

/**
 * @author JayQiu
 * @create 2018/10/29
 * @Describe
 */
public class GetCenterPointFromListOfCoordinates {

    /**
     *  根据输入的地点坐标计算中心点
     * @param geoCoordinateList
     * @return
     */
    public static GeoCoordinate getCenterPoint(List<GeoCoordinate> geoCoordinateList) {
        int total = geoCoordinateList.size();
        double X = 0, Y = 0, Z = 0;
        for (GeoCoordinate g : geoCoordinateList) {
            double lat, lon, x, y, z;
            lat = g.getLatitude() * Math.PI / 180;
            lon = g.getLongitude() * Math.PI / 180;
            x = Math.cos(lat) * Math.cos(lon);
            y = Math.cos(lat) * Math.sin(lon);
            z = Math.sin(lat);
            X += x;
            Y += y;
            Z += z;
        }

        X = X / total;
        Y = Y / total;
        Z = Z / total;
        double Lon = Math.atan2(Y, X);
        double Hyp = Math.sqrt(X * X + Y * Y);
        double Lat = Math.atan2(Z, Hyp);
        return new GeoCoordinate(Lat * 180 / Math.PI, Lon * 180 / Math.PI);
    }

    /**
     * 根据输入的地点坐标计算中心点（适用于400km以下的场合）
     *
     * @param geoCoordinateList
     * @return
     */
    public static GeoCoordinate getCenterPoint400(List<GeoCoordinate> geoCoordinateList) {
        // 以下为简化方法（400km以内）
        int total = geoCoordinateList.size();
        double lat = 0, lon = 0;
        for (GeoCoordinate g : geoCoordinateList) {
            lat += g.getLatitude() * Math.PI / 180;
            lon += g.getLongitude() * Math.PI / 180;
        }
        lat /= total;
        lon /= total;
        return new GeoCoordinate(lat * 180 / Math.PI, lon * 180 / Math.PI);
    }

}
