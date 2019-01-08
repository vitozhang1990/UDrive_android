package cn.com.i_zj.udrive_az.widget.urefresh;

/**
 * @author JayQiu
 * @create 2018/11/7
 * @Describe
 */
public enum ViewStatus {
    START("初始状态",0),
    REFRESHING("正在刷新",1), //这个状态由刷新框架设置
    END_REFRESHING("结束结束加载动画",2),
    END("结束",3);
    private String name;
    private int id;

    ViewStatus(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}