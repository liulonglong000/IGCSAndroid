package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jaygoo.selector.MultiData;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.DignoseDetail;
import com.xxs.igcsandroid.layout.LayoutButtonOne;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AcCollectActivity extends AppCompatActivity {
    private String mGhId;
    private String mGhName;

    private RadioGroup collect_way;

    private TextView collect_row;
    private TextView location;

    private ImageView collect_image;
    private TextView message;

    private TextView collect_front;
    private TextView collect_back1;
    private TextView collect_back2;
    private TextView collect_back3;

    private Button next;
    private Button out;

    private TextView page;

    private ArrayList<DignoseDetail> dignoseDetailsList = new ArrayList<>();
    private int count;
    private static int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_list);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mGhId = bundle.getString("ghId");
        mGhName = bundle.getString("mGhName");

        setTitle(mGhName + "-病毒诊断");

        collect_way = findViewById(R.id.collect_way);
        collect_row = findViewById(R.id.collect_row);
        location = findViewById(R.id.location);
        collect_image = findViewById(R.id.collect_image);
        message = findViewById(R.id.message);
        collect_front = findViewById(R.id.collect_front);
        collect_back1 = findViewById(R.id.collect_back1);
        collect_back2 = findViewById(R.id.collect_back2);
        collect_back3 = findViewById(R.id.collect_back3);
        next = findViewById(R.id.next);
        out = findViewById(R.id.out);
        page = findViewById(R.id.page);

        collect_front.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        collect_back1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        collect_back2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        collect_back3.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);


        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = 1;
                Intent intent = new Intent(AcCollectActivity.this, AcDiagnosisListActivity.class);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getFromServer();
        if(index == count){
            next.setText("完成");
            next.setBackgroundColor(Color.GREEN);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    index = 1;
                    Intent intent = new Intent(AcCollectActivity.this, AcDiagnosisListActivity.class);
                    intent.putExtra("ghId", mGhId);
                    intent.putExtra("ghName", mGhName);
                    startActivity(intent);
                }
            });
        }else{
            next.setText("下一步");
            next.setBackgroundColor(Color.GRAY);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    index++;
                    Intent intent = new Intent(AcCollectActivity.this, AcCollectActivity.class);
                    intent.putExtra("mGhName", mGhName);
                    intent.putExtra("ghId", mGhId);
                    startActivity(intent);
                }
            });
        }
    }

    private void getFromServer() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(this, "autoctrl/queryDetail", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    dignoseDetailsList.clear();
                    for (int i = 1; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        DignoseDetail detail = new DignoseDetail(obj);
                        dignoseDetailsList.add(detail);
                    }
                    count = dignoseDetailsList.size();

                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(AcCollectActivity.this, e);
                }
                page.setText(index + "/" + count);
                collect_row.setText(String.valueOf(dignoseDetailsList.get(index-1).getCollRow()));
                location.setText(dignoseDetailsList.get(index-1).getCollArea());
            }
        }, null);
    }
}