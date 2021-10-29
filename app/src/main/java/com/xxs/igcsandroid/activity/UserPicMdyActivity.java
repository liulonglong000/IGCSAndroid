package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.loopj.android.image.SmartImageView;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.golbal.Constants;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.HandlePic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserPicMdyActivity extends AppCompatActivity {
    private Button btnCommit;

    private HandlePic hdlPic;

    private String mUserId;
    private String mPicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pic_mdy);

        setTitle("更改头像");

        hdlPic = new HandlePic(this);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mUserId = bundle.getString("userId");
        mPicPath = bundle.getString("picPath");

        hdlPic.setImageView((SmartImageView) findViewById(R.id.iv_image));
        hdlPic.handleSelPic((Button) findViewById(R.id.btn_sel_pic));
        hdlPic.handleShotPic((Button) findViewById(R.id.btn_shot_pic));
        hdlPic.handleDelPic((Button) findViewById(R.id.btn_del_pic));
        hdlPic.setInfoByPic(mPicPath);

        btnCommit = findViewById(R.id.btn_add);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMdyPic();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            hdlPic.onActivityResult(requestCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void gotoMdyPic() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mUserId);
        mp.put("picSrcFile", hdlPic.getPicSrcFile());
        hdlPic.getFileToUpdate(mUserId + System.currentTimeMillis());

        AsyncSocketUtil.postWithFile(this, "user/mdyUserPic", mp, "picSel", hdlPic.getFileSel(), null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject dataObj = response.getJSONObject(1);
                    mPicPath = dataObj.getString("pic");

                    DlgUtil.showMsgInfo(UserPicMdyActivity.this, "修改头像成功！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.putExtra("picPath", mPicPath);
                            setResult(RESULT_OK, intent);
                            UserPicMdyActivity.this.finish();
                        }
                    });
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(UserPicMdyActivity.this, e);
                }
            }
        }, null);
    }
}
