package com.xxs.igcsandroid.util;

public class Time {
    private int year;
    private int month;
    private int date;

    public Time(){}

    public Time(int year, int month, int date) {
        this.year = year;
        this.month = month;
        this.date = date;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDate() {
        return date;
    }

    @Override
    public String toString() {
        String yearStr = String.valueOf(year);
        String monthStr = String.valueOf(month);
        String dateStr = String.valueOf(date);
        if(month<10){
            monthStr = "0" + monthStr;
        }
        if(date<10){
            dateStr = "0" + dateStr;
        }
        return yearStr + "-" + monthStr + "-" + dateStr;
    }
}
