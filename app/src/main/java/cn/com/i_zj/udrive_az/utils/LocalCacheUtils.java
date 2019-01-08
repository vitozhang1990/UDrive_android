package cn.com.i_zj.udrive_az.utils;

import android.content.Context;
import android.content.SharedPreferences;

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
}
