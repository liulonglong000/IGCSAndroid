package com.xxs.igcsandroid.entity;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class EncodeInfo {
    private String gwId;
    private String nodeId;
    private String nodeName;

    private String equiId;
    private String equiType;
    private String equiName;
    private String curQs;
    private String totalQs;

    public EncodeInfo(JSONObject obj, String gwId, String nodeId, String nodeName) throws JSONException {
        this.gwId = gwId;
        this.nodeId = nodeId;
        this.nodeName = nodeName;

        this.equiId = obj.getString("equiId");
        this.equiType = obj.getString("equiTypeString");
        this.equiName = obj.getString("equiName");
        this.curQs = obj.getString("curQs");
        this.totalQs = obj.getString("totalQs");
    }

    public EncodeInfo(Bundle obj) {
        this.gwId = obj.getString("gwId");
        this.nodeId = obj.getString("nodeId");
        this.nodeName = obj.getString("nodeName");
        this.equiId = obj.getString("equiId");
        this.equiType = obj.getString("equiTypeString");
        this.equiName = obj.getString("equiName");
        this.curQs = obj.getString("curQs");
        this.totalQs = obj.getString("totalQs");
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

    public String getEquiId() {
        return equiId;
    }

    public String getEquiType() {
        return equiType;
    }

    public String getEquiName() {
        return equiName;
    }

    public String getCurQs() {
        return curQs;
    }

    public String getTotalQs() {
        return totalQs;
    }

    public String getNameString() {
        return nodeName + "(网关" + gwId + " 节点" + nodeId + ") " + equiName + equiId;
    }

    public String getEquipNameString() {
        return equiName + equiId;
    }

    public String getContentString() {
        return "当前圈数：" + curQs + "\r\n"
                + "总  圈  数：" + totalQs;
    }
}
