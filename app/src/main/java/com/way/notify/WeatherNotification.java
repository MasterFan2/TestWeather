package com.way.notify;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.way.yahoo.MainActivity;
import com.way.yahoo.R;

/**
 * Created by masterfan on 16/2/25.
 */
public class WeatherNotification {

    private static final String NOTIFICATION_TAG = "WeatherPushMessage";

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void nofity(final Context context){
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification n = new Notification(R.drawable.ic_launcher, "重要通知!", System.currentTimeMillis());
        Notification n = new Notification();
        Notification.Builder builder = new Notification.Builder(context);
        builder.setTicker("重要通知 ");
        builder.setContentTitle("天气变换通知");
        builder.setContentText("要接近40度哦");

        n.flags = Notification.FLAG_AUTO_CANCEL;
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

        //PendingIntent
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                R.string.app_name,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, builder.build());
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), builder.build());
        }

//        n.setLatestEventInfo(
//                context,
//                "Hello,there!",
//                "Hello,there,I'm john.",
//                contentIntent);
//        nm.notify(R.string.app_name, n);
    }
}
