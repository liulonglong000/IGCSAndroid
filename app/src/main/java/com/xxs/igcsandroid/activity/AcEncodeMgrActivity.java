package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.EncodeInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.thread.CheckResultThread;
import com.xxs.igcsandroid.util.CommuCmdUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AcEncodeMgrActivity extends AppCompatActivity {
    private String mGhId;
    private String mGhName;

    private LinearLayout llRoot;

    private Handler handler;
    private Map<String, CheckResultThread> mThreads = new HashMap<>();
    private boolean bEndThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_encode_mgr);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mGhId = bundle.getString("ghId");
        mGhName = bundle.getString("ghName");

        setTitle(mGhName + "->编码器设置");

        llRoot = findViewById(R.id.ll_encode_root);

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
                        if (tradeId.startsWith("c_")) {
                            getGetEncoderResult(tradeId, count);
                        } else if (tradeId.startsWith("b_")) {
                            getSetEncoderAResult(tradeId, count);
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
        getEncodeInfo();
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

    public void getEncodeInfo() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(this, "gateway/getGhNodesEncode", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    llRoot.removeAllViews();

                    for (int i = 1; i < response.length(); i += 2) {
                        View vNode = LayoutInflater.from(AcEncodeMgrActivity.this).inflate(R.layout.layout_one_node_head, null);
                        LinearLayout llOneNodeRoot = vNode.findViewById(R.id.ll_one_node_root);
                        TextView tvNode = vNode.findViewById(R.id.tv_node);

                        JSONObject nodeObj = response.getJSONObject(i);
                        final String gwId = nodeObj.getString("gwId");
                        final String nodeId = nodeObj.getString("NodeId");
                        String nodeName = nodeObj.getString("NodeName");
                        tvNode.setText(nodeName + "(网关" + gwId + " 节点" + nodeId + ")");

                        JSONArray jAOne = response.getJSONArray(i + 1);
                        for (int j = 0; j < jAOne.length(); j += 1) {
                            View vTlRow = LayoutInflater.from(AcEncodeMgrActivity.this).inflate(R.layout.layout_equipment_row, null);
                            LinearLayout llSensorRow = vTlRow.findViewById(R.id.ll_equipment_row);

                            JSONObject jOSensor = jAOne.getJSONObject(j);
                            EncodeInfo entity = new EncodeInfo(jOSensor, gwId, nodeId, nodeName);
                            View ll_sensor = getViewOfEncode(entity);
                            if (ll_sensor != null) {
                                llSensorRow.addView(ll_sensor, 0);
                            }

                            llOneNodeRoot.addView(vTlRow);
                        }

                        llRoot.addView(llOneNodeRoot);
                    }
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(AcEncodeMgrActivity.this, e);
                }
            }
        }, null);
    }

    private View getViewOfEncode(final EncodeInfo entity) {
        View convertView = LayoutInflater.from(AcEncodeMgrActivity.this).inflate(R.layout.list_item_time_limit, null);
        TextView tvTlName = convertView.findViewById(R.id.tv_lt_name);
        TextView tvTlContent = convertView.findViewById(R.id.tv_lt_content);
        tvTlName.setText(entity.getEquipNameString());
        tvTlContent.setText(entity.getContentString());

        convertView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, Menu.NONE, "下位机自行测试")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                toSetEncodeA(entity);
                                return true;
                            }
                        });
                menu.add(0, 1, Menu.NONE, "设置编码器参数")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                toSetEncoderM(entity);
                                return true;
                            }
                        });
                menu.add(0, 2, Menu.NONE, "获取编码器参数")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                toGetEncoder(entity);
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

    private void toSetEncoderM(EncodeInfo entity) {
        Intent intent = new Intent(AcEncodeMgrActivity.this, AcEncodeActivity.class);
        intent.putExtra("gwId", entity.getGwId());
        intent.putExtra("nodeId", entity.getNodeId());
        intent.putExtra("nodeName", entity.getNodeName());
        intent.putExtra("equiId", entity.getEquiId());
        intent.putExtra("equiTypeString", entity.getEquiType());
        intent.putExtra("equiName", entity.getEquiName());
        intent.putExtra("curQs", entity.getCurQs());
        intent.putExtra("totalQs", entity.getTotalQs());
        startActivity(intent);
    }

    private void toSetEncodeA(final EncodeInfo entity) {
        DlgUtil.showAsk(AcEncodeMgrActivity.this, "确定要进行下位机自行测试吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                gotoAddTrade(entity, "b_", "SetEncoderA");
            }
        });
    }

    private void toGetEncoder(final EncodeInfo entity) {
        DlgUtil.showAsk(AcEncodeMgrActivity.this, "确定要获取编码器参数吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                gotoAddTrade(entity, "c_", "GetEncoder");
            }
        });
    }

    private void gotoAddTrade(EncodeInfo entity, String preFix, String controType) {
        Map<String, String> mp = new LinkedHashMap<>();
        final String tradeId = preFix + System.currentTimeMillis();
        mp.put("tradeId", tradeId);
        mp.put("gwId", entity.getGwId());
        mp.put("nodeId", entity.getNodeId());
        mp.put("equiType", entity.getEquiType());
        mp.put("equiId", entity.getEquiId());
        mp.put("userId", MyApplication.getInstance().getMyUserId());
        mp.put("controlType", controType);

        AsyncSocketUtil.post(AcEncodeMgrActivity.this, "autoctrl/addTrade", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
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
        CheckResultThread thread = new CheckResultThread(AcEncodeMgrActivity.this, handler, 1);
        thread.setData(tradeId, nCnt);
        mThreads.put(tradeId, thread);
        thread.start();
    }

    private void getSetEncoderAResult(final String tradeId, final int count) {
        CommuCmdUtil.getTradeResult(AcEncodeMgrActivity.this, tradeId, count, new CommuCmdUtil.OnGetTradeResult() {
            @Override
            public void OnSuccess(String result) {
                if (result.equals("H")) {
                    DlgUtil.showMsgInfo(AcEncodeMgrActivity.this, "已启动下位机自行测试编码器，完成后将以信息告警的方式通知您！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                } else {
                    DlgUtil.showMsgInfo(AcEncodeMgrActivity.this, "进行下位机自行测试编码器成功！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getEncodeInfo();
                        }
                    });
                }
            }

            @Override
            public void OnFail(String errMsg) {
                DlgUtil.showMsgInfo(AcEncodeMgrActivity.this, "进行下位机自行测试编码器失败：" + errMsg, new DialogInterface.OnClickListener() {
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

    private void getGetEncoderResult(final String tradeId, final int count) {
        CommuCmdUtil.getTradeResult(AcEncodeMgrActivity.this, tradeId, count, new CommuCmdUtil.OnGetTradeResult() {
            @Override
            public void OnSuccess(String result) {
                DlgUtil.showMsgInfo(AcEncodeMgrActivity.this, "获取编码器参数成功！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getEncodeInfo();
                    }
                });
            }

            @Override
            public void OnFail(String errMsg) {
                DlgUtil.showMsgInfo(AcEncodeMgrActivity.this, "获取编码器参数失败：" + errMsg, new DialogInterface.OnClickListener() {
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