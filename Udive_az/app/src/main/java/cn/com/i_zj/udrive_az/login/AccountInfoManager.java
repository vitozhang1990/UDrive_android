package cn.com.i_zj.udrive_az.login;

import android.text.TextUtils;

import com.google.gson.Gson;

import cn.com.i_zj.udrive_az.model.AccountInfoResult;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.Constants;
import cn.com.i_zj.udrive_az.utils.LocalCacheUtils;

// TODO 与 SessionManager 抽取相同逻辑
public class AccountInfoManager {

  private static AccountInfoManager accountInfoManager;

  private AccountInfoResult accountInfoResult = null;

  private AccountInfoManager() {
    String data = LocalCacheUtils.getPersistentSettingString(Constants.SP_GLOBAL_NAME, Constants.SP_ACCOUNT_INFO_MANAGER, "");
    if (!TextUtils.isEmpty(data)) {
      accountInfoResult = new Gson().fromJson(data, AccountInfoResult.class);
    }
  }

  public static synchronized AccountInfoManager getInstance() {
    if (null == accountInfoManager) {
      accountInfoManager = new AccountInfoManager();
    }
    return accountInfoManager;
  }

  public void cacheAccount(AccountInfoResult accountInfoResult) {
    LocalCacheUtils.savePersistentSettingString(Constants.SP_GLOBAL_NAME, Constants.SP_ACCOUNT_INFO_MANAGER, new Gson().toJson(accountInfoResult));
    this.accountInfoResult = accountInfoResult;
  }

  public void clearAccount() {
    accountInfoResult = null;
    LocalCacheUtils.savePersistentSettingString(Constants.SP_GLOBAL_NAME, Constants.SP_ACCOUNT_INFO_MANAGER, "");
  }

  public AccountInfoResult getAccountInfo() {
    return accountInfoResult;
  }
}
