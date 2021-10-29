package com.xxs.igcsandroid.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.EquipmentInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class NodeControlFragment extends BaseFragment {
    private Handler handler;        // 简单理解，Handler就是解决线程和线程之间的通信的
    private Map<String, CheckResultThread> mThreads = new HashMap<>();
    private Map<String, RefreshResultThread> mRefreashThreads = new HashMap<>();
    private boolean bEndThread = false;

    private String mGhId;
    private String mUserId;

    private LinearLayout llRoot;

    public NodeControlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle data = getArguments();
        mGhId = data.getString("ghId");

        mUserId = MyApplication.getInstance().getMyUserId();

        View view = inflater.inflate(R.layout.fragment_node_control, container, false);

        llRoot = view.findViewById(R.id.linerlayout_equi_root);

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
                        getTradeResult(tradeId, (EquipmentInfo) msg.obj, count);
                        return;
                    case 2:
                        if (bEndThread) {
                            return;
                        }
                        bundle = msg.getData();
                        tradeId = bundle.getString("tradeId");
                        count = bundle.getInt("count");
                        RefreshResultThread tR = mRefreashThreads.get(tradeId);
                        handler.removeCallbacks(tR);
                        tR.interrupt();
                        tR = null;
                        mRefreashThreads.remove(tradeId);
                        getRefreashResult(tradeId, (Button) msg.obj, count);
                        return;
                }
                super.handleMessage(msg);
            }
        };

        return view;
    }

    @Override
    public void onResume() {
        bEndThread = true;
        super.onResume();
        getEquipmentInfo();
    }

    @Override
    public void onStop() {
        bEndThread = true;
        super.onStop();
    }

    @Override
    public void onPause() {
        bEndThread = true;
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeMessages(1);
        for (CheckResultThread t : mThreads.values()) {
            handler.removeCallbacks(t);
        }
        handler.removeMessages(2);
        for (RefreshResultThread tR : mRefreashThreads.values()) {
            handler.removeCallbacks(tR);
        }
    }

    private void getEquipmentInfo() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(mActivity, "gateway/getGhNodesEquipment", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    llRoot.removeAllViews();

                    for (int i = 1; i < response.length(); i += 2) {
                        View vNode = LayoutInflater.from(mActivity).inflate(R.layout.layout_equipment_node, null);
                        LinearLayout llOneNdeRoot = vNode.findViewById(R.id.ll_one_node_root);

                        TextView tvNode = vNode.findViewById(R.id.tv_node);
                        JSONObject nodeObj = response.getJSONObject(i);
                        final String gwId = nodeObj.getString("gwId");
                        final String nodeId = nodeObj.getString("NodeId");
                        String nodeName = nodeObj.getString("NodeName");
                        tvNode.setText(nodeName + "(网关" + gwId + " 节点" + nodeId + ")");

                        final Button btnRefresh = vNode.findViewById(R.id.btn_refresh);
                        btnRefresh.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnRefresh.setEnabled(false);
                                toRefreshNodeEquips(btnRefresh, gwId, nodeId);
                            }
                        });

                        JSONArray jAOne = response.getJSONArray(i + 1);
                        for (int j = 0; j < jAOne.length(); j += 1) {
                            View vSensorRow = LayoutInflater.from(mActivity).inflate(R.layout.layout_equipment_row, null);
                            LinearLayout llSensorRow = vSensorRow.findViewById(R.id.ll_equipment_row);

                            JSONObject jOSensor = jAOne.getJSONObject(j);
                            EquipmentInfo equipInfo = new EquipmentInfo(jOSensor, gwId, nodeId, nodeName, mActivity);
                            View ll_sensor = getViewOfEquip(equipInfo);
                            if (ll_sensor != null) {
                                llSensorRow.addView(ll_sensor, 0);
                            }

                            llOneNdeRoot.addView(vSensorRow);
                        }

                        llRoot.addView(llOneNdeRoot);
                    }

                    bEndThread = false;
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }

    private void toRefreshNodeEquips(final Button btnRefresh, String gwId, final String nodeId) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("gwId", gwId);
        mp.put("nodeId", nodeId);
        mp.put("userId", mUserId);

        AsyncSocketUtil.post(mActivity, "operation/refreshNodeEquips", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    String trades = "";
                    for (int i= 1; i < response.length(); i++) {
                        String tId = response.getJSONObject(i).getString("tradeId");
                        if (trades.length() > 0) {
                            trades += "+";
                        }
                        trades += tId;
                    }
                    if (trades.length() > 0) {
                        startRefreshThread(trades, btnRefresh, 1);
                    } else {
                        btnRefresh.setEnabled(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    DlgUtil.showMsgError(mActivity, e.getLocalizedMessage());
                    btnRefresh.setEnabled(true);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                btnRefresh.setEnabled(true);
            }
        });
    }

