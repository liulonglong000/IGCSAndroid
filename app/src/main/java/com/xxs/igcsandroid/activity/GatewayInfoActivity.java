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
import com.xxs.igcsandroid.entity.UserRole;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.HandlePic;
import com.xxs.igcsandroid.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class GatewayInfoActivity extends AppCompatActivity {
    private String mUserId;
    private String mUserRole;
    private String mGatewayId;

    private LinearLayout llParkId;
    private EditText etParkId;
    private EditText etParkName;
    private EditText etAdress;
    private EditText etRemark;
    private Button btnCommit;

    private HandlePic hdlPic;
    
    private Short type = 0;     // 1:添加；2:修改

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_info);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mUserId = bundle.getString("userId");
        mUserRole = bundle.getString("userRole");
        mGatewayId = bundle.getString("gatewayId");
        if (StringUtil.isStringNullOrEmpty(mGatewayId)) {
            setTitle("添加网关信息");
            type = 1;
        } else {
            setTitle("修改网关信息");
            type = 2;
        }

        hdlPic = new HandlePic(this);

        llParkId = findViewById(R.id.ll_gwId);
        etParkId = findViewById(R.id.et_gwId);
        etParkName = findViewById(R.id.et_gwName);
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
                if (!checkGatewayInput()) {
                    return;
                }
                gotoAddGatewayInfo();
            }
        });

        if (type == 2) {
            getGatewayFromSrv();
        }
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

    private boolean checkGatewayInput() {
        if (!CheckInputUtil.checkTextViewInput(etParkId, this, "请输入网关ID！")) {
            return false;
        }
        return true;
    }

    private void gotoAddGatewayInfo() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mUserId);
        mp.put("gatewayId", etParkId.getText().toString().trim());
        mp.put("gatewayName", etParkName.getText().toString().trim());
        mp.put("gatewayAddress", etAdress.getText().toString().trim());
        mp.put("gatewayRemark", etRemark.getText().toString().trim());
        mp.put("picSrcFile", hdlPic.getPicSrcFile());
        hdlPic.getFileToUpdate(etParkId.getText().toString().trim() + System.currentTimeMillis());

        String url = "gateway/addGatewayInfo";
        if (type == 2) {
            url = "gateway/mdyGatewayInfo";
        }

        AsyncSocketUtil.postWithFile(this, url, mp, "picSel", hdlPic.getFileSel(), null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                String str = "添加网关信息成功！";
                if (type == 2) {
                    str = "修改网关信息成功！";
                }
                DlgUtil.showMsgInfo(GatewayInfoActivity.this, str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        GatewayInfoActivity.this.finish();
                    }
                });
            }
        }, null);
    }

    private void getGatewayFromSrv() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("gatewayId", mGatewayId);

        AsyncSocketUtil.post(this, "gateway/getGatewayInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject dataObj = response.getJSONObject(1);
                    setUiToMdy(dataObj.getString("id"));
                    etParkName.setText(dataObj.getString("name"));
                    etAdress.setText(dataObj.getString("addr"));
                    etRemark.setText(dataObj.getString("remark"));

                    hdlPic.setInfoByPic(dataObj.getString("pic"));
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(GatewayInfoActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                GatewayInfoActivity.this.finish();
            }
        });
    }

    private void setUiToMdy(String id) {
        llParkId.setVisibility(View.GONE);
        etParkId.setText(id);
        btnCommit.setText("修  改");
    }
}
