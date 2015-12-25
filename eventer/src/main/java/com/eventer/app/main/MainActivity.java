package com.eventer.app.main;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.ChatroomDao;
import com.eventer.app.db.EventDao;
import com.eventer.app.db.InviteMessgeDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.A;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.entity.ChatRoom;
import com.eventer.app.entity.Event;
import com.eventer.app.entity.InviteMessage;
import com.eventer.app.entity.InviteMessage.InviteMesageStatus;
import com.eventer.app.entity.Msg.Container;
import com.eventer.app.entity.Schedual;
import com.eventer.app.entity.User;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.other.Activity_Chat;
import com.eventer.app.other.Activity_Friends_New;
import com.eventer.app.socket.SocketService;
import com.eventer.app.util.PreferenceUtils;
import com.eventer.app.util.SmileUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends FragmentActivity {

	private int index = 0;
	// 当前fragment的index
	private int currentTabIndex = 0;
	private ImageView[] imagebuttons;
	private TextView[] textviews;
	private Fragment[] fragments;
	private TextView unreadLabel;
	private ActivityFragment activityfragment;
	private MessageFragment msgfragment;
	private ProfileFragment profilefragment;
	private static final String PRV_INDEX = "pre_index";
	private static final String[] FRAGMENT_TAG = { "homefrag", "activityfrag",
			"msgfrag", "profilefrag" };
	private static boolean isExit = false;
	private MsgReceiver msgReceiver;
	private EventReceiver eventReceiver;
	private NotificationManager manager;
	private Map<String,Integer> notifyIdMap=new HashMap<>();
	private int notifyNum=0;
	private  Handler mHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				isExit = false;
				return false;
			}
		});

	private Context context;
	public static Map<String, Schedual> Alarmlist = new HashMap<>();
	public static MainActivity instance = null;
	public ScheduleFragment homefragment;
	public Queue<A> queued = new LinkedList<>();
	public SocketService.SocketSendBinder binder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UmengUpdateAgent.setDeltaUpdate(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		MobclickAgent.setSessionContinueMillis(30 * 1000);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				Log.e("1", updateStatus + "000");
				switch (updateStatus) {
					// 有更新
					case UpdateStatus.Yes:
						break;
				}
			}

		});
		UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {

			@Override
			public void onClick(int arg0) {
				// TODO Auto-generated method stub
				Log.e("1", arg0 + "00");
				switch (arg0) {
					case UpdateStatus.Ignore:
					case UpdateStatus.NotNow:
						PreferenceUtils.getInstance().setVersionAlert(true);
						break;
				}
			}
		});

		setContentView(R.layout.activity_main);
		context = this;
		instance = this;
		Constant.AlarmChange=true;
//		if (Constant.isExist||TextUtils.isEmpty(Constant.UID)) {
//			startActivity(new Intent().setClass(context, LoginActivity.class));
//			finish();
//			MobclickAgent.onProfileSignOff();
//			MobclickAgent.onKillProcess(context);
//			System.exit(0);
//		}
		initData();
		FragmentManager fm = getSupportFragmentManager();
		if (savedInstanceState != null) {
			// 读取上一次界面Save的时候tab选中的状态
			currentTabIndex = savedInstanceState.getInt(PRV_INDEX,
					currentTabIndex);
			activityfragment = (ActivityFragment) fm
					.findFragmentByTag(FRAGMENT_TAG[0]);
			homefragment = (ScheduleFragment) fm
					.findFragmentByTag(FRAGMENT_TAG[1]);
			msgfragment = (MessageFragment) fm
					.findFragmentByTag(FRAGMENT_TAG[2]);
			profilefragment = (ProfileFragment) fm
					.findFragmentByTag(FRAGMENT_TAG[3]);
		}
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("android.intent.alarm.START");
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		sendBroadcast(broadcastIntent);
	}




	private void initData() {
		EventDao dao = new EventDao(context);
		List<String> list = dao.getEventIDList();
		MyApplication.getInstance().setCacheByKey("EventList", list);
		loadFriendList();
		bindService(new Intent(this, SocketService.class),
				internetServiceConnection, Context.BIND_AUTO_CREATE);
		msgReceiver = new MsgReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.eventer.app.socket.RECEIVER");
		intentFilter.setPriority(1000);
		registerReceiver(msgReceiver, intentFilter);
		manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		eventReceiver = new EventReceiver();
		IntentFilter intentFilter1 = new IntentFilter();
		intentFilter1.addAction("com.eventer.app.activity");
		registerReceiver(eventReceiver, intentFilter1);
		initView();
	}

	private void initView() {
		// 初始化四个模块的Fragment
		homefragment = new ScheduleFragment();
		activityfragment = new ActivityFragment();
		msgfragment = new MessageFragment();
		profilefragment = new ProfileFragment();
		fragments = new Fragment[] { activityfragment, homefragment,
				msgfragment, profilefragment };
		// 添加显示第一个fragment
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.fragment_container, activityfragment, FRAGMENT_TAG[0])
				.add(R.id.fragment_container, homefragment, FRAGMENT_TAG[1])
				.add(R.id.fragment_container, msgfragment, FRAGMENT_TAG[2])
				.add(R.id.fragment_container, profilefragment, FRAGMENT_TAG[3])
				.hide(homefragment).hide(profilefragment).hide(msgfragment)
				.show(activityfragment).commit();

		imagebuttons = new ImageView[4];
		imagebuttons[0] = (ImageView) findViewById(R.id.ib_activity);
		imagebuttons[1] = (ImageView) findViewById(R.id.ib_schedual);
		imagebuttons[2] = (ImageView) findViewById(R.id.ib_message);
		imagebuttons[3] = (ImageView) findViewById(R.id.ib_profile);
		imagebuttons[0].setSelected(true);

		textviews = new TextView[4];
		textviews[0] = (TextView) findViewById(R.id.tv_activity);
		textviews[1] = (TextView) findViewById(R.id.tv_schedual);
		textviews[2] = (TextView) findViewById(R.id.tv_message);
		textviews[3] = (TextView) findViewById(R.id.tv_profile);
		textviews[0].setSelected(true);

		// 初始化日程提醒机制
