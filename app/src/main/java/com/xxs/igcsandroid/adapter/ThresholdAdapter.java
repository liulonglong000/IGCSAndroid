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
import com.xxs.igcsandroid.activity.AcThresholdInfoActivity;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.ThresholdInfo;
import com.xxs.igcsandroid.golbal.Constants;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ThresholdAdapter extends MyBaseAdapter {
    private ArrayList<ThresholdInfo> mList;

    private String mGhId;
    private String mGhName;
    private String mThresholdGroupId;

    public ThresholdAdapter(Activity activity, String thresholdGroupId, String ghId, String ghName) {
        super(activity);
        mList = new ArrayList<>();
        mThresholdGroupId = thresholdGroupId;
        mGhId = ghId;
        mGhName = ghName;
    }

    public void setDataInfo(ArrayList<ThresholdInfo> lstEntity) {
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
        TextView txtViewName;
        TextView txtViewInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_threshold, null);
            holder = new ViewHolder();
            holder.txtViewName = convertView.findViewById(R.id.tv_name);
            holder.txtViewInfo = convertView.findViewById(R.id.tv_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final ThresholdInfo entity = mList.get(position);

        holder.txtViewName.setText(entity.getSensorFullName());
        holder.txtViewInfo.setText(entity.getInfo());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AcThresholdInfoActivity.class);
                intent.putExtra("tgId", mThresholdGroupId);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                intent.putExtra("thresholdId", entity.getThresholdId());
                intent.putExtra("fullName", entity.getSensorFullName());
                intent.putExtra("typePara", entity.getTypePara());
                intent.putExtra("typeId", entity.getTypeCode());
                intent.putExtra("compPara", entity.getCompPara());
                intent.putExtra("compId", entity.getCompCode());
                intent.putExtra("unit", entity.getUnit());
                mActivity.startActivityForResult(intent, Constants.ACTIVITY_RESULT_TG_RELOAD);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DlgUtil.showMsgInfo(mActivity, "确定要删除该阈值吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoDelThreshold(entity.getThresholdId());
                    }
                });
                return true;
            }
        });

        return convertView;
    }

    private void gotoDelThreshold(String thresholdId) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("thresholdId", thresholdId);
        mp.put("userId", MyApplication.getInstance().getMyUserId());

        AsyncSocketUtil.post(mActivity, "autoctrl/delThresholdInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                DlgUtil.showMsgInfo(mActivity, "删除阈值信息成功！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((AcThresholdGroupInfoActivity)mActivity).getThresholdGroupFromSrv();
                    }
                });
            }
        }, null);
    }
}
