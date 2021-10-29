package com.xxs.igcsandroid.util;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.TextView;

public class CheckInputUtil {
    public static boolean checkTextViewInput(TextView tv, Context context, String msg) {
        if (TextUtils.isEmpty(tv.getText().toString().trim())) {
            DlgUtil.showMsgInfo(context, msg);
            tv.requestFocus();
            return false;
        }
        return true;
    }
}
