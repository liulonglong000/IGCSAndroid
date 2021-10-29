package com.xxs.igcsandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.image.SmartImageView;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.thread.CheckResultThread;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.HandlePic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class NodeInfoActivity extends AppCompatActivity {
    private Handler handler;        // 简单理解，Handler就是解决线程和线程之间的通信的
    private Map<String, CheckResultThread> mThreads = new HashMap<>();
    private Map<String, CheckResultThread> mRefreashThreads = new HashMap<>();
    private boolean bEndThread = false;
    private Button btnMdy;
    private boolean bSyncTime = false;

    private String nodeId;
    private String mGwId;
    private EditText etName;
    private EditText etAdress;
    private EditText etRemark;
    private EditText etFrquency;
    private HandlePic hdlPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_info);

        setTitle("修改节点信息");

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        nodeId = bundle.getString("nodeId");
        mGwId = bundle.getString("gwId");

        etName = findViewById(R.id.et_node_name);
        etName.setText(bundle.getString("nodeName"));

        etAdress = findViewById(R.id.et_address);
        etAdress.setText(bundle.getString("addr"));

        etRemark = findViewById(R.id.et_remark);
        etRemark.setText(bundle.getString("remark"));

        etFrquency = findViewById(R.id.et_frquency);
        etFrquency.setText(bundle.getString("frequency"));

        hdlPic = new HandlePic(this);
        hdlPic.setImageView((SmartImageView) findViewById(R.id.iv_image));
        hdlPic.handleSelPic((Button) findViewById(R.id.btn_sel_pic));
        hdlPic.handleShotPic((Button) findViewById(R.id.btn_shot_pic));
        hdlPic.handleDelPic((Button) findViewById(R.id.btn_del_pic));
        hdlPic.setInfoByPic(bundle.getString("pic"));

        btnMdy = findViewById(R.id.btn_add);
        btnMdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMdy.setEnabled(false);
                gotoMdyNodeInfo();
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
                        getMdyFrqResult(tradeId, count);
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
                        getSyncTimeResult(tradeId, count);
                        return;
                }
                super.handleMessage(msg);
            }
        };
    }

    private void getSyncTimeResult(final String tradeId, final int nCount) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("tradeId", tradeId);

        AsyncSocketUtil.post(NodeInfoActivity.this, "operation/checkSyncNodeTimeResult", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject msgObj = response.getJSONObject(0);
                    String msg = msgObj.getString("message");
                    if (msg.equals("success")) {
                        String result = response.getJSONObject(1).getString("result");
                        if (result.equals("0") || result.equals("2")) {
                            if (nCount < 21) {
                                startCheckSyncTimeThread(tradeId, nCount + 1);
                            } else {
                                DlgUtil.showMsgInfo(NodeInfoActivity.this, "同步节点时间失败：没有反应，请联系系统管理员！", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        bSyncTime = false;
                                    }
                                });
                            }
                        } else if (result.equals("1")) {
                            DlgUtil.showMsgInfo(NodeInfoActivity.this, "同步节点时间成功！", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bSyncTime = false;
                                }
                            });
                        } else {
                            DlgUtil.showMsgInfo(NodeInfoActivity.this, "同步节点时间失败：" + response.getJSONObject(1).getString("remark"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bSyncTime = false;
                                }
                            });
                        }
                    } else {
                        DlgUtil.showMsgInfo(NodeInfoActivity.this, "同步节点时间失败：" + msg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bSyncTime = false;
                            }
                        });
                    }
                } catch (JSONException e) {
                    DlgUtil.showMsgInfo(NodeInfoActivity.this, "同步节点时间失败：" + e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
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

    private void getMdyFrqResult(final String tradeId, final int nCount) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("tradeId", tradeId);

        AsyncSocketUtil.post(NodeInfoActivity.this, "operation/checkMdyNodeFrqResult", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject msgObj = response.getJSONObject(0);
                    String msg = msgObj.getString("message");
                    if (msg.equals("success")) {
                        String result = response.getJSONObject(1).getString("result");
                        if (result.equals("0") || result.equals("2")) {
                            if (nCount < 21) {
                                startCheckMdyFrqThread(tradeId, nCount + 1);
                            } else {
                                DlgUtil.showMsgInfo(NodeInfoActivity.this, "修改节点采集频率失败：没有反应，请联系系统管理员！", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        btnMdy.setEnabled(true);
                                    }
                                });
                            }
                        } else if (result.equals("1")) {
                            nodeMdyComplete();
                        } else {
                            DlgUtil.showMsgInfo(NodeInfoActivity.this, "修改节点采集频率失败：" + response.getJSONObject(1).getString("remark"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    btnMdy.setEnabled(true);
                                }
                            });
                        }
                    } else {
                        DlgUtil.showMsgInfo(NodeInfoActivity.this, "修改节点采集频率失败：" + msg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btnMdy.setEnabled(true);
                            }
                        });
                    }
                } catch (JSONException e) {
                    DlgUtil.showMsgInfo(NodeInfoActivity.this, "修改节点采集频率失败：" + e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
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

    private void gotoMdyNodeInfo() {
        if (!CheckInputUtil.checkTextViewInput(etFrquency, this, "请输入采集频率！")) {
            return;
        }

        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("nodeId", nodeId);
        mp.put("gwId", mGwId);
        mp.put("nodeName", etName.getText().toString().trim());
        mp.put("nodeAddr", etAdress.getText().toString().trim());
        mp.put("nodeRemark", etRemark.getText().toString().trim());
        mp.put("frquency", etFrquency.getText().toString().trim());
        mp.put("picSrcFile", hdlPic.getPicSrcFile());
        hdlPic.getFileToUpdate(nodeId + System.currentTimeMillis());

        AsyncSocketUtil.postWithFile(this, "gateway/mdyNodeInfo", mp, "picSel", hdlPic.getFileSel(), null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    boolean bFrqMdy = response.getJSONObject(1).getBoolean("frqMdy");
                    if (bFrqMdy) {
                        toMdyNodeFrq(etFrquency.getText().toString().trim());
                    } else {
                        nodeMdyComplete();
                    }
                } catch (Exception e) {
                    DlgUtil.showMsgError(NodeInfoActivity.this, e.getLocalizedMessage());
                    btnMdy.setEnabled(true);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                btnMdy.setEnabled(true);
            }
        });
    }

    private void toMdyNodeFrq(final String frqPara) {
        Map<String, String> mp = new LinkedHashMap<>();
        final String tradeId = "2_" + System.currentTimeMillis();
        mp.put("tradeId", tradeId);
        mp.put("gwId", mGwId);
        mp.put("nodeId", nodeId);
        mp.put("frqPara", frqPara);
        mp.put("userId", MyApplication.getInstance().getMyUserId());

        AsyncSocketUtil.post(this, "operation/addMdyNodeFrq", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                startCheckMdyFrqThread(tradeId, 1);
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                btnMdy.setEnabled(true);
            }
        });
    }

    private void startCheckMdyFrqThread(String tradeId, int nCnt) {
        CheckResultThread thread = new CheckResultThread(this, handler, 1);
        thread.setData(tradeId, nCnt);
        mThreads.put(tradeId, thread);
        thread.start();
    }

    void nodeMdyComplete() {
        DlgUtil.showMsgInfo(NodeInfoActivity.this, "修改节点信息成功！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                NodeInfoActivity.this.finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        bEndThread = true;

        hdlPic.resetFileSel();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            hdlPic.onActivityResult(requestCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
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
                    DlgUtil.showMsgInfo(this, "正在同步节点时间......");
                    break;
                }

                DlgUtil.showAsk(this, "确定要同步节点时间吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        bSyncTime = true;
                        toSyncNodeTime();
                    }
                });

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toSyncNodeTime() {
        Map<String, String> mp = new LinkedHashMap<>();
        final String tradeId = "3_" + System.currentTimeMillis();
        mp.put("tradeId", tradeId);
        mp.put("gwId", mGwId);
        mp.put("nodeId", nodeId);
        mp.put("userId", MyApplication.getInstance().getMyUserId());

        AsyncSocketUtil.post(this, "operation/syncNodeTime", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                startCheckSyncTimeThread(tradeId, 1);
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                bSyncTime = false;
            }
        });
    }

    private void startCheckSyncTimeThread(String tradeId, int nCnt) {
        CheckResultThread thread = new CheckResultThread(this, handler, 2);
        thread.setData(tradeId, nCnt);
        mRefreashThreads.put(tradeId, thread);
        thread.start();
    }
}
