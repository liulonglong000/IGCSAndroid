package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.AcThresholdGroupInfoActivity;
import com.xxs.igcsandroid.activity.AcThresholdGroupMgrActivity;
import com.xxs.igcsandroid.entity.ThresholdGroupInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ThresholdGroupAdapter extends MyBaseAdapter {
    private String mGhId;
    private String mGhName;
    private ArrayList<ThresholdGroupInfo> mList;

    public ThresholdGroupAdapter(Activity activity) {
        super(activity);
        mList = new ArrayList<>();
    }

    public void setInfo(String ghId, String ghName) {
        mGhId = ghId;
        mGhName = ghName;
    }

    public void setDataInfo(ArrayList<ThresholdGroupInfo> lstEntity) {
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
        TextView tvName;
        TextView tvRemark;
        TextView tvInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_threshold_group, null);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvRemark = convertView.findViewById(R.id.tv_remark);
            holder.tvInfo = convertView.findViewById(R.id.tv_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final ThresholdGroupInfo entity = mList.get(position);

        holder.tvName.setText(entity.getTgName());
        String remark = entity.getRemark();
        if (remark.length() == 0) {
            remark = "暂无说明信息！";
        }
        holder.tvRemark.setText(remark);
        holder.tvInfo.setText(entity.getUserName() + "        " + entity.getMdyTime());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AcThresholdGroupInfoActivity.class);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                intent.putExtra("tgId", entity.getTgId());
                mActivity.startActivity(intent);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DlgUtil.showMsgInfo(mActivity, "确定要删除该阈值组吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoDelThresholdGroup(entity.getTgId());
                    }
                });
                return true;
            }
        });

        return convertView;
    }

    private void gotoDelThresholdGroup(String tgId) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("tgId", tgId);

        AsyncSocketUtil.post(mActivity, "autoctrl/delThresholdGroupInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                DlgUtil.showMsgInfo(mActivity, "删除阈值组信息成功！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((AcThresholdGroupMgrActivity)mActivity).getTgFromServer();
                    }
                });
            }
        }, null);
    }
}
