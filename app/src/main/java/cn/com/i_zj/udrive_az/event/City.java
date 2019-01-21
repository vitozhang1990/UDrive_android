package cn.com.i_zj.udrive_az.event;

public enum City {

    CHENGDU("成都", 0), DALI("大理", 1), LIJIANG("丽江", 2);

    private String name;
    private int index;

    City(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
