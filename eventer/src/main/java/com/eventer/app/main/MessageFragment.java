package com.eventer.app.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.adapter.ConversationAdapter;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.other.Activity_Chat;
import com.eventer.app.other.Activity_Contact;
import com.eventer.app.view.MyToast;
import com.eventer.app.view.swipemenulistview.SwipeMenu;
import com.eventer.app.view.swipemenulistview.SwipeMenuCreator;
import com.eventer.app.view.swipemenulistview.SwipeMenuItem;
import com.eventer.app.view.swipemenulistview.SwipeMenuListView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;



@SuppressLint("InflateParams")
public  class MessageFragment extends Fragment{

	private SwipeMenuListView listView;
	LayoutInflater infalter;
	private List<ChatEntity> mData=new ArrayList<>();
	private Context context;
	ImageView contact;
	public static MessageFragment instance;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_message, container, false);
		context = getActivity();
		instance = MessageFragment.this;
		infalter = LayoutInflater.from(context);
		initView(rootView);
		refreshView();
		return rootView;

	}

	/***
	 * 初始化控件，给控件添加事件响应
	 */
	private void initView(View rootView) {
		listView = (SwipeMenuListView) rootView.findViewById(R.id.lv_conversation);
		contact = (ImageView) rootView.findViewById(R.id.iv_contact);

		contact.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!"0".equals(Constant.UID)) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), Activity_Contact.class);
					startActivity(intent);
				} else {
					MyToast.makeText(context, "请登录！", Toast.LENGTH_SHORT).show();
				}

			}
		});

		//为ListView设置各种事件
		ConversationAdapter adapter = new ConversationAdapter(context, mData);
		listView.setAdapter(adapter);
		listView.setEmptyView(rootView.findViewById(R.id.iv_empty));


		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
										   int position, long id) {
				ChatEntity msg = mData.get(position - 1);
				showMyDialog("提示", msg);
				return true;
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
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
				if (activity != null) {
					activity.cancelNotify(username);
				}
				startActivity(intent);
			}


		});

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
						refreshView();
						break;
				}
				return false;
			}
		});
	}

	/***
	 * 刷新页面的数据
	 */
	public void refreshView(){
		mData.clear();
		ChatEntityDao dao = new ChatEntityDao(context);
		//获取对话列表
		List<ChatEntity> msgList = dao.getChatEntityList(new String[]{"*","Max(addTime)"},null, null,ChatEntityDao.COLUMN_NAME_FROM,ChatEntityDao.COLUMN_NAME_TIME+" desc");
		for (ChatEntity chat: msgList){
			mData.add(chat);
		}
		//获取未读信息
		List<ChatEntity> unreadMsg = dao.getChatEntityList(new String[]{"*" ,"Max(addTime)",
				"Count(*) as NotRead"}, "status=1", null, "talker", null);
		for (ChatEntity chatEntity : unreadMsg) {
			String user = chatEntity.getFrom();
			int unread = chatEntity.getNotRead();
			for (ChatEntity chat : mData) {
				if(chat.getFrom().equals(user)){
					chat.setNotRead(unread);
				}
			}
		}

	}

	public void refreshData(){
		MainActivity.instance.updateUnreadLabel();
		refreshView();
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
	private void showMyDialog(String title, final ChatEntity message) {

		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		Window window = dlg.getWindow();
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.alertdialog);
		window.findViewById(R.id.ll_title).setVisibility(View.VISIBLE);

		TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
		tv_title.setText(title);
		TextView tv_content1 = (TextView) window.findViewById(R.id.tv_content1);
		tv_content1.setVisibility(View.GONE);
		TextView tv_content2 = (TextView) window.findViewById(R.id.tv_content2);
		tv_content2.setText("删除该对话");
		tv_content2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				delete(message);
				refreshView();
				dlg.cancel();

			}
		});

	}



	public void onResume() {
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
  		
     
