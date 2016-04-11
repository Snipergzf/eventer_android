package com.eventer.app.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.adapter.ConversationAdapter;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.other.Activity_Chat;
import com.eventer.app.other.Activity_Contact;
import com.eventer.app.service.CheckInternetService;
import com.eventer.app.widget.swipemenulistview.SwipeMenu;
import com.eventer.app.widget.swipemenulistview.SwipeMenuCreator;
import com.eventer.app.widget.swipemenulistview.SwipeMenuItem;
import com.eventer.app.widget.swipemenulistview.SwipeMenuListView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;



@SuppressLint("InflateParams")
public  class MessageFragment extends Fragment implements OnScrollListener {

	private SwipeMenuListView listView;
	LayoutInflater infalter;
	private List<ChatEntity> mData=new ArrayList<>();
	private Context context;
	private ConversationAdapter adapter;
	ImageView contact;
	public static MessageFragment instance;
	private NetReceiver netReceiver;
	private final int NET_GOOD = 11;
	private final int NET_BAD = 22;
	private LinearLayout note;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		final View rootView = inflater.inflate(R.layout.fragment_message, container, false);
		context=getActivity();
		instance=MessageFragment.this;
		initView(rootView);
		netReceiver = new NetReceiver();
		IntentFilter intentFilter1 = new IntentFilter();
		intentFilter1.addAction("android.net.conn.ISGOODORBAD");
		context.registerReceiver(netReceiver, intentFilter1);
		return rootView;

	}

	private void initView(View rootView) {
		listView = (SwipeMenuListView) rootView.findViewById(R.id.lv_conversation);
		contact = (ImageView) rootView.findViewById(R.id.iv_contact);

		note=(LinearLayout)rootView.findViewById(R.id.note);
		note.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				context.startService(new Intent(context, CheckInternetService.class));
//				Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
//				startActivity(intent);
			}
		});
		infalter=LayoutInflater.from(context);
//		View headView = infalter.inflate(R.layout.item_conversation_header,
//				null);
//		listView.addHeaderView(headView);
//		re_contact=(RelativeLayout)headView.findViewById(R.id.re_contact);
		adapter = new ConversationAdapter(context,mData);
		listView.setAdapter(adapter);
		listView.setEmptyView(rootView.findViewById(R.id.iv_empty));
		if(!Constant.isConnectNet){
			note.setVisibility(View.VISIBLE);
		}
		//给对话的item的添加左滑菜单
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem openItem = new SwipeMenuItem(
						context);
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				openItem.setWidth(dp2px(90));
				openItem.setTitle("进入");
				openItem.setTitleSize(18);
				openItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(openItem);
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						context);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		listView.setMenuCreator(creator);

		listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				ChatEntity item = mData.get(position);
				switch (index) {
					case 0:
						Log.e("1", "listview-count:" + listView.getCount());
						ChatEntity msg = mData.get(position);
						String username = msg.getFrom();

						Intent intent = new Intent();
						intent.setClass(getActivity(), Activity_Chat.class);
						if (username.contains("@")) {
							intent.putExtra("chatType", Activity_Chat.CHATTYPE_GROUP);
							intent.putExtra("groupId", username);
						} else {
							intent.putExtra("userId", username);
						}
						startActivity(intent);
						break;
					case 1:
						// delete
						delete(item);
//	                   mData.remove(position);
						refreshView();
						break;
				}
				return false;
			}
		});

		// set SwipeListener
		listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});

		// set MenuStateChangeListener
		listView.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
			@Override
			public void onMenuOpen(int position) {
			}

			@Override
			public void onMenuClose(int position) {
			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
										   int position, long id) {
//				if (position != 0) {
					ChatEntity msg = mData.get(position - 1);
					showMyDialog("提示", msg, position - 1);
//				}
				return true;
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
//				if (position != listView.getCount()) {
					Log.e("1", "listview-count:" + listView.getCount());
					ChatEntity msg = mData.get(position);
					String username = msg.getFrom();

					Intent intent = new Intent();
					intent.setClass(getActivity(), Activity_Chat.class);
					if (username.contains("@")) {
						intent.putExtra("chatType", Activity_Chat.CHATTYPE_GROUP);
						intent.putExtra("groupId", username);
					} else {
						intent.putExtra("userId", username);
					}
				    MainActivity activity = MainActivity.instance;
					if(activity!=null){
						activity.cancelNotify(username);
					}
					startActivity(intent);
//				}
			}


		});
		contact.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(getActivity(), Activity_Contact.class);
				startActivity(intent);
			}
		});
		refresh();
	}

	//删除对话
	private void delete(ChatEntity item) {
		ChatEntityDao dao=new ChatEntityDao(context);
		dao.deleteMessageByUser(item.getFrom());
	}


	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	//删除对话的消息框 
	private void showMyDialog(String title, final ChatEntity message, final int position) {

		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		Window window = dlg.getWindow();
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.alertdialog);
		window.findViewById(R.id.ll_title).setVisibility(View.VISIBLE);

		TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
		tv_title.setText(title);
		TextView tv_content1 = (TextView) window.findViewById(R.id.tv_content1);
		// 是否已经置顶
//            tv_content1.setText("置顶聊天");
		tv_content1.setVisibility(View.GONE);
		TextView tv_content2 = (TextView) window.findViewById(R.id.tv_content2);
		tv_content2.setText("删除该对话");
		tv_content2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				delete(message);
//                mData.remove(position);
				refreshView();
				dlg.cancel();

			}
		});

	}

	public void refreshData(){
		MainActivity.instance.updateUnreadLabel();
		RefreshThread thread=new RefreshThread();
		new Thread(thread).start();
	}

	class RefreshThread  implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			refresh();
		}
	}
	//刷新页面
	private void refresh(){
		mData=new ArrayList<>();
		ChatEntityDao dao=new ChatEntityDao(context);
		//获取对话列表
		mData=dao.getChatEntityList(new String[]{"*","Max(addTime)"},null, null,ChatEntityDao.COLUMN_NAME_FROM,ChatEntityDao.COLUMN_NAME_TIME+" desc");
		//获取未读信息
		List<ChatEntity> unreadMsg=dao.getChatEntityList(new String[]{"*" ,"Max(addTime)","Count(*) as NotRead"}, "status=1", null, "talker", null);
		for (ChatEntity chatEntity : unreadMsg) {
			String user=chatEntity.getFrom();
			int unread=chatEntity.getNotRead();
			for (ChatEntity chat : mData) {
				if(chat.getFrom().equals(user)){
					chat.setNotRead(unread);
				}
			}
		}

	}

	public void refreshView(){
		refresh();
		adapter = new ConversationAdapter(context,mData);
		listView.setAdapter(adapter);
	}


	/**
	 * receive network situation
	 *
	 * @author LiuNana
	 *
	 */
	@SuppressLint("NewApi")
	public class NetReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean net=intent.getBooleanExtra("net",false);
			Log.e("1","rece"+net+"");
			if(net){
				mHandler.sendEmptyMessage(NET_GOOD);
			}else{
				mHandler.sendEmptyMessage(NET_BAD);
			}
		}
	}

	Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case NET_BAD:
					note.setVisibility(View.VISIBLE);
					break;
				case NET_GOOD:
					note.setVisibility(View.GONE);
					break;
				default:
					break;
			}
			return false;
		}
	});


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(netReceiver);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}


	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshView();
		MobclickAgent.onPageStart("MainScreen"); //统计页面
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}




}
  		
     
