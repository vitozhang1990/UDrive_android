package cn.com.i_zj.udrive_az.utils;

import android.app.Activity;

import java.util.Enumeration;
import java.util.Stack;

/**
 * @author jayqiu
 * @description Activity 栈管理
 * @CreationTime 2015年12月17日 下午2:23:32
 */
public class ScreenManager {
    private static Stack<Activity> activityStack;
    private static ScreenManager instance;

    private ScreenManager() {

    }

    public static ScreenManager getScreenManager() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    // 退出栈顶Activity
    public int getSize() {
        return activityStack.size();
    }

    // 获得当前栈顶Activity
    public Activity currentActivity() {
        Activity activity = null;
        try {
            activity = activityStack.lastElement();
        } catch (Exception e) {
        }
        return activity;
    }

    // 将当前Activity推入栈中
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack();
        }
        activityStack.add(activity);
    }

    public void clearActivity() {
        if (activityStack != null) {
            activityStack.clear();
        }
    }

    // 退出栈中指定Activity
    public void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }

    public boolean isHaveActivity(Class cls) {
        Activity activity = currentActivity();
        if (activity == null) {
            return false;
        }
        Enumeration items = activityStack.elements();
        while (items.hasMoreElements()) // 显示枚举（stack ） 中的所有元素
            if (items.nextElement().getClass().equals(cls)) {
                return true;
            }

        return false;
    }

    // 移除一个activity
    public void popActivity(Activity activity) {
        if (activityStack != null && activityStack.size() > 0) {
            if (activity != null) {
                activity.finish();
                activityStack.remove(activity);
                activity = null;
            }
        }
    }

    public void popActivity(Class cls) {
        if (activityStack != null && activityStack.size() > 0) {
            Enumeration items = activityStack.elements();
            while (items.hasMoreElements()) { // 显示枚举（stack ） 中的所有元素
                try {
                    Activity activity = (Activity) items.nextElement();
                    if (activity.getClass().equals(cls)) {
                        activity.finish();
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    // 退
    // 退出栈中所有Activity
    public void popAllActivityExceptOne() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popActivity(activity);
        }
    }
}