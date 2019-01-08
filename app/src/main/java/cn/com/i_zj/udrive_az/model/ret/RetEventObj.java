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
    private boolean  hasNextPage;
    private List<ActivityInfo> list;

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public List<ActivityInfo> getList() {
        return list;
    }

    public void setList(List<ActivityInfo> list) {
        this.list = list;
    }
}