//    private View getViewOfEquipment(JSONObject jObj, String gwId, String nodeId, String nodeName) throws Exception {
//        EquipmentInfo equipInfo = new EquipmentInfo(jObj, gwId, nodeId, nodeName, mActivity);
//
//        return getViewOfEquip(equipInfo);
//    }

    private View getViewOfEquip(final EquipmentInfo equipInfo) {
        View llEquip = LayoutInflater.from(mActivity).inflate(R.layout.layout_equipment_one, null);
        equipInfo.setLlEquip(llEquip);

        equipInfo.refreshUI();

        llEquip.findViewById(R.id.ll_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> mp = new LinkedHashMap<>();
                mp.put("gwId", equipInfo.getGwId());
                mp.put("nodeId", equipInfo.getNodeId());
                mp.put("equipType", equipInfo.getEquiType());
                mp.put("equipId", equipInfo.getEquiId());

                AsyncSocketUtil.post(mActivity, "gateway/getEquipmentInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
                    @Override
                    public void OnJSONArrayResult(JSONArray response) {
                        try {
                            JSONObject jOEquip = response.getJSONObject(1);
                            equipInfo.updateStatusInfo(jOEquip);
                            equipInfo.refreshUIStatus();

                            if (equipInfo.showOperationDlg(new EquipmentInfo.onOKClicked() {
                                @Override
                                public void toAddTrade(final String tradeId, Map<String, String> mpTradeInfo) {
                                    mpTradeInfo.put("userId", mUserId);

                                    equipInfo.diasableUI();

                                    AsyncSocketUtil.post(mActivity, "operation/addTradeInfo", mpTradeInfo, null, new AsyncSocketUtil.onSuccessJSONArray() {
                                        @Override
                                        public void OnJSONArrayResult(JSONArray response) {
                                            startCheckThread(tradeId, equipInfo, 1);
                                        }
                                    }, null);
                                }
                            }) == null) {
                                DlgUtil.showMsgInfo(mActivity, "对该设备的操作不支持，请更新程序版本！");
                            }

                        } catch (Exception e) {
                            DlgUtil.showExceptionPrompt(mActivity, e);
                        }
                    }
                }, null);
            }
        });

        return llEquip;
    }

