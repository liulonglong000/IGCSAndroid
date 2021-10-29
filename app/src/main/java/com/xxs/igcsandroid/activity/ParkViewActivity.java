package com.xxs.igcsandroid.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ParkViewActivity extends AppCompatActivity {
    private SmartImageView sivPic;
    private TextView tvId;
    private TextView tvName;
    private TextView tvArea;
    private TextView tvAddress;
    private TextView tvRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_view);

        setTitle("园区信息");

        sivPic = findViewById(R.id.siv_parkPic);
        tvId = findViewById(R.id.tv_parkId);
        tvName = findViewById(R.id.tv_parkName);
        tvArea = findViewById(R.id.tv_parkArea);
        tvAddress = findViewById(R.id.tv_parkAddr);
        tvRemark = findViewById(R.id.tv_parkRemark);

        getParkInfoFromSrv();
    }

    private void getParkInfoFromSrv() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", MyApplication.getInstance().getMyUserId());

        AsyncSocketUtil.post(this, "user/getParkInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject dataObj = response.getJSONObject(1);
                    tvId.setText(dataObj.getString("id"));
                    tvName.setText(dataObj.getString("name"));
                    tvArea.setText(dataObj.getString("area"));
                    tvAddress.setText(dataObj.getString("addr"));
                    tvRemark.setText(dataObj.getString("remark"));

                    String picPath = dataObj.getString("pic");
                    if (picPath.length() > 0) {
                        sivPic.setImageUrl(Constants.SERVER_URL + "downloadFile/downloadFile?inputPath=" + picPath);
                    }
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(ParkViewActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                ParkViewActivity.this.finish();
            }
        });
    }
}
