package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.TimeLimitInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.thread.CheckResultThread;
import com.xxs.igcsandroid.util.CommuCmdUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AcTimeLimitMgrActivity extends MainBaseActivity {
    private String mGhId;
    private String mGhName;

    private LinearLayout llRoot;

    private Handler handler;
    private Map<String, CheckResultThread> mThreads = new HashMap<>();
    private boolean bEndThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_time_limit_mgr);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mGhId = bundle.getString("ghId");
        mGhName = bundle.getString("ghName");

        setTitle(mGhName + "->时间限位管理");

        llRoot = findViewById(R.id.ll_timelimit_root);

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
                        if (tradeId.startsWith("8_")) {
                            getGetTimeLimitResult(tradeId, count);
                        } else if (tradeId.startsWith("9_")) {
                            getSetTimeLimitAResult(tradeId, count);
                        }
                        return;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTimeLimitInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bEndThread = true;

        handler.removeMessages(1);
        for (CheckResultThread t : mThreads.values()) {
            handler.removeCallbacks(t);
        }
    }

    public void getTimeLimitInfo() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(this, "gateway/getGhNodesTimeLimit", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    llRoot.removeAllViews();

                    for (int i = 1; i < response.length(); i += 2) {
                        View vNode = LayoutInflater.from(AcTimeLimitMgrActivity.this).inflate(R.layout.layout_one_node_head, null);
                        LinearLayout llOneNodeRoot = vNode.findViewById(R.id.ll_one_node_root);
                        TextView tvNode = vNode.findViewById(R.id.tv_node);

                        JSONObject nodeObj = response.getJSONObject(i);
                        final String gwId = nodeObj.getString("gwId");
                        final String nodeId = nodeObj.getString("NodeId");
                        String nodeName = nodeObj.getString("NodeName");
                        tvNode.setText(nodeName + "(网关" + gwId + " 节点" + nodeId + ")");

                        JSONArray jAOne = response.getJSONArray(i + 1);
                        for (int j = 0; j < jAOne.length(); j += 1) {
                            View vTlRow = LayoutInflater.from(AcTimeLimitMgrActivity.this).inflate(R.layout.layout_equipment_row, null);
                            LinearLayout llSensorRow = vTlRow.findViewById(R.id.ll_equipment_row);

                            JSONObject jOSensor = jAOne.getJSONObject(j);
                            TimeLimitInfo entity = new TimeLimitInfo(jOSensor, gwId, nodeId, nodeName);
                            View ll_sensor = getViewOfTl(entity);
                            if (ll_sensor != null) {
                                llSensorRow.addView(ll_sensor, 0);
                            }

                            llOneNodeRoot.addView(vTlRow);
                        }

                        llRoot.addView(llOneNodeRoot);
                    }
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(AcTimeLimitMgrActivity.this, e);
                }
            }
        }, null);
    }

    private View getViewOfTl(final TimeLimitInfo entity) {
        View convertView = LayoutInflater.from(AcTimeLimitMgrActivity.this).inflate(R.layout.list_item_time_limit, null);
        TextView tvTlName = convertView.findViewById(R.id.tv_lt_name);
        TextView tvTlContent = convertView.findViewById(R.id.tv_lt_content);
        tvTlName.setText(entity.getEquipNameString());
        tvTlContent.setText(entity.getContentString());

        convertView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, Menu.NONE, "自动时间限位设置")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                toSetTimeLimitA(entity);
                                return true;
                            }
                        });
                menu.add(0, 1, Menu.NONE, "手动时间限位设置")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                toSetTimeLimitM(entity);
                                return true;
                            }
                        });
                menu.add(0, 2, Menu.NONE, "获取下位机时间限位")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                toGetTimeLimit(entity);
                                return true;
                            }
                        });

            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.showContextMenu();
            }
        });

        return convertView;
    }

    private void toSetTimeLimitM(TimeLimitInfo entity) {
        Intent intent = new Intent(AcTimeLimitMgrActivity.this, AcTimeLimitActivity.class);
        intent.putExtra("gwId", entity.getGwId());
        intent.putExtra("nodeId", entity.getNodeId());
        intent.putExtra("nodeName", entity.getNodeName());
        intent.putExtra("equiId", entity.getEquiId());
        intent.putExtra("equiTypeString", entity.getEquiType());
        intent.putExtra("equiName", entity.getEquiName());
        intent.putExtra("locate", entity.getLocate());
        intent.putExtra("timeUp", entity.getTimeUp());
        intent.putExtra("timeDown", entity.getTimeDown());
        intent.putExtra("timeError", entity.getTimeError());
        startActivity(intent);
    }

    private void toSetTimeLimitA(final TimeLimitInfo entity) {
        DlgUtil.showAsk(AcTimeLimitMgrActivity.this, "确定要进行自动时间限位设置吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                gotoAddTrade(entity, "9_", "SetTimeLimitA");
            }
        });
    }

    private void toGetTimeLimit(final TimeLimitInfo entity) {
        DlgUtil.showAsk(AcTimeLimitMgrActivity.this, "确定要获取下位机时间限位信息吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                gotoAddTrade(entity, "8_", "GetTimeLimit");
            }
        });
    }

    private void gotoAddTrade(TimeLimitInfo entity, String preFix, String controType) {
        Map<String, String> mp = new LinkedHashMap<>();
        final String tradeId = preFix + System.currentTimeMillis();
        mp.put("tradeId", tradeId);
        mp.put("gwId", entity.getGwId());
        mp.put("nodeId", entity.getNodeId());
        mp.put("equiType", entity.getEquiType());
        mp.put("equiId", entity.getEquiId());
        mp.put("userId", MyApplication.getInstance().getMyUserId());
        mp.put("controlType", controType);

        AsyncSocketUtil.post(AcTimeLimitMgrActivity.this, "autoctrl/addTrade", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                startCheck(tradeId, 1);
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {

            }
        });
    }

    private void startCheck(String tradeId, int nCnt) {
        CheckResultThread thread = new CheckResultThread(AcTimeLimitMgrActivity.this, handler, 1);
        thread.setData(tradeId, nCnt);
        mThreads.put(tradeId, thread);
        thread.start();
    }

    private void getSetTimeLimitAResult(final String tradeId, final int count) {
        CommuCmdUtil.getTradeResult(AcTimeLimitMgrActivity.this, tradeId, count, new CommuCmdUtil.OnGetTradeResult() {
            @Override
            public void OnSuccess(String result) {
                if (result.equals("H")) {
                    DlgUtil.showMsgInfo(AcTimeLimitMgrActivity.this, "已启动下位机自动时间限位设置，完成后将以信息告警的方式通知您！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                } else {
                    DlgUtil.showMsgInfo(AcTimeLimitMgrActivity.this, "进行自动时间限位设置成功！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getTimeLimitInfo();
                        }
                    });
                }
            }

            @Override
            public void OnFail(String errMsg) {
                DlgUtil.showMsgInfo(AcTimeLimitMgrActivity.this, "进行自动时间限位设置失败：" + errMsg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }

            @Override
            public void OnContinue() {
                startCheck(tradeId, count + 1);
            }
        });
    }

    private void getGetTimeLimitResult(final String tradeId, final int count) {
        CommuCmdUtil.getTradeResult(AcTimeLimitMgrActivity.this, tradeId, count, new CommuCmdUtil.OnGetTradeResult() {
            @Override
            public void OnSuccess(String result) {
                DlgUtil.showMsgInfo(AcTimeLimitMgrActivity.this, "获取下位机时间限位成功！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getTimeLimitInfo();
                    }
                });
            }

            @Override
            public void OnFail(String errMsg) {
                DlgUtil.showMsgInfo(AcTimeLimitMgrActivity.this, "获取下位机时间限位失败：" + errMsg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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