package com.xxs.igcsandroid.alarmInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.os.Build;

import com.xxs.igcsandroid.application.MyApplication;

public class MySQLiteDatabase {
    private DatabaseHelper mDbHelper = null;
    private SQLiteDatabase mDb = null;
    private Context mCtx = null;
    private MyApplication mApp = null;
    private boolean bNeedSync = false;
    private String sync;

    public MySQLiteDatabase(Context context, DatabaseHelper databaseHelper, String sync) {
        this.mDbHelper = databaseHelper;
        this.sync = sync;
        this.mCtx = context;
        this.mApp = (MyApplication) mCtx.getApplicationContext();
    }

    public boolean isOpened() {
        if (mDb == null || !mDb.isOpen()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean open() {
        if (mDb != null) {
            if (mDb.isOpen()) {
                return true;
            }
        }
        try {
            mDb = mDbHelper.getWritableDatabase();
            if (Build.VERSION.SDK_INT >= 11) {
                mDb.enableWriteAheadLogging();  // 处理同步问题
            } else {
                bNeedSync = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("Open DB sucessfully.");
        return true;
    }

    public void closeDB() {
        if (mDbHelper != null) {
            mDbHelper.close();
            System.out.println("Close DB sucessfully.");
        }
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        try {
            if (bNeedSync) {
                synchronized (sync) {
                    return mDb.rawQuery(sql, selectionArgs);
                }
            } else {
                return mDb.rawQuery(sql, selectionArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void execSQL(String sql) {
        try {
            if (bNeedSync) {
                synchronized (sync) {
                    mDb.execSQL(sql);
                }
            } else {
                mDb.execSQL(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        try {
            if (bNeedSync) {
                synchronized (sync) {
                    return mDb.insert(table, nullColumnHack, values);
                }
            } else {
                return mDb.insert(table, nullColumnHack, values);
            }
        } catch (Exception e) {
            if (e.hashCode() != 19) {
                e.printStackTrace();
            }
            return MyDBAdapter.ERR_FAILED;
        }
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        try {
            if (bNeedSync) {
                synchronized (sync) {
                    return mDb.update(table, values, whereClause, whereArgs);
                }
            } else {
                return mDb.update(table, values, whereClause, whereArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MyDBAdapter.ERR_FAILED;
        }
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        try {
            if (bNeedSync) {
                synchronized (sync) {
                    return mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
                }
            } else {
                return mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        try {
            if (bNeedSync) {
                synchronized (sync) {
                    return mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
                }
            } else {
                return mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        try {
            if (bNeedSync) {
                synchronized (sync) {
                    return mDb.delete(table, whereClause, whereArgs);
                }
            } else {
                return mDb.delete(table, whereClause, whereArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MyDBAdapter.ERR_FAILED;
        }
    }
}
