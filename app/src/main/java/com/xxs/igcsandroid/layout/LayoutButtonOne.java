package com.xxs.igcsandroid.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Button;

public class LayoutButtonOne {
    public static void setTextAndImg(Button btn, String text, Context ctx, int id) {
        btn.setText(text);
        Drawable drawable = ctx.getResources().getDrawable(id);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  // 设置边界
        btn.setCompoundDrawables(drawable, null, null, null);
    }
}
