package com.xxs.igcsandroid.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.xxs.igcsandroid.activity.UserPwdMdyActivity;
import com.xxs.igcsandroid.application.MyApplication;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class LoginUtil {
    public interface OnLoginResult {
        public void OnGetLoginResult(boolean bSuccess);
    }

    public static void doLogin(final Context ctx, String userId, String userPwd, final OnLoginResult loginResult) {
        AsyncSocketUtil.postLogin(ctx, "login/doLogin", userId, userPwd, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject msgObj = response.getJSONObject(0);
                    String msg = msgObj.getString("message");
                    if (msg.equals("success")) {
                        JSONObject jObj = response.getJSONObject(1);
                        MyApplication.getInstance().setToken(jObj.getString("token"));
                        MyApplication.getInstance().setUserRole(jObj.getString("userRole"));
                        loginResult.OnGetLoginResult(true);
                    } else {
                        DlgUtil.showMsgInfo(ctx, msg);
                        loginResult.OnGetLoginResult(false);
                    }
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(ctx, e);
                    loginResult.OnGetLoginResult(false);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                DlgUtil.showMsgInfo(ctx, errMsg);
                loginResult.OnGetLoginResult(false);
            }
        });
    }

    public static void saveLoginInfo(final Context ctx, String userId, String userPwd) {
        SharedPreferences userSettings = ctx.getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString("userId", userId);
        editor.putString("password", userPwd);
        editor.commit();
    }

    public static void saveLoginInfo(Context ctx, String userPwd) {
        SharedPreferences userSettings = ctx.getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString("password", userPwd);
        editor.commit();
    }

    public static void clearLoginInfo(final Context ctx) {
        SharedPreferences userSettings = ctx.getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString("userId", "");
        editor.putString("password", "");
        editor.commit();
    }
}
