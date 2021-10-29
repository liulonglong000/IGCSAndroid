package com.xxs.igcsandroid.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.adapter.AlarmAdapter;
import com.xxs.igcsandroid.alarmInfo.AlarmBroadcastReceiverHdl;
import com.xxs.igcsandroid.alarmInfo.MyNotification;
import com.xxs.igcsandroid.application.MyApplication;

public class AlarmNewActivity extends AppCompatActivity {
    private AlarmAdapter adapterAlarm;

    private AlarmBroadcastReceiverHdl abrHdl;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_new);

        MyNotification.CancelNotification(this);

        setTitle("未读告警信息");

        adapterAlarm = new AlarmAdapter(this);
        ListView lv = findViewById(R.id.lv_alarm_new);
        lv.setAdapter(adapterAlarm);

        mainHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case AlarmBroadcastReceiverHdl.HANDLERMSG_RECVED_ALARM_INFO: {
                        getAlarmNew();
                        break;
                    }
                    default:
                        break;
                }
                return;
            }
        };

        abrHdl = new AlarmBroadcastReceiverHdl(this, mainHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        abrHdl.RegisterAlarmReceiver();
        getAlarmNew();
    }

    @Override
    public void onStop(){
        super.onStop();
        abrHdl.UnregistAlarmReceiver();
    }

    private void getAlarmNew() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapterAlarm.addDataInfoToFront(((MyApplication)getApplicationContext()).mAppAlarmMgr.getAlarmNewToShow());
                adapterAlarm.notifyDataSetChanged();

                MyNotification.CancelNotification(AlarmNewActivity.this);
            }
        });
    }
}
