package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.NodeInfoActivity;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.NodeInfo;
import com.xxs.igcsandroid.golbal.Constants;

import java.util.ArrayList;

public class NodeAdapter extends MyBaseAdapter {
    private ArrayList<NodeInfo> mLstGh;

    public NodeAdapter(Activity activity) {
        super(activity);
        mLstGh = new ArrayList<>();
    }

    public void setDataInfo(ArrayList<NodeInfo> lstEntity) {
        if (mLstGh != null) {
            mLstGh.clear();
        }
        mLstGh = lstEntity;
    }

    @Override
    public int getCount() {
        if (mLstGh == null) {
            return 0;
        } else {
            return mLstGh.size();
        }
    }

    static class ViewHolder {
        SmartImageView smvPic;
        TextView tvName;
        TextView tvInfo;
        TextView tvFrequency;
        TextView tvStatus;
        TextView tvTime;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_node, null);
            holder = new ViewHolder();
            holder.smvPic = view.findViewById(R.id.gh_pic);
            holder.tvName = view.findViewById(R.id.tv_name);
            holder.tvInfo = view.findViewById(R.id.tv_info);
            holder.tvFrequency = view.findViewById(R.id.tv_frequency);
            holder.tvStatus = view.findViewById(R.id.tv_connect_status);
            holder.tvTime = view.findViewById(R.id.tv_connect_time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        final NodeInfo entity = mLstGh.get(position);
        String picPath = Constants.SERVER_URL + "downloadFile/downloadFile?inputPath=";
        if (entity.getPicPath().length() > 0) {
            picPath = picPath + entity.getPicPath();
        } else {
            picPath = picPath + "IGCS_DEFAULT_NODE.png";
        }
        holder.smvPic.setImageUrl(picPath);
        holder.tvName.setText(entity.getFullName());
        holder.tvInfo.setText(entity.getInfo());
        holder.tvFrequency.setText("采集频率" + entity.getFrequencyString());
        holder.tvStatus.setText(entity.getStatus());
        holder.tvTime.setText(entity.getConnectTime());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, NodeInfoActivity.class);
                intent.putExtra("nodeId", entity.getNodeId());
                intent.putExtra("gwId", entity.getGwId());
                intent.putExtra("nodeName", entity.getNodeName());
                intent.putExtra("frequency", entity.getFrequencyData());
                intent.putExtra("addr", entity.getNodeAddr());
                intent.putExtra("remark", entity.getRemark());
                intent.putExtra("pic", entity.getPicPath());
                mActivity.startActivityForResult(intent, Constants.ACTIVITY_RESULT_NODE_RELOAD);
            }
        });

        return view;
    }
}
