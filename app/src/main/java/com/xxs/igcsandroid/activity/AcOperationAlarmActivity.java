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

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.OperationInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.StringUtil;

import org.json.JSONArray;

import java.util.LinkedHashMap;
import java.util.Map;

// 用于Operation中告警操作的添加和修改
public class AcOperationAlarmActivity extends AppCompatActivity {
    private String mOgId;
    private String mGhId;
    private String mOperationId;

    private Short type = 0;     // 1:添加；2:修改

    private RadioGroup rgOper;
    private RadioButton rbOn;
    private RadioButton rbOff;
    private EditText etPara1;
    private Button btnCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_operation_alarm);

        rgOper = findViewById(R.id.radioGroup_oper_alarm);
        rbOn = findViewById(R.id.radioButton_alarm);
        rbOff = findViewById(R.id.radioButton_alarm_sms);

        etPara1 = findViewById(R.id.et_para1);

        btnCommit = findViewById(R.id.btn_add);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    return;
                }
                gotoAddOperationAlarm();
            }
        });

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mOgId = bundle.getString("tgId");
        mGhId = bundle.getString("ghId");
        mOperationId = bundle.getString("operationId");
        if (StringUtil.isStringNullOrEmpty(mOperationId)) {
            setTitle(bundle.getString("ghName") + "->添加告警操作信息");
            type = 1;
        } else {
            setTitle(bundle.getString("ghName") + "->修改告警操作信息");
            type = 2;

            String ctrType = bundle.getString("controlType");
            if (ctrType.equals("ALARM")) {
                rbOn.setChecked(true);
            } else {
                rbOff.setChecked(true);
            }

            String aTime = bundle.getString("para1");
            etPara1.setText(aTime);

            btnCommit.setText("修  改");
        }
    }

    private void gotoAddOperationAlarm() {
        String para1 = etPara1.getText().toString();
        if (StringUtil.isStringNullOrEmpty(para1)) {
            DlgUtil.showMsgInfo(this, "请输入告警间隔！");
            return;
        }

        String ctrlType = "ALARM";
        if (rgOper.getCheckedRadioButtonId() == R.id.radioButton_alarm_sms) {
            ctrlType = "ALARM_SMS";
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("nodeId", mGhId);
        mp.put("controlType", ctrlType);
        mp.put("para1", para1);
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
                String str = "添加告警操作信息成功！";
                if (type == 2) {
                    str = "修改告警操作信息成功！";
                }
                DlgUtil.showMsgInfo(AcOperationAlarmActivity.this, str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        AcOperationAlarmActivity.this.finish();
                    }
                });
            }
        }, null);
    }
}
