package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.loopj.android.image.SmartImageView;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.HandlePic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParkInfoActivity extends AppCompatActivity {
    private LinearLayout llParkId;
    private EditText etParkId;
    private EditText etParkName;
    private EditText etArea;
    private EditText etAdress;
    private EditText etRemark;
    private Button btnCommit;

    private HandlePic hdlPic;

    private String mUserId;
    private Short type = 0;     // 1:添加；2:修改

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_info);

        hdlPic = new HandlePic(this);

        mUserId = MyApplication.getInstance().getMyUserId();

        llParkId = findViewById(R.id.ll_parkId);
        etParkId = findViewById(R.id.et_parkId);
        etParkName = findViewById(R.id.et_parkName);
        etArea = findViewById(R.id.et_area);
        etAdress = findViewById(R.id.et_address);
        etRemark = findViewById(R.id.et_remark);

        hdlPic.setImageView((SmartImageView) findViewById(R.id.iv_image));
        hdlPic.handleSelPic((Button) findViewById(R.id.btn_sel_pic));
        hdlPic.handleShotPic((Button) findViewById(R.id.btn_shot_pic));
        hdlPic.handleDelPic((Button) findViewById(R.id.btn_del_pic));

        btnCommit = findViewById(R.id.btn_add);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    return;
                }
                if (!checkParkInput()) {
                    return;
                }
                gotoAddParkInfo();
            }
        });

        getParkInfoFromSrv();
    }

    @Override
    protected void onDestroy() {
        hdlPic.resetFileSel();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            hdlPic.onActivityResult(requestCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkParkInput() {
        if (!CheckInputUtil.checkTextViewInput(etParkId, this, "请输入园区ID！")) {
            return false;
        }
        return true;
    }

    private void gotoAddParkInfo() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mUserId);
        mp.put("parkId", etParkId.getText().toString().trim());
        mp.put("parkName", etParkName.getText().toString().trim());
        mp.put("parkArea", etArea.getText().toString().trim());
        mp.put("parkAddress", etAdress.getText().toString().trim());
        mp.put("parkRemark", etRemark.getText().toString().trim());
        mp.put("picSrcFile", hdlPic.getPicSrcFile());
        hdlPic.getFileToUpdate(etParkId.getText().toString().trim() + System.currentTimeMillis());

        String url = "user/addParkInfo";
        if (type == 2) {
            url = "user/mdyParkInfo";
        }

        AsyncSocketUtil.postWithFile(this, url, mp, "picSel", hdlPic.getFileSel(), null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                String str = "添加园区信息成功！";
                if (type == 2) {
                    str = "修改园区信息成功！";
                }
                DlgUtil.showMsgInfo(ParkInfoActivity.this, str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParkInfoActivity.this.finish();
                    }
                });
            }
        }, null);
    }

    private void getParkInfoFromSrv() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mUserId);

        AsyncSocketUtil.post(this, "user/getParkInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject msgObj = response.getJSONObject(0);
                    String msg = msgObj.getString("message");
                    if (msg.equals("success")) {
                        type = 2;

                        JSONObject dataObj = response.getJSONObject(1);
                        setUiToMdy(dataObj.getString("id"));
                        etParkName.setText(dataObj.getString("name"));
                        etArea.setText(dataObj.getString("area"));
                        etAdress.setText(dataObj.getString("addr"));
                        etRemark.setText(dataObj.getString("remark"));

                        hdlPic.setInfoByPic(dataObj.getString("pic"));
                    } else if (msg.equals("add")) {
                        type = 1;
                        setTitle("添加园区信息");
                    }
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(ParkInfoActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                ParkInfoActivity.this.finish();
            }
        });
    }

    private void setUiToMdy(String parkId) {
        setTitle("修改园区信息->" + parkId);
        llParkId.setVisibility(View.GONE);
        etParkId.setText(parkId);
        btnCommit.setText("修  改");
    }
}
