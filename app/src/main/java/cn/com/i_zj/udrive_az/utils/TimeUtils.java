package cn.com.i_zj.udrive_az.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间相关工具类
 */
public class TimeUtils {
    public final static long MinLong                    = 60L * 1000L;
    public final static long HourLong                   = 60L * 60L * 1000L;
    public final static long DayLong                    = 24L * 60L * 60L * 1000L;
    public final static long MonthLong                  = 24L * 60L * 60L * 30L * 1000L;
    public final static long YearLong                   = 24L * 60L * 60L * 30L * 12L * 1000L;

    public final static String FORMAT_1                 = "M月d日";
    public final static String FORMAT_2                 = "MM月dd日";
    public final static String FORMAT_3                 = "yy年MM月dd日";
    public final static String FORMAT_4                 = "yyyy年MM月dd日";
    public final static String FORMAT_5                 = "M-d";
    public final static String FORMAT_6                 = "MM-dd";
    public final static String FORMAT_7                 = "yy-MM-dd";
    public final static String FORMAT_8                 = "yyyy-MM-dd";
    public final static String FORMAT_9                 = "yyyy年MM月";
    public final static String FORMAT_10                = "yyyy-MM";
    public final static String FORMAT_11                = "yyyy";

    public final static String FORMAT_WITH_TIME_1       = "M月d日 HH:mm";
    public final static String FORMAT_WITH_TIME_2       = "MM月dd日 HH:mm";
    public final static String FORMAT_WITH_TIME_3       = "yy年MM月dd日 HH:mm";
    public final static String FORMAT_WITH_TIME_4       = "yyyy年MM月dd日 HH:mm";
    public final static String FORMAT_WITH_TIME_5       = "HH:mm";
    public final static String FORMAT_WITH_TIME_6       = "M-d HH:mm";
    public final static String FORMAT_WITH_TIME_7       = "MM-dd HH:mm";
    public final static String FORMAT_WITH_TIME_8       = "yy-MM-dd HH:mm";
    public final static String FORMAT_WITH_TIME_9       = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_WITH_TIME_10      = "M月d日 HH:mm:ss";
    public final static String FORMAT_WITH_TIME_11      = "MM月dd日 HH:mm:ss";
    public final static String FORMAT_WITH_TIME_12      = "yy年MM月dd日 HH:mm:ss";
    public final static String FORMAT_WITH_TIME_13      = "yyyy年MM月dd日 HH:mm:ss";
    public final static String FORMAT_WITH_TIME_14      = "HH:mm:ss";
    public final static String FORMAT_WITH_TIME_15      = "M-d HH:mm:ss";
    public final static String FORMAT_WITH_TIME_16      = "MM-dd HH:mm:ss";
    public final static String FORMAT_WITH_TIME_17      = "yy-MM-dd HH:mm:ss";
    public final static String FORMAT_WITH_TIME_18      = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_WITH_TIME_19      = "MM/dd HH:mm";
    public final static String FORMAT_WITH_TIME_20      = "yy/MM/dd HH:mm";

    public static final String FORMAT_DATETIME_NONE_SSS = "yyyyMMddHHmmss";

    public static final String DEFAULT_FORMAT           = FORMAT_WITH_TIME_18;

    public static String timeStamp2String(long seconds, String format) {
        if (TextUtils.isEmpty(format)) {
            format = DEFAULT_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        return sdf.format(new Date(getMillis(seconds)));
    }

    public static String date2String(Date date, String format) {
        if (TextUtils.isEmpty(format)) {
            format = DEFAULT_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        return sdf.format(date);
    }

    public static Date string2Date(String dateStr, String format) {
        if (TextUtils.isEmpty(format)) {
            format = DEFAULT_FORMAT;
        }
        Date d;
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        try {
            sdf.setLenient(false);
            d = sdf.parse(dateStr);
        } catch (Exception e) {
            d = null;
        }
        return d;
    }

    public static boolean isToday(long time) {
        return isThisTime(time, FORMAT_8);
    }

    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(getMillis(time)));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }

    public static boolean isThisMonth(long time) {
        return isThisTime(time, FORMAT_10);
    }

    public static boolean isThisYear(long time) {
        return isThisTime(time, FORMAT_11);
    }

    public static boolean isThisTime(long time, String format) {
        Date date = new Date(getMillis(time));
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        String param = sdf.format(date);
        String now = sdf.format(new Date());
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

    public static long getMillis(long time) {
        String timeString = String.valueOf(time);
        if (timeString.length() > 10) {
            return time;
        } else {
            return time * 1000;
        }
    }
}