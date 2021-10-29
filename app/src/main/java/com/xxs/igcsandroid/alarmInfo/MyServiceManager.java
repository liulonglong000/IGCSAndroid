package com.xxs.igcsandroid.alarmInfo;

import android.content.Context;
import android.content.Intent;

import com.xxs.igcsandroid.application.MyApplication;

public class MyServiceManager {
    private MyApplication mApp = null;
    private Context mContext = null;
    private Intent mServiceIntent = null;

    public MyServiceManager(Context context) {
        mContext = context;
        mApp = (MyApplication)mContext.getApplicationContext();
    }

    public void Start() {
        if (mServiceIntent != null) {
            return;
        }

        mServiceIntent = new Intent(mContext, MyService.class);
        mServiceIntent.putExtra("thread_status", MyService.THREAD_STATUS_WORK);
        mContext.startService(mServiceIntent);
    }
}
