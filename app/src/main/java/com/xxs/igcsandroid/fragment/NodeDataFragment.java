package com.xxs.igcsandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.SensorDetailActivity;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class NodeDataFragment extends BaseFragment {
    private Handler handler;        // 简单理解，Handler就是解决线程和线程之间的通信的
    private Runnable runnable;      // 通过实现Runnable接口，实现多线程

    private LinearLayout llRoot;
    private TextView tv_countdown;
    private String remainTime = "600";
    private int freshTime = 1000;

    private String mGhId;

    public NodeDataFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle data = getArguments();
        mGhId = data.getString("ghId");

        View view = inflater.inflate(R.layout.fragment_node_data, container, false);

        llRoot = view.findViewById(R.id.linerlayout_gh_root);
        tv_countdown = view.findViewById(R.id.tv_countdown);
        tv_countdown.setText(remainTime);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                int now = Integer.parseInt(tv_countdown.getText().toString());
                if (now > 1) {
                    now = now -1;
                    tv_countdown.setText(String.valueOf(now));
                } else {
                    tv_countdown.setText(remainTime);
                    getData();
                }

                handler.postDelayed(this, freshTime);
            }
        };
        handler.postDelayed(runnable, freshTime);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        tv_countdown.setText(remainTime);
        getData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);      // 关闭定时器处理
    }

    private void getData() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(mActivity, "gateway/getGhNodesData", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    llRoot.removeAllViews();

                    for (int i = 1; i < response.length(); i += 2) {
                        View vNode = LayoutInflater.from(mActivity).inflate(R.layout.layout_node, null);
                        LinearLayout llOneNdeRoot = vNode.findViewById(R.id.ll_one_node_root);

                        TextView tvNode = vNode.findViewById(R.id.tv_node);
                        TextView tvNodeTime = vNode.findViewById(R.id.tv_datatime);
                        JSONObject nodeObj = response.getJSONObject(i);
                        String gwId = nodeObj.getString("gwId");
                        String nodeId = nodeObj.getString("NodeId");
                        String nodeName = nodeObj.getString("NodeName");
                        tvNode.setText(nodeName + "(网关" + gwId + " 节点" + nodeId + ")");
                        tvNodeTime.setText(nodeObj.getString("NodeTime"));

                        JSONArray jAOne = response.getJSONArray(i + 1);
                        for (int j = 0; j < jAOne.length(); j += 3) {
                            View vSensorRow = LayoutInflater.from(mActivity).inflate(R.layout.layout_sensor_row, null);
                            LinearLayout llSensorRow = vSensorRow.findViewById(R.id.ll_sensor_row);

                            JSONObject jOSensor = jAOne.getJSONObject(j);
                            View ll_sensor = getViewOfSensor(jOSensor, gwId, nodeId, nodeName);
                            llSensorRow.addView(ll_sensor);

                            Space spaceBetween = getSpaceView();
                            llSensorRow.addView(spaceBetween);

                            if (j + 1 < jAOne.length()) {
                                jOSensor = jAOne.getJSONObject(j + 1);
                                ll_sensor = getViewOfSensor(jOSensor, gwId, nodeId, nodeName);
                            } else {
                                ll_sensor = getViewOfEmptySensor();
                            }
                            llSensorRow.addView(ll_sensor);
                            spaceBetween = getSpaceView();
                            llSensorRow.addView(spaceBetween);

                            if (j + 2 < jAOne.length()) {
                                jOSensor = jAOne.getJSONObject(j + 2);
                                ll_sensor = getViewOfSensor(jOSensor, gwId, nodeId, nodeName);
                            } else {
                                ll_sensor = getViewOfEmptySensor();
                            }
                            llSensorRow.addView(ll_sensor);

                            llOneNdeRoot.addView(llSensorRow);
                        }

                        llRoot.addView(llOneNdeRoot);
                    }
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }

    private View getViewOfSensor(JSONObject jOSensor, final String gwId, final String nodeId, final String nodeName) throws JSONException {
        float value = (float) jOSensor.getDouble("SensorValue");
        final String sensorId = jOSensor.getString("SensorId");
        final String sensorTypeCode = jOSensor.getString("SensorTypeCode");
        final String sensorUnit = jOSensor.getString("SensorUnit");
        final String sensorName = jOSensor.getString("SensorName");
        String className = jOSensor.getString("SensorClass");
        String sensorPicClass = jOSensor.getString("SensorPicClass").toLowerCase();

        View ll_sensor;
        ImageView ivType;
        TextView tvValue;
        TextView tvUnit;
        TextView tvName;

        if (className.equals("sensor_normal_bg")) {
            ll_sensor = LayoutInflater.from(mActivity).inflate(R.layout.layout_sensor_normal, null);
            ivType = ll_sensor.findViewById(R.id.iv_type);
        } else {
            ll_sensor = LayoutInflater.from(mActivity).inflate(R.layout.layout_sensor_abnormal, null);
            ivType = ll_sensor.findViewById(R.id.iv_abtype);
        }
        int resid = getResources().getIdentifier(sensorPicClass , "drawable", mActivity.getPackageName());
        ivType.setImageResource(resid);

        tvValue = ll_sensor.findViewById(R.id.tv_sensor_value);
        String strVal = String.valueOf(value);
        if (sensorTypeCode.equals("ACO2") || sensorTypeCode.equals("AILLU")) {
            strVal = String.format("%.0f", value);
        }
        tvValue.setText(strVal);

        tvUnit = ll_sensor.findViewById(R.id.tv_sensor_unit);
        tvUnit.setText(sensorUnit);

        tvName = ll_sensor.findViewById(R.id.tv_sensor_name);
        tvName.setText(sensorName);

        ll_sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SensorDetailActivity.class);
                intent.putExtra("gwId", gwId);
                intent.putExtra("nodeId", nodeId);
                intent.putExtra("sensorId", sensorId);
                intent.putExtra("sensorTypeCode", sensorTypeCode);
                intent.putExtra("nodeName", nodeName);
                intent.putExtra("sensorName", sensorName);
                intent.putExtra("sensorUnit", sensorUnit);
                mActivity.startActivity(intent);
            }
        });

        return ll_sensor;
    }

    private View getViewOfEmptySensor() {
        return LayoutInflater.from(mActivity).inflate(R.layout.layout_sensor_empty, null);
    }

    private Space getSpaceView() {
        Space spaceBetween = new Space(mActivity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        spaceBetween.setLayoutParams(layoutParams);
        return spaceBetween;
    }

}
