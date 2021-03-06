package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.layout.LayoutButtonOne;

public class AcAppStrategyMgrActivity extends AppCompatActivity {
    private String mGhId;
    private String mGhName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_app_strategy_mgr);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mGhId = bundle.getString("ghId");
        mGhName = bundle.getString("ghName");

        setTitle(mGhName + "->上位机策略管理");

        Button btn = findViewById(R.id.btn_threshold_group_manage);
        LayoutButtonOne.setTextAndImg(btn, "阈值组管理", this, R.drawable.ic_autoctrl_threshold_group_manage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AcAppStrategyMgrActivity.this, AcThresholdGroupMgrActivity.class);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                startActivity(intent);
            }
        });

        btn = findViewById(R.id.btn_operation_group_manage);
        LayoutButtonOne.setTextAndImg(btn, "操作组管理", this, R.drawable.ic_autoctrl_operation_group_manage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AcAppStrategyMgrActivity.this, AcOperationGroupMgrActivity.class);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                startActivity(intent);
            }
        });

        btn = findViewById(R.id.btn_strategy_manage);
        LayoutButtonOne.setTextAndImg(btn, "策略管理", this, R.drawable.ic_autoctrl_strategy_manage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AcAppStrategyMgrActivity.this, AcStrategyMgrActivity.class);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                startActivity(intent);
            }
        });
        btn = findViewById(R.id.btn_virus_diagnosis);
        LayoutButtonOne.setTextAndImg(btn, "病毒诊断", this, R.drawable.ic_autoctrl_virus_diagnosis);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AcAppStrategyMgrActivity.this, AcDiagnosisListActivity.class);
                intent.putExtra("ghId", mGhId);
                intent.putExtra("ghName", mGhName);
                startActivity(intent);
            }
        });
    }
}