package cn.com.i_zj.udrive_az.login;

public class GotoLoginDialogEvent {

  public NextJump nextEvent;

  public enum NextJump {
    NONE, ACCOUNT_INFO_ACTIVITY, ORDER_ACTIVITY, MY_CAR_ACTIVITY, MONEY_ACTIVITY
  }

  public GotoLoginDialogEvent() {

  }

  public GotoLoginDialogEvent(NextJump nextEvent) {
    this.nextEvent = nextEvent;
  }
}
