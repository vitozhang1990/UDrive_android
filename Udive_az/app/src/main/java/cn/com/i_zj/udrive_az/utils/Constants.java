package cn.com.i_zj.udrive_az.utils;

/**
 * Created by wli on 2018/8/11.
 */

public class Constants {

  public static final String WEIXIN_APP_ID = "wx573f46942b7cffbf";

  public static final String SP_GLOBAL_NAME = "sp_global_name";

  public static final String SP_DEVICE_ID = "sp_device_id";
  public static final String SP_SESSION_MANAGER = "sp_session_manager";
  public static final String SP_ACCOUNT_INFO_MANAGER = "sp_account_info_manager";

  public static final String INTENT_KET_COMMENT_DATA = "intent_key_comment_data";

  public static final String INTENT_TITLE = "intent_title";
  public static final String INTENT_REGISTER_ID = "身份证认证";
  public static final String INTENT_DRIVER_INFO = "驾驶证认证";
  //手持身份证照片
  public static final String URL_HAND_CARD_PHOTO = "handCardPhoto";
  //身份证正面
  public static final String URL_IDENTITY_CARD_PHOTO_FRONT = "identityCardPhotoFront";
  //身份证反面
  public static final String URL_IDENTITY_CARD_PHOTO_BEHIND = "identityCardPhotoBehind";
  public static final String URL_BEAN = "bean";

  public static final String INTENT_KEY_BUNLD_PARK = "bunldPark";
  public static final String INTENT_KEY_RESERVE_DATA = "intent_key_reserver_data";
  public static final String INTENT_KEY_CAR_DATA = "intent_key_car_data";

  public static final String WEIXIN_PAY_TYPE_BALANCE = "pay_balance";

  //未认证
  public static final int ID_UN_AUTHORIZED = 0;
  //审核中
  public static final int ID_UNDER_REVIEW = 1;
  //认证成功
  public static final int ID_AUTHORIZED_SUCCESS = 2;
  //认证失败
  public static final int ID_AUTHORIZED_FAIL = 3;

  //行程中
  public static final int ORDER_MOVE = 0;
  //未付款
  public static final int ORDER_WAIT_PAY = 1;
  //已完成
  public static final int ORDER_FINISH = 2;
}
