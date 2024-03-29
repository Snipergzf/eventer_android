package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.ChatroomDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.ChatRoom;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.main.MainActivity;
import com.eventer.app.task.LoadImage;
import com.eventer.app.task.LoadImage.ImageDownloadedCallBack;
import com.eventer.app.util.FileUtil;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.view.ExpandGridView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@SuppressLint("SetTextI18n")
public class ChatRoomSettingActivity extends Activity implements
		OnClickListener {
	private TextView tv_groupname;
	// 成员总数
	private TextView tv_m_total;
	// 成员总数
	int m_total = 0;
	// 成员列表
	ExpandGridView gridview;
	private RelativeLayout re_change_groupname;
	private TextView tv_clear,tv_group_schedule;
	FileUtil fileUtil;
	// 删除并退出
	private Button exitBtn;
	private boolean isGroupMember = false;
	List<UserInfo> members = new ArrayList<>();
	String[] member;
	String[] display;
	int display_index=0;



	private String groupId;
	private ChatRoom group;

	private GridAdapter adapter;

	public static ChatRoomSettingActivity instance;
	private ProgressDialog progressDialog;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_room_setting);
		instance = this;
		context = this;
		fileUtil = new FileUtil(context, Constant.IMAGE_PATH);
		initView();
		initData();
	}

	private void initView() {
		progressDialog = new ProgressDialog(context);
		tv_groupname = (TextView) findViewById(R.id.tv_group_name);
		tv_m_total = (TextView) findViewById(R.id.tv_m_total);

		gridview = (ExpandGridView) findViewById(R.id.gridview);

		re_change_groupname = (RelativeLayout) findViewById(R.id.re_change_groupname);
		tv_clear = (TextView) findViewById(R.id.tv_clear);
		tv_group_schedule = (TextView) findViewById(R.id.tv_group_schedule);

		exitBtn = (Button) findViewById(R.id.btn_exit_grp);
		adapter = new GridAdapter(this, members);
		gridview.setAdapter(adapter);

		// 设置OnTouchListener,为了让群主方便地推出删除模》
		gridview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						break;
					default:
						break;
				}
				return false;
			}
		});

	}

	private void initData() {
		// 获取传过来的groupid
		groupId = getIntent().getStringExtra("groupId");

//		ChatroomDao dao = new ChatroomDao(context);
//		group = dao.getRoom(groupId);
//		// 获取封装的群名（里面封装了显示的群名和群组成员的信息）
//
//		if( group != null ){
//			String group_name = group.getRoomname();
//			if(!TextUtils.isEmpty(group_name)){
//				tv_groupname.setText(group_name);
//			}
//		}
		getGroupMember(groupId);
		re_change_groupname.setOnClickListener(this);
		tv_group_schedule.setOnClickListener(this);
		tv_clear.setOnClickListener(this);
		exitBtn.setOnClickListener(this);
//		exitBtn.setVisibility(View.GONE);
	}

	// 显示群成员头像昵称的gridview
