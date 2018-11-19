package cn.com.i_zj.udrive_az.model.ret;

import java.io.Serializable;
import java.util.List;

import cn.com.i_zj.udrive_az.model.ActivityInfo;

/**
 * @author JayQiu
 * @create 2018/11/15
 * @Describe
 */
public class RetEventObj implements Serializable {
    private boolean isLastPage;
    private List<ActivityInfo> list;

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public List<ActivityInfo> getList() {
        return list;
    }

    public void setList(List<ActivityInfo> list) {
        this.list = list;
    }
}
