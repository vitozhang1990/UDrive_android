package cn.com.i_zj.udrive_az.service;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.dhh.websocket.Config;
import com.dhh.websocket.RxWebSocket;
import com.dhh.websocket.WebSocketSubscriber;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.com.i_zj.udrive_az.BuildConfig;
import cn.com.i_zj.udrive_az.event.WebSocketEvent;
import cn.com.i_zj.udrive_az.model.WebSocketLocation;
import cn.com.i_zj.udrive_az.model.WebSocketPark;
import cn.com.i_zj.udrive_az.model.WebSocketPrice;
import cn.com.i_zj.udrive_az.model.WebSocketResult;
import okhttp3.WebSocket;

public class BackService extends BaseService {

    private WebSocket mWebSocket;
    private MyWebSocketSubscriber socketSubscriber;

    @Override
    public boolean init() {
        Config config = new Config.Builder()
                .setShowLog(true)
                .setShowLog(true, TAG)
                .setReconnectInterval(2, TimeUnit.SECONDS)
                .build();
        RxWebSocket.setConfig(config);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(WebSocketEvent webSocketEvent) {
        if (socketSubscriber != null) {
            socketSubscriber.dispose();
            socketSubscriber = null;
        }
        if (webSocketEvent.getUserId() != 0) { //取消所有webSocket
            connect("app_" + webSocketEvent.getUserId());
        }
    }

    private void connect(String userId) {
        socketSubscriber = new MyWebSocketSubscriber();
        RxWebSocket.get(BuildConfig.API_BASE_URL + "mobile/websocket/" + userId)
                .subscribe(socketSubscriber);
    }

    private class MyWebSocketSubscriber extends WebSocketSubscriber {
        @Override
        protected void onOpen(WebSocket webSocket) {
            super.onOpen(webSocket);
            mWebSocket = webSocket;
        }

        @Override
        protected void onMessage(String text) {
            super.onMessage(text);
            GsonBuilder gb = new GsonBuilder();
            Gson gson = gb.create();
            WebSocketResult result = gson.fromJson(text, WebSocketResult.class);
            if (result == null || result.getCode() == null || !result.getSuccess()) {
                return;
            }
            switch (result.getCode()) {
                case 1000://推送停车场信息
                    Type parkType = new TypeToken<WebSocketResult<Integer>>() {
                    }.getType();
                    WebSocketResult<WebSocketPark> park = gson.fromJson(text, parkType);
                    break;
                case 2000://推送车辆定位信息
                    Type locationType = new TypeToken<WebSocketResult<Integer>>() {
                    }.getType();
                    WebSocketResult<WebSocketLocation> location = gson.fromJson(text, locationType);
                    break;
                case 3000://推送实时价格信息
                    Type priceType = new TypeToken<WebSocketResult<Integer>>() {
                    }.getType();
                    WebSocketResult<WebSocketPrice> price = gson.fromJson(text, priceType);
                    break;
                case 4000://推送订单是否超额信息
                    Type chaoeType = new TypeToken<WebSocketResult<Integer>>() {
                    }.getType();
                    WebSocketResult<Integer> chaoe = gson.fromJson(text, chaoeType);
                    break;
                case 5000://推送订单是否断电信息
                    Type duandianType = new TypeToken<WebSocketResult<Integer>>() {
                    }.getType();
                    WebSocketResult<Integer> duandian = gson.fromJson(text, duandianType);
                    break;
                case 6000://推送强制结束订单信息
                    Type finishType = new TypeToken<WebSocketResult<Integer>>() {
                    }.getType();
                    WebSocketResult<Integer> finish = gson.fromJson(text, finishType);
                    break;
            }
        }

        @Override
        protected void onClose() {
            super.onClose();
            mWebSocket.close(1000, "再见");
        }
    }

    @Override
    public void restartService() {
        if (serviceRunning(this, "BackService")) {
            if (socketSubscriber != null) {
                socketSubscriber.dispose();
                socketSubscriber = null;
            }
            Intent intent = new Intent(this, BackService.class);
            this.startService(intent);
        }
    }

    private boolean serviceRunning(Context context, String className) {
        try {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                    .getRunningServices(Integer.MAX_VALUE);

            if (!(serviceList.size() > 0)) {
                return false;
            }

            for (int i = 0; i < serviceList.size(); i++) {
                ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
                ComponentName serviceName = serviceInfo.service;

                if (serviceName.getClassName().equals(className)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}