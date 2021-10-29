package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jaygoo.selector.MultiData;
import com.jaygoo.selector.SignalSelectPopWindow;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DateTimePickDialogUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AcStrategyInfoActivity extends AppCompatActivity {
    private String mGhId;
    private String mGhName;
    private String mStrategyId;

    private Short type = 0;     // 1:添加；2:修改

    private TextView tvTg1;
    private TextView tvTg2;
    private TextView tvTg3;
    private TextView tvOg;
    private TextView btnStartTime;
    private TextView btnEndTime;
    private Button btnCommit;

    private List<MultiData> lstTgs;
    private List<MultiData> lstOgs;
    private String mSelTg1;
    private String mSelTg2;
    private String mSelTg3;
    private String mSelOg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_strategy_info);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mGhId = bundle.getString("ghId");
        mGhName = bundle.getString("ghName");
        mStrategyId = bundle.getString("strategyId");
        if (StringUtil.isStringNullOrEmpty(mStrategyId)) {
            setTitle(mGhName + "->添加策略");
            type = 1;
        } else {
            setTitle(mGhName + "->修改策略");
            type = 2;
        }

        tvTg1 = findViewById(R.id.sel_tg1);
        tvTg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignalSelectPopWindow.Builder(AcStrategyInfoActivity.this)
                        .setDataArray(lstTgs)
                        .setConfirmListener(new SignalSelectPopWindow.OnConfirmClickListener() {
                            @Override
                            public void onClick(Integer indexSel) {
                                tvTg1.setText(lstTgs.get(indexSel).getText());
                                mSelTg1 = lstTgs.get(indexSel).getId();

//                                for (int i = 0; i < lstTgs.size(); i++) {
//                                    lstTgs.get(i).setbSelect(false);
//                                }
//                                lstTgs.get(indexSel).setbSelect(true);
                            }
                        })
                        .setTitle("阈值组列表")
                        .build()
                        .show(findViewById(R.id.mBottom));
            }
        });
        tvTg2 = findViewById(R.id.sel_tg2);
        tvTg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignalSelectPopWindow.Builder(AcStrategyInfoActivity.this)
                        .setDataArray(lstTgs)
                        .setConfirmListener(new SignalSelectPopWindow.OnConfirmClickListener() {
                            @Override
                            public void onClick(Integer indexSel) {
                                tvTg2.setText(lstTgs.get(indexSel).getText());
                                mSelTg2 = lstTgs.get(indexSel).getId();

//                                for (int i = 0; i < lstTgs.size(); i++) {
//                                    lstTgs.get(i).setbSelect(false);
//                                }
//                                lstTgs.get(indexSel).setbSelect(true);
                            }
                        })
                        .setTitle("阈值组列表")
                        .build()
                        .show(findViewById(R.id.mBottom));
            }
        });
        tvTg3 = findViewById(R.id.sel_tg3);
        tvTg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignalSelectPopWindow.Builder(AcStrategyInfoActivity.this)
                        .setDataArray(lstTgs)
                        .setConfirmListener(new SignalSelectPopWindow.OnConfirmClickListener() {
                            @Override
                            public void onClick(Integer indexSel) {
                                tvTg3.setText(lstTgs.get(indexSel).getText());
                                mSelTg3 = lstTgs.get(indexSel).getId();

//                                for (int i = 0; i < lstTgs.size(); i++) {
//                                    lstTgs.get(i).setbSelect(false);
//                                }
//                                lstTgs.get(indexSel).setbSelect(true);
                            }
                        })
                        .setTitle("阈值组列表")
                        .build()
                        .show(findViewById(R.id.mBottom));
            }
        });
        tvOg = findViewById(R.id.sel_og);
        tvOg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignalSelectPopWindow.Builder(AcStrategyInfoActivity.this)
                        .setDataArray(lstOgs)
                        .setConfirmListener(new SignalSelectPopWindow.OnConfirmClickListener() {
                            @Override
                            public void onClick(Integer indexSel) {
                                tvOg.setText(lstOgs.get(indexSel).getText());
                                mSelOg = lstOgs.get(indexSel).getId();

                                for (int i = 0; i < lstOgs.size(); i++) {
                                    lstOgs.get(i).setbSelect(false);
                                }
                                lstOgs.get(indexSel).setbSelect(true);
                            }
                        })
                        .setTitle("操作组列表")
                        .build()
                        .show(findViewById(R.id.mBottom));
            }
        });

        btnStartTime = findViewById(R.id.start_time);
        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedShowStartTime();
            }
        });
        btnEndTime = findViewById(R.id.end_time);
        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedShowEndTime();
            }
        });

        btnCommit = findViewById(R.id.btn_add);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    return;
                }
                gotoAddStrategyInfo();
            }
        });

        if (type == 2) {
            mSelTg1 = bundle.getString("tgId1");
            tvTg1.setText(bundle.getString("tgName1"));
            if (bundle.getString("tgId2") != null) {
                mSelTg2 = bundle.getString("tgId2");
                tvTg2.setText(bundle.getString("tgName2"));
            }
            if (bundle.getString("tgId3") != null) {
                mSelTg3 = bundle.getString("tgId3");
                tvTg3.setText(bundle.getString("tgName3"));
            }
            mSelOg = bundle.getString("ogId");
            tvOg.setText(bundle.getString("ogName"));

            btnStartTime.setText(bundle.getString("start"));
            btnEndTime.setText(bundle.getString("end"));

            btnCommit.setText("修  改");
        }

        getDatasFromSrv();
    }

    private void gotoAddStrategyInfo() {
        if (mSelTg1 == null) {
            DlgUtil.showMsgInfo(this, "请选择阈值组1！");
            tvTg1.requestFocus();
            return;
        }
        if (mSelTg2 == null && mSelTg3 != null) {
            DlgUtil.showMsgInfo(this, "请选择阈值组2！");
            tvTg2.requestFocus();
            return;
        }
        if (!CheckInputUtil.checkTextViewInput(tvOg, this, "请选择操作组！")) {
            return;
        }

        String startTime = btnStartTime.getText().toString();
        if (TextUtils.isEmpty(startTime)) {
            DlgUtil.showMsgInfo(this, "请设置时间段！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clickedShowStartTime();
                }
            });
            return;
        }
        String endTime = btnEndTime.getText().toString();
        if (TextUtils.isEmpty(endTime)) {
            DlgUtil.showMsgInfo(this, "请设置时间段！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clickedShowEndTime();
                }
            });
            return;
        }
        if (startTime.compareTo(endTime) == 0) {
            DlgUtil.showMsgInfo(this, "请设置时间段！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clickedShowEndTime();
                }
            });
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("tgId1", mSelTg1);
        mp.put("tgId2", mSelTg2);
        mp.put("tgId3", mSelTg3);
        mp.put("ogId", mSelOg);
        mp.put("start", startTime);
        mp.put("end", endTime);
        mp.put("userId", MyApplication.getInstance().getMyUserId());

        String url = "autoctrl/addStrategyInfo";
        if (type == 1) {
            mStrategyId = "" + System.currentTimeMillis();
            mp.put("strategyId", mStrategyId);
            mp.put("ghId", mGhId);
        } else if (type == 2) {
            url = "autoctrl/mdyStrategyInfo";
            mp.put("strategyId", mStrategyId);
        }

        AsyncSocketUtil.post(this, url, mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                String str = "添加策略信息成功！";
                if (type == 2) {
                    str = "修改策略信息成功！";
                }
                DlgUtil.showMsgInfo(AcStrategyInfoActivity.this, str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        AcStrategyInfoActivity.this.finish();
                    }
                });
            }
        }, null);
    }

    private void getDatasFromSrv() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(this, "autoctrl/getDatasForStrategyAdd", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONArray arrayTg = response.getJSONArray(1);
                    lstTgs = new ArrayList<>();
                    for (int i = 0; i < arrayTg.length(); i++) {
                        JSONObject obj = arrayTg.getJSONObject(i);
                        String tgId = obj.getString("tgId");
                        String tgName = obj.getString("tgName");
                        lstTgs.add(new MultiData(tgId, tgName, false));
                    }

                    JSONArray arrayOg = response.getJSONArray(2);
                    lstOgs = new ArrayList<>();
                    for (int i = 0; i < arrayOg.length(); i++) {
                        JSONObject obj = arrayOg.getJSONObject(i);
                        String ogId = obj.getString("ogId");
                        String ogName = obj.getString("ogName");
                        lstOgs.add(new MultiData(ogId, ogName, false));
                    }
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(AcStrategyInfoActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                AcStrategyInfoActivity.this.finish();
            }
        });
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
}
