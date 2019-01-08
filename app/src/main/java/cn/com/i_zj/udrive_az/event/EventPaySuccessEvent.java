package cn.com.i_zj.udrive_az.event;

public class EventPaySuccessEvent {

  public PayType payType;
  public PayMethod payMethod;

  /**
   * 支付类型
   * <p>
   * 比如余额、直接购买服务等
   */
  public enum PayType {
    BALANCE, // 余额
  }

  /**
   * 支付方式
   * <p>
   * 微信或者支付宝
   */
  public enum PayMethod {
    WEICHAT,
    ALI
  }

  public EventPaySuccessEvent(PayType payType, PayMethod payMethod) {

  }

}
