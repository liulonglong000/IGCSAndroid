package com.xxs.igcsandroid.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.layout.LayoutButtonOne;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingSysAdminFragment extends SettingFragment {
    public SettingSysAdminFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_sys_admin, container, false);

        Button btn = view.findViewById(R.id.btn_selfInfo);
        LayoutButtonOne.setTextAndImg(btn, "个人信息", mActivity, R.drawable.ic_self_info);
        btn.setOnClickListener(new View.OnClickListener() {
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
