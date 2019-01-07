package cn.com.i_zj.udrive_az.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        ParkType.Rectangle, ParkType.Circle, ParkType.Polygon
})

public @interface ParkType {
    String Rectangle = "rectangle";
    String Circle = "circle";
    String Polygon = "polygon";
}