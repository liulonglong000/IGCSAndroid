package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.control.LayoutBottomItem;
import com.xxs.igcsandroid.fragment.MonitorParkAdminFragmentEx;
import com.xxs.igcsandroid.fragment.WeatherHistoryFragment;
import com.xxs.igcsandroid.fragment.WeatherParkAdminFragment;

public class GatewayShowActivity extends AppCompatActivity implements View.OnClickListener {
    private String mParkId;

    private Fragment fragment_monitor;
    private Fragment fragment_weather;
    private Fragment fragment_weather_history;

    private LayoutBottomItem linear_monitor;
    private LayoutBottomItem linear_weather;
    private LayoutBottomItem linear_weather_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_show);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mParkId = bundle.getString("parkId");

        setTitle(bundle.getString("parkName"));

        linear_monitor = findViewById(R.id.menu_monitor);
        linear_monitor.initItem(R.drawable.ic_bm_greenhouse_enable, R.drawable.ic_bm_greenhouse_disable, "温室监控");
        linear_monitor.setOnClickListener(this);

        linear_weather = findViewById(R.id.menu_weather);
        linear_weather.initItem(R.drawable.ic_bm_weather_enable, R.drawable.ic_bm_weather_disable, "气象监测");
        linear_weather.setOnClickListener(this);

        linear_weather_history = findViewById(R.id.menu_weather_history);
        linear_weather_history.initItem(R.drawable.ic_bm_weather_history_enable, R.drawable.ic_bm_weather_history_disable, "气象查询");
        linear_weather_history.setOnClickListener(this);

        linear_monitor.callOnClick();
    }

    @Override
    public void onClick(View v) {
        linear_monitor.setEnable(false);
        linear_weather.setEnable(false);
        linear_weather_history.setEnable(false);

        // v4包下的Fragment，使用getSupportFragmentManager获取
        FragmentManager fragmentManager = getSupportFragmentManager();
        // 启动事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // 将所有的Fragment隐藏
        hideAllFragment(transaction);

        switch(v.getId()){
            case R.id.menu_monitor:
                linear_monitor.setEnable(true);
                if (fragment_monitor == null){
                    fragment_monitor = new MonitorParkAdminFragmentEx();
                    Bundle data = new Bundle();
                    data.putString("parkId", mParkId);
                    fragment_monitor.setArguments(data);
                    transaction.add(R.id.frame_content, fragment_monitor);
                } else{
                    transaction.show(fragment_monitor);
                    fragment_monitor.onResume();
                }
                break;

            case R.id.menu_weather:
                linear_weather.setEnable(true);
                if (fragment_weather == null){
                    fragment_weather = new WeatherParkAdminFragment();
                    Bundle data = new Bundle();
                    data.putString("parkId", mParkId);
                    fragment_weather.setArguments(data);
                    transaction.add(R.id.frame_content, fragment_weather);
                } else{
                    transaction.show(fragment_weather);
                    fragment_weather.onResume();
                }
                break;

            case R.id.menu_weather_history:
                linear_weather_history.setEnable(true);
                if (fragment_weather_history == null){
                    fragment_weather_history = new WeatherHistoryFragment();
                    Bundle data = new Bundle();
                    data.putString("parkId", mParkId);
                    fragment_weather_history.setArguments(data);
                    transaction.add(R.id.frame_content, fragment_weather_history);
                } else {
                    transaction.show(fragment_weather_history);
                    fragment_weather_history.onResume();
                }
                break;
        }

        // 提交事务
        transaction.commit();
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        if (fragment_monitor != null){
            transaction.hide(fragment_monitor);
        }
        if (fragment_weather != null){
            transaction.hide(fragment_weather);
        }
        if (fragment_weather_history != null){
            transaction.hide(fragment_weather_history);
        }
    }
}
