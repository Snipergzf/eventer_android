package com.eventer.app.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.ChatroomDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.ChatRoom;
import com.eventer.app.entity.User;
import com.eventer.app.main.MainActivity;
import com.eventer.app.socket.Activity_Chat;
import com.eventer.app.task.LoadDataFromServer;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.ui.base.BaseActivity;
import com.eventer.app.util.LocalUserInfo;


 
@SuppressLint({ "InflateParams", "SdCardPath" })
public class CreatChatRoomActivity extends BaseActivity {
    private ImageView iv_search;
    private TextView tv_checked;
    private ListView listView;
    /** 是否为一个新建的群组 */
    protected boolean isCreatingNewGroup;
    /** 是否为单选 */
    private boolean isSignleChecked;
    private PickContactAdapter contactAdapter;
    /** group中一开始就有的成员 */
    private List<String> exitingMembers = new ArrayList<String>();
    // 可滑动的显示选中用户的View
    private LinearLayout menuLinerLayout;

    // 选中用户总数,右上角显示
    int total = 0;
    private String userId = null;
    private String groupId = null;
    private ProgressDialog progressDialog;
    private String groupname;
    // 添加的列表
    private List<String> addList = new ArrayList<String>();
    private String hxid;
    private ChatRoom group;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        context=this;
        hxid = LocalUserInfo.getInstance(CreatChatRoomActivity.this)
                .getUserInfo("hxid");

        progressDialog = new ProgressDialog(this);
        groupId = getIntent().getStringExtra("groupId");
        userId = getIntent().getStringExtra("userId");

        tv_checked = (TextView) this.findViewById(R.id.tv_checked);

        if (groupId != null) {
            isCreatingNewGroup = false;
            ChatroomDao dao=new ChatroomDao(context);
            group=dao.getRoom(groupId);
           if (group != null) {
            	String[] members=group.getMember();
            	if(members!=null&&members.length>0){
            		exitingMembers=new ArrayList<String>(Arrays.asList(members));
            	}
                groupname = group.getRoomname();
            }
        }else if (userId != null) {
            isCreatingNewGroup = true;
            exitingMembers.add(userId);
            total = 1;
            addList.add(userId);
        } else {
            isCreatingNewGroup = true;
        }

        // 获取好友列表
        final List<User> alluserList = new ArrayList<User>();
        UserDao dao=new UserDao(context);
        List<User> users=dao.getFriendList();
        for (User user : users) {
        	alluserList.add(user);
		}
 
        // 对list进行排序
        Collections.sort(alluserList, new PinyinComparator() {
        });

