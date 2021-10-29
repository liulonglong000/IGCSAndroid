package com.xxs.igcsandroid.entity;

import org.json.JSONObject;

public class StrategyInfo {
    private String id;
    private String tgId1;
    private String tgId2;
    private String tgId3;
    private String ogId;
    private String userName;
    private String tgName1;
    private String tgName2;
    private String tgName3;
    private String ogName;
    private String start;
    private String end;
    private String mdyTime;

    public StrategyInfo(JSONObject obj) throws Exception {
        this.id = obj.getString("id");
        this.tgId1 = obj.getString("tgId1");
        this.tgName1 = obj.getString("tgName1");
        if (obj.has("tgId2")) {
            this.tgId2 = obj.getString("tgId2");
        }
        if (obj.has("tgName2")) {
            this.tgName2 = obj.getString("tgName2");
        }
        if (obj.has("tgId3")) {
            this.tgId3 = obj.getString("tgId3");
        }
        if (obj.has("tgName3")) {
            this.tgName3 = obj.getString("tgName3");
        }
        this.ogId = obj.getString("ogId");
        this.ogName = obj.getString("ogName");
        this.start = obj.getString("start");
        this.end = obj.getString("end");
        this.userName = obj.getString("userName");
        this.mdyTime = obj.getString("mdyTime");
    }

    public String getId() {
        return id;
    }

    public String getTgId1() {
        return tgId1;
    }

    public String getTgId2() {
        return tgId2;
    }

    public String getTgId3() {
        return tgId3;
    }

    public String getOgId() {
        return ogId;
    }

    public String getUserName() {
        return userName;
    }

    public String getTgName1() {
        return tgName1;
    }

    public String getTgName2() {
        return tgName2;
    }

    public String getTgName3() {
        return tgName3;
    }

    public String getOgName() {
        return ogName;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getMdyTime() {
        return mdyTime;
    }
}
