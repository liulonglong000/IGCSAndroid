package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.adapter.GreenhouseAdapter;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.GreenhouseInfo;
import com.xxs.igcsandroid.entity.UserRole;
import com.xxs.igcsandroid.golbal.Constants;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class GreenhouseManageActivity extends AppCompatActivity {
    private String mUserId;
    private String mUserRole;
    private GreenhouseAdapter ghAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greenhouse_manage);

        setTitle("温室管理");

        mUserId = MyApplication.getInstance().getMyUserId();
        mUserRole = MyApplication.getInstance().getUserRole();

        ghAdapter = new GreenhouseAdapter(this);
        ListView lv_monitor = findViewById(R.id.listView_gh);
        lv_monitor.setAdapter(ghAdapter);

        getGhsFromServer();
    }

    private void getGhsFromServer() {
        if (ghAdapter == null) {
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mUserId);

        AsyncSocketUtil.post(this, "gateway/queryGreenhouseByUserId", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
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
                    DlgUtil.showExceptionPrompt(GreenhouseManageActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                GreenhouseManageActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mUserRole == UserRole.ROLE_SYS_ADMIN) {
            return false;
        } else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_add, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_add:
                Intent intent = new Intent(this, GreenhouseInfoActivity.class);
                intent.putExtra("userId", mUserId);
                startActivityForResult(intent, Constants.ACTIVITY_RESULT_GH_RELOAD);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.ACTIVITY_RESULT_GH_RELOAD) {
                getGhsFromServer();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
