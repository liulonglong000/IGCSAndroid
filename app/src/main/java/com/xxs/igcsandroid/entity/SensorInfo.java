package com.xxs.igcsandroid.entity;

import org.json.JSONObject;

public class SensorInfo {
    private String sensorId;
    private String sensorTypeCode;
    private String sensorTypeName;
    private String sensorUnit;
    private String atGwId;
    private String atNodeId;
    private String atNodeName;

    public SensorInfo() {

    }

    public SensorInfo(JSONObject obj) throws Exception {
        this.atGwId = obj.getString("gwId");
        this.atNodeId = obj.getString("nodeId");
        this.atNodeName = obj.getString("nodeName");
        this.sensorTypeCode = obj.getString("sensorType");
        this.sensorTypeName = obj.getString("sensorTypeName");
        this.sensorId = obj.getString("sensorId");
        this.sensorUnit = obj.getString("sensorUnit");
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getSensorTypeCode() {
        return sensorTypeCode;
    }

    public String getSensorTypeName() {
        return sensorTypeName;
    }

    public String getSensorUnit() {
        return sensorUnit;
    }

    public String getAtGwId() {
        return atGwId;
    }

    public String getAtNodeId() {
        return atNodeId;
    }

    public String getAtNodeName() {
        return atNodeName;
    }

    public String getFullName() {
        return atNodeName + "(网关" + atGwId + " 节点" + atNodeId + ")" + "  " + sensorTypeName + sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public void setSensorTypeCode(String sensorTypeCode) {
        this.sensorTypeCode = sensorTypeCode;
    }

    public void setSensorTypeName(String sensorTypeName) {
        this.sensorTypeName = sensorTypeName;
    }

    public void setSensorUnit(String sensorUnit) {
        this.sensorUnit = sensorUnit;
    }

    public void setAtGwId(String atGwId) {
        this.atGwId = atGwId;
    }

    public void setAtNodeId(String atNodeId) {
        this.atNodeId = atNodeId;
    }

    public void setAtNodeName(String atNodeName) {
        this.atNodeName = atNodeName;
    }
}
