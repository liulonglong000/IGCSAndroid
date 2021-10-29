package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.TimeLimitInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.thread.CheckResultThread;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.CommuCmdUtil;
import com.xxs.igcsandroid.util.DecimalDigitsInputFilter;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AcTimeLimitActivity extends AppCompatActivity {
    private TimeLimitInfo tlInfo;

    private EditText etLocate;
    private EditText etTimeUp;
    private EditText etTimeDown;
    private EditText etTimeError;
    private Button btnMdy;

    private Handler handler;
    private Map<String, CheckResultThread> mThreads = new HashMap<>();
    private boolean bEndThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_time_limit);

        setTitle("下位机手动时间限位设置");

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();

        tlInfo = new TimeLimitInfo(bundle);

        TextView tvTlName = findViewById(R.id.tv_lt_name);
        tvTlName.setText(tlInfo.getNameString());

        etLocate = findViewById(R.id.et_lt_locate);
        etLocate.setText(tlInfo.getLocate());
        etLocate.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        etTimeUp = findViewById(R.id.et_lt_time_up);
        etTimeUp.setText(tlInfo.getTimeUp());
        etTimeDown = findViewById(R.id.et_lt_time_down);
        etTimeDown.setText(tlInfo.getTimeDown());
        etTimeError = findViewById(R.id.et_lt_time_error);
        etTimeError.setText(tlInfo.getTimeError());

        btnMdy = findViewById(R.id.btn_modify);
        btnMdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doModifyTimeLimit();
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if (bEndThread) {
                            return;
                        }
                        Bundle bundle = msg.getData();
                        String tradeId = bundle.getString("tradeId");
                        int count = bundle.getInt("count");
                        CheckResultThread t = mThreads.get(tradeId);
                        handler.removeCallbacks(t);
                        t.interrupt();
                        t = null;
                        mThreads.remove(tradeId);
                        if (tradeId.startsWith("a_")) {
                            getSetTimeLimitResult(tradeId, count);
                        }
                        return;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onDestroy() {
        bEndThread = true;

        super.onDestroy();

        handler.removeMessages(1);
        for (CheckResultThread t : mThreads.values()) {
            handler.removeCallbacks(t);
        }
    }

    private void doModifyTimeLimit() {
        if (!CheckInputUtil.checkTextViewInput(etLocate, this, "请输入当前位置！")) {
            return;
        }
        if (!CheckInputUtil.checkTextViewInput(etTimeUp, this, "请输入向上运转总时长！")) {
            return;
        }
        if (!CheckInputUtil.checkTextViewInput(etTimeDown, this, "请输入向下运转总时长！")) {
            return;
        }
        if (!CheckInputUtil.checkTextViewInput(etTimeError, this, "请输入告警的间隔阈值！")) {
            return;
        }

        boolean bModified = false;
        do {
            if (!etLocate.getText().toString().equals(tlInfo.getLocate())) {
                bModified = true;
                break;
            }
            if (!etTimeUp.getText().toString().equals(tlInfo.getTimeUp())) {
                bModified = true;
                break;
            }
            if (!etTimeDown.getText().toString().equals(tlInfo.getTimeDown())) {
                bModified = true;
                break;
            }
            if (!etTimeError.getText().toString().equals(tlInfo.getTimeError())) {
                bModified = true;
                break;
            }
        } while(false);
        if (!bModified) {
            DlgUtil.showMsgInfo(this, "没有修改，无需保存");
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        final String tradeId = "a_" + System.currentTimeMillis();
        mp.put("tradeId", tradeId);
        mp.put("gwId", tlInfo.getGwId());
        mp.put("nodeId", tlInfo.getNodeId());
        mp.put("equiType", tlInfo.getEquiType());
        mp.put("equiId", tlInfo.getEquiId());
        mp.put("userId", MyApplication.getInstance().getMyUserId());
        mp.put("controlType", "SetTimeLimit");
        mp.put("para1", "{" + etTimeUp.getText().toString() + "," + etTimeDown.getText().toString() + "}");
        mp.put("para2", etLocate.getText().toString());
        mp.put("para3", etTimeError.getText().toString());

        setOperateEnable(false);

        AsyncSocketUtil.post(this, "autoctrl/addTradeEx", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                startCheck(tradeId, 1);
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                setOperateEnable(true);
            }
        });
    }

    private void startCheck(String tradeId, int nCnt) {
        CheckResultThread thread = new CheckResultThread(this, handler, 1);
        thread.setData(tradeId, nCnt);
        mThreads.put(tradeId, thread);
        thread.start();
    }

    private void setOperateEnable(boolean bEnable) {
        btnMdy.setEnabled(bEnable);
    }

    private void getSetTimeLimitResult(final String tradeId, final int count) {
        CommuCmdUtil.getTradeResult(this, tradeId, count, new CommuCmdUtil.OnGetTradeResult() {
            @Override
            public void OnSuccess(String result) {
                DlgUtil.showMsgInfo(AcTimeLimitActivity.this, "进行手动时间限位设置成功！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        setOperateEnable(true);
                        finish();
                    }
                });
            }

            @Override
            public void OnFail(String errMsg) {
                DlgUtil.showMsgInfo(AcTimeLimitActivity.this, "进行手动时间限位设置失败：" + errMsg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setOperateEnable(true);
                    }
                });
            }

            @Override
            public void OnContinue() {
                startCheck(tradeId, count + 1);
            }
        });
    }
}