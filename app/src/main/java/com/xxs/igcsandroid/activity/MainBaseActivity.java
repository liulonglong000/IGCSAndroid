package com.xxs.igcsandroid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.alarmInfo.AlarmBroadcastReceiverHdl;
import com.xxs.igcsandroid.application.MyApplication;

public class MainBaseActivity extends AppCompatActivity {
    protected MyApplication mApp;
    private AlarmBroadcastReceiverHdl abrHdl;
    private Handler mainHandler;
    private int mAlarmNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_base);

        mApp = (MyApplication)getApplication();
        mApp.initApplication();

        mainHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case AlarmBroadcastReceiverHdl.HANDLERMSG_RECVED_ALARM_INFO: {
                        mAlarmNum++;
                        invalidateOptionsMenu();
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAlarmNum = ((MyApplication)getApplicationContext()).mAppAlarmMgr.getAlarmNewNum();
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onStop(){
        super.onStop();
        abrHdl.UnregistAlarmReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mAlarmNum > 0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_alarm_new, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_new:
                Intent intent = new Intent(this, AlarmNewActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
