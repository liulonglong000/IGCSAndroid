package com.xxs.igcsandroid.entity;

import org.json.JSONObject;

public class ThresholdGroupInfo {
    private String tgId;
    private String tgName;
    private String remark;
    private String userName;
    private String mdyTime;

    public ThresholdGroupInfo(JSONObject obj) throws Exception {
        this.tgId = obj.getString("id");
        this.tgName = obj.getString("name");
        this.remark = obj.getString("remark");
        this.userName = obj.getString("userName");
        this.mdyTime = obj.getString("mdyTime");
    }

    public String getTgId() {
        return tgId;
    }

    public String getTgName() {
        return tgName;
    }

    public String getRemark() {
        return remark;
    }

    public String getUserName() {
        return userName;
    }

    public String getMdyTime() {
        return mdyTime;
    }
}
