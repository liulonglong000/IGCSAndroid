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
import com.xxs.igcsandroid.entity.EncodeInfo;
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

public class AcEncodeActivity extends AppCompatActivity {
    private EncodeInfo ecInfo;

    private EditText etCurQs;
    private EditText etTotalQs;
    private Button btnMdy;

    private Handler handler;
    private Map<String, CheckResultThread> mThreads = new HashMap<>();
    private boolean bEndThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_encode);

        setTitle("设置编码器参数");

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();

        ecInfo = new EncodeInfo(bundle);

        TextView tvTlName = findViewById(R.id.tv_encode_name);
        tvTlName.setText(ecInfo.getNameString());

        etCurQs = findViewById(R.id.et_cur_qs);
        etCurQs.setText(ecInfo.getCurQs());
        etTotalQs = findViewById(R.id.et_total_qs);
        etTotalQs.setText(ecInfo.getTotalQs());

        btnMdy = findViewById(R.id.btn_modify);
        btnMdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doModifyEncode();
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
                        if (tradeId.startsWith("d_")) {
                            getSetEncodeResult(tradeId, count);
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

    private void doModifyEncode() {
        if (!CheckInputUtil.checkTextViewInput(etCurQs, this, "请输入当前圈数！")) {
            return;
        }
        if (!CheckInputUtil.checkTextViewInput(etTotalQs, this, "请输入总圈数！")) {
            return;
        }

        boolean bModified = false;
        do {
            if (!etCurQs.getText().toString().equals(ecInfo.getCurQs())) {
                bModified = true;
                break;
            }
            if (!etTotalQs.getText().toString().equals(ecInfo.getTotalQs())) {
                bModified = true;
                break;
            }
        } while(false);
        if (!bModified) {
            DlgUtil.showMsgInfo(this, "没有修改，无需保存");
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        final String tradeId = "d_" + System.currentTimeMillis();
        mp.put("tradeId", tradeId);
        mp.put("gwId", ecInfo.getGwId());
        mp.put("nodeId", ecInfo.getNodeId());
        mp.put("equiType", ecInfo.getEquiType());
        mp.put("equiId", ecInfo.getEquiId());
        mp.put("userId", MyApplication.getInstance().getMyUserId());
        mp.put("controlType", "SetEncoder");
        mp.put("para1", etCurQs.getText().toString());
        mp.put("para2", etTotalQs.getText().toString());

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

    private void getSetEncodeResult(final String tradeId, final int count) {
        CommuCmdUtil.getTradeResult(this, tradeId, count, new CommuCmdUtil.OnGetTradeResult() {
            @Override
            public void OnSuccess(String result) {
                DlgUtil.showMsgInfo(AcEncodeActivity.this, "设置编码器参数成功！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        setOperateEnable(true);
                        finish();
                    }
                });
            }

            @Override
            public void OnFail(String errMsg) {
                DlgUtil.showMsgInfo(AcEncodeActivity.this, "设置编码器参数失败：" + errMsg, new DialogInterface.OnClickListener() {
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