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
	// ��Ա����	
	private TextView tv_m_total;
	// ��Ա����
	int m_total = 0;
	// ��Ա�б�
	private ExpandGridView gridview;
	private RelativeLayout re_change_groupname;
	private RelativeLayout re_clear;
	
	// ɾ�����˳�	
	private Button exitBtn;
	
	// Ⱥ����
	private String group_name;
	// �Ƿ��ǹ���
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
		// ��ȡ��������groupid
		groupId = getIntent().getStringExtra("groupId");
		Log.e("236", "ChatRoomSettingActivity"+groupId);
		// ��ȡ���ظ�Ⱥ����
		ChatroomDao dao=new  ChatroomDao(context);
		group=dao.getRoom(groupId);
		// ��ȡ��װ��Ⱥ���������װ����ʾ��Ⱥ����Ⱥ���Ա����Ϣ��
		String group_name = group.getRoomname();
		tv_groupname.setText(group_name);
		member=group.getMember();
		m_total=member.length;
		tv_m_total.setText("(" + String.valueOf(m_total) + ")");
		// ����Ⱥ���Ա��Ϣ
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
		
		// ��ʾȺ���Աͷ����ǳ�
		showMembers(members);	
		re_change_groupname.setOnClickListener(this);	
		re_clear.setOnClickListener(this);	
		exitBtn.setOnClickListener(this);
	}

		// ��ʾȺ��Աͷ���ǳƵ�gridview
		@SuppressLint("ClickableViewAccessibility")
		private void showMembers(List<UserInfo> members2) {
		adapter = new GridAdapter(this, members2);
		gridview.setAdapter(adapter);
		
		// ����OnTouchListener,Ϊ����Ⱥ��������Ƴ�ɾ��ģ��
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
	case R.id.re_clear: // ��������¼
	    progressDialog.setMessage("�������Ⱥ��Ϣ...");
	    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    progressDialog.show();
	    // ��������Ҫ������и���ʾ����ֹ��¼̫�٣�ɾ��̫�죬����ʾ
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
* ���Ⱥ�����¼
*/
public void clearGroupHistory() {

	ChatEntityDao dao=new ChatEntityDao(context);
	dao.deleteMessageByUser(group.getRoomId());
	progressDialog.cancel();
}

/**
* Ⱥ���Աgridadapter
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
		
		    // ���һ��item�����˰�ť
		
		    if (position == getCount() - 1 && is_admin) {
		        tv_username.setText("");
		        badge_delete.setVisibility(View.GONE);
		        iv_avatar.setImageResource(R.drawable.icon_btn_deleteperson);
		
		        if (isInDeleteMode) {
		            // ������ɾ��ģʽ�£�����ɾ����ť
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
		            || (!is_admin && position == getCount() - 1)) { // ���Ⱥ���Ա��ť
		        tv_username.setText("");
		        badge_delete.setVisibility(View.GONE);
		        iv_avatar.setImageResource(R.drawable.jy_drltsz_btn_addperson);
		        // ������ɾ��ģʽ��,������Ӱ�ť
		        if (isInDeleteMode) {
		            convertView.setVisibility(View.GONE);
		        } else {
		            convertView.setVisibility(View.VISIBLE);
		        }
		        iv_avatar.setOnClickListener(new OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                
		                    // ����ѡ��ҳ��
		                    startActivity((new Intent(context,
		                            ChatRoomCreatActivity.class).putExtra(
		                            "groupId", groupId)));
		                 
		            }
		        });
		    }
		
		    else { // ��ͨitem����ʾȺ���Ա
		
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
		
		        // demoȺ���Ա��ͷ����Ĭ��ͷ�����ɿ������Լ�ȥ����ͷ��
		        if (isInDeleteMode) {
		            // �����ɾ��ģʽ�£���ʾ����ͼ��
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
		                    // �����ɾ���Լ���return
		                    if (EMChatManager.getInstance().getCurrentUser()
		                            .equals(userid)) {
		                        startActivity(new Intent(
		                                context,
		                                AlertDialog.class).putExtra("msg",
		                                "����ɾ���Լ�"));
		                        return;
		                    }
		                    if (!NetUtils.hasNetwork(getApplicationContext())) {
		                        Toast.makeText(
		                                getApplicationContext(),
		                                "���粻����",
		                                Toast.LENGTH_SHORT).show();
		                        return;
		                    }
		
		                    deleteMembersFromGroup(userid);
		                } else {
		                    // ��������µ��user�����Խ����û������������ҳ��ȵ�
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
//            // ���±�������
//            EMGroupManager.getInstance().createOrUpdateLocalGroup(
//                    returnGroup);
//
//            runOnUiThread(new Runnable() {
//                public void run() {
//
//                    if (group != null) {
//                        // ���ó�ʼ���γ�ʼ״̬
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
	// *** ��Ҫ����������ʵ������Ч����.
	// ���ô��ڵ�����ҳ��,shrew_exit_dialog.xml�ļ��ж���view����
	window.setContentView(R.layout.social_alertdialog);
	// �����ܵ������뷨
	dlg.getWindow().clearFlags(
	        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
	// Ϊȷ�ϰ�ť����¼�,ִ���˳�Ӧ�ò���
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
	            // �����Ⱥ��ֱ�ӵ��ñ���SDK��API
	            if (is_admin) {
	                EMGroupManager.getInstance().changeGroupName(groupId,
	                        updateStr);
	
	            }
	            // ��ȺԱ��Ա��Ҫ���÷������˴���...
	            else {
	                updateGroupName(groupId, updateStr);
	
	            }
	            progressDialog.dismiss();
	            tv_groupname.setText(newName);
	            group_name = newName;
	            Toast.makeText(context, "�޸ĳɹ�",
	                    Toast.LENGTH_LONG).show();
	        } catch (EaseMobException e) {
	            Toast.makeText(context, "�޸�ʧ��",
	                    Toast.LENGTH_LONG).show();
	            e.printStackTrace();
	        }
	
	        dlg.cancel();
	    }
	});
	// �ر�alert�Ի����
	Button cancel = (Button) window.findViewById(R.id.btn_cancel);
	cancel.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
	        dlg.cancel();
	    }
	});

}

/**
* ɾ��Ⱥ��Ա
* 
* @param username
*/
protected void deleteMembersFromGroup(final String username) {
final ProgressDialog deleteDialog = new ProgressDialog(
        context);
// ��ɾ�������Լ���ʱ��,��ζ�ž�����Ⱥ��Ⱥ����Ⱥ��Ҫ��ɢȺ�ģ�����Ҫ���ж�
if (!username.equals("")) {
    deleteDialog.setMessage("�����˳�...");
    deleteDialog.setCanceledOnTouchOutside(false);
    deleteDialog.show();
    // ��Ⱥ���˳�
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
            // Ⱥ��Ա�˳��Ժ�Ҫ����Ⱥ��Ϣ��Ҳ�ͷ�װ��Ⱥ��..
            updateGroupName(groupId, updateStr);
            EMGroupManager.getInstance().exitFromGroup(groupId);
            deleteDialog.dismiss();
            Toast.makeText(context, "�˳��ɹ�",
                    Toast.LENGTH_LONG).show();
            setResult(100);
            finish();
        } catch (EaseMobException e) {
            deleteDialog.dismiss();
            Toast.makeText(context, "�˳�ʧ��",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    // Ⱥ����Ⱥ
    else {

        try {
            EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
            deleteDialog.dismiss();
            Toast.makeText(context, "�˳��ɹ�",
                    Toast.LENGTH_LONG).show();
            setResult(100);
            finish();
        } catch (EaseMobException e) {
            deleteDialog.dismiss();
            Toast.makeText(context, "�˳�ʧ��",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }// �첽ִ��
    }

}
// Ⱥ��ɾȺ��Ա����
else {
    deleteDialog.setMessage("�����Ƴ�...");
    deleteDialog.setCanceledOnTouchOutside(false);
    deleteDialog.show();
    try {
        EMGroupManager.getInstance().removeUserFromGroup(groupId,
                username);
        for (int i = 0; i < members.size(); i++) {
            UserInfo user = members.get(i);
            if (user.getUsername().equals(username)) {
                // �Ƴ���ɾ��Ա��Ϣ
                members.remove(user);
                adapter.notifyDataSetChanged();
                m_total = members.size();
                tv_m_total.setText("(" + String.valueOf(m_total) + ")");
                JSONObject newJSON = new JSONObject();
                newJSON.put("groupname", group_name);
                // �ڷ�װ��������ȡ��ɾ����Ա�����Ҹ���
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
        Toast.makeText(context, "�Ƴ��ɹ�",
                Toast.LENGTH_LONG).show();
    } catch (EaseMobException e) {
        deleteDialog.dismiss();
        Toast.makeText(context, "�Ƴ�ʧ��",
                Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }// �첽ִ��

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
                // ֪ͨ����Ա������

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
