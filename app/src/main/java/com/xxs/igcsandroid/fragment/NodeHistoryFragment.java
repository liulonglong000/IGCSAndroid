package com.xxs.igcsandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jaygoo.selector.MultiData;
import com.jaygoo.selector.MultiSelectPopWindow;
import com.jaygoo.selector.SignalSelectPopWindow;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.HistoryChartActivity;
import com.xxs.igcsandroid.activity.HistoryTableActivity;
import com.xxs.igcsandroid.control.LayoutTextAndSelect;
import com.xxs.igcsandroid.entity.HistoryDataHolder;
import com.xxs.igcsandroid.entity.NodeInfo;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NodeHistoryFragment extends HistoryFragment {
    private String mGhId;

    private LayoutTextAndSelect tasNode;
    private List<MultiData> lstNodes = new ArrayList<>();
    private String mSelGwNodeId;    // 格式：81000003-1
    private View viewNode;

    private boolean bHasGetFromSrv = false;

    public NodeHistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle data = getArguments();
        mGhId = data.getString("ghId");

        View view = inflater.inflate(R.layout.fragment_node_history, container, false);

        tasNode = view.findViewById(R.id.tas_node);
        tasNode.init("节        点：");
        tasNode.getTvSelect().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNodeSelection();
            }
        });

        tasSensor = view.findViewById(R.id.tas_sensor);
        tasSensor.init("传 感 器：");
        tasSensor.getTvSelect().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSensorSelection();
            }
        });

        viewNode = view.findViewById(R.id.mBottom);

        tasStartTime = view.findViewById(R.id.tas_start_time);
        tasStartTime.init("开始时间：");
        tasEndTime = view.findViewById(R.id.tas_end_time);
        tasEndTime.init("结束时间：");
        tasStartTime.getTvSelect().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedShowStartTime();
            }
        });
        tasEndTime.getTvSelect().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedShowEndTime();
            }
        });

        rbTable = view.findViewById(R.id.rb_table);
        rbPic = view.findViewById(R.id.rb_pic);
        initShowInfo();

        Button btn = view.findViewById(R.id.btn_query);
        btn.setText("查    询");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doQuery();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        saveShowInfo();
    }

    private void showNodeSelection() {
        if (!bHasGetFromSrv) {
            getNodesFromServer();
            return;
        }

        doShowNodeSelection();
    }

    private void doShowNodeSelection() {
        if (lstNodes.size() == 0) {
            DlgUtil.showMsgInfo(mActivity, "没有可供选择的节点！");
            return;
        } else if (lstNodes.size() == 1) {
            mSelGwNodeId = lstNodes.get(0).getId();
            tasNode.getTvSelect().setText(lstNodes.get(0).getText());
            return;
        }

        new SignalSelectPopWindow.Builder(mActivity)
                .setDataArray(lstNodes)
                .setConfirmListener(new SignalSelectPopWindow.OnConfirmClickListener() {
                    @Override
                    public void onClick(Integer indexSel) {
                        if (mSelGwNodeId != null && mSelGwNodeId.equals(lstNodes.get(indexSel).getId())) {
                            return;
                        }

                        tasNode.getTvSelect().setText(lstNodes.get(indexSel).getText());
                        mSelGwNodeId = lstNodes.get(indexSel).getId();

                        for (int i = 0; i < lstNodes.size(); i++) {
                            lstNodes.get(i).setbSelect(false);
                        }
                        lstNodes.get(indexSel).setbSelect(true);

                        clearSensorSelection();
                    }
                })
                .setTitle("节点列表")
                .build()
                .show(viewNode);
    }

    private void getNodesFromServer() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(mActivity, "gateway/getGhNodes", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    bHasGetFromSrv = true;

                    lstNodes.clear();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        NodeInfo nInfo = new NodeInfo(obj, false);
                        lstNodes.add(new MultiData(nInfo.getKeyId(),
                                nInfo.getValueShow(), false, null));
                    }

                    doShowNodeSelection();
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }

    private void showSensorSelection() {
        if (mSelGwNodeId == null) {
            DlgUtil.showMsgInfo(mActivity, "请先选择节点！");
            return;
        }

        if (!mpSensors.containsKey(mSelGwNodeId)) {
            getSensorInfoFromSrv();
            return;
        }

        doShowSensorSelection();
    }

    private void getSensorInfoFromSrv() {
        Map<String, String> mp = new LinkedHashMap<>();
        String[] strA = mSelGwNodeId.split("-");
        mp.put("gwId", strA[0]);
        mp.put("nodeId", strA[1]);

        AsyncSocketUtil.post(mActivity, "gateway/getNodeSensors", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    List<MultiData> mpSensor = new ArrayList<>();

                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        mpSensor.add(new MultiData(obj.getString("sensorId"), obj.getString("sensorName"), false));
                    }

                    mpSensors.put(mSelGwNodeId, mpSensor);

                    doShowSensorSelection();

                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }

    private void doShowSensorSelection() {
        final List<MultiData> lstSensors = mpSensors.get(mSelGwNodeId);

        new MultiSelectPopWindow.Builder(mActivity)
                .setDataArray(lstSensors)
                .setConfirmListener(new MultiSelectPopWindow.OnConfirmClickListener() {
                    @Override
                    public void onClick(List<Integer> indexList) {
                        String strSel = "";
                        for (int i = 0; i < lstSensors.size(); i++) {
                            MultiData data = lstSensors.get(i);
                            if (indexList.contains(i)) {
                                data.setbSelect(true);
                                strSel += data.getText();
                                strSel += " ";
                            } else {
                                data.setbSelect(false);
                            }
                        }
                        tasSensor.getTvSelect().setText(strSel);
                    }
                })
                .setTitle("传感器列表")
                .build()
                .show(viewNode);
    }

    private void doQuery() {
        if (!CheckInputUtil.checkTextViewInput(tasNode.getTvSelect(), mActivity, "请选择要查询的节点！")) {
            return;
        }
        if (!CheckInputUtil.checkTextViewInput(tasSensor.getTvSelect(), mActivity, "请选择要查询的传感器！")) {
            return;
        }
        if (!isTimeOK()) {
            return;
        }

        final String dateFormat = getDatwFormat();

        Map<String, String> mp = new LinkedHashMap<>();
        String[] strA = mSelGwNodeId.split("-");
        mp.put("gwFilter", strA[0]);
        mp.put("nodeFilter", strA[1]);
        mp.put("sensorListFilter", getSensorList(mpSensors.get(mSelGwNodeId)));
        mp.put("startTimeFilter", tasStartTime.getTvSelect().getText().toString());
        mp.put("endTimeFilter", tasEndTime.getTvSelect().getText().toString());
        mp.put("bWeather", "0");

        AsyncSocketUtil.post(mActivity, "gateway/getHistoryExInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    if (response.length() == 1) {
                        DlgUtil.showMsgInfo(mActivity, "在该条件设置下没有历史数据！");
                    } else {
                        Intent intent;
                        if (rbTable.isChecked()) {
                            intent = new Intent(mActivity, HistoryTableActivity.class);
                            String dataId = "1002";
                            intent.putExtra("dataId", dataId);
                            HistoryDataHolder.getInstance().save(dataId, response);
                        } else {
                            intent = new Intent(mActivity, HistoryChartActivity.class);
                            String dataId = "1001";
                            intent.putExtra("dataId", dataId);
                            HistoryDataHolder.getInstance().save(dataId, response);
                        }
                        intent.putExtra("dateFormat", dateFormat);
                        intent.putExtra("title", tasNode.getTvSelect().getText().toString() + "历史数据");
                        mActivity.startActivity(intent);
                    }
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }
}
