package cn.com.i_zj.udrive_az.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wli on 2018/8/11.
 */

public class NetworkResult {

  @SerializedName("success")
  public boolean success = true;

  @SerializedName("result")
  public String result;
}
