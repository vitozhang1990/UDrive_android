package cn.com.i_zj.udrive_az.utils;


import cn.com.i_zj.udrive_az.App;

/**
 * Created by wli on 2018/8/11.
 */

public class UIUtils {
  public static int getPixelById(int dimensionId) {
    return App.appContext.getResources().getDimensionPixelSize(dimensionId);
  }

  public static int dp2px(final float dpValue) {
    final float scale = App.appContext.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  public static int px2dp(final float pxValue) {
    final float scale = App.appContext.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }
}
