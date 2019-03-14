package cn.com.i_zj.udrive_az.network;


import java.util.List;
import java.util.Map;

import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.lz.bean.OriginContrail;
import cn.com.i_zj.udrive_az.lz.bean.ParkRemark;
import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.model.ActivityResult;
import cn.com.i_zj.udrive_az.model.AliPayOrder;
import cn.com.i_zj.udrive_az.model.AreaTagsResult;
import cn.com.i_zj.udrive_az.model.AuthResult;
import cn.com.i_zj.udrive_az.model.CarInfoResult;
import cn.com.i_zj.udrive_az.model.CheckCarResult;
import cn.com.i_zj.udrive_az.model.CityListResult;
import cn.com.i_zj.udrive_az.model.CreateDepositResult;
import cn.com.i_zj.udrive_az.model.CreateOderBean;
import cn.com.i_zj.udrive_az.model.DepositAmountResult;
import cn.com.i_zj.udrive_az.model.DoorBean;
import cn.com.i_zj.udrive_az.model.DriverResult;
import cn.com.i_zj.udrive_az.model.GetReservation;
import cn.com.i_zj.udrive_az.model.HomeActivityEntity;
import cn.com.i_zj.udrive_az.model.IDResult;
import cn.com.i_zj.udrive_az.model.ImageUrlResult;
import cn.com.i_zj.udrive_az.model.NetworkResult;
import cn.com.i_zj.udrive_az.model.OilHistoryEntity;
import cn.com.i_zj.udrive_az.model.OrderDetailResult;
import cn.com.i_zj.udrive_az.model.OrderResult;
import cn.com.i_zj.udrive_az.model.ParkDetailResult;
import cn.com.i_zj.udrive_az.model.ParkOutAmount;
import cn.com.i_zj.udrive_az.model.ParksResult;
import cn.com.i_zj.udrive_az.model.PayOrderByBlanceResult;
import cn.com.i_zj.udrive_az.model.RechargeOrder;
import cn.com.i_zj.udrive_az.model.RefundDepositResult;
import cn.com.i_zj.udrive_az.model.ReserVationBean;
import cn.com.i_zj.udrive_az.model.SessionResult;
import cn.com.i_zj.udrive_az.model.UnFinishOrderResult;
import cn.com.i_zj.udrive_az.model.UnUseCouponResult;
import cn.com.i_zj.udrive_az.model.UserDepositResult;
import cn.com.i_zj.udrive_az.model.WalletResult;
import cn.com.i_zj.udrive_az.model.WeichatPayOrder;
import cn.com.i_zj.udrive_az.model.req.AddDriverCardInfo;
import cn.com.i_zj.udrive_az.model.req.AddIdCardInfo;
import cn.com.i_zj.udrive_az.model.ret.BaseRetObj;
import cn.com.i_zj.udrive_az.model.ret.RefuelObj;
import cn.com.i_zj.udrive_az.model.ret.RetAppversionObj;
import cn.com.i_zj.udrive_az.model.ret.RetEventObj;
import cn.com.i_zj.udrive_az.model.ret.RetParkObj;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by wli on 2018/8/11.
 */

public interface UdriveRestAPI {

    String DETAIL_URL = BuildConfig.ORDER_DETAIL_URL;

    @FormUrlEncoded
    @Headers("Authorization: Basic dGVzdDp0ZXN0")
    @POST("/oauth/mobile")
    Observable<SessionResult> login(@Header("deviceId") String deviceId, @Field("mobile") String mobile, @Field("smsCode") String smsCode);

//  @Header("Authorization") String authorization,

    @FormUrlEncoded
    @Headers("Authorization: Basic dGVzdDp0ZXN0")
    @POST("/oauth/token")
    Observable<SessionResult> refreshToken(@Field("grant_type") String grant_type, @Field("refresh_token") String refresh_token);

    @FormUrlEncoded
    @Headers("Authorization: Basic dGVzdDp0ZXN0")
    @POST("/oauth/token")
    Call<SessionResult> refreshToken1(@Field("grant_type") String grant_type, @Field("refresh_token") String refresh_token);

    @GET("/code/sms")
    Observable<NetworkResult> requestSms(@Header("deviceId") String deviceId, @Query("mobile") String mobile);

    //提交驾驶证信息
    @Headers("Content-Type: application/json")
    @PUT("/mobile/card/addDriverCardInfo")
    Observable<DriverResult> addDriver(@Body AddDriverCardInfo body);

    //我的钱包
    @GET("/mobile/pay/myWallet")
    Observable<WalletResult> myWallet();

    //查询用户押金信息
    @GET("/mobile/deposit/userDeposit")
    Observable<UserDepositResult> userDeposit();

    //获取押金金额
    @GET("/mobile/deposit/getDepositAmount")
    Observable<DepositAmountResult> getDepositAmount();


    //上传身份证正反面
    @Multipart
    @POST("mobile/card/uploadImage")
    Observable<ImageUrlResult> postImage(@Part MultipartBody.Part file);

