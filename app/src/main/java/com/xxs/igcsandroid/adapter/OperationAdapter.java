package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.AcOperationAlarmActivity;
import com.xxs.igcsandroid.activity.AcOperationGroupInfoActivity;
import com.xxs.igcsandroid.activity.AcOperationInfoActivity;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.OperationInfo;
import com.xxs.igcsandroid.golbal.Constants;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class OperationAdapter extends MyBaseAdapter {
    private ArrayList<OperationInfo> mList;

    private String mGhId;
    private String mGhName;
    private String mOperationGroupId;

    public OperationAdapter(Activity activity, String thresholdGroupId, String ghId, String ghName) {
        super(activity);
        mList = new ArrayList<>();
        mOperationGroupId = thresholdGroupId;
        mGhId = ghId;
        mGhName = ghName;
    }

    public void setDataInfo(ArrayList<OperationInfo> lstEntity) {
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

        final OperationInfo entity = mList.get(position);

        holder.txtViewName.setText(entity.getEquipFullName());
        holder.txtViewInfo.setText(entity.getInfo());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (entity.getControlType().startsWith("ALARM")) {
                    intent = new Intent(mActivity, AcOperationAlarmActivity.class);
                } else {
                    intent = new Intent(mActivity, AcOperationInfoActivity.class);
                }
                intent.putExtra("ogId", mOperationGroupId);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                intent.putExtra("operationId", entity.getOperateId());
                intent.putExtra("fullName", entity.getEquipFullName());
                intent.putExtra("controlType", entity.getControlType());
                intent.putExtra("para1", entity.getControlParameter1());
                intent.putExtra("para2", entity.getControlParameter2());
                intent.putExtra("para3", entity.getControlParameter3());
                mActivity.startActivityForResult(intent, Constants.ACTIVITY_RESULT_OG_RELOAD);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DlgUtil.showMsgInfo(mActivity, "确定要删除该操作吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoDelOperation(entity.getOperateId());
                    }
                });
                return true;
            }
        });

        return convertView;
    }

    private void gotoDelOperation(String operateId) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("operationId", operateId);
        mp.put("userId", MyApplication.getInstance().getMyUserId());

        AsyncSocketUtil.post(mActivity, "autoctrl/delOperationInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                DlgUtil.showMsgInfo(mActivity, "删除操作信息成功！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((AcOperationGroupInfoActivity)mActivity).getOperationGroupFromSrv();
                    }
                });
            }
        }, null);
    }
}
