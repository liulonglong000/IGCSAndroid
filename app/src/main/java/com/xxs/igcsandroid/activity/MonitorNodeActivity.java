package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.ezuikit.PlayActivity;
import com.xxs.igcsandroid.fragment.NodeAutoctrlFragment;
import com.xxs.igcsandroid.fragment.NodeControlFragment;
import com.xxs.igcsandroid.fragment.NodeDataFragment;
import com.xxs.igcsandroid.fragment.NodeHistoryFragment;

public class MonitorNodeActivity extends MainBaseActivity implements View.OnClickListener {
    // Fragment
    private Fragment fragment_monitor;
    private Fragment fragment_control;
    private Fragment fragment_history;
    private Fragment fragment_autocrel;

    // 底端菜单栏LinearLayout
    private LinearLayout linear_monitor;
    private LinearLayout linear_control;
    private LinearLayout linear_history;
    private LinearLayout linear_autoctrl;

    // 底端菜单栏Imageview
    private ImageView iv_monitor;
    private ImageView iv_control;
    private ImageView iv_history;
    private ImageView iv_setting;

    // 底端菜单栏textView
    private TextView tv_monitor;
    private TextView tv_control;
    private TextView tv_history;
    private TextView tv_setting;

    private String mGhId;
    private String mGwId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_node);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mGhId = bundle.getString("ghId");
        setTitle(bundle.getString("ghName"));
        mGwId = bundle.getString("gwId");

        // 初始化各个控件
        InitView();

        // 初始化点击触发事件
        InitEvent();

        linear_monitor.callOnClick();
    }

    private void InitView() {
        linear_monitor = findViewById(R.id.menu_monitor);
        linear_control = findViewById(R.id.menu_control);
        linear_history = findViewById(R.id.menu_history);
        linear_autoctrl = findViewById(R.id.menu_setting);

        iv_monitor = findViewById(R.id.menu_monitor_iv);
        iv_control = findViewById(R.id.menu_control_iv);
        iv_history = findViewById(R.id.menu_history_iv);
        iv_setting = findViewById(R.id.menu_setting_iv);

        tv_monitor = findViewById(R.id.menu_monitor_tv);
        tv_control = findViewById(R.id.menu_control_tv);
        tv_history = findViewById(R.id.menu_history_tv);
        tv_setting = findViewById(R.id.menu_setting_tv);
    }

    private void InitEvent() {
        // 设置LinearLayout监听
        linear_monitor.setOnClickListener(this);
        linear_control.setOnClickListener(this);
        linear_history.setOnClickListener(this);
        linear_autoctrl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // 每次点击之后，将所有的ImageView和TextView设置为未选中
        restartButton();
        // 再根据所选，进行跳转页面，并将ImageView和TextView设置为选中
        switch (v.getId()){
            case R.id.menu_monitor:
                iv_monitor.setImageResource(R.drawable.ic_monitor_enable);
                tv_monitor.setTextColor(getResources().getColor(R.color.light_green));
                InitFragment(1);
                break;

            case R.id.menu_control:
                iv_control.setImageResource(R.drawable.ic_control_enable);
                tv_control.setTextColor(getResources().getColor(R.color.light_green));
                InitFragment(2);
                break;

            case R.id.menu_history:
                iv_history.setImageResource(R.drawable.ic_history_enable);
                tv_history.setTextColor(getResources().getColor(R.color.light_green));
                InitFragment(3);
                break;

            case R.id.menu_setting:
                iv_setting.setImageResource(R.drawable.ic_autoctrl_enable);
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
                    fragment_monitor = new NodeDataFragment();
                    Bundle data = new Bundle();
                    data.putString("ghId", mGhId);
                    fragment_monitor.setArguments(data);
                    transaction.add(R.id.frame_content, fragment_monitor);
                } else{
                    transaction.show(fragment_monitor);
                    fragment_monitor.onResume();
                }
                break;

            case 2:
                if (fragment_control == null){
                    fragment_control = new NodeControlFragment();
                    Bundle data = new Bundle();
                    data.putString("ghId", mGhId);
                    fragment_control.setArguments(data);
                    transaction.add(R.id.frame_content, fragment_control);
                } else{
                    transaction.show(fragment_control);
                    fragment_control.onResume();
                }
                break;

            case 3:
                if (fragment_history == null){
                    fragment_history = new NodeHistoryFragment();
                    Bundle data = new Bundle();
                    data.putString("ghId", mGhId);
                    fragment_history.setArguments(data);
                    transaction.add(R.id.frame_content, fragment_history);
                } else{
                    transaction.show(fragment_history);
                    fragment_history.onResume();
                }
                break;

            case 4:
                if (fragment_autocrel == null){
                    fragment_autocrel = new NodeAutoctrlFragment();
                    Bundle data = new Bundle();
                    data.putString("ghId", mGhId);
                    data.putString("gwId", mGwId);
                    data.putString("ghName", getTitle().toString());
                    fragment_autocrel.setArguments(data);
                    transaction.add(R.id.frame_content, fragment_autocrel);
                } else{
                    transaction.show(fragment_autocrel);
                    fragment_autocrel.onResume();
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
        if (fragment_control != null){
            transaction.hide(fragment_control);
        }
        if (fragment_history != null){
            transaction.hide(fragment_history);
        }
        if (fragment_autocrel != null){
            transaction.hide(fragment_autocrel);
        }
    }

    private void restartButton() {
        // 设置为未点击状态
        iv_monitor.setImageResource(R.drawable.ic_monitor_disable);
        iv_control.setImageResource(R.drawable.ic_control_disable);
        iv_history.setImageResource(R.drawable.ic_history_disable);
        iv_setting.setImageResource(R.drawable.ic_autoctrl_disable);

        // 设置为灰色
        tv_monitor.setTextColor(getResources().getColor(R.color.white));
        tv_control.setTextColor(getResources().getColor(R.color.white));
        tv_history.setTextColor(getResources().getColor(R.color.white));
        tv_setting.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_video:
//                Intent intent = new Intent(this, VideoMonitorActivity.class);
//                intent.putExtra("ghId", mGhId);
//                intent.putExtra("ghName", getTitle());
//                startActivity(intent);

                String mAppKey = "a09a0230e3cf491d9bfb2e806da355d5";
                String mAccessToken = "at.aeuhin2jbqerclgg62gbtpo8cnkbm0c3-26jtrzt9qn-1hw2uue-ky2arqfjl";
                String mUrl = "ezopen://open.ys7.com/E51489600/1.hd.live";

                PlayActivity.startPlayActivity(this, mAppKey, mAccessToken, mUrl);

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