    //上传身份信息
    @Headers("Content-Type: application/json")
    @PUT("/mobile/card/addIdCardInfo")
    Observable<IDResult> postAddIdCardInfo(@Body Map<String, Object> body);

    //上传身份信息
    @Headers("Content-Type: application/json")
    @PUT("/mobile/card/addIdCardInfo")
    Observable<IDResult> postAddIdCardInfo(@Body AddIdCardInfo addIdCardInfo);

    //查询所有订单
    @GET("/mobile/tripOrder/queryAllOrdersByUser")
    Observable<OrderResult> queryAllOrdersByUser();

    @GET("/mobile/v1/park/checkCar")
    Observable<CheckCarResult> checkCar(@Query("carId") String carId, @Query("parkId") int parkId);

    //获取user信息
    @GET("/mobile/card/getUserInfo")
    Observable<AccountInfoResult> getUserInfo();

    //获取我的行程状态
    @GET("/mobile/tripOrder/unfinishedOrder")
    Observable<UnFinishOrderResult> getUnfinishedOrder();

    @GET("/open/getAppView")
    Observable<ActivityResult> getAppView();

    //获取订单详情
    @GET("/mobile/tripOrder/{orderNum}")
    Observable<OrderDetailResult> tripOrderDetail(@Path("orderNum") String orderNum);

    @GET("/mobile/park/getParkOutAmount/{parkId}/{carId}")
    Observable<ParkOutAmount> getParkOutAmount(@Path("parkId") String parkId, @Path("carId") String carId);

    //获取订单轨迹
    @GET("/mobile/tripOrder/originContrail/{orderId}")
    Observable<BaseRetObj<List<OriginContrail>>> originContrail(@Path("orderId") int orderId);

    //获取优惠券
    @Headers("Content-Type: application/json")
    @POST("/mobile/tripOrder/payAmount")
    Observable<OrderDetailResult> payAmount(@Body Map<String, Object> body);

    //获取所有的优惠券
    @GET("/mobile/preferential/findAllPreferential")
    Observable<UnUseCouponResult> findAllPreferential(@Query("userId") String id);

    //获取所有的优惠券
    @GET("/mobile/v1/preferential/findAllPreferential")
    Observable<BaseRetObj<List<UnUseCouponResult.DataBean>>> v1FindAllPreferential(@Query("userId") String id);

    @GET("/mobile/tripOrder/getSysCity/{orderId}")
    Observable<BaseRetObj<List<CityListResult>>> getSysCity(@Path("orderId") int orderId);

    //获取押金订单号
    @GET("/mobile/deposit/refundDeposit")
    Observable<RefundDepositResult> refundDeposit();

    //获取城市信息
    @GET("/open/getCityList")
    Observable<BaseRetObj<List<CityListResult>>> getCityList();

    //根据订单号退押金
    @POST("/mobile/refund/{orderNum}")
    Observable<RefundDepositResult> refundMoney(@Path("orderNum") String orderNum);

    //创建缴纳押金订单
    @GET("/mobile/deposit/createDeposit")
    Observable<CreateDepositResult> createDeposit();

    //获取支付宝押金信息
    @POST("/mobile/alipay/getPrepayId/depositOrder/{orderNum}")
    Observable<AliPayOrder> getAliPayYajinOderInfo(@Path("orderNum") String orderNum);

    //获取微信押金信息
    @POST("/mobile/wechatpay/getPrepayId/depositOrder/{orderNum}")
    Observable<WeichatPayOrder> getWechatYajinOderInfo(@Path("orderNum") String orderNum);

    //余额支付
    @Headers("Content-Type: application/json")
    @POST("/mobile/pay/payOrderByBalance")
    Observable<PayOrderByBlanceResult> payOrderByBalance(@Body Map<String, Object> body);

    //行程订单微信支付统一下单获取预支付ID
    @POST("/mobile/wechatpay/getPrepayId/tripOrder/{orderNum}")
    Observable<WeichatPayOrder> getWechatTripOrder(@Path("orderNum") String orderNum);

    @POST("/mobile/wechatpay/getPrepayId/rentApp/{orderNum}")
    Observable<WeichatPayOrder> getWechatRentApp(@Path("orderNum") String orderNum);

    //指定行程订单订单号，获取支付宝支付参数
    @POST("/mobile/alipay/getPayOrder/tripOrder/{orderNum}")
    Observable<AliPayOrder> getAliPayTripOrder(@Path("orderNum") String orderNum);

    //获取所有可用的优惠券
    @GET("/mobile/preferential/findUnUsePreferential")
    Observable<UnUseCouponResult> findUnUsePreferential(@Query("userId") String id);

    //获取所有可用的优惠券
    @GET("/mobile/v1/preferential/findUnUsePreferential")
    Observable<BaseRetObj<List<UnUseCouponResult.DataBean>>> v1FindUnUsePreferential(@Query("orderId") String Authorization, @Query("userId") String id);

    //获取停车场区域标签
    @GET("/mobile/park/areaTags")
    Observable<AreaTagsResult> getAreaTags();

