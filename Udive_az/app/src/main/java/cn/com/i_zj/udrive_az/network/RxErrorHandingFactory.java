package cn.com.i_zj.udrive_az.network;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by wli on 2018/8/11.
 */

public class RxErrorHandingFactory extends CallAdapter.Factory {
  private final RxJava2CallAdapterFactory original;

  private RxErrorHandingFactory() {
    original = RxJava2CallAdapterFactory.create();
  }

  public static CallAdapter.Factory create() {
    return new RxErrorHandingFactory();
  }

  @Override
  public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
    String returnTypeString = ((ParameterizedType) returnType).getRawType().toString();
    if (returnTypeString.contains("Observable") || returnTypeString.contains("Maybe")) {
      return new RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit));
    } else {
      return null;
    }
  }

  private static class RxCallAdapterWrapper<R> implements CallAdapter<R, Object> {
    private final Retrofit retrofit;
    private final CallAdapter<R, Object> wrapped;

    public RxCallAdapterWrapper(Retrofit retrofit, CallAdapter<R, Object> wrapped) {
      this.retrofit = retrofit;
      this.wrapped = wrapped;
    }

    @Override
    public Type responseType() {
      return wrapped.responseType();
    }

    @Override
    public Object adapt(Call<R> call) {
      Object result = wrapped.adapt(call);
      if (result instanceof Single) {
        return ((Single) result).onErrorResumeNext(new Function<Throwable, SingleSource>() {
          @Override
          public SingleSource apply(@NonNull Throwable throwable) throws Exception {
            return Single.error(asRetrofitException(throwable));
          }
        });
      }
      if (result instanceof Observable) {
        return ((Observable) result).onErrorResumeNext(new Function<Throwable, ObservableSource>() {
          @Override
          public ObservableSource apply(@NonNull Throwable throwable) throws Exception {
            return Observable.error(asRetrofitException(throwable));
          }
        });
      }

      if (result instanceof Completable) {
        return ((Completable) result).onErrorResumeNext(new Function<Throwable, CompletableSource>() {
          @Override
          public CompletableSource apply(@NonNull Throwable throwable) throws Exception {
            return Completable.error(asRetrofitException(throwable));
          }
        });
      }

      return result;
    }

    private RetrofitException asRetrofitException(Throwable throwable) {
      if (throwable instanceof HttpException) {
        HttpException httpException = (HttpException) throwable;
        Response response = httpException.response();
        return RetrofitException.httpError(response.raw().request().url().toString(), response, retrofit);
      }
      if (throwable instanceof IOException) {
        return RetrofitException.networkError((IOException) throwable);
      }
      return RetrofitException.unexpectedError(throwable);
    }
  }
}
