package cn.com.i_zj.udrive_az.model.ret;

import java.io.Serializable;

public class ViolationCheck implements Serializable {
    private boolean exist;

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }
}
