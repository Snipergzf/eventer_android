package com.eventer.app.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.adapter.EventAdapter;
import com.eventer.app.db.EventDao;
import com.eventer.app.entity.Event;
import com.eventer.app.other.Activity_EventDetail;
import com.eventer.app.service.CheckInternetService;
import com.eventer.app.util.PreferenceUtils;
import com.eventer.app.widget.refreshlist.IXListViewRefreshListener;
import com.eventer.app.widget.refreshlist.XListView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public  class ActivityFragment extends Fragment implements OnClickListener,OnScrollListener {

	private XListView listView;
	private EventAdapter listViewAdapter;
	private List<Event> listItems;
	private final int REFRESH_MORE = 0;
	private final int NET_GOOD = 11;
	private final int NET_BAD = 22;
	private TextView[] themelist;
	private int themeindex=0;
	private String theme="";

	private int visibleLastIndex = 0;   //最后的可视项索引    
	private	ArrayList<Event> all_event= new ArrayList<>();
//	int datasize = 5;
	int visibleItemCount;       // 当前窗口可见项总数
	private Context context;
	private LinearLayout note;
	private NetReceiver netReceiver;
	public static ActivityFragment instance;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final View rootView = inflater.inflate(R.layout.fragment_activity, container, false);
		context=getActivity();
		instance=ActivityFragment.this;
		initView(rootView);
		initData();
		netReceiver = new NetReceiver();
		IntentFilter intentFilter1 = new IntentFilter();
		intentFilter1.addAction("android.net.conn.ISGOODORBAD");
		getActivity().registerReceiver(netReceiver, intentFilter1);
		return rootView;
	}


	private void initView(View rootView) {
		// TODO Auto-generated method stub
		note=(LinearLayout)rootView.findViewById(R.id.note);
		listView = (XListView)rootView.findViewById(R.id.listview);
		themelist=new TextView[5];
		themelist[0]=(TextView)rootView.findViewById(R.id.tv_theme_all);
		themelist[1]=(TextView)rootView.findViewById(R.id.tv_theme_lecture);
		themelist[2]=(TextView)rootView.findViewById(R.id.tv_theme_fun);
		themelist[3]=(TextView)rootView.findViewById(R.id.tv_theme_job);
		themelist[4]=(TextView)rootView.findViewById(R.id.tv_theme_other);
		themelist[0].setSelected(true);
		themelist[0].setOnClickListener(this);
		themelist[1].setOnClickListener(this);
		themelist[2].setOnClickListener(this);
		themelist[3].setOnClickListener(this);
		themelist[4].setOnClickListener(this);
		if(!Constant.isConnectNet){
			note.setVisibility(View.VISIBLE);
		}
		note.setOnClickListener(this);
		listItems = getListItems();
		listView.setEmptyView(rootView.findViewById(R.id.iv_empty));
		//活动列表的适配器
		listViewAdapter = new EventAdapter(this.getActivity(), listItems); //创建适配器
		listView.setAdapter(listViewAdapter);
		listView.setOnScrollListener(this);
		listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

		//活动item点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				if(position-1>-1){
					Event e=listItems.get(position-1);
					Intent intent=new Intent();
					intent.setClass(getActivity(), Activity_EventDetail.class);
					intent.putExtra("event_id", e.getEventID());
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.in_from_right, 0);
				}
			}
		});

		listView.setPullRefreshEnable(new IXListViewRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessageDelayed(REFRESH_MORE, 800);
			}
		});
	}

	private void animateViewIn() {

        Animation anim = AnimationUtils.loadAnimation(note.getContext(), R.anim.top_in);
        anim.setInterpolator(com.eventer.app.util.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        anim.setDuration(700);
		anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {


			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
        note.startAnimation(anim);

    }
	    private void animateViewOut() {

        Animation anim = AnimationUtils.loadAnimation(note.getContext(), R.anim.top_out);
        anim.setInterpolator(com.eventer.app.util.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
			anim.setDuration(700);
		anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
//                onViewHidden(event);
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
        note.startAnimation(anim);

    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int index;
		theme="";
		switch (v.getId()) {
			case R.id.tv_theme_all:
				index=0;
				break;
			case R.id.tv_theme_lecture:
				index=1;
				theme="讲座";
				break;
			case R.id.tv_theme_fun:
				index=2;
				theme = "文体";
				break;
			case R.id.tv_theme_job:
				index=3;
				theme="招聘";
				break;
			case R.id.tv_theme_other:
				index=4;
				theme="其他";
				break;
			case R.id.note:
				context.startService(new Intent(context, CheckInternetService.class));
				Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
				startActivity(intent);
				return;
			default:
				index=-1;
				break;
		}
		if(index>-1){
			themelist[themeindex].setSelected(false);
			themelist[index].setSelected(true);
			themeindex=index;
			listItems.clear();
			if(!theme.equals("")){
				for (Event event : all_event) {
					if(theme.equals(event.getTheme())){
						listItems.add(event);
					}
				}
			}else{
				for (Event event : all_event) {
					listItems.add(event);
				}
//				listItems=all_event;//两者指向同一内容
			}
			if(listItems.size()>0){
				Collections.sort(listItems, new EventComparator() {});
			}
			listViewAdapter.notifyDataSetChanged();
		}

	}

	Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case REFRESH_MORE:
					@SuppressWarnings("unchecked")
					List<Event> eventlist = (List<Event>) MyApplication.getInstance()
							.getCacheByKey("CacheEventList");
					if(eventlist!=null){
						for (Event event : eventlist) {
							event=getEventItem(event);
							if(event!=null){
								if(event.getTheme()!=null&&theme.equals(event.getTheme())){
									listItems.add(0,event);
								}
								all_event.add(event);
							}
						}
					}

					listViewAdapter.notifyDataSetChanged();
					MyApplication.getInstance()
							.setCacheByKey("CacheEventList", null);
					listView.stopRefresh();
					break;
				case NET_BAD:

					note.setVisibility(View.VISIBLE);
					note.setAnimation(AnimationUtils.loadAnimation(context,
						R.anim.fade_in));
					break;
				case NET_GOOD:
					note.setAnimation(AnimationUtils.loadAnimation(context,
							R.anim.fade_out));
					note.setVisibility(View.GONE);

					break;
				default:
					break;
			}
			return false;
		}
	});

	private void initData() {
		// TODO Auto-generated method stub
	}

	private List<Event> getListItems() {
		List<Event> listItems;
		List<Event> list = new ArrayList<>();
		EventDao dao=new EventDao(context);
		listItems=dao.getEventList();
		for (Event event : listItems) {
			event=getEventItem(event);
			if(event!=null){
				list.add(event);
				all_event.add(event);
			}
		}
		Collections.sort(list, new EventComparator() {});

		return list;
	}

	public void Refresh(){
		all_event.clear();
		getListItems();
		listItems.clear();
		if(!theme.equals("")){
			for (Event event : all_event) {
				if(theme.equals(event.getTheme())){
					listItems.add(event);
				}
			}
		}else{
			for (Event event : all_event) {
				listItems.add(event);
			}
		}
		if(listItems.size()>0){
			Collections.sort(listItems, new EventComparator() {});
		}
		listViewAdapter.notifyDataSetChanged();

	}

	public class EventComparator implements Comparator<Event>{

		@Override
		public int compare(Event e1, Event e2) {
			// TODO Auto-generated method stub
			String start1=e1.getStart()+"";
			String start2=e2.getStart()+"";
			return start1.compareTo(start2);
		}

	}

	private Event getEventItem(Event event) {
		// TODO Auto-generated method stub
		boolean alert= PreferenceUtils.getInstance().getDisplayUserlessEvent();
		if(!alert){
			String time=event.getTime();
			JSONArray time1;
			long now=System.currentTimeMillis()/1000;
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
						event.setEnd(end);;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//时间成对，可能有多个时间

			if(now>event.getEnd()){
				event=null;
			}
		}
        return event;
	}

	public String getTime(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		return  sdf.format(new Date(now));
	}

//	public void addEvent(Event event){
//		event=getEventItem(event);
//		if(event!=null){
//			listItems.add(0,event);
//			//listViewAdapter.addItem(event);
//			listViewAdapter.notifyDataSetChanged();
//		}
//	}

	/**
	 * receive network situation
	 *
	 * @author LiuNana
	 *
	 */
	@SuppressLint("NewApi")
	public class NetReceiver extends BroadcastReceiver{

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


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(netReceiver);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		int itemsLastIndex = listViewAdapter.getCount()-1;  //数据集最后一项的索引
		int lastIndex = itemsLastIndex + 1;
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& visibleLastIndex == lastIndex) {
			// 如果是自动加载,可以在这里放置异步加载数据的代码
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {

		this.visibleItemCount = visibleItemCount;
		visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
		//如果所有的记录选项等于数据集的条数，则出现列表底部视图
//		if(totalItemCount == datasize+1){
//
//		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MainScreen"); //统计页面
		listViewAdapter.notifyDataSetChanged();
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}
}
  		
     
