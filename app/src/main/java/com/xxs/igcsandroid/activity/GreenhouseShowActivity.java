package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class GreenhouseShowActivity extends AppCompatActivity {
    private String mUserId;
    private String mGwId;
    private MonitorParkUserAdapter adapter_monitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greenhouse_show);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mGwId = bundle.getString("gatewayId");

        setTitle(bundle.getString("gatewayName"));

        mUserId = MyApplication.getInstance().getMyUserId();

        adapter_monitor = new MonitorParkUserAdapter(this);
        ListView lv_monitor = findViewById(R.id.lv_ghs);
        lv_monitor.setAdapter(adapter_monitor);
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
        mp.put("gwId", mGwId);

        AsyncSocketUtil.post(this, "gateway/queryGreenhouseOfGw", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
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
                    DlgUtil.showExceptionPrompt(GreenhouseShowActivity.this, e);
                }
            }
        }, null);
    }
}
