package com.xxs.igcsandroid.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.adapter.AlarmAdapter;
import com.xxs.igcsandroid.alarmInfo.AlarmBroadcastReceiverHdl;
import com.xxs.igcsandroid.alarmInfo.MyNotification;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.AlarmInfo;

import java.util.ArrayList;

public class AlarmQueryActivity extends AppCompatActivity {
    private AlarmAdapter adapterAlarm;
    private Button btnLoadMore;
    private Integer curPage = 0;
    private Integer numPrePreValue = 10;

    private AlarmBroadcastReceiverHdl abrHdl;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_query);

        setTitle("告警信息");

        adapterAlarm = new AlarmAdapter(this);
        ListView lv = findViewById(R.id.lv_alarm);
        lv.setAdapter(adapterAlarm);

        btnLoadMore = findViewById(R.id.btn_reload);
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlarmData(true);
            }
        });

        getAlarmData(false);

        mainHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case AlarmBroadcastReceiverHdl.HANDLERMSG_RECVED_ALARM_INFO: {
                        curPage = 0;
                        getAlarmData(false);
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

    private void getAlarmData(final boolean bFirst) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<AlarmInfo> lstEntity = ((MyApplication)getApplicationContext()).mAppAlarmMgr.getAlarmToShow(curPage, numPrePreValue);
                if (lstEntity.size() == 0) {
                    btnLoadMore.setVisibility(View.GONE);
                    return;
                } else if (lstEntity.size() < numPrePreValue) {
                    btnLoadMore.setVisibility(View.GONE);
                }

                if (bFirst) {
                    adapterAlarm.addDataInfo(lstEntity);
                } else {
                    adapterAlarm.setDataInfo(lstEntity);
                }
                adapterAlarm.notifyDataSetChanged();

                curPage++;

                MyNotification.CancelNotification(AlarmQueryActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        abrHdl.RegisterAlarmReceiver();
    }

    @Override
    public void onStop(){
        super.onStop();
        abrHdl.UnregistAlarmReceiver();
    }
}
