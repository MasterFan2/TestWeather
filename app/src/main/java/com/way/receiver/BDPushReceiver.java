package com.way.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.way.utils.S;
import com.way.yahoo.MainActivity;
import com.way.yahoo.R;
import com.way.yahoo.TwitterListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by masterfan on 16/2/24.
 */
public class BDPushReceiver  {//extends PushMessageReceiver {

//    public static final int PAGE_HOME = 0x001;//home page
//    public static final int PAGE_LIST = 0x002;//list page.
//
//    /**
//     * TAG to Log
//     */
//    public static final String TAG = BDPushReceiver.class.getSimpleName();
//
//    /**
//     * 调用PushManager.startWork后，sdk将对push
//     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
//     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
//     *
//     * @param context   BroadcastReceiver的执行Context
//     * @param errorCode 绑定接口返回值，0 - 成功
//     * @param appid     应用id。errorCode非0时为null
//     * @param userId    应用user id。errorCode非0时为null
//     * @param channelId 应用channel id。errorCode非0时为null
//     * @param requestId 向服务端发起的请求id。在追查问题时有用；
//     * @return none
//     */
//    @Override
//    public void onBind(Context context, int errorCode, String appid,
//                       String userId, String channelId, String requestId) {
//        String responseString = "onBind errorCode=" + errorCode + " appid="
//                + appid + " userId=" + userId + " channelId=" + channelId
//                + " requestId=" + requestId;
//        System.out.println("bindString:" + responseString);
//
//        if (errorCode == 0) {
//            // 绑定成功
//        }
//        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//        updateContent(context, responseString);
//    }
//
//    /**
//     * 接收透传消息的函数。
//     *
//     * @param context             上下文
//     * @param message             推送的消息
//     * @param customContentString 自定义内容,为空或者json字符串
//     */
//    @Override
//    public void onMessage(Context context, String message,
//                          String customContentString) {
//        String messageString = ":::" + message;
//
//        S.o("::RRR:>>"+messageString);
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        // 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
//        if (!TextUtils.isEmpty(message)) {
//            JSONObject customJson = null;
//            try {
//                customJson = new JSONObject(message);
//                String title   = new String(customJson.getString("title").getBytes(), "UTF-8");
//                String content = new String(customJson.getString("content").getBytes(), "UTF-8");
//                int toPage     = customJson.getInt("to");
//
//                Intent intent;
//                if(toPage == PAGE_HOME) {
//                    intent = new Intent(context, MainActivity.class);
//                }else {
//                    intent = new Intent(context, TwitterListActivity.class);
//                }
//
//                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//                       intent, 0);
//                // 下面需兼容Android 2.x版本是的处理方式
//                // Notification notify1 = new Notification(R.drawable.message,
//                // "TickerText:" + "您有新短消息，请注意查收！", System.currentTimeMillis());
//                Notification notify3 = null;
//                Notification.Builder builder = new Notification.Builder(context)
//                        .setSmallIcon(R.drawable.ic_launcher)
//                        .setTicker("看天穿衣提示")
//                        .setContentTitle(title)
//                        .setContentText(content)
//                        .setContentIntent(pendingIntent); // 需要注意build()是在APIlevel16及之后增加的，API11可以使用getNotificatin()来替代
//
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    notify3 = builder.build();
//                }else{
//                    notify3 = builder.getNotification();
//                }
//
//                notify3.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
//                notify3.defaults |= Notification.DEFAULT_SOUND;
//                manager.notify(199, notify3);
////                TestPushMessageNotification.notify(context, title, content, toPage, 0);
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                System.out.println(e.getMessage());
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//        //        updateContent(context, messageString);
//    }
//
//    public String decodeUnicode(final String dataStr) {
//        int start = 0;
//        int end = 0;
//        final StringBuffer buffer = new StringBuffer();
//        while (start > -1) {
//            end = dataStr.indexOf("\\u", start + 2);
//            String charStr = "";
//            if (end == -1) {
//                charStr = dataStr.substring(start + 2, dataStr.length());
//            } else {
//                charStr = dataStr.substring(start + 2, end);
//            }
//            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
//            buffer.append(new Character(letter).toString());
//            start = end;
//        }
//        return buffer.toString();
//    }
//
//    /**
//     * 接收通知点击的函数。
//     *
//     * @param context             上下文
//     * @param title               推送的通知的标题
//     * @param description         推送的通知的描述
//     * @param customContentString 自定义内容，为空或者json字符串
//     */
//    @Override
//    public void onNotificationClicked(Context context, String title,
//                                      String description, String customContentString) {
//        String notifyString = "通知点击 title=\"" + title + "\" description=\""
//                + description + "\" customContent=" + customContentString;
//        Log.d(TAG, notifyString);
//
//        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
//        if (!TextUtils.isEmpty(customContentString)) {
//            JSONObject customJson = null;
//            try {
//                customJson = new JSONObject(customContentString);
//                String myvalue = null;
//                if (!customJson.isNull("mykey")) {
//                    myvalue = customJson.getString("mykey");
//                }
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//
//        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//        updateContent(context, notifyString);
//    }
//
//    /**
//     * 接收通知到达的函数。
//     *
//     * @param context             上下文
//     * @param title               推送的通知的标题
//     * @param description         推送的通知的描述
//     * @param customContentString 自定义内容，为空或者json字符串
//     */
//
//    @Override
//    public void onNotificationArrived(Context context, String title,
//                                      String description, String customContentString) {
//
//        String notifyString = "onNotificationArrived  title=\"" + title
//                + "\" description=\"" + description + "\" customContent="
//                + customContentString;
//        Log.d(TAG, notifyString);
//
//        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
//        if (!TextUtils.isEmpty(customContentString)) {
//            JSONObject customJson = null;
//            try {
//                customJson = new JSONObject(customContentString);
//                String myvalue = null;
//                if (!customJson.isNull("mykey")) {
//                    myvalue = customJson.getString("mykey");
//                }
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//        // 你可以參考 onNotificationClicked中的提示从自定义内容获取具体值
//        updateContent(context, notifyString);
//    }
//
//    /**
//     * setTags() 的回调函数。
//     *
//     * @param context     上下文
//     * @param errorCode   错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
//     * @param failTags    设置失败的tag
//     * @param requestId   分配给对云推送的请求的id
//     */
//    @Override
//    public void onSetTags(Context context, int errorCode,
//                          List<String> sucessTags, List<String> failTags, String requestId) {
//        String responseString = "onSetTags errorCode=" + errorCode
//                + " sucessTags=" + sucessTags + " failTags=" + failTags
//                + " requestId=" + requestId;
//        Log.d(TAG, responseString);
//
//        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//        updateContent(context, responseString);
//    }
//
//    /**
//     * delTags() 的回调函数。
//     *
//     * @param context     上下文
//     * @param errorCode   错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
//     * @param failTags    删除失败的tag
//     * @param requestId   分配给对云推送的请求的id
//     */
//    @Override
//    public void onDelTags(Context context, int errorCode,
//                          List<String> sucessTags, List<String> failTags, String requestId) {
//        String responseString = "onDelTags errorCode=" + errorCode
//                + " sucessTags=" + sucessTags + " failTags=" + failTags
//                + " requestId=" + requestId;
//        Log.d(TAG, responseString);
//
//        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//        updateContent(context, responseString);
//    }
//
//    /**
//     * listTags() 的回调函数。
//     *
//     * @param context   上下文
//     * @param errorCode 错误码。0表示列举tag成功；非0表示失败。
//     * @param tags      当前应用设置的所有tag。
//     * @param requestId 分配给对云推送的请求的id
//     */
//    @Override
//    public void onListTags(Context context, int errorCode, List<String> tags,
//                           String requestId) {
//        String responseString = "onListTags errorCode=" + errorCode + " tags="
//                + tags;
//        Log.d(TAG, responseString);
//
//        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//        updateContent(context, responseString);
//    }
//
//    /**
//     * PushManager.stopWork() 的回调函数。
//     *
//     * @param context   上下文
//     * @param errorCode 错误码。0表示从云推送解绑定成功；非0表示失败。
//     * @param requestId 分配给对云推送的请求的id
//     */
//    @Override
//    public void onUnbind(Context context, int errorCode, String requestId) {
//        String responseString = "onUnbind errorCode=" + errorCode
//                + " requestId = " + requestId;
//        Log.d(TAG, responseString);
//
//        if (errorCode == 0) {
//            // 解绑定成功
//        }
//        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//        updateContent(context, responseString);
//    }
//
//    private void updateContent(Context context, String content) {
//        Log.d(TAG, "updateContent");
//        String logText = "" + Utils.logStringCache;
//
//        if (!logText.equals("")) {
//            logText += "\n";
//        }
//
//        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH-mm-ss");
//        logText += sDateFormat.format(new Date()) + ": ";
//        logText += content;
//
//        Utils.logStringCache = logText;
//
//        Intent intent = new Intent();
//        intent.setClass(context.getApplicationContext(), MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.getApplicationContext().startActivity(intent);
//    }
}