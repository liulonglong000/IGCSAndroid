package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.ArmStrategy;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.thread.CheckResultThread;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DateTimePickDialogUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AcArmStrategyValueActivity extends AppCompatActivity {
    private ArmStrategy curArmS;

    private Switch status;
    private TextView btnStartTime;
    private TextView btnEndTime;
    private EditText etSensorVal;
    private EditText etParaTime;
    private Button btnMdy;
    private boolean bSyncTime = false;

    private Handler handler;        // 简单理解，Handler就是解决线程和线程之间的通信的
    private Map<String, CheckResultThread> mThreads = new HashMap<>();
    private Map<String, CheckResultThread> mRefreashThreads = new HashMap<>();
    private boolean bEndThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_arm_strategy_value);

        setTitle("修改下位机策略");

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();

        curArmS = new ArmStrategy(bundle);

        TextView tvCtrlType = findViewById(R.id.tv_control_type);
        tvCtrlType.setText(curArmS.getThresholdTypeString());

        TextView tvGwNode = findViewById(R.id.gw_node_info);
        tvGwNode.setText(curArmS.getPositionString());

        status = findViewById(R.id.switch_status);
        status.setChecked(curArmS.getIsUseBool());

        btnStartTime = findViewById(R.id.start_time);
        btnStartTime.setText(String.format("%s:%s",
                curArmS.getStartHourEx(), curArmS.getStartMinEx()));
        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedShowStartTime();
            }
        });

        btnEndTime = findViewById(R.id.end_time);
        btnEndTime.setText(String.format("%s:%s",
                curArmS.getEndHourEx(), curArmS.getEndMinEx()));
        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedShowEndTime();
            }
        });

        TextView tvArmSensor1 = findViewById(R.id.arm_sensor1);
        tvArmSensor1.setText(curArmS.getSensorInfo1());

        etSensorVal = findViewById(R.id.et_sensor_value);
        etSensorVal.setText(curArmS.getComparePara());

        TextView tvArmSensor2 = findViewById(R.id.arm_sensor2);
        tvArmSensor2.setText(curArmS.getSensorInfo2());

        TextView tvArmEquip = findViewById(R.id.arm_equip);
        tvArmEquip.setText(curArmS.getEquipmentInfo());

        etParaTime = findViewById(R.id.et_para_time);
        etParaTime.setText(curArmS.getOperatePara());

        btnMdy = findViewById(R.id.btn_modify);
        btnMdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doModifyArmStrategy();
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
                        getSetValueThresholdResult(tradeId, count);
                        return;
                    case 2:
                        if (bEndThread) {
                            return;
                        }
                        bundle = msg.getData();
                        tradeId = bundle.getString("tradeId");
                        count = bundle.getInt("count");
                        CheckResultThread tR = mRefreashThreads.get(tradeId);
                        handler.removeCallbacks(tR);
                        tR.interrupt();
                        tR = null;
                        mRefreashThreads.remove(tradeId);
                        getGetTimeThresholdResult(tradeId, count);
                        return;
                }
                super.handleMessage(msg);
            }
        };
    }

    private void doModifyArmStrategy() {
        String startTime = btnStartTime.getText().toString();
        if (TextUtils.isEmpty(startTime)) {
            DlgUtil.showMsgInfo(this, "请选择开始时间！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clickedShowStartTime();
                }
            });
            return;
        }

        String endTime = btnEndTime.getText().toString();
        if (TextUtils.isEmpty(endTime)) {
            DlgUtil.showMsgInfo(this, "请选择结束时间！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clickedShowEndTime();
                }
            });
            return;
        }

        if (!CheckInputUtil.checkTextViewInput(etSensorVal, this, "请输入传感器值！")) {
            return;
        }

        if (!CheckInputUtil.checkTextViewInput(etParaTime, this, "请输入时长！")) {
            return;
        }

        boolean bModified = false;
        do {
            if (curArmS.getIsUseBool() != status.isChecked()) {
                bModified = true;
                break;
            }
            if (!btnStartTime.getText().toString().equals(curArmS.getStartTimeString())) {
                bModified = true;
                break;
            }
            if (!btnEndTime.getText().toString().equals(curArmS.getEndTimeString())) {
                bModified = true;
                break;
            }
            if (!etSensorVal.getText().toString().equals(curArmS.getComparePara())) {
                bModified = true;
                break;
            }
            if (!etParaTime.getText().toString().equals(curArmS.getOperatePara())) {
                bModified = true;
                break;
            }
        } while(false);
        if (!bModified) {
            DlgUtil.showMsgInfo(this, "没有修改，无需保存");
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        final String tradeId = "5_" + System.currentTimeMillis();
        mp.put("tradeId", tradeId);
        mp.put("strategyId", curArmS.getArmStrategyId());
        if (status.isChecked()) {
            mp.put("isUse", "1");
        } else {
            mp.put("isUse", "0");
        }
        mp.put("startHour", Integer.valueOf(btnStartTime.getText().toString().substring(0, 2)).toString());
        mp.put("startMin", Integer.valueOf(btnStartTime.getText().toString().substring(3, 5)).toString());
        mp.put("endHour", Integer.valueOf(btnEndTime.getText().toString().substring(0, 2)).toString());
        mp.put("endMin", Integer.valueOf(btnEndTime.getText().toString().substring(3, 5)).toString());
        mp.put("compPara", etSensorVal.getText().toString());
        mp.put("operPara", etParaTime.getText().toString());
        mp.put("userId", MyApplication.getInstance().getMyUserId());

        btnMdy.setEnabled(false);

        AsyncSocketUtil.post(this, "autoctrl/setValueThreshold", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                startCheckSetValueThresholdThread(tradeId, 1);
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                btnMdy.setEnabled(true);
            }
        });
    }

    private void getSetValueThresholdResult(final String tradeId, final int nCount) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("tradeId", tradeId);
        mp.put("strategyId", curArmS.getArmStrategyId());
        if (status.isChecked()) {
            mp.put("isUse", "1");
        } else {
            mp.put("isUse", "0");
        }
        mp.put("startHour", Integer.valueOf(btnStartTime.getText().toString().substring(0, 2)).toString());
        mp.put("startMin", Integer.valueOf(btnStartTime.getText().toString().substring(3, 5)).toString());
        mp.put("endHour", Integer.valueOf(btnEndTime.getText().toString().substring(0, 2)).toString());
        mp.put("endMin", Integer.valueOf(btnEndTime.getText().toString().substring(3, 5)).toString());
        mp.put("compPara", etSensorVal.getText().toString());
        mp.put("operPara", etParaTime.getText().toString());

        AsyncSocketUtil.post(AcArmStrategyValueActivity.this, "autoctrl/checkSetValueThreshold", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject msgObj = response.getJSONObject(0);
                    String msg = msgObj.getString("message");
                    if (msg.equals("success")) {
                        String result = response.getJSONObject(1).getString("result");
                        if (result.equals("0") || result.equals("2")) {
                            if (nCount < 21) {
                                startCheckSetValueThresholdThread(tradeId, nCount + 1);
                            } else {
                                DlgUtil.showMsgInfo(AcArmStrategyValueActivity.this, "修改下位机策略失败：没有反应，请联系系统管理员！", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        btnMdy.setEnabled(true);
                                    }
                                });
                            }
                        } else if (result.equals("1")) {
                            DlgUtil.showMsgInfo(AcArmStrategyValueActivity.this, "修改下位机策略成功！", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    btnMdy.setEnabled(true);
                                }
                            });
                        } else {
                            DlgUtil.showMsgInfo(AcArmStrategyValueActivity.this, "修改下位机策略失败：" + response.getJSONObject(1).getString("remark"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    btnMdy.setEnabled(true);
                                }
                            });
                        }
                    } else {
                        DlgUtil.showMsgInfo(AcArmStrategyValueActivity.this, "修改下位机策略失败：" + msg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btnMdy.setEnabled(true);
                            }
                        });
                    }
                } catch (JSONException e) {
                    DlgUtil.showMsgInfo(AcArmStrategyValueActivity.this, "修改下位机策略失败：" + e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            btnMdy.setEnabled(true);
                        }
                    });
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                btnMdy.setEnabled(true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        bEndThread = true;

        super.onDestroy();

        handler.removeMessages(1);
        for (CheckResultThread t : mThreads.values()) {
            handler.removeCallbacks(t);
        }
        handler.removeMessages(2);
        for (CheckResultThread tR : mRefreashThreads.values()) {
            handler.removeCallbacks(tR);
        }
    }

    private void startCheckSetValueThresholdThread(String tradeId, int nCnt) {
        CheckResultThread thread = new CheckResultThread(this, handler, 1);
        thread.setData(tradeId, nCnt);
        mThreads.put(tradeId, thread);
        thread.start();
    }

    private void clickedShowStartTime() {
        try {
            DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(this, getStartTime());
            dateTimePicKDialog.timePicKDialog(btnStartTime);
        } catch (Exception e) {
            DlgUtil.showExceptionPrompt(this, e);
        }
    }

    private void clickedShowEndTime() {
        try {
            DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(this, getEndTime());
            dateTimePicKDialog.timePicKDialog(btnEndTime);
        } catch (Exception e) {
            DlgUtil.showExceptionPrompt(this, e);
        }
    }

    private String getStartTime() {
        String start = btnStartTime.getText().toString();
        if (start == null || start.length() == 0) {
            return "00:00";
        } else {
            return start;
        }
    }

    private String getEndTime() {
        String end = btnEndTime.getText().toString();
        if (end == null || end.length() == 0) {
            return "00:00";
        } else {
            return end;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_syc_time, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_sync_time:
                if (bSyncTime) {
                    DlgUtil.showMsgInfo(this, "正在获取下位机控制策略......");
                    break;
                }

                DlgUtil.showAsk(this, "确定要获取下位机控制策略吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        bSyncTime = true;
                        toGetTimeThreshold();
                    }
                });

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toGetTimeThreshold() {
        Map<String, String> mp = new LinkedHashMap<>();
        final String tradeId = "7_" + System.currentTimeMillis();
        mp.put("tradeId", tradeId);
        mp.put("strategyId", curArmS.getArmStrategyId());
        mp.put("userId", MyApplication.getInstance().getMyUserId());

        AsyncSocketUtil.post(this, "autoctrl/getValueThreshold", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                startCheckGetTimeThreshold(tradeId, 1);
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                bSyncTime = false;
            }
        });
    }

    private void startCheckGetTimeThreshold(String tradeId, int nCnt) {
        CheckResultThread thread = new CheckResultThread(this, handler, 2);
        thread.setData(tradeId, nCnt);
        mRefreashThreads.put(tradeId, thread);
        thread.start();
    }

    private void getGetTimeThresholdResult(final String tradeId, final int nCount) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("tradeId", tradeId);

        AsyncSocketUtil.post(AcArmStrategyValueActivity.this, "autoctrl/checkGetValueThreshold", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject msgObj = response.getJSONObject(0);
                    String msg = msgObj.getString("message");
                    if (msg.equals("success")) {
                        String result = response.getJSONObject(1).getString("result");
                        if (result.equals("0") || result.equals("2")) {
                            if (nCount < 21) {
                                startCheckGetTimeThreshold(tradeId, nCount + 1);
                            } else {
                                DlgUtil.showMsgInfo(AcArmStrategyValueActivity.this, "获取下位机控制策略失败：没有反应，请联系系统管理员！", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        bSyncTime = false;
                                    }
                                });
                            }
                        } else if (result.equals("1")) {
                            DlgUtil.showMsgInfo(AcArmStrategyValueActivity.this, "获取下位机控制策略成功！", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bSyncTime = false;
                                    finish();
                                }
                            });
                        } else {
                            DlgUtil.showMsgInfo(AcArmStrategyValueActivity.this, "获取下位机控制策略失败：" + response.getJSONObject(1).getString("remark"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bSyncTime = false;
                                }
                            });
                        }
                    } else {
                        DlgUtil.showMsgInfo(AcArmStrategyValueActivity.this, "获取下位机控制策略失败：" + msg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bSyncTime = false;
                            }
                        });
                    }
                } catch (JSONException e) {
                    DlgUtil.showMsgInfo(AcArmStrategyValueActivity.this, "获取下位机控制策略失败：" + e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bSyncTime = false;
                        }
                    });
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                bSyncTime = false;
            }
        });
    }
}