package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.ezuikit.PlayActivity;
import com.xxs.igcsandroid.util.DlgUtil;

public class VideoMonitorActivity extends AppCompatActivity {
    private String mGhId;

    /**
     * 开发者申请的Appkey
     */
    private String mAppKey;

    /**
     * 授权accesstoken
     */
    private String mAccessToken;

    /**
     * 播放url：ezopen协议
     */
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_monitor);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mGhId = bundle.getString("ghId");
        setTitle(bundle.getString("ghName"));

//        DlgUtil.showMsgInfo(this, "未安装摄像头！", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        });

        showVideo();
    }

    private void showVideo() {
        mAppKey = "26810f3acd794862b608b6cfbc32a6b8";
        mAccessToken = "ra.33funzg37c95cfra4n52w4mt45c2e0kg-64c6umkwk6-0dvsn6g-sqhxs94zq";
        mUrl = "ezopen://open.ys7.com/203751922/1.live";

        PlayActivity.startPlayActivity(this, mAppKey, mAccessToken, mUrl);
    }
}
