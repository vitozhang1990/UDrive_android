package cn.com.i_zj.udrive_az.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cn.com.i_zj.udrive_az.App;

/**
 * Created by wli on 2018/8/11.
 */

public class LocalCacheUtils {
  public static String getPersistentSettingString(String name, String key, String defaultValue) {
    SharedPreferences settings = App.appContext.getSharedPreferences(name, 0);
    return settings.getString(key, defaultValue);
  }

  public static void savePersistentSettingString(String name, String key, String value) {
    SharedPreferences settings = App.appContext.getSharedPreferences(name, 0);
    SharedPreferences.Editor editor = settings.edit();
    editor.putString(key, value);
    editor.apply();
  }

  public static void removePersistentSetting(String name, String key) {
    SharedPreferences settings = App.appContext.getSharedPreferences(name, 0);
    SharedPreferences.Editor editor = settings.edit();
    editor.remove(key);
    editor.apply();
  }

  public static boolean getPersistentSettingBoolean(String name, String key) {
    return getPersistentSettingBoolean(name, key, false);
  }

  public static boolean getPersistentSettingBoolean(String name, String key, Boolean defaultValue) {
    SharedPreferences settings = App.appContext.getSharedPreferences(name, Context.MODE_PRIVATE);
    return settings.getBoolean(key, defaultValue);
  }

  public static void savePersistentSettingBoolean(String name, String key, Boolean value) {
    SharedPreferences settings = App.appContext.getSharedPreferences(name, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = settings.edit();
    editor.putBoolean(key, value);
    editor.apply();
  }

  public static int getPersistentSettingInteger(String name, String key) {
    SharedPreferences settings = App.appContext.getSharedPreferences(name, Context.MODE_PRIVATE);
    return settings.getInt(key, 0);
  }

  public static void savePersistentSettingInteger(String name, String key, int value) {
    SharedPreferences settings = App.appContext.getSharedPreferences(name, 0);
    SharedPreferences.Editor editor = settings.edit();
    editor.putInt(key, value);
    editor.apply();
  }

  public static long getPersistentSettingLong(String name, String key) {
    SharedPreferences settings = App.appContext.getSharedPreferences(name, Context.MODE_PRIVATE);
    return settings.getLong(key, 0);
  }

  public static void savePersistentSettingLong(String name, String key, long value) {
    SharedPreferences settings = App.appContext.getSharedPreferences(name, 0);
    SharedPreferences.Editor editor = settings.edit();
    editor.putLong(key, value);
    editor.apply();
  }

  public static void clearPersistent(String name) {
    SharedPreferences settings = App.appContext.getSharedPreferences(name, 0);
    SharedPreferences.Editor editor = settings.edit();
    editor.clear();
    editor.apply();
  }

  /**
   * 将对象储存到sharepreference
   *
   * @param key
   * @param device
   * @param <T>
   */
  public static <T> boolean saveDeviceData(String name, String key, T device) {
    SharedPreferences mSharedPreferences = App.appContext.getSharedPreferences(name, 0);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {   //Device为自定义类
      // 创建对象输出流，并封装字节流
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      // 将对象写入字节流
      oos.writeObject(device);
      // 将字节流编码成base64的字符串
      String oAuth_Base64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
      mSharedPreferences.edit().putString(key, oAuth_Base64).apply();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 将对象从shareprerence中取出来
   *
   * @param key
   * @param <T>
   * @return
   */
  public static <T> T getDeviceData(String name, String key) {
    SharedPreferences mSharedPreferences = App.appContext.getSharedPreferences(name, 0);
    T device = null;
    String productBase64 = mSharedPreferences.getString(key, null);
    if (productBase64 == null) {
      return null;
    }
    // 读取字节
    byte[] base64 = Base64.decode(productBase64.getBytes(), Base64.DEFAULT);
    // 封装到字节流
    ByteArrayInputStream bais = new ByteArrayInputStream(base64);
    try {
      // 再次封装
      ObjectInputStream bis = new ObjectInputStream(bais);
      // 读取对象
      device = (T) bis.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return device;
  }
}
