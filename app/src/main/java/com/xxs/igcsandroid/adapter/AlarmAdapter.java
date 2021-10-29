package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.entity.AlarmInfo;

import java.util.ArrayList;

public class AlarmAdapter extends MyBaseAdapter {
    private ArrayList<AlarmInfo> mList;

    public AlarmAdapter(Activity activity) {
        super(activity);
        mList = new ArrayList<>();
    }

    public void setDataInfo(ArrayList<AlarmInfo> lstEntity) {
        if (mList != null) {
            mList.clear();
        }
        mList = lstEntity;
    }

    public void addDataInfo(ArrayList<AlarmInfo> lstEntity) {
        mList.addAll(lstEntity);
    }

    public void addDataInfoToFront(ArrayList<AlarmInfo> lstEntity) {
        mList.addAll(0, lstEntity);
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        } else {
            return mList.size();
        }
    }

    static class ViewHolder {
        ImageView ivRead;
        TextView tvInfo;
        TextView tvTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_alarm, null);
            holder = new ViewHolder();
            holder.ivRead = convertView.findViewById(R.id.iv_pic);
            holder.tvInfo = convertView.findViewById(R.id.tv_alarm_info);
            holder.tvTime = convertView.findViewById(R.id.tv_alarm_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final AlarmInfo entity = mList.get(position);
        if (entity.getaIsRead().equals("0")) {
            holder.ivRead.setImageResource(R.drawable.ic_alarm_new);
        } else {
            holder.ivRead.setImageResource(R.drawable.ic_alarm_all);
        }
        holder.tvInfo.setText(entity.getaMessage());
        holder.tvTime.setText(entity.getaTime());

        return convertView;
    }
}
