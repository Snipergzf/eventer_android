package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.adapter.ExpressionAdapter;
import com.eventer.app.adapter.ExpressionPagerAdapter;
import com.eventer.app.adapter.MessageAdapter;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.ChatroomDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.entity.ChatRoom;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.main.MainActivity;
import com.eventer.app.main.MessageFragment;
import com.eventer.app.ui.base.BaseActivityTest;
import com.eventer.app.util.SmileUtils;
import com.eventer.app.widget.ExpandGridView;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import hirondelle.date4j.DateTime;

/**
 * 聊天页面
 *
 */
@SuppressLint({ "HandlerLeak", "SimpleDateFormat" })
@SuppressWarnings("deprecation")
public class Activity_Chat extends BaseActivityTest implements OnClickListener {
	public static final int REQUEST_CODE_GROUP_DETAIL = 21;
	public static final int RESULT_CODE_EXIT_GROUP = 7;
	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	private ListView listView;
	private EditText mEditTextContent;
	private View buttonSetModeKeyboard;
	private View buttonPressToSpeak;
	private LinearLayout emojiIconContainer;
	private LinearLayout btnContainer;
	ImageView iv_clear;
	private RelativeLayout re_notify;
	private View more;
	ViewPager expressionViewpager;
	private InputMethodManager manager;
	private List<String> reslist;
	private int chatType;
	public static Activity_Chat instance = null;
	// 给谁发送消息
	private MessageAdapter adapter;
	private List<ChatEntity> mData = new ArrayList<>();
	Queue<String> toastqueue = new LinkedList<>();
	private Handler mHandler;
	private ImageView iv_emoticons_normal;
	private ImageView iv_emoticons_checked;
	private RelativeLayout edittext_layout;
	private ProgressBar loadmorePB;
	private boolean isloading;
	final int pagesize = 20;
	private boolean haveMoreData = true;
	private Context context;
	public String talker;
	private User user = new User();
	// 分享的照片
	String iamge_path = null;
	// 设置按钮
	ImageView iv_setting_group;
	private MsgReceiver msgReceiver;
//	@SuppressLint("HandlerLeak")
//	Handler micImageHandler = new Handler() {
//		@Override
//		public void handleMessage(android.os.Message msg) {
//			// 切换msg切换图片
//			micImage.setImageDrawable(micImages[msg.what]);
//		}
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		context = Activity_Chat.this;
		instance = this;
		setBaseTitle(R.string.chat);
		initView();
		setUpView();
		iamge_path = this.getIntent().getStringExtra("iamge_path");
//		if (iamge_path != null && !iamge_path.equals("")) {
//			// sendPicture(iamge_path, true);
//		}
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case 33:
						Bundle b = (Bundle) msg.obj;
						String body = b.getString("body");
						String msg1 = b.getString("msg");
						if (body != null && !body.equals("")) {

							JSONObject recvJs = JSONObject.parseObject(msg1);
							String shareId = "";
							if (recvJs.containsKey("shareId"))
								shareId = recvJs.getString("shareId");
							int type = recvJs.getInteger("type");

							ChatEntity entity1 = new ChatEntity();
							entity1.setType(type);
							entity1.setFrom(talker);
							entity1.setShareId(shareId);
							entity1.setContent(body);
							if (b.getLong("time") != -1) {
								entity1.setMsgTime(b.getLong("time"));
							} else {
								entity1.setMsgTime(System.currentTimeMillis() / 1000);
							}
							entity1.setStatus(0);
							entity1.setMsgID(System.currentTimeMillis());
							ChatEntityDao dao = new ChatEntityDao(context);
							dao.saveMessage(entity1);
							mData.add(0, entity1);
							adapter.notifyDataSetChanged();
							listView.setSelection(listView.getCount() - 1);
						}
						break;

