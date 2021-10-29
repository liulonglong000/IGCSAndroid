package com.xxs.igcsandroid.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.xxs.igcsandroid.R;

public class DlgUtil {

    public static void showMsgWithOneButton(Context context, String title, String msg) {
        new AlertDialog.Builder(context).setTitle(title)
                //.setIcon(R.drawable.icon_ask)
                .setMessage(msg)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static void showMsgWithOneButton(Context context, String title, String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setTitle(title)
                //.setIcon(R.drawable.icon_ask)
                .setMessage(msg)
                .setNegativeButton("确定", listener).show();
    }

    public static void showMsgInfo(Context context, String msg) {
        showMsgWithOneButton(context, "提示", msg);
    }

    public static void showMsgInfo(Context context, String msg, DialogInterface.OnClickListener listener) {
        showMsgWithOneButton(context, "提示", msg, listener);
    }

    public static void showMsgWarn(Context context, String msg) {
        showMsgWithOneButton(context, "警告", msg);
    }

    public static void showMsgError(Context context, String msg) {
        showMsgWithOneButton(context, "错误", msg);
    }

    public static void showSocketPrompt(Context context, Throwable throwable) {
        showMsgWithOneButton(context, "提示", "通讯失败：" + throwable.getLocalizedMessage());
    }

    public static void showSocketPrompt(Context context, Throwable throwable, DialogInterface.OnClickListener listener) {
        showMsgWithOneButton(context, "提示", "通讯失败：" + throwable.getLocalizedMessage(), listener);
    }

    public static void showExceptionPrompt(Context context, Exception e) {
        showMsgWithOneButton(context, "提示", "异常：" + e.getLocalizedMessage());
    }

    public static void showAsk(Context ctx, String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(ctx).setTitle("提示")
                .setIcon(R.drawable.icon_ask)
                .setMessage(msg)
                .setPositiveButton("确定", listener)
                .setNegativeButton("取消", null)
                .show();
    }

    public static void showAsk(Context ctx, String msg, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener cancelListen) {
        new AlertDialog.Builder(ctx).setTitle("提示")
                .setIcon(R.drawable.icon_ask)
                .setMessage(msg)
                .setPositiveButton("确定", listener)
                .setNegativeButton("取消", cancelListen)
                .show();
    }
}
