package com.xxs.igcsandroid.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.fragment.MonitorSysAdminFragment;
import com.xxs.igcsandroid.fragment.SettingSysAdminFragment;

public class MainSysAdminActivity extends MainBaseActivity implements View.OnClickListener {
    // Fragment
    private Fragment fragment_monitor;
    private Fragment fragment_setting;

    // 底端菜单栏LinearLayout
    private LinearLayout linear_monitor;
    private LinearLayout linear_setting;

    // 底端菜单栏Imageview
    private ImageView iv_monitor;
    private ImageView iv_setting;

    // 底端菜单栏textView
    private TextView tv_monitor;
    private TextView tv_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sys_admin);

//        ActionBar mActionBar = getSupportActionBar();
//        mActionBar.setHomeAsUpIndicator(R.drawable.img_launcher_round);
//        mActionBar.setDisplayHomeAsUpEnabled(true);
//        mActionBar.setDisplayShowHomeEnabled(true);
//        mActionBar.setHomeButtonEnabled(true);

        setTitle(getString(R.string.app_title) + "-" + MyApplication.getInstance().getMyUserId());

        // 初始化各个控件
        InitView();

        // 初始化点击触发事件
        InitEvent();

        linear_monitor.callOnClick();
    }

    private void InitView() {
        linear_monitor = findViewById(R.id.menu_monitor);
        linear_setting = findViewById(R.id.menu_setting);

        iv_monitor = findViewById(R.id.menu_monitor_iv);
        iv_setting = findViewById(R.id.menu_setting_iv);

        tv_monitor = findViewById(R.id.menu_monitor_tv);
        tv_setting = findViewById(R.id.menu_setting_tv);
    }

    private void InitEvent() {
        // 设置LinearLayout监听
        linear_monitor.setOnClickListener(this);
        linear_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // 每次点击之后，将所有的ImageView和TextView设置为未选中
        restartButton();
        // 再根据所选，进行跳转页面，并将ImageView和TextView设置为选中
        switch(view.getId()){
            case R.id.menu_monitor:
                iv_monitor.setImageResource(R.drawable.ic_park_manage);
                tv_monitor.setTextColor(getResources().getColor(R.color.light_green));
                InitFragment(1);
                break;

            case R.id.menu_setting:
                iv_setting.setImageResource(R.drawable.ic_bm_setting_enable);
                tv_setting.setTextColor(getResources().getColor(R.color.light_green));
                InitFragment(4);
                break;
        }
    }

    private void InitFragment(int index) {
        // v4包下的Fragment，使用getSupportFragmentManager获取
        FragmentManager fragmentManager = getSupportFragmentManager();
        // 启动事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // 将所有的Fragment隐藏
        hideAllFragment(transaction);
        switch (index){
            case 1:
                if (fragment_monitor == null){
                    fragment_monitor = new MonitorSysAdminFragment();
                    transaction.add(R.id.frame_content, fragment_monitor);
                } else{
                    transaction.show(fragment_monitor);
                    fragment_monitor.onResume();
                }
                break;

            case 4:
                if (fragment_setting == null){
                    fragment_setting = new SettingSysAdminFragment();
                    transaction.add(R.id.frame_content, fragment_setting);
                } else{
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
        if (fragment_setting != null){
            transaction.hide(fragment_setting);
        }
    }

    private void restartButton() {
        // 设置为未点击状态
        iv_monitor.setImageResource(R.drawable.ic_bm_park_disable);
        iv_setting.setImageResource(R.drawable.ic_bm_setting_disable);

        // 设置为灰色
        tv_monitor.setTextColor(getResources().getColor(R.color.white));
        tv_setting.setTextColor(getResources().getColor(R.color.white));
    }
}
