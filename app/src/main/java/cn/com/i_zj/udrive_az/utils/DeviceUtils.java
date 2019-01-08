package cn.com.i_zj.udrive_az.utils;

import android.text.TextUtils;

import java.util.UUID;

/**
 * Created by wli on 2018/8/11.
 */

public class DeviceUtils {

  private static String deviceId = "";


  public static synchronized String getDeviceId() {
    if (!TextUtils.isEmpty(deviceId)) {
      return deviceId;
    }

    String data = LocalCacheUtils.getPersistentSettingString(Constants.SP_GLOBAL_NAME, Constants.SP_DEVICE_ID, "");
    if (!TextUtils.isEmpty(data)) {
      deviceId = data;
      return deviceId;
    }


    String uuid = UUID.randomUUID().toString();
    LocalCacheUtils.savePersistentSettingString(Constants.SP_GLOBAL_NAME, Constants.SP_DEVICE_ID, uuid);
    deviceId = uuid;
    return deviceId;
  }
}
