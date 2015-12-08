package com.eventer.app.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.easemob.util.HanziToPinyin;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ViewHolder")
public class NewFriendsAdapter extends BaseAdapter {
    Context context;
    List<InviteMessage> msgs;
    private InviteMessgeDao messgeDao;
    private LoadUserAvatar avatarLoader;
    private boolean try_again=false;
    int total = 0;
    //private LoadUserAvatar avatarLoader;

    @SuppressLint("SdCardPath")
    public NewFriendsAdapter(Context context, List<InviteMessage> msgs) {
        this.context = context;
        this.msgs = msgs;
        messgeDao = new InviteMessgeDao(context);
        avatarLoader = new LoadUserAvatar(context, Constant.IMAGE_PATH);
        //avatarLoader = new LoadUserAvatar(context, "/sdcard/fanxin/");
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
        // int msg_id = msg.getId();
        // String userUid = msg.getFrom();
//        String reason_total = msg.getReason();
//        String[] sourceStrArray = reason_total.split("66split88");
        // 先附初值
        String name = msg.getName();

        String avatar = msg.getAvatar();
        String reason = "";
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
            if(reason!=null&&reason!=""){
            }
            else{
                reason="等待对方确认";
            }
        }else if ( msg.getStatus() == InviteMesageStatus.BEAGREED) {
            holder.tv_added.setText("已添加");
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
            holder.btn_ignore.setVisibility(View.GONE);
            if(reason!=null&&reason!=""){
            }
            else{
                reason="等待对方确认";
            }
        } else if (msg.getStatus() == InviteMesageStatus.AGREED){
            holder.tv_added.setText("已同意");
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
            holder.btn_ignore.setVisibility(View.GONE);
            if(reason!=null&&reason!=""){
            }
            else{
                reason="对方请求添加你为好友";
            }
        }else if(msg.getStatus()==InviteMesageStatus.REFUSED){
            holder.tv_added.setText("已忽略");
            holder.tv_added.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
            holder.btn_ignore.setVisibility(View.GONE);
            if(reason!=null&&reason!=""){}
            else{
                reason="对方请求添加你为好友";
            }
        }else {
            holder.tv_added.setVisibility(View.GONE);
            holder.btn_add.setVisibility(View.VISIBLE);
            holder.btn_ignore.setVisibility(View.VISIBLE);
            holder.btn_add.setTag(msg);
            if(reason!=null&&reason!=""){}
            else{reason="对方请求添加你为好友"; }
            holder.btn_add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    acceptInvitation(holder.btn_add, holder.btn_ignore,msg, holder.tv_added);
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
        // showUserAvatar(holder.iv_avatar,avatar);
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
        if (url_avatar != null && url_avatar.indexOf("http")!=-1) {
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
     * @param button
     * @param username
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
                    Map<String,Object> result=new HashMap<String, Object>();
                    if (msg.getGroupId() == null) // 同意好友请求
                    {

                        Map<String,String> map=new HashMap<String, String>();
                        map.put("uid", Constant.UID+"");
                        map.put("friend_id", msg.getId()+"");
                        map.put("certificate",msg.getCertification());
                        map.put("token", Constant.TOKEN);

                        result=HttpUnit.sendFriendComfirm(map);

                    }
                    else{

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

//    private void addFriendToList(final String hxid) {
//        Map<String, String> map_uf = new HashMap<String, String>();
//        map_uf.put("hxid", hxid);
//        LoadDataFromServer task = new LoadDataFromServer(null,
//                Constant.URL_Get_UserInfo, map_uf);
//        task.getData(new DataCallBack() {
//            public void onDataCallBack(JSONObject data) {
//                try {
//
//                    int code = data.getInteger("code");
//                    if (code == 1) {
//
//                        JSONObject json = data.getJSONObject("user");
//                        if (json != null && json.size() != 0) {
//
//                        }
//                        String nick = json.getString("nick");
//                        String avatar = json.getString("avatar");
//
//                        String hxid = json.getString("hxid");
//                        String fxid = json.getString("fxid");
//                        String region = json.getString("region");
//                        String sex = json.getString("sex");
//                        String sign = json.getString("sign");
//                        String tel = json.getString("tel");
//                        User user = new User();
//
//                        user.setUsername(hxid);
//                        user.setNick(nick);
//                        user.setAvatar(avatar);
//                        user.setFxid(fxid);
//                        user.setRegion(region);
//                        user.setSex(sex);
//                        user.setSign(sign);
//                        user.setTel(tel);
//                        setUserHearder(hxid,user);
//                        Map<String, User> userlist = DemoApplication
//                                .getInstance().getContactList();
//                        Map<String, User> map_temp = new HashMap<String, User>();
//                        map_temp.put(hxid, user);
//                        userlist.putAll(map_temp);
//                        // 存入内存
//                        DemoApplication.getInstance().setContactList(userlist);
//                        // 存入db
//                        UserDao dao = new UserDao(context);
//
//                        dao.saveContact(user);
//
//                    }
//
//                } catch (JSONException e) {
//
//                    e.printStackTrace();
//                }
//
//            }
//
//        });
//
//    }


    public void GetAvatar(final Object... params) {
        new AsyncTask<Object, Object,Map<String, Object>>() {

            @SuppressWarnings("unchecked")
            @Override
            protected Map<String, Object> doInBackground(Object... params) {
                Map<String, Object> status=new HashMap<String, Object>();
                try {
                    status=HttpUnit.sendGetAvatarRequest((Map<String, String>) params[0]);
                    return status;

                } catch (Throwable e) {
                    // TODO Auto-generated catch block
                    Log.e("1", e.toString());
                    return null;
                }
            }
            protected void onPostExecute(Map<String, Object> result) {
                if(result!=null){
                    int status=(int)result.get("status");
                    String info=(String)result.get("info");
                    if(status==0){
                        Log.e("1", "获取头像地址成功！");

                        try_again=true;

                    }else {


                    }
                }
            };

        }.execute(params);}

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param username
     * @param user
     */
    @SuppressLint("DefaultLocale")
    protected void setUserHearder(String username, User user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        headerName = headerName.trim();
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance()
                    .get(headerName.substring(0, 1)).get(0).target.substring(0,
                            1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }
}
