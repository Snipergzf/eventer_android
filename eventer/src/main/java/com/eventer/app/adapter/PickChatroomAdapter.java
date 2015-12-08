package com.eventer.app.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.entity.ChatRoom;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.other.ShareToGroupActivity;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.util.LocalUserInfo;

/**
 * 简单的好友Adapter实现
 *
 */
public  class PickChatroomAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private boolean[] isCheckedArray;
    private Map<String,Bitmap[]> bitmaps;
    private LoadUserAvatar avatarLoader;
    private List<ChatRoom> list = new ArrayList<ChatRoom>();
    private List<String> exitingMembers = new ArrayList<String>();
    private List<String> addList = new ArrayList<String>();
    private int res;
    private Context context;

    public PickChatroomAdapter(Context context, int resource,
                               List<ChatRoom> rooms) {

        layoutInflater = LayoutInflater.from(context);
        avatarLoader = new LoadUserAvatar(context, Constant.IMAGE_PATH);
        this.res = resource;
        this.list = rooms;
        this.context=context;
        bitmaps = new HashMap<String, Bitmap[]>();
        isCheckedArray = new boolean[list.size()];

    }

    public Bitmap[] getBitmap(int position) {
//    	ChatRoom room=list.get(position);
        return bitmaps.get(position+"");
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    public List<String> getMembers(){
        return exitingMembers;
    }

    @Override
    public ChatRoom getItem(int position) {
        return list.get(position);

    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    private static class ViewHolder {

        TextView tv_name;

    }
    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        ChatRoom group = list.get(position);

        final String groupId=group.getRoomId();
        int membersNum = 0;

        String groupName =  group.getRoomname();

        String groupName_temp2 = "";
        String[] members=group.getMember();
        String[] displayName=group.getDisplayname();
        membersNum = members.length;
        Bitmap[] b=new Bitmap[membersNum];
        bitmaps.put(position+"", b);
        convertView = creatConvertView(membersNum);
        final View view=convertView;
        for (int i = 0; i < membersNum; i++) {
            if (i == 0) {
                groupName_temp2 = displayName[i];
            } else if (i < 4) {
                groupName_temp2 += "、" + displayName[i];

            } else if (i == 4) {
                groupName_temp2 += "...("+displayName+")";
            }
        }

        if (groupName==null||groupName.equals("")) {
            groupName = groupName_temp2;
        }
        if (groupName==null||groupName.equals("")) {
            groupName = "群组";
        }

        holder.tv_name = (TextView)view.findViewById(R.id.tv_name);
        holder.tv_name.setText(groupName);
        final int[] avatar_id=new int[]{R.id.iv_avatar1,R.id.iv_avatar2,R.id.iv_avatar3,R.id.iv_avatar4};
        for( int i=0;i<membersNum&&i<4;i++){
            final int loc=i;
            final String member=members[i];
            if(member.equals(Constant.UID)){
                String avatar=LocalUserInfo.getInstance(context).getUserInfo("avatar");
                showUserAvatar((ImageView) view
                        .findViewById(avatar_id[i]), avatar,position,i);
            }else if(MyApplication.getInstance().getContactList().containsKey(member)){
                User u=MyApplication.getInstance().getContactList().get(member);
                String avatar=u.getAvatar();
                showUserAvatar((ImageView) view
                        .findViewById(avatar_id[i]), avatar,position,i);
            }else if(MyApplication.getInstance().getUserList().containsKey(member)){
                UserInfo u=MyApplication.getInstance().getUserList().get(member);
                String avatar=u.getAvatar();
                showUserAvatar((ImageView) view
                        .findViewById(avatar_id[i]), avatar,position,i);
            }else{
                Map<String,String> map=new HashMap<String, String>();
                map.put("uid", member);
                LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_GET_USERINFO, map);
                task.getData(new DataCallBack() {
                    @Override
                    public void onDataCallBack(JSONObject data) {
                        // TODO Auto-generated method stub
                        try {
                            int status=data.getInteger("status");
                            switch (status) {
                                case 0:
                                    JSONObject user_action=data.getJSONObject("user_action");
                                    JSONObject info=user_action.getJSONObject("info");
                                    String name=info.getString("name");
                                    String avatar=info.getString("avatar");
                                    showUserAvatar((ImageView) view.findViewById(avatar_id[loc]), avatar,position,loc);
                                    UserInfo user=new UserInfo();
                                    user.setAvatar(avatar);
                                    user.setNick(name);
                                    user.setType(22);
                                    user.setUsername(member);
                                    MyApplication.getInstance().addUser(user);
                                    break;
                                default:
                                    Log.e("1", "获取用户信息失败：");
                                    break;
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        // 为了item变色在此处写监听
        final CheckBox checkBox = (CheckBox) convertView
                .findViewById(R.id.checkbox);

        if (exitingMembers != null && exitingMembers.contains(groupId)) {
            checkBox.setButtonDrawable(R.drawable.btn_check);
        } else {
            checkBox.setButtonDrawable(R.drawable.check_blue);
        }

        if (addList != null && addList.contains(groupId)) {
            checkBox.setChecked(true);
            isCheckedArray[position] = true;
        }
        if (checkBox != null) {
            checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // 群组中原来的成员一直设为选中状态
                    if (exitingMembers.contains(groupId)) {
                        isChecked = true;
                        checkBox.setChecked(true);
                    }
                    isCheckedArray[position] = isChecked;

                    if (isChecked) {
                        // 选中用户显示在滑动栏显示
                        ShareToGroupActivity.instance.showCheckImage(getBitmap(position),
                                list.get(position));

                    } else {
                        // 用户显示在滑动栏删除
                        ShareToGroupActivity.instance.deleteImage(list.get(position));

                    }

                }
            });
            // 群组中原来的成员一直设为选中状态
            if (exitingMembers.contains(groupId)) {
                checkBox.setChecked(true);
                isCheckedArray[position] = true;
            } else {
                checkBox.setChecked(isCheckedArray[position]);
            }

        }
        return convertView;
    }

    private void showUserAvatar(ImageView iamgeView, String avatar,int position,int loc) {
        if(avatar==null||avatar.equals("")||avatar.equals("default")){
            Bitmap[] bitmap_temp=bitmaps.get(position+"");
            bitmap_temp[loc]=null;
            bitmaps.put(position+"", bitmap_temp);
            return;
        }

        final String url_avatar = avatar;
        iamgeView.setTag(url_avatar);
        Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
                new ImageDownloadedCallBack() {

                    public void onImageDownloaded(ImageView imageView,
                                                  Bitmap bitmap,int status) {
                        if(status==-1){
                            if (imageView.getTag() == url_avatar) {
                                imageView.setImageBitmap(bitmap);

                            }
                        }
                    }

                });
        if (bitmap != null){}
        iamgeView.setImageBitmap(bitmap);
        Bitmap[] bitmap_temp=bitmaps.get(position+"");
        bitmap_temp[loc]=bitmap;
        bitmaps.put(position+"", bitmap_temp);

    }

    private View creatConvertView( int size) {
        View convertView;
        switch (size) {
            case 1:
                convertView = layoutInflater.inflate(R.layout.item_chatroom_checkbox_1, null,
                        false);

                break;
            case 2:
                convertView = layoutInflater.inflate(R.layout.item_chatroom_checkbox_2, null,
                        false);
                break;
            case 3:
                convertView = layoutInflater.inflate(R.layout.item_chatroom_checkbox_3, null,
                        false);
                break;
            case 4:
                convertView = layoutInflater.inflate(R.layout.item_chatroom_checkbox_4, null,
                        false);
                break;

            default:
                convertView = layoutInflater.inflate(R.layout.item_chatroom_checkbox_4, null,
                        false);
                break;

        }
        return convertView;
    }
}
