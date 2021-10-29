package com.xxs.igcsandroid.alarmInfo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.UserManager;
import android.support.v4.app.NotificationCompat;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.AlarmNewActivity;
import com.xxs.igcsandroid.activity.MainParkUserActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyNotification {
    public static void ShowNotification(Context context, String title, String message, String notifyTag) {
        Intent intent = new Intent();
        PendingIntent contentIntent = null;
        intent.setClass(context, AlarmNewActivity.class);
//        intent.putExtra("ChatObjID", strUserId);
        contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // 高版本需要渠道
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // 只在Android O之上需要渠道，这里的第一个参数要和下面的channelId一样
            NotificationChannel notificationChannel = new NotificationChannel("1", "name", NotificationManager.IMPORTANCE_HIGH);
            // 如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，通知才能正常弹出
            manager.createNotificationChannel(notificationChannel);
        }

        boolean bVibrate = true;
        boolean bSound = true;
        boolean bLight = true;
        long[] vibrate = {0,100,200,300};
        int idefault = 0;
        if (bVibrate) {
            idefault |= Notification.DEFAULT_VIBRATE;
        }
        if (bSound){
            idefault |= Notification.DEFAULT_SOUND;
        }
        if (bLight){
            idefault |= Notification.DEFAULT_LIGHTS;
        }

//        PendingIntent contentIntent = null;
//
//        Intent appIntent = new Intent();
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, appIntent, 0);

        // 这里的第二个参数要和上面的第一个参数一样
        Notification notification = new NotificationCompat.Builder(context, "1")
                .setContentTitle(title)
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setVibrate(vibrate)
                .setLights(0xff000000/*颜色*/, 1000/*灯亮时间*/, 1000/*暗灯时间*/)
                .setDefaults(idefault)
                .build();
        manager.notify(notifyTag, 1, notification);
    }

    public static void CancelNotification(Context context){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

//    private static Intent[] MakeIntentStack(Context context, String userId){
//        Intent[] intents = new Intent[2];
//        intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, MainActivity.class));
//        intents[1] = new Intent(context, MapActivity.class);
//        intents[1].putExtra("ChatObjID", userId);
//        return intents;
//    }
}
