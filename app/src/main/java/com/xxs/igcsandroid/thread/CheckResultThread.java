package com.xxs.igcsandroid.thread;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xxs.igcsandroid.util.DlgUtil;

public class CheckResultThread extends Thread {
    private Activity mActivity;
    private Handler mHandler;
    private int mWhat;
    private String mTradeId;
    private int mCount;

    public CheckResultThread(Context context, Handler handler, int what) {
        mActivity = (Activity)context;
        mHandler = handler;
        mWhat = what;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);

            Message message = Message.obtain();
            message.what = mWhat;
            Bundle data = new Bundle();
            data.putString("tradeId", mTradeId);
            data.putInt("count", mCount);
            message.setData(data);
            mHandler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            DlgUtil.showExceptionPrompt(mActivity, e);
        }
    }

    public void setData(String tradeId, int nCnt) {
        mTradeId = tradeId;
        mCount = nCnt;
    }
}
