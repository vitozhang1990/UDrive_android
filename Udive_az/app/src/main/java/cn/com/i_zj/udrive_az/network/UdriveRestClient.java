package cn.com.i_zj.udrive_az.network;

import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wli on 2018/8/11.
 */

public class UdriveRestClient {

  private static UdriveRestAPI udriveRestAPI;

  private static final String HOST_ONLINE = "http://132.232.128.121:8088/";


  public synchronized static UdriveRestAPI getClentInstance() {
    if (null == udriveRestAPI) {
      HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(HttpLoggingInterceptor.Level.BODY);

      OkHttpClient httpClient = new OkHttpClient.Builder()
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .addInterceptor(new ResponseInterceptor()).build();

      Retrofit retrofit = new Retrofit.Builder()
        .client(httpClient)
        .baseUrl(getServerApi())
        .addConverterFactory(GsonConverterFactory.create())
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
      return chain.proceed(chain.request());
    }
  }
}
