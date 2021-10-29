package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.entity.HistoryDataHolder;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class HistoryChartActivity extends AppCompatActivity {
    private Bundle bundle;

    private static final int CHART_COLOR[] = {Color.rgb(124,181,236), Color.rgb(67,67,72), Color.rgb(144,237,125),
            Color.rgb(247,163,92), Color.rgb(128,133,233), Color.rgb(241,92,128), Color.rgb(228,211,84),
            Color.rgb(128,133,232), Color.rgb(141,70,83), Color.rgb(145,232,225)};
    private LineChartView mLineChartView;
    private List<String> lstLineNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_chart);

        mLineChartView = findViewById(R.id.chart_show);
        // 设置行为属性，支持缩放、滑动以及平移
        mLineChartView.setInteractive(true);
        mLineChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        mLineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        // 设置数据值点击监听
        mLineChartView.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, PointValue pointValue) {
                Toast.makeText(HistoryChartActivity.this, lstLineNames.get(i) + "：" + pointValue.getY(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {

            }
        });

        Intent curIntent = getIntent();
        bundle = curIntent.getExtras();
        setTitle(bundle.getString("title"));

        loadChart();
    }

    private void loadChart() {
        try {
            String dataId = bundle.getString("dataId");
            JSONArray response = (JSONArray) HistoryDataHolder.getInstance().retrieve(dataId);

            List<Line> lines = new ArrayList<>();
            LineChartData data = new LineChartData();

            int n = 0;
            float fVal;
            boolean bHasLeftY = false;
            String leftName = "";
            boolean bHasRightY = false;
            String rightName = "";
            int xyNum = 0;
            Set<String> setLeft = new TreeSet<String>();
            Set<String> setRight = new TreeSet<String>();
            Set<String> setTimeStr = getXAxisData(response);
            List<AxisValue> mAxisValues = new ArrayList<>();
            for (String time : setTimeStr) {
                mAxisValues.add(new AxisValue(n).setLabel(time));
                n++;
            }

            Axis axisX = getAxisX();
            axisX.setValues(mAxisValues);
            data.setAxisXBottom(axisX);

            for (int i = 1; i < response.length(); i++) {
                JSONObject lineObj = response.getJSONObject(i);
                String lineInfo = lineObj.getString("data");
                int yType = lineObj.getInt("yAxis");
                String sensor = lineObj.getString("type");
                String sensorTypeCode = lineObj.getString("sensorTypeCode");
                lstLineNames.add(sensor);

                List<PointValue> mPointValues = new ArrayList<>();
                Map<String, Float> mpLine = convertLineStringToMap(lineInfo);

                n = 0;
                for (String time : setTimeStr) {
                    if (mpLine.containsKey(time)) {
                        fVal = mpLine.get(time);
                    } else {
                        fVal = 0;
                    }
                    if (yType != 2) {
                        mPointValues.add(new PointValue(n, fVal));
                    } else {
                        mPointValues.add(new PointValue(n, fVal));
                    }
                    n++;
                }
                Line line = new Line(mPointValues).setColor(CHART_COLOR[(i - 1) % 10]).setCubic(false);
                line.setPointRadius(1);
//                line.setHasLabelsOnlyForSelected(true);
                lines.add(line);

                if (setLeft.contains(sensorTypeCode)) {
                    if (leftName.length() > 0) {
                        leftName += "/";
                    }
                    leftName += sensor;
                } else if (setRight.contains(sensorTypeCode)) {
                    if (rightName.length() > 0) {
                        rightName += "/";
                    }
                    rightName += sensor;
                } else {
                    if (setLeft.size() <= setRight.size()) {
                        if (leftName.length() > 0) {
                            leftName += "/";
                        }
                        leftName += sensor;
                        setLeft.add(sensorTypeCode);
                        bHasLeftY = true;
                    } else {
                        if (rightName.length() > 0) {
                            rightName += "/";
                        }
                        rightName += sensor;
                        setRight.add(sensorTypeCode);
                        bHasRightY = true;
                    }
                }

//                if (xyNum % 2 == 0/*yType != 2*/) {
//                    bHasLeftY = true;
//                    if (!leftName.contains(sensor)) {
//                        if (leftName.length() > 0) {
//                            leftName += "/";
//                        }
//                        leftName += sensor;
//                        xyNum++;
//                    }
//                } else {
//                    bHasRightY = true;
//                    if (!rightName.contains(sensor)) {
//                        if (rightName.length() > 0) {
//                            rightName += "/";
//                        }
//                        rightName += sensor;
//                        xyNum++;
//                    }
//                }
            }
            data.setLines(lines);

            if (bHasLeftY) {
                Axis axisY = getAxisY();
                axisY.setName(leftName);
                data.setAxisYLeft(axisY);
            }
            if (bHasRightY) {
                Axis axisY = getAxisY();
                axisY.setName(rightName);
                data.setAxisYRight(axisY);
            }

            mLineChartView.setLineChartData(data);
        } catch (Exception e) {
            DlgUtil.showExceptionPrompt(this, e);
        }
    }

    private Axis getAxisX() {
        Axis axisX = new Axis();
        axisX.setHasLines(true);                            // x轴分割线
        axisX.setLineColor(Color.rgb(169, 169, 169));
        axisX.setHasTiltedLabels(false);                    // X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.rgb(102, 102, 102));       // 设置字体颜色
//      axisX.setTextSize(10);                              // 设置字体大小
//      axisX.setName("采集时间");                           // 标注
        axisX.setMaxLabelChars(12);                         // 最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length

        return axisX;
    }

    private Axis getAxisY() {
        Axis axisY = new Axis();
        axisY.setHasLines(true);                            // Y轴分割线
        axisY.setLineColor(Color.rgb(169, 169, 169));
        axisY.setMaxLabelChars(5);                          // 默认是3，只能看最后三个数字
//      axisY.setName(SensorType.getUnitString(sensorType));
        axisY.setTextColor(Color.rgb(102, 102, 102));       // 设置字体颜色

        return axisY;
    }

    private Set<String> getXAxisData(JSONArray response) throws JSONException {
        Set<Long> setTime = new TreeSet<>();
        Long lVal;

        for (int i = 1; i < response.length(); i++) {
            JSONObject lineObj = response.getJSONObject(i);
            String lineInfo = lineObj.getString("data");

            String[] strArry = lineInfo.split("\\[\\[|\\],\\[|\\]\\]");
            for (int j = 0; j < strArry.length; j++) {
                if (strArry[j].length() == 0) {
                    continue;
                }
                String[] strSub = strArry[j].split(",");
                lVal = Long.valueOf(strSub[0]);
                if (setTime.contains(lVal)) {
                    continue;
                }
                setTime.add(lVal);
            }
        }

        Set<String> setTimeStr = new TreeSet<>();
        SimpleDateFormat sf = new SimpleDateFormat(bundle.getString("dateFormat"));
        Date date = new Date();
        for (Long l : setTime) {
            date.setTime(l);
            setTimeStr.add(sf.format(date));
        }

        return setTimeStr;
    }

    private Map<String,Float> convertLineStringToMap(String lineInfo) {
        Map<String, Float> mp = new LinkedHashMap<>();
        String[] strArry = lineInfo.split("\\[\\[|\\],\\[|\\]\\]");
        SimpleDateFormat sf = new SimpleDateFormat(bundle.getString("dateFormat"));
        Date date = new Date();
        for (int i = 0; i < strArry.length; i++) {
            if (strArry[i].length() == 0) {
                continue;
            }
            String[] strSub = strArry[i].split(",");
            date.setTime(Long.valueOf(strSub[0]));
            mp.put(sf.format(date), Float.valueOf(strSub[1]));
        }
        return mp;
    }
}
