package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

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
import com.eventer.app.http.HttpParamUnit;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.main.MainActivity;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.widget.CircleImageView;
import com.eventer.app.widget.ExpandGridView;
import com.eventer.app.widget.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hirondelle.date4j.DateTime;

@SuppressLint({ "SimpleDateFormat", "InflateParams","SetTextI18n" })
public class ShareSchedualActivity extends SwipeBackActivity implements OnClickListener {
	private Context context;
	private String shareId;
	ChatEntity message;
	private CircleImageView iv_avatar;
	private ImageView iv_delete;
	private TextView tv_nick,tv_title,tv_time,tv_place,
			tv_detail,tv_time_info,tv_attend_num;
	ImageView iv_finish,iv_collect,iv_share;
	private TextView tv_collect;
	private ExpandGridView gridview;
	Schedual schedual_db=new Schedual();
	private Schedual schedual=new Schedual();
	GridAdapter adapter;
	private String publisher;
	private String shareTo;
	private LoadUserAvatar avatarLoader;
	private AlertDialog dlg;
	private boolean isCollect=false;
	private Dialog mDialog;
	List<UserInfo> members = new ArrayList<>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_event);
		context=this;
		setBaseTitle(R.string.share_activity);
		shareId=getIntent().getStringExtra("shareId");
		shareTo = getIntent().getStringExtra("groupId");
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
		tv_place=(TextView)findViewById(R.id.tv_place);
		tv_time=(TextView)findViewById(R.id.tv_time);
		tv_time_info=(TextView)findViewById(R.id.tv_time_info);
		tv_title=(TextView)findViewById(R.id.tv_title);
		tv_collect=(TextView)findViewById(R.id.tv_collect_action);
		tv_attend_num=(TextView)findViewById(R.id.tv_attend_num);
		iv_finish=(ImageView)findViewById(R.id.iv_finish);
		iv_collect=(ImageView)findViewById(R.id.iv_collect);
		iv_share = (ImageView) findViewById(R.id.iv_share);
		iv_avatar=(CircleImageView)findViewById(R.id.iv_avatar);
		iv_delete = (ImageView)findViewById(R.id.iv_delete);
		gridview=(ExpandGridView)findViewById(R.id.gridview);

		iv_finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		iv_collect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				String friend_json=schedual.getFriend();
