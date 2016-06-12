package com.eventer.app.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.adapter.SchedualAdapter;
import com.eventer.app.db.DBManager;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.entity.Schedual;
import com.eventer.app.other.Activity_EventDetail;
import com.eventer.app.other.Calendar_AddSchedual;
import com.eventer.app.other.Calendar_ViewSchedual;
import com.eventer.app.other.ShareSchedualActivity;
import com.eventer.app.view.MyToast;
import com.eventer.app.view.calendar.AlarmReceiver;
import com.eventer.app.view.calendar.CaldroidFragment;
import com.eventer.app.view.calendar.CaldroidListener;
import com.eventer.app.view.swipemenulistview.SwipeMenu;
import com.eventer.app.view.swipemenulistview.SwipeMenuCreator;
import com.eventer.app.view.swipemenulistview.SwipeMenuItem;
import com.eventer.app.view.swipemenulistview.SwipeMenuListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hirondelle.date4j.DateTime;


@SuppressLint("SimpleDateFormat")
public  class ScheduleFragment extends Fragment{

	private CaldroidFragment caldroidFragment;
	public static ScheduleFragment instance;
	private Date lastdate;
	public static final int REQUEST_DETAIL = 0x110;
	public static boolean IsRefresh = false;
	private TextView eventlist_time;
	final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private SwipeMenuListView eventList;
	private SchedualAdapter mAdapter;
	private List<Schedual> sList=new ArrayList<>();
	public static String eventdate="";
	private String today;
	private Context context;


