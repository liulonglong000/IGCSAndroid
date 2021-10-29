package com.xxs.igcsandroid.entity;

import org.json.JSONObject;

public class OperationGroupInfo {
    private String ogId;
    private String ogName;
    private String remark;
    private String userName;
    private String mdyTime;

    public OperationGroupInfo(JSONObject obj) throws Exception {
        this.ogId = obj.getString("id");
        this.ogName = obj.getString("name");
        this.remark = obj.getString("remark");
        this.userName = obj.getString("userName");
        this.mdyTime = obj.getString("mdyTime");
    }

    public String getOgId() {
        return ogId;
    }

    public String getOgName() {
        return ogName;
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
