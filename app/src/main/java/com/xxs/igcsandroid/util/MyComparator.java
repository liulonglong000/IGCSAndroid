package com.xxs.igcsandroid.util;

import com.xxs.igcsandroid.entity.DignoseGroupInfo;

import java.util.Comparator;

public class MyComparator implements Comparator<DignoseGroupInfo> {

    @Override
    public int compare(DignoseGroupInfo a, DignoseGroupInfo b) {
        if((a.getDgStatus().equals("1"))&&!(b.getDgStatus().equals("1"))){
            return -1;
        }else if((b.getDgStatus().equals("1"))&&!(a.getDgStatus().equals("1"))){
            return 1;
        }else{
            if(a.getDgInDate().getYear() > b.getDgInDate().getYear()){
                return -1;
            }else if(a.getDgInDate().getYear() < b.getDgInDate().getYear()){
                return 1;
            }
            else {
                if(a.getDgInDate().getMonth() > b.getDgInDate().getMonth()){
                    return -1;
                }else if(a.getDgInDate().getMonth() < b.getDgInDate().getMonth()){
                    return 1;
                }else {
                    if(a.getDgInDate().getDate() > b.getDgInDate().getDate()) {
                        return -1;
                    }else return 1;
                }
            }
        }
    }
}
