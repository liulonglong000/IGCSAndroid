package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.adapter.DiagnoseGroupAdapter;
import com.xxs.igcsandroid.adapter.ThresholdGroupAdapter;
import com.xxs.igcsandroid.entity.DignoseGroupInfo;
import com.xxs.igcsandroid.entity.ThresholdGroupInfo;
import com.xxs.igcsandroid.layout.LayoutButtonOne;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.MyComparator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class AcDiagnosisListActivity extends AppCompatActivity {
    private String mGhId;
    private String mGhName;

    private TextView dealing;
    private TextView dealed;

    private DiagnoseGroupAdapter adapter;
    private String status = "未采集或采集中";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_diagnosis_list);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mGhId = bundle.getString("ghId");
        mGhName = bundle.getString("ghName");

        setTitle(mGhName + "-病毒诊断");

        dealing = findViewById(R.id.willDealing);
        dealed = findViewById(R.id.dealed);

        dealed.setBackgroundResource(R.color.gray);

        adapter = new DiagnoseGroupAdapter(this);
        adapter.setInfo(mGhId, mGhName);
        ListView lv_diagnose = findViewById(R.id.listView_dl);
        lv_diagnose.setAdapter(adapter);

        dealing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealing();
            }
        });

        dealed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealed();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        getTgFromServer();
    }

    public void getTgFromServer() {
        if (adapter == null) {
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);
        mp.put("status",status);

        AsyncSocketUtil.post(this, "autoctrl/queryDiagnosisGroup", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    ArrayList<DignoseGroupInfo> lstEntity = new ArrayList<>();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        DignoseGroupInfo entity = new DignoseGroupInfo(obj);
                        lstEntity.add(entity);
                    }
                    Comparator<DignoseGroupInfo> myComparator = new MyComparator();
                    lstEntity.sort(myComparator);
                    adapter.setDataInfo(lstEntity);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(AcDiagnosisListActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                AcDiagnosisListActivity.this.finish();
            }
        });
    }

    void dealing(){
        dealing.setBackgroundResource(R.color.white);
        dealed.setBackgroundResource(R.color.gray);
        status = "未采集或采集中";
        getTgFromServer();
    }

    void dealed(){
        dealed.setBackgroundResource(R.color.white);
        dealing.setBackgroundResource(R.color.gray);
        status = "采集完成或已过期";
        getTgFromServer();
    }


}