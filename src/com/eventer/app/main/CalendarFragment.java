package com.eventer.app.main;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.adapter.SchedualAdapter;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.DBManager;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.entity.Event;
import com.eventer.app.entity.Schedual;
import com.eventer.app.other.Activity_EventDetail;
import com.eventer.app.other.Calendar_AddSchedual;
import com.eventer.app.other.Calendar_ViewSchedual;
import com.eventer.app.socket.Activity_Chat;
import com.eventer.app.widget.calendar.CaldroidFragment;
import com.eventer.app.widget.calendar.CaldroidListener;


public  class CalendarFragment extends Fragment  {

	private CaldroidFragment caldroidFragment;
	public static CalendarFragment instance;
	private Date lastdate;
	public static final int REQUEST_DETAIL = 0x110;
	public static final int REQUEST_LIST = 0x109;
	public static boolean IsRefresh = false;
	private TextView eventlist_time;
	private Date select_date=new Date();
	final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private SwipeMenuListView eventList;
	private SchedualAdapter mAdapter;
	private List<Schedual> sList=new ArrayList<Schedual>();
	public static String eventdate="";
	private Context context;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 		 {
		final View rootView = inflater.inflate(R.layout.calendar_main, container, false);	
		instance=this;
		context=getActivity();
		
		eventlist_time=(TextView) rootView.findViewById(R.id.eventlist_time);
		caldroidFragment = new CaldroidFragment();
		if (savedInstanceState != null&&!IsRefresh) {
			caldroidFragment.restoreStatesFromKey(savedInstanceState,
					"CALDROID_SAVED_STATE");
			
		}
		// If activity is created from fresh
		else {
			Bundle args = new Bundle();
			
			Calendar cal = Calendar.getInstance();
			args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
			args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
			args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
			args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);

			caldroidFragment.setArguments(args);
	
			
		}
		

		// Attach to the activity
		FragmentTransaction t = getFragmentManager().beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.commit();

