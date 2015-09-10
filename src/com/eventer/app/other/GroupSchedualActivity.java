package com.eventer.app.other;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.easemob.util.DateUtils;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.R.id;
import com.eventer.app.R.layout;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.entity.Event;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.ui.base.BaseActivity;
import com.eventer.app.util.LocalUserInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GroupSchedualActivity extends BaseActivity {
	
	private ListView listview;
	private List<Map<String,String>> mData=new ArrayList<Map<String,String>>();
	private MyAadpter adapter;
	private Context context;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_share);
		context=this;
		initView();	
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		listview=(ListView)findViewById(R.id.listview);
		final String groupId=getIntent().getStringExtra("groupId");
		ChatEntityDao dao=new ChatEntityDao(context);
		mData=dao.getShareScheduals(groupId);
		adapter=new MyAadpter(context);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Map<String,String> map=mData.get(position);
				String share=map.get("shareId");
				context.startActivity(new Intent().setClass(context,ShareSchedualActivity.class)
	        			 .putExtra("groupId", groupId)
	        			 .putExtra("shareId", share));
			}
		});
		
	}

	public class MyAadpter extends BaseAdapter{
	    private LayoutInflater mInflater;  
		  
	    public MyAadpter(Context context) {  
	            this.mInflater = LayoutInflater.from(context);  
	     }  
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();  
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			Map<String,String> map=mData.get(position);
			if (convertView == null) {                
                holder=new ViewHolder();                    
                //可以理解为从vlist获取view  之后把view返回给ListView                  
                convertView = mInflater.inflate(R.layout.item_history_eventlist, null);  
                holder.title = (TextView)convertView.findViewById(R.id.tv_title); 
                holder.info=(TextView)convertView.findViewById(R.id.tv_info);             
                convertView.setTag(holder);               
            }else {               
                holder = (ViewHolder)convertView.getTag();  
            }  
			String content=map.get("content");
			String publisher="";
			 int type=0;
			 String title="";
			int loc=content.indexOf("\n");
			if(loc!=-1){
				content=content.substring(loc+1);
				publisher=content.substring(0,loc);
			}else{
				publisher=Constant.UID;
			}
			try{
				JSONObject json=JSONObject.parseObject(content);
			    title=json.getString("schedual_title");
			    type=json.getInteger("schedual_type");		 
			}catch(Exception e){
				
			}
			switch (type) {
			case 2:
				title="【日程】"+title;
				break;
			case 3:
				title="【待办】"+title;
				break;
			default:
				break;
			}
            
			holder.title.setText(title);
			
			if(publisher.equals(Constant.UID)){	
				String nick=LocalUserInfo.getInstance(context).getUserInfo("nick");
				holder.info.setText("发布者: "+nick);
    		}else if(MyApplication.getInstance().getContactList().containsKey(publisher)){
    			User u=MyApplication.getInstance().getContactList().get(publisher);
    			String nick=u.getNick();
    			String beizhu=u.getBeizhu();
    			if(!TextUtils.isEmpty(beizhu)){
    				nick=beizhu;}
    			holder.info.setText("发布者: "+nick);   				
    			
    		}else if(MyApplication.getInstance().getUserList().containsKey(publisher)){
    			UserInfo u=MyApplication.getInstance().getUserList().get(publisher);
    			String nick=u.getNick();
    			holder.info.setText("发布者: "+nick);
    		}else{
    			
    		}
			
			return convertView;
		}
		
	}
	//提取出来方便点  
    public final class ViewHolder {   
        TextView title; 
        TextView info;

    }
}
