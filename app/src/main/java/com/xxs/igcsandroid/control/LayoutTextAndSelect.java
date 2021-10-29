package com.xxs.igcsandroid.control;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxs.igcsandroid.R;

public class LayoutTextAndSelect extends LinearLayout {
    private TextView tvInfo;
    private TextView tvSelect;

    public LayoutTextAndSelect(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater llInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = llInflater.inflate(R.layout.layout_text_and_select, this);
        tvInfo = view.findViewById(R.id.tv_info);
        tvSelect = view.findViewById(R.id.tv_select);
    }

    public void init(String strInfo) {
        tvInfo.setText(strInfo);
    }

    public TextView getTvSelect() {
        return tvSelect;
    }
}
