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
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.control.LayoutTextAndSelect;
import com.xxs.igcsandroid.entity.HistoryDataHolder;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WeatherHistoryFragment extends HistoryFragment {
    private LayoutTextAndSelect tasWeather;
    private List<MultiData> lstWeathers;
    private String mSelWeatherId;
    private View viewWeather;

    private boolean bHasGetFromSrv = false;
    private String mParkId;

    public WeatherHistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_history, container, false);

        Bundle data = getArguments();
        if (data != null && data.containsKey("parkId")) {
            mParkId = data.getString("parkId");
        }

        tasWeather = view.findViewById(R.id.tas_weather);
        tasWeather.init("气 象 站：");
        tasWeather.getTvSelect().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWeatherSelection();
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

        viewWeather = view.findViewById(R.id.mBottom);

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

    private void showWeatherSelection() {
        if (!bHasGetFromSrv) {
            getWeatherInfoFromSrv();
            return;
        }

        doShowWeatherSelection();
    }

    private void doShowWeatherSelection() {
        if (lstWeathers.size() == 0) {
            DlgUtil.showMsgInfo(mActivity, "没有可供选择的气象站！");
            return;
        } else if (lstWeathers.size() == 1) {
            mSelWeatherId = lstWeathers.get(0).getId();
            tasWeather.getTvSelect().setText(lstWeathers.get(0).getText());
            return;
        }

        new SignalSelectPopWindow.Builder(mActivity)
                .setDataArray(lstWeathers)
                .setConfirmListener(new SignalSelectPopWindow.OnConfirmClickListener() {
                    @Override
                    public void onClick(Integer indexSel) {
                        if (mSelWeatherId != null && mSelWeatherId.equals(lstWeathers.get(indexSel).getId())) {
                            return;
                        }

                        tasWeather.getTvSelect().setText(lstWeathers.get(indexSel).getText());
                        mSelWeatherId = lstWeathers.get(indexSel).getId();

                        for (int i = 0; i < lstWeathers.size(); i++) {
                            lstWeathers.get(i).setbSelect(false);
                        }
                        lstWeathers.get(indexSel).setbSelect(true);

                        clearSensorSelection();
                    }
                })
                .setTitle("气象站列表")
                .build()
                .show(viewWeather);
    }

    private void getWeatherInfoFromSrv() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", MyApplication.getInstance().getMyUserId());
        mp.put("parkId", mParkId);

        AsyncSocketUtil.post(mActivity, "gateway/getWeathers", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    bHasGetFromSrv = true;

                    lstWeathers = new ArrayList<>();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        lstWeathers.add(new MultiData(obj.getString("weatherId"), obj.getString("weatherName"), false));
                    }

                    doShowWeatherSelection();

                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }

    private void showSensorSelection() {
        if (mSelWeatherId == null) {
            DlgUtil.showMsgInfo(mActivity, "请先选择气象站！");
            return;
        }

        if (!mpSensors.containsKey(mSelWeatherId)) {
            getSensorInfoFromSrv();
            return;
        }

        doShowSensorSelection();
    }

    private void getSensorInfoFromSrv() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("weatherId", mSelWeatherId);

        AsyncSocketUtil.post(mActivity, "gateway/getWeatherSensors", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    List<MultiData> mpSensor = new ArrayList<>();

                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        mpSensor.add(new MultiData(obj.getString("sensorId"), obj.getString("sensorName"), false));
                    }

                    mpSensors.put(mSelWeatherId, mpSensor);

                    doShowSensorSelection();

                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }

    private void doShowSensorSelection() {
        final List<MultiData> lstSensors = mpSensors.get(mSelWeatherId);

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
                .show(viewWeather);
    }

    private void doQuery() {
        if (!CheckInputUtil.checkTextViewInput(tasWeather.getTvSelect(), mActivity, "请选择要查询的气象站！")) {
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
        mp.put("gwFilter", mSelWeatherId);
        mp.put("nodeFilter", "1");
        mp.put("sensorListFilter", getSensorList(mpSensors.get(mSelWeatherId)));
        mp.put("startTimeFilter", tasStartTime.getTvSelect().getText().toString());
        mp.put("endTimeFilter", tasEndTime.getTvSelect().getText().toString());
        mp.put("bWeather", "1");

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
                        intent.putExtra("title", tasWeather.getTvSelect().getText().toString() + "历史数据");
                        mActivity.startActivity(intent);
                    }
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }
}
