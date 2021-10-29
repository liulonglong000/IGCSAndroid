package com.xxs.igcsandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.adapter.MonitorParkUserAdapter;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.GreenhouseInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MonitorParkAdminFragmentEx extends BaseFragment {
    private String mUserId;
    private MonitorParkUserAdapter ghAdapter;
    private boolean bIsParkUser = true;
    private String mParkId;

    public MonitorParkAdminFragmentEx() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_park_admin_ex, container, false);
        Bundle data = getArguments();
        if (data != null && data.containsKey("parkId")) {
            mParkId = data.getString("parkId");
            bIsParkUser = false;
        }

        mUserId = MyApplication.getInstance().getMyUserId();

        ghAdapter = new MonitorParkUserAdapter(mActivity);
        ListView lv_monitor = view.findViewById(R.id.lv_ghs);
        lv_monitor.setAdapter(ghAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ghAdapter == null) {
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        String strUrl = "";
        if (bIsParkUser) {
            mp.put("userId", mUserId);
            strUrl = "gateway/queryGreenhouseByUserId";
        } else {
            mp.put("parkId", mParkId);
            strUrl = "gateway/queryGreenhouseByParkId";
        }

        AsyncSocketUtil.post(mActivity, strUrl, mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    ArrayList<GreenhouseInfo> lstEntity = new ArrayList<>();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        GreenhouseInfo entity = new GreenhouseInfo(obj);
                        lstEntity.add(entity);
                    }
                    ghAdapter.setDataInfo(lstEntity);
                    ghAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }
}