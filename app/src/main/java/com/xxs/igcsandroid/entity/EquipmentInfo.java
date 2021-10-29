package com.xxs.igcsandroid.entity;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.util.CheckInputUtil;
import com.xxs.igcsandroid.util.DecimalDigitsInputFilter;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class EquipmentInfo {
    private String gwId;
    private String nodeId;
    private String nodeName;

    private String equiId;
    private String equiType;
    private String equiName;
    private String equiStatus;
    private String equiStatusString;
    private Integer equiStatusPara;
    private Integer equiPara;
    private Float equiLocate;
    private String equipPara3;

    private String connectTime;

    private Activity mActivity;
    private View llEquip;
    private AlertDialog ad;

    private String operatinMsg;

    public interface onOKClicked {
        void toAddTrade(String tradeId, Map<String, String> mpTradeInfo);
    }

    public EquipmentInfo(JSONObject obj, String gwId, String nodeId, String nodeName, Activity mActivity) throws JSONException {
        this.gwId = gwId;
        this.nodeId = nodeId;
        this.nodeName = nodeName;

        this.equiId = obj.getString("equiId");
        this.equiType = obj.getString("equiTypeString");
        this.equiName = obj.getString("equiName");
        this.equiStatus = obj.getString("equiStatus");
        this.equiStatusString = obj.getString("equiStatusString");
        if (obj.has("equiStatusPara")) {
            this.equiStatusPara = obj.getInt("equiStatusPara");
        } else {
            this.equiStatusPara = 0;
        }
        if (obj.has("equiPara")) {
            this.equiPara = obj.getInt("equiPara");
        } else {
            this.equiPara = 0;
        }
        this.equiLocate = (float)(obj.getDouble("equipLocate"));

        connectTime = "";
        equipPara3 = obj.getString("equipPara3");

        this.mActivity = mActivity;
    }

    public int getImageId() {
        if (equiType.equals("LED")) {
            return R.drawable.ic_equipment_led;
        } else if (equiType.equals("DRIP")) {
            return R.drawable.ic_equipment_drip;
        } else if (equiType.equals("FERT")) {
            return R.drawable.ic_equipment_fert;
        } else if (equiType.equals("ROWF")) {
            return R.drawable.ic_equipment_rowf;
        } else if (equiType.equals("WAVE")) {
            return R.drawable.ic_equipment_wave;
        } else if (equiType.equals("PUSH")) {
            return R.drawable.ic_equipment_push;
        } else if (equiType.equals("RBM")) {
            return R.drawable.ic_equipment_rbm;
        } else if (equiType.equals("FRM")) {
            return R.drawable.ic_equipment_frm;
        } else if (equiType.equals("HOT")) {
            return R.drawable.ic_equipment_hot;
        } else {
            return R.drawable.ic_equipment_led;
        }
    }

    public String getEquipFullName() {
        return equiName + equiId;
    }

    public String getEquipFullStatus() {
        if (equiType.equals("RBM") || equiType.equals("PUSH")) {
            if (equiLocate != -1) {
                if (equipPara3.length() == 0) {
                    return equiStatusString;
                } else {
                    return equiStatusString + "(" + equiLocate + "%" + ")";
                }
            } else {
                return equiStatusString;
            }
        } else {
            return equiStatusString;
        }
//        if (equiType.equals("RBM") || equiType.equals("PUSH")) {
//            return equiStatusString + "(" + equiStatusPara + "/" + equiPara + ")";
//        } else {
//            return equiStatusString;
//        }
    }

    public View getLlEquip() {
        return llEquip;
    }

    public void setLlEquip(View llEquip) {
        this.llEquip = llEquip;
    }

    public String getGwId() {
        return gwId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getEquiType() {
        return equiType;
    }

    public String getEquiId() {
        return equiId;
    }

    public void updateStatusInfo(JSONObject obj) throws JSONException {
        this.equiStatus = obj.getString("equiStatus");
        this.equiStatusString = obj.getString("equiStatusString");
        if (obj.has("equiStatusPara")) {
            this.equiStatusPara = obj.getInt("equiStatusPara");
        } else {
            this.equiStatusPara = 0;
        }
        if (obj.has("equiPara")) {
            this.equiPara = obj.getInt("equiPara");
        } else {
            this.equiPara = 0;
        }
        this.equiLocate = (float)(obj.getDouble("equipLocate"));
        connectTime = obj.getString("connectTime");

        equipPara3 = obj.getString("equipPara3");
    }

    public void refreshUI() {
        llEquip.findViewById(R.id.iv_image).setBackground(mActivity.getResources().getDrawable(getImageId()));
        ((TextView)llEquip.findViewById(R.id.tv_name)).setText(getEquipFullName());
        refreshUIStatus();
    }

    public void refreshUIStatus() {
        ((TextView)llEquip.findViewById(R.id.tv_status)).setText(getEquipFullStatus());
    }

    public void diasableUI() {
        ((TextView)llEquip.findViewById(R.id.tv_status)).setText("操作中...");
        llEquip.setClickable(false);
    }

    public void enasableUI() {
        refreshUIStatus();
        llEquip.setClickable(true);
    }

    private int getLayoutByStatus() {
        if (equiType.equals("RBM") || equiType.equals("FRM") || equiType.equals("PUSH")) {
            if (equiStatus.equals("0")) {
                if (equiType.equals("FRM")) {
                    return R.layout.layout_operation_frm_close;
                }
                return R.layout.layout_operation_rbm_close;
            } else if (equiStatus.equals("1")) {
                return R.layout.layout_operation_rbm_open;
            } else if (equiStatus.equals("2")) {
                return R.layout.layout_operation_rbm_open;
            } else if (equiStatus.equals("3")) {
                return R.layout.layout_operation_rbm_manaual;
            }
        } else {
            if (equiStatus.equals("0")) {
                if (equiType.equals("DRIP")) {
                    return R.layout.layout_operation_drip_close;
                }
                return R.layout.layout_operation_led_close;
            } else if (equiStatus.equals("1")) {
                return R.layout.layout_operation_led_open;
            }
        }

        return -1;
    }

    public AlertDialog showOperationDlg(final onOKClicked okCallback) throws Exception {
        int nLayout = getLayoutByStatus();
        if (nLayout == -1) {
            return null;
        }

        final LinearLayout llRoot = (LinearLayout) mActivity.getLayoutInflater().inflate(nLayout, null);
        EditText etPercent = llRoot.findViewById(R.id.et_percent);
        if (etPercent != null) {
            etPercent.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        }

        if ((nLayout == R.layout.layout_operation_led_close)
                && (equiType.equals("LED") || equiType.equals("HOT"))) {
            ((TextView)llRoot.findViewById(R.id.tv_led_unit)).setText("分钟");
        } else if (nLayout == R.layout.layout_operation_rbm_close) {
            if (equipPara3.length() == 0) {
                RadioButton rbTime = llRoot.findViewById(R.id.rb_roll_to);
                if (rbTime != null) {
                    rbTime.setEnabled(false);
                }
            }
        }

        ((TextView)llRoot.findViewById(R.id.tv_operation_title)).setText(nodeName + "-" + getEquipFullName());
        ((TextView)llRoot.findViewById(R.id.tv_connect_time)).setText(connectTime);
        ((TextView) llRoot.findViewById(R.id.tv_equip_status)).setText(getEquipFullStatus());

        final RadioGroup rgRbm = llRoot.findViewById(R.id.rg_rbm);
        rgRbm.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_open_all || checkedId == R.id.rb_close_all || checkedId == R.id.rb_open_manual) {
                    updateRadioInfo(false, false, false, false);
                } else if (checkedId == R.id.rb_open_time) {
                    updateRadioInfo(true, false, false, false);
                } else if (checkedId == R.id.rb_close_time) {
                    updateRadioInfo(false, true, false, false);
                } else if (checkedId == R.id.rb_roll_to) {
                    updateRadioInfo(false, false, true, false);
                /*} else if (checkedId == R.id.rb_open_close_to) {
                    updateRadioInfo(false, false, false, true);*/
                } else if (checkedId == R.id.rb_led_open) {
                    updateLedRadioInfo(false);
                } else if (checkedId == R.id.rb_led_open_time) {
                    updateLedRadioInfo(true);
                } else if (checkedId == R.id.rb_drip_open) {
                    updateDripRadioInfo(false, false);
                } else if (checkedId == R.id.rb_drip_open_time) {
                    updateDripRadioInfo(true, false);
                } else if (checkedId == R.id.rb_drip_open_count) {
                    updateDripRadioInfo(false, true);
                }
            }

            private void updateRadioInfo(boolean bOpenTimeEnable, boolean bCloseTimeEnable,
                                         boolean bPercentEnable, boolean bPositionEnable) {
                EditText etOpenTime = llRoot.findViewById(R.id.et_open_time);
                EditText etCloseTime = llRoot.findViewById(R.id.et_close_time);
                EditText etPercent = llRoot.findViewById(R.id.et_percent);
                //EditText etPosition = llRoot.findViewById(R.id.et_position);
                etOpenTime.setEnabled(bOpenTimeEnable);
                etCloseTime.setEnabled(bCloseTimeEnable);
                if (etPercent != null) {
                    etPercent.setEnabled(bPercentEnable);
                }
//                if (etPosition != null) {
//                    etPosition.setEnabled(bPositionEnable);
//                }
            }

            private void updateLedRadioInfo(boolean bOpenTimeEnable) {
                EditText etOpenTime = llRoot.findViewById(R.id.et_led_open_time);
                etOpenTime.setEnabled(bOpenTimeEnable);
            }

            private void updateDripRadioInfo(boolean bOpenTimeEnable, boolean bOpenConutEnable) {
                EditText etOpenTime = llRoot.findViewById(R.id.et_drip_open_time);
                EditText etOpenCount = llRoot.findViewById(R.id.et_drip_open_count);
                etOpenTime.setEnabled(bOpenTimeEnable);
                etOpenCount.setEnabled(bOpenConutEnable);
            }
        });

        Button btnOK = llRoot.findViewById(R.id.btn_ok);
        Button btnCancel = llRoot.findViewById(R.id.btn_cancel);

        ad = new AlertDialog.Builder(mActivity)
                .setView(llRoot)
                .show();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String paraTime = null;
                String paraPercent = null;
                String paraPos = null;
                String controlType = "";
                operatinMsg = "";

                switch (rgRbm.getCheckedRadioButtonId()) {
                    case R.id.rb_open_all:
                        controlType = equiType + "FWD";
//                        paraPos = equiPara.toString();
                        operatinMsg = "卷起";
                        break;
                    case R.id.rb_close_all:
                        controlType = equiType + "REV";
//                        paraPos = "0";
                        operatinMsg = "铺开";
                        break;
                    case R.id.rb_open_time:
                        controlType = equiType + "FWD";
                        EditText etOpenTime = llRoot.findViewById(R.id.et_open_time);
                        if (!CheckInputUtil.checkTextViewInput(etOpenTime, mActivity, "请输入运行时间！")) {
                            return;
                        }
                        paraTime = etOpenTime.getText().toString().trim();
                        operatinMsg = "卷起" + paraTime + "秒";
                        break;
                    case R.id.rb_close_time:
                        controlType = equiType + "REV";
                        EditText etCloseTime = llRoot.findViewById(R.id.et_close_time);
                        if (!CheckInputUtil.checkTextViewInput(etCloseTime, mActivity, "请输入运行时间！")) {
                            return;
                        }
                        paraTime = etCloseTime.getText().toString().trim();
                        operatinMsg = "铺开" + paraTime + "秒";
                        break;
                    case R.id.rb_roll_to:
                        EditText etPercent = llRoot.findViewById(R.id.et_percent);
                        if (!CheckInputUtil.checkTextViewInput(etPercent, mActivity, "请输入最终位置！")) {
                            return;
                        }
                        float pos = Float.valueOf(etPercent.getText().toString().trim());
                        if (pos < 0 || pos > 100) {
                            DlgUtil.showMsgInfo(mActivity, "输入的百分比必须在0到100之间！");
                            return;
                        }
                        if (pos == equiLocate) {
                            DlgUtil.showMsgInfo(mActivity, "输入的百分比与现在设备的状态相同！");
                            return;
                        } else if (pos < equiLocate) {
                            controlType = equiType + "REV";
                            operatinMsg = "铺开到";
                        } else {
                            controlType = equiType + "FWD";
                            operatinMsg = "卷起到";
                        }
                        paraPercent = pos + "";
                        operatinMsg += paraPercent + "%";
                        break;
                    /*case R.id.rb_open_close_to:
                        EditText etPosition = llRoot.findViewById(R.id.et_position);
                        if (!CheckInputUtil.checkTextViewInput(etPosition, mActivity, "请输入最终位置！")) {
                            return;
                        }
                        Integer pos = Integer.valueOf(etPosition.getText().toString().trim());
                        if (pos < 0 || pos > equiPara) {
                            DlgUtil.showMsgInfo(mActivity, "输入的圈数必须在0到" + equiPara + "之间！");
                            return;
                        }
                        if (pos == equiStatusPara) {
                            DlgUtil.showMsgInfo(mActivity, "输入的圈数与现在设备的状态相同！");
                            return;
                        } else if (pos < equiStatusPara) {
                            controlType = equiType + "REV";
                            operatinMsg = "铺开到";
                        } else {
                            controlType = equiType + "FWD";
                            operatinMsg = "卷起到";
                        }
                        paraPos = pos.toString();
                        operatinMsg += paraPos + "圈";
                        break;*/
                    case R.id.rb_stop_rbm:
                        controlType = equiType + "OFF";
                        operatinMsg = "停止转动";
                        break;
                    case R.id.rb_open_manual:
                        controlType = equiType + "DEON";
                        operatinMsg = "启用外设开关";
                        break;
                    case R.id.rb_close_manual:
                        controlType = equiType + "DEOFF";
                        operatinMsg = "停用外设开关";
                        break;
                    case R.id.rb_led_open:
                        controlType = equiType + "ON";
                        operatinMsg = "打开";
                        break;
                    case R.id.rb_led_open_time:
                        controlType = equiType + "ON";
                        EditText etLedOpenTime = llRoot.findViewById(R.id.et_led_open_time);
                        if (!CheckInputUtil.checkTextViewInput(etLedOpenTime, mActivity, "请输入打开时间！")) {
                            return;
                        }
                        paraTime = etLedOpenTime.getText().toString().trim();
                        operatinMsg = "打开" + paraTime + ((TextView)llRoot.findViewById(R.id.tv_led_unit)).getText().toString();
                        break;
                    case R.id.rb_led_close:
                        controlType = equiType + "OFF";
                        operatinMsg = "关闭";
                        break;
                    case R.id.rb_drip_open:
                        controlType = equiType + "ON";
                        operatinMsg = "打开";
                        break;
                    case R.id.rb_drip_open_time:
                        controlType = equiType + "ON";
                        EditText etDripOpenTime = llRoot.findViewById(R.id.et_drip_open_time);
                        if (!CheckInputUtil.checkTextViewInput(etDripOpenTime, mActivity, "请输入打开时间！")) {
                            return;
                        }
                        paraTime = etDripOpenTime.getText().toString().trim();
                        operatinMsg = "打开" + paraTime + "秒";
                        break;
                    case R.id.rb_drip_open_count:
                        controlType = equiType + "ON";
                        EditText etDripOpenCount = llRoot.findViewById(R.id.et_drip_open_count);
                        if (!CheckInputUtil.checkTextViewInput(etDripOpenCount, mActivity, "请输入打开的立方数！")) {
                            return;
                        }
                        paraPos = etDripOpenCount.getText().toString().trim();
                        operatinMsg = "打开" + paraPos + "立方";
                        break;
                }

                String msg = "确定要操作" + getEquipFullName() + "（" + operatinMsg + "）" + "吗？";
                final String finalControlType = controlType;
                final String finalParaTime = paraTime;
                final String finalParaPos = paraPos;
                final String finalParaPercent = paraPercent;
                DlgUtil.showAsk(mActivity, msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, String> mp = new LinkedHashMap<>();
                        final String tradeId = "0_" + System.currentTimeMillis();
                        mp.put("tradeId", tradeId);
                        mp.put("gwId", gwId);
                        mp.put("nodeId", nodeId);
                        mp.put("equiType", equiType);
                        mp.put("equiId", equiId);
                        mp.put("controlType", finalControlType);
                        mp.put("insertWay", "0");
                        mp.put("para1", finalParaTime);
                        mp.put("para2", finalParaPos);
                        mp.put("para3", finalParaPercent);

                        okCallback.toAddTrade(tradeId, mp);

                        ad.dismiss();
                    }
                });
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

    public String getOperatinMsg() {
        return operatinMsg;
    }
}
