package com.xxs.igcsandroid.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.xxs.igcsandroid.R;
import com.xxs.igcsandroid.activity.LoginActivity;
import com.xxs.igcsandroid.entity.UserInfo;
import com.xxs.igcsandroid.golbal.Constants;
import com.xxs.igcsandroid.socket.AsyncSocketUtil;
import com.xxs.igcsandroid.util.DlgUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserInfoAdapter extends MyBaseAdapter {
    private ArrayList<UserInfo> mUserList;
    private boolean isParkShow;

    public UserInfoAdapter(Activity activity) {
        super(activity);
        mUserList = new ArrayList<>();
    }

    public void setParkShow(boolean parkShow) {
        isParkShow = parkShow;
    }

    public synchronized void setUserInfo(ArrayList<UserInfo> users) {
        if (mUserList != null) {
            mUserList.clear();
        }
        mUserList = users;
    }

    @Override
    public int getCount() {
        if (mUserList == null) {
            return 0;
        } else {
            return mUserList.size();
        }
    }

    static class ViewHolder {
        SmartImageView smvPic;
        TextView txtViewAccound;
        TextView txtViewName;
        TextView txtViewPhone;
        TextView txtViewTime;
        TextView txtViewPark;
    }

    @Override
    public synchronized View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_user, null);
            holder = new ViewHolder();
            holder.smvPic = view.findViewById(R.id.gh_pic);
            holder.txtViewAccound = view.findViewById(R.id.tv_account);
            holder.txtViewName = view.findViewById(R.id.tv_name);
            holder.txtViewPhone = view.findViewById(R.id.tv_phone);
            holder.txtViewTime = view.findViewById(R.id.tv_time);
            holder.txtViewPark = view.findViewById(R.id.tv_parkInfo);
            if (!isParkShow) {
                holder.txtViewPark.setVisibility(View.GONE);
            }
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        final UserInfo entity = mUserList.get(position);
        String picPath = Constants.SERVER_URL + "downloadFile/downloadFile?inputPath=";
        if (entity.getLogoAddr().length() > 0) {
            picPath = picPath + entity.getLogoAddr();
        } else {
            picPath = picPath + "IGCS_DEFAULT_USER.png";
        }
        holder.smvPic.setImageUrl(picPath);
        holder.txtViewAccound.setText(entity.getUserId());
        holder.txtViewName.setText(entity.getUserName());
        holder.txtViewPhone.setText(entity.getPhoneNo());
        holder.txtViewTime.setText(entity.getAddTime());
        holder.txtViewPark.setText(entity.getParkInfo());

        view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, Menu.NONE, "重置密码")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                toResetPwd(entity.getUserId());
                                return true;
                            }
                        });
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.showContextMenu();
                return true;
            }
        });

        return view;
    }

    private void toResetPwd(String userId) {
        Map<String, String> mp = new LinkedHashMap<>();
        mp.put("userId", userId);

        AsyncSocketUtil.post(mActivity, "user/resetPassword", mp, "正在重置密码......", new AsyncSocketUtil.onSuccessJSONArray() {
            @Override
            public void OnJSONArrayResult(JSONArray response) {
                try {
                    DlgUtil.showMsgInfo(mActivity, "重置密码成功！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    DlgUtil.showExceptionPrompt(mActivity, e);
                }
            }
        }, null);
    }
}
