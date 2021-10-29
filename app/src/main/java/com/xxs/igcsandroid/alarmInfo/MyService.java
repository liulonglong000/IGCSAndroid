package com.xxs.igcsandroid.alarmInfo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Button;
import android.widget.Toast;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.AlarmInfo;
import com.xxs.igcsandroid.entity.EquipmentInfo;
import com.xxs.igcsandroid.entity.GreenhouseInfo;
import com.xxs.igcsandroid.fragment.NodeControlFragment;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    public static final int THREAD_STATUS_WORK = 0;
    public static final int THREAD_STATUS_WAIT = 1;
    public static final int THREAD_STATUS_EXIT = 2;

    private MyApplication mApp = null;

    private Handler handler;
    private Timer mTimer;

    public MyService() {
    }

    // onBind方法用于与服务通信的信道进行绑定，这里返回空值。
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // onCreate方法用于创建后台服务程序。
    @Override
    public void onCreate() {
        super.onCreate();

        mApp = (MyApplication) this.getApplication();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 11:
                        ReportAlarmToServer();
                        getAlarmFromServer();
                        return;
                }
                super.handleMessage(msg);
            }
        };

        mTimer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = 11;
                handler.sendMessage(message);
            }
        };
        mTimer.schedule(task, 3000, 30000);
    }

    private void ReportAlarmToServer() {
        List<AlarmInfo> lst = mApp.mAppAlarmMgr.getAlarmReadToShow();
        if (lst.size() == 0) {
            return;
        }

        String ids = "";
        int i = 0;
        for (i = 0; i < lst.size() - 1; i++) {
            ids += lst.get(i).getaId();
            ids += ",";
        }
        ids += lst.get(i).getaId();

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("alarmIds", ids);

        AsyncSocketUtil.postBack("alarm/reportAlarmRead", mp, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {

            }
        }, null);
    }

    private void getAlarmFromServer() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", ((MyApplication) this.getApplication()).getMyUserId());

        AsyncSocketUtil.postBack("alarm/getNewAlarmByUserId", mp, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        AlarmInfo entity = new AlarmInfo(obj);
                        handleNewAlarm(entity);
                    }

                } catch (Exception e) {

                }
            }
        }, null);
    }

    private void handleNewAlarm(AlarmInfo alarm) {
        try {
            if (!mApp.mAppAlarmMgr.AddOneAlarm(alarm)) {
                DlgUtil.showMsgError(mApp.getApplicationContext(), "保存告警信息失败！");
            }

            if (alarm.isaExistInDB()) {
                return;
            }

            MyNotification.ShowNotification(mApp.getApplicationContext(), "告警信息", alarm.getaMessage(), alarm.getaId());

            Intent intent = new Intent();
            intent.putExtra("alarm_id", alarm.getaId());
            intent.putExtra("alarm_msg", alarm.getaMessage());
            intent.putExtra("alarm_time", alarm.getaTime());
            intent.setAction("android.intent.action.alarmmsg");
            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mApp.getApplicationContext());
            lbm.sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
            DlgUtil.showMsgError(mApp.getApplicationContext(), "处理告警信息失败！");
        }
    }

    // onStartCommand方法用于启动后台服务程序。
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    // onDestroy方法用于销毁所有的后台服务程序，同时删除所有的服务调用。
    @Override
    public void onDestroy() {
        super.onDestroy();
//        handler.removeMessages(1);
//        mTimer.cancel();
    }
}
