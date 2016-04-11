package com.eventer.app.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.adapter.EventAdapter;
import com.eventer.app.db.EventDao;
import com.eventer.app.db.EventOpDao;
import com.eventer.app.entity.Event;
import com.eventer.app.widget.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CollectActivity extends SwipeBackActivity {
	ListView listview;
	private EventAdapter adapter;
	private Context context;
	private List<Event> mData=new ArrayList<>();
	EventOpDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect);
		context=this;
		setBaseTitle(R.string.my_collect);
		dao=new EventOpDao(context);
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		listview=(ListView)findViewById(R.id.listview);
		adapter=new EventAdapter(this, mData);
		listview.setEmptyView(findViewById(R.id.tv_empty));
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				Event e = mData.get(position);
				Intent intent = new Intent();
				intent.setClass(context, Activity_EventDetail.class);
				intent.putExtra("event_id", e.getEventID());
//				MyApplication.getInstance().setValueByKey("event_detail", e);
				startActivity(intent);
			}
		});
		getListItems();
	}

	private void getListItems() {
		mData.clear();
		List<Event> listItems;
		EventDao dao=new EventDao(context);
		listItems=dao.getEventListByInfo(new String[]{Constant.UID + "", "1"});
		for (Event event : listItems) {
			event = getEventItem(event);
			if(event != null){
				mData.add(event);
			}
		}
		Collections.sort(mData, new EventComparator() {
		});
		adapter.notifyDataSetChanged();
	}

	public class EventComparator implements Comparator<Event> {

		@Override
		public int compare(Event e1, Event e2) {
			// TODO Auto-generated method stub
			String start1=e1.getStart()+"";
			String start2=e2.getStart()+"";
			return start2.compareTo(start1);
		}

	}

	private Event getEventItem(Event event) {
		// TODO Auto-generated method stub
			String time=event.getTime();
			JSONArray time1;
			try {
				time1 = new JSONArray(time);

				for(int i=0;i<time1.length()/2;i++){
					long begin=time1.getLong(2*i);
					long end=time1.getLong(2*i+1);
					if(i==0){
						event.setStart(begin);
						event.setEnd(end);
					}
					if(begin<event.getStart()){
						event.setStart(begin);
					}
					if(end>event.getEnd()){
						event.setEnd(end);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//时间成对，可能有多个时间
		return event;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
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
