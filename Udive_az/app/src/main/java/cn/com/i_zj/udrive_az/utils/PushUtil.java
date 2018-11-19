package cn.com.i_zj.udrive_az.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import cn.com.i_zj.udrive_az.network.UObserver;
import cn.com.i_zj.udrive_az.network.UdriveRestClient;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author JayQiu
 * @create 2018/11/8
 * @Describe
 */
public class PushUtil {
    /**
     * 注册推送
     *
     * @param regid
     */
    public static void registPush(String regid) {
        Map<String,Object> map= new HashMap<>();
        map.put("regId",regid);
        UdriveRestClient.getClentInstance().registration(map).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UObserver<Object>() {
                    @Override
                    public void onSuccess(Object response) {
                    }

                    @Override
                    public void onException(int code, String message) {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
    }

}