//    private View getViewOfRbm(final String equiName, String equiStatus, final String gwId, final String nodeId, final String equiType,
//                              final String equiId, final Integer equiStatusPara, final Integer equiPara, int imgId) {
//        final View llEquip = LayoutInflater.from(mActivity).inflate(R.layout.layout_equipment_rbm, null);
//        ImageView ivImg = llEquip.findViewById(R.id.iv_image);
//        ivImg.setBackground(mActivity.getResources().getDrawable(imgId));
//        TextView tvName = llEquip.findViewById(R.id.tv_name);
//        tvName.setText(equiName + equiId);
//        TextView tvStatus = llEquip.findViewById(R.id.tv_status);
//        tvStatus.setText(equiStatusPara + "/" + equiPara);
//        final ImageView ivMore = llEquip.findViewById(R.id.iv_more);
//        final LinearLayout llSet = llEquip.findViewById(R.id.ll_set);
//        final LinearLayout llInfo = llEquip.findViewById(R.id.ll_info);
//        llInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (llSet.getVisibility() == View.GONE) {
//                    llSet.setVisibility(View.VISIBLE);
//                    ivMore.setPivotX(ivMore.getWidth() / 2);
//                    ivMore.setPivotY(ivMore.getHeight() / 2); // 支点在图片中心
//                    ivMore.setRotation(90);
//                } else {
//                    llSet.setVisibility(View.GONE);
//                    ivMore.setPivotX(ivMore.getWidth() / 2);
//                    ivMore.setPivotY(ivMore.getHeight() / 2);
//                    ivMore.setRotation(0);
//                }
//            }
//        });
//
//        final EditText etOpenTime = llEquip.findViewById(R.id.et_open_time);
//        final EditText etCloseTime = llEquip.findViewById(R.id.et_close_time);
//        final EditText etPosition = llEquip.findViewById(R.id.et_position);
//        final RadioGroup rgRbm = llEquip.findViewById(R.id.rg_rbm);
//        rgRbm.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.rb_open_all || checkedId == R.id.rb_close_all) {
//                    etOpenTime.setEnabled(false);
//                    etCloseTime.setEnabled(false);
//                    etPosition.setEnabled(false);
//                } else if (checkedId == R.id.rb_open_time) {
//                    etOpenTime.setEnabled(true);
//                    etCloseTime.setEnabled(false);
//                    etPosition.setEnabled(false);
//                } else if (checkedId == R.id.rb_close_time) {
//                    etOpenTime.setEnabled(false);
//                    etCloseTime.setEnabled(true);
//                    etPosition.setEnabled(false);
//                } else if (checkedId == R.id.rb_open_close_to) {
//                    etOpenTime.setEnabled(false);
//                    etCloseTime.setEnabled(false);
//                    etPosition.setEnabled(true);
//                }
//            }
//        });
//
//        Button btnStart = llEquip.findViewById(R.id.btn_start);
//        btnStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String paraTime = null;
//                String paraPos = null;
//                String controlType = equiType + "REV";
//
//                switch (rgRbm.getCheckedRadioButtonId()) {
//                    case R.id.rb_open_all:
//                        paraPos = equiPara.toString();
//                        break;
//                    case R.id.rb_close_all:
//                        controlType = equiType + "FWD";
//                        paraPos = "0";
//                        break;
//                    case R.id.rb_open_time:
//                        if (!CheckInputUtil.checkTextViewInput(etOpenTime, mActivity, "请输入运行时间！")) {
//                            return;
//                        }
//                        paraTime = etOpenTime.getText().toString().trim();
//                        break;
//                    case R.id.rb_close_time:
//                        controlType = equiType + "FWD";
//                        if (!CheckInputUtil.checkTextViewInput(etCloseTime, mActivity, "请输入运行时间！")) {
//                            return;
//                        }
//                        paraTime = etCloseTime.getText().toString().trim();
//                        break;
//                    case R.id.rb_open_close_to:
//                        if (!CheckInputUtil.checkTextViewInput(etPosition, mActivity, "请输入最终位置！")) {
//                            return;
//                        }
//                        Integer pos = Integer.valueOf(etPosition.getText().toString().trim());
//                        if (pos < 0 || pos > equiPara) {
//                            DlgUtil.showMsgInfo(mActivity, "输入的圈数必须在0到" + equiPara + "之间！");
//                            return;
//                        }
//                        if (pos == equiStatusPara) {
//                            DlgUtil.showMsgInfo(mActivity, "输入的圈数与现在设备的状态相同！");
//                            return;
//                        } else if (pos < equiStatusPara) {
//                            controlType = equiType + "FWD";
//                        }
//                        paraPos = pos.toString();
//                        break;
//                }
//
//                String msg = "确定要启动该" + equiName + "吗？";
//                final String finalControlType = controlType;
//                final String finalParaTime = paraTime;
//                final String finalParaPos = paraPos;
//                DlgUtil.showAsk(mActivity, msg, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Map<String, String> mp = new LinkedHashMap<>();
//                        final String tradeId = "0_" + System.currentTimeMillis();
//                        mp.put("tradeId", tradeId);
//                        mp.put("gwId", gwId);
//                        mp.put("nodeId", nodeId);
//                        mp.put("equiType", equiType);
//                        mp.put("equiId", equiId);
//                        mp.put("controlType", finalControlType);
//                        mp.put("insertWay", "0");
//                        mp.put("userId", mUserId);
//                        mp.put("para1", finalParaTime);
//                        mp.put("para2", finalParaPos);
//
//                        AsyncSocketUtil.post(mActivity, "operation/addTradeInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
//                            @Override
//                            public void OnJSONArrayResult(JSONArray response) {
//                                startCheckThread(tradeId, "启动该" + equiName, null, 1);
//                            }
//                        }, null);
//
//                        llInfo.callOnClick();
//                    }
//                });
//            }
//        });
//
//        return llEquip;
//    }

