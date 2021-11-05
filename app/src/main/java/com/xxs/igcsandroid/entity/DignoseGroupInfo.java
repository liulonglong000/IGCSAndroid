package com.xxs.igcsandroid.entity;

import com.xxs.igcsandroid.util.Time;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DignoseGroupInfo {
    private String dgId;
    private String dgHouseId;
    private Time dgInDate;
    private Time dgPreFinDate;
    private String dgStatus;
    private String dgReason;
    private int dgPlanNum;
    private int dgRealNum;
    private Time dgFinDate;
    public DignoseGroupInfo(JSONObject obj) throws Exception {
        this.dgId = obj.getString("dgId");
        this.dgHouseId = obj.getString("dgHouseId");
        this.dgInDate = strToTime(obj.getString("dgInDate"));
        this.dgPreFinDate = strToTime(obj.getString("dgPreFinDate"));
        this.dgStatus = obj.getString("dgStatus");
        this.dgReason = obj.getString("dgReason");
        this.dgPlanNum = obj.getInt("dgPlanNum");
        this.dgRealNum = obj.getInt("dgRealNum");
        this.dgFinDate = strToTime(obj.getString("dgFinDate"));
    }

    public String getDgId() {
        return dgId;
    }

    public String getDgHouseId() {
        return dgHouseId;
    }

    public Time getDgInDate() {
        return dgInDate;
    }

    public Time getDgPreFinDate() {
        return dgPreFinDate;
    }

    public String getDgStatus() {
        return dgStatus;
    }

    public String getDgReason() {
        return dgReason;
    }

    public int getDgPlanNum() {
        return dgPlanNum;
    }

    public int getDgRealNum() {
        return dgRealNum;
    }

    public Time getDgFinDate() {
        return dgFinDate;
    }

    Time strToTime(String time){
        int a=0,b=0,c=0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            a = calendar.get(Calendar.YEAR);
            b = calendar.get(Calendar.MONTH)+1;
            c = calendar.get(Calendar.DATE);
        }catch (Exception e){
            e.printStackTrace();
        }
        Time time1 = new Time(a,b,c);
        return time1;
    }
}
