package com.xxs.igcsandroid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MonitorParkUserFragment extends BaseFragment {
    private String mUserId;
    private MonitorParkUserAdapter adapter_monitor;

    public MonitorParkUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_park_user, container, false);

        mUserId = MyApplication.getInstance().getMyUserId();

        adapter_monitor = new MonitorParkUserAdapter(mActivity);
        ListView lv_monitor = view.findViewById(R.id.lv_ghs);
        lv_monitor.setAdapter(adapter_monitor);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        if (adapter_monitor == null) {
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mUserId);

        AsyncSocketUtil.post(mActivity, "gateway/queryGreenhouse", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    ArrayList<GreenhouseInfo> lstEntity = new ArrayList<>();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        GreenhouseInfo entity = new GreenhouseInfo(obj);
                        lstEntity.add(entity);
                    }
                    adapter_monitor.setDataInfo(lstEntity);
                    adapter_monitor.notifyDataSetChanged();
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }
}