//    private boolean getCheckByStatus(String equiStatus) {
//        if (equiStatus.equals("开") || equiStatus.equals("1")) {
//            return true;
//        } else {
//            return false;
//        }
//    }

//    private View getViewOfLed(final String equiName, final String equiStatus, final String gwId, final String nodeId,
//                              final String equiType, final String equiId, int imgId) {
//        final View llEquip = LayoutInflater.from(mActivity).inflate(R.layout.layout_equipment_led, null);
//        ImageView ivImg = llEquip.findViewById(R.id.iv_image);
//        ivImg.setBackground(mActivity.getResources().getDrawable(imgId));
//        TextView tvName = llEquip.findViewById(R.id.tv_name);
//        tvName.setText(equiName + equiId);
//        final Switch status = llEquip.findViewById(R.id.switch_status);
//        final LinearLayout llTimePata = llEquip.findViewById(R.id.ll_paras);
//        final EditText etOpenTime = llEquip.findViewById(R.id.et_open_time);
//        TextView tvTimeUnit = llEquip.findViewById(R.id.tv_time_unit);
//        if (equiType.equals("LED")) {
//            tvTimeUnit.setText("分钟");
//        } else {
//            tvTimeUnit.setText("秒");
//        }
//        status.setChecked(getCheckByStatus(equiStatus));
//        if (status.isChecked()) {
//            llTimePata.setVisibility(View.GONE);
//        } else {
//            llTimePata.setVisibility(View.VISIBLE);
//        }
//        status.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String msg = "确定要";
//                if (status.isChecked()) {
//                    msg += "开启";
//                } else {
//                    msg += "关闭";
//                }
//                msg += "该" + equiName + "吗？";
//
//                DlgUtil.showAsk(mActivity, msg, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Map<String, String> mp = new LinkedHashMap<>();
//                        final String tradeId = "0_" + System.currentTimeMillis();
//                        mp.put("tradeId", tradeId);
//                        mp.put("gwId", gwId);
//                        mp.put("nodeId", nodeId);
//                        mp.put("equiType", equiType);
//                        mp.put("equiId", equiId);
//                        String controlType = equiType + "ON";
//                        String msg = "打开";
//                        if (!status.isChecked()) {
//                            controlType = equiType + "OFF";
//                            msg = "关闭";
//                        } else {
//                            if (!TextUtils.isEmpty(etOpenTime.getText().toString().trim())) {
//                                mp.put("para1", etOpenTime.getText().toString().trim());
//                            }
//                        }
//                        msg += equiName;
//                        mp.put("controlType", controlType);
//                        mp.put("insertWay", "0");
//                        mp.put("userId", mUserId);
//
//                        final String finalMsg = msg;
//                        AsyncSocketUtil.post(mActivity, "operation/addTradeInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
//                            @Override
//                            public void OnJSONArrayResult(JSONArray response) {
//                                try {
//                                    JSONObject msgObj = response.getJSONObject(0);
//                                    String msg = msgObj.getString("message");
//                                    if (msg.equals("success")) {
//                                        startCheckThread(tradeId, finalMsg, llEquip, 1);
//                                        if (status.isChecked()) {
//                                            llTimePata.setVisibility(View.GONE);
//                                        } else {
//                                            llTimePata.setVisibility(View.VISIBLE);
//                                        }
//                                    } else {
//                                        status.setChecked(!status.isChecked());
//                                    }
//                                } catch (JSONException e) {
//                                    status.setChecked(!status.isChecked());
//                                }
//                            }
//                        }, new AsyncSocketUtil.onFailString() {
//                            @Override
//                            public void OnStringResult(String errMsg) {
//                                status.setChecked(!status.isChecked());
//                            }
//                        });
//                    }
//                }, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        status.setChecked(!status.isChecked());
//                    }
//                });
//            }
//        });
//        return llEquip;
//    }

