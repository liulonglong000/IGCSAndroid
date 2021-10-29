package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.GatewayShowActivity;
import com.xxs.igcsandroid.entity.ParkInfo;
import com.xxs.igcsandroid.golbal.Constants;

import java.util.ArrayList;

public class parkAdapter extends MyBaseAdapter {
    private ArrayList<ParkInfo> mList;

    public parkAdapter(Activity activity) {
        super(activity);
        mList = new ArrayList<>();
    }

    public void setDataInfo(ArrayList<ParkInfo> lstEntity) {
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
            view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_park, null);
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

        final ParkInfo entity = mList.get(position);
        String picPath = Constants.SERVER_URL + "downloadFile/downloadFile?inputPath=";
        if (entity.getPicPath().length() > 0) {
            picPath = picPath + entity.getPicPath();
        } else {
            picPath = picPath + "IGCS_DEFAULT_PARK.png";
        }
        holder.smvPic.setImageUrl(picPath);
        holder.tvName.setText(entity.getParkName());
        holder.tvInfo.setText(entity.getInfo());
        holder.tvFrequency.setText("占地面积" + entity.getAreaString());
        holder.tvStatus.setText(entity.getUserName());
        holder.tvTime.setText(entity.getAddTime());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, GatewayShowActivity.class);
                intent.putExtra("parkId", entity.getParkId());
                intent.putExtra("parkName", entity.getParkName());
                mActivity.startActivity(intent);
            }
        });

        return view;
    }
}
