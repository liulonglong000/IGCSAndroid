package com.xxs.igcsandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.GatewayManageActivity;
import com.xxs.igcsandroid.activity.GreenhouseManageActivity;
import com.xxs.igcsandroid.activity.ParkInfoActivity;
import com.xxs.igcsandroid.layout.LayoutButtonOne;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingParkAdminFragment extends SettingFragment {

    public SettingParkAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_park_admin, container, false);

        view.findViewById(R.id.btn_selfInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doClickedSelfInfo();
            }
        });

        view.findViewById(R.id.btn_user_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doClickedUserMgr();
            }
        });

        view.findViewById(R.id.btn_park_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ParkInfoActivity.class);
                startActivity(intent);
            }
        });

        Button btn = view.findViewById(R.id.btn_gateway_manage);
        LayoutButtonOne.setTextAndImg(btn, "网关管理", mActivity, R.drawable.ic_gateway_manage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, GatewayManageActivity.class);
                startActivity(intent);
            }
        });

        btn = view.findViewById(R.id.btn_gh_manage);
        LayoutButtonOne.setTextAndImg(btn, "温室管理", mActivity, R.drawable.ic_gh_manage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, GreenhouseManageActivity.class);
                startActivity(intent);
            }
        });

        btn = view.findViewById(R.id.btn_alarm_info);
        LayoutButtonOne.setTextAndImg(btn, "告警信息", mActivity, R.drawable.ic_alarm_all);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doQueryAlarm();
            }
        });

        view.findViewById(R.id.btn_clear_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCacheClear();
            }
        });

        view.findViewById(R.id.btn_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogOut();
            }
        });

        return view;
    }

}
