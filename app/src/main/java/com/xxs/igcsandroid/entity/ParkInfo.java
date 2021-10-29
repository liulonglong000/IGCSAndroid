package com.xxs.igcsandroid.entity;

import org.json.JSONObject;

public class ParkInfo {
    private String parkId;
    private String parkName;
    private String parkAddr;
    private String parkRemark;
    private String addTime;
    private String userName;
    private String picPath;
    private Integer area;

    public ParkInfo(JSONObject obj) throws Exception {
        this.parkId = obj.getString("parkId");
        this.parkName = obj.getString("parkName");
        this.parkAddr = obj.getString("parkAddr");
        this.parkRemark = obj.getString("parkRemark");
        this.addTime = obj.getString("addTime");
        this.picPath = obj.getString("picPath");
        this.userName = obj.getString("userName");
        if (obj.has("area")) {
            this.area = obj.getInt("area");
        }
    }

    public String getParkId() {
        return parkId;
    }

    public String getParkName() {
        return parkName;
    }

    public String getParkAddr() {
        return parkAddr;
    }

    public String getParkRemark() {
        return parkRemark;
    }

    public String getAddTime() {
        return addTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getPicPath() {
        return picPath;
    }

    public Integer getArea() {
        return area;
    }

    public String getInfo() {
        if (parkAddr.length() == 0 && parkRemark.length() == 0) {
            return "暂无描述信息";
        }
        if (parkAddr.length() == 0) {
            return parkRemark;
        }
        if (parkRemark.length() == 0) {
            return parkAddr;
        }
        return parkAddr + "，" + parkRemark;
    }

    public String getAreaString() {
        if (area == null) {
            return "未知";
        }
        return area + "亩";
    }

    public String getName() {
        return parkId + "    " + parkName;
    }
}