//				JSONObject json=JSONObject.parseObject(friend_json);
//				JSONArray array=json.getJSONArray("friend");
//				String shareTo=json.getString("share");
//				JSONArray array1 = JSON.parseArray(friend_json);
				if(isCollect){
					showAlert();
				}else{
					joinGroupSchedual();
				}
			}
		});

		iv_share.setOnClickListener(this);
		iv_delete.setOnClickListener(this);
		initData();
	}

	public void IsTodayEvent(int _f,String remind,String end){
		if(Constant.AlarmChange){
			return;
		}
		DateTime Remind_dt = new DateTime(remind + ":00");
		String[] remind_time = remind.split(" ");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		String today_str = formatter.format(new Date());
		String[] today_time = today_str.split(" ");
		DateTime End_dt = new DateTime(end + ":00");
		DateTime today_dt = new DateTime(today_str + ":00");
		DateTime time_db = new DateTime(remind_time[0] + " 00:00:00");
		DateTime today_db = new DateTime(today_time[0] + " 00:00:00");
		int diff = today_db.numDaysFrom(time_db);
		int i,j;
		boolean isTodayEvent=false;
		switch (_f) {
			case 0:
				isTodayEvent = true;
				break;
			case 1:
				if (today_time[0].compareTo(remind_time[0]) >= 0)
					isTodayEvent= true;
				break;
			case 2:
				// int i=Remind_dt.getWeekDay();
				if (Remind_dt.getWeekDay() > 1 && Remind_dt.getWeekDay() < 7
						&& today_time[0].compareTo(remind_time[0]) >= 0)
					Constant.AlarmChange = true;
				break;
			case 3:
				i=today_dt.getWeekDay();
				j=Remind_dt.getWeekDay();
				if (i == j
						&& today_time[0].compareTo(remind_time[0]) >= 0)
					isTodayEvent= true;
				break;
			case 4:
				i=today_dt.getDay();
				j=Remind_dt.getDay();
				if (i == j
						&& today_time[0].compareTo(remind_time[0]) >= 0)
					isTodayEvent = true;
				break;
			case 5:
				int month1 = today_dt.getMonth();
				int day1 = today_dt.getDay();
				int month2 = Remind_dt.getMonth();
				int day2 = Remind_dt.getDay();
				if (month1 == month2 && day1 == day2
						&& today_time[0].compareTo(remind_time[0]) >= 0)
					isTodayEvent = true;
				break;
		}
		if(isTodayEvent){
			if (diff > 0) {
				End_dt = End_dt.plusDays(diff);
			}
			if (!today_dt.gt(End_dt)) {
				Constant.AlarmChange = true;
			}
		}

	}

	private void sendMsg(int type){
		if(!TextUtils.isEmpty(shareTo)){
			String body;
			JSONObject content_json = new JSONObject();
			content_json.put("member", Constant.UID);
			content_json.put("event_id", shareId);
			content_json.put("event_name", schedual.getTitle());
			content_json.put("nick", LocalUserInfo.getInstance(context).getUserInfo("nick"));
			JSONObject send_json = new JSONObject();
			send_json.put("action", "send");
			send_json.put("data", content_json);
			send_json.put("type", type);
			String content=content_json.toJSONString();
			body = send_json.toJSONString();
			ChatEntity msg=new ChatEntity();
			long time=System.currentTimeMillis();
			msg.setType(type);
			msg.setFrom(shareTo);
			msg.setContent(content);
			msg.setMsgTime(time / 1000);
			msg.setStatus(2);
			msg.setMsgID(time);
			msg.setShareId(shareId);
			ChatEntityDao dao1 =new ChatEntityDao(context);
			dao1.saveMessage(msg);
			if(shareTo.contains("@")){
				MainActivity.instance.newMsg(shareTo, shareTo, body, 49);
			}else{
				MainActivity.instance.newMsg("1", shareTo, body,17);
			}
		}
		initData();
	}

	private void showAlert() {

		dlg = new AlertDialog.Builder(this).create();
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
				exitGroupSchedual();
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

	private void showDeleteAlert() {

		dlg = new AlertDialog.Builder(this).create();
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
		content.setText(getResources().getString(R.string.delete_share));
		ok.setText(getResources().getString(R.string.submit));
		ok.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("ShowToast")
			public void onClick(View v) {
				deleteGroupSchedual();
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
        if(!TextUtils.isEmpty(publisher) && publisher.equals(Constant.UID)){
			iv_delete.setVisibility(View.VISIBLE);
		}

		members=new ArrayList<>();
		if(schedual_db!=null){
			schedual=schedual_db;
			initSchedule();
			checkGroupSchedual();
		} else{
			checkGroupSchedual();

		}


	}

	private void initSchedule(){
		String detail,end,start,place,title,friend;
		int _f,type;
		detail=schedual.getDetail();
		title=schedual.getTitle();
		end=schedual.getEndtime();
		start=schedual.getStarttime();
		_f=schedual.getFrequency();
		place=schedual.getPlace();
		type=schedual.getType();
		friend=schedual.getFriend();


		switch (type) {
			case 2:
				setBaseTitle("日程详情");
				tv_time_info.setText("时间");
				if(_f>0){
					String[] repeat=getResources().getStringArray(R.array.eventrepeat);
					if(_f<repeat.length)
						start+="("+repeat[_f]+")";
				}
				tv_time.setText(start);
				break;
			case 3:
				setBaseTitle("代办事项详情");
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

//		JSONArray array=JSONArray.parseArray(friend);
		String[] array = new String[]{};
		if(!TextUtils.isEmpty(friend)){
			array = friend.split(";");
		}
		List<String> list = new ArrayList<>();
		for(String user:array){
			if(!list.contains(user))
				list.add(user);
		}

		tv_attend_num.setText("("+array.length+")");
		members.clear();
		adapter=new GridAdapter(context, members);

		gridview.setAdapter(adapter);
		for (String name: list) {
			if(name.equals(Constant.UID)){
				UserInfo user=new UserInfo();
				user.setAvatar(LocalUserInfo.getInstance(context).getUserInfo("avatar"));
				user.setNick(LocalUserInfo.getInstance(context).getUserInfo("nick"));
				user.setUsername(name);
				members.add(user);
				synchronized (gridview){
					gridview.notifyAll();
				}
				iv_collect.setSelected(true);
				tv_collect.setText("取消");
				isCollect=true;
			}else if(MyApplication.getInstance().getContactList().containsKey(name)){
				UserInfo user=new UserInfo();
				User u=MyApplication.getInstance().getContactList().get(name);
				user.setAvatar(u.getAvatar());
				user.setUsername(name);
				String Beizhu =u.getBeizhu();
				if(TextUtils.isEmpty(Beizhu)){
					user.setNick(u.getNick());
				}else{
					user.setNick(Beizhu);
				}
				members.add(user);

				synchronized (gridview){
					gridview.notifyAll();
				}

			}else if(MyApplication.getInstance().getUserList().containsKey(name)){
				UserInfo u=MyApplication.getInstance().getUserList().get(name);
				members.add(u);
				synchronized (gridview){
					gridview.notifyAll();
				}
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



	private void joinGroupSchedual(){
		Map<String,String> map= HttpParamUnit.activityParam(shareId);
		LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_ACTIVITY_JOIN, map);
		task.getData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					int status = data.getInteger("status");
					if (status == 0) {
						Log.e("create activity", status + "");

						iv_collect.setSelected(true);
						tv_collect.setText("取消");
						isCollect=true;
						JSONObject action = data.getJSONObject("web_action");
						String participants = action.getString("a_participants");
						schedual.setFriend(participants);

						SchedualDao dao=new SchedualDao(context);
						schedual.setSchdeual_ID(System.currentTimeMillis() / 1000);
						status=getStatus( schedual.getEndtime(), schedual.getStarttime());
						schedual.setStatus(status);

						schedual.setRemindtime(getRemindTime(schedual.getStarttime(), 1));
						dao.saveSchedual(schedual);
						sendMsg(21);
						initSchedule();
					}else if(status == 35){
						Toast.makeText(context, "该群日程已删除~", Toast.LENGTH_SHORT).show();
						SchedualDao dao=new SchedualDao(context);
						dao.deleteSchedual(schedual.getSchdeual_ID() + "");
						finish();
					}else{
						Toast.makeText(context, "发生异常~", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(context, "加入失败~", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void checkGroupSchedual() {
		Map<String, String> map= HttpParamUnit.activityParam(shareId);
		LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_ACTIVITY_CHECK, map);
		task.getData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					int status = data.getInteger("status");
					if (status == 0) {
						JSONObject action = data.getJSONObject("web_action");
						JSONObject activity = action.getJSONObject("activity");
						if(schedual == null){
							schedual.setSchdeual_ID(System.currentTimeMillis()/1000);
						}
						schedual.setTitle(activity.getString("a_name"));
						schedual.setPlace(activity.getString("a_place"));
						schedual.setShareId(activity.getString("a_id"));
						schedual.setDetail(activity.getString("a_desc"));
						schedual.setFrequency(Integer.parseInt(activity.getString("a_frequency")));
						schedual.setType(Integer.parseInt(activity.getString("a_type")));
						schedual.setSharer(activity.getString("uid"));

						String time = activity.getString("a_time");
						schedual.setStarttime(time);
						schedual.setEndtime(time);
						schedual.setRemind(1);
						schedual.setRemindtime(getRemindTime(time, 1));


                        String participants = activity.getString("participants");

						String[] members = participants.split(";");
						boolean hasEnter = false;
						for (String user : members) {
							if (!TextUtils.isEmpty(user) && user.equals(Constant.UID)){
								hasEnter = true;
							}
						}
						schedual.setFriend(participants);
						SchedualDao dao = new SchedualDao(context);
						if(hasEnter){
							dao.saveSchedual(schedual,1);
						}else{
							dao.saveSchedual(schedual,0);
						}

						initSchedule();
					}else if(status == 35){
						Toast.makeText(context, "该群日程已删除~", Toast.LENGTH_SHORT).show();
						SchedualDao dao=new SchedualDao(context);
						dao.deleteSchedual(schedual.getSchdeual_ID() + "");
						finish();
					}else{
						Toast.makeText(context, "刷新异常~", Toast.LENGTH_SHORT).show();
//						finish();
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.e("error", e.toString());
					Toast.makeText(context, "刷新失败~", Toast.LENGTH_SHORT).show();
//					finish();
				}
			}
		});
	}

	private void exitGroupSchedual(){
		Map<String,String> map= HttpParamUnit.activityParam(shareId);
		LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_ACTIVITY_EXIT, map);
		task.getData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
//				try {
					int status = data.getInteger("status");
					if (status == 0) {
						Log.e("create activity", status + "");
						iv_collect.setSelected(false);
						tv_collect.setText("参与");
						isCollect=false;
						JSONObject action = data.getJSONObject("web_action");
						String participants = action.getString("a_participants");
						schedual.setFriend(participants);
						SchedualDao dao=new SchedualDao(context);
						dao.saveSchedual(schedual);
						dao.delSchedualByShareId(shareId);
						Constant.AlarmChange=true;
                        sendMsg(22);
						initSchedule();
						dlg.cancel();
					}else if(status == 35){
						dlg.cancel();
						Toast.makeText(context, "该群日程已删除~", Toast.LENGTH_SHORT).show();
						SchedualDao dao=new SchedualDao(context);
						dao.deleteSchedual(schedual.getSchdeual_ID() + "");
						finish();
					}else{
						dlg.cancel();
						Toast.makeText(context, "发生异常~", Toast.LENGTH_SHORT).show();

					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					dlg.cancel();
//					Toast.makeText(context, "退出失败~", Toast.LENGTH_SHORT).show();
//
//				}
			}
		});
	}

	private void deleteGroupSchedual() {
		Map<String,String> map= HttpParamUnit.activityParam(shareId);
		LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_ACTIVITY_DELETE, map);
		task.getData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
//				try {
					int status = data.getInteger("status");
					if (status == 0) {

						iv_collect.setSelected(false);
						tv_collect.setText("参与");
						isCollect=false;
						schedual.setFriend("");
						SchedualDao dao=new SchedualDao(context);
						dao.deleteSchedual(schedual.getSchdeual_ID()+"");
						Constant.AlarmChange=true;
						sendMsg(23);
						initSchedule();
						dlg.cancel();
						finish();
					}else if(status == 35){
						dlg.cancel();
						Toast.makeText(context, "该群日程已删除~", Toast.LENGTH_SHORT).show();
						SchedualDao dao=new SchedualDao(context);
						dao.deleteSchedual(schedual.getSchdeual_ID() + "");
						finish();
					}else{
						dlg.cancel();
						Toast.makeText(context, "发生异常~", Toast.LENGTH_SHORT).show();

					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					dlg.cancel();
//					e.printStackTrace();
//					Toast.makeText(context, "删除失败~", Toast.LENGTH_SHORT).show();
//
//				}
			}
		});
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_finish:
			case R.id.iv_back:
				this.finish();
				break;
			case R.id.iv_share:
				if (mDialog == null) {
					mDialog = new Dialog(context, R.style.login_dialog);
					mDialog.setCanceledOnTouchOutside(true);
					Window win = mDialog.getWindow();
					WindowManager.LayoutParams params = new WindowManager.LayoutParams();
					params.width = WindowManager.LayoutParams.MATCH_PARENT;
					params.height = WindowManager.LayoutParams.WRAP_CONTENT;
					params.x = 0;
					params.y = 0;
					win.setAttributes(params);
					mDialog.setContentView(R.layout.dialog_share);
					mDialog.findViewById(R.id.share_by_chatroom).setOnClickListener(this);
					mDialog.findViewById(R.id.share_by_user).setOnClickListener(this);
					mDialog.findViewById(R.id.share_cancel).setOnClickListener(this);
					mDialog.findViewById(R.id.share_layout).setOnClickListener(this);
				}
				mDialog.show();
				break;
			case R.id.share_by_chatroom:
				Log.e("1", schedual.getSchdeual_ID()+"");
				startActivity(new Intent().setClass(context, ShareToGroupActivity.class)
						.putExtra("schedual_id", schedual.getSchdeual_ID()+"")
						.putExtra("sharetype", ShareToSingleActivity.SHARE_SCHEDUAL));
				mDialog.dismiss();
				break;
			case R.id.share_by_user:
				Log.e("1", schedual.getSchdeual_ID()+"");
				startActivity(new Intent().setClass(context, ShareToSingleActivity.class)
						.putExtra("schedual_id", schedual.getSchdeual_ID()+"")
						.putExtra("sharetype", ShareToSingleActivity.SHARE_SCHEDUAL));
				mDialog.dismiss();
				break;
			case R.id.share_cancel:
			case R.id.share_layout:
				mDialog.dismiss();
				break;
			case R.id.iv_delete:
				showDeleteAlert();
				break;
			default:
				break;
		}
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
