package com.way.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.way.common.util.T;
import com.way.utils.S;
import com.way.yahoo.MainActivity;
import com.way.yahoo.R;
import com.way.yahoo.TwitterListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by 13510 on 2016/3/1.
 */
public class GeTuiReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

                if (payload != null) {
                    String data = new String(payload);

                    Log.d("GetuiSdkDemo", "receiver payload : " + data);

                    payloadData.append(data);
                    payloadData.append("\n");

                    S.o(":::>" + data);

                    JSONObject customJson = null;
                    try {
                        customJson = new JSONObject(data);
                        String title = new String(customJson.getString("title").getBytes(), "UTF-8");
                        String content = new String(customJson.getString("content").getBytes(), "UTF-8");
                        int toPage = customJson.getInt("to");

                        Intent notifyIntent;
                        if (toPage == 1) {//home page.
                            notifyIntent = new Intent(context, MainActivity.class);
                        } else {          //list page
                            notifyIntent = new Intent(context, TwitterListActivity.class);
                        }

                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                                notifyIntent, 0);
                        // 下面需兼容Android 2.x版本是的处理方式
                        // Notification notify1 = new Notification(R.drawable.message,
                        // "TickerText:" + "您有新短消息，请注意查收！", System.currentTimeMillis());
                        Notification notify3 = null;
                        Notification.Builder builder = new Notification.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setTicker("看天穿衣提示")
                                .setContentTitle(title)
                                .setContentText(content)
                                .setContentIntent(pendingIntent); // 需要注意build()是在APIlevel16及之后增加的，API11可以使用getNotificatin()来替代

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            notify3 = builder.build();
                        } else {
                            notify3 = builder.getNotification();
                        }

                        notify3.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
                        notify3.defaults |= Notification.DEFAULT_SOUND;
                        manager.notify(199, notify3);
//                TestPushMessageNotification.notify(context, title, content, toPage, 0);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
//                T.showShort(context, "绑定成功:" + cid);
                break;

            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 *
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            default:
                break;
        }
    }
}
