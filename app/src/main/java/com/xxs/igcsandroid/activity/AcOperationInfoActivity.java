package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jaygoo.selector.MultiData;
import com.jaygoo.selector.SignalSelectPopWindow;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.OperationInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.StringUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AcOperationInfoActivity extends AppCompatActivity {
    private String mOgId;
    private String mGhId;
    private String mOperationId;

    private Short type = 0;     // 1:添加；2:修改

    private TextView tvEquip;
    private List<OperationInfo> mLstEquip;
    private List<MultiData> lstEquips;
    private String mSelEquip;
    private RadioGroup rgOper;
    private RadioButton rbOn;
    private RadioButton rbOff;
    private EditText etPara1;
    private EditText etPara2;
    private Button btnCommit;

    private String mFullName;
    private OperationInfo mOInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_operation_info);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mOgId = bundle.getString("tgId");
        mGhId = bundle.getString("ghId");
        mOperationId = bundle.getString("operationId");
        if (StringUtil.isStringNullOrEmpty(mOperationId)) {
            setTitle(bundle.getString("ghName") + "->添加操作信息");
            type = 1;
        } else {
            setTitle(bundle.getString("ghName") + "->修改操作信息");
            type = 2;
            mFullName = bundle.getString("fullName");

            try {
                mOInfo = new OperationInfo(bundle.getString("controlType"), bundle.getString("para1"), bundle.getString("para2"),
                        bundle.getString("para3"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        tvEquip = findViewById(R.id.sel_equip);
        tvEquip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignalSelectPopWindow.Builder(AcOperationInfoActivity.this)
                        .setDataArray(lstEquips)
                        .setConfirmListener(new SignalSelectPopWindow.OnConfirmClickListener() {
                            @Override
                            public void onClick(Integer indexSel) {
                                tvEquip.setText(lstEquips.get(indexSel).getText());
                                mSelEquip = lstEquips.get(indexSel).getId();

                                for (int i = 0; i < lstEquips.size(); i++) {
                                    lstEquips.get(i).setbSelect(false);
                                }
                                lstEquips.get(indexSel).setbSelect(true);
                            }
                        })
                        .setTitle("设备列表")
                        .build()
                        .show(findViewById(R.id.mBottom));
            }
        });

        rgOper = findViewById(R.id.radioGroup_oper);
        rbOn = findViewById(R.id.radioButton_open);
        rbOff = findViewById(R.id.radioButton_close);

        etPara1 = findViewById(R.id.et_para1);
        etPara2 = findViewById(R.id.et_para2);

        btnCommit = findViewById(R.id.btn_add);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    return;
                }
                gotoAddOperationInfo();
            }
        });

        getDatasFromSrv();
    }

    private void getDatasFromSrv() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(this, "autoctrl/getDatasForOperationAdd", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    mLstEquip = new ArrayList<>();
                    lstEquips = new ArrayList<>();
                    for (int i = 1; i < response.length(); i++) {
                        OperationInfo entity = new OperationInfo(response.getJSONArray(i));
                        mLstEquip.add(entity);

                        if (type == 2) {
                            if ((entity.getFullName().equals(mFullName))
                                    || (entity.getFullName().startsWith(mFullName))) {
                                mSelEquip = String.valueOf(i - 1);
                                lstEquips.add(new MultiData(String.valueOf(i - 1), entity.getFullName(), true));
                            } else {
                                lstEquips.add(new MultiData(String.valueOf(i - 1), entity.getFullName(), false));
                            }
                        } else {
                            lstEquips.add(new MultiData(String.valueOf(i - 1), entity.getFullName(), false));
                        }
                    }

                    if (type == 2) {
                        tvEquip.setText(mFullName);
                        String ctrlType = mOInfo.getControlType();
                        if (ctrlType.endsWith("ON") || ctrlType.endsWith("FWD")) {
                            rbOn.setChecked(true);
                        } else {
                            rbOff.setChecked(true);
                        }
                        etPara1.setText(String.valueOf(mOInfo.getControlParameter1()));
                        etPara2.setText(String.valueOf(mOInfo.getControlParameter2()));
                        btnCommit.setText("修  改");
                    }
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(AcOperationInfoActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                AcOperationInfoActivity.this.finish();
            }
        });
    }

    private void gotoAddOperationInfo() {
        if (!CheckInputUtil.checkTextViewInput(tvEquip, this, "请选择设备！")) {
            return;
        }

        OperationInfo oper = mLstEquip.get(Integer.valueOf(mSelEquip));
        Short sType = oper.getType();
        String equipTypeCode = oper.getEquipmentTypeCode();
        String equipTypeName = oper.getEquipmentTypeName();
        String ctrlType = "";
        String para1 = etPara1.getText().toString();
        String para2 = etPara2.getText().toString();
        String para3 = null;
        if (rgOper.getCheckedRadioButtonId() == R.id.radioButton_open) {
            if (equipTypeCode.equals("LED")) {
                ctrlType = equipTypeCode + "ON";
                if (!StringUtil.isStringNullOrEmpty(para2)) {
                    DlgUtil.showMsgInfo(AcOperationInfoActivity.this, "参数2对打开" + equipTypeName + "无效，请删除！");
                    return;
                }
            } else if (equipTypeCode.equals("DRIP")) {
                ctrlType = equipTypeCode + "ON";
                if (!StringUtil.isStringNullOrEmpty(para2)) {
                    DlgUtil.showMsgInfo(AcOperationInfoActivity.this, "参数2对打开" + equipTypeName + "无效，请删除！");
                    return;
                }
            } else if (equipTypeCode.equals("RBM")) {
                ctrlType = equipTypeCode + "FWD";
                if (!StringUtil.isStringNullOrEmpty(para1) && !StringUtil.isStringNullOrEmpty(para2)) {
                    DlgUtil.showMsgInfo(AcOperationInfoActivity.this, "参数1和参数2只能设置一个，请删除一个！");
                    return;
                }
            } else if (equipTypeCode.equals("FRM")) {
                ctrlType = equipTypeCode + "FWD";
//                if (!StringUtil.isStringNullOrEmpty(para1) && !StringUtil.isStringNullOrEmpty(para2)) {
//                    DlgUtil.showMsgInfo(AcOperationInfoActivity.this, "参数1和参数2只能设置一个，请删除一个！");
//                    return;
//                }
                if (!StringUtil.isStringNullOrEmpty(para2)) {
                    DlgUtil.showMsgInfo(AcOperationInfoActivity.this, "参数2对打开" + equipTypeName + "无效，请删除！");
                    return;
                }
            }
        } else {
            if (equipTypeCode.equals("LED")) {
                ctrlType = equipTypeCode + "OFF";
                if (!StringUtil.isStringNullOrEmpty(para1)) {
                    DlgUtil.showMsgInfo(AcOperationInfoActivity.this, "参数1对关闭" + equipTypeName + "无效，请删除！");
                    return;
                }
                if (!StringUtil.isStringNullOrEmpty(para2)) {
                    DlgUtil.showMsgInfo(AcOperationInfoActivity.this, "参数2对关闭" + equipTypeName + "无效，请删除！");
                    return;
                }
            } else if (equipTypeCode.equals("DRIP")) {
                ctrlType = equipTypeCode + "OFF";
                if (!StringUtil.isStringNullOrEmpty(para1)) {
                    DlgUtil.showMsgInfo(AcOperationInfoActivity.this, "参数1对关闭" + equipTypeName + "无效，请删除！");
                    return;
                }
                if (!StringUtil.isStringNullOrEmpty(para2)) {
                    DlgUtil.showMsgInfo(AcOperationInfoActivity.this, "参数2对关闭" + equipTypeName + "无效，请删除！");
                    return;
                }
            } else if (equipTypeCode.equals("RBM")) {
                ctrlType = equipTypeCode + "REV";
                if (!StringUtil.isStringNullOrEmpty(para1) && !StringUtil.isStringNullOrEmpty(para2)) {
                    DlgUtil.showMsgInfo(AcOperationInfoActivity.this, "参数1和参数2只能设置一个，请删除一个！");
                    return;
                }
            } else if (equipTypeCode.equals("FRM")) {
                ctrlType = equipTypeCode + "REV";
//                if (!StringUtil.isStringNullOrEmpty(para1) && !StringUtil.isStringNullOrEmpty(para2)) {
//                    DlgUtil.showMsgInfo(AcOperationInfoActivity.this, "参数1和参数2只能设置一个，请删除一个！");
//                    return;
//                }
                if (!StringUtil.isStringNullOrEmpty(para2)) {
                    DlgUtil.showMsgInfo(AcOperationInfoActivity.this, "参数2对关闭" + equipTypeName + "无效，请删除！");
                    return;
                }
            }
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("gwId", oper.getGwId());
        mp.put("nodeId", oper.getNodeId());
        mp.put("equipTypeCode", equipTypeCode);
        mp.put("equipId", oper.getEquipmentId());
        mp.put("controlType", ctrlType);
        mp.put("para1", para1);
        mp.put("para2", para2);
        mp.put("para3", para3);
        mp.put("userId", MyApplication.getInstance().getMyUserId());

        String url = "autoctrl/addOperationInfo";
        if (type == 1) {
            mOperationId = "" + System.currentTimeMillis();
            mp.put("operationId", mOperationId);
            mp.put("ogId", mOgId);
        } else if (type == 2) {
            url = "autoctrl/mdyOperationInfo";
            mp.put("operationId", mOperationId);
        }

        AsyncSocketUtil.post(this, url, mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                String str = "添加操作信息成功！";
                if (type == 2) {
                    str = "修改操作信息成功！";
                }
                DlgUtil.showMsgInfo(AcOperationInfoActivity.this, str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        AcOperationInfoActivity.this.finish();
                    }
                });
            }
        }, null);
    }
}
