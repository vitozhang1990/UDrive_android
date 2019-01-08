package cn.com.i_zj.udrive_az.overlay;

import android.content.Context;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkStep;

import java.util.ArrayList;
import java.util.List;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.utils.AMapUtil;


/**
 * 导航路线图层类。
 */
public class WalkRouteOverlay extends RouteOverlay {

    private WalkPath walkPath;
    private List<LatLonPoint> throughPointList;
    private List<Marker> throughPointMarkerList = new ArrayList<Marker>();
    private boolean throughPointMarkerVisible = true;
    private PolylineOptions mPolylineOptions;
    private PolylineOptions mPolylineOptionscolor;
    private Context mContext;
    private boolean isColorfulline = true;
    private List<LatLng> mLatLngsOfPath;


    public void setIsColorfulline(boolean iscolorfulline) {
        this.isColorfulline = iscolorfulline;
    }

    /**
     * 根据给定的参数，构造一个导航路线图层类对象。
     *
     * @param amap    地图对象。
     * @param path    导航路线规划方案。
     * @param context 当前的activity对象。
     */
    public WalkRouteOverlay(Context context, AMap amap, WalkPath path,
                            LatLonPoint start, LatLonPoint end, List<LatLonPoint> throughPointList) {
        super(context);
        mContext = context;
        mAMap = amap;
        this.walkPath = path;
        startPoint = AMapUtil.convertToLatLng(start);
        endPoint = AMapUtil.convertToLatLng(end);
        this.throughPointList = throughPointList;
    }

    /**
     * 添加驾车路线添加到地图上显示。
     */
    public void addToMap() {
        initPolylineOptions();
        try {
            if (mAMap == null) {
                return;
            }

            if (getRouteWidth() == 0 || walkPath == null) {
                return;
            }
            mLatLngsOfPath = new ArrayList<>();
            List<WalkStep> walkSteps = walkPath.getSteps();
            mPolylineOptions.add(startPoint);
            for (WalkStep step : walkSteps) {
                List<LatLonPoint> latlonPoints = step.getPolyline();
                addDrivingStationMarkers(step, convertToLatLng(latlonPoints.get(0)));
                for (LatLonPoint latlonpoint : latlonPoints) {
                    mPolylineOptions.add(convertToLatLng(latlonpoint));
                    mLatLngsOfPath.add(convertToLatLng(latlonpoint));
                }
            }
            mPolylineOptions.add(endPoint);
            if (startMarker != null) {
                startMarker.remove();
                startMarker = null;
            }
            if (endMarker != null) {
                endMarker.remove();
                endMarker = null;
            }
//            addStartAndEndMarker();
            addThroughPointMarker();
            showPolyline();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化线段属性
     */
    private void initPolylineOptions() {

        mPolylineOptions = null;

        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(getDriveColor()).width(getRouteWidth());
    }

    private void showPolyline() {
        addPolyLine(mPolylineOptions);
    }

    private void showcolorPolyline() {
        addPolyLine(mPolylineOptionscolor);
    }

    private int getcolor(String status) {

        if (status.equals("畅通")) {
            return Color.GREEN;
        } else if (status.equals("缓行")) {
            return Color.YELLOW;
        } else if (status.equals("拥堵")) {
            return Color.RED;
        } else if (status.equals("严重拥堵")) {
            return Color.parseColor("#990033");
        } else {
            return Color.parseColor("#537edc");
        }
    }

    public LatLng convertToLatLng(LatLonPoint point) {
        return new LatLng(point.getLatitude(), point.getLongitude());
    }

    /**
     * @param walkStep
     * @param latLng
     */
    private void addDrivingStationMarkers(WalkStep walkStep, LatLng latLng) {
        addStationMarker(new MarkerOptions()
                .position(latLng)
                .title("\u65B9\u5411:" + walkStep.getAction()
                        + "\n\u9053\u8DEF:" + walkStep.getRoad())
                .snippet(walkStep.getInstruction()).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(getDriveBitmapDescriptor()));
    }

    @Override
    protected LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new LatLng(startPoint.latitude, startPoint.longitude));
        b.include(new LatLng(endPoint.latitude, endPoint.longitude));
        if (this.throughPointList != null && this.throughPointList.size() > 0) {
            for (int i = 0; i < this.throughPointList.size(); i++) {
                b.include(new LatLng(
                        this.throughPointList.get(i).getLatitude(),
                        this.throughPointList.get(i).getLongitude()));
            }
        }
        return b.build();
    }

