package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.adapter.NodeAdapter;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.GreenhouseInfo;
import com.xxs.igcsandroid.entity.NodeInfo;
import com.xxs.igcsandroid.golbal.Constants;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class NodeManageActivity extends AppCompatActivity {
    private String mUserId;
    private String mGhId;
    private NodeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_manage);

        mUserId = MyApplication.getInstance().getMyUserId();
        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mGhId = bundle.getString("ghId");
        setTitle(bundle.getString("ghName") + "->节点管理");

        adapter = new NodeAdapter(this);
        ListView lv_monitor = findViewById(R.id.lv_node);
        lv_monitor.setAdapter(adapter);

        getNodesFromServer();
    }

    private void getNodesFromServer() {
        if (adapter == null) {
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(this, "gateway/queryNode", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    ArrayList<NodeInfo> lstEntity = new ArrayList<>();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        NodeInfo entity = new NodeInfo(obj, true);
                        lstEntity.add(entity);
                    }
                    adapter.setDataInfo(lstEntity);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(NodeManageActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                NodeManageActivity.this.finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.ACTIVITY_RESULT_NODE_RELOAD) {
                getNodesFromServer();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
