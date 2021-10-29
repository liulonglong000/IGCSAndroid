package com.xxs.igcsandroid.alarmInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.AlarmInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AlarmDB {
    public static final String TABLE_NAME = "igcs_b_alarm";
    public static final String KEY_ID = "alarm_log_id";
    public static final String KEY_MESSAGE = "alarm_message";
    public static final String KEY_TIME = "insert_time";
    public static final String KEY_READ = "is_read";
    public static final String KEY_RECV_USER = "recv_user_id";
    public static final String KEY_READ_TIME = "read_time";

    private MyContentResolver mContentResolverPtr = null;
    private MyApplication mAppPtr = null;

    public AlarmDB(Context context, MyContentResolver contentResolver){
//        mContext = context;
        mAppPtr = (MyApplication)context.getApplicationContext();
        mContentResolverPtr = contentResolver;
    }

    public static void CreateTableIfNotExist(MyDBAdapter dbAdapter) {
        String strTableCreate = "create table " + TABLE_NAME
                + " (" + KEY_ID + " text not null primary key, "
                + KEY_MESSAGE + " text not null, "
                + KEY_TIME + " text not null, "
                + KEY_READ + " text not null, "
                + KEY_RECV_USER + " text not null, "
                + KEY_READ_TIME + " text, "
                + "unique(" + KEY_ID + ") ON CONFLICT IGNORE);";
        if (!dbAdapter.isOpened()) {
            dbAdapter.openDB();
        }
        if (!dbAdapter.CreateTableIfNotExist(TABLE_NAME, strTableCreate)){
            return;
        }
    }

    public synchronized long AddAlarmToDB(AlarmInfo alarm) throws Exception {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ID, alarm.getaId());
        initialValues.put(KEY_MESSAGE, alarm.getaMessage());
        initialValues.put(KEY_TIME, alarm.getaTime());
        initialValues.put(KEY_READ, "0");
        initialValues.put(KEY_RECV_USER, mAppPtr.getMyUserId());

        return mContentResolverPtr.insert(TABLE_NAME, null, initialValues);
    }

    public synchronized boolean isAlarmExist(String alarmId) {
        boolean bRet = false;
        Cursor queryResult = null;
        String selection = KEY_ID + "=?";
        String[] args = {alarmId};

        queryResult = mContentResolverPtr.query(TABLE_NAME, null, selection, args, null, null, null, null);
        if (queryResult != null) {
            if (queryResult.moveToNext()) {
                if (queryResult.getCount() > 0) {
                    bRet = true;
                }
            }
            queryResult.close();
        }

        return bRet;
    }

    public ArrayList<AlarmInfo> getAllAlarmWithStatus(String strRead) {
        ArrayList<AlarmInfo> chatMessages = new ArrayList<>();
        Cursor queryResult = null;
        String selection = KEY_RECV_USER + " ='" + mAppPtr.getMyUserId() + "'" +
                " AND " + KEY_READ + " = '" + strRead + "'";
        String strOrderBy = KEY_TIME + " DESC";

        try {
            queryResult = mContentResolverPtr.query(TABLE_NAME, null, selection, null, null, null, strOrderBy, null);
            if (queryResult != null) {
                int count = queryResult.getCount();
                for (int i = 0; i < count; i++) {
                    queryResult.moveToNext();
                    String strId = queryResult.getString(queryResult.getColumnIndex(KEY_ID));
                    String strMsg = queryResult.getString(queryResult.getColumnIndex(KEY_MESSAGE));
                    String strTime = queryResult.getString(queryResult.getColumnIndex(KEY_TIME));
                    AlarmInfo msg = new AlarmInfo(strId, strMsg, strTime, strRead);
                    chatMessages.add(msg);
                }

                queryResult.close();
                queryResult = null;
            }
        } catch (Exception e) {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return chatMessages;
    }

    public void updateAlarmState(AlarmInfo info, String strRead) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_READ, strRead);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        updateValues.put(KEY_READ_TIME, formatter.format(curDate));

        String strSelectionToUpdate = KEY_ID + " =" + info.getaId() + "";

        mContentResolverPtr.update(TABLE_NAME, updateValues, strSelectionToUpdate, null);
    }

    public ArrayList<AlarmInfo> getAllAlarm(Integer curPage, Integer numPrePreValue) {
        ArrayList<AlarmInfo> chatMessages = new ArrayList<>();
        Cursor queryResult = null;
        String selection = KEY_RECV_USER + " ='" + mAppPtr.getMyUserId() + "'";
        String strOrderBy = KEY_READ + " ASC, " + KEY_TIME + " DESC";
        String strLimit = "limit " + numPrePreValue + " offset " + curPage * numPrePreValue;

        try {
            queryResult = mContentResolverPtr.query(TABLE_NAME, null, selection, null, null, null, strOrderBy, strLimit);
            if (queryResult != null) {
                int count = queryResult.getCount();
                for (int i = 0; i < count; i++) {
                    queryResult.moveToNext();
                    String strId = queryResult.getString(queryResult.getColumnIndex(KEY_ID));
                    String strMsg = queryResult.getString(queryResult.getColumnIndex(KEY_MESSAGE));
                    String strTime = queryResult.getString(queryResult.getColumnIndex(KEY_TIME));
                    String strRead = queryResult.getString(queryResult.getColumnIndex(KEY_READ));
                    AlarmInfo msg = new AlarmInfo(strId, strMsg, strTime, strRead);
                    chatMessages.add(msg);
                }

                queryResult.close();
                queryResult = null;
            }
        } catch (Exception e) {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return chatMessages;
    }

    public int getAlarmNumWithStatus(String strRead) {
        Cursor queryResult = null;
        int count = 0;
        String selection = KEY_RECV_USER + " ='" + mAppPtr.getMyUserId() + "'" +
                        " AND " + KEY_READ + " = '" + strRead + "'";

        try {
            queryResult = mContentResolverPtr.query(TABLE_NAME, null, selection, null, null, null, null, null);
            if (queryResult != null) {
                count = queryResult.getCount();

                queryResult.close();
                queryResult = null;
            }
        } catch (Exception e) {
            if (queryResult != null) {
                queryResult.close();
            }
        }

        return count;
    }
}
