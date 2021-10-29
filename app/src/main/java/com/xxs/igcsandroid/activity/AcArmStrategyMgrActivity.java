package com.xxs.igcsandroid.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.entity.ArmStrategy;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class AcArmStrategyMgrActivity extends AppCompatActivity {
    private String mGhId;
    private String mGhName;

    private LinearLayout llRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_arm_strategy_mgr);

        Intent curIntent = getIntent();
        Bundle bundle = curIntent.getExtras();
        mGhId = bundle.getString("ghId");
        mGhName = bundle.getString("ghName");

        setTitle(mGhName + "->下位机策略管理");

        llRoot = findViewById(R.id.ll_arm_strategy_root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getArmStrategyFromServer();
    }

    public void getArmStrategyFromServer() {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("ghId", mGhId);

        AsyncSocketUtil.post(this, "autoctrl/queryArmStrategy", mp, null, new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    llRoot.removeAllViews();

                    for (int i = 1; i < response.length(); i += 2) {
                        View vNode = LayoutInflater.from(AcArmStrategyMgrActivity.this).inflate(R.layout.layout_one_node_head, null);
                        LinearLayout llOneNodeRoot = vNode.findViewById(R.id.ll_one_node_root);
                        TextView tvNode = vNode.findViewById(R.id.tv_node);

                        JSONObject nodeObj = response.getJSONObject(i);
                        final String gwId = nodeObj.getString("gwId");
                        final String nodeId = nodeObj.getString("NodeId");
                        String nodeName = nodeObj.getString("NodeName");
                        tvNode.setText(nodeName + "(网关" + gwId + " 节点" + nodeId + ")");

                        JSONArray jAOne = response.getJSONArray(i + 1);
                        for (int j = 0; j < jAOne.length(); j += 1) {
                            View vTlRow = LayoutInflater.from(AcArmStrategyMgrActivity.this).inflate(R.layout.layout_equipment_row, null);
                            LinearLayout llSensorRow = vTlRow.findViewById(R.id.ll_equipment_row);

                            JSONObject jOSensor = jAOne.getJSONObject(j);
                            ArmStrategy entity = new ArmStrategy(jOSensor, gwId, nodeId, nodeName);
                            View ll_sensor = getViewOfAs(entity);
                            if (ll_sensor != null) {
                                llSensorRow.addView(ll_sensor, 0);
                            }

                            llOneNodeRoot.addView(vTlRow);
                        }

                        llRoot.addView(llOneNodeRoot);
                    }
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(AcArmStrategyMgrActivity.this, e);
                }
            }
        }, new AsyncSocketUtil.onFailString() {
            @Override
            public void OnStringResult(String errMsg) {
                AcArmStrategyMgrActivity.this.finish();
            }
        });
    }

    private View getViewOfAs(final ArmStrategy entity) {
        View convertView = LayoutInflater.from(AcArmStrategyMgrActivity.this).inflate(R.layout.list_item_arm_strategy, null);
        TextView tvControlType = convertView.findViewById(R.id.tv_control_type);
        TextView tvStatus = convertView.findViewById(R.id.tv_status);
        TextView tvContent1 = convertView.findViewById(R.id.tv_content1);

        tvControlType.setText(entity.getThresholdTypeString());
        tvStatus.setText(entity.getIsUseString());
        if (entity.getThresholdType().equals("time")) {
            tvContent1.setText(entity.getTimeContent());
        } else if (entity.getThresholdType().equals("value")) {
            tvContent1.setText(entity.getValueContent());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (entity.getThresholdType().equals("time")) {
                    intent = new Intent(AcArmStrategyMgrActivity.this, AcArmStrategyTimeActivity.class);
                } else if (entity.getThresholdType().equals("value")) {
                    intent = new Intent(AcArmStrategyMgrActivity.this, AcArmStrategyValueActivity.class);
                } else {
                    return;
                }
                intent.putExtra("id", entity.getArmStrategyId());
                intent.putExtra("gwId", entity.getGatewayId());
                intent.putExtra("nodeId", entity.getNodeId());
                intent.putExtra("nodeName", entity.getNodeName());
                intent.putExtra("armId", entity.getThresholdId());
                intent.putExtra("type", entity.getThresholdType());
                intent.putExtra("isUse", entity.getIsUse());
                intent.putExtra("startH", entity.getStartHour());
                intent.putExtra("startM", entity.getStartMin());
                intent.putExtra("endH", entity.getEndHour());
                intent.putExtra("endM", entity.getEndMin());
                intent.putExtra("sensorType", entity.getSensorTypeCode());
                intent.putExtra("sensorId", entity.getSensorId());
                intent.putExtra("sensorUnit", entity.getSensorUnit());
                intent.putExtra("compType", entity.getCompareType());
                intent.putExtra("compPara", entity.getComparePara());
                intent.putExtra("equipType", entity.getEquipmentType());
                intent.putExtra("equipId", entity.getEquipmentId());
                intent.putExtra("opertType", entity.getOperateType());
                intent.putExtra("opertPara", entity.getOperatePara());
                startActivity(intent);
            }
        });

        return convertView;
    }
}