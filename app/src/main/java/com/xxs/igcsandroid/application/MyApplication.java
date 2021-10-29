package com.xxs.igcsandroid.application;

import android.app.Application;

import com.xxs.igcsandroid.alarmInfo.AlarmManager;
import com.xxs.igcsandroid.alarmInfo.MyContentResolver;
import com.xxs.igcsandroid.alarmInfo.MyServiceManager;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;

public class MyApplication extends Application {
    private static MyApplication instance;

    private String token;
    private String myUserId;
    private String userPwd;
    private String userRole;

    public MyServiceManager mMainServiceMgr = null;
    public AlarmManager mAppAlarmMgr = null;            // 告警数据管理
    public MyContentResolver mContentResolver = null;   // 数据库处理

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        AsyncSocketUtil.initAsyncSocket(getApplicationContext());
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public void setLoginInfo(String userId, String userPwd) {
        this.myUserId = userId;
        this.userPwd = userPwd;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public void initApplication() {
        if (mMainServiceMgr == null) {
            mMainServiceMgr = new MyServiceManager(this);

            mMainServiceMgr.Start();
        }

        if (mContentResolver == null) {
            mContentResolver = new MyContentResolver(this);
        }

        if (mAppAlarmMgr == null) {
            mAppAlarmMgr = new AlarmManager(this, mContentResolver);
        }
    }

    public String getMyUserId() {
        return myUserId;
    }
}
