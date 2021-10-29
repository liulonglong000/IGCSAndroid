package com.xxs.igcsandroid.util;

import android.content.Context;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommuCmdUtil {
    public interface OnGetTradeResult {
        public void OnSuccess(String result);
        public void OnFail(String errMsg);
        public void OnContinue();
    }

    public static void getTradeResult(final Context ctx, final String tradeId, final int nCount,
                                      final OnGetTradeResult sResult) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("tradeId", tradeId);

        AsyncSocketUtil.post(ctx, "autoctrl/checkTradeResult", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    JSONObject msgObj = response.getJSONObject(0);
                    String msg = msgObj.getString("message");
                    if (msg.equals("success")) {
                        String result = response.getJSONObject(1).getString("result");
                        if (result.equals("0") || result.equals("2")) {
                            if (nCount < 21) {
                                sResult.OnContinue();
                            } else {
                                sResult.OnFail("没有反应，请联系系统管理员！");
                            }
                        } else if (result.equals("1") || result.equals("H")) {
                            sResult.OnSuccess(result);
                        } else {
                            sResult.OnFail(response.getJSONObject(1).getString("remark"));
                        }
                    } else {
                        sResult.OnFail(msg);
                    }
                } catch (JSONException e) {
                    sResult.OnFail(e.getLocalizedMessage());
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                sResult.OnFail(errMsg);
            }
        });
    }
}
