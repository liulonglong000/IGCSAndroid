package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.AcOperationGroupInfoActivity;
import com.xxs.igcsandroid.activity.AcStrategyInfoActivity;
import com.xxs.igcsandroid.activity.AcStrategyMgrActivity;
import com.xxs.igcsandroid.activity.AcThresholdGroupInfoActivity;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.StrategyInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.StringUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class StrategyAdapter extends MyBaseAdapter {
    private String mGhId;
    private String mGhName;
    private ArrayList<StrategyInfo> mList;

    public StrategyAdapter(Activity activity) {
        super(activity);
        mList = new ArrayList<>();
    }

    public void setInfo(String ghId, String ghName) {
        mGhId = ghId;
        mGhName = ghName;
    }

    public void setDataInfo(ArrayList<StrategyInfo> lstEntity) {
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
        TextView tvTimeStart;
        TextView tvTimeEnd;
        TextView tvTg1;
        TextView tvTg2;
        TextView tvTg3;
        TextView tvOg;
        TextView tvUser;
        LinearLayout llTg2;
        LinearLayout llTg3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_strategy, null);
            holder = new ViewHolder();
            holder.tvTimeStart = convertView.findViewById(R.id.tv_time_start);
            holder.tvTimeEnd = convertView.findViewById(R.id.tv_time_end);
            holder.tvTg1 = convertView.findViewById(R.id.tv_tg1);
            holder.tvTg1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            holder.tvTg2 = convertView.findViewById(R.id.tv_tg2);
            holder.tvTg2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            holder.tvTg3 = convertView.findViewById(R.id.tv_tg3);
            holder.tvTg3.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            holder.tvOg = convertView.findViewById(R.id.tv_og);
            holder.tvOg.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            holder.tvUser = convertView.findViewById(R.id.tv_info);
            holder.llTg2 = convertView.findViewById(R.id.ll_tg2);
            holder.llTg3 = convertView.findViewById(R.id.ll_tg3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final StrategyInfo entity = mList.get(position);

        holder.tvTimeStart.setText(entity.getStart());
        holder.tvTimeEnd.setText(entity.getEnd());
        holder.tvTg1.setText(entity.getTgName1());
        holder.tvTg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTgDetail(entity.getTgId1());
            }
        });
        if (StringUtil.isStringNullOrEmpty(entity.getTgName2())) {
            holder.llTg2.setVisibility(View.GONE);
        } else {
            holder.tvTg2.setText(entity.getTgName2());
            holder.llTg2.setVisibility(View.VISIBLE);
            holder.tvTg2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTgDetail(entity.getTgId2());
                }
            });
        }
        if (StringUtil.isStringNullOrEmpty(entity.getTgName3())) {
            holder.llTg3.setVisibility(View.GONE);
        } else {
            holder.tvTg3.setText(entity.getTgName3());
            holder.llTg3.setVisibility(View.VISIBLE);
            holder.tvTg3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTgDetail(entity.getTgId3());
                }
            });
        }
        holder.tvOg.setText(entity.getOgName());
        holder.tvOg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOgDetail(entity.getOgId());
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AcStrategyInfoActivity.class);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                intent.putExtra("strategyId", entity.getId());
                intent.putExtra("tgId1", entity.getTgId1());
                intent.putExtra("tgId2", entity.getTgId2());
                intent.putExtra("tgId3", entity.getTgId3());
                intent.putExtra("ogId", entity.getOgId());
                intent.putExtra("tgName1", entity.getTgName1());
                intent.putExtra("tgName2", entity.getTgName2());
                intent.putExtra("tgName3", entity.getTgName3());
                intent.putExtra("ogName", entity.getOgName());
                intent.putExtra("start", entity.getStart());
                intent.putExtra("end", entity.getEnd());
                mActivity.startActivity(intent);
            }
        });

        holder.tvUser.setText(entity.getUserName() + "        " + entity.getMdyTime());

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DlgUtil.showMsgInfo(mActivity, "确定要删除该策略吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoDelStrategy(entity.getId());
                    }
                });
                return true;
            }
        });

        return convertView;
    }

    private void showOgDetail(String ogId) {
        Intent intent = new Intent(mActivity, AcOperationGroupInfoActivity.class);
        intent.putExtra("ghId", mGhId);
        intent.putExtra("ghName", mGhName);
        intent.putExtra("ogId", ogId);
        mActivity.startActivity(intent);
    }

    private void showTgDetail(String tgId) {
        Intent intent = new Intent(mActivity, AcThresholdGroupInfoActivity.class);
        intent.putExtra("ghId", mGhId);
        intent.putExtra("ghName", mGhName);
        intent.putExtra("tgId", tgId);
        mActivity.startActivity(intent);
    }

    private void gotoDelStrategy(String id) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("strategyId", id);

        AsyncSocketUtil.post(mActivity, "autoctrl/delStrategyInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                DlgUtil.showMsgInfo(mActivity, "删除策略信息成功！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((AcStrategyMgrActivity)mActivity).getStrategyFromServer();
                    }
                });
            }
        }, null);
    }
}
