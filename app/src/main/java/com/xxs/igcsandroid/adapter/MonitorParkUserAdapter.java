package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.MonitorNodeActivity;
import com.xxs.igcsandroid.entity.GreenhouseInfo;
import com.xxs.igcsandroid.golbal.Constants;

import java.util.ArrayList;

public class MonitorParkUserAdapter extends MyBaseAdapter {
    private ArrayList<GreenhouseInfo> mUserList;

    public MonitorParkUserAdapter(Activity activity) {
        super(activity);
        mUserList = new ArrayList<>();
    }

    public void setDataInfo(ArrayList<GreenhouseInfo> lstEntity) {
        if (mUserList != null) {
            mUserList.clear();
        }
        mUserList = lstEntity;
    }

    @Override
    public int getCount() {
        if (mUserList == null) {
            return 0;
        } else {
            return mUserList.size();
        }
    }

    static class ViewHolder {
        SmartImageView smvPic;
        TextView txtViewName;
        TextView txtViewDetail;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_monitor_park_user, null);
            holder = new ViewHolder();
            holder.smvPic = view.findViewById(R.id.gh_pic);
            holder.txtViewName = view.findViewById(R.id.tv_name);
            holder.txtViewDetail = view.findViewById(R.id.tv_detail);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        final GreenhouseInfo entity = mUserList.get(position);
        String picPath = Constants.SERVER_URL + "downloadFile/downloadFile?inputPath=";
        if (entity.getPicPath().length() > 0) {
            picPath = picPath + entity.getPicPath();
        } else {
            picPath = picPath + "IGCS_DEFAULT_GH.png";
        }
        holder.smvPic.setImageUrl(picPath);
        holder.txtViewName.setText(entity.getGreenhouseName());
        holder.txtViewDetail.setText(entity.getGreenhouseAddr());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MonitorNodeActivity.class);
                intent.putExtra("ghId", entity.getGreenhouseId());
                intent.putExtra("ghName", entity.getGreenhouseName());
//                intent.putExtra("gwId", entity.getGatewayId());
                mActivity.startActivity(intent);
            }
        });

        return view;
    }
}
