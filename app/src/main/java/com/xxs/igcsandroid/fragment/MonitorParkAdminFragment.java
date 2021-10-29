package com.xxs.igcsandroid.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.adapter.GatewayAdapter;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.GatewayInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

// 没用了
public class MonitorParkAdminFragment extends BaseFragment {
    private String mUserId;
    private GatewayAdapter gwAdapter;
    private boolean bParkAdmin = true;
    private String mParkId;

    public MonitorParkAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_park_admin, container, false);

        Bundle data = getArguments();
        if (data != null && data.containsKey("parkId")) {
            mParkId = data.getString("parkId");
            bParkAdmin = false;
        }

        mUserId = MyApplication.getInstance().getMyUserId();

        gwAdapter = new GatewayAdapter(mActivity);
        gwAdapter.setbClickShowGh(true);
        ListView lv_monitor = view.findViewById(R.id.lv_gws);
        lv_monitor.setAdapter(gwAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bParkAdmin) {
            getGatewaysFromServer();
        } else {
            getData();
        }
    }

    private void getGatewaysFromServer() {
        if (gwAdapter == null) {
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mUserId);

        AsyncSocketUtil.post(mActivity, "gateway/queryGateway", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    ArrayList<GatewayInfo> lstEntity = new ArrayList<>();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        GatewayInfo entity = new GatewayInfo(obj);
                        lstEntity.add(entity);
                    }
                    gwAdapter.setDataInfo(lstEntity);
                    gwAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }

    private void getData() {
        if (gwAdapter == null) {
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("parkId", mParkId);

        AsyncSocketUtil.post(mActivity, "gateway/queryGatewayOfPark", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    ArrayList<GatewayInfo> lstEntity = new ArrayList<>();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        GatewayInfo entity = new GatewayInfo(obj);
                        lstEntity.add(entity);
                    }
                    gwAdapter.setDataInfo(lstEntity);
                    gwAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }
}
