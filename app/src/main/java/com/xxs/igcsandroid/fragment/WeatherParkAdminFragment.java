package com.xxs.igcsandroid.fragment;


import android.content.Intent;
import android.graphics.Color;
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
import com.xxs.igcsandroid.activity.SensorWDetailActivity;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherParkAdminFragment extends BaseFragment {
    private Handler handler;        // 简单理解，Handler就是解决线程和线程之间的通信的
    private Runnable runnable;      // 通过实现Runnable接口，实现多线程

    private LinearLayout llRoot;
    private TextView tv_countdown;
    private String remainTime = "600";
    private int freshTime = 1000;

    private boolean bSysAdmin = false;
    private String mParkId;

    public WeatherParkAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_park_admin, container, false);

        Bundle data = getArguments();
        if (data != null && data.containsKey("parkId")) {
            mParkId = data.getString("parkId");
            bSysAdmin = true;
        }

        llRoot = view.findViewById(R.id.linerlayout_weather_root);
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
                    getWeathersFromServer();
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
        getWeathersFromServer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);      // 关闭定时器处理
    }

    private void getWeathersFromServer() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", MyApplication.getInstance().getMyUserId());
        mp.put("parkId", mParkId);

        AsyncSocketUtil.post(mActivity, "gateway/getWeatherData", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    llRoot.removeAllViews();

                    for (int i = 1; i < response.length(); i += 2) {
                        View vNode = LayoutInflater.from(mActivity).inflate(R.layout.layout_weather, null);
                        LinearLayout llOneNdeRoot = vNode.findViewById(R.id.ll_one_weather_root);

                        TextView tvNode = vNode.findViewById(R.id.tv_weather_name);
                        TextView tvNodeTime = vNode.findViewById(R.id.tv_datatime);
                        JSONObject nodeObj = response.getJSONObject(i);
                        String gwId = nodeObj.getString("gwId");
                        String nodeId = nodeObj.getString("NodeId");
                        String gwName = nodeObj.getString("gwName");
                        tvNode.setText(gwName);
                        tvNodeTime.setText(nodeObj.getString("gwTime"));

                        JSONArray jAOne = response.getJSONArray(i + 1);
                        for (int j = 0; j < jAOne.length(); j += 2) {
                            View vSensorRow = LayoutInflater.from(mActivity).inflate(R.layout.layout_wsensor_row, null);
                            LinearLayout llSensorRow = vSensorRow.findViewById(R.id.ll_wsensor_row);

                            JSONObject jOSensor = jAOne.getJSONObject(j);
                            updateViewOfSensor(jOSensor, gwId, nodeId, gwName, vSensorRow, true);

                            if (j + 1 < jAOne.length()) {
                                jOSensor = jAOne.getJSONObject(j + 1);
                                updateViewOfSensor(jOSensor, gwId, nodeId, gwName, vSensorRow, false);
                            }

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

    private void updateViewOfSensor(JSONObject jOSensor, final String gwId, final String nodeId, final String gwName, View ll_sensor, boolean bLeft) throws JSONException {
        float value = (float) jOSensor.getDouble("SensorValue");
        final String sensorId = jOSensor.getString("SensorId");
        final String sensorTypeCode = jOSensor.getString("SensorTypeCode");
        final String sensorUnit = jOSensor.getString("SensorUnit");
        final String sensorName = jOSensor.getString("SensorName");

        LinearLayout llBg;
        TextView tvValue;
        TextView tvUnit;
        TextView tvName;

        if (bLeft) {
            llBg = ll_sensor.findViewById(R.id.ll_sensor_bg);
        } else {
            llBg = ll_sensor.findViewById(R.id.ll_sensor_bg2);
        }
        llBg.setBackgroundColor(Color.WHITE);

        if (bLeft) {
            tvValue = ll_sensor.findViewById(R.id.tv_sensor_value);
        } else {
            tvValue = ll_sensor.findViewById(R.id.tv_sensor_value2);
        }
        tvValue.setText(String.valueOf(value));

        if (bLeft) {
            tvUnit = ll_sensor.findViewById(R.id.tv_sensor_unit);
        } else {
            tvUnit = ll_sensor.findViewById(R.id.tv_sensor_unit2);
        }
        tvUnit.setText(sensorUnit);

        if (bLeft) {
            tvName = ll_sensor.findViewById(R.id.tv_wsensor_name);
        } else {
            tvName = ll_sensor.findViewById(R.id.tv_wsensor_name2);
        }
        tvName.setText(sensorName);

        llBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SensorWDetailActivity.class);
                intent.putExtra("gwId", gwId);
                intent.putExtra("nodeId", nodeId);
                intent.putExtra("sensorId", sensorId);
                intent.putExtra("sensorTypeCode", sensorTypeCode);
                intent.putExtra("gwName", gwName);
                intent.putExtra("sensorName", sensorName);
                intent.putExtra("sensorUnit", sensorUnit);
                mActivity.startActivity(intent);
            }
        });
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
