package com.xxs.igcsandroid.util;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class LineChartUtil {
    private LineChartView mLineChartView;

    public LineChartUtil(AppCompatActivity context, int chartId) {
        mLineChartView = context.findViewById(chartId);

        // 设置该图表是否可交互。如不可交互，则图表不会响应缩放、滑动、选择或点击等操作。默认值为true，可交互。
        mLineChartView.setInteractive(true);
        // 设置缩放类型，可选的类型包括：ZoomType.HORIZONTAL_AND_VERTICAL, ZoomType.HORIZONTAL, ZoomType.VERTICAL，默认值为HORIZONTAL_AND_VERTICAL。
        mLineChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        // 设置是否允许图表在父容器中滑动
        mLineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
    }

    public void setData(String recvDataString) {
        String[] strArray = convertLineToArray(recvDataString);
        LineChartData data = getLineChartData(strArray);
        mLineChartView.setLineChartData(data);
    }

    private String[] convertLineToArray(String lineInfo) {
        return lineInfo.split("\\[\\[|\\],\\[|\\]\\]");
    }

    private LineChartData getLineChartData(String[] strArray) {
        LineChartData data = new LineChartData();

        List<Line> lines = new ArrayList<>();
        lines.add(getLine(getPointValus(strArray)));
        data.setLines(lines);

        Axis axisX = getAxisX();
        axisX.setValues(getAxisValues(strArray));
        data.setAxisXBottom(axisX);

        Axis axisY = getAxisY();
        data.setAxisYLeft(axisY);

        return data;
    }

    private List<PointValue> getPointValus(String[] strArray) {
        List<PointValue> mPointValues = new ArrayList<>();
        int n = 0;
        for (int i = 0; i < strArray.length; i++) {
            if (strArray[i].length() == 0) {
                continue;
            }
            String[] strSub = strArray[i].split(",");
            mPointValues.add(new PointValue(n, Float.valueOf(strSub[1])));
            n++;
        }

        return mPointValues;
    }

    private Line getLine(List<PointValue> mPointValues) {
        Line line = new Line(mPointValues);
        line.setColor(Color.parseColor("#24CBE5"));   // 折线的颜色
        line.setShape(ValueShape.CIRCLE);             // 折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);                         // 曲线是否平滑，即是曲线还是折线
        line.setPointRadius(1);
//      line.setHasLabels(true);                      // 曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);       // 点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
//      line.setHasLines(false);                      // 是否用线显示。如果为false 则没有曲线只有点显示
//      line.setHasPoints(true);                      // 是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        return line;
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

    private List<AxisValue> getAxisValues(String[] strArray) {
        List<AxisValue> mAxisValues = new ArrayList<>();
        SimpleDateFormat sf;
        sf = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        int n = 0;
        for (int i = 0; i < strArray.length; i++) {
            if (strArray[i].length() == 0) {
                continue;
            }
            String[] strSub = strArray[i].split(",");
            date.setTime(Long.valueOf(strSub[0]));
            mAxisValues.add(new AxisValue(n).setLabel(sf.format(date)));
            n++;
        }

        return mAxisValues;
    }
}
