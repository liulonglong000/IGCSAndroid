package com.xxs.igcsandroid.control;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxs.igcsandroid.R;

public class LayoutBottomItem extends LinearLayout {
    private ImageView iv_menu;
    private TextView tv_menu;
    private int imgEnable;
    private int imgDisable;

    public LayoutBottomItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater llInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = llInflater.inflate(R.layout.layout_bottom_item, this);
        iv_menu = view.findViewById(R.id.menu_iv);
        tv_menu = view.findViewById(R.id.menu_tv);
    }

    public void initItem(int imgEnable, int imgDisable, String strText) {
        this.imgEnable = imgEnable;
        this.imgDisable = imgDisable;
        tv_menu.setText(strText);
    }

    public void setEnable(boolean bEnable) {
        if (bEnable) {
            iv_menu.setImageResource(imgEnable);
            tv_menu.setTextColor(getResources().getColor(R.color.light_green));
        } else {
            iv_menu.setImageResource(imgDisable);
            tv_menu.setTextColor(getResources().getColor(R.color.white));
        }
    }
}
