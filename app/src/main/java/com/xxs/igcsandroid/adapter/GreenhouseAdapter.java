package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.GreenhouseInfoActivity;
import com.xxs.igcsandroid.activity.NodeManageActivity;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.GreenhouseInfo;
import com.xxs.igcsandroid.golbal.Constants;

import java.util.ArrayList;

public class GreenhouseAdapter extends MyBaseAdapter {
    private ArrayList<GreenhouseInfo> mLstGh;

    public GreenhouseAdapter(Activity activity) {
        super(activity);
        mLstGh = new ArrayList<>();
    }

    public void setDataInfo(ArrayList<GreenhouseInfo> lstEntity) {
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
        TextView tvAddr;
        TextView tvRemark;
        TextView tvUser;
        TextView tvTime;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_greenhouse, null);
            holder = new ViewHolder();
            holder.smvPic = view.findViewById(R.id.gh_pic);
            holder.tvName = view.findViewById(R.id.tv_name);
            holder.tvName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            holder.tvAddr = view.findViewById(R.id.tv_addr);
            holder.tvRemark = view.findViewById(R.id.tv_remark);
            holder.tvUser = view.findViewById(R.id.tv_user);
            holder.tvTime = view.findViewById(R.id.tv_add_time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        final GreenhouseInfo entity = mLstGh.get(position);
        String picPath = Constants.SERVER_URL + "downloadFile/downloadFile?inputPath=";
        if (entity.getPicPath().length() > 0) {
            picPath = picPath + entity.getPicPath();
        } else {
            picPath = picPath + "IGCS_DEFAULT_GH.png";
        }
        holder.smvPic.setImageUrl(picPath);
        holder.tvName.setText(entity.getGreenhouseName());
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, GreenhouseInfoActivity.class);
                intent.putExtra("userId", MyApplication.getInstance().getMyUserId());
                intent.putExtra("ghId", entity.getGreenhouseId());
                mActivity.startActivityForResult(intent, Constants.ACTIVITY_RESULT_GH_RELOAD);
            }
        });
        holder.tvAddr.setText(entity.getGreenhouseAddr());
        holder.tvRemark.setText(entity.getRemarkString());
        holder.tvUser.setText(entity.getUserName());
        holder.tvTime.setText(entity.getInDate());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, NodeManageActivity.class);
                intent.putExtra("ghId", entity.getGreenhouseId());
                intent.putExtra("ghName", entity.getGreenhouseName());
                mActivity.startActivity(intent);
            }
        });

        return view;
    }
}
