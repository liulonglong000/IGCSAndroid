package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.entity.SpinnerCommonData;

import java.util.ArrayList;

public class SpinnerCommonAdapter extends MyBaseAdapter {
    private ArrayList<SpinnerCommonData> mLstData;

    public SpinnerCommonAdapter(Activity activity) {
        super(activity);
        mLstData = new ArrayList<>();
    }

    public void setData(ArrayList<SpinnerCommonData> data) {
        if (mLstData != null) {
            mLstData.clear();
        }
        mLstData = data;
    }

    @Override
    public int getCount() {
        if (mLstData == null) {
            return 0;
        } else {
            return mLstData.size();
        }
    }

    static class ViewHolder {
        String id;
        TextView txtViewTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.spinner_item_common, null);
            holder = new ViewHolder();
            holder.txtViewTitle = convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final SpinnerCommonData entity = mLstData.get(position);
        holder.id = entity.getId();
        holder.txtViewTitle.setText(entity.getName());

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return mLstData.get(position);
    }
}
