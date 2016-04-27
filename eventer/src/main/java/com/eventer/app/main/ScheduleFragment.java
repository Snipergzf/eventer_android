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
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.adapter.SchedualAdapter;
import com.eventer.app.db.ClassInfoDao;
import com.eventer.app.db.DBManager;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.entity.ClassInfo;
import com.eventer.app.entity.Schedual;
import com.eventer.app.other.Activity_Course;
import com.eventer.app.other.Activity_EventDetail;
import com.eventer.app.other.Calendar_AddSchedual;
import com.eventer.app.other.Calendar_ViewSchedual;
import com.eventer.app.other.ShareSchedualActivity;
import com.eventer.app.view.MyToast;
import com.eventer.app.view.calendar.AlarmReceiver;
import com.eventer.app.view.calendar.CaldroidFragment;
import com.eventer.app.view.calendar.CaldroidFragment.TurntoTodayListener;
import com.eventer.app.view.calendar.CaldroidListener;
import com.eventer.app.view.swipemenulistview.SwipeMenu;
import com.eventer.app.view.swipemenulistview.SwipeMenuCreator;
import com.eventer.app.view.swipemenulistview.SwipeMenuItem;
import com.eventer.app.view.swipemenulistview.SwipeMenuListView;
import com.eventer.app.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;

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
	private TextView tv_course;
	private float mLastY = -1;
	private int NowWeek = -1;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) 		 {
		final View rootView = inflater.inflate(R.layout.fragment_schedual, container, false);
		View footView = inflater.inflate(R.layout.sheduallist_footer,
				null);
		View headView = inflater.inflate(R.layout.sheduallist_header,
				null);
		instance=this;
		context=getActivity();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		today = sdf.format(new Date());
		eventlist_time=(TextView) rootView.findViewById(R.id.eventlist_time);
		tv_course=(TextView) headView.findViewById(R.id.tv_course_info);
		caldroidFragment = new CaldroidFragment();
		if (savedInstanceState != null&&!IsRefresh) {
			caldroidFragment.restoreStatesFromKey(savedInstanceState,
					"CALDROID_SAVED_STATE");
		}
		else {
			Bundle args = new Bundle();
			//设置日历的初始参数
			Calendar cal = Calendar.getInstance();
			args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
			args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
			args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
			args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);

			caldroidFragment.setArguments(args);
		}
		rootView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				onTouchEvent(event);
				return false;
			}
		});
		headView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!"0".equals(Constant.UID)){
					startActivity(new Intent().setClass(context, Activity_Course.class)
							.putExtra("week",NowWeek));
				} else{
					MyToast.makeText(context, "请登录！", Toast.LENGTH_SHORT).show();
				}
			}
		});
		FragmentTransaction t = getFragmentManager().beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.commit();
		caldroidFragment.setCaldroidListener(listener);
		caldroidFragment.turnToToday(new TurntoTodayListener() {
			@Override
			public void onTurnTo() {
				// TODO Auto-generated method stub
				try {
					setSelectDate(formatter.parse(today));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		IsRefresh=false;
		eventList=(SwipeMenuListView)rootView.findViewById(R.id.calendar_lv);
		eventList.addFooterView(footView);
		eventList.addHeaderView(headView);
		footView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!"0".equals(Constant.UID)){
					addSchedual();
				} else{
					MyToast.makeText(context, "请登录！", Toast.LENGTH_SHORT).show();
				}
			}
		});
		//日程列表的适配器
		mAdapter=new SchedualAdapter(context, sList);
		eventList.setAdapter(mAdapter);
		eventList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				//点击条目时触发
				//arg2即为点中项的位置
				if(position>0&&position<eventList.getCount()-1){
					position=position-1;
					Schedual s=sList.get(position);
					String sid= s.getSchdeual_ID()+"";
					String eid=s.getEventId();
					//eid不为空，该事件为活动，则跳转至活动详情
					if(eid!=null&&!eid.equals("")){
						Intent intent=new Intent();
						intent.setClass(getActivity(), Activity_EventDetail.class);
						intent.putExtra("event_id", eid);
						startActivity(intent);
					}else{
						SchedualDao dao=new SchedualDao(context);
						s=dao.getSchedual(sid);
						String shareId=s.getShareId();
						if(!TextUtils.isEmpty(shareId)){
							context.startActivity(new Intent()
									.setClass(context, ShareSchedualActivity.class)
									.putExtra("schedual", true)
									.putExtra("shareId", shareId));
						}else{
							Intent intent=new Intent();
							intent.setClass(getActivity(), Calendar_ViewSchedual.class);
							Bundle bundle = new Bundle();                           //创建Bundle对象
							bundle.putString(Calendar_ViewSchedual.ARGUMENT_ID, sid);     //装入数据
							bundle.putString(Calendar_ViewSchedual.ARGUMENT_DATE, eventdate);
							bundle.putInt(Calendar_ViewSchedual.ARGUMENT_LOC, position);
							intent.putExtras(bundle);
							startActivityForResult(intent,110);
						}
					}
				}

			}
		});


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
				Log.e("1",".................");
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
		eventList.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//				position=position-1;
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
						delete(item);
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

		eventList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
										   int position, long id) {
				return true;
			}
		});
		showCourseInfo();
		return rootView;
	}

	//dp转化成px
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	//删除日程
	private void delete(Schedual item) {
		SchedualDao dao=new SchedualDao(context);
		dao.deleteSchedual(item.getSchdeual_ID()+"");
	}
	//跳转至添加日程界面
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
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

		if (caldroidFragment != null) {
			caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
		}
	}

	public void onStart()
	{
		super.onStart();
	}
	//从数据库中获取日程列表的数据
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
		// Log.e("1",""+dbHelper.deleteDatabase(getActivity()));

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
			switch(_f){
				case 0:
					if(eventdate.compareTo(start_time[0])>=0&&eventdate.compareTo(end_time[0])<=0)
					{
						IsTodayEvent=true;
					}
					break;
				case 1:
					if(eventdate.compareTo(start_time[0])>=0)
						IsTodayEvent=true;
					break;
				case 2:
					int i=Start_dt.getWeekDay();
					if(i>1&&i<7&&eventdate.compareTo(start_time[0])>=0)
						IsTodayEvent=true;
					break;
				case 3:
					if (today_dt.getWeekDay().equals(Start_dt.getWeekDay()) && eventdate.compareTo(start_time[0]) >= 0) {
						IsTodayEvent=true;
					}
					break;
				case 4:
					if(today_dt.getDay().equals(Start_dt.getDay())&&eventdate.compareTo(start_time[0])>=0)
						IsTodayEvent=true;
					break;
				case 5:
					int month1=today_dt.getMonth();
					int day1=today_dt.getDay();
					int month2=Start_dt.getMonth();
					int day2=Start_dt.getDay();
					if (month1==month2&&day1==day2&&eventdate.compareTo(start_time[0])>=0)
						IsTodayEvent=true;
					break;
			}
			int type=c.getInt(c.getColumnIndex("type"));
			int status=c.getInt(c.getColumnIndex("status"));
			if(type==3){
				if(status==0){
					if(eventdate.equals(end_time[0])){
						IsTodayEvent=true;
					}
				}else if(eventdate.equals(today)&&eventdate.compareTo(end_time[0])<=0){
					IsTodayEvent=true;
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
				if(type==3){
					if(end_time[0].equals(eventdate)){
						s.setStarttime(EndTime.substring(11));
					}else{
						s.setEndtime("00:00");
					}
				}else{
					if(start_time[0].equals(end_time[0])){
						s.setStarttime(StartTime.substring(11));
					}else{
						if(start_time[0].equals(eventdate)){
							s.setStarttime(StartTime.substring(11));
						}else if(eventdate.compareTo(start_time[0])>0&&eventdate.compareTo(end_time[0])<0){
							s.setStarttime("00:00");
						}else if(end_time[0].equals(eventdate)){
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

	private List<ClassInfo> initCouseInfo(){
		List<ClassInfo> classList=new ArrayList<>();
		DBManager dbHelper;
		dbHelper = new DBManager(context);
		dbHelper.openDatabase();
		Cursor c=dbHelper.findList(true, "dbCourseSetting", null,
				null, null, null, null,null,null);
//		SimpleDateFormat sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//		String  time =sDateFormat.format(new Date());
		if(TextUtils.isEmpty(eventdate)){
			eventdate=today;
		}
		DateTime Today=new DateTime(eventdate);
		int weekday=Today.getWeekDay()-1;
		NowWeek=-1;
		if(c.moveToNext()){
			String start=c.getString(c.getColumnIndex("StartDay"));
			DateTime startDay=new DateTime(start);
			int diff=startDay.numDaysFrom(Today);
			if(diff>=0){
				NowWeek=diff/7+1;
			}
			dbHelper.closeDatabase();
		}else{
			//课表为空
			dbHelper.closeDatabase();
			return classList;
		}
		List<ClassInfo> AllClassList;
		ClassInfoDao dao =new ClassInfoDao(context);
		AllClassList=dao.getClassInfoList();
		for (ClassInfo c_info : AllClassList) {
			if(c_info.getWeeks().contains(NowWeek)&&c_info.getWeekday()==weekday){
				classList.add(c_info);
				Log.e("1",c_info.getClassname());
			}
		}
		return classList;
	}

//	public boolean isSelectDateEvent(){
//		boolean isEvent=false;
//		return isEvent;
//	}

	//日历中的cell的事件监听
	public	CaldroidListener listener = new CaldroidListener() {
		@Override
		public void onSelectDate(Date date, View view) {
			setSelectDate(date);
		}
		@Override
		public void onChangeMonth(int month, int year) {
		}

		@Override
		public void onLongClickDate(Date date, View view) {
			if(!"0".equals(Constant.UID)){
				setSelectDate(date);
				addSchedual();
			} else{
				MyToast.makeText(context, "请登录！", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onCaldroidViewCreated() {
		}

	};
	//设置日历的选中日期
	//根据选中日期，刷新日程列表
	public void setSelectDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH)+1;
		int year = calendar.get(Calendar.YEAR);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if(day>9){
			if(month>9)
				eventdate=year+"-"+month+"-"+day;
			else
				eventdate=year+"-0"+month+"-"+day;
		}else{
			if(month>9)
				eventdate=year+"-"+month+"-0"+day;
			else
				eventdate=year+"-0"+month+"-0"+day;
		}
		SetEventListData();
		if (month != CaldroidFragment.month) {
			caldroidFragment.moveToDate(date);
		}
		String select_dt= formatter.format(date);
		String today=formatter.format(new Date());

		if(lastdate!=null){
			String last=formatter.format(lastdate);
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
		lastdate=date;
		mAdapter=new SchedualAdapter(context, sList);
		eventList.setAdapter(mAdapter);
		showCourseInfo();
	}

	private void showCourseInfo() {
		List<ClassInfo> list=initCouseInfo();
		String time;
		if(today.equals(eventdate)){
			time="今日";
		}else{
			time="当日";
		}
		if(list.size()>0){
			tv_course.setText(time+"有"+list.size()+"门课程,亲查看课表！");
		}else{
			tv_course.setText(time+"没有课程！");
		}
	}


	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastY = ev.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
//				final float deltaY = ev.getRawY() - mLastY;
				mLastY = ev.getRawY();
//				if (deltaY > 0){
//				updateHeaderHeight(deltaY / OFFSET_RADIO);
//			}else if ( deltaY < 0) {
//				updateFooterHeight(-deltaY / OFFSET_RADIO);
//			}
				break;
			default:
				mLastY = -1; // reset	
				break;
		}

		return true;
	}

	//
//  protected void updateHeaderHeight(float delta) {	
//	setVisiableHeight((int) delta
//			+ getVisiableHeight());
//   }
//  public void setVisiableHeight(int height) {
//		if (height < 0)
//			height = 0;
//		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) eventList
//				.getLayoutParams();
//		lp.height = height;
//		eventList.setLayoutParams(lp);
//	}
//
//	public int getVisiableHeight() {
//		return eventList.getHeight();
//	}
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
	public void onStop()
	{
		super.onStop();
		Log.e("1", "stop-----------");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();


	}

	@Override
	public void onResume()
	{
		super.onResume();
//		if(IsRefresh){
//		}
		refreshView();
		IsRefresh=false;

	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

}
  		
     
