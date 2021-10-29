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
import com.xxs.igcsandroid.adapter.GatewayAdapter;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.GatewayInfo;
import com.xxs.igcsandroid.entity.UserRole;
import com.xxs.igcsandroid.golbal.CommonRequest;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class GatewayManageActivity extends AppCompatActivity {
    private String mUserId;
    private String mUserRole;
    private GatewayAdapter gwAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_manage);

        setTitle("网关管理");

        mUserId = MyApplication.getInstance().getMyUserId();
        mUserRole = MyApplication.getInstance().getUserRole();

        gwAdapter = new GatewayAdapter(this);
        ListView lv_monitor = findViewById(R.id.listView_gateway);
        lv_monitor.setAdapter(gwAdapter);

        getGatewaysFromServer();
    }

    private void getGatewaysFromServer() {
        if (gwAdapter == null) {
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mUserId);

        AsyncSocketUtil.post(this, "gateway/queryGateway", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
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
                    DlgUtil.showExceptionPrompt(GatewayManageActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                GatewayManageActivity.this.finish();
            }
        });
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
                if (mUserRole.equals(UserRole.ROLE_PARK_ADMIN)) {
                    CommonRequest cr = new CommonRequest(this);
                    cr.checkParkExist(mUserId, new CommonRequest.onSuccessJSONObject() {
                        @Override
                        public void OnJSONObjectResult(JSONObject response) {
                            try {
                                Intent intent = new Intent(GatewayManageActivity.this, GatewayInfoActivity.class);
                                intent.putExtra("userId", mUserId);
                                intent.putExtra("userRole", mUserRole);
                                intent.putExtra("parkId", response.getString("id"));
                                startActivityForResult(intent, 301);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                DlgUtil.showExceptionPrompt(GatewayManageActivity.this, e);
                            }
                        }
                    });
                } else if (mUserRole.equals(UserRole.ROLE_PARK_USER)) {
                    Intent intent = new Intent(this, GatewayInfoActivity.class);
                    intent.putExtra("userId", mUserId);
                    intent.putExtra("userRole", mUserRole);
                    startActivityForResult(intent, 301);
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 301) {
                getGatewaysFromServer();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
