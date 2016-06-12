package com.eventer.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.eventer.app.other.Activity_Chat;
import com.eventer.app.task.LoadImage;
import com.eventer.app.task.LoadImage.ImageDownloadedCallBack;
import com.eventer.app.util.LocalUserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint({ "SdCardPath", "InflateParams" })
public class ChatRoomAdapter extends BaseAdapter {

    Context context;
    List<ChatRoom> grouplist;
    private LayoutInflater inflater;
    private LoadImage avatarLoader;

    public ChatRoomAdapter(Context context, List<ChatRoom> grouplist) {
        this.context = context;
        this.grouplist = grouplist;
        inflater = LayoutInflater.from(context);
        avatarLoader = new LoadImage(context, Constant.IMAGE_PATH);
    }

    @Override
    public int getCount() {
        return grouplist.size();
    }

    @Override
    public ChatRoom getItem(int position) {
        return grouplist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        ChatRoom group = grouplist.get(position);
        final String groupId = group.getRoomId();

        int membersNum;

        String groupName =  group.getDefaultName();


        String[] members=group.getMember();

        membersNum = members.length;
        convertView = creatConvertView(membersNum);
        final View view=convertView;

        holder.tv_name = (TextView)view.findViewById(R.id.tv_name);
        holder.tv_name.setText(groupName);
        final int[] avatar_id=new int[]{R.id.iv_avatar1,R.id.iv_avatar2,R.id.iv_avatar3,R.id.iv_avatar4};
        for( int i=0;i<membersNum&&i<4;i++){
            final int loc=i;
            final String member=members[i];
            if(member.equals(Constant.UID)){
                String avatar=LocalUserInfo.getInstance(context).getUserInfo("avatar");
                showUserAvatar((ImageView) view
                        .findViewById(avatar_id[i]), avatar);
            }else if(MyApplication.getInstance().getContactList().containsKey(member)){
                User u=MyApplication.getInstance().getContactList().get(member);
                String avatar=u.getAvatar();
                showUserAvatar((ImageView) view
                        .findViewById(avatar_id[i]), avatar);
            }else if(MyApplication.getInstance().getUserList().containsKey(member)){
                UserInfo u=MyApplication.getInstance().getUserList().get(member);
                String avatar=u.getAvatar();
                showUserAvatar((ImageView) view
                        .findViewById(avatar_id[i]), avatar);
            }else{
                Map<String,String> map=new HashMap<>();
                map.put("uid", member);
                LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_GET_USERINFO, map);
                task.getData(new DataCallBack() {
                    @Override
                    public void onDataCallBack(JSONObject data) {
                        if(data!=null){
                            int status=data.getInteger("status");
                            switch (status) {
                                case 0:
                                    JSONObject user_action=data.getJSONObject("user_action");
                                    JSONObject info=user_action.getJSONObject("info");
                                    String name=info.getString("name");
                                    String avatar=info.getString("avatar");
                                    showUserAvatar((ImageView) view.findViewById(avatar_id[loc]), avatar);
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
                        }

                    }
                });
            }
        }
        // 为了item变色在此处写监听
        RelativeLayout re_item = (RelativeLayout) convertView
                .findViewById(R.id.re_item);
        re_item.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 进入群聊
                Intent intent = new Intent(context, Activity_Chat.class);
                // it is group chat
                intent.putExtra("chatType", Activity_Chat.CHATTYPE_GROUP);
                intent.putExtra("groupId", groupId);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private static class ViewHolder {

        TextView tv_name;

    }

    private void showUserAvatar(ImageView iamgeView, String avatar) {
        if(avatar==null||avatar.equals("")||avatar.equals("default")) return;
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
        if (bitmap != null)
            iamgeView.setImageBitmap(bitmap);

    }

    private View creatConvertView( int size) {
        View convertView;
        switch (size) {
            case 1:
                convertView = inflater.inflate(R.layout.item_chatroom_1, null,
                        false);

                break;
            case 2:
                convertView = inflater.inflate(R.layout.item_chatroom_2, null,
                        false);
                break;
            case 3:
                convertView = inflater.inflate(R.layout.item_chatroom_3, null,
                        false);
                break;
            case 4:
                convertView = inflater.inflate(R.layout.item_chatroom_4, null,
                        false);
                break;

            default:
                convertView = inflater.inflate(R.layout.item_chatroom_4, null,
                        false);
                break;

        }
        return convertView;
    }

}
