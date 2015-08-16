package com.eventer.app.other;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.adapter.FriendAdapter;
import com.eventer.app.entity.User;

public class FriendSearchResult extends Activity {
	private ListView listView;
	private FriendAdapter adapter;
	private List<User> contactList=new ArrayList<User>();;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsearch_result);
        initView();
    
    }

   

    private void initView() {
		// TODO Auto-generated method stub
    	listView = (ListView) findViewById(R.id.listview);
    	User u=new User();
        u.setUsername("3");
		u.setNick("小明");
		u.setBeizhu(null);
		u.setRegion("上海");
		u.setSex("1");
		u.setSign(null);
		u.setTel("13667252029");
		u.setAvatar(null);
    	contactList.add(u);
        adapter = new FriendAdapter(this, R.layout.item_friend_list,
                contactList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {                	                    
                	    User user=contactList.get(position); 
	                    Intent intent=new Intent();
	                    MyApplication.getInstance().setValueByKey("friend_search_info", user);
	                    intent.setClass(FriendSearchResult.this,Activity_UserInfo.class);
	                    startActivity(intent);
                
            }
        });
	}
    
	public void back(View view) {
        finish();
    }

}