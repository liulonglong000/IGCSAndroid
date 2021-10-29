package com.xxs.igcsandroid.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.GreenhouseManageActivity;
import com.xxs.igcsandroid.activity.ParkViewActivity;
import com.xxs.igcsandroid.layout.LayoutButtonOne;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingParkUserFragment extends SettingFragment {

    public SettingParkUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_park_user, container, false);

        Button btn = view.findViewById(R.id.btn_selfInfo);
        LayoutButtonOne.setTextAndImg(btn, "个人信息", mActivity, R.drawable.ic_self_info);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doClickedSelfInfo();
            }
        });

        btn = view.findViewById(R.id.btn_park_info);
        LayoutButtonOne.setTextAndImg(btn, "园区信息", mActivity, R.drawable.ic_park_manage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ParkViewActivity.class);
                startActivity(intent);
            }
        });

//        btn = view.findViewById(R.id.btn_gh_manage);
//        LayoutButtonOne.setTextAndImg(btn, "温室管理", mActivity, R.drawable.ic_gh_manage);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mActivity, GreenhouseManageActivity.class);
//                startActivity(intent);
//            }
//        });

        btn = view.findViewById(R.id.btn_alarm_info);
        LayoutButtonOne.setTextAndImg(btn, "告警信息", mActivity, R.drawable.ic_alarm_all);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doQueryAlarm();
            }
        });

        btn = view.findViewById(R.id.btn_clear_cache);
        LayoutButtonOne.setTextAndImg(btn, "清除缓存", mActivity, R.drawable.ic_clear_cache);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCacheClear();
            }
        });

        btn = view.findViewById(R.id.btn_quit);
        LayoutButtonOne.setTextAndImg(btn, "退出登录", mActivity, R.drawable.ic_logout);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogOut();
            }
        });

        return view;
    }

}
