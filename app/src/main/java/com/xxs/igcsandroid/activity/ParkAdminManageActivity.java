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
import com.xxs.igcsandroid.adapter.UserInfoAdapter;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.UserInfo;
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

public class ParkAdminManageActivity extends AppCompatActivity {
    private String mUserId;
    private String mUserRole;

    private UserInfoAdapter adapter_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_admin_manage);

        adapter_user = new UserInfoAdapter(this);
        ListView lv_monitor = findViewById(R.id.listView_users);
        lv_monitor.setAdapter(adapter_user);

        mUserId = MyApplication.getInstance().getMyUserId();
        mUserRole = MyApplication.getInstance().getUserRole();
        if (mUserRole.equals(UserRole.ROLE_SYS_ADMIN)) {
            setTitle("管理园区管理员");
            adapter_user.setParkShow(true);
        } else if (mUserRole.equals(UserRole.ROLE_PARK_ADMIN)) {
            setTitle("管理园区工作人员");
            adapter_user.setParkShow(false);
        }

        getChildUsersFromServer();
    }

    private void getChildUsersFromServer() {
        if (adapter_user == null) {
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("parentUserId", mUserId);

        AsyncSocketUtil.post(this, "user/queryUserInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    ArrayList<UserInfo> lstEntity = new ArrayList<>();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        UserInfo entity = new UserInfo(obj);
                        lstEntity.add(entity);
                    }
                    adapter_user.setUserInfo(lstEntity);
                    adapter_user.notifyDataSetChanged();
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(ParkAdminManageActivity.this, e);
                }
            }
        }, null);
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
                                Intent intent = new Intent(ParkAdminManageActivity.this, UserAddActivity.class);
                                intent.putExtra("userId", mUserId);
                                intent.putExtra("userRole", mUserRole);
                                intent.putExtra("parkId", response.getString("id"));
                                startActivityForResult(intent, 201);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                DlgUtil.showExceptionPrompt(ParkAdminManageActivity.this, e);
                            }
                        }
                    });
                } else if (mUserRole.equals(UserRole.ROLE_SYS_ADMIN)) {
                    Intent intent = new Intent(this, UserAddActivity.class);
                    intent.putExtra("userId", mUserId);
                    intent.putExtra("userRole", mUserRole);
                    startActivityForResult(intent, 201);
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 201) {
//                String userId = data.getStringExtra("userId");
//                String name = data.getStringExtra("userName");
                getChildUsersFromServer();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
