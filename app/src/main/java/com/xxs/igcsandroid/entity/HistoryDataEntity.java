package com.xxs.igcsandroid.entity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class HistoryDataEntity implements Serializable {
    private String time;
    private Map<Integer, Float> sensorData;

    public HistoryDataEntity(String time) {
        this.time = time;
        sensorData = new LinkedHashMap<Integer, Float>();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Map<Integer, Float> getSensorData() {
        return sensorData;
    }

    public void setSensorData(Map<Integer, Float> sensorData) {
        this.sensorData = sensorData;
    }

    public String getSensorDataById(int n) {
        if (sensorData.containsKey(n)) {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(sensorData.get(n));
        } else {
            return "";
        }
    }
}
