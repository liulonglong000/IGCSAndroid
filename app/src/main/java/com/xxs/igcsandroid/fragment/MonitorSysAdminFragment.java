package com.xxs.igcsandroid.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.adapter.parkAdapter;
import com.xxs.igcsandroid.entity.ParkInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonitorSysAdminFragment extends BaseFragment {
    private parkAdapter adapter;

    public MonitorSysAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_sys_admin, container, false);

        adapter = new parkAdapter(mActivity);
        ListView lv_monitor = view.findViewById(R.id.lv_parks);
        lv_monitor.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getParksFromServer();
    }

    private void getParksFromServer() {
        if (adapter == null) {
            return;
        }

        AsyncSocketUtil.post(mActivity, "gateway/queryPark", null, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    ArrayList<ParkInfo> lstEntity = new ArrayList<>();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        ParkInfo entity = new ParkInfo(obj);
                        lstEntity.add(entity);
                    }
                    adapter.setDataInfo(lstEntity);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }
}