	private View footView;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) 		 {
		final View rootView = inflater.inflate(R.layout.fragment_schedual, container, false);
		instance=this;
		context=getActivity();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		today = sdf.format(new Date());
		IsRefresh=false;

		footView = inflater.inflate(R.layout.sheduallist_footer,
				null);
		eventlist_time = (TextView) rootView.findViewById(R.id.eventlist_time);
		eventList = (SwipeMenuListView) rootView.findViewById(R.id.calendar_lv);
		caldroidFragment = new CaldroidFragment();

		if (savedInstanceState != null && !IsRefresh) {
			caldroidFragment.restoreStatesFromKey(savedInstanceState,
					"CALDROID_SAVED_STATE");
		} else {
			Bundle args = new Bundle();
			//设置日历的初始参数
			Calendar cal = Calendar.getInstance();
			args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
			args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
			args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
			args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);

			caldroidFragment.setArguments(args);
		}

		initView();
		return rootView;
	}

	private void initView() {
        initCalendar();
		initEventListView();
        setSwipeMenu();
	}

	private void initCalendar() {
		//初始化日历设置
		FragmentTransaction t = getFragmentManager().beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.commit();
		/***
		 * 日历中的事件监听
		 * 包括长按，选择日期（相当于点击事件）等
		 */
		caldroidFragment.setCaldroidListener(new CaldroidListener() {
			@Override
			public void onSelectDate(Date date, View view) {
				setSelectDate(date);
			}

			@Override
			public void onLongClickDate(Date date, View view) {
				if (!"0".equals(Constant.UID)) {
					setSelectDate(date);
					addSchedual();
				} else {
					MyToast.makeText(context, "请登录！", Toast.LENGTH_SHORT).show();
				}
			}

		});
		/***
		 * 日历中今天按钮的监听
		 */
		caldroidFragment.turnToToday(new CaldroidFragment.TurntoTodayListener() {
			@Override
			public void onTurnTo() {
				try {
					setSelectDate(formatter.parse(today));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/***
	 * 初始化日程列表
	 */
	private void initEventListView() {

		footView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!"0".equals(Constant.UID)) {
					addSchedual();
				} else {
					MyToast.makeText(context, "请登录！", Toast.LENGTH_SHORT).show();
				}
			}
		});

		eventList.addFooterView(footView); //课程信息

		//日程列表的适配器
		mAdapter = new SchedualAdapter(context, sList);
		eventList.setAdapter(mAdapter);

		eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() { //日程列表的item点击事件
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
					Schedual s = sList.get(position);
					String sid = s.getSchdeual_ID() + "";
					String eid = s.getEventId();
					//eid不为空，该事件为活动，则跳转至活动详情
					if (eid != null && !eid.equals("")) {
						Intent intent = new Intent();
						intent.setClass(getActivity(), Activity_EventDetail.class);
						intent.putExtra("event_id", eid);
						startActivity(intent);
					} else {
						SchedualDao dao = new SchedualDao(context);
						s = dao.getSchedual(sid);
						String shareId = s.getShareId();
						if (!TextUtils.isEmpty(shareId)) {
							context.startActivity(new Intent()
									.setClass(context, ShareSchedualActivity.class)
									.putExtra("schedual", true)
									.putExtra("shareId", shareId));
						} else {
							Intent intent = new Intent();
							intent.setClass(getActivity(), Calendar_ViewSchedual.class);
							Bundle bundle = new Bundle();                           //创建Bundle对象
							bundle.putString(Calendar_ViewSchedual.ARGUMENT_ID, sid);     //装入数据
							bundle.putString(Calendar_ViewSchedual.ARGUMENT_DATE, eventdate);
							bundle.putInt(Calendar_ViewSchedual.ARGUMENT_LOC, position);
							intent.putExtras(bundle);
							startActivityForResult(intent, 110);
						}
					}
				}

		});

		eventList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
										   int position, long id) {
				return true;
			}
		});
	}


	/***
	 * 实现日程列表item的左滑效果
	 */
	private void setSwipeMenu() {
		//日程列表左滑，显示菜单，实现“完成”和“删除”
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem finishItem = new SwipeMenuItem(
						context);
				finishItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				finishItem.setWidth(dp2px(60));
				finishItem.setTitle("完成");
				finishItem.setTitleSize(18);
				finishItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(finishItem);
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						context);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				deleteItem.setWidth(dp2px(60));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		eventList.setMenuCreator(creator);
		//为日程列表的左滑菜单中的按钮，设置点击事件
		eventList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				//position = position-1;
				Schedual item = sList.get(position);
				switch (index) {
					case 0:
						//标记完成
						String time = eventdate + " " + item.getStarttime();
						int type = item.getType();
						boolean isBegin = false;
						if (type < 3) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
							String nowtime = sdf.format(new Date());
							DateTime now = new DateTime(nowtime + ":00");
							DateTime begin = new DateTime(time + ":00");
							if (now.gteq(begin)) {
								isBegin = true;
							} else {
								Toast.makeText(context, "该事件还未开始，不可标记完成！", Toast.LENGTH_LONG).show();
							}
						} else {
							isBegin = true;
						}
						if (isBegin) {
							item.setStatus(0);
							SchedualDao dao = new SchedualDao(context);
							dao.update(item);
							sList.remove(position);
							sList.add(position, item);
							mAdapter.notifyDataSetChanged();
						}
						break;
					case 1:
						// delete
						deleteSchedual(item);
						refreshView();
						caldroidFragment.SetScheduleDates();
						caldroidFragment.refreshView();
						break;
				}
				long id = item.getSchdeual_ID();
				if (AlarmReceiver.Alarmlist != null && AlarmReceiver.Alarmlist.containsKey(id + "")) {
					AlarmReceiver.Alarmlist.remove(id + "");
				}
				return false;
			}
		});
	}


	//dp转化成px
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	/***
	 * 删除日程
	 */
	private void deleteSchedual(Schedual item) {
		SchedualDao dao=new SchedualDao(context);
		dao.deleteSchedual(item.getSchdeual_ID() + "");
	}

	/***
	 * 添加日程
	 */
	public void addSchedual(){
		Intent intent = new Intent();
		intent.setClass(getActivity(), Calendar_AddSchedual.class);
		if (eventdate == null || eventdate.length() == 0) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			eventdate= formatter.format(new Date());
		}
		intent.putExtra(Calendar_ViewSchedual.ARGUMENT_DATE, eventdate);
		startActivityForResult(intent, 122);
	}

	/***
	 * 读取数据库，获取选中日期的日程列表
	 */
	public void SetEventListData(){
		sList.clear();
		boolean hasEvent=false;
		new SchedualDao(context);
		DBManager dbHelper;
		dbHelper = new DBManager(getActivity());
		dbHelper.openDatabase();
		if (eventdate == null || eventdate.length() == 0) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			eventdate= formatter.format(new Date());
		}
		String time=eventdate.replace('-', '/');
		eventlist_time.setText(time);
		DateTime today_dt=new DateTime(eventdate+" 00:00:00");
		List<Long> list = new ArrayList<>();
		Cursor c=dbHelper.findList(true, "dbSchedule", null,
				" status >? and flag > 0", new String[]{"-1"},null, null, "startTime",null);
		while (c.moveToNext()) {
			long id= c.getLong(c.getColumnIndex("scheduleID"));
			if(list.contains(id)){
				continue;
			}else{
				list.add(id);
			}
			int _f=c.getInt(c.getColumnIndex("frequency"));
			boolean IsTodayEvent=false;
			String StartTime=c.getString(c.getColumnIndex("startTime"));
			String EndTime=c.getString(c.getColumnIndex("endTime"));
			if(TextUtils.isEmpty(EndTime)){
				EndTime=StartTime;
			}
			String[] end_time=EndTime.split(" ");
			String[] start_time=StartTime.split(" ");
			DateTime Start_dt=new DateTime(StartTime+":00");
			//判断日程是不是选中日期的日程
			switch(_f){
				case 0:
					if(eventdate.compareTo(start_time[0]) >= 0 && eventdate.compareTo(end_time[0]) <= 0)
					{
						IsTodayEvent=true;
					}
					break;
				case 1:
					if(eventdate.compareTo(start_time[0]) >= 0)
						IsTodayEvent=true;
					break;
				case 2:
					int i=Start_dt.getWeekDay();
					if(i > 1 && i < 7 &&eventdate.compareTo(start_time[0]) >= 0)
						IsTodayEvent=true;
					break;
				case 3:
					if (today_dt.getWeekDay().equals(Start_dt.getWeekDay()) && eventdate.compareTo(start_time[0]) >= 0) {
						IsTodayEvent=true;
					}
					break;
				case 4:
					if(today_dt.getDay().equals(Start_dt.getDay()) && eventdate.compareTo(start_time[0]) >= 0)
						IsTodayEvent=true;
					break;
				case 5:
					int month1 = today_dt.getMonth();
					int day1 = today_dt.getDay();
					int month2 = Start_dt.getMonth();
					int day2 = Start_dt.getDay();
					if (month1 == month2 && day1 == day2&&eventdate.compareTo(start_time[0]) >= 0)
						IsTodayEvent = true;
					break;
			}
			int type = c.getInt(c.getColumnIndex("type"));
			int status = c.getInt(c.getColumnIndex("status"));
			if(type == 3){
				if(status == 0){
					if(eventdate.equals(end_time[0])){
						IsTodayEvent = true;
					}
				}else if(eventdate.equals(today) && eventdate.compareTo(end_time[0]) <= 0){
					IsTodayEvent = true;
				}
			}

			if(IsTodayEvent){
				Schedual s=new Schedual();

				String eid=c.getString(c.getColumnIndex("eventID"));
				String title=c.getString(c.getColumnIndex("title"));
				String place=c.getString(c.getColumnIndex("place"));

				s.setSchdeual_ID(id);
				s.setEventId(eid);
				s.setTitle(title);
				s.setPlace(place);
				s.setStatus(status);
				s.setType(type);
				if(type == 3){
					if(end_time[0].equals(eventdate)){
						s.setStarttime(EndTime.substring(11));
					}else{
						s.setEndtime("00:00");
					}
				}else{
					if ( start_time[0].equals(end_time[0]) ) {
						s.setStarttime(StartTime.substring(11));
					} else{
						if(start_time[0].equals(eventdate)){
							s.setStarttime(StartTime.substring(11));
						} else if ( eventdate.compareTo(start_time[0]) > 0 &&
								eventdate.compareTo(end_time[0]) < 0){
							s.setStarttime("00:00");
						} else if (end_time[0].equals(eventdate) ){
							s.setStarttime(EndTime.substring(11));
						}
					}
				}
				sList.add(s);
				if(!hasEvent){
					caldroidFragment.addScheduleDates(0, eventdate);
					hasEvent=true;
				}
			}
		}
		dbHelper.closeDatabase();
	}


	/***
	 * 设置日历的选中日期
	 * 根据选中日期，刷新页面
	 */
	public void setSelectDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH)+1;
		int year = calendar.get(Calendar.YEAR);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if(day > 9){
			if(month > 9)
				eventdate = year+"-"+month+"-"+day;
			else
				eventdate = year+"-0"+month+"-"+day;
		}else{
			if(month > 9)
				eventdate = year+"-"+month+"-0"+day;
			else
				eventdate = year+"-0"+month+"-0"+day;
		}
		SetEventListData();
		if (month != CaldroidFragment.month) {
			caldroidFragment.moveToDate(date);
		}
		String select_dt = formatter.format(date);
		String today = formatter.format(new Date());

		if(lastdate != null){
			String last = formatter.format(lastdate);
			if(!last.equals(today)){
				caldroidFragment.setBackgroundResourceForDate(R.color.white,
						lastdate);
			}

		}
		if(!select_dt.equals(today)){
			caldroidFragment.setBackgroundResourceForDate(R.drawable.gray_border,
					date);
		}else{
			caldroidFragment.setBackgroundResourceForDate(R.drawable.red_border,
					date);
		}
		caldroidFragment.refreshView();
		lastdate = date;
		mAdapter = new SchedualAdapter(context, sList);
		eventList.setAdapter(mAdapter);
	}




	//刷新界面
	public void refreshView() {
		// TODO Auto-generated method stub		
		caldroidFragment.SetScheduleDates();
		SetEventListData();
		caldroidFragment.refreshView();
		mAdapter=new SchedualAdapter(context, sList);
		eventList.setAdapter(mAdapter);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_DETAIL)
		{
			String evaluate = data
					.getStringExtra(Calendar_AddSchedual.RESPONSE);
			Toast.makeText(getActivity(), evaluate, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Save current states of the Caldroid here
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (caldroidFragment != null) {
			caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
		}
	}


	@Override
	public void onResume()
	{
		super.onResume();
		refreshView();
		IsRefresh=false;

	}

}
  		
     