        listView = (ListView) findViewById(R.id.list);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View headerView = layoutInflater.inflate(R.layout.item_chatroom_header,
                null);
        TextView tv_header = (TextView) headerView.findViewById(R.id.tv_header);
        tv_header.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreatChatRoomActivity.this,
                        ChatRoomActivity.class));
                finish();
            }

        });
        menuLinerLayout = (LinearLayout) this
                .findViewById(R.id.linearLayoutMenu);

        final EditText et_search = (EditText) this.findViewById(R.id.et_search);

        et_search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                if (s.length() > 0) {
                    String str_s = et_search.getText().toString().trim();
                    List<User> users_temp = new ArrayList<User>();
                    for (User user : alluserList) {
                        String usernick = user.getNick();
                        if (usernick.contains(str_s)) {

                            users_temp.add(user);
                        }
                        contactAdapter = new PickContactAdapter(
                                CreatChatRoomActivity.this,
                                R.layout.item_contactlist_listview_checkbox,
                                users_temp);
                        listView.setAdapter(contactAdapter);

                    }

                } else {
                    contactAdapter = new PickContactAdapter(
                            CreatChatRoomActivity.this,
                            R.layout.item_contactlist_listview_checkbox,
                            alluserList);
                    listView.setAdapter(contactAdapter);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });
        listView.addHeaderView(headerView);

        contactAdapter = new PickContactAdapter(this,
                R.layout.item_contactlist_listview_checkbox, alluserList);
        listView.setAdapter(contactAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.toggle();

            }
        });
        tv_checked.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                save();
            }

        });

        iv_search = (ImageView) this.findViewById(R.id.iv_search);

    }

    // 即时显示被选中用户的头像和昵称。

    private void showCheckImage(Bitmap bitmap, User glufineid) {

        if (exitingMembers.contains(glufineid.getUsername()) && groupId != null) {
            return;
        }
        if (addList.contains(glufineid.getUsername())) {
            return;
        }
        total++;
        // 包含TextView的LinearLayout
        // 参数设置
        android.widget.LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                108, 108, 1);
        View view = LayoutInflater.from(this).inflate(
                R.layout.item_chatroom_header_item_1, null);
        ImageView images = (ImageView) view.findViewById(R.id.iv_avatar1);
        menuLinerLayoutParames.setMargins(6, 6, 6, 6);

        // 设置id，方便后面删除
        view.setTag(glufineid);
        if (bitmap == null) {
            images.setImageResource(R.drawable.default_avatar);
        } else {
            images.setImageBitmap(bitmap);
        }

        menuLinerLayout.addView(view, menuLinerLayoutParames);
        tv_checked.setText("确定(" + total + ")");
        if (total > 0) {
            if (iv_search.getVisibility() == View.VISIBLE) {
                iv_search.setVisibility(View.GONE);
            }
        }
        addList.add(glufineid.getUsername());
    }

    private void deleteImage(User glufineid) {
        View view = (View) menuLinerLayout.findViewWithTag(glufineid);

        menuLinerLayout.removeView(view);
        total--;
        tv_checked.setText("确定(" + total + ")");
        addList.remove(glufineid.getUsername());
        if (total < 1) {
            if (iv_search.getVisibility() == View.GONE) {
                iv_search.setVisibility(View.VISIBLE);
            }
        }

    }

    /**
     * 确认选择的members
     * 
     * @param v
     */
    public void save() {
        if (addList.size() == 0) {
            Toast.makeText(CreatChatRoomActivity.this, "请选择用户",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // 如果只有一个用户说明只是单聊,并且不是从群组加人
        if (addList.size() == 1 && isCreatingNewGroup) {
            String userId = addList.get(0);
            
            User user = MyApplication.getInstance().getContactList()
                    .get(userId);
            if (user != null) {
                String userNick = user.getNick();
                String userAvatar = user.getAvatar();
                startActivity(new Intent(getApplicationContext(),
                        Activity_Chat.class).putExtra("userId", userId)
                        .putExtra("userNick", userNick)
                        .putExtra("userAvatar", userAvatar));

                finish();
            }

        } else {

            if (isCreatingNewGroup) {
                progressDialog.setMessage("正在创建群聊...");
            } else {
                progressDialog.setMessage("正在加人...");
            }
            progressDialog.show();
            creatNewGroup(addList);

        }

    }

    /**
     * 创建新群组
     * 
     * @param newmembers
     */
    private void creatNewGroup(List<String> members) {

        String nick = LocalUserInfo.getInstance(CreatChatRoomActivity.this)
                .getUserInfo("nick");

        String avatar = LocalUserInfo.getInstance(CreatChatRoomActivity.this)
                .getUserInfo("avatar");
        if (isCreatingNewGroup) {
           if(!members.contains(Constant.UID))
        	       members.add(Constant.UID+"");
           JSONObject obj=new JSONObject();
           obj.put("action", "join");
           obj.put("data", (String[])members.toArray(new String[members.size()]));
//           obj.put("data", new String[]{"3","20"});
           Log.e("1", obj.toJSONString());
           String groupmame=Constant.UID+"@"+System.currentTimeMillis()/1000;
          // MainActivity.instance.newMsg(0+"","group1", obj.toJSONString(), 49);
           MainActivity.instance.newMsg("group",groupmame, obj.toJSONString(), 49);
           ChatRoom room=new ChatRoom();
           room.setRoomId(groupmame);
           room.setTime(System.currentTimeMillis()/1000);
           room.setOwner(Constant.UID);
           String[] memberlist=new String[members.size()];
           String[] displaylist=new String[members.size()];
           for (int i=0;i<members.size();i++) {
        	   String id=members.get(i);
			   memberlist[i]=id;
			   if(!id.equals(Constant.UID)){
				   User u=MyApplication.getInstance().getContactList().get(id);
				   String name="";
				   if(!TextUtils.isEmpty(u.getBeizhu()))
					   name=u.getBeizhu();
				   if(!TextUtils.isEmpty(u.getNick()))
				       name=u.getNick();
				   displaylist[i]=name;
			   }else{
				   displaylist[i]=LocalUserInfo.getInstance(context).getUserInfo("nick");			   
			   }
			   
		   }
           room.setMember(memberlist);
           room.setDisplayname(displaylist);
           ChatroomDao dao=new ChatroomDao(context);
           dao.saveChatROOM(room);
           progressDialog.dismiss();
           String roomname;
           if(!TextUtils.isEmpty(room.getRoomname()))
        	   roomname=room.getRoomname();
           else
        	   roomname=ListToString(room.getDisplayname());
           startActivity(new Intent(getApplicationContext(),
                   Activity_Chat.class)
                   .putExtra("groupId", room.getRoomId())
                   .putExtra("chatType", Activity_Chat.CHATTYPE_GROUP)
                   .putExtra("groupName", roomname));

        } else {

        	JSONObject obj=new JSONObject();
            obj.put("action", "invite");
            obj.put("data", (String[])members.toArray(new String[members.size()]));
            Log.e("1", obj.toJSONString());
            
           // MainActivity.instance.newMsg(0+"","group1", obj.toJSONString(), 49);
            MainActivity.instance.newMsg("group",groupId, obj.toJSONString(), 49);
            progressDialog.dismiss();
//            try {
//                if (hxid.equals(group.getOwner())) {
//                    EMGroupManager.getInstance().addUsersToGroup(groupId,
//                            members.toArray(new String[0]));
//                } else {
//                    EMGroupManager.getInstance().inviteUser(groupId,
//                            members.toArray(new String[0]), null);
//                }
//
//                JSONObject oldjson = JSONObject.parseObject(groupname);
//                JSONArray oldjsonArray = oldjson.getJSONArray("jsonArray");
//
//                String groupName = oldjson.getString("groupname");
//                for (int i = 0; i < members.size(); i++) {
//
//                    User user = MyApplication.getInstance().getContactList()
//                            .get(members.get(i));
//                    if (user != null) {
//                        JSONObject json_member = new JSONObject();
//                        json_member.put("hxid", user.getUsername());
//                        json_member.put("nick", user.getNick());
//                        json_member.put("avatar", user.getAvatar());
//                        oldjsonArray.add(json_member);
//
//                    }
//                }
//                JSONObject finalJson = new JSONObject();
//                finalJson.put("jsonArray", oldjsonArray);
//
//                finalJson.put("groupname", groupName);
//                String groupJSON = finalJson.toJSONString();
//                if (hxid.equals(group.getOwner())) {
//                EMGroupManager.getInstance()
//                        .changeGroupName(groupId, groupJSON);//
//                }else{
//                    updateGroupName(groupId, groupJSON);
//                }
//                startActivity(new Intent(getApplicationContext(),
//                        Activity_Chat.class).putExtra("groupId", groupId)
//                        .putExtra("chatType", Activity_Chat.CHATTYPE_GROUP)
//                        .putExtra("groupName", groupName));
//
//                progressDialog.dismiss();
//            } catch (EaseMobException e) {
//                progressDialog.dismiss();
//                Toast.makeText(CreatChatRoomActivity.this, "群聊加人失败。。。",
//                        Toast.LENGTH_LONG).show();
//                e.printStackTrace();
//            }

        }

    }
    private String ListToString(String[] list){
		String str="";
		for(int i=0;i<list.length;i++){
			if(i<list.length-1){
				str+=list[i]+",";
			}else{
				str+=list[i];
			}
		}
		return str;
	}
    
    
    private void updateGroupName(String groupId, String updateStr) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("groupId", groupId);
        map.put("groupName", updateStr);
        LoadDataFromServer task = new LoadDataFromServer(
                getApplicationContext(), Constant.URL_UPDATE_Groupnanme,
                map);

        task.getData(new com.eventer.app.task.LoadDataFromServer.DataCallBack() {

            @Override
            public void onDataCallBack(JSONObject data) {
                if (data != null) {
                    int code = data.getInteger("code");

                    if (code != 1) {
                        // 通知管理员。。。

                    }

                }
            }
        });

    }


    /**
     * adapter
     */
    private class PickContactAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private boolean[] isCheckedArray;
        private Bitmap[] bitmaps;
        private LoadUserAvatar avatarLoader;
        private List<User> list = new ArrayList<User>();
        private int res;

        public PickContactAdapter(Context context, int resource,
                List<User> users) {

            layoutInflater = LayoutInflater.from(context);
            avatarLoader = new LoadUserAvatar(context, Constant.IMAGE_PATH);

            this.res = resource;
            this.list = users;
            bitmaps = new Bitmap[list.size()];
            isCheckedArray = new boolean[list.size()];

        }

        public Bitmap getBitmap(int position) {
            return bitmaps[position];
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int position, View convertView,
                ViewGroup parent) {

            convertView = layoutInflater.inflate(res, null);

            ImageView iv_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            TextView tv_name = (TextView) convertView
                    .findViewById(R.id.tv_name);
            TextView tvHeader = (TextView) convertView
                    .findViewById(R.id.header);
            final User user = list.get(position);

            final String avatar = user.getAvatar();
            String name = user.getNick();
            String header = user.getHeader();
            final String username = user.getUsername();
            tv_name.setText(name);
            iv_avatar.setImageResource(R.drawable.default_avatar);
            iv_avatar.setTag(avatar);
            Bitmap bitmap = null;
            if (avatar != null && !avatar.equals("")&&!avatar.equals("default")) {
                bitmap = avatarLoader.loadImage(iv_avatar, avatar,
                        new ImageDownloadedCallBack() {
                            @Override
                            public void onImageDownloaded(ImageView imageView,
                                    Bitmap bitmap,int status) {
                            	if(status==-1){
                            		if (imageView.getTag() == avatar) {
                                        imageView.setImageBitmap(bitmap);
                                    }
                            	}                     
                            }

                        });

                if (bitmap != null) {
                    iv_avatar.setImageBitmap(bitmap);
                }
                bitmaps[position] = bitmap;
            }
            if (position == 0 || header != null
                    && !header.equals(getItem(position - 1))) {
                if ("".equals(header)) {
                    tvHeader.setVisibility(View.GONE);
                } else {
                    tvHeader.setVisibility(View.VISIBLE);
                    tvHeader.setText(header);
                }
            } else {
                tvHeader.setVisibility(View.GONE);
            }

            // 选择框checkbox
            final CheckBox checkBox = (CheckBox) convertView
                    .findViewById(R.id.checkbox);

            if (exitingMembers != null && exitingMembers.contains(username)) {
                checkBox.setButtonDrawable(R.drawable.btn_check);
            } else {
                checkBox.setButtonDrawable(R.drawable.check_blue);
            }

            if (addList != null && addList.contains(username)) {
                checkBox.setChecked(true);
                isCheckedArray[position] = true;
            }
            if (checkBox != null) {
                checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        // 群组中原来的成员一直设为选中状态
                        if (exitingMembers.contains(username)) {
                            isChecked = true;
                            checkBox.setChecked(true);
                        }
                        isCheckedArray[position] = isChecked;
                        // 如果是单选模式
                        if (isSignleChecked && isChecked) {
                            for (int i = 0; i < isCheckedArray.length; i++) {
                                if (i != position) {
                                    isCheckedArray[i] = false;
                                }
                            }
                            contactAdapter.notifyDataSetChanged();
                        }

                        if (isChecked) {
                            // 选中用户显示在滑动栏显示
                            showCheckImage(contactAdapter.getBitmap(position),
                                    list.get(position));

                        } else {
                            // 用户显示在滑动栏删除
                            deleteImage(list.get(position));

                        }

                    }
                });
                // 群组中原来的成员一直设为选中状态
                if (exitingMembers.contains(username)) {
                    checkBox.setChecked(true);
                    isCheckedArray[position] = true;
                } else {
                    checkBox.setChecked(isCheckedArray[position]);
                }

            }
            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public String getItem(int position) {
            if (position < 0) {
                return "";
            }

            String header = list.get(position).getHeader();

            return header;

        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
    }

    public void back(View view) {
        finish();
    }

    @SuppressLint("DefaultLocale")
    public class PinyinComparator implements Comparator<User> {

        @SuppressLint("DefaultLocale")
        @Override
        public int compare(User o1, User o2) {
            // TODO Auto-generated method stub
            String py1 = o1.getHeader();
            String py2 = o2.getHeader();
            // 判断是否为空""
            if (isEmpty(py1) && isEmpty(py2))
                return 0;
            if (isEmpty(py1))
                return -1;
            if (isEmpty(py2))
                return 1;
            String str1 = "";
            String str2 = "";
            try {
                str1 = ((o1.getHeader()).toUpperCase()).substring(0, 1);
                str2 = ((o2.getHeader()).toUpperCase()).substring(0, 1);
            } catch (Exception e) {
                System.out.println("某个str为\" \" 空");
            }
            return str1.compareTo(str2);
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }

}
