package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserNameMdyActivity extends AppCompatActivity {
    private EditText etName;

    private String mUserId;
    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name_mdy);

        setTitle("更改姓名");

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mUserId = bundle.getString("userId");
        mUserName = bundle.getString("userName");

        etName = findViewById(R.id.edit_name);
        etName.setText(mUserName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_save:
                String newName = etName.getText().toString().trim();
                if (StringUtil.isStringNullOrEmpty(newName)) {
                    DlgUtil.showMsgInfo(this, "姓名不能为空！");
                    break;
                }
                if (mUserName.equals(newName)) {
                    DlgUtil.showMsgInfo(this, "姓名没有改动，无需保存！");
                    break;
                }
                
                updateUserName(newName);
                
                break;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void updateUserName(final String newName) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mUserId);
        mp.put("userName", newName);

        AsyncSocketUtil.post(this, "user/mdyUserName", mp, "正在更改......", new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    DlgUtil.showMsgInfo(UserNameMdyActivity.this, "姓名更改成功！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.putExtra("userName", newName);
                            setResult(RESULT_OK, intent);
                            UserNameMdyActivity.this.finish();
                        }
                    });
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(UserNameMdyActivity.this, e);
                }
            }
        }, null);
    }
}
