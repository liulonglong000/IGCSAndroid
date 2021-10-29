package com.xxs.igcsandroid.alarmInfo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class MyContentResolver {
    public static final String CONTENT_PROVIDER = "com.xxs.igcsandroid.provider.myprovider";

    public static final String TAG = "MyContentResolver";
    Context mContext;
    ContentResolver mContentResolver;

    public MyContentResolver(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    public Context getContext() {
        return mContext;
    }

    // 失败返回-1
    public long insert(String table, String nullColumnHack, ContentValues values) {
        Uri uri = Uri.parse("content://" + CONTENT_PROVIDER + "/" + table);
        Uri ret = mContentResolver.insert(uri, values);
        if (ret == null) {
            return -1;
        }

        return ContentUris.parseId(ret);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        Uri uri = Uri.parse("content://" + CONTENT_PROVIDER + "/" + table);
        return mContentResolver.update(uri, values, whereClause, whereArgs);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        Uri uri = Uri.parse("content://" + CONTENT_PROVIDER + "/" + table);
        return mContentResolver.query(uri, columns, selection, selectionArgs, orderBy);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit) {
        Uri uri = Uri.parse("content://" + CONTENT_PROVIDER + "/" + table);
        String strOrderBy = null;
        if (orderBy != null) {
            strOrderBy = orderBy;
        }
        if (limit != null) {
            strOrderBy += " " + limit;
        }
        return mContentResolver.query(uri, columns, selection, selectionArgs, strOrderBy);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        Uri uri = Uri.parse("content://" + CONTENT_PROVIDER + "/" + table);
        return mContentResolver.delete(uri, whereClause, whereArgs);
    }
}
