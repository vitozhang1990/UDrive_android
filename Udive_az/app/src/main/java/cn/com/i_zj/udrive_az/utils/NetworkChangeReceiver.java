package cn.com.i_zj.udrive_az.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

import cn.com.i_zj.udrive_az.event.NetWorkEvent;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtils.isAvailableByPing()) {
            EventBus.getDefault().post(new NetWorkEvent());
        }
    }
}
