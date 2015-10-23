package com.eventer.app.other;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.util.DateUtils;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.EventDao;
import com.eventer.app.db.EventOpDao;
import com.eventer.app.entity.Event;
import com.umeng.analytics.MobclickAgent;

public class BrowserHistoryActivity extends Activity {

	private ListView listview;
	private MyEventAadpter adapter;
	private Context context;
	private List<Event> mData=new ArrayList<Event>();
	private EventOpDao dao;
	private Button btn_clear;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser_history);
		context=this;
		dao=new EventOpDao(context);
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
        listview=(ListView)findViewById(R.id.listview);
        btn_clear=(Button)findViewById(R.id.btn_clear);
		adapter=new MyEventAadpter(context);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Event e=mData.get(position);
				Intent intent=new Intent();
				intent.setClass(context, Activity_EventDetail.class);
				intent.putExtra("event_id", e.getEventID());
//				MyApplication.getInstance().setValueByKey("event_detail", e);
				startActivity(intent);
			}
		});
		btn_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dao.delOperation(Constant.UID, "4");
				mData=new ArrayList<Event>();
				adapter.notifyDataSetChanged();
			}
		});
	}
	public class MyEventAadpter extends BaseAdapter{
	    private LayoutInflater mInflater;  
		  
	    public MyEventAadpter(Context context) {  
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
			Event event=mData.get(position);
			if (convertView == null) {                
                holder=new ViewHolder();                    
                //可以理解为从vlist获取view  之后把view返回给ListView                  
                convertView = mInflater.inflate(R.layout.item_history_eventlist, null);  
                holder.title = (TextView)convertView.findViewById(R.id.tv_title); 
                holder.info=(TextView)convertView.findViewById(R.id.tv_info);
                holder.time=(TextView)convertView.findViewById(R.id.tv_time);
               
                convertView.setTag(holder);               
            }else {               
                holder = (ViewHolder)convertView.getTag();  
            }  
			String tilte=event.getTitle();
			String theme=event.getTheme();
			String publisher=event.getPublisher();
			long time=event.getOpTime();
			String temp_title="";
			String temp_info="";
			if(!TextUtils.isEmpty(theme)){
				temp_title+="【"+theme+"】 ";
			}
			temp_title+=tilte;
			holder.title.setText(temp_title);
			if(!TextUtils.isEmpty(publisher)){
				temp_info+="发布者: "+publisher;
			}
			holder.info.setText(temp_info);
			if(time>0){
				holder.time.setText(DateUtils.getTimestampString(new Date(time*1000)));
			}
			
			return convertView;
		}
		
	}
	
	private void refresh(){
		EventDao d=new EventDao(context);
		mData=d.getEventListByInfo(new String[]{Constant.UID+"","4"});
		adapter.notifyDataSetChanged();
		Log.e("1", mData.size()+"");
	}
	//提取出来方便点  
    public final class ViewHolder {   
        TextView title; 
        TextView time;
        TextView info;

    }
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		refresh();
	}
	public void back(View v){
		finish();
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}	
	
}
