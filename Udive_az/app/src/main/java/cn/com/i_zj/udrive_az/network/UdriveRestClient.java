package cn.com.i_zj.udrive_az.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.com.i_zj.udrive_az.login.SessionManager;
import cn.com.i_zj.udrive_az.utils.StringUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private static final String HOST_ONLINE = "http://test.zzbcjj.com:8088/";


    public synchronized static UdriveRestAPI getClentInstance() {
        if (null == udriveRestAPI) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient httpClient = new OkHttpClient.Builder()
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
            Request.Builder builder = chain.request()
                    .newBuilder();
            String aAuthorization = SessionManager.getInstance().getAuthorization();
            if (!StringUtils.isEmpty(aAuthorization)) {
                builder.addHeader("Authorization", aAuthorization);
            }
            return chain.proceed(builder.build());
        }
    }
}