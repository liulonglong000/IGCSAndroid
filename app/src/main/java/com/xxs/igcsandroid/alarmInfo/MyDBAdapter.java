package com.xxs.igcsandroid.alarmInfo;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.xxs.igcsandroid.application.MyApplication;

public class MyDBAdapter {
    public static final int ERR_OK = 0;
    public static final int ERR_FAILED = -1;

    private final Context mCtx;
    private MyApplication mApp = null;
    private DatabaseHelper mDbHelper = null;
    public MySQLiteDatabase mDb = null;
    private DBUpgradeListener mDbUpgradeListener = null;

    public interface DBUpgradeListener{
        public void onUpgradeDB(int error, int oldVersion, int newVersion);
    }

    public MyDBAdapter(Context ctx, DBUpgradeListener dbUpgradeListener) {

        this.mCtx = ctx;
        mApp = (MyApplication)mCtx.getApplicationContext();

        if (mDbHelper == null) {
            mDbHelper = new DatabaseHelper(mCtx); // 处理数据库升级
        }
        mDb = new MySQLiteDatabase(mCtx, mDbHelper, "Database");
        mDbUpgradeListener = dbUpgradeListener;

        AlarmDB.CreateTableIfNotExist(this);
    }

    public boolean isOpened() {
        return mDb.isOpened();
    }

    public boolean openDB() {
        return mDb.open();
    }

    public boolean CreateTableIfNotExist(String TABLE_NAME, String strCreateTable) {
        String sql = "SELECT count(*) as c FROM Sqlite_master WHERE type='table' AND name='"
                + TABLE_NAME + "'";
        Cursor cursor = null;
        boolean result = false;

        if (mDb == null || !mDb.isOpened()) {
            if (!openDB()) {
                return false;
            }
        }

        try {
            cursor = mDb.rawQuery(sql, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(cursor.getColumnIndex("c"));
                    if (count > 0) {
                        result = true;
                    }
                }
                cursor.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (cursor != null){
                cursor.close();
            }
            return false;
        }

        if (!result) {
            try {
                mDb.execSQL(strCreateTable);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

            System.out.println("Create TABLE sucessfully.");
        }

        return true;
    }
}
