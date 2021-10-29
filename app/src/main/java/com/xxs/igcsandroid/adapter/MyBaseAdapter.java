package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.widget.BaseAdapter;

import com.xxs.igcsandroid.entity.AlarmInfo;
import com.xxs.igcsandroid.entity.GatewayInfo;
import com.xxs.igcsandroid.entity.GreenhouseInfo;

import java.util.ArrayList;

public abstract class MyBaseAdapter extends BaseAdapter {
    protected Activity mActivity;

    public MyBaseAdapter(Activity activity) {
        super();
        mActivity = activity;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
