package com.xxs.igcsandroid.util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class HandleCacheInfo {
    public static final String FILE_NAME = "IGCSAndroid";
    public static final boolean HISTORY_SHOW_TABLE = false;
    public static final boolean HISTORY_SHOW_PIC = true;

    public static boolean getHistoryShow(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        return sp.getBoolean("HistoryShow", HISTORY_SHOW_PIC);
    }

    public static void setHistoryShow(Context context, boolean show) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("HistoryShow", show);
        editor.commit();
    }
}
