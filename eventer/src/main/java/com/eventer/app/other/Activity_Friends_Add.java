package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.InviteMessgeDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.InviteMessage;
import com.eventer.app.entity.InviteMessage.InviteMesageStatus;
import com.eventer.app.entity.User;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.main.MainActivity;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.view.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Friends_Add extends SwipeBackActivity {
	TextView tv_send;
	private EditText et_reason;
	private Context context;
	private String nick;
	private String avatar;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addfriends_final);

		context=this;
		setBaseTitle(R.string.send_request);
		final String id =this.getIntent().getStringExtra("id");
		nick=getIntent().getStringExtra("nick");
		avatar=getIntent().getStringExtra("avatar");
		Log.e("1", nick+")))"+avatar);
		tv_send= (TextView) this.findViewById(R.id.tv_send);
		et_reason= (EditText) this.findViewById(R.id.et_reason);

		tv_send.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				addContact(id);
			}

		});
	}


	@SuppressLint("ShowToast")
	public void addContact(final String glufine_id) {
		if (glufine_id == null || glufine_id.equals("")) {
			return;
		}

		if (glufine_id.equals(Constant.UID+"")) {
			Toast.makeText(getApplicationContext(),
					"不能添加自己", Toast.LENGTH_LONG).show();
			return;
		}

		if (MyApplication.getInstance().getContactIDList().contains(glufine_id)) {
			Toast.makeText(getApplicationContext(),
					"此用户已是你的好友", Toast.LENGTH_LONG).show();
			return;
		}

		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在发送请求...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();


		new Thread(new Runnable() {
			public void run() {

				try {
					// 在reason封装请求者的昵称/头像/时间等信息，在通知中显示

					Map<String,String> params=new HashMap<>();
					params.put("uid", Constant.UID+"");
					params.put("token", Constant.TOKEN);
					params.put("friend_id", glufine_id);
					Map<String,Object> result=HttpUnit.sendFriendRequest(params);
					int status=(int)result.get("status");
					final String info=(String)result.get("info");
					if(status==0){
						String reason= et_reason.getText().toString().trim();
						JSONObject send_json = new JSONObject();
						send_json.put("action", "friend_request");
						send_json.put("type", 1);
						send_json.put("data", reason);
						send_json.put("certificate", info);
						send_json.put("name", LocalUserInfo.getInstance(context)
								.getUserInfo("nick"));
						send_json.put("avatar",LocalUserInfo.getInstance(context)
								.getUserInfo("avatar"));
						send_json.put("user_rank",LocalUserInfo.getInstance(context)
								.getUserInfo("user_rank"));
						String send_body = send_json.toString();

						InviteMessage invite = new InviteMessage();
						invite.setId(Integer.parseInt(glufine_id));
						invite.setReason(reason);
						invite.setCertification(info);
						invite.setTime(System.currentTimeMillis()/1000);
						invite.setFrom(nick);
						invite.setAvatar(avatar);
						Log.e("1", nick+")))"+avatar);
						invite.setStatus(InviteMesageStatus.INVITE);
						InviteMessgeDao dao=new InviteMessgeDao(context);
						dao.saveMessage(invite);
						MainActivity.instance.newMsg("ADD", glufine_id, send_body,1|16);
						runOnUiThread(new Runnable() {
							public void run() {
								progressDialog.dismiss();
								Toast.makeText(getApplicationContext(),
										"发送请求成功,等待对方验证", Toast.LENGTH_SHORT).show();
								Intent intent=new Intent();
								intent.setClass(context, Activity_Friends_New.class);
								startActivity(intent);
								finish();
							}
						});
					}else if(status==5){
						isFriend(glufine_id);
					}else{
						runOnUiThread(new Runnable() {
							public void run() {
								progressDialog.dismiss();
								if(!Constant.isConnectNet){
									Toast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(getApplicationContext(),
											info, Toast.LENGTH_SHORT).show();
								}
								finish();
							}
						});
					}



				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(),
									"请求添加好友失败!", Toast.LENGTH_SHORT ).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 从服务器端拉取好友列表
	 */
	protected void isFriend(final String fid) {
		// TODO Auto-generated method stub
		Map<String, String> map = new HashMap<>();
		map.put("uid", Constant.UID + "");
		map.put("token", Constant.TOKEN);
		LoadDataFromHTTP task = new LoadDataFromHTTP(context,
				Constant.URL_GET_FRIENDLIST, map);
		task.getData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					int code = data.getInteger("status");
					if (code == 0) {
						Log.e("1", "friendlist");
						JSONObject obj = data.getJSONObject("friend_action");
						JSONArray friends = obj.getJSONArray("friends");
						List<String> friend = new ArrayList<>();
						List<String> addFriend = new ArrayList<>();
						for (int i = 0; i < friends.size(); i++) {
							friend.add(friends.get(i) + "");
						}
						if (friend.contains(fid)) {
							addFriend.add(fid);
							AddFriendList(addFriend);
						}else{
							Toast.makeText(getApplicationContext(),
									"你们已经是好友了！", Toast.LENGTH_SHORT ).show();
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


		});
	}


	/**
	 * 从服务器端拉取新的好友的信息，并将这些好友信息存入数据库
	 */
	public void AddFriendList(final Object... params) {
		new AsyncTask<Object, Object, Integer>() {
			@SuppressWarnings("unchecked")
			@Override
			protected Integer doInBackground(Object... params) {
				int status = 0;
				try {
					final List<User> users = HttpUnit
							.searchFriendListRequest((List<String>) params[0]);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							UserDao dao = new UserDao(context);
							dao.saveContactList(users);
							try {
								User user=users.get(0);
								Intent intent = new Intent();
								intent.putExtra("userId", user.getUsername());
								intent.putExtra("userNick", user.getNick());
								intent.setClass(Activity_Friends_Add.this, Activity_Chat.class);
								startActivity(intent);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

							}
						}
					});
					return status;
				} catch (Throwable e) {
					Log.e("1", e.toString());
					return -1;
				}
			}

			protected void onPostExecute(Integer status) {
				if (status == 0) {
					Log.e("1", status+"");
				}
			}

		}.execute(params);
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