//		if (am == null) {
//			am = (AlarmManager) getSystemService(ALARM_SERVICE);
//		}
//		try {
//			Intent intent = new Intent(getApplicationContext(),
//					AlarmReceiver.class);
//			PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent,
//					PendingIntent.FLAG_UPDATE_CURRENT);
//			Long now_time = System.currentTimeMillis();
//			am.setRepeating(AlarmManager.RTC_WAKEUP, now_time + 3, 60 * 1000,
//					sender);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
		// 刷新未读信息的提醒信息
		updateUnreadLabel();

	}

	/***
	 * Fragment的切换
	 *
	 */
	public void onTabClick(View view) {
		switch (view.getId()) {
			case R.id.re_schedual:
				index = 1;
				break;
			case R.id.re_activity:
				index = 0;
				break;
			case R.id.re_message:
				index = 2;
				break;
			case R.id.re_profile:
				index = 3;
				break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager()
					.beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();

			imagebuttons[currentTabIndex].setSelected(false);
			textviews[currentTabIndex].setSelected(false);
			// 把当前tab设为选中状态
			imagebuttons[index].setSelected(true);
			textviews[index].setSelected(true);
			currentTabIndex = index;
		}

	}

	// 实现消息网关的Service
	public ServiceConnection internetServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName arg0, IBinder service) {
			Log.e("1", "here internetServiceConnection");
			binder = (SocketService.SocketSendBinder) service;
			A a;
			while (!queued.isEmpty()) {
				if ((a = queued.poll()) != null) {
					sendToService(a);
				}
			}
		}

		public void onServiceDisconnected(ComponentName arg0) {
			Log.e("1", "service disconnected");
			binder = null;
		}
	};

	// 通过消息网关发送消息
	public boolean newMsg(String MID, String RID, String body, int type) {
		if (binder == null) {
			Log.e("newMsg", "binder is null");
			queued.add(new A(MID, RID, body, type));
			bindService(new Intent(this, SocketService.class),
					internetServiceConnection, Context.BIND_AUTO_CREATE);
			return false;
		} else {
			sendToService(new A(MID, RID, body, type));
		}
		return true;
	}

	public void sendToService(A a) {
		Container msg = Container.newBuilder().setMID(String.valueOf(a.MID))
				.setSID(String.valueOf(Constant.UID))
				.setRID(String.valueOf(a.RID)).setTYPE(a.type).setSTIME(a.time)
				.setBODY(a.body).build();
		Log.e("1", msg.toString());
		// binder.setCurrentActivity(this);
		binder.sendOne(msg);
	}

	/**
	 * 获取未读消息数
	 *
	 * @return int
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal;
		ChatEntityDao dao = new ChatEntityDao(context);
		unreadMsgCountTotal = dao.getUnreadMsgCount();
		return unreadMsgCountTotal;
	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unreadLabel.setText(String.valueOf(count));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 从服务器端拉取好友列表
	 */
	protected void loadFriendList() {
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
						List<String> list = MyApplication.getInstance()
								.getContactIDList();
						List<String> friend = new ArrayList<>();
						List<String> delFriend = new ArrayList<>();
						List<String> addFriend = new ArrayList<>();
						for (int i = 0; i < friends.size(); i++) {
							friend.add(friends.get(i) + "");
						}
						for (String string : list) {
							if (!friend.contains(string)) {
								delFriend.add(string);
							}
						}
						for (String string : friend) {
							if (!list.contains(string)) {
								addFriend.add(string);
							}
						}
						UserDao dao = new UserDao(context);
						dao.updateUsers(delFriend);
						AddFriendList(addFriend);

					} else if (code == 13) {
						UserDao dao = new UserDao(context);
						dao.updateUsers(MyApplication.getInstance()
								.getContactIDList());
						Toast.makeText(getApplicationContext(), "你现在还没有好友！",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(), "加载好友列表失败！",
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 从服务器端拉取好友列表
	 */
	protected void isFriend(final String fid, final String msg, final long time) {
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
							JSONObject recvJs = JSONObject.parseObject(msg);
							String bodyString = recvJs.getString("data");
							int type = recvJs.getInteger("type");
							String shareId = recvJs.getString("shareId");
							ChatEntity entity = new ChatEntity();
							entity.setType(type);
							entity.setFrom(fid);
							entity.setShareId(shareId);
							entity.setContent(bodyString);
							entity.setMsgTime(time);
							entity.setStatus(1);
							entity.setMsgID(System.currentTimeMillis());
							ChatEntityDao dao = new ChatEntityDao(context);
							dao.saveMessage(entity);
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
							updateUnreadLabel();
							MessageFragment.instance.refreshView();
						}
					});
					return status;
				} catch (Throwable e) {
					Log.e("1", e.toString());
					return -1;
				}
			}

			protected void onPostExecute(Integer status) {

			}

		}.execute(params);
	}

	/**
	 * 接收推送的活动，处理活动
	 *
	 * @author LiuNana
	 *
	 */
	@SuppressLint("NewApi")
	public class EventReceiver extends BroadcastReceiver {

		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context context, Intent intent) {
			// 拿到进度，更新UI
			String msg = intent.getStringExtra("msg");
			JSONObject recvJs;
			try {
				recvJs = JSONObject.parseObject(msg);
				String id = recvJs.getString("_id");
				List<String> list = (List<String>) MyApplication.getInstance()
						.getCacheByKey("EventList");
				if (list == null || list.size() == 0) {
					list = new ArrayList<>();
				}
				if (!list.contains(id + "")) {

					String provider = recvJs.getString("cEvent_provider");
					String content = recvJs.getString("cEvent_content");
					String theme = recvJs.getString("cEvent_theme");
					String place = recvJs.getString("cEvent_place");
					String name = recvJs.getString("cEvent_name");
					String time = recvJs.getString("cEvent_time");// 时间成对，可能有多个时间
					String pubtime = recvJs.getString("cEvent_publish");
					long issuetime = Long.parseLong(pubtime);
					time = time.replace("null,", "");
					Event event = new Event();
					event.setEventID(id);
					event.setContent(content);
					event.setPublisher(provider);
					event.setIssueTime(issuetime);
					event.setTime(time);
					event.setTitle(name);
					event.setTheme(theme);
					event.setPublisher(provider);
					event.setPlace(place);
					EventDao dao = new EventDao(context);
					dao.saveEvent(event);
//					ActivityFragment.instance.addEvent(event);
					list.add(id + "");
					MyApplication.getInstance()
							.setCacheByKey("EventList", list);
					List<Event> eventlist = (List<Event>) MyApplication.getInstance()
							.getCacheByKey("CacheEventList");
					if(eventlist!=null){
						eventlist.add(event);
					}else{
						eventlist=new ArrayList<>();
						eventlist.add(event);
					}
					MyApplication.getInstance()
							.setCacheByKey("CacheEventList", eventlist);
				}

			} catch(Exception e){
				 e.printStackTrace();
			}
		}

	}


	/**
	 * 接收好友或者群组消息
	 *
	 * @author LiuNana
	 *
	 */
	public class MsgReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 拿到进度，更新UI
			String id = intent.getStringExtra("talker");
			UserDao uDao=new UserDao(context);
			final int GROUP_CREATED_NOTIFICATION = 6;
			final int GROUP_INVITE_NOTIFICATION=8;
			String nick=uDao.getNick(id);
			if(nick==null){
				nick="路人";
			}

			long time = intent.getLongExtra("time", -1);
			if (time == -1) {
				time = System.currentTimeMillis() / 1000;
			}
			String mid = intent.getStringExtra("mid");
			String msg = intent.getStringExtra("msg");
			JSONObject recvJs;
			try {
				if (mid.equals("ADD")) {
					// 加好友
					String title = "";
					String ticker = "";
					String msgBody = "";

					recvJs = JSONObject.parseObject(msg);
					String bodyString = recvJs.getString("data");
					String certificate = recvJs.getString("certificate");
					int status = recvJs.getInteger("type");
					Log.e("1", "main:msgRecv+" + bodyString);
					String name = recvJs.getString("name");
					String avatar = recvJs.getString("avatar");
					InviteMessage invite = new InviteMessage();
					invite.setId(Integer.parseInt(id));
					invite.setReason(bodyString);
					invite.setCertification(certificate);
					invite.setFrom(name);

					invite.setAvatar(avatar);
					invite.setTime(System.currentTimeMillis() / 1000);
					User u = new User();
					u.setAvatar(avatar);
					u.setNick(name);
					u.setUsername(id + "");
					if (status == 4) {
						invite.setStatus(InviteMesageStatus.BEINVITEED);
						ticker = "收到一条好友请求";
						title = "好友请求";
						msgBody = name + "发来一条好友请求！";
						u.setType(22);

					} else if (status == 2) {
						invite.setStatus(InviteMesageStatus.BEAGREED);
						ticker = name + "同意了您的好友请求！";
						title = "好友请求";
						msgBody = name + "同意了您的好友请求！";
						u.setType(1);
					}

					else if (status == 3) {
						invite.setStatus(InviteMesageStatus.BEREFUSED);
						ticker = name + "拒绝了您的好友请求！";
						title = "好友请求";
						msgBody = name + "拒绝了您的好友请求！";
						u.setType(22);
					}

					else if (status == 1) {
						invite.setStatus(InviteMesageStatus.BEAPPLYED);
						ticker = "收到一条好友请求";
						title = "好友请求";
						msgBody = name + "发来一条好友请求！";
						u.setType(22);
					}

					UserDao dao1 = new UserDao(context);
					dao1.saveUser(u);
					InviteMessgeDao dao = new InviteMessgeDao(context);
					dao.saveMessage(invite);
					Intent intent1 = new Intent(context,
							Activity_Friends_New.class);
					intent1.putExtra("userId", id);

					notifyMsg(ticker, title, msgBody, intent1,"ADD");
				} else if (mid.contains("@")) {
					// 群组消息
					recvJs = JSONObject.parseObject(msg);
					String bodyString = recvJs.getString("data");
					String shareId = recvJs.getString("shareId");
					int type = recvJs.getInteger("type");
					Log.e("1", "main:msgRecv+" + bodyString);
					if (type == GROUP_CREATED_NOTIFICATION) {
						Intent intent1 = new Intent(context,
								Activity_Chat.class);
						intent1.putExtra("groupId", mid);
						intent1.putExtra("chatType",
								Activity_Chat.CHATTYPE_GROUP);
						notifyMsg("收到来自群组的消息！", "消息通知", nick + "建立了群组！你们可以开始聊天了。", intent1,mid);
						ChatEntity entity = new ChatEntity();
						entity.setType(type);
						entity.setFrom(mid);
						entity.setContent(id + ":\n"+bodyString);
						entity.setMsgTime(time);
						entity.setStatus(1);
						entity.setMsgID(System.currentTimeMillis());
						ChatEntityDao dao = new ChatEntityDao(context);
						dao.saveMessage(entity);
						updateUnreadLabel();
						MessageFragment.instance.refreshView();
						UpdateRoom(bodyString,mid,id);
					}else if(type == GROUP_INVITE_NOTIFICATION){
						ChatEntity entity = new ChatEntity();
						entity.setType(type);
						entity.setFrom(mid);
						entity.setContent(id + ":\n"+bodyString);
						entity.setMsgTime(time);
						entity.setStatus(0);
						entity.setMsgID(System.currentTimeMillis());
						ChatEntityDao dao = new ChatEntityDao(context);
						dao.saveMessage(entity);
						UpdateRoom(bodyString,mid,id);
					} else {
						Intent intent1 = new Intent(context,
								Activity_Chat.class);
						intent1.putExtra("groupId", mid);
						intent1.putExtra("chatType",
								Activity_Chat.CHATTYPE_GROUP);
						handleText(bodyString, type, nick, intent1,2);
						ChatEntity entity = new ChatEntity();
						entity.setType(type);
						entity.setFrom(mid);
						entity.setShareId(shareId);
						entity.setContent(id + ":\n" + bodyString);
						entity.setMsgTime(time);
						entity.setStatus(1);
						entity.setMsgID(System.currentTimeMillis());
						ChatEntityDao dao = new ChatEntityDao(context);
						dao.saveMessage(entity);
						updateUnreadLabel();
						MessageFragment.instance.refreshView();
					}
				} else if (mid.equals("DEL")) {
					// 删除好友消息
					UserDao dao = new UserDao(context);
					List<String> delFriend = new ArrayList<>();
					delFriend.add(id);
					MyApplication.getInstance().clearContact();
					dao.updateUsers(delFriend);
				} else if (mid.equals("1")) {
					// 单聊消息
					if(MyApplication.getInstance().getContactIDList()
							.contains(id)){
						recvJs = JSONObject.parseObject(msg);
						String bodyString = recvJs.getString("data");
						int type = recvJs.getInteger("type");
						String shareId = recvJs.getString("shareId");
						Log.e("1", "main:msgRecv+" + bodyString);
						Intent intent1 = new Intent(context, Activity_Chat.class);
						intent1.putExtra("userId", id);
						handleText(bodyString, type, nick, intent1,1);
						ChatEntity entity = new ChatEntity();
						entity.setType(type);
						entity.setFrom(id);
						entity.setShareId(shareId);
						entity.setContent(bodyString);
						entity.setMsgTime(time);
						entity.setStatus(1);
						entity.setMsgID(System.currentTimeMillis());
						ChatEntityDao dao = new ChatEntityDao(context);
						dao.saveMessage(entity);
						updateUnreadLabel();
						MessageFragment.instance.refreshView();
					}else{
						isFriend(id,msg,time);
					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void UpdateRoom(String bodyString,String name,String owner) {
			// TODO Auto-generated method stub
			ChatRoom room = new ChatRoom();
			room.setRoomId(name);
			room.setTime(System.currentTimeMillis() / 1000);
			try {
				owner=name.split("@")[0];
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				room.setOwner(owner);
				JSONObject groupObject = JSONObject.parseObject(bodyString);
				//从groupObject中获取displayList的JsonArray，并转换成String[]
				JSONArray displayJsonArray = groupObject.getJSONArray("displaylist");
				int size_2 = displayJsonArray.size();
				String[] displays = new String[size_2];
				for(int i =0;i<size_2;i++){
					displays[i] = displayJsonArray.get(i).toString();
				}
				//从groupObject中获取member的JsonArray，并转换成String[]
				JSONArray memberJsonArray = groupObject.getJSONArray("memberlist");
				int size_1 = memberJsonArray.size();
				String[] members = new String[size_1];
				for (int i = 0;i<size_1;i++){
					members[i] = memberJsonArray.get(i).toString();
				}
				room.setMember(members);
				room.setDisplayname(displays);
				ChatroomDao dao = new ChatroomDao(context);
				dao.saveChatROOM(room);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void handleText(String txt, int type,String nick,Intent intent,int msgtype){
		switch (type) {
			case 1:
				Spannable span = SmileUtils
						.getSmiledString(txt);
				txt=span.toString();
				break;
			case 2:
				txt="[活动分享]";
				break;
			case 3:
				txt="[日程分享]";
				break;
			case 4:
			case 5:
				txt="[对日程进行了操作]";
				break;
			default:
				break;
		}
		switch (msgtype) {
			case 1:
				String group=intent.getStringExtra("userId");
				if(!TextUtils.isEmpty(group)){
					notifyMsg("收到来自好友的消息！", "消息通知", nick + ":" + txt,
							intent,group);
				}
				break;
			default:
				String user=intent.getStringExtra("groupId");
				if(!TextUtils.isEmpty(user)){
					notifyMsg("收到来自群组的消息！", "消息通知", nick + ":" + txt,
							intent,user);
				}
				break;
		}

	}

	@SuppressLint("NewApi")
	/**
	 * 消息提醒
	 * @param ticker
	 * @param title
	 * @param content
	 * @param intent
	 * @param notify
	 * @author LiuNana
	 */
	private void notifyMsg(String ticker, String title, String content,
						   Intent intent,String send) {
		boolean alert, alert_detail, alert_shake, alert_voice;
		alert = PreferenceUtils.getInstance().getMsgAlert();
		if (alert) {
			alert_detail = PreferenceUtils.getInstance().getMsgAlertDetail();
			alert_shake = PreferenceUtils.getInstance().getMsgAlertShake();
			alert_voice = PreferenceUtils.getInstance().getMsgAlertVoice();
			if (!alert_detail) {
				content = "";
			}
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					intent, PendingIntent.FLAG_CANCEL_CURRENT);
			Notification.Builder mBuilder = new Notification.Builder(context);
			mBuilder.setSmallIcon(R.drawable.ic_launcher).setTicker(ticker)
					.setContentTitle(title).setContentText(content)
					.setContentIntent(pendingIntent);

			final Notification mNotification = mBuilder.build();
			mNotification.flags = Notification.FLAG_AUTO_CANCEL;// FLAG_ONGOING_EVENT
			if(!notifyIdMap.containsKey(send)){
				notifyNum++;
				notifyIdMap.put(send, notifyNum);
				manager.notify(notifyNum, mNotification);
			}else{
				alert_shake=false;
				alert_voice=false;
			}
			// 在顶部常驻，可以调用下面的清除方法去除
			// FLAG_AUTO_CANCEL
			// 点击和清理可以去调
			if (alert_voice) {
				if (alert_shake) {
					mNotification.defaults = Notification.DEFAULT_SOUND
							| Notification.DEFAULT_VIBRATE
							| Notification.DEFAULT_LIGHTS;
				} else {
					mNotification.defaults = Notification.DEFAULT_SOUND
							| Notification.DEFAULT_LIGHTS;
				}
			} else {
				if (alert_shake) {
					mNotification.defaults = Notification.DEFAULT_VIBRATE
							| Notification.DEFAULT_LIGHTS;
				} else {
					mNotification.defaults = Notification.DEFAULT_LIGHTS;
				}
			}
			mNotification.when = System.currentTimeMillis();

			manager.notify(notifyNum, mNotification);

		}
	}

	public void cancelNotify(String info){
		if(notifyIdMap.containsKey(info)){
			int notify=notifyIdMap.get(info);
			manager.cancel(notify);
			notifyIdMap.remove(info);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == ProfileFragment.IS_EXIT) {
			if (!Constant.isLogin) {
				startActivity(new Intent().setClass(context,
						LoginActivity.class));
				finish();
				Constant.isExist=true;
				System.exit(0);
			}
		}
	}

	/**
	 * 短时间内连续按两次返回键，退出程序 exit()
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 判断所按得键是否是back键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	private void exit() {
		// 在两秒内连续点击两次back键，退出程序
		// TODO Auto-generated method stub
		if (!isExit) {
			isExit = true;
			// Toast要记得加上show()
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			System.exit(0);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		// super.onSaveInstanceState(outState);
		// //将这一行注释掉，阻止activity保存fragment的状态

	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.e("1", "Main___onStart");
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
		// 注销服务和广播接收者
		unbindService(internetServiceConnection);
		unregisterReceiver(msgReceiver);
		unregisterReceiver(eventReceiver);
	}

//	public boolean newMsg(String MID, String RID, String SID, String body,
//						  int type) {
//		Container msg = Container.newBuilder().setMID(String.valueOf(MID))
//				.setSID(String.valueOf(SID)).setRID(String.valueOf(RID))
//				.setTYPE(type).setSTIME(System.currentTimeMillis() / 1000)
//				.setBODY(body).build();
//		Log.e("1", msg.toString());
//		binder.sendOne(msg);
//		return false;
//	}

//	public static String getDeviceInfo(Context context) {
//		try{
//			org.json.JSONObject json = new org.json.JSONObject();
//			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
//					.getSystemService(Context.TELEPHONY_SERVICE);
//
//			String device_id = tm.getDeviceId();
//
//			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//
//			String mac = wifi.getConnectionInfo().getMacAddress();
//			json.put("mac", mac);
//
//			if( TextUtils.isEmpty(device_id) ){
//				device_id = mac;
//			}
//
//			if( TextUtils.isEmpty(device_id) ){
//				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
//			}
//
//		json.put("device_id", device_id);
//
//			return json.toString();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return null;
//	}
}
