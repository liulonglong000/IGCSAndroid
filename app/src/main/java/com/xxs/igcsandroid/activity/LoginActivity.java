package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.UserRole;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.LoginUtil;
import com.xxs.igcsandroid.util.StringUtil;

public class LoginActivity extends AppCompatActivity {
    private EditText mAccount;                        // 用户名编辑
    private EditText mPwd;                            // 密码编辑
//    private RadioGroup radioGroup_role;
    private CheckBox mRememberCheck;

    private String mUserId;
    private String mUserPwd;
//    private String mUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAccount = findViewById(R.id.login_edit_account);
        mPwd = findViewById(R.id.login_edit_pwd);
//        radioGroup_role = findViewById(R.id.radioGroup_role);
        mRememberCheck = findViewById(R.id.Login_Remember);

        findViewById(R.id.login_text_change_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DlgUtil.showMsgInfo(LoginActivity.this, "请联系上级管理员重置密码！");
            }
        });

        findViewById(R.id.login_btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLoginInfo()) {
                    gotoLogin();
                }
            }
        });
    }

    private boolean checkLoginInfo() {
        mUserId = mAccount.getText().toString().trim();
        if (StringUtil.isStringNullOrEmpty(mUserId)) {
            DlgUtil.showMsgInfo(this, mAccount.getHint().toString());
            return false;
        }
        mUserPwd = mPwd.getText().toString().trim();
        if (StringUtil.isStringNullOrEmpty(mUserPwd)) {
            DlgUtil.showMsgInfo(this, mPwd.getHint().toString());
            return false;
        }

//        int checkedId = radioGroup_role.getCheckedRadioButtonId();
//        RadioButton radioButton_checked = radioGroup_role.findViewById(checkedId);
//        mUserRole = radioButton_checked.getText().toString();

        return true;
    }

    private void gotoLogin() {
        LoginUtil.doLogin(this, mUserId, mUserPwd, new LoginUtil.OnLoginResult() {
            @Override
            public void OnGetLoginResult(boolean bSuccess) {
                if (bSuccess) {
                    String userRole = MyApplication.getInstance().getUserRole();
                    if (mRememberCheck.isChecked()) {
                        LoginUtil.saveLoginInfo(LoginActivity.this, mUserId, mUserPwd);
                    } else {
                        LoginUtil.clearLoginInfo(LoginActivity.this);
                    }

                    MyApplication.getInstance().setLoginInfo(mUserId, mUserPwd);

                    if (userRole.equals(UserRole.ROLE_SYS_ADMIN)) {
                        Intent intent = new Intent(LoginActivity.this, MainSysAdminActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    } else if (userRole.equals(UserRole.ROLE_PARK_ADMIN)) {
                        Intent intent = new Intent(LoginActivity.this, MainParkAdminActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    } else if (userRole.equals(UserRole.ROLE_PARK_USER)) {
                        Intent intent = new Intent(LoginActivity.this, MainParkUserActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                }
            }
        });
    }
}
