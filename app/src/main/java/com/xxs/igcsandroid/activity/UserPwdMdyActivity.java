package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.LoginUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserPwdMdyActivity extends AppCompatActivity {
    private EditText etPwdOld;
    private EditText etPwd;
    private EditText etPwdAgain;

    private String mUserId;
    private String mUserPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pwd_mdy);

        setTitle("更改密码");

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mUserId = bundle.getString("userId");
        mUserPwd = bundle.getString("userPwd");

        etPwdOld = findViewById(R.id.et_pwd_old);
        etPwd = findViewById(R.id.et_pwd);
        etPwdAgain = findViewById(R.id.et_pwd_again);

        findViewById(R.id.btn_modify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMdyPwd();
            }
        });
    }

    private void gotoMdyPwd() {
        if (!CheckInputUtil.checkTextViewInput(etPwdOld, this, "请输入旧密码！")) {
            return;
        }
        final String pwdNew = etPwd.getText().toString().trim();
        if (!CheckInputUtil.checkTextViewInput(etPwd, this, "请输入新密码！")) {
            return;
        }
        if (!CheckInputUtil.checkTextViewInput(etPwdAgain, this, "请输入确认密码！")) {
            return;
        }
        if (!pwdNew.equals(etPwdAgain.getText().toString().trim())) {
            DlgUtil.showMsgInfo(this, "新密码和确认密码不一致，请重新输入！");
            etPwd.requestFocus();
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mUserId);
        mp.put("userPwdOld", etPwdOld.getText().toString().trim());
        mp.put("userPwdNew", pwdNew);

        AsyncSocketUtil.post(this, "user/mdyUserPwd", mp, "正在更改......", new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    MyApplication.getInstance().setUserPwd(pwdNew);
                    LoginUtil.saveLoginInfo(UserPwdMdyActivity.this, pwdNew);

                    DlgUtil.showMsgInfo(UserPwdMdyActivity.this, "密码更改成功！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UserPwdMdyActivity.this.finish();
                        }
                    });
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(UserPwdMdyActivity.this, e);
                }
            }
        }, null);
    }
}
