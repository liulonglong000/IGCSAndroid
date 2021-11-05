package com.xxs.igcsandroid.entity;

import org.json.JSONObject;

public class DignoseDetail {
    private String ddId;
    private int collId;
    private int collRow;
    private String collArea;
    private String frontFileName;
    private String front1FileName;
    private String front2FileName;
    private String front3FileName;
    private String front1Position;
    private String front2Position;
    private String front3Position;
    private String back1FileName;
    private String back2FileName;
    private String back3FileName;
    private String status;
    private String finishTime;

    public DignoseDetail() {
    }

    public void setDdId(String ddId) {
        this.ddId = ddId;
    }

    public void setCollId(int collId) {
        this.collId = collId;
    }

    public void setCollRow(int collRow) {
        this.collRow = collRow;
    }

    public void setCollArea(String collArea) {
        this.collArea = collArea;
    }

    public void setFrontFileName(String frontFileName) {
        this.frontFileName = frontFileName;
    }

    public void setFront1FileName(String front1FileName) {
        this.front1FileName = front1FileName;
    }

    public void setFront2FileName(String front2FileName) {
        this.front2FileName = front2FileName;
    }

    public void setFront3FileName(String front3FileName) {
        this.front3FileName = front3FileName;
    }

    public void setFront1Position(String front1Position) {
        this.front1Position = front1Position;
    }

    public void setFront2Position(String front2Position) {
        this.front2Position = front2Position;
    }

    public void setFront3Position(String front3Position) {
        this.front3Position = front3Position;
    }

    public void setBack1FileName(String back1FileName) {
        this.back1FileName = back1FileName;
    }

    public void setBack2FileName(String back2FileName) {
        this.back2FileName = back2FileName;
    }

    public void setBack3FileName(String back3FileName) {
        this.back3FileName = back3FileName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public DignoseDetail(JSONObject obj) throws Exception {
        this.ddId = obj.getString("ddId");
        this.collId = obj.getInt("collId");
        this.collRow = obj.getInt("collRow");
        this.collArea = obj.getString("collArea");
        this.frontFileName = obj.getString("frontFileName");
        this.front1FileName = obj.getString("front1FileName");
        this.front2FileName = obj.getString("front2FileName");
        this.front3FileName = obj.getString("front3FileName");
        this.front1Position = obj.getString("front1Position");
        this.front2Position = obj.getString("front2Position");
        this.front3Position = obj.getString("front3Position");
        this.back1FileName = obj.getString("back1FileName");
        this.back2FileName = obj.getString("back2FileName");
        this.back3FileName = obj.getString("back3FileName");
        this.status = obj.getString("status");
        this.finishTime = obj.getString("finishTime");
    }

    public String getDdId() {
        return ddId;
    }

    public int getCollId() {
        return collId;
    }

    public int getCollRow() {
        return collRow;
    }

    public String getCollArea() {
        return collArea;
    }

    public String getFrontFileName() {
        return frontFileName;
    }

    public String getFront1FileName() {
        return front1FileName;
    }

    public String getFront2FileName() {
        return front2FileName;
    }

    public String getFront3FileName() {
        return front3FileName;
    }

    public String getFront1Position() {
        return front1Position;
    }

    public String getFront2Position() {
        return front2Position;
    }

    public String getFront3Position() {
        return front3Position;
    }

    public String getBack1FileName() {
        return back1FileName;
    }

    public String getBack2FileName() {
        return back2FileName;
    }

    public String getBack3FileName() {
        return back3FileName;
    }

    public String getStatus() {
        return status;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public DignoseDetail(String ddId, int collId, int collRow, String collArea, String frontFileName, String front1FileName, String front2FileName, String front3FileName, String front1Position, String front2Position, String front3Position, String back1FileName, String back2FileName, String back3FileName, String status, String finishTime) {
        this.ddId = ddId;
        this.collId = collId;
        this.collRow = collRow;
        this.collArea = collArea;
        this.frontFileName = frontFileName;
        this.front1FileName = front1FileName;
        this.front2FileName = front2FileName;
        this.front3FileName = front3FileName;
        this.front1Position = front1Position;
        this.front2Position = front2Position;
        this.front3Position = front3Position;
        this.back1FileName = back1FileName;
        this.back2FileName = back2FileName;
        this.back3FileName = back3FileName;
        this.status = status;
        this.finishTime = finishTime;
    }

    @Override
    public String toString() {
        return "DignoseDetail{" +
                "ddId='" + ddId + '\'' +
                ", collId=" + collId +
                ", collRow=" + collRow +
                ", collArea='" + collArea + '\'' +
                ", frontFileName='" + frontFileName + '\'' +
                ", front1FileName='" + front1FileName + '\'' +
                ", front2FileName='" + front2FileName + '\'' +
                ", front3FileName='" + front3FileName + '\'' +
                ", front1Position='" + front1Position + '\'' +
                ", front2Position='" + front2Position + '\'' +
                ", front3Position='" + front3Position + '\'' +
                ", back1FileName='" + back1FileName + '\'' +
                ", back2FileName='" + back2FileName + '\'' +
                ", back3FileName='" + back3FileName + '\'' +
                ", status='" + status + '\'' +
                ", finishTime='" + finishTime + '\'' +
                '}';
    }
}