//	@SuppressLint("ClickableViewAccessibility")
//	private void showMembers() {
//
//
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_clear: // 清空聊天记录
				progressDialog.setMessage("正在清空群消息...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.show();
				// 按照你们要求必须有个提示，防止记录太少，删得太快，不提示
				clearGroupHistory();
				break;
			case R.id.tv_group_schedule:
				startActivity(new Intent().setClass(context,GroupSchedualActivity.class)
				.putExtra("groupId",groupId));
				break;
			case R.id.re_change_groupname:
//				showNameAlert();
				startActivity(new Intent().setClass(context,ResetGroupNameActivity.class)
						.putExtra("groupId",groupId));
				break;
			case R.id.btn_exit_grp:
				//leave the chatroom
				JSONObject obj = new JSONObject();
				obj.put("action", "leave");
				obj.put("data", new String[]{Constant.UID});
				Log.e("1", obj.toJSONString());
				MainActivity.instance.newMsg("group", groupId, obj.toJSONString(),
						49);
				JSONObject send_json_ = new JSONObject();
				send_json_.put("id", Constant.UID);
				send_json_.put("nick", LocalUserInfo.getInstance(context).getUserInfo("nick"));
				JSONObject send_json = new JSONObject();
				send_json.put("action", "send");
				send_json.put("data", send_json_.toJSONString());
				send_json.put("type", Constant.GROUP_LEAVE_NOTIFICATION);
				String body = send_json.toJSONString();
				MainActivity.instance.newMsg(groupId, groupId, body, 49);
				ChatEntityDao dao1 = new ChatEntityDao(context);
				dao1.deleteMessageByUser(groupId);
				ChatroomDao dao = new ChatroomDao(context);
				dao.delRoom(groupId);
				setResult(Activity_Chat.RESULT_CODE_EXIT_GROUP,
						new Intent().putExtra("Id",groupId));
				finish();
				break;

			default:
				break;
		}

	}

	/**
	 * 清空群聊天记录
	 */
	public void clearGroupHistory() {

		ChatEntityDao dao = new ChatEntityDao(context);
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
		private List<UserInfo> objects;
		Context context;
		public GridAdapter(Context context, List<UserInfo> members2) {
			this.objects = members2;
			this.context = context;
		}

		public final class ViewHolder {
			ImageView iv_avatar;
			TextView tv_username;
		}

		@Override
		public View getView(final int position, View convertView,
							final ViewGroup parent) {
			ViewHolder holder ;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.social_chatsetting_gridview_item, parent, false);
				holder=new ViewHolder();
				holder.iv_avatar =  (ImageView)convertView.findViewById(R.id.iv_avatar);
				holder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
				convertView.setTag(holder);

			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.tv_username.setText("");
			holder.iv_avatar.setVisibility(View.VISIBLE);
			if (position == getCount() - 1){ // 添加群组成员按钮
				if(isGroupMember){
					holder.iv_avatar.setImageResource(R.drawable.jy_drltsz_btn_addperson);
					holder.iv_avatar.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							// 进入选人页面
							startActivity((new Intent(context,
									ChatRoomCreatActivity.class).putExtra(
									"groupId", groupId)));

						}
					});
				}else{
					holder.iv_avatar.setVisibility(View.GONE);
				}

			}

			else { // 普通item，显示群组成员

				UserInfo user = objects.get(position);
				if(user!=null){
					String usernick = user.getNick();
					final String userid = user.getUsername();
					final String useravatar = user.getAvatar();
					holder.tv_username.setText(usernick);
					holder.iv_avatar.setImageResource(R.drawable.default_avatar);
					holder.iv_avatar.setTag(useravatar);
					showAvatar(holder.iv_avatar, useravatar);
					holder.iv_avatar.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							if (!userid.equals(Constant.UID)) {
								Intent intent = new Intent();
								intent.putExtra("user", userid);
								intent.setClass(context,
										Activity_UserInfo.class);
								context.startActivity(intent);
							} else {
								Intent intent = new Intent();
								intent.setClass(context,
										MyUserInfoActivity.class);
								context.startActivity(intent);
							}

						}

					});
					convertView.setVisibility(View.VISIBLE);
				}else{
					convertView.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

		@Override
		public int getCount() {
			return objects.size()+1;
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

	private void showAvatar(final ImageView iv_avatar, final String useravatar) {
		// TODO Auto-generated method stub
		if (useravatar != null && !useravatar.equals("")) {
			LoadImage avatarLoader = new LoadImage(context, Constant.IMAGE_PATH);
			Bitmap bitmap = avatarLoader.loadImage(iv_avatar,
					useravatar, new ImageDownloadedCallBack() {
						@Override
						public void onImageDownloaded(
								ImageView imageView, Bitmap bitmap,
								int status) {
							if (imageView.getTag() == useravatar&& status == -1) {
								imageView.setImageBitmap(bitmap);

							}
						}

					});
			if (bitmap != null)
				iv_avatar.setImageBitmap(bitmap);

		}
	}

	private void getGroupMember(final String groupId) {

		Map<String, String> map = new HashMap<>();
		map.put("group_id", groupId);
		map.put("uid", Constant.UID);
		LoadDataFromHTTP task = new LoadDataFromHTTP(
				context, Constant.URL_GET_GROUP_INFO, map);
		task.getData(new DataCallBack() {
			@SuppressLint("ShowToast")
			@Override
			public void onDataCallBack(JSONObject data) {
				try {
					int status = data.getInteger("status");
					if (status == 0) {
						JSONObject action = data.getJSONObject("group_action");
						JSONObject group_info = action.getJSONObject("group_info");
						String memberinfo = null;
						String group_name = null;
						try{
							memberinfo = group_info.getString("group_member");
						}catch (Exception e){
							Log.e("get group member", "error");
						}

						try{
							group_name = group_info.getString("group_name");
						}catch (Exception e){
							Log.e("get group member", "error");
						}
						if( !TextUtils.isEmpty(memberinfo) ){
							member = memberinfo.split(";");
							display = new String[member.length];
							display_index = 0;
							for (int i = 0; i < member.length; i++) {
								members.add(i, null);
							}
							exitBtn.setVisibility(View.GONE);
							for (int i = 0; i < member.length; i++) {
								String info = member[i];
								if (info.equals(Constant.UID)) {
									isGroupMember = true;
									exitBtn.setVisibility(View.VISIBLE);
									UserInfo user = new UserInfo();
									user.setAvatar(LocalUserInfo.getInstance(context).getUserInfo(
											"avatar"));
									user.setNick(LocalUserInfo.getInstance(context).getUserInfo(
											"nick"));
									user.setUsername(info);

//								synchronized (members){
									members.set(i, user);
//								}
//								synchronized (adapter){
									adapter.notifyDataSetChanged();
//								}

									try {
										display[i] = LocalUserInfo.getInstance(context).getUserInfo(
												"nick");
										saveDisplay();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									Map<String, UserInfo> map = MyApplication.getInstance()
											.getUserList();
									if (map.containsKey(info)) {
										UserInfo user = map.get(info);
										String nick = new UserDao(context).getNick(info);
										if (nick != null) {
											user.setNick(nick);
										}
//									synchronized (members){
										members.set(i, user);
//									}
//									synchronized (adapter){
										adapter.notifyDataSetChanged();
//									}
										try {
											display[i] = nick;
											saveDisplay();
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									} else {
										getUserInfo(info, i);
									}
								}
							}
							ContentValues values = new ContentValues();
							if(!TextUtils.isEmpty(memberinfo))
								values.put(ChatroomDao.COLUMN_NAME_MEMVBER, memberinfo.replace(";",","));
							if(!TextUtils.isEmpty(group_name)){
								values.put(ChatroomDao.COLUMN_NAME_ROOMNAME, group_name);
								tv_groupname.setText(group_name);
							}
							ChatroomDao dao = new ChatroomDao(context);
							dao.update(values, groupId);
							tv_m_total.setText("(" + member.length + ")");
						}


					} else if (status == 27) {

						Toast.makeText(context, "不存在该群组信息...",
								Toast.LENGTH_SHORT).show();
						finish();
					} else {

						Toast.makeText(context, "服务器繁忙请重试...",
								Toast.LENGTH_SHORT).show();
					}

				} catch (JSONException e) {

					Toast.makeText(context, "数据解析错误...",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(context, "更新群信息失败...",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					ChatroomDao dao = new ChatroomDao(context);
					group = dao.getRoom(groupId);
					// 获取封装的群名（里面封装了显示的群名和群组成员的信息）

					if( group == null ){
						return;
					}

					member = group.getMember();
					m_total = member.length;
					tv_m_total.setText("(" + String.valueOf(m_total) + ")");
					// 解析群组成员信息
					for (int i = 0; i < member.length; i++) {
						if (member[i].equals(Constant.UID)) {
							UserInfo user = new UserInfo();
							user.setAvatar(LocalUserInfo.getInstance(context).getUserInfo(
									"avatar"));
							user.setNick(LocalUserInfo.getInstance(context).getUserInfo(
									"nick"));
							user.setUsername(member[i]);
							members.add(i, user);
						} else {
							Map<String, UserInfo> map = MyApplication.getInstance()
									.getUserList();
							UserInfo user = map.get(member[i]);
							if (user != null)
								members.add(i, user);
						}
					}
					adapter.notifyDataSetChanged();
				}


			}
		});


	}
	private String ListToString(String[] list){
		String str="";
		for(int i=0;i<list.length;i++){
			if(i<list.length-1){
				str+=list[i]+";";
			}else{
				str+=list[i];
			}
		}
		return str;
	}

	private void saveDisplay(){
		if(display_index==member.length-1){
			ContentValues values = new ContentValues();
			values.put(ChatroomDao.COLUMN_NAME_MEMBERNAME,ListToString(display));
			ChatroomDao dao = new ChatroomDao(context);
			dao.update(values,groupId);
		}
		display_index++;
	}

	private void getUserInfo(String uid,final int pos){
		Map<String,String> map=new HashMap<>();
		map.put("uid", uid);
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
							UserInfo user=new UserInfo();
							user.setAvatar(avatar);
							user.setNick(name);
							user.setType(22);
							user.setUsername(name);
							MyApplication.getInstance().addUser(user);
//							synchronized (members){
								members.set(pos, user);
//							}
//							synchronized (adapter){
								adapter.notifyDataSetChanged();
//							}

							try {
								display[pos]=name;
								saveDisplay();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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

	@Override
	protected void onResume() {
		super.onResume();
		ChatroomDao dao = new ChatroomDao(context);
		ChatRoom room = dao.getRoom(groupId);
		if(room != null){
			String groupName = room.getRoomname();
			if(!TextUtils.isEmpty(groupName)){
				tv_groupname.setText(groupName);
			}else{
				tv_groupname.setText(getText(R.string.no_name));
			}
		}
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
