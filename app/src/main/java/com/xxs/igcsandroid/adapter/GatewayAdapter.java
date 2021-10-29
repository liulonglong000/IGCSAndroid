package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.GatewayInfoActivity;
import com.xxs.igcsandroid.activity.GreenhouseShowActivity;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.GatewayInfo;
import com.xxs.igcsandroid.golbal.Constants;

import java.util.ArrayList;

public class GatewayAdapter extends MyBaseAdapter {
    private ArrayList<GatewayInfo> mList;
    private boolean bClickShowGh = false;

    public GatewayAdapter(Activity activity) {
        super(activity);
        mList = new ArrayList<>();
    }

    public void setbClickShowGh(boolean bClickShowGh) {
        this.bClickShowGh = bClickShowGh;
    }

    public void setDataInfo(ArrayList<GatewayInfo> lstEntity) {
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
        SmartImageView smvPic;
        TextView txtViewAccound;
        TextView tvInfo;
        TextView tvStatus;
        TextView tvTime;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_gateway, null);
            holder = new ViewHolder();
            holder.smvPic = view.findViewById(R.id.gh_pic);
            holder.txtViewAccound = view.findViewById(R.id.tv_account);
            holder.tvInfo = view.findViewById(R.id.tv_info);
            holder.tvStatus = view.findViewById(R.id.tv_connect_status);
            holder.tvTime = view.findViewById(R.id.tv_connect_time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        final GatewayInfo entity = mList.get(position);
        String picPath = Constants.SERVER_URL + "downloadFile/downloadFile?inputPath=";
        if (entity.getGatewayPic().length() > 0) {
            picPath = picPath + entity.getGatewayPic();
        } else {
            picPath = picPath + "IGCS_DEFAULT_GW.png";
        }
        holder.smvPic.setImageUrl(picPath);
        holder.txtViewAccound.setText(entity.getGatewayId() + "    " + entity.getGatewayName());
        holder.tvInfo.setText(entity.getGatewayInfo());
        holder.tvStatus.setText(entity.getStatus());
        holder.tvTime.setText(entity.getLastConnectTime());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bClickShowGh) {
                    Intent intent = new Intent(mActivity, GreenhouseShowActivity.class);
                    intent.putExtra("gatewayId", entity.getGatewayId());
                    intent.putExtra("gatewayName", entity.getGatewayId() + "    " + entity.getGatewayName());
                    mActivity.startActivity(intent);
                } else {
                    Intent intent = new Intent(mActivity, GatewayInfoActivity.class);
                    intent.putExtra("userId", entity.getUserId());
                    intent.putExtra("userRole", MyApplication.getInstance().getUserRole());
                    intent.putExtra("gatewayId", entity.getGatewayId());
                    mActivity.startActivityForResult(intent, 301);
                }
            }
        });

        return view;
    }
}
