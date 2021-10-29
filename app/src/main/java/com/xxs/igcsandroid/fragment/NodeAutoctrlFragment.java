package com.xxs.igcsandroid.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.AcAppStrategyMgrActivity;
import com.xxs.igcsandroid.activity.AcArmStrategyMgrActivity;
import com.xxs.igcsandroid.activity.AcEncodeMgrActivity;
import com.xxs.igcsandroid.activity.AcTimeLimitMgrActivity;
import com.xxs.igcsandroid.layout.LayoutButtonOne;

/**
 * A simple {@link Fragment} subclass.
 */
public class NodeAutoctrlFragment extends BaseFragment {
    private String mGhId;
    private String mGwId;
    private String mGhName;

    public NodeAutoctrlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle data = getArguments();
        mGhId = data.getString("ghId");
        mGwId = data.getString("gwId");
        mGhName = data.getString("ghName");

        View view = inflater.inflate(R.layout.fragment_node_autoctrl, container, false);

        Button btn = view.findViewById(R.id.btn_app_strategy_manage);
        LayoutButtonOne.setTextAndImg(btn, "上位机策略设置", mActivity, R.drawable.ic_app_strategy_manage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AcAppStrategyMgrActivity.class);
                intent.putExtra("gwId", mGwId);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                startActivity(intent);
            }
        });

        btn = view.findViewById(R.id.btn_arm_strategy_manage);
        LayoutButtonOne.setTextAndImg(btn, "下位机策略设置", mActivity, R.drawable.ic_arm_strategy_manage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AcArmStrategyMgrActivity.class);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                startActivity(intent);
            }
        });

        btn = view.findViewById(R.id.btn_time_limit_manage);
        LayoutButtonOne.setTextAndImg(btn, "时间限位设置", mActivity, R.drawable.ic_time_limit_manage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AcTimeLimitMgrActivity.class);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                startActivity(intent);
            }
        });

        btn = view.findViewById(R.id.btn_encode_manage);
        LayoutButtonOne.setTextAndImg(btn, "编码器设置", mActivity, R.drawable.ic_encode_manage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AcEncodeMgrActivity.class);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                startActivity(intent);
            }
        });

        return view;
    }

}
