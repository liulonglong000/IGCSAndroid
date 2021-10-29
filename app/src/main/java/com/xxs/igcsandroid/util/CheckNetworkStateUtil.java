package com.xxs.igcsandroid.util;

import android.content.Context;
import android.net.ConnectivityManager;

public class CheckNetworkStateUtil {
    // 检测网络是否连接
    public static boolean check(final Context context) {
        boolean flag = false;
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }
}
