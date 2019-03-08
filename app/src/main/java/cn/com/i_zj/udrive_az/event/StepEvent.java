package cn.com.i_zj.udrive_az.event;

import cn.com.i_zj.udrive_az.model.req.AddIdCardInfo;

public class StepEvent {
    private int step;
    private boolean success;
    private AddIdCardInfo addIdCardInfo;

    public StepEvent(int step) {
        this(step, true);
    }

    public StepEvent(int step, boolean success) {
        this.step = step;
        this.success = success;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public AddIdCardInfo getAddIdCardInfo() {
        return addIdCardInfo;
    }

    public void setAddIdCardInfo(AddIdCardInfo addIdCardInfo) {
        this.addIdCardInfo = addIdCardInfo;
    }
}
