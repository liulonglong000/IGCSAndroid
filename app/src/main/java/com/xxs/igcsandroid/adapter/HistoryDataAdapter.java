package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.entity.HistoryDataEntity;

import java.util.ArrayList;
import java.util.List;

public class HistoryDataAdapter extends MyBaseAdapter {
    private ArrayList<HistoryDataEntity> mLstItem;
    private int maxDataColum;

    public HistoryDataAdapter(Activity activity) {
        super(activity);
        mLstItem = new ArrayList<>();
        maxDataColum = 0;
    }

    public synchronized void setItemList(ArrayList<HistoryDataEntity> users, int max) {
        if (mLstItem != null) {
            mLstItem.clear();
        }
        mLstItem = users;
        maxDataColum = max;
    }

    @Override
    public int getCount() {
        if (mLstItem == null) {
            return 0;
        } else {
            return mLstItem.size();
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_history_data, null);
            holder = new ViewHolder();
            holder.llBodyParent = view.findViewById(R.id.ll_body_parent);
            holder.tvId = view.findViewById(R.id.body_id);
            ViewGroup.LayoutParams lp = holder.tvId.getLayoutParams();
            lp.width = 130;
            holder.tvId.setLayoutParams(lp);
            holder.tvTime = view.findViewById(R.id.body_time);
            lp = holder.tvTime.getLayoutParams();
            lp.width = 300;
            holder.tvTime.setLayoutParams(lp);
            for (int l = 0; l < maxDataColum; l++) {
                TextView tvBody = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.layout_history_table_body, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT);
                holder.llBodyParent.addView(tvBody, params);
                holder.lstTvBodys.add(tvBody);
            }

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        HistoryDataEntity entity = mLstItem.get(i);
        holder.tvId.setText(String.valueOf(i + 1));
        holder.tvTime.setText(entity.getTime());
        int n;
        TextView tvBody;
        for (n = 1; n <= maxDataColum; n++) {
            tvBody = holder.lstTvBodys.get(n - 1);
            tvBody.setText(entity.getSensorDataById(n));
        }

        if (i % 2 == 0) {
            view.setBackgroundColor(Color.WHITE);
        } else {
            view.setBackgroundColor(Color.parseColor("#ffdddddd"));
        }

        return view;
    }

    static class ViewHolder {
        LinearLayout llBodyParent;
        TextView tvId;
        TextView tvTime;
        List<TextView> lstTvBodys = new ArrayList<>();
    }
}
