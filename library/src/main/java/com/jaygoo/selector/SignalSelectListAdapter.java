package com.jaygoo.selector;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SignalSelectListAdapter extends RecyclerView.Adapter<SignalSelectListAdapter.ChoiceViewHolder> {

    private List<MultiData> choiceDatas;
    private Boolean[] selectStates;
    private OnSelectChangeListener mOnSelectChangeListener;

    /**
     * when you select the choices,it will be called
     */
    public interface OnSelectChangeListener{
        void onChanged(Integer indexSel);
    }

    public SignalSelectListAdapter(List<MultiData> list){
        choiceDatas = list;

        // init the select state array
        if (choiceDatas != null && choiceDatas.size() >0) {
            selectStates = new Boolean[choiceDatas.size()];
            Arrays.fill(selectStates, false);
        }
    }

    public void initWithData() {
        if (choiceDatas != null && choiceDatas.size() > 0) {
            for (int i = 0; i < choiceDatas.size(); i++) {
                if (choiceDatas.get(i).isbSelect()) {
                    selectStates[i] = true;
                } else {
                    selectStates[i] = false;
                }
            }

            notifyDataSetChanged();
            if (mOnSelectChangeListener != null){
                mOnSelectChangeListener.onChanged(getSelectedPosition());
            }
        }
    }

    @Override
    public ChoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChoiceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.signal_select_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final ChoiceViewHolder holder, final int position) {
        holder.choiceNameBtn.setText(choiceDatas.get(position).getText());
        holder.choiceNameBtn.setSelected(selectStates[position]);
        holder.choiceNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectStates != null) {
                    Arrays.fill(selectStates, false);
                    selectStates[position] = true;
                    notifyDataSetChanged();
                    if (mOnSelectChangeListener != null){
                        mOnSelectChangeListener.onChanged(getSelectedPosition());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return choiceDatas.size();
    }

    public Integer getSelectedPosition(){
        Integer result = -1;
        if (selectStates == null) {
            return result;
        }
        for (int i = 0; i < selectStates.length ; i++){
            if (selectStates[i]){
                result = i;
                break;
            }
        }
        return result;
    }

    public void setOnSelectChangeListener(OnSelectChangeListener listener){
        mOnSelectChangeListener = listener;
    }

    /**
     * viewHolder
     */
    public class ChoiceViewHolder extends RecyclerView.ViewHolder {
        public TextView choiceNameBtn;

        public ChoiceViewHolder(View itemView) {
            super(itemView);
            choiceNameBtn = itemView.findViewById(R.id.choiceNameBtn);
        }
    }
}
