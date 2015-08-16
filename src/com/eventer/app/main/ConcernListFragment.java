package com.eventer.app.main;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.EventDao;
import com.eventer.app.db.EventOpDao;
import com.eventer.app.entity.Event;
import com.eventer.app.main.CollectListFragment.MyEventAadpter;
import com.eventer.app.main.CollectListFragment.ViewHolder;
import com.eventer.app.other.Activity_EventDetail;

public class ConcernListFragment extends Fragment {
	private ListView listview;
	private MyEventAadpter adapter;
	private Context context;
	private List<Event> mData=new ArrayList<Event>();
	private EventOpDao dao;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_collect_eventlist, container, false);	
		context=getActivity();
		dao=new EventOpDao(context);
		initView(rootView);
		return rootView;
	}

	private void initView(View rootView) {
		// TODO Auto-generated method stub
		listview=(ListView)rootView.findViewById(R.id.listview);
//		EventDao d=new EventDao(context);
//		mData=d.getEventListByInfo(new String[]{Constant.UID+"","1"});
		
		adapter=new MyEventAadpter(context);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Event e=mData.get(position);
				Intent intent=new Intent();
				intent.setClass(getActivity(), Activity_EventDetail.class);
				intent.putExtra("event_id", e.getEventID());
//				MyApplication.getInstance().setValueByKey("event_detail", e);
				startActivity(intent);
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
                convertView = mInflater.inflate(R.layout.item_concern_eventlist, null);  
                holder.title = (TextView)convertView.findViewById(R.id.tv_title); 
                holder.iv=(ImageView)convertView.findViewById(R.id.iv_collect);
                convertView.setTag(holder);               
            }else {               
                holder = (ViewHolder)convertView.getTag();  
            }  
			holder.title.setText(event.getTitle());
			holder.iv.setSelected(true);
			
			holder.iv.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					((ImageView)v).setSelected(false);				
					dao.deleteRecord(EventOpDao.COLUMN_NAME_ID+"=? and "+EventOpDao.COLUMN_NAME_OPERATOR+"=?",
							new String[]{mData.get(position).getEventID()+"",Constant.UID+""});
//					mData.remove(position);
//					notifyDataSetChanged();
					
				}
			});
			return convertView;
		}
		
	}
	
	private void refresh(){
		EventDao d=new EventDao(context);
		mData=d.getEventListByInfo(new String[]{Constant.UID+"","2"});
		Log.e("1", mData.size()+"");
		adapter.notifyDataSetChanged();
	}
	//提取出来方便点  
    public final class ViewHolder {   
        public TextView title; 
        public ImageView iv;
    }
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refresh();
		
	}
    
}
