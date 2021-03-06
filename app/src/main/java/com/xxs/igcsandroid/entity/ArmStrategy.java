package com.xxs.igcsandroid.entity;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class ArmStrategy {
    private String armStrategyId;
    private String gatewayId;
    private String nodeId;
    private String nodeName;
    private Integer thresholdId;
    private String thresholdType;
    private Integer isUse;
    private String startHour;
    private String startMin;
    private String endHour;
    private String endMin;
    private String sensorTypeCode;
    private String sensorId;
    private String sensorUnit;
    private String compareType;
    private String comparePara;
    private String equipmentType;
    private String equipmentId;
    private String operateType;
    private String operatePara;

    public ArmStrategy(JSONObject obj, String gwId, String nodeId, String nodeName) throws JSONException {
        this.armStrategyId = obj.getString("id");
        this.gatewayId = gwId;
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.thresholdId = obj.getInt("armId");
        this.thresholdType = obj.getString("type");
        this.isUse = obj.getInt("isUse");
        this.startHour = obj.getString("startH");
        this.startMin = obj.getString("startM");
        this.endHour = obj.getString("endH");
        this.endMin = obj.getString("endM");
        this.sensorTypeCode = obj.getString("sensorType");
        this.sensorId = obj.getString("sensorId");
        this.sensorUnit = obj.getString("sensorUnit");
        this.compareType = obj.getString("compType");
        this.comparePara = obj.getString("compPara");
        this.equipmentType = obj.getString("equipType");
        this.equipmentId = obj.getString("equipId");
        this.operateType = obj.getString("opertType");
        this.operatePara = obj.getString("opertPara");
    }

    public ArmStrategy(Bundle obj) {
        this.armStrategyId = obj.getString("id");
        this.gatewayId = obj.getString("gwId");
        this.nodeId = obj.getString("nodeId");
        this.nodeName = obj.getString("nodeName");
        this.thresholdId = obj.getInt("armId");
        this.thresholdType = obj.getString("type");
        this.isUse = obj.getInt("isUse");
        this.startHour = obj.getString("startH");
        this.startMin = obj.getString("startM");
        this.endHour = obj.getString("endH");
        this.endMin = obj.getString("endM");
        this.sensorTypeCode = obj.getString("sensorType");
        this.sensorId = obj.getString("sensorId");
        this.sensorUnit = obj.getString("sensorUnit");
        this.compareType = obj.getString("compType");
        this.comparePara = obj.getString("compPara");
        this.equipmentType = obj.getString("equipType");
        this.equipmentId = obj.getString("equipId");
        this.operateType = obj.getString("opertType");
        this.operatePara = obj.getString("opertPara");
    }

    public String getThresholdTypeString() {
        if (thresholdType.equals("time")) {
            return "???????????????";
        } else if (thresholdType.equals("value")) {
            return "???????????????";
        } else {
            return "?????????";
        }
    }

    public String getPositionString() {
        return nodeName + "(??????" + gatewayId + " ??????" + nodeId + ")";
    }

    public String getIsUseString() {
        if (isUse == 1) {
            return "??????";
        } else {
            return "?????????";
        }
    }

    public boolean getIsUseBool() {
        if (isUse == 1) {
            return true;
        } else {
            return false;
        }
    }

    public Integer getIsUse() {
        return isUse;
    }

    public String getThresholdType() {
        return thresholdType;
    }

    public String getTimeContent() {
        return "???" + getStartHourEx() + ":" + getStartMinEx() + "??????\r\n"
                + equipmentType + equipmentId + "???????????????" + operateType + "\r\n"
                + "?????????" + operatePara + "???";
    }

    public String getValueContent() {
        return "???" + getStartHourEx() + ":" + getStartMinEx() + "???" + getEndHourEx() + ":" + getEndMinEx() + "\r\n"
                + "???" + sensorTypeCode + sensorId + "??????" + compareType + comparePara + sensorUnit + "???\r\n"
                + equipmentType + equipmentId + "???????????????" + operateType + "\r\n"
                + "?????????" + operatePara + "???";
    }

    public String getArmStrategyId() {
        return armStrategyId;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public Integer getThresholdId() {
        return thresholdId;
    }

    public String getStartHour() {
        return startHour;
    }

    public String getStartHourEx() {
        if (startHour.length() == 2) {
            return startHour;
        } else {
            return "0" + startHour;
        }
    }

    public String getStartMin() {
        return startMin;
    }

    public String getStartMinEx() {
        if (startMin.length() == 2) {
            return startMin;
        } else {
            return "0" + startMin;
        }
    }

    public String getEndHour() {
        return endHour;
    }

    public String getEndHourEx() {
        if (endHour.length() == 2) {
            return endHour;
        } else {
            return "0" + endHour;
        }
    }

    public String getEndMin() {
        return endMin;
    }

    public String getEndMinEx() {
        if (endMin.length() == 2) {
            return endMin;
        } else {
            return "0" + endMin;
        }
    }

    public String getSensorTypeCode() {
        return sensorTypeCode;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getSensorUnit() {
        return sensorUnit;
    }

    public String getCompareType() {
        return compareType;
    }

    public String getComparePara() {
        return comparePara;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getOperateType() {
        return operateType;
    }

    public String getOperatePara() {
        return operatePara;
    }

    public String getEquipmentInfo() {
        return equipmentType + equipmentId + "???????????????" + operateType;
    }

    public String getStartTimeString() {
        return getStartHourEx() + ":" + getStartMinEx();
    }

    public String getSensorInfo1() {
        return "???" + sensorTypeCode + sensorId + "??????" + compareType;
    }

    public String getSensorInfo2() {
        return sensorUnit + "???";
    }

    public String getEndTimeString() {
        return getEndHourEx() + ":" + getEndMinEx();
    }
}
