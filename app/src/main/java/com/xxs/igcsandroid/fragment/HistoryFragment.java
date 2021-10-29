package com.xxs.igcsandroid.fragment;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.RadioButton;

import com.jaygoo.selector.MultiData;
import com.xxs.igcsandroid.control.LayoutTextAndSelect;
import com.xxs.igcsandroid.util.DateTimePickDialogUtil;
import com.xxs.igcsandroid.util.DlgUtil;
import com.xxs.igcsandroid.util.HandleCacheInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends BaseFragment {
    protected LayoutTextAndSelect tasSensor;
    protected LayoutTextAndSelect tasStartTime;
    protected LayoutTextAndSelect tasEndTime;
    protected RadioButton rbTable;
    protected RadioButton rbPic;
    protected Map<String, List<MultiData>> mpSensors = new LinkedHashMap<>();

    public HistoryFragment() {

    }

    protected void clickedShowStartTime() {
        try {
            DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(mActivity, getStartTime());
            dateTimePicKDialog.dateTimePicKDialog(tasStartTime.getTvSelect());
        } catch (Exception e) {
            DlgUtil.showExceptionPrompt(mActivity, e);
        }
    }

    protected void clickedShowEndTime() {
        try {
            DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(mActivity, getEndTime());
            dateTimePicKDialog.dateTimePicKDialog(tasEndTime.getTvSelect());
        } catch (Exception e) {
            DlgUtil.showExceptionPrompt(mActivity, e);
        }
    }

    private String getStartTime() throws Exception {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String start = tasStartTime.getTvSelect().getText().toString();
        if (start == null || start.length() == 0) {
            String end = tasEndTime.getTvSelect().getText().toString();
            Long time;
            if (end == null || end.length() == 0) {
                time = System.currentTimeMillis();
            } else {
                Date date = sf.parse(end);
                time = date.getTime();
            }
            time -= 24 * 3600 * 1000;
            return sf.format(new Date(time));
        } else {
            return start;
        }
    }

    private String getEndTime() throws Exception {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String end = tasEndTime.getTvSelect().getText().toString();
        if (end == null || end.length() == 0) {
            String start = tasStartTime.getTvSelect().getText().toString();
            Calendar calendar = Calendar.getInstance();
            Long time = calendar.getTimeInMillis();
            if (start != null && start.length() > 0) {
                Date date = sf.parse(start);
                time = date.getTime();
                time += 24 * 3600 * 1000;
                if (time > calendar.getTimeInMillis()) {
                    time = calendar.getTimeInMillis();
                }
            }
            calendar.setTimeInMillis(time);
            return sf.format(calendar.getTime());
        } else {
            return end;
        }
    }

    protected void initShowInfo() {
        boolean bShow = HandleCacheInfo.getHistoryShow(mActivity);
        if (bShow == HandleCacheInfo.HISTORY_SHOW_PIC) {
            rbPic.setChecked(true);
        } else {
            rbTable.setChecked(true);
        }
    }

    protected void saveShowInfo() {
        if (rbTable.isChecked()) {
            HandleCacheInfo.setHistoryShow(mActivity, HandleCacheInfo.HISTORY_SHOW_TABLE);
        } else {
            HandleCacheInfo.setHistoryShow(mActivity, HandleCacheInfo.HISTORY_SHOW_PIC);
        }
    }

    protected boolean isTimeOK() {
        String startTime = tasStartTime.getTvSelect().getText().toString();
        if (TextUtils.isEmpty(startTime)) {
            DlgUtil.showMsgInfo(mActivity, "请选择开始时间！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clickedShowStartTime();
                }
            });
            return false;
        }
        String endTime = tasEndTime.getTvSelect().getText().toString();
        if (TextUtils.isEmpty(endTime)) {
            DlgUtil.showMsgInfo(mActivity, "请选择结束时间！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clickedShowEndTime();
                }
            });
            return false;
        }
        if (startTime.compareTo(endTime) >= 0) {
            DlgUtil.showMsgInfo(mActivity, "结束时间需晚于开始时间！");
            return false;
        }

        return true;
    }

    protected String getDatwFormat() {
        String startTime = tasStartTime.getTvSelect().getText().toString();
        String endTime = tasEndTime.getTvSelect().getText().toString();

        String startPrefix = startTime.substring(0, 10);
        String endPrefix = endTime.substring(0, 10);
        if (startPrefix.equals(endPrefix)) {
            return "HH:mm";
        } else {
            return "MM-dd HH:mm";
        }
    }

    protected String getSensorList(List<MultiData> lstS) {
        String strSensors = "";

        for (MultiData data : lstS) {
            if (data.isbSelect()) {
                if (strSensors.length() > 0) {
                    strSensors += ",";
                }
                strSensors += data.getId();
            }
        }

        return strSensors;
    }

    protected void clearSensorSelection() {
        tasSensor.getTvSelect().setText("");

        for (Map.Entry<String, List<MultiData>> mp : mpSensors.entrySet()) {
            List<MultiData> lst = mp.getValue();
            for (MultiData data : lst) {
                data.setbSelect(false);
            }
        }
    }
}
