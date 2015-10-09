package com.eventer.app.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.NetUtils;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.ChatroomDao;
import com.eventer.app.entity.ChatRoom;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.http.UploadPicToServer;
import com.eventer.app.http.UploadPicToServer.DataCallBack;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.ui.base.BaseActivity;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.widget.ExpandGridView;

public class ChatRoomSettingActivity extends BaseActivity implements
OnClickListener {
	private TextView tv_groupname;
	// 成员总数	
	private TextView tv_m_total;
	// 成员总数
	int m_total = 0;
	// 成员列表
	private ExpandGridView gridview;
	private RelativeLayout re_change_groupname;
	private RelativeLayout re_clear;
	
	// 删除并退出	
	private Button exitBtn;
	
	// 群名称
	private String group_name;
	// 是否是管主
	boolean is_admin = false;
	List<UserInfo> members = new ArrayList<UserInfo>();
	String[] member;
	String longClickUsername = null;
	
	private String groupId;
	private ChatRoom group;
	
	
	private GridAdapter adapter;
	
	public static ChatRoomSettingActivity instance;
	private ProgressDialog progressDialog;
	private JSONArray jsonarray;
    private Context context;

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_chat_room_setting);
	instance = this;
	context=this;
	initView();
	initData();
	updateGroup();
}

private void initView() {
	progressDialog = new ProgressDialog(context);
	tv_groupname = (TextView) findViewById(R.id.tv_groupname);
	
	tv_m_total = (TextView) findViewById(R.id.tv_m_total);
	
	gridview = (ExpandGridView) findViewById(R.id.gridview);
	
	re_change_groupname = (RelativeLayout) findViewById(R.id.re_change_groupname);
	
	re_clear = (RelativeLayout) findViewById(R.id.re_clear);

	exitBtn = (Button) findViewById(R.id.btn_exit_grp);

}

private void initData() {
		// 获取传过来的groupid
		groupId = getIntent().getStringExtra("groupId");
		Log.e("236", "ChatRoomSettingActivity"+groupId);
		// 获取本地该群数据
		ChatroomDao dao=new  ChatroomDao(context);
		group=dao.getRoom(groupId);
		// 获取封装的群名（里面封装了显示的群名和群组成员的信息）
		String group_name = group.getRoomname();
		tv_groupname.setText(group_name);
		member=group.getMember();
		m_total=member.length;
		tv_m_total.setText("(" + String.valueOf(m_total) + ")");
		// 解析群组成员信息
		for (int i = 0; i < member.length; i++) {
			if(member[i].equals(Constant.UID)){
				UserInfo user=new UserInfo();
				user.setAvatar(LocalUserInfo.getInstance(context).getUserInfo("avatar"));
				user.setNick(LocalUserInfo.getInstance(context).getUserInfo("nick"));
				user.setUsername(member[i]);
				members.add(user);
			}else{
				Map<String,UserInfo> map=MyApplication.getInstance().getUserList();
			    UserInfo user=map.get(member[i]);
			    if(user!=null)
			      members.add(user);
			}
		}
		
		// 显示群组成员头像和昵称
		showMembers(members);	
		re_change_groupname.setOnClickListener(this);	
		re_clear.setOnClickListener(this);	
		exitBtn.setOnClickListener(this);
	}

		// 显示群成员头像昵称的gridview
		@SuppressLint("ClickableViewAccessibility")
		private void showMembers(List<UserInfo> members2) {
		adapter = new GridAdapter(this, members2);
		gridview.setAdapter(adapter);
		
		// 设置OnTouchListener,为了让群主方便地推出删除模》
		gridview.setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        switch (event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		            if (adapter.isInDeleteMode) {
		                adapter.isInDeleteMode = false;
		                adapter.notifyDataSetChanged();
		                return true;
		            }
		            break;
		        default:
		            break;
		        }
		        return false;
		    }
		});

}

@Override
public void onClick(View v) {
	switch (v.getId()) {
	case R.id.re_clear: // 清空聊天记录
	    progressDialog.setMessage("正在清空群消息...");
	    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    progressDialog.show();
	    // 按照你们要求必须有个提示，防止记录太少，删得太快，不提示
	    clearGroupHistory();
	    break;
	case R.id.re_change_groupname:
	    showNameAlert();
	    break;
	case R.id.btn_exit_grp:
	
//	    deleteMembersFromGroup(hxid);
	    break;
	
	default:
	    break;
}

}

