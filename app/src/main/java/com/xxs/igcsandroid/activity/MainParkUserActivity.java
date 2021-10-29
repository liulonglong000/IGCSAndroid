package com.xxs.igcsandroid.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.control.LayoutBottomItem;
import com.xxs.igcsandroid.fragment.MonitorParkUserFragment;
import com.xxs.igcsandroid.fragment.SettingParkUserFragment;
import com.xxs.igcsandroid.fragment.WeatherHistoryFragment;
import com.xxs.igcsandroid.fragment.WeatherParkAdminFragment;

public class MainParkUserActivity extends MainBaseActivity implements View.OnClickListener {
    private Fragment fragment_monitor;
    private Fragment fragment_weather;
    private Fragment fragment_weather_history;
    private Fragment fragment_setting;

    private LayoutBottomItem linear_monitor;
    private LayoutBottomItem linear_weather;
    private LayoutBottomItem linear_weather_history;
    private LayoutBottomItem linear_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_park_user);

        setTitle(getString(R.string.app_title) + "-" + MyApplication.getInstance().getMyUserId());

        linear_monitor = findViewById(R.id.menu_monitor);
        linear_monitor.initItem(R.drawable.ic_bm_greenhouse_enable, R.drawable.ic_bm_greenhouse_disable, "温室监控");
        linear_weather = findViewById(R.id.menu_weather);
        linear_weather.initItem(R.drawable.ic_bm_weather_enable, R.drawable.ic_bm_weather_disable, "气象监测");
        linear_weather_history = findViewById(R.id.menu_weather_history);
        linear_weather_history.initItem(R.drawable.ic_bm_weather_history_enable, R.drawable.ic_bm_weather_history_disable, "气象查询");
        linear_setting = findViewById(R.id.menu_setting);
        linear_setting.initItem(R.drawable.ic_bm_setting_enable, R.drawable.ic_bm_setting_disable, "系统设置");

        linear_monitor.setOnClickListener(this);
        linear_weather.setOnClickListener(this);
        linear_weather_history.setOnClickListener(this);
        linear_setting.setOnClickListener(this);

        linear_monitor.callOnClick();
    }

    @Override
    public void onClick(View view) {
        linear_monitor.setEnable(false);
        linear_weather.setEnable(false);
        linear_weather_history.setEnable(false);
        linear_setting.setEnable(false);

        // v4包下的Fragment，使用getSupportFragmentManager获取
        FragmentManager fragmentManager = getSupportFragmentManager();
        // 启动事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // 将所有的Fragment隐藏
        hideAllFragment(transaction);

        switch (view.getId()) {
            case R.id.menu_monitor:
                linear_monitor.setEnable(true);
                if (fragment_monitor == null){
                    fragment_monitor = new MonitorParkUserFragment();
                    transaction.add(R.id.frame_content, fragment_monitor);
                } else {
                    transaction.show(fragment_monitor);
                    fragment_monitor.onResume();
                }
                break;

            case R.id.menu_weather:
                linear_weather.setEnable(true);
                if (fragment_weather == null){
                    fragment_weather = new WeatherParkAdminFragment();
                    transaction.add(R.id.frame_content, fragment_weather);
                } else {
                    transaction.show(fragment_weather);
                    fragment_weather.onResume();
                }
                break;

            case R.id.menu_weather_history:
                linear_weather_history.setEnable(true);
                if (fragment_weather_history == null){
                    fragment_weather_history = new WeatherHistoryFragment();
                    transaction.add(R.id.frame_content, fragment_weather_history);
                } else {
                    transaction.show(fragment_weather_history);
                    fragment_weather_history.onResume();
                }
                break;

            case R.id.menu_setting:
                linear_setting.setEnable(true);
                if (fragment_setting == null){
                    fragment_setting = new SettingParkUserFragment();
                    transaction.add(R.id.frame_content, fragment_setting);
                } else {
                    transaction.show(fragment_setting);
                    fragment_setting.onResume();
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
        if (fragment_setting != null){
            transaction.hide(fragment_setting);
        }
    }
}
