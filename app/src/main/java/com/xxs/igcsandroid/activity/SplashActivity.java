package com.xxs.igcsandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.entity.UserRole;
import com.xxs.igcsandroid.util.CheckNetworkStateUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.LoginUtil;
import com.xxs.igcsandroid.util.StringUtil;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            PackageInfo pi = getPackageManager().getPackageInfo(this.getPackageName(), 0);
            ((TextView)findViewById(R.id.versionNumber)).setText("Version " + pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            DlgUtil.showMsgError(this, e.getLocalizedMessage());
        }

        if (CheckNetworkStateUtil.check(this)) {
            gotoNext();
        } else {
            DlgUtil.showMsgInfo(this, "抱歉，暂时无法处理您的请求，请检查您的网络连接！");
        }
    }

    private void gotoNext() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
                String userId = preferences.getString("userId", null);
                String password = preferences.getString("password", null);
                if (StringUtil.isStringNullOrEmpty(userId) || StringUtil.isStringNullOrEmpty(password)) {
                    gotoLogin();
                } else {
                    doAutoLogin(userId, password);
                }
            }
        }, 1000);
    }

    private void doAutoLogin(final String userId, final String password) {
        LoginUtil.doLogin(this, userId, password, new LoginUtil.OnLoginResult() {
            @Override
            public void OnGetLoginResult(boolean bSuccess) {
                if (bSuccess) {
                    MyApplication.getInstance().setLoginInfo(userId, password);

                    String roleInfo = MyApplication.getInstance().getUserRole();
                    if (roleInfo.equals(UserRole.ROLE_SYS_ADMIN)) {
                        Intent intent = new Intent(SplashActivity.this, MainSysAdminActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    } else if (roleInfo.equals(UserRole.ROLE_PARK_ADMIN)) {
                        Intent intent = new Intent(SplashActivity.this, MainParkAdminActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    } else if (roleInfo.equals(UserRole.ROLE_PARK_USER)) {
                        Intent intent = new Intent(SplashActivity.this, MainParkUserActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    }
                } else {
                    gotoLogin();
                }
            }
        });
    }

    private void gotoLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

}
