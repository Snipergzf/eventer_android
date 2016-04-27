package com.eventer.app.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.InviteMessgeDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.InviteMessage;
import com.eventer.app.entity.InviteMessage.InviteMesageStatus;
import com.eventer.app.entity.User;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.main.MainActivity;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.util.LocalUserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ViewHolder")
public class NewFriendsAdapter extends BaseAdapter {
    Context context;
    List<InviteMessage> msgs;
    private InviteMessgeDao messgeDao;
    private LoadUserAvatar avatarLoader;
    int total = 0;

    @SuppressLint("SdCardPath")
    public NewFriendsAdapter(Context context, List<InviteMessage> msgs) {
        this.context = context;
        this.msgs = msgs;
        messgeDao = new InviteMessgeDao(context);
        avatarLoader = new LoadUserAvatar(context, Constant.IMAGE_PATH);
        total = msgs.size();
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public InviteMessage getItem(int position) {
        // TODO Auto-generated method stub
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        holder = new ViewHolder();
        final InviteMessage msg = getItem(position);
        String name = msg.getName();

        String avatar = msg.getAvatar();
        String reason;
        reason=msg.getReason();
        convertView = View.inflate(context, R.layout.item_newfriendsmsag, null);
        holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
        holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        holder.tv_reason = (TextView) convertView.findViewById(R.id.tv_reason);
        holder.tv_added = (TextView) convertView.findViewById(R.id.tv_added);
        holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
        holder.btn_ignore=(Button)convertView.findViewById(R.id.btn_ignore);
        holder.tv_name.setText(name);
        showUserAvatar(holder.iv_avatar, avatar);

        if(msg.getStatus()==InviteMesageStatus.INVITE){
            holder.tv_added.setText("等待确认");
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
            holder.btn_ignore.setVisibility(View.GONE);
            if(reason==null||reason.equals("")){
                reason="等待对方确认";
            }
        }else if ( msg.getStatus() == InviteMesageStatus.BEAGREED) {
            holder.tv_added.setText("已添加");
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
            holder.btn_ignore.setVisibility(View.GONE);
            if(reason==null||reason.equals("")){
                reason="等待对方确认";
            }
        } else if (msg.getStatus() == InviteMesageStatus.AGREED){
            holder.tv_added.setText("已同意");
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
            holder.btn_ignore.setVisibility(View.GONE);
            if(reason==null||reason.equals("")){
                reason="对方请求添加你为好友";
            }
        }else if(msg.getStatus()==InviteMesageStatus.REFUSED){
            holder.tv_added.setText("已忽略");
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
            holder.btn_ignore.setVisibility(View.GONE);
            if(reason==null||reason.equals("")){
                reason="对方请求添加你为好友";
            }
        }else {
            holder.tv_added.setVisibility(View.GONE);
            holder.btn_add.setVisibility(View.VISIBLE);
            holder.btn_ignore.setVisibility(View.VISIBLE);
            holder.btn_add.setTag(msg);
            if(reason==null||reason.equals("")){
                reason="对方请求添加你为好友";
            }
            holder.btn_add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    acceptInvitation(holder.btn_add, holder.btn_ignore, msg, holder.tv_added);
                }

            });
            holder.btn_ignore.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    ignoreInvitation(holder.btn_ignore,holder.btn_add, msg, holder.tv_added);
                }
            });

        }
        holder.tv_reason.setText(reason);
        return convertView;
    }

    protected void ignoreInvitation(final Button button, final Button button1, final InviteMessage msg,
                                    final TextView textview) {
        // TODO Auto-generated method stub

        new Thread(new Runnable() {
            public void run() {
                // 调用sdk的同意方法
                try {

                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            textview.setText("已忽略");
                            textview.setVisibility(View.VISIBLE);
                            button.setEnabled(false);
                            button.setVisibility(View.GONE);
                            button1.setEnabled(false);
                            button1.setVisibility(View.GONE);
                            msg.setStatus(InviteMesageStatus.REFUSED);
                            // 更新db
                            ContentValues values = new ContentValues();
                            values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg
                                    .getStatus().ordinal());
                            messgeDao.updateMessage(msg.getId(), values);
                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "忽略失败: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }

    private static class ViewHolder {
        ImageView iv_avatar;
        TextView tv_name;
        TextView tv_reason;
        TextView tv_added;
        Button btn_add;
        Button btn_ignore;

    }

    private void showUserAvatar(final ImageView iamgeView, String avatar) {
        final String url_avatar = avatar;
        iamgeView.setTag(url_avatar);
        if (url_avatar != null && url_avatar.contains("http")) {
            Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
                    new ImageDownloadedCallBack() {

                        @Override
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
    }
    /**
     * 同意好友请求或者群申请
     *
     */
    private void acceptInvitation(final Button button,final Button button1, final InviteMessage msg,
                                  final TextView textview) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("正在同意...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // 调用sdk的同意方法
                try {
                    Map<String,Object> result=new HashMap<>();
                    if (msg.getGroupId() == null) // 同意好友请求
                    {

                        Map<String,String> map=new HashMap<>();
                        map.put("uid", Constant.UID+"");
                        map.put("friend_id", msg.getId()+"");
                        map.put("certificate",msg.getCertification());
                        map.put("token", Constant.TOKEN);

                        result=HttpUnit.sendFriendComfirm(map);

                    }

                    final String info=(String)result.get("info");
                    if((int)result.get("status")==0){

                        JSONObject send_json = new JSONObject();
                        send_json.put("action", "friend_request");
                        send_json.put("type", 2);
                        send_json.put("data", "");
                        send_json.put("certificate", "");
                        send_json.put("name", LocalUserInfo.getInstance(context)
                                .getUserInfo("nick"));
                        send_json.put("avatar",LocalUserInfo.getInstance(context)
                                .getUserInfo("avatar"));
                        send_json.put("user_rank",LocalUserInfo.getInstance(context)
                                .getUserInfo("user_rank"));
                        String send_body = send_json.toString();
                        MainActivity.instance.newMsg("ADD", msg.getId()+"", send_body,1|16);
                        User u=new User();
                        u.setAvatar(msg.getAvatar());
                        u.setNick(msg.getFrom());
                        u.setUsername(msg.getId()+"");
                        u.setType(1);
                        UserDao d=new UserDao(context);
                        d.saveUser(u);
                        ((Activity) context).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                pd.dismiss();
                                textview.setText("已添加");
                                textview.setVisibility(View.VISIBLE);
                                button.setEnabled(false);
                                button.setVisibility(View.GONE);
                                button1.setEnabled(false);
                                button1.setVisibility(View.GONE);
                                msg.setStatus(InviteMesageStatus.AGREED);
                                // 更新db
                                ContentValues values = new ContentValues();
                                values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg
                                        .getStatus().ordinal());
                                messgeDao.updateMessage(msg.getId(), values);

                                // 巩固程序,即时将该好友存入好友列表

                                //  addFriendToList(msg.getFrom());

                            }
                        });
                    }else if((int)result.get("status")==5){

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(context, info,
                                        Toast.LENGTH_SHORT).show();
                                textview.setText("已添加");
                                textview.setVisibility(View.VISIBLE);
                                button.setEnabled(false);
                                button.setVisibility(View.GONE);
                                button1.setEnabled(false);
                                button1.setVisibility(View.GONE);
                                msg.setStatus(InviteMesageStatus.AGREED);
                                ContentValues values = new ContentValues();
                                values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg
                                        .getStatus().ordinal());
                                messgeDao.updateMessage(msg.getId(), values);
                            }
                        });
                    }else{
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(context, info,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, "同意失败: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }

}
