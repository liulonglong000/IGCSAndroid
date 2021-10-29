package com.xxs.igcsandroid.entity;

import org.json.JSONObject;

public class NodeInfo {
    private String nodeId;
    private String gwId;
    private String nodeName;
    private String nodeAddr;
    private String addTime;
    private String connectTime;
    private String remark;
    private String status;
    private String picPath;
    private Integer frequency;

    public NodeInfo(JSONObject obj, boolean bAll) throws Exception {
        this.gwId = obj.getString("gwId");
        this.nodeId = obj.getString("nodeId");
        this.nodeName = obj.getString("nodeName");
        this.status = obj.getString("status");

        if (bAll) {
            this.nodeAddr = obj.getString("nodeAddr");
            this.addTime = obj.getString("addTime");
            this.connectTime = obj.getString("connectTime");
            this.remark = obj.getString("remark");
            this.picPath = obj.getString("picPath");
            if (obj.has("frequency")) {
                this.frequency = obj.getInt("frequency");
            }
        }
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getGwId() {
        return gwId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeAddr() {
        return nodeAddr;
    }

    public String getAddTime() {
        return addTime;
    }

    public String getConnectTime() {
        return connectTime;
    }

    public String getRemark() {
        return remark;
    }

    public String getStatus() {
        return status;
    }

    public String getPicPath() {
        return picPath;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public String getInfo() {
        if (nodeAddr.length() == 0 && remark.length() == 0) {
            return "暂无描述信息";
        }
        if (nodeAddr.length() == 0) {
            return remark;
        }
        if (remark.length() == 0) {
            return nodeAddr;
        }
        return nodeAddr + "，" + remark;
    }

    public String getFrequencyString() {
        if (frequency == null) {
            return "未知";
        }
        return frequency + "分钟";
    }

    public String getFrequencyData() {
        if (frequency == null) {
            return "";
        }
        return frequency + "";
    }

    public String getKeyId() {
        return gwId + "-" + nodeId;
    }

    public String getValueShow() {
        return nodeName + "(网关" + gwId + " 节点" + nodeId + ", " + status + ")";
    }

    public String getFullName() {
        return nodeName + "(网关" + gwId + " 节点" + nodeId + ")";
    }
}
