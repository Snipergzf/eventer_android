package com.eventer.app.main;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.adapter.EventAdapter;
import com.eventer.app.db.EventDao;
import com.eventer.app.entity.Event;
import com.eventer.app.http.HttpParamUnit;
import com.eventer.app.http.JSONtoEntity;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.other.Activity_EventDetail;
import com.eventer.app.util.PreferenceUtils;
import com.eventer.app.view.MyToast;
import com.eventer.app.view.refreshlist.IXListViewLoadMore;
import com.eventer.app.view.refreshlist.IXListViewRefreshListener;
import com.eventer.app.view.refreshlist.XListView;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public  class ActivityFragment extends Fragment implements OnClickListener {

	private XListView listView;
	private EventAdapter listViewAdapter;
	private List<Event> listItems; //ListView的数据

	private final int REFRESH_MORE = 0;
	private final int LOADING_MORE = 1;

	private TextView[] themelist;
	private int themeindex = 0;

	private String theme = "all";

	private	List<Event> all_event= new ArrayList<>(); //所有活动的列表
	private List<String> id_list=new ArrayList<>(); //所有活动的ID列表

	private Context context;
	public static ActivityFragment instance;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_activity, container, false);
		context=getActivity();
		instance=ActivityFragment.this;
		initView(rootView);
		IntentFilter intentFilter1 = new IntentFilter();
		intentFilter1.addAction("android.net.conn.ISGOODORBAD");
		return rootView;
	}

	/***
	 * 初始化控件，给控件添加事件响应
	 */
	private void initView(View rootView) {
		
		listView = (XListView) rootView.findViewById(R.id.listview);
		themelist = new TextView[5];
		themelist[0] = (TextView) rootView.findViewById(R.id.tv_theme_all);
		themelist[1] = (TextView) rootView.findViewById(R.id.tv_theme_lecture);
		themelist[2] = (TextView) rootView.findViewById(R.id.tv_theme_fun);
		themelist[3] = (TextView) rootView.findViewById(R.id.tv_theme_job);
		themelist[4] = (TextView) rootView.findViewById(R.id.tv_theme_other);
		themelist[0].setSelected(true);
		themelist[0].setOnClickListener(this);
		themelist[1].setOnClickListener(this);
		themelist[2].setOnClickListener(this);
		themelist[3].setOnClickListener(this);
		themelist[4].setOnClickListener(this);

		listItems = new ArrayList<>();
        loadEventList(0, REFRESH_MORE);

		listView.setEmptyView(rootView.findViewById(R.id.iv_empty));
		rootView.findViewById(R.id.iv_empty).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadEventList(0, REFRESH_MORE);
			  }
		});
		//活动列表的适配器
		listViewAdapter = new EventAdapter(this.getActivity(), listItems); //创建适配器
		listView.setAdapter(listViewAdapter);
		listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

		//活动item点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				if (position - 1 > -1) {
					Event e = listItems.get(position - 1);
					Intent intent = new Intent();
					intent.setClass(getActivity(), Activity_EventDetail.class);
					intent.putExtra("event_id", e.getEventID());
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.in_from_right, 0);
				}
			}
		});

        //listView的刷新响应
		listView.setPullRefreshEnable(new IXListViewRefreshListener() {

			@Override
			public void onRefresh() {
				mHandler.sendEmptyMessageDelayed(REFRESH_MORE, 800);
			}
		});

		//listView的加载响应
		listView.setPullLoadEnable(new IXListViewLoadMore() {

			@Override
			public void onLoadMore() {
				mHandler.sendEmptyMessageDelayed(LOADING_MORE, 800);
			}
		});

	}


	/**
	 * 页面控件的点击事件,切换主题
	 */
	@Override
	public void onClick(View v) {
		int index;
		theme = "all";
		switch (v.getId()) {
			case R.id.tv_theme_all:
				index = 0;
				theme = "all";
				break;
			case R.id.tv_theme_lecture:
				index = 1;
				theme = "讲座";
				break;
			case R.id.tv_theme_fun:
				index = 2;
				theme = "娱乐";
				break;
			case R.id.tv_theme_job:
				index = 3;
				theme = "招聘";
				break;
			case R.id.tv_theme_other:
				index = 4;
				theme = "其他";
				break;
			default:
				index=-1;
				break;
		}
		if(index > -1){
			themelist[themeindex].setSelected(false);
			themelist[index].setSelected(true);
			themeindex = index;
			listItems.clear();
			loadEventList(0, REFRESH_MORE);

		}

	}

	/**
	 * 向服务器发送请求，加载活动列表
	 * @param pos  已经加载的活动数量
	 * @param type 加载类型，刷新还是加载
	 */
	private void loadEventList(int pos, final int type) {
		if ("0".equals(Constant.UID)){
			loadTouristEventList(pos, type);
		} else {
			loadUserEventList(pos, type);
		}
	}


	/**
	 * 向服务器发送请求，加载活动列表
	 * @param pos  已经加载的活动数量
	 * @param type 加载类型，刷新还是加载
	 */
	private void loadUserEventList(int pos, final int type) {
		Map<String, String> map = HttpParamUnit.userEventListParam( pos, 10, theme);
		LoadDataFromHTTP task = new LoadDataFromHTTP(context,
				Constant.URL_GET_USERTEVENTLIST, map);
		task.getData(new LoadDataFromHTTP.DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				try {
					int code = data.getInteger("status");
					if (code == 0) {
						JSONObject action = data.getJSONObject("event_action");
						com.alibaba.fastjson.JSONArray json = action.getJSONArray("event");
						List<Event> list = JSONtoEntity.JsonToEventList(json);
						if(list.size() > 0){
							EventDao dao = new EventDao(context);
							if(type == REFRESH_MORE){
								listItems.clear();
								id_list.clear();
								if( "all".equals(theme)){
									dao.delBriefEvent();
								}
							}
							for (Event e: list) {
								dao.saveEvent(e);
								if(!id_list.contains( e.getEventID() )){
									listItems.add(e);
									id_list.add(e.getEventID());
								}

							}
							listViewAdapter.notifyDataSetChanged();
						}
					} else if (code == 40) {
						MyToast.makeText(context, "操作过于频繁，请稍后再试！",
								Toast.LENGTH_SHORT).show();
					} else if (code == 41) {
						MyToast.makeText(context, "没有更新活动了！",
								Toast.LENGTH_SHORT).show();
					} else{
						if(!Constant.isConnectNet){
							MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if(!Constant.isConnectNet){
						MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
					}

					showEventFromDB();
				} finally {
					listView.stopLoadMore();
					listView.stopRefresh();
				}
			}

		});
	}

	/**
	 * 向服务器发送请求，加载活动列表
	 * @param pos  已经加载的活动数量
	 * @param type 加载类型，刷新还是加载
	 */
	private void loadTouristEventList(int pos, final int type) {
		String imei = PreferenceUtils.getInstance().getDeviceId();
		Map<String, String> map = HttpParamUnit.eventListParam(imei, pos, 10, theme);
		LoadDataFromHTTP task = new LoadDataFromHTTP(context,
				Constant.URL_GET_TOURISTEVENTLIST, map);
		task.getData(new LoadDataFromHTTP.DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				try {
					int code = data.getInteger("status");
					if (code == 0) {
						JSONObject action = data.getJSONObject("event_action");
						com.alibaba.fastjson.JSONArray json = action.getJSONArray("event");
						List<Event> list = JSONtoEntity.JsonToEventList(json);
						if(list.size()>0){
							EventDao dao = new EventDao(context);
							if(type == REFRESH_MORE){
								listItems.clear();
								id_list.clear();
								dao.delBriefEvent();
							}
							for (Event e: list) {
								dao.saveEvent(e);
								if(!id_list.contains( e.getEventID() )){
									listItems.add(e);
									id_list.add(e.getEventID());
								}
							}
							listViewAdapter.notifyDataSetChanged();
						}
					} else if (code == 40) {
						MyToast.makeText(context, "操作过于频繁，请稍后再试！",
								Toast.LENGTH_SHORT).show();
					} else if (code == 41) {
						MyToast.makeText(context, "没有更新活动了！",
								Toast.LENGTH_SHORT).show();
					} else{
						if(!Constant.isConnectNet){
							MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
						}
						showEventFromDB();
					}
				} catch (Exception e) {
					e.printStackTrace();
					if(!Constant.isConnectNet){
						MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
					}
					showEventFromDB();
				} finally {
					listView.stopLoadMore();
					listView.stopRefresh();
				}
			}
		});
	}


	private void showEventFromDB() {
		EventDao dao = new EventDao(context);
		all_event = dao.getBriefEventList();
		if(!"all".equals(theme)){
			for (Event event : all_event) {
				if(theme.equals(event.getTheme())){
					if(!id_list.contains(event.getEventID())){
						listItems.add(event);
						id_list.add(event.getEventID());
					}

				}
			}
		} else{
			for (Event event : all_event) {
				if(!id_list.contains(event.getEventID())){
					listItems.add(event);
					id_list.add(event.getEventID());
				}
			}
		}
		if(listItems.size() > 0){
			Collections.sort(listItems, new EventComparator() {
			});
		}
		listViewAdapter.notifyDataSetChanged();
	}


	/**
	 * 处理列表的加载和刷新
	 */
	Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case REFRESH_MORE:

						loadEventList(0, REFRESH_MORE);


					break;
				case LOADING_MORE:

						loadEventList(listItems.size(), LOADING_MORE);


					break;
				default:
					break;
			}
			return false;
		}
	});







	public String getTime(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		return  sdf.format(new Date(now));
	}


	/**
	 * 为活动列表排序
	 */
	public class EventComparator implements Comparator<Event>{

		@Override
		public int compare(Event e1, Event e2) {
			String start1=e1.getStart()+"";
			String start2=e2.getStart()+"";
			return start1.compareTo(start2);
		}

	}



	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MainScreen"); //统计页面
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}

}


  		
     
