package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.adapter.ThresholdGroupAdapter;
import com.xxs.igcsandroid.entity.ThresholdGroupInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AcThresholdGroupMgrActivity extends AppCompatActivity {
    private String mGhId;
    private String mGhName;

    private ThresholdGroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_threshold_group_mgr);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mGhId = bundle.getString("ghId");
        mGhName = bundle.getString("ghName");

        setTitle(mGhName + "->阈值组管理");

        adapter = new ThresholdGroupAdapter(this);
        adapter.setInfo(mGhId, mGhName);
        ListView lv_monitor = findViewById(R.id.listView_tg);
        lv_monitor.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTgFromServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_add:
                Intent intent = new Intent(this, AcThresholdGroupInfoActivity.class);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getTgFromServer() {
        if (adapter == null) {
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(this, "autoctrl/queryThresholdGroup", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    ArrayList<ThresholdGroupInfo> lstEntity = new ArrayList<>();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        ThresholdGroupInfo entity = new ThresholdGroupInfo(obj);
                        lstEntity.add(entity);
                    }
                    adapter.setDataInfo(lstEntity);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(AcThresholdGroupMgrActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                AcThresholdGroupMgrActivity.this.finish();
            }
        });
    }
}
