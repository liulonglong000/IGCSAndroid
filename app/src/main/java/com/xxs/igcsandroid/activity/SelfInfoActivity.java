package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.golbal.Constants;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class SelfInfoActivity extends AppCompatActivity {
    private SmartImageView sivPic;
    private String picPath;
    private TextView tvName;
    private TextView tvPhone;

    private String mUserId;
    private String mUserPwd;
    private String mUserRole;

    private boolean bUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_info);

        mUserId = MyApplication.getInstance().getMyUserId();
        mUserPwd = MyApplication.getInstance().getUserPwd();
        mUserRole = MyApplication.getInstance().getUserRole();
        this.setTitle(mUserRole + "->" + mUserId);

        sivPic = findViewById(R.id.siv_accountPic);
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);

        findViewById(R.id.layout_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bUpdate){
                    return;
                }

                Intent intent = new Intent(SelfInfoActivity.this, UserPicMdyActivity.class);
                intent.putExtra("userId", mUserId);
                intent.putExtra("picPath", picPath);
                startActivityForResult(intent, 102);
            }
        });

        findViewById(R.id.layout_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bUpdate){
                    return;
                }

                Intent intent = new Intent(SelfInfoActivity.this, UserNameMdyActivity.class);
                intent.putExtra("userId", mUserId);
                intent.putExtra("userName", tvName.getText().toString());
                startActivityForResult(intent, 101);
            }
        });

        findViewById(R.id.layout_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bUpdate){
                    return;
                }

                Intent intent = new Intent(SelfInfoActivity.this, UserPwdMdyActivity.class);
                intent.putExtra("userId", mUserId);
                intent.putExtra("userPwd", MyApplication.getInstance().getUserPwd());
                startActivity(intent);
            }
        });

        findViewById(R.id.layout_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bUpdate){
                    return;
                }

                Intent intent = new Intent(SelfInfoActivity.this, UserPhoneMdyActivity.class);
                intent.putExtra("userId", mUserId);
                intent.putExtra("userTel", tvPhone.getText().toString());
                startActivityForResult(intent, 103);
            }
        });
        
        getSelInfoFromServer();
    }

    private void getSelInfoFromServer() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mUserId);

        AsyncSocketUtil.post(this, "user/getLoginInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject dataObj = response.getJSONObject(1);
                    tvName.setText(dataObj.getString("name"));
                    tvPhone.setText(dataObj.getString("phone"));

                    picPath = dataObj.getString("picPath");
                    if (picPath.length() > 0) {
                        sivPic.setImageUrl(Constants.SERVER_URL + "downloadFile/downloadFile?inputPath=" + picPath);
                    }

                    bUpdate = true;
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(SelfInfoActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                SelfInfoActivity.this.finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                String name = data.getStringExtra("userName");
                tvName.setText(name);
            } else if (requestCode == 102) {
                picPath = data.getStringExtra("picPath");
                if (picPath.length() > 0) {
                    sivPic.setImageUrl(Constants.SERVER_URL + "downloadFile/downloadFile?inputPath=" + picPath);
                } else {
                    sivPic.setImageDrawable(null);
                }
            } else if (requestCode == 103) {
                String name = data.getStringExtra("userTel");
                tvPhone.setText(name);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
