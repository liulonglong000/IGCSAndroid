package com.xxs.igcsandroid.entity;

import org.json.JSONObject;

public class ThresholdInfo {
    private String thresholdId;
    private SensorInfo sensorInfo = new SensorInfo();

    private String typeCode;
    private String typeCodeName;
    private String typePara;                // 秒的值

    private String compCode;
    private String compCodeName;
    private Integer compPara;               // 传感器的数值

    private String addDate;

    public ThresholdInfo(JSONObject obj) throws Exception {
        this.thresholdId = obj.getString("id");

        sensorInfo.setAtGwId(obj.getString("gwId"));
        sensorInfo.setAtNodeId(obj.getString("nodeId"));
        sensorInfo.setAtNodeName(obj.getString("nodeName"));
        sensorInfo.setSensorTypeCode(obj.getString("sensorType"));
        sensorInfo.setSensorTypeName(obj.getString("sensorTypeName"));
        sensorInfo.setSensorId(obj.getString("sensorId"));
        sensorInfo.setSensorUnit(obj.getString("unit"));

        this.typeCode = obj.getString("typeCode");
        this.typeCodeName = obj.getString("typeName");
        this.typePara = obj.getString("typePara");
        this.compCode = obj.getString("compCode");
        this.compCodeName = obj.getString("compName");
        this.compPara = obj.getInt("compPara");
        this.addDate = obj.getString("addDate");
    }

    public ThresholdInfo(String typePara, String typeId, Integer compPara, String compId, String unit) {
        this.typePara = typePara;
        this.typeCode = typeId;
        this.compPara = compPara;
        this.compCode = compId;
        sensorInfo.setSensorUnit(unit);
    }

    public String getThresholdId() {
        return thresholdId;
    }

    public String getNodeId() {
        return sensorInfo.getAtNodeId();
    }

    public String getSensorTypeCode() {
        return sensorInfo.getSensorTypeCode();
    }

    public String getSensorTypeName() {
        return sensorInfo.getSensorTypeName();
    }

    public String getSensorId() {
        return sensorInfo.getSensorId();
    }

    public String getTypeCode() {
        return typeCode;
    }

    public String getTypeCodeName() {
        return typeCodeName;
    }

    public String getTypePara() {
        return typePara;
    }

    public String getCompCode() {
        return compCode;
    }

    public String getCompCodeName() {
        return compCodeName;
    }

    public Integer getCompPara() {
        return compPara;
    }

    public String getAddDate() {
        return addDate;
    }

    public String getSensorFullName() {
        return sensorInfo.getFullName();
    }

    public String getInfo() {
        return typePara + "秒内" + typeCodeName + compCodeName + compPara + sensorInfo.getSensorUnit();
    }

    public String getUnit() {
        return sensorInfo.getSensorUnit();
    }
}
