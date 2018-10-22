package cn.com.i_zj.udrive_az.map;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;

import java.util.List;

import cn.com.i_zj.udrive_az.R;
import cn.com.i_zj.udrive_az.overlay.DrivingRouteOverlay;

/**
 * Created by liuwei on 2018/9/5.
 */

public class NewDrivingRouteOverlay extends DrivingRouteOverlay {
    Context mContext;
    public NewDrivingRouteOverlay(Context arg0, AMap arg1, DrivePath arg2,
                                  LatLonPoint arg3, LatLonPoint arg4,List<LatLonPoint> throughPointList) {
        super(arg0, arg1, arg2, arg3, arg4,throughPointList);
        mContext = arg0;
    }
    //自定义结束位置marker
    @Override
    protected BitmapDescriptor getEndBitmapDescriptor() {
        return BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(mContext.getResources(), R.drawable.startpoit));//此处添加你的图标
    }
    //自定义开始位置marker
    @Override
    protected BitmapDescriptor getStartBitmapDescriptor() {
        return BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(mContext.getResources(), R.drawable.endpoit));//此处添加你的图标
    }
    //自定义路线颜色
    @Override
    protected int getDriveColor() {
        return Color.BLACK;
    }
}