    //获取用户状态/身份证状态/驾驶证状态/押金状态接口
    @GET("/mobile/v1/user/checkAuth")
    Observable<AuthResult> checkAuth();

    //获取停车场
    @GET("/mobile/park/findRelativeParks")
    Observable<ParksResult> getParks();

    //获取停车场
    @GET("/mobile/park/findRelativeParks")
    Observable<ParksResult> getParks(@Query("areaCode") String areaCode);

    //获取停车场
    @GET("/mobile/park/findRelativeParks")
    Observable<BaseRetObj<List<ParksResult.DataBean>>> getParkslll();

    //获取停车场明细
    @GET("/mobile/park/detail/{id}")
    Observable<ParkDetailResult> getParkDetail(@Path("id") int id);

    @GET()
    Observable<CarInfoResult> getCarInfo(@Url String id);

    //预约车辆接口
    @Headers("Content-Type: application/json")
    @POST("mobile/v1/order/reservation")
    Observable<ReserVationBean> reservation(@Body Map<String, String> body);

    @Headers("Content-Type: application/json")
    @POST("mobile/v1/order")
    Observable<CreateOderBean> createTripOrder(@Body Map<String, Object> body);

    @Headers("Content-Type: application/json")
    @PUT("mobile/v1/order/finish")
    Observable<OrderDetailResult> finishTripOrder(@Body Map<String, Object> body);

    //创建订单接口
    @Headers("Content-Type: application/json")
    @POST("mobile/tripOrder/create")
    Observable<CreateOderBean> createOder(@Body Map<String, String> body);

    //打开车门
    @Headers("Content-Type: application/json")
    @POST("/mobile/tripOrder/openCar")
    Observable<DoorBean> openCar(@Body Map<String, String> body);

    //关闭车门
    @Headers("Content-Type: application/json")
    @POST("/mobile/tripOrder/lockCar")
    Observable<DoorBean> lockCar(@Body Map<String, String> body);

    //寻车
    @Headers("Content-Type: application/json")
    @POST("/mobile/tripOrder/searchCarBySound")
    Observable<DoorBean> searchCarBySound(@Body Map<String, String> body);

    @Headers("Content-Type: application/json")
    @POST("/mobile/tripOrder/searchCarByReservation")
    Observable<DoorBean> searchCarByReservation();

    //结束行程
    @PUT("/mobile/tripOrder/completeTripOrder/{orderNum}")
    Observable<OrderDetailResult> completeTripOrder(@Path("orderNum") String orderNum);

    //取消预约
    @Headers("Content-Type: application/json")
    @PUT("/mobile/car/cancelReservation")
    Observable<DoorBean> cancelReservation(@Body Map<String, String> body);

    //查询用户预约信息
    @GET("/mobile/tripOrder/getReservation")
    Observable<GetReservation> getReservation();

    @Headers("Content-Type: application/json")
    @POST("mobile/recharge/create")
    Observable<RechargeOrder> createRechargeOrder(@Body Map<String, Object> body);


    @POST("/mobile/alipay/getPrepayId/rechargeOrder/{orderNum}")
    Observable<AliPayOrder> getAliPayOderInfo(@Path("orderNum") String orderNum);

    @POST("/mobile/wechatpay/getPrepayId/rechargeOrder/{orderNum}")
    Observable<WeichatPayOrder> getWeiChatPayOderInfo(@Path("orderNum") String orderNum);

    //更新订单终点停车场
    @Headers("Content-Type: application/json")
    @PUT("/mobile/tripOrder/updateDestinationPark")
    Observable<RetParkObj> updateDestinationPark(@Body Map<String, Object> body);

    @GET("/mobile/appversion/1/check")
    Observable<RetAppversionObj> appversionCheck(@Query("version") String version);

    @POST("/open/registration/up")
    Observable<BaseRetObj<Object>> registration(@Body Map<String, Object> body);

    @POST("/open/registration/down")
    Observable<BaseRetObj<String>> registrationDown(@Body Map<String, Object> body);

    @GET("/mobile/activity/index")
    Observable<BaseRetObj<HomeActivityEntity>> activity();

    @GET("/mobile/activity/page")
    Observable<BaseRetObj<RetEventObj>> activityPage(@Query("pageNumber") int pageNumber, @Query("pageSize") int pageSize);

    /**
     * 获取停车场图片
     * @param id
     * @return
     */
    @GET("/mobile/park/remark/{id}")
    Observable<BaseRetObj<ParkRemark>> getParkRemark(@Path("id") String id);

    //加油相关api
    @POST("/mobile/tripOrder/refuel")
    Observable<BaseRetObj<RefuelObj>> refuel(@Body Map<String, Object> body);

    @GET("/mobile/tripOrder/refuel/{number}")
    Observable<BaseRetObj<List<OilHistoryEntity>>> refuelHistory(@Path("number") String number);

    @GET("/mobile/tripOrder/refuel/check")
    Observable<BaseRetObj<RefuelObj>> refuelStatus(@Query("number") String number);
}
