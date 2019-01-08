package cn.com.i_zj.udrive_az.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wli on 2018/8/11.
 */

public class SessionResult {

  @SerializedName("access_token")
  public String access_token;

  @SerializedName("token_type")
  public String token_type;

  @SerializedName("refresh_token")
  public String refresh_token;

  // 服务器并未返回此字段，客户端缓存用于拉取token，单位 秒
  @SerializedName("last_fetch_time")
  public long last_fetch_time;

  @SerializedName("expires_in")
  public int expires_in;

  @SerializedName("scope")
  public String scope;

  @SerializedName("jti")
  public String jti;

  public String getAuthorization() {
    if (!TextUtils.isEmpty(access_token)) {
      return String.format("Bearer %s", access_token);
    }
    return null;
  }
}
