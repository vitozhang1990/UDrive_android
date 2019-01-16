package cn.com.i_zj.udrive_az.login;

import android.support.annotation.MainThread;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import cn.com.i_zj.udrive_az.event.LoginSuccessEvent;
import cn.com.i_zj.udrive_az.model.SessionResult;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wli on 2018/8/12.
 * SessionManager 管理
 */

public class SessionManager {
  private static SessionManager sessionManager;

  private SessionResult sessionResult = null;

  private SessionManager() {
    String data = LocalCacheUtils.getPersistentSettingString(Constants.SP_GLOBAL_NAME, Constants.SP_SESSION_MANAGER, "");
    if (!TextUtils.isEmpty(data)) {
      sessionResult = new Gson().fromJson(data, SessionResult.class);
    }
  }

  public static synchronized SessionManager getInstance() {
    if (null == sessionManager) {
      sessionManager = new SessionManager();
    }
    return sessionManager;
  }

  public void cacheSession(SessionResult sessionResult) {
    sessionResult.last_fetch_time = System.currentTimeMillis() / 1000;
    LocalCacheUtils.savePersistentSettingString(Constants.SP_GLOBAL_NAME, Constants.SP_SESSION_MANAGER, new Gson().toJson(sessionResult));
    this.sessionResult = sessionResult;
  }

  public void clearSession() {
    sessionResult = null;
    LocalCacheUtils.savePersistentSettingString(Constants.SP_GLOBAL_NAME, Constants.SP_SESSION_MANAGER, "");
    AccountInfoManager.getInstance().clearAccount();
  }

  public SessionResult getSession() {
    return sessionResult;
  }

  public boolean isLogin() {
    return !TextUtils.isEmpty(getAuthorization()) && (System.currentTimeMillis() / 1000 - sessionResult.last_fetch_time < sessionResult.expires_in);
  }

  /**
   * 服务器端的有效期为2小时，所以需要客户端频繁去刷新 token，这里 Android 端的逻辑为当时间过一半的时候就去刷新 token
   *
   * @return
   */
  private boolean isNeedRefeshToken() {
    if (null == sessionResult) {
      return false;
    }
    if (0 == sessionResult.last_fetch_time) {
      return true;
    }

    long appRunSecound = System.currentTimeMillis() / 1000 - sessionResult.last_fetch_time;
    return (appRunSecound > sessionResult.expires_in / 2);
  }

  /**
   * 刷新 token
   *
   * @param isForce 是否强制，如果是 true 则直接刷新 token，如为 false 则根据 {@link #isNeedRefeshToken} 方法判定是否需要刷新
   */
  @MainThread
  public void refreshToken(boolean isForce) {
    if (null != sessionResult && (isForce || isNeedRefeshToken())) {
      String refreshToken = sessionResult.refresh_token;
      UdriveRestClient.getClentInstance().refreshToken("refresh_token", refreshToken)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<SessionResult>() {
          @Override
          public void onSubscribe(Disposable d) {

          }

          @Override
          public void onNext(SessionResult sessionResult) {
            if (null != sessionResult) {
              sessionResult.last_fetch_time = System.currentTimeMillis() / 1000;
              EventBus.getDefault().post(new LoginSuccessEvent());
              SessionManager.getInstance().cacheSession(sessionResult);
            }
          }

          @Override
          public void onError(Throwable e) {
          }

          @Override
          public void onComplete() {
          }
        });
    }
  }

  public String getRefresh() {
    if (null != sessionResult) {
      return sessionResult.getRefresh();
    }
    return null;
  }

  public String getAccess() {
    if (null != sessionResult) {
      return sessionResult.getAccess();
    }
    return null;
  }

  public String getAuthorization() {
    if (null != sessionResult) {
      return sessionResult.getAuthorization();
    }
    return null;
  }
}
