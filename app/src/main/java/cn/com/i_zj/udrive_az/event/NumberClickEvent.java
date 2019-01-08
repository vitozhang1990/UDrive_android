package cn.com.i_zj.udrive_az.event;

/**
 * Created by wli on 2018/8/11.
 */

public class NumberClickEvent {
  public String number;
  public boolean isDelete = false;
  public NumberClickEvent(String number) {
    this.number =  number;
  }

  public NumberClickEvent(boolean isDelete) {
    this.isDelete =  isDelete;
  }
}