					default:
						break;
				}
			}

		};

		msgReceiver = new MsgReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.eventer.app.socket.RECEIVER");
		intentFilter.setPriority(3000);
		registerReceiver(msgReceiver, intentFilter);

		ChatEntityDao dao = new ChatEntityDao(context);
		dao.ClearUnReadMsg(talker);
		MainActivity.instance.cancelNotify(talker);
		MessageFragment.instance.refreshData();
	}

	/**
	 * initView
	 */
	protected void initView() {

		listView = (ListView) findViewById(R.id.list);
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		findViewById(R.id.btn_send);
		buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
		iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
		iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
		loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
		iv_clear = (ImageView) findViewById(R.id.iv_clear);
		re_notify = (RelativeLayout) findViewById(R.id.re_notify);
		iv_clear.setOnClickListener(this);
		re_notify.setOnClickListener(this);

		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		iv_emoticons_checked.setOnClickListener(this);
		iv_emoticons_normal.setOnClickListener(this);
		more = findViewById(R.id.more);
		edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);

		// 表情list
		reslist = getExpressionRes(54);
		// 初始化表情viewpager
		List<View> views = new ArrayList<>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		View gv3 = getGridChildView(3);
		views.add(gv1);
		views.add(gv2);
		views.add(gv3);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		edittext_layout.requestFocus();
		// buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edittext_layout
							.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					edittext_layout
							.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}

			}
		});
		mEditTextContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edittext_layout
						.setBackgroundResource(R.drawable.input_bar_bg_active);
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
			}
		});
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
		// 监听文字框
		// 判断单聊还是群聊
		chatType = getIntent().getIntExtra("chatType", CHATTYPE_SINGLE);
		// type=getIntent().getIntExtra("type", 0);
		if (chatType == CHATTYPE_SINGLE) { // 单聊
			talker = getIntent().getStringExtra("userId");

		} else {
			talker = getIntent().getStringExtra("groupId");

		}
		listView.setOnScrollListener(new ListScrollListener());
		listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		adapter = new MessageAdapter(this, mData, chatType);
		listView.setAdapter(adapter);
		listView.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
				return false;
			}
		});
		initNotify();

	}

	// 初始化通知
	// 有未过期群日程，显示通知
	private void initNotify() {
		// TODO Auto-generated method stub
		if (chatType == CHATTYPE_GROUP) {
			ChatEntityDao dao = new ChatEntityDao(context);
			List<String> list = dao.getShareList(talker);
			for (String string : list) {
				int loc = string.indexOf("\n");
				if (loc != -1) {
					string = string.substring(loc + 1);
				}
				try {
					JSONObject json = JSONObject.parseObject(string);
					String end = json.getString("schedual_end");
					if (getStatus(end)) {
						re_notify.setVisibility(View.VISIBLE);
					}

				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}
	}

	public String getTime(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date(now));
	}

	// 获取日程状态
	private boolean getStatus(String end) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowtime = sdf.format(new Date());
		int status;
		DateTime now = new DateTime(nowtime + ":00");
		DateTime finish = new DateTime(end + ":00");

		if (now.lteq(finish)) {
			status = 1;
		} else {
			status = 0;
		}
		return status > 0;
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		mData.clear();
		ChatEntityDao dao1 = new ChatEntityDao(context);
		List<ChatEntity> data = dao1.getMsgList(talker, 0, 15);
		if (data.size() > 0){
			for (ChatEntity chatEntity : data) {
				mData.add(chatEntity);
			}
		}
		adapter.notifyDataSetChanged();
		int count = listView.getCount();
		if (count > 0) {
			listView.setSelection(count - 1);
		}

	}

	private void setUpView() {
		iv_setting_group = (ImageView) this.findViewById(R.id.iv_setting_group);

		if (chatType == CHATTYPE_GROUP) {
			iv_setting_group.setVisibility(View.VISIBLE);
			iv_setting_group.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					startActivityForResult((new Intent(Activity_Chat.this,
							ChatRoomSettingActivity.class).putExtra("groupId",
							talker)), REQUEST_CODE_GROUP_DETAIL);
				}

			});
		}

	}



	/**
	 * 显示键盘图标
	 *
	 */
	public void setModeKeyboard(View view) {
		edittext_layout.setVisibility(View.VISIBLE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		// mEditTextContent.setVisibility(View.VISIBLE);
		mEditTextContent.requestFocus();
		// buttonSend.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.GONE);
	}

	/**
	 * 点击文字输入框
	 *
	 */
	public void editClick(View v) {
		listView.setSelection(listView.getCount() - 1);
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		}

	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "e_" + x;

			reslist.add(filename);

		}
		return reslist;

	}

	/**
	 * 获取表情的gridview的子view
	 *
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, 40));
		} else if (i == 3) {
			list.addAll(reslist.subList(40, reslist.size()));
		}
		list.add("back_over");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this,
				1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

						if (!filename.equals("back_over")) { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							@SuppressWarnings("rawtypes")
							Class clz = Class
									.forName("com.eventer.app.util.SmileUtils");
							Field field = clz.getField(filename);
							mEditTextContent.append(SmileUtils.getSmiledText(
									Activity_Chat.this,
									(String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(mEditTextContent.getText())) {

								int selectionStart = mEditTextContent
										.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = mEditTextContent.getText()
											.toString();
									String tempStr = body.substring(0,
											selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i,
												selectionStart);
										if (SmileUtils.containsKey(cs
												.toString()))
											mEditTextContent.getEditableText()
													.delete(i, selectionStart);
										else
											mEditTextContent.getEditableText()
													.delete(selectionStart - 1,
															selectionStart);
									} else {
										mEditTextContent.getEditableText()
												.delete(selectionStart - 1,
														selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		return view;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
		unregisterReceiver(msgReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

		if (chatType == CHATTYPE_SINGLE) { // 单聊
			User u = MyApplication.getInstance().getContactList().get(talker);
			if (u != null) {
				user = u;
				if (!TextUtils.isEmpty(u.getBeizhu()))
					setBaseTitle(u.getBeizhu());
				else if (!TextUtils.isEmpty(u.getNick()))
					setBaseTitle(u.getNick());
			} else {
				UserInfo info = MyApplication.getInstance().getUserList()
						.get(talker);
				if (info != null) {
					user.setAvatar(info.getAvatar());
					user.setNick(info.getNick());
				}
			}

		} else {
			ChatroomDao dao = new ChatroomDao(context);
			ChatRoom room = dao.getRoom(talker);
			String roomName = "群聊";
			if (room != null) {
				roomName = room.getRoomname();
			}
			if (TextUtils.isEmpty(roomName)) {
				roomName = "群聊";
			}
			findViewById(R.id.container_voice_call).setVisibility(View.GONE);
			// String groupName = getIntent().getStringExtra("groupName");
			setBaseTitle(roomName);

		}
		refresh();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 覆盖手机返回键
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		} else {
			startActivity(new Intent().setClass(context, MainActivity.class));
			// scrollToFinishActivity();
			finish();
		}
	}

//	public String getToChatUsername() {
//		return talker;
//	}

	private void send() {
		String contString = mEditTextContent.getText().toString();
		if (Constant.isConnectNet&&contString.length() > 0) {
			ChatEntity entity = new ChatEntity();
			entity.setType(1);
			entity.setFrom(talker);
			entity.setContent(contString);
			entity.setMsgTime(System.currentTimeMillis() / 1000);
			entity.setStatus(2);
			entity.setMsgID(System.currentTimeMillis());
			mData.add(entity);
			adapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			listView.setSelection(listView.getCount() - 1);
		}else if(!Constant.isConnectNet){
			Toast.makeText(context,getText(R.string.no_network),Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void back(View view){
		startActivity(new Intent().setClass(context, MainActivity.class));
		// scrollToFinishActivity();
		finish();
	}
	/**
	 * 消息图标点击事件
	 *
	 */
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_send:
				String s = mEditTextContent.getText().toString();
				if (chatType == CHATTYPE_SINGLE
						&& !MyApplication.getInstance().getContactList()
						.containsKey(talker)) {
					isFriend(talker,s);
				} else if (TextUtils.isEmpty(s)) {
					Toast.makeText(context, "内容不能为空！", Toast.LENGTH_SHORT).show();
				} else {
					sendText(s);
				}
				break;
			case R.id.iv_emoticons_checked:
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				btnContainer.setVisibility(View.VISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				more.setVisibility(View.GONE);
				break;
			case R.id.iv_emoticons_normal:
				more.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.INVISIBLE);
				iv_emoticons_checked.setVisibility(View.VISIBLE);
				btnContainer.setVisibility(View.GONE);
				emojiIconContainer.setVisibility(View.VISIBLE);
				hideKeyboard();
				break;
			case R.id.iv_clear:
				re_notify.setVisibility(View.GONE);
				break;
			case R.id.re_notify:
				startActivity(new Intent().setClass(context,
						GroupSchedualActivity.class).putExtra("groupId", talker));
				break;
			default:
				break;
		}
	}

	/**
	 * 从服务器端拉取好友列表
	 *
	 */
	protected void isFriend(final String fid, final String s) {
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
							sendText(s);
						}else{
							showAlert();
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
					Log.e("1", status.toString());
				}
			}

		}.execute(params);
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
		content.setText(getResources().getString(R.string.not_friend_hint));
		ok.setText(getResources().getString(R.string.add));
		ok.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("ShowToast")
			public void onClick(View v) {

				startActivity(new Intent()
						.setClass(context, Activity_Friends_Add.class)
						.putExtra("id", talker)
						.putExtra("avatar", user.getAvatar())
						.putExtra("nick", user.getNick()));
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
	 * 发送文本消息
	 *
	 * @param content
	 *            message content
	 */
	private void sendText(String content) {

		if (content.length() > 0) {
			mEditTextContent.setText("");
			ChatEntity msg = new ChatEntity();
			long time = System.currentTimeMillis();
			msg.setType(1);
			Log.e("1", talker);
			msg.setFrom(talker);
			msg.setContent(content);
			msg.setMsgTime(time / 1000);
			msg.setStatus(2);
			msg.setMsgID(time);
			ChatEntityDao dao = new ChatEntityDao(context);
			dao.saveMessage(msg);
			mData.add(0, msg);
			adapter.notifyDataSetChanged();
			listView.setSelection(listView.getCount() - 1);

			JSONObject send_json = new JSONObject();
			try {
				send_json.put("action", "send");
				send_json.put("data", content);
				send_json.put("type", 1);
				String send_body = send_json.toString();
				if (chatType == CHATTYPE_SINGLE)
					MainActivity.instance
							.newMsg("1", talker, send_body, 1 | 16);
				else
					MainActivity.instance.newMsg(talker, talker, send_body, 49);
				send();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			setResult(RESULT_OK);

		}
	}

	/**
	 * 广播接收器
	 *
	 * @author len
	 *
	 */
	public class MsgReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 拿到进度，更新UI
			String id = intent.getStringExtra("talker");
			long time = intent.getLongExtra("time", -1);
			String mid = intent.getStringExtra("mid");
			boolean needRecv=false;
			try {
				if(chatType==CHATTYPE_SINGLE&&"1".equals(mid)){
					if(talker.equals(id)){
						needRecv=true;
					}
				}else{
					if(talker.equals(mid)){
						needRecv=true;
					}
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(needRecv){
				if (time == -1) {
					time = System.currentTimeMillis() / 1000;
				}
				String msg = intent.getStringExtra("msg");
				JSONObject recvJs;
				try {
					recvJs = JSONObject.parseObject(msg);
					String bodyString = recvJs.getString("data");
					toastqueue.add(bodyString);
					Message m = new Message();
					m.what = 33;
					Bundle bundle = new Bundle();
					if (talker.equals(id))
						bundle.putString("body", bodyString);
					else if (talker.equals(mid)) {
						bundle.putString("body", id + ":\n" + bodyString);
					}
					// 往Bundle中存放数据
					bundle.putLong("time", time); // 往Bundle中put数据
					bundle.putString("msg", msg); // 往Bundle中存放数据
					m.obj = bundle;
					mHandler.sendMessage(m);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				abortBroadcast();
			}


		}

	}

	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {
			finish();
		}

		if (resultCode == RESULT_CODE_EXIT_GROUP) {
			setResult(RESULT_OK);
			finish();
		}
//		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
//
//		}
//		if (resultCode == RESULT_OK) { // 清空消息
//
//		}
	}

	/**
	 * listview滑动监听listener
	 *
	 */
	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					if (view.getFirstVisiblePosition() == 0 && !isloading
							&& haveMoreData) {
						loadmorePB.setVisibility(View.VISIBLE);
						// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
						List<ChatEntity> messages;
						try {
							// 获取更多messges，调用此方法的时候从db获取的messages
							ChatEntityDao dao = new ChatEntityDao(context);
							messages = dao.getMsgList(talker,
									adapter.getItem(mData.size() - 1).getMsgID(),
									pagesize);

						} catch (Exception e1) {
							loadmorePB.setVisibility(View.GONE);
							return;
						}
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (messages.size() != 0) {
							// 刷新ui
							for (ChatEntity chatEntity : messages) {
								mData.add(chatEntity);
							}
							Log.e("1", mData.size() + "");
							adapter.notifyDataSetChanged();
							listView.setSelection(messages.size() - 1);
							if (messages.size() != pagesize)
								haveMoreData = false;
						} else {
							haveMoreData = false;
						}
						loadmorePB.setVisibility(View.GONE);
						isloading = false;
					}
					break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
							 int visibleItemCount, int totalItemCount) {

		}

	}


}
