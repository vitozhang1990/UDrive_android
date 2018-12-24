package cn.com.i_zj.udrive_az.lz.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author jayqiu.
 * @description
 * @Created time 2018/12/17
 */
public class ParkRemark implements Serializable {
    private  String remark;
    private  String name;
    private ArrayList<ParkImage> imgs;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ParkImage> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<ParkImage> imgs) {
        this.imgs = imgs;
    }
}
