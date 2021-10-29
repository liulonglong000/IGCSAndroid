package com.xxs.igcsandroid.alarmInfo;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xxs.igcsandroid.application.MyApplication;

// ContentProvider的作用是为不同的应用之间数据共享，提供统一的接口，我们知道安卓系统中应用内部的数据是对外隔离的，
// 要想让其它应用能使用自己的数据（例如通讯录）这个时候就用到了ContentProvider
// ContentProvider通过uri来标识其它应用要访问的数据，通过ContentResolver的增、删、改、查方法实现对共享数据的操作。
// 还可以通过注册ContentObserver来监听数据是否发生了变化来对应的刷新页面
public class MyContentProvider extends ContentProvider {
    public UriMatcher  mMatcher;
    private MyDBAdapter mMyDBAdapter;
    private MySQLiteDatabase mMySqLiteDatabase;

    // 在创建ContentProvider时使用
    @Override
    public boolean onCreate() {
        mMyDBAdapter = new MyDBAdapter(this.getContext(), new MyDBAdapter.DBUpgradeListener() {
            @Override
            public void onUpgradeDB(int error, int oldVersion, int newVersion) {
                if (error != 0){
                    return;
                }
            }
        });

        mMyDBAdapter.openDB();
        mMySqLiteDatabase = mMyDBAdapter.mDb;

        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(MyContentResolver.CONTENT_PROVIDER, AlarmDB.TABLE_NAME, 1);

        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String strTableName = getTabelName(uri);

        if (strTableName != null) {
            if (mMySqLiteDatabase != null) {
                return mMySqLiteDatabase.query(strTableName, projection, selection, selectionArgs, null, null, sortOrder);
            }
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        String strTableName = getTabelName(uri);

        if (strTableName != null) {
            if (mMySqLiteDatabase != null) {
                long lRowId = mMySqLiteDatabase.insert(strTableName, null, values);
                if (lRowId == -1) {
                    return null;
                }

                Uri resultUri = ContentUris.withAppendedId(uri, lRowId);
                return resultUri;
            }
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String strTableName = getTabelName(uri);

        if (strTableName != null) {
            if (mMySqLiteDatabase != null) {
                return mMySqLiteDatabase.delete(strTableName, selection, selectionArgs);
            }
        }

        return -1;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String strTableName = getTabelName(uri);

        if (strTableName != null) {
            if (mMySqLiteDatabase != null) {
                return mMySqLiteDatabase.update(strTableName, values, selection, selectionArgs);
            }
        }

        return -1;
    }

    String getTabelName(Uri uri) {
        String strTableName = null;
        int iMatch = mMatcher.match(uri);
        switch (iMatch) {
            case 1:
                strTableName = AlarmDB.TABLE_NAME;
                break;
            default:
                break;
        }

        return strTableName;
    }
}
