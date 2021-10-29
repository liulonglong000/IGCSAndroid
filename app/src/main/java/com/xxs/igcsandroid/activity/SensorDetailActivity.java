package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.LineChartUtil;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class SensorDetailActivity extends AppCompatActivity {
    private String gwId;
    private String nodeId;
    private String sensorId;
    private String sensorTypeCode;

    private LineChartUtil mLcUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_detail);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        gwId = bundle.getString("gwId");
        nodeId = bundle.getString("nodeId");
        sensorId = bundle.getString("sensorId");
        sensorTypeCode = bundle.getString("sensorTypeCode");
        String name = bundle.getString("ghName");

        setTitle(bundle.getString("nodeName") + "-" + bundle.getString("sensorName")
                + "（" + bundle.getString("sensorUnit") + "）24小时数据信息");

        mLcUtil = new LineChartUtil(this, R.id.chart_detail);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getRecentInfo();
    }

    private void getRecentInfo() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("gwId", gwId);
        mp.put("nodeId", nodeId);
        mp.put("sensorId", sensorId);
        mp.put("sensorTypeCode", sensorTypeCode);

        AsyncSocketUtil.post(SensorDetailActivity.this, "gateway/getRecentInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    mLcUtil.setData(response.getJSONObject(1).getString("data"));
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(SensorDetailActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                SensorDetailActivity.this.finish();
            }
        });
    }
}
