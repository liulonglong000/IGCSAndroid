package com.xxs.igcsandroid.alarmInfo;

import android.content.Context;

import com.xxs.igcsandroid.entity.AlarmInfo;

import java.util.ArrayList;
import java.util.List;

public class AlarmManager {
    private AlarmDB mAlarmDB;

    public AlarmManager(Context context, MyContentResolver contentResolver) {
//        mContext = context;
//        mApp = (MainApplication)context.getApplicationContext();
        mAlarmDB = new AlarmDB(context, contentResolver);
    }

    public synchronized boolean AddOneAlarm(AlarmInfo alarm) throws Exception {
        if (mAlarmDB.isAlarmExist(alarm.getaId())) {
            alarm.setaExistInDB(true);
            return true;
        }

        if (mAlarmDB.AddAlarmToDB(alarm) == -1) {
            return false;
        }

        return true;
    }

    public synchronized ArrayList<AlarmInfo> getAlarmNewToShow() {
        ArrayList<AlarmInfo> messages = mAlarmDB.getAllAlarmWithStatus("0");
        for (AlarmInfo info : messages) {
            mAlarmDB.updateAlarmState(info, "1");
        }
        return messages;
    }

    public List<AlarmInfo> getAlarmReadToShow() {
        ArrayList<AlarmInfo> messages = mAlarmDB.getAllAlarmWithStatus("1");
        for (AlarmInfo info : messages) {
            mAlarmDB.updateAlarmState(info, "2");
        }
        return messages;
    }

    public ArrayList<AlarmInfo> getAlarmToShow(Integer curPage, Integer numPrePreValue) {
        ArrayList<AlarmInfo> messages = mAlarmDB.getAllAlarm(curPage, numPrePreValue);
        for (AlarmInfo info : messages) {
            if (info.getaIsRead().equals("0")) {
                mAlarmDB.updateAlarmState(info, "1");
            } else {
                break;
            }
        }
        return messages;
    }

    public int getAlarmNewNum() {
        return mAlarmDB.getAlarmNumWithStatus("0");
    }
}
