package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jaygoo.selector.MultiData;
import com.jaygoo.selector.SignalSelectPopWindow;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.adapter.SpinnerCommonAdapter;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.SensorInfo;
import com.xxs.igcsandroid.entity.SpinnerCommonData;
import com.xxs.igcsandroid.entity.ThresholdInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AcThresholdInfoActivity extends AppCompatActivity {
    private String mTgId;
    private String mGhId;
    private String mThresholdId;

    private Short type = 0;     // 1:添加；2:修改

    private TextView tvSensor;
    private List<SensorInfo> mLstSensor;
    private List<MultiData> lstGws;
    private String mSelGw;
    private Spinner spinnerType;
    private SpinnerCommonAdapter adapterType;
    private String typeSelected;
    private EditText etType;
    private Spinner spinnerComp;
    private SpinnerCommonAdapter adapterComp;
    private String compSelected;
    private EditText etComp;
    private TextView tvUnit;
    private Button btnCommit;

    private String mFullName;
    private ThresholdInfo mTInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_threshold_info);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mTgId = bundle.getString("tgId");
        mGhId = bundle.getString("ghId");
        mThresholdId = bundle.getString("thresholdId");
        if (StringUtil.isStringNullOrEmpty(mThresholdId)) {
            setTitle(bundle.getString("ghName") + "->添加阈值信息");
            type = 1;
        } else {
            setTitle(bundle.getString("ghName") + "->修改阈值信息");
            type = 2;
            mFullName = bundle.getString("fullName");

            try {
                mTInfo = new ThresholdInfo(bundle.getString("typePara"), bundle.getString("typeId"), bundle.getInt("compPara"),
                        bundle.getString("compId"), bundle.getString("unit"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        tvSensor = findViewById(R.id.sel_sensor);
        tvSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignalSelectPopWindow.Builder(AcThresholdInfoActivity.this)
                        .setDataArray(lstGws)
                        .setConfirmListener(new SignalSelectPopWindow.OnConfirmClickListener() {
                            @Override
                            public void onClick(Integer indexSel) {
                                tvSensor.setText(lstGws.get(indexSel).getText());
                                mSelGw = lstGws.get(indexSel).getId();
                                tvUnit.setText(mLstSensor.get(Integer.valueOf(mSelGw)).getSensorUnit());

                                for (int i = 0; i < lstGws.size(); i++) {
                                    lstGws.get(i).setbSelect(false);
                                }
                                lstGws.get(indexSel).setbSelect(true);
                            }
                        })
                        .setTitle("传感器列表")
                        .build()
                        .show(findViewById(R.id.mBottom));
            }
        });

        spinnerType = findViewById(R.id.spinner_type);
        adapterType = new SpinnerCommonAdapter(this);
        spinnerType.setAdapter(adapterType);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeSelected = ((SpinnerCommonData)adapterType.getItem(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                typeSelected = null;
            }
        });
        etType = findViewById(R.id.et_type);

        spinnerComp = findViewById(R.id.spinner_comp);
        adapterComp = new SpinnerCommonAdapter(this);
        spinnerComp.setAdapter(adapterComp);
        spinnerComp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                compSelected = ((SpinnerCommonData)adapterComp.getItem(position)).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                compSelected = null;
            }
        });
        etComp = findViewById(R.id.et_comp);
        tvUnit = findViewById(R.id.tv_unit);

        btnCommit = findViewById(R.id.btn_add);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    return;
                }
                if (!checkThresholdInput()) {
                    return;
                }
                gotoAddThresholdInfo();
            }
        });

        getDatasFromSrv();
    }

    private void getDatasFromSrv() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(this, "autoctrl/getDatasForThresholdAdd", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONArray arraySensor = response.getJSONArray(1);
                    mLstSensor = new ArrayList<>();
                    lstGws = new ArrayList<>();
                    for (int i = 0; i < arraySensor.length(); i++) {
                        JSONObject obj = arraySensor.getJSONObject(i);
                        SensorInfo entity = new SensorInfo(obj);
                        mLstSensor.add(entity);

                        if (entity.getFullName().equals(mFullName)) {
                            mSelGw = String.valueOf(i);
                            lstGws.add(new MultiData(String.valueOf(i), entity.getFullName(), true));
                        } else {
                            lstGws.add(new MultiData(String.valueOf(i), entity.getFullName(), false));
                        }
                    }

                    JSONArray arrayType = response.getJSONArray(2);
                    ArrayList<SpinnerCommonData> lstEntity = new ArrayList<>();
                    int iType = 0;
                    for (int i = 0; i < arrayType.length(); i++) {
                        JSONObject obj = arrayType.getJSONObject(i);
                        SpinnerCommonData entity = new SpinnerCommonData(obj.getString("typeId"), obj.getString("typeName"));
                        lstEntity.add(entity);
                        if (mTInfo != null && entity.getId().equals(mTInfo.getTypeCode())) {
                            iType = i;
                        }
                    }
                    adapterType.setData(lstEntity);
                    adapterType.notifyDataSetChanged();
                    spinnerType.setSelection(iType);

                    JSONArray arrayComp = response.getJSONArray(3);
                    lstEntity = new ArrayList<>();
                    int iComp = 0;
                    for (int i = 0; i < arrayComp.length(); i++) {
                        JSONObject obj = arrayComp.getJSONObject(i);
                        SpinnerCommonData entity = new SpinnerCommonData(obj.getString("compId"), obj.getString("compName"));
                        lstEntity.add(entity);
                        if (mTInfo != null && entity.getId().equals(mTInfo.getCompCode())) {
                            iComp = i;
                        }
                    }
                    adapterComp.setData(lstEntity);
                    adapterComp.notifyDataSetChanged();
                    spinnerComp.setSelection(iComp);

                    if (type == 2) {
                        tvSensor.setText(mFullName);
                        etType.setText(mTInfo.getTypePara());
                        etComp.setText(String.valueOf(mTInfo.getCompPara()));
                        tvUnit.setText(mTInfo.getUnit());
                        btnCommit.setText("修  改");
                    }
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(AcThresholdInfoActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                AcThresholdInfoActivity.this.finish();
            }
        });
    }

    private boolean checkThresholdInput() {
        if (!CheckInputUtil.checkTextViewInput(tvSensor, this, "请选择传感器！")) {
            return false;
        }
        if (!CheckInputUtil.checkTextViewInput(etType, this, "请输入条件值！")) {
            return false;
        }
        if (!CheckInputUtil.checkTextViewInput(etComp, this, "请输入比较值！")) {
            return false;
        }
        return true;
    }

    private void gotoAddThresholdInfo() {
        Map<String, String> mp = new LinkedHashMap<>();
        SensorInfo sensor = mLstSensor.get(Integer.valueOf(mSelGw));
        mp.put("gwId", sensor.getAtGwId());
        mp.put("nodeId", sensor.getAtNodeId());
        mp.put("sensorTypeCode", sensor.getSensorTypeCode());
        mp.put("sensorId", sensor.getSensorId());
        mp.put("typeCode", typeSelected);
        mp.put("typePara", etType.getText().toString().trim());
        mp.put("compCode", compSelected);
        mp.put("compPara", etComp.getText().toString().trim());
        mp.put("userId", MyApplication.getInstance().getMyUserId());

        String url = "autoctrl/addThresholdInfo";
        if (type == 1) {
            mThresholdId = "" + System.currentTimeMillis();
            mp.put("thresholdId", mThresholdId);
            mp.put("tgId", mTgId);
        } else if (type == 2) {
            url = "autoctrl/mdyThresholdInfo";
            mp.put("thresholdId", mThresholdId);
        }

        AsyncSocketUtil.post(this, url, mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                String str = "添加阈值信息成功！";
                if (type == 2) {
                    str = "修改阈值信息成功！";
                }
                DlgUtil.showMsgInfo(AcThresholdInfoActivity.this, str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        AcThresholdInfoActivity.this.finish();
                    }
                });
            }
        }, null);
    }
}
