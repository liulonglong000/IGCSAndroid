package com.xxs.igcsandroid.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class GreenhouseInfo {
    private String greenhouseId;            // 温室ID
    private String greenhouseName;          // 温室名称
    private String greenhouseAddr;          // 温室所处位置
    private String remark;                  // 备注
    private String inDate;                  // 温室加入时间
    private String userName;
    private String picPath;

    public GreenhouseInfo(JSONObject obj) throws JSONException {
        this.greenhouseId = obj.getString("ghId");
        this.greenhouseName = obj.getString("ghName");
        this.greenhouseAddr = obj.getString("ghAddr");
        this.remark = obj.getString("ghRemark");
        this.inDate = obj.getString("addTime");
        this.userName = obj.getString("userName");
        this.picPath = obj.getString("picPath");
    }

    public String getGreenhouseId() {
        return greenhouseId;
    }

    public String getGreenhouseName() {
        return greenhouseName;
    }

    public String getGreenhouseAddr() {
        return greenhouseAddr;
    }

    public String getRemark() {
        return remark;
    }

    public String getInDate() {
        return inDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getPicPath() {
        return picPath;
    }

    public String getRemarkString() {
        if (remark.length() == 0) {
            return "暂无说明信息";
        }
        return remark;
    }
}
