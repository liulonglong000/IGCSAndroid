package com.xxs.igcsandroid.alarmInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

public class AlarmBroadcastReceiverHdl {
    public static final int HANDLERMSG_RECVED_ALARM_INFO = 101;

    private Handler mainHandlerPtr;
    private AlarmBroadcastReceiver mReceiver;
    private LocalBroadcastManager lbm;

    public AlarmBroadcastReceiverHdl(Context context, Handler mainHandler) {
        lbm = LocalBroadcastManager.getInstance(context);
        this.mainHandlerPtr = mainHandler;
    }

    public void RegisterAlarmReceiver() {
        UnregistAlarmReceiver();

        mReceiver = new AlarmBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.alarmmsg");
        lbm.registerReceiver(mReceiver, filter);
    }

    public void UnregistAlarmReceiver() {
        if (mReceiver != null) {
            lbm.unregisterReceiver(mReceiver); // 解除注册接收器
            mReceiver = null;
        }
    }

    private class AlarmBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String strAction = intent.getAction();
            if (strAction == null) {
                return;
            }
            if (strAction.equals("android.intent.action.alarmmsg")) {
                Message msg = new Message();
                msg.what = HANDLERMSG_RECVED_ALARM_INFO;
                msg.setData(intent.getExtras());
                mainHandlerPtr.handleMessage(msg);
            }
        }
    }
}