    public void setThroughPointIconVisibility(boolean visible) {
        try {
            throughPointMarkerVisible = visible;
            if (this.throughPointMarkerList != null
                    && this.throughPointMarkerList.size() > 0) {
                for (int i = 0; i < this.throughPointMarkerList.size(); i++) {
                    this.throughPointMarkerList.get(i).setVisible(visible);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void addThroughPointMarker() {
        if (this.throughPointList != null && this.throughPointList.size() > 0) {
            LatLonPoint latLonPoint = null;
            for (int i = 0; i < this.throughPointList.size(); i++) {
                latLonPoint = this.throughPointList.get(i);
                if (latLonPoint != null) {
                    throughPointMarkerList.add(mAMap
                            .addMarker((new MarkerOptions())
                                    .position(
                                            new LatLng(latLonPoint
                                                    .getLatitude(), latLonPoint
                                                    .getLongitude()))
                                    .visible(throughPointMarkerVisible)
                                    .icon(getThroughPointBitDes())
                                    .title("\u9014\u7ECF\u70B9")));
                }
            }
        }
    }

    private BitmapDescriptor getThroughPointBitDes() {
        return BitmapDescriptorFactory.fromResource(R.drawable.startpoit);

    }

    /**
     * 获取两点间距离
     *
     * @param start
     * @param end
     * @return
     */
    public static int calculateDistance(LatLng start, LatLng end) {
        double x1 = start.longitude;
        double y1 = start.latitude;
        double x2 = end.longitude;
        double y2 = end.latitude;
        return calculateDistance(x1, y1, x2, y2);
    }

    public static int calculateDistance(double x1, double y1, double x2, double y2) {
        final double NF_pi = 0.01745329251994329; // 弧度 PI/180
        x1 *= NF_pi;
        y1 *= NF_pi;
        x2 *= NF_pi;
        y2 *= NF_pi;
        double sinx1 = Math.sin(x1);
        double siny1 = Math.sin(y1);
        double cosx1 = Math.cos(x1);
        double cosy1 = Math.cos(y1);
        double sinx2 = Math.sin(x2);
        double siny2 = Math.sin(y2);
        double cosx2 = Math.cos(x2);
        double cosy2 = Math.cos(y2);
        double[] v1 = new double[3];
        v1[0] = cosy1 * cosx1 - cosy2 * cosx2;
        v1[1] = cosy1 * sinx1 - cosy2 * sinx2;
        v1[2] = siny1 - siny2;
        double dist = Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2]);

        return (int) (Math.asin(dist / 2) * 12742001.5798544);
    }


    //获取指定两点之间固定距离点
    public static LatLng getPointForDis(LatLng sPt, LatLng ePt, double dis) {
        double lSegLength = calculateDistance(sPt, ePt);
        double preResult = dis / lSegLength;
        return new LatLng((ePt.latitude - sPt.latitude) * preResult + sPt.latitude, (ePt.longitude - sPt.longitude) * preResult + sPt.longitude);
    }

    /**
     * 去掉DriveLineOverlay上的线段和标记。
     */
    @Override
    public void removeFromMap() {
        try {
            super.removeFromMap();
            if (this.throughPointMarkerList != null
                    && this.throughPointMarkerList.size() > 0) {
                for (int i = 0; i < this.throughPointMarkerList.size(); i++) {
                    this.throughPointMarkerList.get(i).remove();
                }
                this.throughPointMarkerList.clear();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}