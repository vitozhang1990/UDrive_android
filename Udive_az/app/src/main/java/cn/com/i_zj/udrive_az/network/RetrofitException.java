package cn.com.i_zj.udrive_az.network;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.annotation.Annotation;

import cn.com.i_zj.udrive_az.model.NetworkErrorResult;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by wli on 2018/8/11.
 */

public class RetrofitException extends RuntimeException {
  public static RetrofitException httpError(String url, Response response, Retrofit retrofit) {
    String message = response.code() + " " + response.message();
    ResponseBody responseBody = response.errorBody();

    if (null != responseBody) {

      try {
        String errorBody = responseBody.string();
        if (!TextUtils.isEmpty(errorBody)) {
          NetworkErrorResult networkErrorResult = new Gson().fromJson(errorBody, NetworkErrorResult.class);
          if (null != networkErrorResult) {
            message = networkErrorResult.errorMsg;
          }
        }
      } catch (IOException e) {
      }
    }
    return new RetrofitException(response.code(),message, url, response, Kind.HTTP, null, retrofit);
  }

  public static RetrofitException networkError(IOException exception) {
    return new RetrofitException(1,exception.getMessage(), null, null, Kind.NETWORK, exception, null);
  }

  public static RetrofitException unexpectedError(Throwable exception) {
    return new RetrofitException(0,exception.getMessage(), null, null, Kind.UNEXPECTED, exception, null);
  }

  public enum Kind {
    NETWORK,
    HTTP,
    UNEXPECTED
  }

  private final String url;
  private final Response response;
  private final Kind kind;
  private final Retrofit retrofit;
  private final  int code;
  RetrofitException(int code,String message, String url, Response response, Kind kind, Throwable exception, Retrofit retrofit) {
    super(message, exception);
    this.url = url;
    this.code=code;
    this.response = response;
    this.kind = kind;
    this.retrofit = retrofit;
  }

  public String getUrl() {
    return url;
  }

  public Response getResponse() {
    return response;
  }

  public Kind getKind() {
    return kind;
  }

  public Retrofit getRetrofit() {
    return retrofit;
  }

  public int getCode() {
    return code;
  }

  public <T> T getErrorBodyAs(Class<T> type) throws IOException {
    if (response == null || response.errorBody() == null) {
      return null;
    }
    Converter<ResponseBody, T> converter = retrofit.responseBodyConverter(type, new Annotation[0]);
    return converter.convert(response.errorBody());
  }
}

