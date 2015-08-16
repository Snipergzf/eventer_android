package com.eventer.app.other;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.adapter.NewFriendsAdapter;
import com.eventer.app.db.InviteMessgeDao;
import com.eventer.app.entity.InviteMessage;

/**
 * 申请与通知
 * 
 */
public class Activity_NewFriends extends Activity {
    private ListView listView;
    private RelativeLayout contact_rl;
    private Context context;
    private NewFriendsAdapter adapter;
    private List<InviteMessage> msgs;
    private InviteMessgeDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newfriendsmsg);
        // DemoApplication.getInstance().addActivity(this);
        context=Activity_NewFriends.this;
        initView();
        
        
//        tv_add.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Activity_NewFriends.this,
//                        AddFriendsOneActivity.class));
//            }
//
//        });

//        dao = new InviteMessgeDao(this);
//        msgs = dao.getMessagesList();
        // 设置adapter
//        adapter = new NewFriendsAdapter(this, null);
//        listView.setAdapter(adapter);
//        User userTemp = DemoApplication.getInstance().getContactList()
//                .get(Constant.NEW_FRIENDS_USERNAME);
//        if (userTemp != null && userTemp.getUnreadMsgCount() != 0) {
//            userTemp.setUnreadMsgCount(0);
//        }
//
//        DemoApplication.getInstance().getContactList()
//                .get(Constant.NEW_FRIENDS_USERNAME).setUnreadMsgCount(0);
       
    }

    

  
    private void initView() {
		// TODO Auto-generated method stub
    	listView = (ListView) findViewById(R.id.listview);
        contact_rl=(RelativeLayout)findViewById(R.id.addFriend_contact_rl);
        TextView et_search = (TextView) findViewById(R.id.et_search);
     
        et_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_NewFriends.this,
                        Activity_SearchFriends.class));
            }

        });
        
        contact_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Activity_NewFriends.this,
                        LocalContactActivity.class));
			}
		});
        
        dao = new InviteMessgeDao(this);
        msgs = dao.getMessagesList();
        adapter = new NewFriendsAdapter(this, msgs);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				InviteMessage msg=msgs.get(position);
				Intent intent=new Intent();
                intent.putExtra("user", msg.getId()+"");
                intent.setClass(context,Activity_UserInfo.class);
                startActivity(intent);
			}
		});

	}




	public void back(View v) {
        finish();
    }


}