		caldroidFragment.setCaldroidListener(listener);
		IsRefresh=false;
		eventList=(SwipeMenuListView)rootView.findViewById(R.id.calendar_lv);
		mAdapter=new SchedualAdapter(context, sList);	
		eventList.setAdapter(mAdapter);
		eventList.setOnItemClickListener(new OnItemClickListener(){	      
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//点击条目时触发
	              //arg2即为点中项的位置 
				  Schedual s=sList.get(position);
	              String sid= s.getSchdeual_ID()+"";
	              String eid=s.getEventId();
	              if(eid!=null&&!eid.equals("")){
	            	Log.e("1", eid);
		  			Intent intent=new Intent();
		  			intent.setClass(getActivity(), Activity_EventDetail.class);
		  			intent.putExtra("event_id", eid);
		  			startActivity(intent);
	              }else{
	            	  Log.e("1", sid);
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
	      });
		Log.e("1","create____________________________________1");
		
		
		SwipeMenuCreator creator = new SwipeMenuCreator() {

 			@Override
 			public void create(SwipeMenu menu) {
				SwipeMenuItem openItem = new SwipeMenuItem(
						context);
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				openItem.setWidth(dp2px(60));
				openItem.setTitle("完成");
				openItem.setTitleSize(18);
				openItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(openItem);
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

 	eventList.setOnMenuItemClickListener(new OnMenuItemClickListener() {
 			@Override
 			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
 				Schedual item = sList.get(position);
 				switch (index) {
 				case 0:
 					item.setStatus(0);
	                SchedualDao dao=new SchedualDao(context);
	                dao.update(item);
	                sList.remove(position);
	                sList.add(position, item);
	                mAdapter.notifyDataSetChanged();
 					break;
     			case 1:
 					// delete
     			    delete(item);
                    refreshView();
	                caldroidFragment.SetScheduleDates();
	        		caldroidFragment.refreshView();
 					break;
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
		return rootView;
	}
	
	public void refreshView() {
		// TODO Auto-generated method stub
		SetEventListData();
	    mAdapter=new SchedualAdapter(context, sList);
		eventList.setAdapter(mAdapter);
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	

	private void delete(Schedual item) {
		SchedualDao dao=new SchedualDao(context);
		dao.deleteSchedual(item.getSchdeual_ID()+"");
	}
	
	public void addCourse(){
		Intent intent = new Intent();
		intent.setClass(getActivity(), Calendar_AddSchedual.class);
		if(eventdate != null && eventdate.length() != 0){            	
		}else{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        	eventdate= formatter.format(new Date());
		}
		intent.putExtra(Calendar_AddSchedual.ARGUMENT, eventdate);
		startActivityForResult(intent, 122);
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
		{
			super.onActivityResult(requestCode, resultCode, data);
			Log.e("1","____________________________________111111111111111111111111111111111111111");
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
	
	public void SetEventListData(){
		sList.clear();
		DBManager dbHelper;
		dbHelper = new DBManager(getActivity());
        dbHelper.openDatabase();
        if(eventdate != null && eventdate.length() != 0){
        	
		}else{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        	eventdate= formatter.format(new Date());
		}	
        String time=eventdate.replace('-', '/');
    	eventlist_time.setText(time);
		DateTime today_dt=new DateTime(eventdate+" 00:00:00");
       // Log.e("1",""+dbHelper.deleteDatabase(getActivity()));
    	Cursor c=dbHelper.findList(true, "dbSchedule", null,
    			null, null, null, null,"startTime",null);
        while (c.moveToNext()) {
        	
        	int _f=c.getInt(c.getColumnIndex("frequency")); 
        	boolean IsTodayEvent=false;       	
        	String StartTime=c.getString(c.getColumnIndex("startTime"));
        	String EndTime=c.getString(c.getColumnIndex("endTime"));
        	if(TextUtils.isEmpty(EndTime)){
        		EndTime=StartTime;
        	}
        	String[] end_time=EndTime.split(" ");
        	DateTime End_dt=new DateTime(EndTime+":00");        	  
        	
        	String[] start_time=StartTime.split(" ");
        	DateTime Start_dt=new DateTime(StartTime+":00");
        	DateTime time_db=new DateTime(start_time[0]+" 00:00:00");
        	int diff=today_dt.numDaysFrom(time_db);
        	int diff1=0; 
        	
        	switch(_f){
	        	case 0:	        		      
		            Map<String, String> map = new HashMap<String, String>();
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
	        		  if(Start_dt.getWeekDay()>1&&Start_dt.getWeekDay()<7&&eventdate.compareTo(start_time[0])>=0)
	        			  IsTodayEvent=true;	
	        		  break;
	        	case 3:
	        		  if(today_dt.getWeekDay()==Start_dt.getWeekDay()&&eventdate.compareTo(start_time[0])>=0)
	        			  IsTodayEvent=true;	  
	        		  break;
	            case 4:
	        		  if(today_dt.getDay()==Start_dt.getDay()&&eventdate.compareTo(start_time[0])>=0)
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
        	
        	if(IsTodayEvent){
        		Schedual s=new Schedual();
        		long id= c.getLong(c.getColumnIndex("scheduleID"));
            	String eid=c.getString(c.getColumnIndex("eventID"));
            	String title=c.getString(c.getColumnIndex("title"));
            	String place=c.getString(c.getColumnIndex("place"));
            	int status=c.getInt(c.getColumnIndex("status"));
            	s.setSchdeual_ID(id);
			    s.setEventId(eid);
			    s.setTitle(title);
			    s.setPlace(place);
			    s.setStatus(status);
			    Map<String,String> map=new HashMap<String, String>();
        		if(start_time[0].equals(end_time[0])){       			    
 			        s.setStarttime(StartTime.substring(11));  
        		}else{    			
        			if(start_time[0].equals(eventdate)){
        				s.setStarttime(StartTime.substring(11)); 				    		          			        
        			}else if(eventdate.compareTo(start_time[0])>0&&eventdate.compareTo(end_time[0])<0){
        				s.setStarttime("00:00");
        			}else if(end_time[0].equals(eventdate)){
        				s.setStarttime("00:00");
        			}    			
        			
        		}
        		sList.add(s);
        	}
        	
        }  
        dbHelper.closeDatabase();
	}
	//setCustomResourceForDates();
			// Setup listener
	public	CaldroidListener listener = new CaldroidListener() {
				@Override
	            public void onSelectDate(Date date, View view) {
                     setSelectDate(date); 
				}
				@Override
				public void onChangeMonth(int month, int year) {
					String text = "month: " + month + " year: " + year;
//					Toast.makeText(getApplicationContext(), text,
//							Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onLongClickDate(Date date, View view) {
					setSelectDate(date);
					addCourse();
				}

				@Override
				public void onCaldroidViewCreated() {
//					if (caldroidFragment.getLeftArrowButton() != null) {
//						Toast.makeText(getApplicationContext(),
//								"Caldroid view is created", Toast.LENGTH_SHORT)
//								.show();
//					}
				}

			};
			
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
		
		Log.e("1",eventdate);
		if(month==CaldroidFragment.month){
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
			}
			caldroidFragment.refreshView();
			lastdate=date;
		}else{
			caldroidFragment.moveToDate(date);
			//caldroidFragment.setCaldroidListener(listener);
			if(lastdate!=null){
				caldroidFragment.setBackgroundResourceForDate(R.color.white,
						lastdate);	
			}
			caldroidFragment.setBackgroundResourceForDate(R.drawable.gray_border,
					date);
			caldroidFragment.refreshView();
			lastdate=date;
		}
		 select_date=date;
		 
		 SetEventListData();
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
        if(IsRefresh){						
		}
        Log.e("1", "resume--------------calendar");
        caldroidFragment.SetScheduleDates();
		caldroidFragment.refreshView();
		SetEventListData();
		mAdapter=new SchedualAdapter(context, sList);
		eventList.setAdapter(mAdapter);
		IsRefresh=false;
		
	} 
      
	@Override
	public void onPause()
	{
		super.onPause();
		Log.e("1", "pause--------------");
	}
}
  		
     