/**
* 清空群聊天记录
*/
public void clearGroupHistory() {

	ChatEntityDao dao=new ChatEntityDao(context);
	dao.deleteMessageByUser(group.getRoomId());
	progressDialog.cancel();
}

/**
* 群组成员gridadapter
* 
* @author admin_new
* 
*/
private class GridAdapter extends BaseAdapter {

		public boolean isInDeleteMode;
		private List<UserInfo> objects;
		Context context;
		private LoadUserAvatar avatarLoader;
		
		public GridAdapter(Context context, List<UserInfo> members2) {
		
		    this.objects = members2;
		    this.context = context;
		    isInDeleteMode = false;
		    avatarLoader = new LoadUserAvatar(context, "/sdcard/fanxin/");
		}
		
		@Override
		public View getView(final int position, View convertView,
		        final ViewGroup parent) {
		    if (convertView == null) {
		        convertView = LayoutInflater.from(context).inflate(
		                R.layout.social_chatsetting_gridview_item, null);
		    }
		    ImageView iv_avatar = (ImageView) convertView
		            .findViewById(R.id.iv_avatar);
		    TextView tv_username = (TextView) convertView
		            .findViewById(R.id.tv_username);
		    ImageView badge_delete = (ImageView) convertView
		            .findViewById(R.id.badge_delete);
		
		    // 最后一个item，减人按钮
		
		    if (position == getCount() - 1 && is_admin) {
		        tv_username.setText("");
		        badge_delete.setVisibility(View.GONE);
		        iv_avatar.setImageResource(R.drawable.icon_btn_deleteperson);
		
		        if (isInDeleteMode) {
		            // 正处于删除模式下，隐藏删除按钮
		            convertView.setVisibility(View.GONE);
		        } else {
		
		            convertView.setVisibility(View.VISIBLE);
		        }
		
		        iv_avatar.setOnClickListener(new OnClickListener() {
		
		            @Override
		            public void onClick(View v) {
		                isInDeleteMode = true;
		                notifyDataSetChanged();
		            }
		
		        });
		
		    } else if ((is_admin && position == getCount() - 2)
		            || (!is_admin && position == getCount() - 1)) { // 添加群组成员按钮
		        tv_username.setText("");
		        badge_delete.setVisibility(View.GONE);
		        iv_avatar.setImageResource(R.drawable.jy_drltsz_btn_addperson);
		        // 正处于删除模式下,隐藏添加按钮
		        if (isInDeleteMode) {
		            convertView.setVisibility(View.GONE);
		        } else {
		            convertView.setVisibility(View.VISIBLE);
		        }
		        iv_avatar.setOnClickListener(new OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                
		                    // 进入选人页面
		                    startActivity((new Intent(context,
		                            ChatRoomCreatActivity.class).putExtra(
		                            "groupId", groupId)));
		                 
		            }
		        });
		    }
		
		    else { // 普通item，显示群组成员
		
		        UserInfo user = objects.get(position);
		        String usernick = user.getNick();
		        final String userid = user.getUsername();
		        final String useravatar = user.getAvatar();
		        tv_username.setText(usernick);
		        iv_avatar.setImageResource(R.drawable.default_avatar);
		        iv_avatar.setTag(useravatar);
		        if (useravatar != null && !useravatar.equals("")) {
		            Bitmap bitmap = avatarLoader.loadImage(iv_avatar,
		                    useravatar, new ImageDownloadedCallBack() {
		
		                        @Override
		                        public void onImageDownloaded(
		                                ImageView imageView, Bitmap bitmap,int status) {
		                            if (imageView.getTag() == useravatar&&status==-1) {
		                                imageView.setImageBitmap(bitmap);
		
		                            }
		                        }
		
		                    });
		
		            if (bitmap != null) {
		
		                iv_avatar.setImageBitmap(bitmap);
		
		            }
		
		        }
		
		        // demo群组成员的头像都用默认头像，需由开发者自己去设置头像
		        if (isInDeleteMode) {
		            // 如果是删除模式下，显示减人图标
		            convertView.findViewById(R.id.badge_delete).setVisibility(
		                    View.VISIBLE);
		        } else {
		            convertView.findViewById(R.id.badge_delete).setVisibility(
		                    View.INVISIBLE);
		        }
		        iv_avatar.setOnClickListener(new OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                if (isInDeleteMode) {
		                    // 如果是删除自己，return
		                    if (EMChatManager.getInstance().getCurrentUser()
		                            .equals(userid)) {
		                        startActivity(new Intent(
		                                context,
		                                AlertDialog.class).putExtra("msg",
		                                "不能删除自己"));
		                        return;
		                    }
		                    if (!NetUtils.hasNetwork(getApplicationContext())) {
		                        Toast.makeText(
		                                getApplicationContext(),
		                                "网络不可用",
		                                Toast.LENGTH_SHORT).show();
		                        return;
		                    }
		
		                    deleteMembersFromGroup(userid);
		                } else {
		                    // 正常情况下点击user，可以进入用户详情或者聊天页面等等
		                     if(!userid.equals(Constant.UID)){
		 						Intent intent=new Intent();
		 						intent.putExtra("user", userid);
		 						intent.setClass(context, Activity_UserInfo.class);
		 						context.startActivity(intent);
		 					}else{
		 						Intent intent=new Intent();
		 						intent.setClass(context,MyUserInfoActivity.class);
		 						context.startActivity(intent);
		 					}
		
		                }
		            }
		
		        });
		
		    }
		    return convertView;
		}
		
		@Override
		public int getCount() {
		    if (is_admin) {
		        return objects.size() + 2;
		    } else {
		
		        return objects.size() + 1;
		
		    }
		
		}
		
		@Override
		public Object getItem(int position) {
		    // TODO Auto-generated method stub
		    return objects.get(position);
		}
		
		@Override
		public long getItemId(int position) {
		    // TODO Auto-generated method stub
		    return position;
		}
}

