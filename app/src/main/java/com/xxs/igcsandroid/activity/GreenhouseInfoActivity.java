package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaygoo.selector.MultiData;
import com.jaygoo.selector.MultiSelectPopWindow;
import com.jaygoo.selector.SignalSelectPopWindow;
import com.loopj.android.image.SmartImageView;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.entity.NodeInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.HandlePic;
import com.xxs.igcsandroid.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GreenhouseInfoActivity extends AppCompatActivity {
    private String mUserId;
    private String mGhId;

    private LinearLayout llGhId;
    private EditText etGhId;
    private EditText etName;
    private EditText etAdress;
    private EditText etRemark;
    private TextView tvUserOwn;
    private TextView nodesContain;
    private Button btnCommit;

    private HandlePic hdlPic;

    private Short type = 0;     // 1:添加；2:修改

    private List<MultiData> lstGwNodes = new ArrayList<>();
    private boolean bHasGetGwNodes = false;
    private Map<String, NodeInfo> mpNodes = new LinkedHashMap<>();

    private List<MultiData> lstUsers = new ArrayList<>();
    private String mSelUser;
    private boolean bHasGetUserList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greenhouse_info);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mUserId = bundle.getString("userId");
        mGhId = bundle.getString("ghId");
        if (StringUtil.isStringNullOrEmpty(mGhId)) {
            setTitle("添加温室信息");
            type = 1;
        } else {
            setTitle("修改温室信息");
            type = 2;
        }

        llGhId = findViewById(R.id.ll_ghId);
        etGhId = findViewById(R.id.et_ghId);
        etName = findViewById(R.id.et_ghName);
        etAdress = findViewById(R.id.et_address);
        etRemark = findViewById(R.id.et_remark);
        nodesContain = findViewById(R.id.sel_nodes);
        nodesContain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGwNodesSelect();
            }
        });

        hdlPic = new HandlePic(this);
        hdlPic.setImageView((SmartImageView) findViewById(R.id.iv_image));
        hdlPic.handleSelPic((Button) findViewById(R.id.btn_sel_pic));
        hdlPic.handleShotPic((Button) findViewById(R.id.btn_shot_pic));
        hdlPic.handleDelPic((Button) findViewById(R.id.btn_del_pic));

        tvUserOwn = findViewById(R.id.sel_gh_user);
        tvUserOwn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bHasGetUserList) {
                    getParkUsersFromServer();
                } else {
                    showUsersSel();
                }
            }
        });

        btnCommit = findViewById(R.id.btn_add);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    return;
                }
                gotoAddGhInfo();
            }
        });

        if (type == 2) {
            getGhFromSrv();
        }
    }

    private void showUsersSel() {
        new SignalSelectPopWindow.Builder(GreenhouseInfoActivity.this)
                .setDataArray(lstUsers)
                .setConfirmListener(new SignalSelectPopWindow.OnConfirmClickListener() {
                    @Override
                    public void onClick(Integer indexSel) {
                        tvUserOwn.setText(lstUsers.get(indexSel).getText());
                        mSelUser = lstUsers.get(indexSel).getId();

                        for (int i = 0; i < lstUsers.size(); i++) {
                            lstUsers.get(i).setbSelect(false);
                        }
                        lstUsers.get(indexSel).setbSelect(true);
                    }
                })
                .setTitle("用户列表")
                .build()
                .show(findViewById(R.id.mBottom_user));
    }

    private void getParkUsersFromServer() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("parentUserId", mUserId);

        AsyncSocketUtil.post(this, "user/queryUserInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    lstUsers.clear();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        String userId = obj.getString("userId");
                        if (userId.equals(mSelUser)) {
                            lstUsers.add(new MultiData(userId, obj.getString("userName"), true));
                        } else {
                            lstUsers.add(new MultiData(userId, obj.getString("userName"), false));
                        }
                    }

                    bHasGetUserList = true;

                    showUsersSel();
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(GreenhouseInfoActivity.this, e);
                }
            }
        }, null);
    }

    @Override
    protected void onDestroy() {
        hdlPic.resetFileSel();
        lstGwNodes.clear();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            hdlPic.onActivityResult(requestCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (type == 2) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_delete, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_delete:
                DlgUtil.showAsk(this, "确定要删除该温室信息吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doDeleteGh();
                    }
                });
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doDeleteGh() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(this, "gateway/delGhInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    DlgUtil.showMsgInfo(GreenhouseInfoActivity.this, "删除温室信息成功！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishWithResult();
                        }
                    });
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(GreenhouseInfoActivity.this, e);
                }
            }
        }, null);
    }

    private void getGhFromSrv() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(this, "gateway/getGhInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject dataObj = response.getJSONObject(1);

                    llGhId.setVisibility(View.GONE);
                    etGhId.setText(dataObj.getString("id"));
                    btnCommit.setText("修  改");
                    etName.setText(dataObj.getString("name"));
                    etAdress.setText(dataObj.getString("addr"));
                    etRemark.setText(dataObj.getString("remark"));
                    hdlPic.setInfoByPic(dataObj.getString("pic"));
                    mSelUser = dataObj.getString("userId");
                    tvUserOwn.setText(dataObj.getString("userName"));

                    mpNodes.clear();
                    String strSel = "";
                    JSONArray jArrayN = dataObj.getJSONArray("nodes");
                    for (int n = 0; n < jArrayN.length(); n++) {
                        JSONObject objN = jArrayN.getJSONObject(n);
                        NodeInfo nNode = new NodeInfo(objN, false);
                        mpNodes.put(nNode.getKeyId(), nNode);

                        if (strSel.length() > 0) {
                            strSel += "\r\n";
                        }
                        strSel += nNode.getValueShow();
                    }
                    nodesContain.setText(strSel);
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(GreenhouseInfoActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                GreenhouseInfoActivity.this.finish();
            }
        });
    }

    private void showGwNodesSelect() {
        if (!bHasGetGwNodes) {
            getGatewayNodesFromServer();
            return;
        }

        doShowGwNodesSelect();
    }

    private void doShowGwNodesSelect() {
        new MultiSelectPopWindow.Builder(GreenhouseInfoActivity.this)
                .setDataArray(lstGwNodes)
                .setConfirmListener(new MultiSelectPopWindow.OnConfirmClickListener() {
                    @Override
                    public void onClick(List<Integer> indexList) {
                        String strSel = "";
                        for (int i = 0; i < lstGwNodes.size(); i++) {
                            MultiData data = lstGwNodes.get(i);
                            if (indexList.contains(i)) {
                                data.setbSelect(true);
                                if (strSel.length() > 0) {
                                    strSel += "\r\n";
                                }
                                strSel += data.getText();
                            } else {
                                data.setbSelect(false);
                            }
                        }
                        nodesContain.setText(strSel);
                    }
                })
                .setTitle("节点列表")
                .build()
                .show(findViewById(R.id.mBottom));
    }

    private void getGatewayNodesFromServer() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mUserId);

        AsyncSocketUtil.post(this, "gateway/getNodesOfPark", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    lstGwNodes.clear();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        NodeInfo nInfo = new NodeInfo(obj, false);
                        String ghId = obj.getString("ghId");
                        boolean bSel = false;
                        if (mpNodes.containsKey(nInfo.getKeyId())) {
                            bSel = true;
                        }
                        lstGwNodes.add(new MultiData(nInfo.getKeyId(),
                                nInfo.getValueShow(), bSel, ghId));
                    }

                    bHasGetGwNodes = true;

                    doShowGwNodesSelect();
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(GreenhouseInfoActivity.this, e);
                }
            }
        }, null);
    }

    private void doAddGhInfo() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", mSelUser);
        mp.put("ghId", etGhId.getText().toString().trim());
        mp.put("ghName", etName.getText().toString().trim());
        mp.put("ghAddress", etAdress.getText().toString().trim());
        mp.put("ghRemark", etRemark.getText().toString().trim());
        mp.put("gwNodes", getGhNodesList());
        mp.put("picSrcFile", hdlPic.getPicSrcFile());
        hdlPic.getFileToUpdate(etGhId.getText().toString().trim() + System.currentTimeMillis());

        String url = "gateway/addGhInfoEx";
        if (type == 2) {
            url = "gateway/mdyGhInfoEx";
        }

        AsyncSocketUtil.postWithFile(this, url, mp, "picSel", hdlPic.getFileSel(), null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                String str = "添加温室信息成功！";
                if (type == 2) {
                    str = "修改温室信息成功！";
                }
                DlgUtil.showMsgInfo(GreenhouseInfoActivity.this, str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishWithResult();
                    }
                });
            }
        }, null);
    }

    private void finishWithResult() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        GreenhouseInfoActivity.this.finish();
    }

    private String getGhNodesList() {
        String strSensors = "";

        for (MultiData data : lstGwNodes) {
            if (data.isbSelect()) {
                if (strSensors.length() > 0) {
                    strSensors += ",";
                }
                strSensors += data.getId();
            }
        }

        return strSensors;
    }

    private void gotoAddGhInfo() {
        if (!CheckInputUtil.checkTextViewInput(etGhId, this, "请输入温室ID！")) {
            return;
        }
        if (!CheckInputUtil.checkTextViewInput(tvUserOwn, this, "请选择温室负责的用户！")) {
            return;
        }

        String strSels = "";
        for (MultiData data : lstGwNodes) {
            if (data.isbSelect()) {
                if (data.getExtraInfo().length() > 0) {
                    if (!data.getExtraInfo().equals(etGhId.getText().toString().trim())) {
                        if (strSels.length() > 0) {
                            strSels += ",";
                        }
                        strSels += data.getText();
                    }
                }
            }
        }

        if (strSels.length() > 0) {
            DlgUtil.showAsk(this, strSels + "已绑定到其它温室，确定要继续吗？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doAddGhInfo();
                }
            });
        } else {
            doAddGhInfo();
        }
    }
}
