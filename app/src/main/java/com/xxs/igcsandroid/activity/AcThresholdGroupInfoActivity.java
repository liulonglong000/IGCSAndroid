package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.adapter.ThresholdAdapter;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.ThresholdInfo;
import com.xxs.igcsandroid.golbal.Constants;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AcThresholdGroupInfoActivity extends AppCompatActivity {
    private String mGhId;
    private String mGhName;
    private String mThresholdGroupId;

    private Short type = 0;     // 1:添加；2:修改

    private LinearLayout llBaseInfo;
    private LinearLayout llThresholdInfo;
    private ImageView ivBaseMore;
    private ImageView ivThresholdMore;
    private LinearLayout llThresholdRoot;
    private LinearLayout llBaseTitle;
    private LinearLayout llThresholdTitle;
    private Button btnCommit;
    private EditText etTgName;
    private EditText etRemark;

    private ThresholdAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_threshold_group_info);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mGhId = bundle.getString("ghId");
        mGhName = bundle.getString("ghName");
        mThresholdGroupId = bundle.getString("tgId");
        if (StringUtil.isStringNullOrEmpty(mThresholdGroupId)) {
            setTitle(mGhName + "->添加阈值组信息");
            type = 1;
        } else {
            setTitle(mGhName + "->修改阈值组信息");
            type = 2;
        }

        ivBaseMore = findViewById(R.id.iv_base_more);
        ivThresholdMore = findViewById(R.id.iv_threshold_more);
        llBaseInfo = findViewById(R.id.ll_base_info);
        llThresholdInfo = findViewById(R.id.ll_threshold_info);
        llBaseTitle = findViewById(R.id.ll_base_title);
        llBaseTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llBaseInfo.getVisibility() == View.GONE) {
                    llBaseInfo.setVisibility(View.VISIBLE);
                    ivBaseMore.setImageResource(R.drawable.ic_more_detail);
                } else {
                    llBaseInfo.setVisibility(View.GONE);
                    ivBaseMore.setImageResource(R.drawable.ic_more);
                }
            }
        });

        llThresholdTitle = findViewById(R.id.ll_threshold_title);
        llThresholdTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llThresholdInfo.getVisibility() == View.GONE) {
                    llThresholdInfo.setVisibility(View.VISIBLE);
                    ivThresholdMore.setImageResource(R.drawable.ic_more_detail);
                } else {
                    llThresholdInfo.setVisibility(View.GONE);
                    ivThresholdMore.setImageResource(R.drawable.ic_more);
                }
            }
        });

        llThresholdRoot = findViewById(R.id.ll_threshold_root);

        etTgName = findViewById(R.id.et_tgName);
        etRemark = findViewById(R.id.et_remark);

        adapter = new ThresholdAdapter(this, mThresholdGroupId, mGhId, mGhName);
        ListView lv_monitor = findViewById(R.id.listView_threshold);
        lv_monitor.setAdapter(adapter);

        btnCommit = findViewById(R.id.btn_add);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    return;
                }
                if (!checkTgInput()) {
                    return;
                }
                gotoAddTgInfo();
            }
        });

        findViewById(R.id.btn_add_threshold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AcThresholdGroupInfoActivity.this, AcThresholdInfoActivity.class);
                intent.putExtra("tgId", mThresholdGroupId);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                startActivityForResult(intent, Constants.ACTIVITY_RESULT_TG_RELOAD);
            }
        });

        if (type == 1) {
            llBaseTitle.callOnClick();
        } else if (type == 2) {
            setUIToModify();
            getThresholdGroupFromSrv();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.ACTIVITY_RESULT_TG_RELOAD) {
                getThresholdGroupFromSrv();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setUIToModify() {
        btnCommit.setText("修  改");
        if (llThresholdRoot.getVisibility() == View.GONE) {
            llThresholdRoot.setVisibility(View.VISIBLE);
            llThresholdTitle.callOnClick();
        }
    }

    public void getThresholdGroupFromSrv() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("tgId", mThresholdGroupId);

        AsyncSocketUtil.post(this, "autoctrl/getThresholdGroupInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject dataObj = response.getJSONObject(1);
                    etTgName.setText(dataObj.getString("name"));
                    etRemark.setText(dataObj.getString("remark"));

                    ArrayList<ThresholdInfo> lstEntity = new ArrayList<>();
                    for (int i = 2; i < response.length(); i++) {
                        ThresholdInfo info = new ThresholdInfo(response.getJSONObject(i));
                        lstEntity.add(info);
                    }
                    adapter.setDataInfo(lstEntity);
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(AcThresholdGroupInfoActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                AcThresholdGroupInfoActivity.this.finish();
            }
        });
    }

    private boolean checkTgInput() {
        if (!CheckInputUtil.checkTextViewInput(etTgName, this, "请输入阈值组名称！")) {
            return false;
        }
        return true;
    }

    private void gotoAddTgInfo() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", MyApplication.getInstance().getMyUserId());
        mp.put("tgName", etTgName.getText().toString().trim());
        mp.put("remark", etRemark.getText().toString().trim());

        String url = "autoctrl/addThresholdGroupInfo";
        if (type == 1) {
            mThresholdGroupId = "" + System.currentTimeMillis();
            mp.put("tgId", mThresholdGroupId);
            mp.put("ghId", mGhId);
        } else if (type == 2) {
            url = "autoctrl/mdyThresholdGroupInfo";
            mp.put("tgId", mThresholdGroupId);
        }

        AsyncSocketUtil.post(this, url, mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                String str = "添加阈值组信息成功！";
                if (type == 2) {
                    str = "修改阈值组信息成功！";
                }
                DlgUtil.showMsgInfo(AcThresholdGroupInfoActivity.this, str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type == 1) {
                            setUIToModify();
                            type = 2;
                        }
                    }
                });
            }
        }, null);
    }
}
