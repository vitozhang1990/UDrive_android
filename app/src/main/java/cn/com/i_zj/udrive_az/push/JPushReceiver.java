package cn.com.i_zj.udrive_az.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.com.i_zj.udrive_az.MainActivity;
import cn.com.i_zj.udrive_az.lz.ui.msg.ActMsg;
import cn.com.i_zj.udrive_az.lz.ui.order.OrderActivity;
import cn.com.i_zj.udrive_az.model.JPushEntity;
import cn.com.i_zj.udrive_az.utils.PushUtil;
import cn.com.i_zj.udrive_az.web.WebActivity;
import cn.jpush.android.api.JPushInterface;

/**
 * @author JayQiu
 * @create 2018/11/6
 * @Describe
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-JPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...
                PushUtil.registPush(regId);
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.e(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.e(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

                String jsonStr = bundle.getString(JPushInterface.EXTRA_EXTRA);
                if (TextUtils.isEmpty(jsonStr)) {
                    Log.i(TAG, "This message has no Extra data");
                    return;
                }
                Log.e("=================>", jsonStr);
                try {
                    Gson gson = new Gson();
                    JPushEntity jPushEntity = gson.fromJson(jsonStr, JPushEntity.class);
                    intentStart(context, jPushEntity);
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }


            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {

        }
    }

    private void intentStart(Context context, JPushEntity jPushEntity) {
        if (jPushEntity == null) {
            return;
        }
        Intent intent= null;
        switch (jPushEntity.getRedirectType()) {
            case JPushEntity.NOT_REDIRECT:
                intent= new Intent(context, MainActivity.class);
                break;
            case JPushEntity.URL_REDIRECT:
                intent= new Intent(context, WebActivity.class);
                intent.putExtra("url",jPushEntity.getRedirect());
                intent.putExtra("title","");
                break;
            case JPushEntity.APP_REDIRECT:// APP内部
                if(JPushEntity.INDEX.equals(jPushEntity.getRedirect())){
                    intent= new Intent(context, MainActivity.class);
                }else if(JPushEntity.ORDER.equals(jPushEntity.getRedirect())){
                    intent= new Intent(context, OrderActivity.class);
                }else if(JPushEntity.EVENT.equals(jPushEntity.getRedirect())){
                    intent= new Intent(context, ActMsg.class);
                }else {
                    intent= new Intent(context, MainActivity.class);
                }
                break;
            default:
                intent= new Intent(context, MainActivity.class);
                break;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {

    }


}
