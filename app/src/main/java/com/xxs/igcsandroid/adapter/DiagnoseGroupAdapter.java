package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.AcCollectActivity;
import com.xxs.igcsandroid.activity.AcDiagnosisListActivity;
import com.xxs.igcsandroid.activity.AcThresholdGroupInfoActivity;
import com.xxs.igcsandroid.activity.AcThresholdGroupMgrActivity;
import com.xxs.igcsandroid.activity.GreenhouseInfoActivity;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.DignoseGroupInfo;
import com.xxs.igcsandroid.entity.ThresholdGroupInfo;
import com.xxs.igcsandroid.golbal.Constants;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiagnoseGroupAdapter extends MyBaseAdapter {
    private String mGhId;
    private String mGhName;
    private ArrayList<DignoseGroupInfo> mList;

    public DiagnoseGroupAdapter(Activity activity) {
        super(activity);
        mList = new ArrayList<>();
    }

    public void setInfo(String ghId, String ghName) {
        mGhId = ghId;
        mGhName = ghName;
    }

    public void setDataInfo(ArrayList<DignoseGroupInfo> lstEntity) {
        if (mList != null) {
            mList.clear();
        }
        mList = lstEntity;
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
        TextView gvId;
        TextView gvTime;
        TextView gvReason;
        TextView gvNumber;
        TextView gvStatus;
        Button start;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_diagnose_group, null);
            holder = new ViewHolder();
            holder.gvId = convertView.findViewById(R.id.gv_id);
            holder.gvTime = convertView.findViewById(R.id.gv_time);
            holder.gvReason = convertView.findViewById(R.id.gv_reason);
            holder.gvNumber = convertView.findViewById(R.id.gv_number);
            holder.gvStatus = convertView.findViewById(R.id.gv_status);
            holder.start = convertView.findViewById(R.id.start);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DignoseGroupInfo entity = mList.get(position);

        String status = entity.getDgStatus();
        System.out.println("status"+status);
        String statusStr = "";
        switch (status) {
            case "0":
                statusStr = "未采集";
                break;
            case "1":
                statusStr = "采集中";
                break;
            case "2":
                statusStr = "采集完成";
                break;
            case "3":
                statusStr = "已过期";
                break;
        }

        holder.gvId.setText("采集计划编号：" + entity.getDgId());
        holder.gvTime.setText("生成日期：" + entity.getDgInDate());
        holder.gvReason.setText("采集原因：" + entity.getDgReason());
        holder.gvNumber.setText("计划收集株数：" + entity.getDgPlanNum());
        holder.gvStatus.setText("采集状态：" + statusStr);

        holder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, AcCollectActivity.class);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("mGhName", mGhName);
                mActivity.startActivityForResult(intent, Constants.ACTIVITY_RESULT_GH_RELOAD);
            }
        });

        return convertView;

    }



}