//    private View getViewOfDrip(final String equiName, final String equiStatus, final String gwId, final String nodeId,
//                              final String equiType, final String equiId, int imgId) {
//        final View llEquip = LayoutInflater.from(mActivity).inflate(R.layout.layout_equipment_drip, null);
//        ImageView ivImg = llEquip.findViewById(R.id.iv_image);
//        ivImg.setBackground(mActivity.getResources().getDrawable(imgId));
//        TextView tvName = llEquip.findViewById(R.id.tv_name);
//        tvName.setText(equiName + equiId);
//        final Switch status = llEquip.findViewById(R.id.switch_status);
//        final LinearLayout llTimePata = llEquip.findViewById(R.id.ll_paras);
//        final EditText etOpenTime = llEquip.findViewById(R.id.et_open_time);
//        final EditText etOpenCount = llEquip.findViewById(R.id.et_open_count);
//        status.setChecked(getCheckByStatus(equiStatus));
//        if (status.isChecked()) {
//            llTimePata.setVisibility(View.GONE);
//        } else {
//            llTimePata.setVisibility(View.VISIBLE);
//        }
//
//        final RadioGroup rgRbm = llEquip.findViewById(R.id.rg_drip);
//        rgRbm.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.rb_open) {
//                    etOpenTime.setEnabled(false);
//                    etOpenCount.setEnabled(false);
//                } else if (checkedId == R.id.rb_open_by_time) {
//                    etOpenTime.setEnabled(true);
//                    etOpenCount.setEnabled(false);
//                } else if (checkedId == R.id.rb_open_by_count) {
//                    etOpenTime.setEnabled(false);
//                    etOpenCount.setEnabled(true);
//                }
//            }
//        });
//
//        status.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String msg = "确定要";
//                if (status.isChecked()) {
//                    msg += "开启";
//                } else {
//                    msg += "关闭";
//                }
//                msg += "该" + equiName + "吗？";
//
//                DlgUtil.showAsk(mActivity, msg, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Map<String, String> mp = new LinkedHashMap<>();
//                        final String tradeId = "0_" + System.currentTimeMillis();
//                        mp.put("tradeId", tradeId);
//                        mp.put("gwId", gwId);
//                        mp.put("nodeId", nodeId);
//                        mp.put("equiType", equiType);
//                        mp.put("equiId", equiId);
//                        String controlType = equiType + "ON";
//                        String msg = "打开";
//                        if (!status.isChecked()) {
//                            controlType = equiType + "OFF";
//                            msg = "关闭";
//                        } else {
//                            switch (rgRbm.getCheckedRadioButtonId()) {
//                                case R.id.rb_open:
//                                    break;
//                                case R.id.rb_open_by_time:
//                                    if (!CheckInputUtil.checkTextViewInput(etOpenTime, mActivity, "请输入开启的时间！")) {
//                                        status.setChecked(getCheckByStatus(equiStatus));
//                                        return;
//                                    }
//                                    mp.put("para1", etOpenTime.getText().toString().trim());
//                                    break;
//                                case R.id.rb_open_by_count:
//                                    if (!CheckInputUtil.checkTextViewInput(etOpenCount, mActivity, "请输入开启的立方数！")) {
//                                        status.setChecked(getCheckByStatus(equiStatus));
//                                        return;
//                                    }
//                                    mp.put("para2", etOpenCount.getText().toString().trim());
//                                    break;
//                            }
//                        }
//                        msg += equiName;
//                        mp.put("controlType", controlType);
//                        mp.put("insertWay", "0");
//                        mp.put("userId", mUserId);
//
//                        final String finalMsg = msg;
//                        AsyncSocketUtil.post(mActivity, "operation/addTradeInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
//                            @Override
//                            public void OnJSONArrayResult(JSONArray response) {
//                                try {
//                                    JSONObject msgObj = response.getJSONObject(0);
//                                    String msg = msgObj.getString("message");
//                                    if (msg.equals("success")) {
//                                        startCheckThread(tradeId, finalMsg, llEquip, 1);
//                                        if (status.isChecked()) {
//                                            llTimePata.setVisibility(View.GONE);
//                                        } else {
//                                            llTimePata.setVisibility(View.VISIBLE);
//                                        }
//                                    } else {
//                                        status.setChecked(!status.isChecked());
//                                    }
//                                } catch (JSONException e) {
//                                    status.setChecked(!status.isChecked());
//                                }
//                            }
//                        }, new AsyncSocketUtil.onFailString() {
//                            @Override
//                            public void OnStringResult(String errMsg) {
//                                status.setChecked(!status.isChecked());
//                            }
//                        });
//                    }
//                }, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        status.setChecked(!status.isChecked());
//                    }
//                });
//            }
//        });
//
//        return llEquip;
//    }

    private void startCheckThread(String tradeId, EquipmentInfo equipInfo, int nCnt) {
        CheckResultThread thread = new CheckResultThread();
        thread.setData(tradeId, equipInfo, nCnt);
        mThreads.put(tradeId, thread);
        thread.start();
    }

    private void startRefreshThread(String tradeId, Button btnfresh, int nCnt) {
        RefreshResultThread thread = new RefreshResultThread();
        thread.setData(tradeId, btnfresh, nCnt);
        mRefreashThreads.put(tradeId, thread);
        thread.start();
    }

    public class CheckResultThread extends Thread {
        private String mTradeId;
        private EquipmentInfo mEquipInfo;
        private int mCount;

        @Override
        public void run() {
            try {
                Thread.sleep(3000);

                Message message = Message.obtain();
                message.what = 1;
                Bundle data = new Bundle();
                data.putString("tradeId", mTradeId);
                data.putInt("count", mCount);
                message.obj = mEquipInfo;
                message.setData(data);
                handler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
                DlgUtil.showExceptionPrompt(mActivity, e);
            }
        }

        public void setData(String tradeId, EquipmentInfo equipInfo, int nCnt) {
            mTradeId = tradeId;
            mEquipInfo = equipInfo;
            mCount = nCnt;
        }
    }

    public class RefreshResultThread extends Thread {
        private String mTradeId;
        private Button mBtnfresh;
        private int mCount;

        @Override
        public void run() {
            try {
                Thread.sleep(3000);

                Message message = Message.obtain();
                message.what = 2;
                Bundle data = new Bundle();
                data.putString("tradeId", mTradeId);
                data.putInt("count", mCount);
                message.obj = mBtnfresh;
                message.setData(data);
                handler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
                DlgUtil.showExceptionPrompt(mActivity, e);
            }
        }

        public void setData(String tradeId, Button btnfresh, int nCnt) {
            mTradeId = tradeId;
            mBtnfresh = btnfresh;
            mCount = nCnt;
        }
    }

    private void getTradeResult(final String tradeId, final EquipmentInfo equipInfo, final int nCount) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("tradeId", tradeId);

        final String finalMsg = "操作" + equipInfo.getEquipFullName() + "（" + equipInfo.getOperatinMsg() + "）";

        AsyncSocketUtil.post(mActivity, "operation/checkTradeResult", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject msgObj = response.getJSONObject(0);
                    String msg = msgObj.getString("message");
                    if (msg.equals("success")) {
                        String result = response.getJSONObject(1).getString("result");
                        if (result.equals("0") || result.equals("2")) {
                            if (nCount < 21) {
                                startCheckThread(tradeId, equipInfo, nCount + 1);
                            } else {
                                DlgUtil.showMsgInfo(mActivity, finalMsg + "失败：没有反应，请联系系统管理员！", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        resetEquipState(equipInfo);
                                    }
                                });
                            }
                        } else if (result.equals("1")) {
                            DlgUtil.showMsgInfo(mActivity, finalMsg + "成功！", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    resetEquipState(equipInfo);
                                }
                            });
                        } else if (result.equals("C")) {
                            DlgUtil.showMsgInfo(mActivity, finalMsg + "，已到达限位！", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    resetEquipState(equipInfo);
                                }
                            });
                        } else if (result.equals("D")) {
                            DlgUtil.showMsgInfo(mActivity, finalMsg + "，外设开关启用状态，请更新设备状态！", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    resetEquipState(equipInfo);
                                }
                            });
                        } else {
                            DlgUtil.showMsgInfo(mActivity, finalMsg + "失败：" + response.getJSONObject(1).getString("remark"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    resetEquipState(equipInfo);
                                }
                            });
                        }
                    } else {
                        DlgUtil.showMsgInfo(mActivity, finalMsg + "失败：" + msg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetEquipState(equipInfo);
                            }
                        });
                    }
                } catch (JSONException e) {
                    DlgUtil.showMsgInfo(mActivity, finalMsg + "失败：" + e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetEquipState(equipInfo);
                        }
                    });
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                resetEquipState(equipInfo);
            }
        });
    }

    private void getRefreashResult(final String trades, final Button btnR, final int nCount) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("tradesId", trades);

        AsyncSocketUtil.post(mActivity, "operation/checkRefreshResult", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject msgObj = response.getJSONObject(0);
                    String msg = msgObj.getString("message");
                    if (msg.equals("success")) {
                        String newTrades = "";
                        for (int i = 1; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String result = obj.getString("result");
                            if (result.equals("0") || result.equals("2")) {
                                if (newTrades.length() > 0) {
                                    newTrades += "+";
                                }
                                newTrades += obj.getString("tradeId");
                            } else if (result.equals("1")) {

                            } else {
                                DlgUtil.showMsgInfo(mActivity, "更新节点设备状态失败：" + obj.getString("remark"), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        btnR.setEnabled(true);
                                    }
                                });
                                return;
                            }
                        }

                        if (newTrades.length() == 0) {
                            DlgUtil.showMsgInfo(mActivity, "更新节点设备状态成功！", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getEquipmentInfo();
                                }
                            });
                        } else {
                            if (nCount < 21) {
                                startRefreshThread(newTrades, btnR, nCount + 1);
                            } else {
                                DlgUtil.showMsgInfo(mActivity, "更新节点设备状态失败：没有反应，请联系系统管理员！", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        btnR.setEnabled(true);
                                    }
                                });
                            }
                        }
                    } else {
                        DlgUtil.showMsgInfo(mActivity, "更新节点设备状态失败：" + msg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btnR.setEnabled(true);
                            }
                        });
                    }
                } catch (JSONException e) {
                    DlgUtil.showMsgInfo(mActivity, "更新节点设备状态失败：" + e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            btnR.setEnabled(true);
                        }
                    });
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                btnR.setEnabled(true);
            }
        });
    }

    private void resetEquipState(final EquipmentInfo equipInfo) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("gwId", equipInfo.getGwId());
        mp.put("nodeId", equipInfo.getNodeId());
        mp.put("equipType", equipInfo.getEquiType());
        mp.put("equipId", equipInfo.getEquiId());

        AsyncSocketUtil.post(mActivity, "gateway/getEquipmentInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject jOEquip = response.getJSONObject(1);
                    equipInfo.updateStatusInfo(jOEquip);
                    equipInfo.enasableUI();
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }

//    private void resetEquipState(View llEquip) {
//        if (llEquip != null) {
//            Switch status = llEquip.findViewById(R.id.switch_status);
//            status.setChecked(!status.isChecked());
//
//            LinearLayout llTimePata = llEquip.findViewById(R.id.ll_paras);
//            if (status.isChecked()) {
//                llTimePata.setVisibility(View.GONE);
//            } else {
//                llTimePata.setVisibility(View.VISIBLE);
//            }
//        }
//    }
}
