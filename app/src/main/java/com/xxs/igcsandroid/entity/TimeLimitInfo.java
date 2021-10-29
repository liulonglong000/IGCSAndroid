package com.xxs.igcsandroid.entity;

import android.app.Activity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class TimeLimitInfo {
    private String gwId;
    private String nodeId;
    private String nodeName;

    private String equiId;
    private String equiType;
    private String equiName;
    private String locate;
    private String timeUp;
    private String timeDown;
    private String timeError;

    public TimeLimitInfo(JSONObject obj, String gwId, String nodeId, String nodeName) throws JSONException {
        this.gwId = gwId;
        this.nodeId = nodeId;
        this.nodeName = nodeName;

        this.equiId = obj.getString("equiId");
        this.equiType = obj.getString("equiTypeString");
        this.equiName = obj.getString("equiName");
        this.locate = obj.getString("locate");
        this.timeUp = obj.getString("timeUp");
        this.timeDown = obj.getString("timeDown");
        this.timeError = obj.getString("timeError");
    }

    public TimeLimitInfo(Bundle obj) {
        this.gwId = obj.getString("gwId");
        this.nodeId = obj.getString("nodeId");
        this.nodeName = obj.getString("nodeName");
        this.equiId = obj.getString("equiId");
        this.equiType = obj.getString("equiTypeString");
        this.equiName = obj.getString("equiName");
        this.locate = obj.getString("locate");
        this.timeUp = obj.getString("timeUp");
        this.timeDown = obj.getString("timeDown");
        this.timeError = obj.getString("timeError");
    }

    public String getGwId() {
        return gwId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getEquiName() {
        return equiName;
    }

    public String getLocate() {
        return locate;
    }

    public String getTimeUp() {
        return timeUp;
    }

    public String getTimeDown() {
        return timeDown;
    }

    public String getTimeError() {
        return timeError;
    }

    public String getEquiId() {
        return equiId;
    }

    public String getEquiType() {
        return equiType;
    }

    public String getNameString() {
        return nodeName + "(网关" + gwId + " 节点" + nodeId + ") " + equiName + equiId;
    }

    public String getEquipNameString() {
        return equiName + equiId;
    }

    public String getLocateString() {
        if (locate.length() > 0) {
            return locate + "%";
        }
        return locate;
    }

    public String getTimeUpString() {
        if (timeUp.length() > 0) {
            return timeUp + "秒";
        }
        return timeUp;
    }

    public String getTimeDownString() {
        if (timeDown.length() > 0) {
            return timeDown + "秒";
        }
        return timeDown;
    }

    public String getTimeErrorString() {
        if (timeError.length() > 0) {
            return timeError + "秒";
        }
        return timeError;
    }

    public String getContentString() {
        return "当前位置：" + getLocateString() + "\r\n"
                + "向上运转总时长：" + getTimeUpString() + "\r\n"
                + "向下运转总时长：" + getTimeDownString() + "\r\n"
                + "告警的间隔阈值：" + getTimeErrorString();
    }
}
