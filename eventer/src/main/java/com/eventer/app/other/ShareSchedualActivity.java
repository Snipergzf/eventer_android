package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.entity.Schedual;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.main.MainActivity;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.ui.base.BaseActivityTest;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.widget.CircleImageView;
import com.eventer.app.widget.ExpandGridView;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hirondelle.date4j.DateTime;

@SuppressLint({ "SimpleDateFormat", "InflateParams","SetTextI18n" })
public class ShareSchedualActivity extends BaseActivityTest {
	private Context context;
	private String shareId;
	ChatEntity message;
	private CircleImageView iv_avatar;
	private TextView tv_nick,tv_title,tv_time,tv_place,
			tv_detail,tv_page_title,tv_time_info,tv_attend_num;
	ImageView iv_finish,iv_collect;
	private TextView tv_collect;
	private ExpandGridView gridview;
	Schedual schedual_db=new Schedual();
	private Schedual schedual=new Schedual();
	GridAdapter adapter;
	private String publisher;
	private LoadUserAvatar avatarLoader;
	LinearLayout li_collect;
	private boolean isCollect=false;
	List<UserInfo> members = new ArrayList<>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_event);
		context=this;
		setBaseTitle(R.string.share_activity);
		shareId=getIntent().getStringExtra("shareId");
		avatarLoader = new LoadUserAvatar(context, Constant.IMAGE_PATH);
		if(!TextUtils.isEmpty(shareId)){
			initView();
		}else{
			Toast.makeText(context, "该日程不存在！", Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	private void initView() {
		// TODO Auto-generated method stub
		tv_detail=(TextView)findViewById(R.id.tv_detail);
		tv_nick=(TextView)findViewById(R.id.tv_nick);
		tv_page_title=(TextView)findViewById(R.id.page_title);
		tv_place=(TextView)findViewById(R.id.tv_place);
		tv_time=(TextView)findViewById(R.id.tv_time);
		tv_time_info=(TextView)findViewById(R.id.tv_time_info);
		tv_title=(TextView)findViewById(R.id.tv_title);
		tv_collect=(TextView)findViewById(R.id.tv_collect_action);
		tv_attend_num=(TextView)findViewById(R.id.tv_attend_num);
		iv_finish=(ImageView)findViewById(R.id.iv_finish);
		iv_collect=(ImageView)findViewById(R.id.iv_collect);
		iv_avatar=(CircleImageView)findViewById(R.id.iv_avatar);
		li_collect=(LinearLayout)findViewById(R.id.li_collect);
		gridview=(ExpandGridView)findViewById(R.id.gridview);

		iv_finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		li_collect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String friend_json=schedual.getFriend();
				JSONObject json=JSONObject.parseObject(friend_json);
				JSONArray array=json.getJSONArray("friend");
				JSONArray shareTo=json.getJSONArray("share");

				if(isCollect){
					showAlert();
				}else{
					iv_collect.setSelected(true);
					tv_collect.setText("取消");
					isCollect=true;
					SchedualDao dao=new SchedualDao(context);
					schedual.setSchdeual_ID(System.currentTimeMillis() / 1000);
					int status=getStatus( schedual.getEndtime(), schedual.getStarttime());
					schedual.setStatus(status);
					schedual.setShareId(shareId);
					schedual.setRemindtime(getRemindTime(schedual.getStarttime(),1));
					if(!array.contains(Constant.UID)){
						array.add(Constant.UID);
					}
					json=new JSONObject();
					json.put("friend", array);
					json.put("share", shareTo);
					schedual.setFriend(json.toJSONString());
					dao.saveSchedual(schedual);
					if(Constant.isConnectNet)
						sendMsg(4,array.toJSONString());
					else
						Toast.makeText(getApplicationContext(),getText(R.string.no_network),Toast.LENGTH_SHORT).show();
				}
			}
		});
		initData();


	}

	private void sendMsg(int type,String friend){
		String body;
		JSONObject content_json = new JSONObject();
		String friend_json=schedual.getFriend();
		JSONObject json=JSONObject.parseObject(friend_json);
		JSONArray shareTo=json.getJSONArray("share");
		content_json.put("schedule_place", schedual.getPlace());
		content_json.put("schedule_detail", schedual.getDetail());
		content_json.put("schedule_title", schedual.getTitle());
		content_json.put("schedule_start", schedual.getStarttime());
		content_json.put("schedule_f", schedual.getFrequency());
		content_json.put("schedule_end", schedual.getEndtime());
		content_json.put("schedule_friend",friend);
		content_json.put("schedule_type", schedual.getType());

		JSONObject send_json = new JSONObject();
		send_json.put("action", "send");
		send_json.put("data", content_json);
		send_json.put("shareId", shareId);
		send_json.put("type", type);
		body = send_json.toJSONString();

		ChatEntity msg=new ChatEntity();
		long time=System.currentTimeMillis();
		msg.setType(type);
		msg.setContent(content_json.toJSONString());
		msg.setMsgTime(time/1000);
		msg.setStatus(2);
		msg.setMsgID(time);
		msg.setShareId(shareId);
		ChatEntityDao dao =new ChatEntityDao(context);
		for (Object share : shareTo) {
			String id=(String) share;
			msg.setFrom(id);
			dao.saveMessage(msg);

			if(id.contains("@")){
				MainActivity.instance.newMsg(id, id, body, 49);
			}else{
				MainActivity.instance.newMsg("1", id, body,17);
			}
		}
		initData();
	}

	private void showAlert() {

		final AlertDialog dlg = new AlertDialog.Builder(this).create();
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.info_alertdialog);
		// 设置能弹出输入法
		dlg.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		// 为确认按钮添加事件,执行退出应用操作
		Button ok = (Button) window.findViewById(R.id.btn_ok);
		TextView content = (TextView) window.findViewById(R.id.tv_info);
		TextView title = (TextView) window.findViewById(R.id.tv_title);
		title.setText(getResources().getString(R.string.prompt));
		content.setText(getResources().getString(R.string.cancel_enter));
		ok.setText(getResources().getString(R.string.submit));
		ok.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("ShowToast")
			public void onClick(View v) {
				String friend_json=schedual.getFriend();
				JSONObject json=JSONObject.parseObject(friend_json);
				JSONArray array=json.getJSONArray("friend");
				iv_collect.setSelected(false);
				tv_collect.setText("参与");
				isCollect=false;
				SchedualDao dao=new SchedualDao(context);
				dao.delSchedualByShareId(shareId);
				if(array.contains(Constant.UID)){
					array.remove(Constant.UID);
				}
				if(Constant.isConnectNet){
					sendMsg(5,array.toJSONString());
				}else{
					Toast.makeText(getApplicationContext(),getText(R.string.no_network),Toast.LENGTH_SHORT).show();
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

	private void initData() {
		// TODO Auto-generated method stub
		ChatEntityDao cdao=new ChatEntityDao(context);
		message=cdao.getLatestMsg(shareId);
		publisher=shareId.split("@")[0];
		SchedualDao dao=new SchedualDao(context);
		schedual_db=dao.getSchedualByShare(shareId);
		if(schedual_db!=null){
			iv_collect.setSelected(true);
			tv_collect.setText("取消");
			isCollect=true;
		}
		String detail="",end="",start="",place="",title="",friend="";
		members=new ArrayList<>();
		int _f=0,type=0;
		if(message!=null){
			String content=message.getContent();
			int loc=content.indexOf("\n");
			if(loc!=-1){
				content=content.substring(loc+1);
			}
			try{
				JSONObject json=JSONObject.parseObject(content);
				detail=json.getString("schedule_detail");
				end=json.getString("schedule_end");
				start=json.getString("schedule_start");
				_f=json.getInteger("schedule_f");
				place=json.getString("schedule_place");
				title=json.getString("schedule_title");
				type=json.getInteger("schedule_type");
				friend=json.getString("schedule_friend");
				schedual.setDetail(detail);
				schedual.setEndtime(end);
				schedual.setFrequency(_f);
				schedual.setPlace(place);
				schedual.setShareId(shareId);
				schedual.setTitle(title);
				schedual.setType(type);
				schedual.setStarttime(start);
				JSONArray array=JSONArray.parseArray(friend);
				JSONObject share_json = new JSONObject();
				friend=array.toJSONString();
				share_json.put("friend",array);
				array=new JSONArray();
				array.add(message.getFrom());
				share_json.put("share",array);
				schedual.setFriend(share_json.toJSONString());
			}catch(Exception e){
                e.printStackTrace();
			}
		}else if(schedual_db!=null){
			detail=schedual_db.getDetail();
			title=schedual_db.getTitle();
			end=schedual_db.getEndtime();
			start=schedual_db.getStarttime();
			_f=schedual_db.getFrequency();
			place=schedual_db.getPlace();
			type=schedual_db.getType();
			String friend_json=schedual_db.getFriend();
			JSONObject json=JSONObject.parseObject(friend_json);
			friend=json.getString("friend");
			schedual=schedual_db;
		} else{
			Toast.makeText(context, "该日程不存在！", Toast.LENGTH_SHORT).show();
			finish();
		}

		switch (type) {
			case 2:
				tv_page_title.setText("日程详情");
				tv_time_info.setText("时间");
				if(_f>0){
					String[] repeat=getResources().getStringArray(R.array.eventrepeat);
					if(_f<repeat.length)
						start+="("+repeat[_f]+")";
				}
				tv_time.setText(start);
				break;
			case 3:
				tv_page_title.setText("代办事项详情");
				tv_time_info.setText("截止");
				tv_time.setText(end);
				break;
			default:
				break;
		}
		if(!TextUtils.isEmpty(title)){
			tv_title.setText(title);
		}else{
			tv_title.setText("未填写");
			tv_title.setTextColor(ContextCompat.getColor(context, R.color.caldroid_lighter_gray));
		}
		if(!TextUtils.isEmpty(place)){
			tv_place.setText(place);
		}else{
			tv_place.setText("未填写");
			tv_place.setTextColor(ContextCompat.getColor(context, R.color.caldroid_lighter_gray));
		}
		if(!TextUtils.isEmpty(detail)){
			tv_detail.setText(detail);
		}else{
			tv_detail.setText("未填写");
			tv_detail.setTextColor(ContextCompat.getColor(context, R.color.caldroid_lighter_gray));
		}

		JSONArray array=JSONArray.parseArray(friend);
		tv_attend_num.setText("("+array.size()+")");
		adapter=new GridAdapter(context, members);
		gridview.setAdapter(adapter);
		for (int i = 0; i < array.size(); i++) {
			String name=array.getString(i);
//				if(name.equals(Constant.UID)){
//					UserInfo user=new UserInfo();
//					user.setAvatar(LocalUserInfo.getInstance(context).getUserInfo("avatar"));
//					user.setNick(LocalUserInfo.getInstance(context).getUserInfo("nick"));
//					user.setUsername(name);
//					members.add(user);
//					gridview.notifyAll();
//				}else{
//					Map<String,UserInfo> map=MyApplication.getInstance().getUserList();
//				    UserInfo user=map.get(name);
//				    if(user!=null){
//				    	members.add(user);
//				    	gridview.notify();
//				    }			      
//				}
			if(name.equals(Constant.UID)){
				UserInfo user=new UserInfo();
				user.setAvatar(LocalUserInfo.getInstance(context).getUserInfo("avatar"));
				user.setNick(LocalUserInfo.getInstance(context).getUserInfo("nick"));
				user.setUsername(name);
				members.add(user);
//				synchronized (gridview){
					gridview.notifyAll();
//				}
			}else if(MyApplication.getInstance().getContactList().containsKey(name)){
				UserInfo user=new UserInfo();
				User u=MyApplication.getInstance().getContactList().get(name);
				user.setAvatar(u.getAvatar());
				user.setUsername(name);
				String Beizhu=u.getBeizhu();
				if(TextUtils.isEmpty(Beizhu)){
					user.setNick(u.getNick());
				}else{
					user.setNick(Beizhu);
				}
				members.add(user);
//				synchronized (gridview){
					gridview.notifyAll();
//				}

			}else if(MyApplication.getInstance().getUserList().containsKey(name)){
				UserInfo u=MyApplication.getInstance().getUserList().get(name);
				members.add(u);
//				synchronized (gridview){
					gridview.notifyAll();
//				}
			}else{
				Map<String,String> map=new HashMap<>();
				map.put("uid", name);
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
									showUserAvatar(iv_avatar, avatar);
									UserInfo user=new UserInfo();
									user.setAvatar(avatar);
									user.setNick(name);
									user.setType(22);
									user.setUsername(name);
									MyApplication.getInstance().addUser(user);
									members.add(user);

									gridview.notifyAll();

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




		if(publisher.equals(Constant.UID)){
			String avatar=LocalUserInfo.getInstance(context).getUserInfo("avatar");
			String nick=LocalUserInfo.getInstance(context).getUserInfo("nick");
			tv_nick.setText(nick);
			showUserAvatar(iv_avatar, avatar);
		}else if(MyApplication.getInstance().getContactList().containsKey(publisher)){
			User u=MyApplication.getInstance().getContactList().get(publisher);
			String avatar=u.getAvatar();
			showUserAvatar(iv_avatar, avatar);
		}else if(MyApplication.getInstance().getUserList().containsKey(publisher)){
			UserInfo u=MyApplication.getInstance().getUserList().get(publisher);
			String avatar=u.getAvatar();
			showUserAvatar(iv_avatar, avatar);
		}else{
			Map<String,String> map=new HashMap<>();
			map.put("uid", publisher);
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
								showUserAvatar(iv_avatar, avatar);
								UserInfo user=new UserInfo();
								user.setAvatar(avatar);
								user.setNick(name);
								user.setType(22);
								user.setUsername(publisher);
								MyApplication.getInstance().addUser(user);
								break;
							default:
								Toast.makeText(context, "获取发布者信息失败！", Toast.LENGTH_SHORT).show();
//						Log.e("1", "获取用户信息失败：");
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
	private String getRemindTime(String start, int span) {
		// TODO Auto-generated method stub
		String rTime;
		start=start.substring(0,16);
		DateTime begin=new DateTime(start+":00");
		DateTime r;
		switch(span){
			case 1:
				r=begin.plus(0, 0, 0, 0, 0, 0, 0, null);
				break;
			case 2:
				r=begin.minus(0, 0, 0, 0, 10, 0, 0, null);
				break;
			case 3:
				r=begin.minus(0, 0, 0, 0, 30, 0, 0, null);
				break;
			case 4:
				r=begin.minus(0, 0, 0, 1, 0, 0, 0, null);
				break;
			case 5:
				r=begin.minus(0, 0, 1, 0, 0, 0, 0, null);
				break;
			default:
				r=begin.minus(0, 0, 0, 0, 0, 0, 0, null);
				break;
		}
		rTime=r.toString().substring(0, 16);
		return rTime;
	}
	public String getTime(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date(now));
	}



	private int getStatus( String end, String remindtime) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowtime = sdf.format(new Date());
		end=end.substring(0,16);
		remindtime=remindtime.substring(0,16);
		int status=0;
		DateTime now=new DateTime(nowtime+":00");
		DateTime finish=new DateTime(end+":00");
		DateTime remind=new DateTime(remindtime+":00");
		if(now.gteq(remind)&&now.lteq(finish)){
			status=1;
		}else if(now.lt(remind)){
			status=1;
		}else if(now.gt(finish)){
			status=0;
		}
		return status;
	}

	private void showUserAvatar(ImageView iamgeView, String avatar) {
		if(avatar==null||avatar.equals("")||avatar.equals("default")) return;
		final String url_avatar =avatar;
		iamgeView.setTag(url_avatar);

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
		@Override
		public View getView(final int position, View convertView,
							final ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.schedual_participant_gridview_item, null);
			}
			ImageView iv_avatar = (ImageView) convertView
					.findViewById(R.id.iv_avatar);
			TextView tv_username = (TextView) convertView
					.findViewById(R.id.tv_username);

			UserInfo user = objects.get(position);
			String usernick = user.getNick();
			final String userid = user.getUsername();
			final String useravatar = user.getAvatar();
			tv_username.setText(usernick);
			iv_avatar.setImageResource(R.drawable.default_avatar);
			iv_avatar.setTag(useravatar);
			if (useravatar != null && !useravatar.equals("")&&!useravatar.equals("default")) {
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

			iv_avatar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
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

			});


			return convertView;
		}

		@Override
		public int getCount() {
			return objects.size();
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
