package com.eventer.app.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.ui.base.BaseActivity;
import com.eventer.app.util.FileUtil;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.widget.ExpandGridView;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
	private FileUtil fileUtil;
	// 删除并退出
	private Button exitBtn;
	List<UserInfo> members = new ArrayList<UserInfo>();
	String[] member;
	String[] display;
	int display_index=0;
	String longClickUsername = null;

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
		tv_groupname = (TextView) findViewById(R.id.tv_groupname);
		tv_m_total = (TextView) findViewById(R.id.tv_m_total);

		gridview = (ExpandGridView) findViewById(R.id.gridview);

		re_change_groupname = (RelativeLayout) findViewById(R.id.re_change_groupname);

		re_clear = (RelativeLayout) findViewById(R.id.re_clear);

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

		getGroupMember(groupId);
		re_change_groupname.setOnClickListener(this);
		re_clear.setOnClickListener(this);
		exitBtn.setOnClickListener(this);
		exitBtn.setVisibility(View.GONE);
	}

	// 显示群成员头像昵称的gridview
	@SuppressLint("ClickableViewAccessibility")
	private void showMembers() {


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
				//showNameAlert();
				break;
			case R.id.btn_exit_grp:
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
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.social_chatsetting_gridview_item, null);
				holder=new ViewHolder();
				holder.iv_avatar =  (ImageView)convertView.findViewById(R.id.iv_avatar);
				holder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
				convertView.setTag(holder);

			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.tv_username.setText("");
			if (position == getCount() - 1){ // 添加群组成员按钮
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
			LoadUserAvatar avatarLoader = new LoadUserAvatar(context, Constant.IMAGE_PATH);
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

		Map<String, String> map = new HashMap<String, String>();
		map.put("group_id", groupId);
		map.put("uid", Constant.UID);

		LoadDataFromHTTP task = new LoadDataFromHTTP(
				context, Constant.URL_GET_GROUP_MEMBER, map);
		task.getData(new DataCallBack() {
			@SuppressLint("ShowToast")
			@Override
			public void onDataCallBack(JSONObject data) {
				try {
					int status = data.getInteger("status");
					if (status == 0) {
						JSONObject json=data.getJSONObject("group_action");
						String memberinfo=json.getString("members");
						member=memberinfo.split(";");
						display=new String[member.length];
						display_index=0;
						for (int i = 0; i < member.length; i++) {
							members.add(i,null);
						}
						for (int i = 0; i < member.length; i++) {
							String info=member[i];
							if (info.equals(Constant.UID)) {
								UserInfo user = new UserInfo();
								user.setAvatar(LocalUserInfo.getInstance(context).getUserInfo(
										"avatar"));
								user.setNick(LocalUserInfo.getInstance(context).getUserInfo(
										"nick"));
								user.setUsername(info);
								synchronized (members){
									members.set(i, user);
								}
								synchronized (adapter){
									adapter.notifyDataSetChanged();
								}
								try {
									display[i]=LocalUserInfo.getInstance(context).getUserInfo(
											"nick");
									saveDisplay();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								Map<String, UserInfo> map = MyApplication.getInstance()
										.getUserList();
								if (map.containsKey(info)){
									UserInfo user=map.get(info);
									String nick=new UserDao(context).getNick(info);
									if(nick!=null){
										user.setNick(nick);
									}
									synchronized (members){
										members.set(i, user);
									}
									synchronized (adapter){
										adapter.notifyDataSetChanged();
									}
									try {
										display[i]=nick;
										saveDisplay();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}else{
									getUserInfo(info,i);
								}
							}
						}
						ContentValues values = new ContentValues();
						values.put(ChatroomDao.COLUMN_NAME_MEMVBER,ListToString(member));
						ChatroomDao dao = new ChatroomDao(context);
						dao.update(values,groupId);
						tv_m_total.setText("("+member.length+")");

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
					if(group!=null){

					}
					String group_name = group.getRoomname();
					tv_groupname.setText(group_name);
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
							members.add(i,user);
						} else {
							Map<String, UserInfo> map = MyApplication.getInstance()
									.getUserList();
							UserInfo user = map.get(member[i]);
							if (user != null)
								members.add(i,user);
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
				str+=list[i]+",";
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
		Map<String,String> map=new HashMap<String, String>();
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
							synchronized (members){
								members.set(pos, user);
							}
							synchronized (adapter){
								adapter.notifyDataSetChanged();
							}
							Toast.makeText(context, "http-"+pos,
									Toast.LENGTH_SHORT).show();
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
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
