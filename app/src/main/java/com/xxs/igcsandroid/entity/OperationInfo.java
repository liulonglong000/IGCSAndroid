package com.xxs.igcsandroid.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class OperationInfo {
    private String operateId;
    private String gwId;
    private String nodeId;
    private String equipmentTypeCode;
    private String equipmentId;
    private String controlType;
    private String controlParameter1;
    private String controlParameter2;
    private String controlParameter3;
    private String addDate;
    private String nodeName;
    private String equipmentTypeName;
    private String controlTypeName;

    private Short type;
    private Integer equipPara;

    private Map<String, String> mpControl;

    public OperationInfo(JSONObject obj) throws Exception {
        this.operateId = obj.getString("id");
        this.gwId = obj.getString("gwId");
        this.nodeId = obj.getString("nodeId");
        this.nodeName = obj.getString("nodeName");
        if (obj.has("equipType")) {
            this.equipmentTypeCode = obj.getString("equipType");
            this.equipmentTypeName = obj.getString("equipTypeName");
            this.equipmentId = obj.getString("equipId");
        }
        this.controlType = obj.getString("controlType");
        this.controlTypeName = obj.getString("controlTypeName");
        this.controlParameter1 = obj.getString("para1");
        this.controlParameter2 = obj.getString("para2");
        this.controlParameter3 = obj.getString("para3");
        this.addDate = obj.getString("addDate");
    }

    public OperationInfo(JSONArray jsonArray) throws Exception {
        JSONObject obj = jsonArray.getJSONObject(0);

        this.gwId = obj.getString("gwId");
        this.nodeId = obj.getString("nodeId");
        this.nodeName = obj.getString("nodeName");
        if (obj.has("equipType")) {
            this.equipmentTypeCode = obj.getString("equipType");
            this.equipmentTypeName = obj.getString("equipTypeName");
            this.equipmentId = obj.getString("equipId");
        }
        this.type = (short)obj.getInt("type");
        if (equipmentTypeCode.equals("RBM") || equipmentTypeCode.equals("FRM")) {
            if (obj.has("equipPara")) {
                this.equipPara = obj.getInt("equipPara");
            } else {
                this.equipPara = -1;
            }
        }

        mpControl = new LinkedHashMap<>();
        for (int i = 1; i < jsonArray.length(); i++) {
            JSONObject objCtrl = jsonArray.getJSONObject(i);
            mpControl.put(objCtrl.getString("ctrlId"), objCtrl.getString("ctrlName"));
        }
    }

    public OperationInfo(String controlType, String para1, String para2, String para3) {
        this.controlType = controlType;
        this.controlParameter1 = para1;
        this.controlParameter2 = para2;
        this.controlParameter3 = para3;
    }

    public String getOperateId() {
        return operateId;
    }

    public String getGwId() {
        return gwId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getEquipmentTypeCode() {
        return equipmentTypeCode;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getControlType() {
        return controlType;
    }

    public String getControlParameter1() {
        return controlParameter1;
    }

    public String getControlParameter2() {
        return controlParameter2;
    }

    public String getControlParameter3() {
        return controlParameter3;
    }

    public String getAddDate() {
        return addDate;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getEquipmentTypeName() {
        return equipmentTypeName;
    }

    public String getControlTypeName() {
        return controlTypeName;
    }

    public Short getType() {
        return type;
    }

    public Integer getEquipPara() {
        return equipPara;
    }

    public String getEquipFullName() {
        if (equipmentTypeName == null) {
            return "告警操作";
        }
        return getFullName();
    }

    public String getInfo() {
        String info = controlTypeName;
        String paras = "";
        if (controlParameter1.length() > 0) {
            if (equipmentTypeName == null) {
                paras += "隔" + controlParameter1 + "分钟通知一次";
            } else {
                paras += controlParameter1 + "秒";
            }
        }
        if (controlParameter2.length() > 0) {
            if (paras.length() > 0) {
                paras += ", ";
            }
            paras += controlParameter2 + "%";
        }
        if (controlParameter3.length() > 0) {
            if (paras.length() > 0) {
                paras += ", ";
            }
            paras += controlParameter3;
        }
        if (paras.length() > 0) {
            info += "（" + paras + "）";
        }
        return info;
    }

    public String getFullName() {
        return nodeName + "(网关" + gwId + " 节点" + nodeId + ")" + "  " + equipmentTypeName + equipmentId;
    }
}