protected void updateGroup() {
new Thread(new Runnable() {
    public void run() {
        try {
//            EMGroup returnGroup = EMGroupManager.getInstance()
//                    .getGroupFromServer(groupId);
//            // 更新本地数据
//            EMGroupManager.getInstance().createOrUpdateLocalGroup(
//                    returnGroup);
//
//            runOnUiThread(new Runnable() {
//                public void run() {
//
//                    if (group != null) {
//                        // 设置初始屏蔽初始状态
//                        if (group.getMsgBlocked()) {
//                            iv_switch_block_groupmsg
//                                    .setVisibility(View.VISIBLE);
//                            iv_switch_unblock_groupmsg
//                                    .setVisibility(View.INVISIBLE);
//                        } else {
//                            iv_switch_block_groupmsg
//                                    .setVisibility(View.INVISIBLE);
//                            iv_switch_unblock_groupmsg
//                                    .setVisibility(View.VISIBLE);
//                        }
//                    }
//
//                }
//            });

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                public void run() {

                }
            });
        }
    }
}).start();
}

private void showNameAlert() {

	final AlertDialog dlg = new AlertDialog.Builder(this).create();
	dlg.show();
	Window window = dlg.getWindow();
	// *** 主要就是在这里实现这种效果的.
	// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
	window.setContentView(R.layout.social_alertdialog);
	// 设置能弹出输入法
	dlg.getWindow().clearFlags(
	        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
	// 为确认按钮添加事件,执行退出应用操作
	Button ok = (Button) window.findViewById(R.id.btn_ok);
	final EditText ed_name = (EditText) window.findViewById(R.id.ed_name);
	
	ok.setOnClickListener(new View.OnClickListener() {
	    @SuppressLint("ShowToast")
	    public void onClick(View v) {
	        final String newName = ed_name.getText().toString().trim();
	
	        if (TextUtils.isEmpty(newName)) {
	            return;
	        }
	
	        try {
	            JSONObject newJSON = new JSONObject();
	            newJSON.put("groupname", newName);
	            newJSON.put("jsonArray", jsonarray);
	            String updateStr = newJSON.toJSONString();
	            // 如果是群主直接调用本地SDK的API
	            if (is_admin) {
	                EMGroupManager.getInstance().changeGroupName(groupId,
	                        updateStr);
	
	            }
	            // 非群员成员需要调用服务器端代码...
	            else {
	                updateGroupName(groupId, updateStr);
	
	            }
	            progressDialog.dismiss();
	            tv_groupname.setText(newName);
	            group_name = newName;
	            Toast.makeText(context, "修改成功",
	                    Toast.LENGTH_LONG).show();
	        } catch (EaseMobException e) {
	            Toast.makeText(context, "修改失败",
	                    Toast.LENGTH_LONG).show();
	            e.printStackTrace();
	        }
	
	        dlg.cancel();
	    }
	});
	// 关闭alert对话框架
	Button cancel = (Button) window.findViewById(R.id.btn_cancel);
	cancel.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
	        dlg.cancel();
	    }
	});

}

