package com.xxs.igcsandroid.entity;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

public class AlarmInfo {
    private String aId;
    private String aMessage;
    private String aTime;
    private String aIsRead;

    private boolean aExistInDB;

    public AlarmInfo(JSONObject obj) throws JSONException {
        this.aId = obj.getString("aId");
        this.aMessage = obj.getString("aMessage");
        this.aTime = obj.getString("aTime");
        aExistInDB = false;
    }

    public AlarmInfo(String strId, String strMsg, String strTime, String strRead) {
        this.aId = strId;
        this.aMessage = strMsg;
        this.aTime = strTime;
        this.aIsRead = strRead;
    }

    public String getaId() {
        return aId;
    }

    public String getaMessage() {
        return aMessage;
    }

    public String getaTime() {
        return aTime;
    }

    public boolean isaExistInDB() {
        return aExistInDB;
    }

    public void setaExistInDB(boolean aExistInDB) {
        this.aExistInDB = aExistInDB;
    }

    public String getaIsRead() {
        return aIsRead;
    }
}
