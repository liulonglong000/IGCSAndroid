package com.xxs.igcsandroid.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.AlarmQueryActivity;
import com.xxs.igcsandroid.activity.LoginActivity;
import com.xxs.igcsandroid.activity.ParkAdminManageActivity;
import com.xxs.igcsandroid.activity.SelfInfoActivity;
import com.xxs.igcsandroid.util.DataCleanManager;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.LoginUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment {
    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }

    protected void doClickedSelfInfo() {
        Intent intent = new Intent(mActivity, SelfInfoActivity.class);
        startActivity(intent);
    }

    protected void doClickedUserMgr() {
        Intent intent = new Intent(mActivity, ParkAdminManageActivity.class);
        startActivity(intent);
    }

    protected void doQueryAlarm() {
        Intent intent = new Intent(mActivity, AlarmQueryActivity.class);
        startActivity(intent);
    }

    protected void doCacheClear() {
        DlgUtil.showAsk(mActivity, "确定要清除缓存吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (DataCleanManager.clearAllCache(getActivity())) {
                    DlgUtil.showMsgInfo(mActivity, "缓存已清除！");
                } else {
                    DlgUtil.showMsgError(mActivity, "缓存清除失败！");
                }
            }
        });
    }

    protected void doLogOut() {
        DlgUtil.showAsk(mActivity, "确定要退出登录吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoginUtil.clearLoginInfo(mActivity);

                Intent intent = new Intent(mActivity, LoginActivity.class);
                startActivity(intent);
                mActivity.finish();
            }
        });
    }
}
