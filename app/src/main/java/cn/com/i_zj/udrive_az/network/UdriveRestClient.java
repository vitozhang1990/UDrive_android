package cn.com.i_zj.udrive_az.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.event.GotoLoginDialogEvent1;
import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.lz.ui.violation.MockInterceptor;
import cn.com.i_zj.udrive_az.model.CityListResult;
import cn.com.i_zj.udrive_az.model.SessionResult;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wli on 2018/8/11.
 */

public class UdriveRestClient {

    public static UdriveRestAPI udriveRestAPI;
    /**
     * 正式
     */
//    private static final String HOST_ONLINE = "http://132.232.128.121:8088/";

    /**
     * 测试
     */
//    public static final String HOST_ONLINE = "http://47.98.47.82:8088/";

//      private static final String HOST_ONLINE = "http://192.168.1.54:8088/";
//      private static final String HOST_ONLINE = "http://192.168.1.56:8088/";
    private static final String HOST_ONLINE = BuildConfig.API_BASE_URL;
    private static String[] whiteList = {
            "/code/sms",
            "/oauth/mobile",
            "/oauth/token",
            "/system/user/register",
            "/system/user/check",
            "/partner/",
            "/open/",
            "/mobile/appversion",
            "/mobile/activity",
            "/mobile/car/getReservationList",
            "/mobile/websocket",
            "/mobile/rent",
            "/mobile/park/findRelativeParks",
            "/mobile/park/detail",
            "/mobile/park/areaTags",
            "/mobile/park/img",
            "/mobile/park/remark",
            "/mobile/alipay/tripOrder/alipayNotify",
            "/mobile/alipay/rechargeOrder/alipayNotify",
            "/mobile/alipay/depositOrder/alipayNotify",
            "/mobile/wechatpay/tripOrder/wxPayNotify",
            "/mobile/wechatpay/rechargeOrder/wxPayNotify",
            "/mobile/wechatpay/depositOrder/wxPayNotify",
            "/mobile/wechatpay/depositOrder/wxPayNotify",
            "/mobile/wechatpay/getPrepayId/rent",
            "/mobile/wechatpay/getPrepayId/renth5",
            "/mobile/wechatpay/rentcar/wxPayNotify",
            "/mobile/wechatpay/rentcar/wxh5PayNotify"
    };


    public synchronized static UdriveRestAPI getClentInstance() {
        if (null == udriveRestAPI) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(new ResponseInterceptor()).build();
            Gson buildGson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
//                    .registerTypeAdapter(String.class, new StringDefaultNullAdapter())
//              .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory<Object>())
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(httpClient)
                    .baseUrl(getServerApi())
                    .addConverterFactory(GsonConverterFactory.create(buildGson))
                    .addCallAdapterFactory(RxErrorHandingFactory.create())
                    .build();

            udriveRestAPI = retrofit.create(UdriveRestAPI.class);
        }
        return udriveRestAPI;
    }

    private static String getServerApi() {
        return getOnlineServerApi();
    }

    private static String getTestServerApi() {
        return "";
    }

    private static String getOnlineServerApi() {
        return HOST_ONLINE;
    }

    private static class ResponseInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            boolean hasTarget = false;
            for (String str : whiteList) {
                String url = chain.request().url().uri().toString();
                if (url.contains(str) && !url.contains("/open/registration/up")) {
                    hasTarget = true;
                    break;
                }
            }
            Request.Builder builder = chain.request().newBuilder();
            String aAuthorization = SessionManager.getInstance().getAuthorization();
            if (!StringUtils.isEmpty(aAuthorization) && !hasTarget) {
                if (SessionManager.getInstance().isNeedReLogin()) { //超过refreshtoken的有效期，需要登录
                    if (BuildConfig.DEBUG) Log.e("zhangwei", "超过refreshtoken的有效期，需要登录");
                    EventBus.getDefault().post(new GotoLoginDialogEvent1());
                } else {
                    if (BuildConfig.DEBUG) Log.e("zhangwei", "我要添加token了");
                    synchronized (this) {
                        if (SessionManager.getInstance().isNeedRefeshToken()) {
                            if (BuildConfig.DEBUG) Log.e("zhangwei", "token失效了，我要去refreshtoken");
                            //取出本地的refreshToken
                            String refreshToken = SessionManager.getInstance().getRefresh();

                            // 通过一个特定的接口获取新的token，此处要用到同步的retrofit请求
                            Call<SessionResult> call = udriveRestAPI.refreshToken1("refresh_token", refreshToken);

                            //要用retrofit的同步方式
                            SessionResult sessionResult = call.execute().body();
                            if (sessionResult != null) {
                                SessionManager.getInstance().cacheSession(sessionResult);
                            }

                            aAuthorization = SessionManager.getInstance().getAuthorization();
                        }
                    }

                    if (BuildConfig.DEBUG) Log.e("zhangwei", "添加token成功");
                    builder.addHeader("Authorization", aAuthorization);
                }
            }
            try {
                CityListResult cityInfo = LocalCacheUtils.getDeviceData(Constants.SP_GLOBAL_NAME, Constants.SP_CITY);
                if (cityInfo != null) {
                    builder.addHeader("areaCode", cityInfo.getAreaCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return chain.proceed(builder.build());
        }
    }
}
