package com.xxs.igcsandroid.util;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xxs.igcsandroid.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimePickDialogUtil implements DatePicker.OnDateChangedListener,
        TimePicker.OnTimeChangedListener {
    private DatePicker datePicker;
    private TimePicker timePicker;
    private AlertDialog ad;
    private String dateTime;
    private String initDateTime;
    private Activity activity;

    /**
     * 日期时间弹出选择框构造函数
     *
     * @param activity
     *            ：调用的父activity
     * @param initDateTime
     *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
     */
    public DateTimePickDialogUtil(Activity activity, String initDateTime) {
        this.activity = activity;
        this.initDateTime = initDateTime;
    }

    public void init(DatePicker datePicker, TimePicker timePicker) throws Exception {
        Calendar calendar = Calendar.getInstance();
        if (!(null == initDateTime || "".equals(initDateTime))) {
            calendar = this.getCalendarByInintData(initDateTime);
        } else {
            initDateTime = calendar.get(Calendar.YEAR) + "-"
                    + calendar.get(Calendar.MONTH) + "-"
                    + calendar.get(Calendar.DAY_OF_MONTH) + " "
                    + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE);
        }

        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    public AlertDialog dateTimePicKDialog(final TextView inputDate) throws Exception {
        LinearLayout dateTimeLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_date_time_picker, null);
        datePicker = dateTimeLayout.findViewById(R.id.datepicker);
        timePicker = dateTimeLayout.findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);
        init(datePicker, timePicker);
        timePicker.setOnTimeChangedListener(this);

        Button btnOK = dateTimeLayout.findViewById(R.id.btn_ok);
        Button btnCancel = dateTimeLayout.findViewById(R.id.btn_cancel);

        ad = new AlertDialog.Builder(activity)
                .setView(dateTimeLayout)
                .show();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDate.setText(dateTime);
                ad.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });

        onDateChanged(null, 0, 0, 0);
        return ad;
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        onDateChanged(null, 0, 0, 0);
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (timePicker.getVisibility() == View.VISIBLE) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(datePicker.getYear(), datePicker.getMonth(),
                    datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                    timePicker.getCurrentMinute());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            dateTime = sdf.format(calendar.getTime());
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(datePicker.getYear(), datePicker.getMonth(),
                    datePicker.getDayOfMonth(), 0,
                    0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateTime = sdf.format(calendar.getTime());
        }
    }

    private Calendar getCalendarByInintData(String initDateTime) throws Exception {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = sf.parse(initDateTime);
        calendar.setTime(date);
        return calendar;
    }

    /* 以下是仅显示时间控件 */
    public AlertDialog timePicKDialog(final TextView inputDate) throws Exception {
        LinearLayout dateTimeLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_date_time_picker, null);

        datePicker = dateTimeLayout.findViewById(R.id.datepicker);
        datePicker.setVisibility(View.GONE);

        timePicker = dateTimeLayout.findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(Integer.valueOf(initDateTime.substring(0, 2)));
        timePicker.setCurrentMinute(Integer.valueOf(initDateTime.substring(3, 5)));
        dateTime = initDateTime;
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay < 10 && minute < 10) {
                    dateTime = "0" + hourOfDay + ":" + "0" + minute;
                } else if (hourOfDay < 10 && minute > 10) {
                    dateTime = "0" + hourOfDay + ":" + minute;
                } else if (hourOfDay > 10 && minute < 10) {
                    dateTime = hourOfDay + ":" + "0" + minute;
                } else {
                    dateTime = hourOfDay + ":" + minute;
                }
            }
        });

        Button btnOK = dateTimeLayout.findViewById(R.id.btn_ok);
        Button btnCancel = dateTimeLayout.findViewById(R.id.btn_cancel);

        ad = new AlertDialog.Builder(activity)
                .setView(dateTimeLayout)
                .show();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDate.setText(dateTime);
                ad.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });

        return ad;
    }

    /* 以下是仅显示日期控件 */
    public AlertDialog datePicKDialog(final TextView inputDate) {
        LinearLayout dateTimeLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_date_time_picker, null);

        timePicker = dateTimeLayout.findViewById(R.id.timepicker);
        timePicker.setVisibility(View.GONE);

        datePicker = dateTimeLayout.findViewById(R.id.datepicker);
        datePicker.init(Integer.valueOf(initDateTime.substring(0, 4)),
                Integer.valueOf(initDateTime.substring(5, 7)) - 1,
                Integer.valueOf(initDateTime.substring(8, 10)), this);

        dateTime = initDateTime;

        Button btnOK = dateTimeLayout.findViewById(R.id.btn_ok);
        Button btnCancel = dateTimeLayout.findViewById(R.id.btn_cancel);

        ad = new AlertDialog.Builder(activity)
                .setView(dateTimeLayout)
                .show();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDate.setText(dateTime);
                ad.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });

        onDateChanged(null, 0, 0, 0);

        return ad;
    }
}
