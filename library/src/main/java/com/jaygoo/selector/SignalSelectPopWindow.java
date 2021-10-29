package com.jaygoo.selector;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：1.1.0
 * 创建日期：2017/2/22
 * 描    述:
 * ================================================
 */
public class SignalSelectPopWindow {

    private PopupWindow mPopupWindow;
    private SignalSelectListAdapter adapter;
    private TextView cancelBtn;
    private TextView confirmBtn;
    private TextView titleTV;
    private OnConfirmClickListener mOnConfirmListener;
    private Integer mselKey;
    private Builder mBuilder;

    public interface OnConfirmClickListener{
        void onClick(Integer indexSel);
    }

    static public class Builder {
        private Activity mActivity;
        private List<MultiData> choiceDataList = new ArrayList<>();
        private String title;
        private String confirmText;
        private String cancelText;
        private boolean isOutsideTouchable;
        private View.OnClickListener mOnCancelListener;
        private OnConfirmClickListener mOnConfirmListener;
        private int mConfirmTextColor;
        private int mCancelTextColor;
        private int mTitleTextColor;

        public Builder(Activity mActivity){
            this.mActivity = mActivity;
            cancelText = "取消";
            confirmText = "完成";
        }

        public Builder setDataArray(List<MultiData> list){
            this.choiceDataList = list;
            return this;
        }

        /**
         * set title
         * @param title
         * @return
         */
        public Builder setTitle(String title){
            this.title = title;
            return this;
        }

        /**
         * set confirm button text
         * @param str
         * @return
         */
        public Builder setConfirm(String str){
            this.confirmText = str;
            return this;
        }

        /**
         * set cacel button text
         * @param str
         * @return
         */
        public Builder setCancel(String str){
            this.cancelText = str;
            return this;
        }

        /**
         * set title's text color
         * @param color
         * @return
         */
        public Builder setTitleTextColor(int color){
            this.mTitleTextColor = color;
            return this;
        }

        /**
         * set confirm button's text color
         * @param color
         * @return
         */
        public Builder setConfirmTextColor(int color){
            this.mConfirmTextColor = color;
            return this;
        }

        /**
         * set cancel button's text color
         * @param color
         * @return
         */
        public Builder setCancelTextColor(int color){
            this.mCancelTextColor = color;
            return this;
        }

        /**
         * set if can touchable if your finger touch outside
         * @param isOutsideTouchable
         * @return
         */
        public Builder setOutsideTouchable(boolean isOutsideTouchable){
            this.isOutsideTouchable = isOutsideTouchable;
            return this;
        }

        public SignalSelectPopWindow build(){
            return new SignalSelectPopWindow(this);
        }

        public Builder setConfirmListener(OnConfirmClickListener listener){
            this.mOnConfirmListener = listener;
            return this;
        }

        public Builder setCancelListener(View.OnClickListener listener){
            this.mOnCancelListener = listener;
            return this;
        }


    }
    private SignalSelectPopWindow(final Builder builder){
        mBuilder = builder;

        //init PopWindow
        View popview = View.inflate(builder.mActivity, R.layout.signal_select_list_popwindow, null);
        mPopupWindow = new PopupWindow(popview, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setAnimationStyle(R.style.popwindow_anim_style);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(builder.isOutsideTouchable);

        initViews(mPopupWindow.getContentView());

        initListener();

    }

    /**
     * init listener
     */
    private void initListener() {
        this.mOnConfirmListener = mBuilder.mOnConfirmListener;

        // change the background's color
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBuilder.mOnConfirmListener != null && mselKey != -1){
                    mOnConfirmListener.onClick(mselKey);
                }
                dismiss();
            }
        });

        if (mBuilder.mOnCancelListener != null){
            cancelBtn.setOnClickListener(mBuilder.mOnCancelListener);
        }

        cancelBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });


        // change the badge number
        adapter.setOnSelectChangeListener(new SignalSelectListAdapter.OnSelectChangeListener() {

            @Override
            public void onChanged(Integer indexSel) {
                mselKey = indexSel;
            }
        });

        adapter.initWithData();
    }

    private void initViews(View root) {
        titleTV = (TextView) root.findViewById(R.id.title);
        cancelBtn = (TextView) root.findViewById(R.id.cancelBtn);
        confirmBtn = (TextView) root.findViewById(R.id.confirmBtn);

        setText(titleTV,mBuilder.title);
        setText(cancelBtn,mBuilder.cancelText);
        setText(confirmBtn,mBuilder.confirmText);

        setTextColor(titleTV,mBuilder.mTitleTextColor);
        setTextColor(cancelBtn,mBuilder.mCancelTextColor);
        setTextColor(confirmBtn,mBuilder.mConfirmTextColor);

        RecyclerView recyclerView = (RecyclerView) mPopupWindow.getContentView().findViewById(R.id.mRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mBuilder.mActivity.getApplication()));
        adapter = new SignalSelectListAdapter(mBuilder.choiceDataList);
        recyclerView.setAdapter(adapter);


    }

    private void setText(TextView tv, String str){
        if (tv != null && str != null){
            tv.setText(str);
        }
    }

    private void setTextColor(TextView tv, int color){
        if (tv != null && color != 0){
            tv.setTextColor(color);
        }
    }

    public void dismiss() {
        if (mPopupWindow != null){
            mPopupWindow.dismiss();
        }
    }

    /**
     * parent is the popwindow show location
     * @param parent
     */
    public void show(View parent){
        if (mPopupWindow != null){
            backgroundAlpha(0.8f);
            mPopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * set background alpha
     * @param alpha
     */
    public void backgroundAlpha(float alpha) {
        try {
            WindowManager.LayoutParams lp = mBuilder.mActivity.getWindow().getAttributes();
            lp.alpha = alpha; //0.0-1.0
            mBuilder.mActivity.getWindow().setAttributes(lp);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
