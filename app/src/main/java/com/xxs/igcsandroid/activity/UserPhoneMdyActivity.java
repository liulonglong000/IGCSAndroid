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

public class UserPhoneMdyActivity extends AppCompatActivity {
    private EditText etTel;

    private String mUserId;
    private String mUserTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_phone_mdy);

        setTitle("更改联系电话");

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mUserId = bundle.getString("userId");
        mUserTel = bundle.getString("userTel");

        etTel = findViewById(R.id.edit_phone);
        etTel.setText(mUserTel);
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
                String newName = etTel.getText().toString().trim();
                if (StringUtil.isStringNullOrEmpty(newName)) {
                    DlgUtil.showMsgInfo(this, "联系电话不能为空！");
                    break;
                }
                if (mUserTel.equals(newName)) {
                    DlgUtil.showMsgInfo(this, "联系电话没有改动，无需保存！");
                    break;
                }

                updateUserTel(newName);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUserTel(final String newTel) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mUserId);
        mp.put("userTel", newTel);

        AsyncSocketUtil.post(this, "user/mdyUserTel", mp, "正在更改......", new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    DlgUtil.showMsgInfo(UserPhoneMdyActivity.this, "联系电话更改成功！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.putExtra("userTel", newTel);
                            setResult(RESULT_OK, intent);
                            UserPhoneMdyActivity.this.finish();
                        }
                    });
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(UserPhoneMdyActivity.this, e);
                }
            }
        }, null);
    }
}
