package com.eventer.app.main;

import java.util.ArrayList;
import java.util.List;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.eventer.app.R;
import com.eventer.app.adapter.ConversationAdapter;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.other.Activity_Chat;
import com.eventer.app.other.Activity_Contact;



@SuppressLint("InflateParams")
public  class MessageFragment extends Fragment implements OnScrollListener {

	private SwipeMenuListView listView;
	private LayoutInflater infalter;
	private List<ChatEntity> mData=new ArrayList<ChatEntity>();
	private Context context;
	private ConversationAdapter adapter;
	private RelativeLayout re_contact;	
	public static MessageFragment instance;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		context=getActivity();
		return inflater.inflate(R.layout.fragment_message, container, false);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		listView = (SwipeMenuListView) getView().findViewById(R.id.lv_conversation);
		infalter=LayoutInflater.from(getActivity());
	    View headView = infalter.inflate(R.layout.item_conversation_header,
	                null);
	    listView.addHeaderView(headView);
	    re_contact=(RelativeLayout)headView.findViewById(R.id.re_contact);
	    adapter = new ConversationAdapter(context,mData);
        listView.setAdapter(adapter);
        
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
 
     	listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
     			@Override
     			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
     				ChatEntity item = mData.get(position);
     				switch (index) {
     				case 0:
 	                	  Log.e("1","listview-count:"+listView.getCount());
 	                	  ChatEntity msg=mData.get(position);
 		                  String username = msg.getFrom(); 
 		                  
 		                  Intent intent=new Intent();
 		                  intent.setClass(getActivity(),Activity_Chat.class);
 		                  if(username.indexOf("@")!=-1){
 		                	  intent.putExtra("chatType", Activity_Chat.CHATTYPE_GROUP);
 		                      intent.putExtra("groupId", username);
 		                  }else{ 
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

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position!=0){
					ChatEntity msg=mData.get(position-1);
					showMyDialog("提示",msg,position-1);
				}
				return true;
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if(position!=0&&position!=listView.getCount()){
                	  Log.e("1","listview-count:"+listView.getCount());
                	  ChatEntity msg=mData.get(position-1);
	                  String username = msg.getFrom(); 
	                  
	                  Intent intent=new Intent();
	                  intent.setClass(getActivity(),Activity_Chat.class);
	                  if(username.indexOf("@")!=-1){
	                	  intent.putExtra("chatType", Activity_Chat.CHATTYPE_GROUP);
	                      intent.putExtra("groupId", username);
	                  }else{ 
		                  intent.putExtra("userId", username);
	                  }
	                  startActivity(intent);
                }
            }


        });
        re_contact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
	        	intent.setClass(getActivity(), Activity_Contact.class);
	        	startActivity(intent);
			}
		});
        
        instance=MessageFragment.this;
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
		mData=new ArrayList<ChatEntity>();
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

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshView();
	}
	
	
	
	
}
  		
     
