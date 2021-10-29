package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.adapter.HistoryDataAdapter;
import com.xxs.igcsandroid.entity.HistoryDataEntity;
import com.xxs.igcsandroid.entity.HistoryDataHolder;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class HistoryTableActivity extends AppCompatActivity {
    private Bundle bundle;
    private LinearLayout llHeaderParent;
    private HistoryDataAdapter mTableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_table);

        llHeaderParent = findViewById(R.id.ll_header_parent);
        TextView tvHeaderId = findViewById(R.id.header_id);
        ViewGroup.LayoutParams lp = tvHeaderId.getLayoutParams();
        lp.width = 130;
        tvHeaderId.setLayoutParams(lp);
        TextView tvHeaderTime = findViewById(R.id.header_time);
        lp = tvHeaderTime.getLayoutParams();
        lp.width = 300;
        tvHeaderTime.setLayoutParams(lp);

        mTableAdapter = new HistoryDataAdapter(this);
        ListView mTableShow = findViewById(R.id.table_show);
        mTableShow.setAdapter(mTableAdapter);

        Intent curIntent = getIntent();
        bundle = curIntent.getExtras();
        setTitle(bundle.getString("title"));

        loadTable();
    }

    private void loadTable() {
        try {
            int n;
            String type;
            String dataId = bundle.getString("dataId");
            JSONArray response = (JSONArray) HistoryDataHolder.getInstance().retrieve(dataId);

            for (n = 1; n < response.length(); n++) {
                JSONObject lineObj = response.getJSONObject(n);
                type = lineObj.getString("type");

                TextView tvHeader = (TextView) LayoutInflater.from(this).inflate(R.layout.layout_history_table_header, null);
                tvHeader.setText(type);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT);
                llHeaderParent.addView(tvHeader, params);
            }

            float fVal = 0;
            Set<String> setTimeStr = getXAxisData(response);
            ArrayList<HistoryDataEntity> lstEntity = new ArrayList<>();
            for (String time : setTimeStr) {
                HistoryDataEntity entity = new HistoryDataEntity(time);
                lstEntity.add(entity);
            }

            int i;
            for (i = 1; i < response.length(); i++) {
                JSONObject lineObj = response.getJSONObject(i);
                String lineInfo = lineObj.getString("data");
                Map<String, Float> mpLine = convertLineStringToMap(lineInfo);

                for (HistoryDataEntity entity : lstEntity) {
                    if (mpLine.containsKey(entity.getTime())) {
                        fVal = mpLine.get(entity.getTime());
                        entity.getSensorData().put(i, fVal);
                    }
                }
            }

            mTableAdapter.setItemList(lstEntity, i - 1);
            mTableAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            DlgUtil.showExceptionPrompt(HistoryTableActivity.this, e);
        }
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
