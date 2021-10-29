package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.entity.UserRole;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserAddActivity extends AppCompatActivity {

    private EditText etAccount;
    private EditText etName;
    private EditText etPwd;
    private EditText etPwdAgain;

    private String mParentUserId;
    private String mParentUserRole;
    private String mParkId;

    private String mAccount;
    private String mName;
    private String mPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mParentUserId = bundle.getString("userId");
        mParentUserRole = bundle.getString("userRole");
        if (mParentUserRole.equals(UserRole.ROLE_SYS_ADMIN)) {
            setTitle("添加园区管理员");
        } else if (mParentUserRole.equals(UserRole.ROLE_PARK_ADMIN)) {
            setTitle("添加园区工作人员");
            mParkId = bundle.getString("parkId");
        }

        etAccount = findViewById(R.id.et_account);
        etName = findViewById(R.id.et_name);
        etPwd = findViewById(R.id.et_pwd);
        etPwdAgain = findViewById(R.id.et_pwd_again);

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInputInfo()) {
                    return;
                }
                gotoAddParkAdmin();
            }
        });
    }

    private void gotoAddParkAdmin() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("parentUserId", mParentUserId);
        mp.put("userId", mAccount);
        mp.put("userName", mName);
        mp.put("userPwd", mPwd);
        mp.put("userRole", UserRole.getChild(mParentUserRole));
        mp.put("parkId", mParkId);

        AsyncSocketUtil.post(this, "user/addUser", mp, "正在添加用户......", new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    DlgUtil.showMsgInfo(UserAddActivity.this, "添加用户成功！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Intent intent = new Intent();
                            intent.putExtra("userId", mAccount);
                            intent.putExtra("userName", mName);
                            setResult(RESULT_OK, intent);
                            UserAddActivity.this.finish();
                        }
                    });
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(UserAddActivity.this, e);
                }
            }
        }, null);
    }

    private boolean checkInputInfo() {
        if (!CheckInputUtil.checkTextViewInput(etAccount, this, "请输入用户名！")) {
            return false;
        }
        mAccount = etAccount.getText().toString().trim();

        if (!CheckInputUtil.checkTextViewInput(etName, this, "请输入姓名！")) {
            return false;
        }
        mName = etName.getText().toString().trim();

        if (!CheckInputUtil.checkTextViewInput(etPwd, this, "请输入密码！")) {
            return false;
        }
        mPwd = etPwd.getText().toString().trim();

        if (!CheckInputUtil.checkTextViewInput(etPwdAgain, this, "请输入确认密码！")) {
            return false;
        }
        String pwdAgain = etPwdAgain.getText().toString().trim();
        if (!mPwd.equals(pwdAgain)) {
            DlgUtil.showMsgInfo(this, "密码和确认密码不相同，请重新输入！");
            return false;
        }

        return true;
    }
}
