package com.xxs.igcsandroid.golbal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.xxs.igcsandroid.activity.ParkAdminManageActivity;
import com.xxs.igcsandroid.activity.UserAddActivity;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommonRequest {
    private Activity mActivity;

    public CommonRequest(Activity context) {
        this.mActivity = context;
    }

    public interface onSuccessJSONObject {
        public void OnJSONObjectResult(JSONObject response);
    }

    public void checkParkExist(String userId, final onSuccessJSONObject cb) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", userId);

        AsyncSocketUtil.post(mActivity, "user/getParkInfo", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject msgObj = response.getJSONObject(0);
                    String msg = msgObj.getString("message");
                    if (msg.equals("success")) {
                        JSONObject dataObj = response.getJSONObject(1);
                        cb.OnJSONObjectResult(dataObj);
                    } else if (msg.equals("add")) {
                        DlgUtil.showMsgInfo(mActivity, "请先添加园区信息！");
                    }
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }
}
