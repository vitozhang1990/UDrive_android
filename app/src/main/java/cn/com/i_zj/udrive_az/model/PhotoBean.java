package cn.com.i_zj.udrive_az.model;

import java.io.Serializable;

public class PhotoBean implements Serializable {
    private String leftFrontBumper;
    private String rightFrontBumper;
    private String leftFrontDoor;
    private String rightFrontDoor;
    private String leftBackDoor;
    private String rightBackDoor;
    private String backBumper;

    public String getLeftFrontBumper() {
        return leftFrontBumper;
    }

    public void setLeftFrontBumper(String leftFrontBumper) {
        this.leftFrontBumper = leftFrontBumper;
    }

    public String getRightFrontBumper() {
        return rightFrontBumper;
    }

    public void setRightFrontBumper(String rightFrontBumper) {
        this.rightFrontBumper = rightFrontBumper;
    }

    public String getLeftFrontDoor() {
        return leftFrontDoor;
    }

    public void setLeftFrontDoor(String leftFrontDoor) {
        this.leftFrontDoor = leftFrontDoor;
    }

    public String getRightFrontDoor() {
        return rightFrontDoor;
    }

    public void setRightFrontDoor(String rightFrontDoor) {
        this.rightFrontDoor = rightFrontDoor;
    }

    public String getLeftBackDoor() {
        return leftBackDoor;
    }

    public void setLeftBackDoor(String leftBackDoor) {
        this.leftBackDoor = leftBackDoor;
    }

    public String getRightBackDoor() {
        return rightBackDoor;
    }

    public void setRightBackDoor(String rightBackDoor) {
        this.rightBackDoor = rightBackDoor;
    }

    public String getBackBumper() {
        return backBumper;
    }

    public void setBackBumper(String backBumper) {
        this.backBumper = backBumper;
    }
}