/**
* 删除群成员
* 
* @param username
*/
protected void deleteMembersFromGroup(final String username) {
final ProgressDialog deleteDialog = new ProgressDialog(
        context);
// 当删除的是自己的时候,意味着就是退群。群主退群是要解散群的，所以要有判断
if (!username.equals("")) {
    deleteDialog.setMessage("正在退出...");
    deleteDialog.setCanceledOnTouchOutside(false);
    deleteDialog.show();
    // 非群主退出
    if (!is_admin) {

        try {

            JSONObject newJSON = new JSONObject();
            newJSON.put("groupname", group_name);
            for (int n = 0; n < jsonarray.size(); n++) {
                JSONObject jsontemp = (JSONObject) jsonarray.get(n);
                if (jsontemp.getString("hxid").equals(username)) {
                    jsonarray.remove(jsontemp);
                }
            }

            newJSON.put("jsonArray", jsonarray);
            String updateStr = newJSON.toJSONString();
            // 群成员退出以后要更新群信息，也就封装的群名..
            updateGroupName(groupId, updateStr);
            EMGroupManager.getInstance().exitFromGroup(groupId);
            deleteDialog.dismiss();
            Toast.makeText(context, "退出成功",
                    Toast.LENGTH_LONG).show();
            setResult(100);
            finish();
        } catch (EaseMobException e) {
            deleteDialog.dismiss();
            Toast.makeText(context, "退出失败",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    // 群主退群
    else {

        try {
            EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
            deleteDialog.dismiss();
            Toast.makeText(context, "退出成功",
                    Toast.LENGTH_LONG).show();
            setResult(100);
            finish();
        } catch (EaseMobException e) {
            deleteDialog.dismiss();
            Toast.makeText(context, "退出失败",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }// 异步执行
    }

}
// 群主删群成员操作
else {
    deleteDialog.setMessage("正在移除...");
    deleteDialog.setCanceledOnTouchOutside(false);
    deleteDialog.show();
    try {
        EMGroupManager.getInstance().removeUserFromGroup(groupId,
                username);
        for (int i = 0; i < members.size(); i++) {
            UserInfo user = members.get(i);
            if (user.getUsername().equals(username)) {
                // 移除被删成员信息
                members.remove(user);
                adapter.notifyDataSetChanged();
                m_total = members.size();
                tv_m_total.setText("(" + String.valueOf(m_total) + ")");
                JSONObject newJSON = new JSONObject();
                newJSON.put("groupname", group_name);
                // 在封装数据里面取出删除成员，并且更新
                for (int n = 0; n < jsonarray.size(); n++) {

                    JSONObject jsontemp = (JSONObject) jsonarray.get(n);
                    if (jsontemp.getString("hxid").equals(username)) {
                        jsonarray.remove(jsontemp);
                    }
                }

                newJSON.put("jsonArray", jsonarray);
                String updateStr = newJSON.toJSONString();
                Log.e("updateStr------>>>>>0", updateStr);

                EMGroupManager.getInstance().changeGroupName(groupId,
                        updateStr);

            }

        }

        deleteDialog.dismiss();
        Toast.makeText(context, "移除成功",
                Toast.LENGTH_LONG).show();
    } catch (EaseMobException e) {
        deleteDialog.dismiss();
        Toast.makeText(context, "移除失败",
                Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }// 异步执行

}

}

private void updateGroupName(String groupId, String updateStr) {

Map<String, String> map = new HashMap<String, String>();
map.put("groupId", groupId);
map.put("groupName", updateStr);
UploadPicToServer task = new UploadPicToServer(
        context, Constant.URL_UPDATE_Groupnanme,
        map);

task.getData(new DataCallBack() {

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

public void back(View view) {
	setResult(RESULT_OK);
	finish();
}

@Override
public void onBackPressed() {
	setResult(RESULT_OK);
	finish();
}

@Override
protected void onDestroy() {
	super.onDestroy();
	instance = null;
}

}
