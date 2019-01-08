package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author JayQiu
 * @create 2018/11/15
 * @Describe
 */
public class HomeActivityEntity implements Serializable {
    private ActivityInfo note;
    private ArrayList<ActivityInfo>  activitys;

    public ActivityInfo getNote() {
        return note;
    }

    public void setNote(ActivityInfo note) {
        this.note = note;
    }

    public ArrayList<ActivityInfo> getActivitys() {
        return activitys;
    }

    public void setActivitys(ArrayList<ActivityInfo> activitys) {
        this.activitys = activitys;
    }
}
