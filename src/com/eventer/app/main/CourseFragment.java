package com.eventer.app.main;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eventer.app.R;
import com.eventer.app.db.ClassInfoDao;
import com.eventer.app.db.DBManager;
import com.eventer.app.entity.ClassInfo;
import com.eventer.app.other.Course_View;
import com.eventer.app.other.Course_edit;
import com.eventer.app.other.Course_setting;
import com.eventer.app.view.CourseView;
import com.eventer.app.view.CourseView.OnItemClassClickListener;
import com.eventer.app.view.PopMenu;


public  class CourseFragment extends Fragment{

	public static CourseFragment instance = null;
	private CourseView courseView;
	private List<ClassInfo> classList;
	private List<ClassInfo> AllClassList;
	public static int NowWeek=1;
	public static int showWeek=1;
	private DateTime startDay;
	public static int totalWeek;
	private int startWeekday,showType,classTotal;
	public static int COURSE_SETTING=0x11;
	SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd"); 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView=inflater.inflate(R.layout.fragment_course, container, false);
		instance=this;
		courseView = (CourseView) rootView.findViewById(R.id.courseview);

		initSetting();		
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("classTotal", classTotal);
		params.put("startWeekday", startWeekday);
		params.put("showType", showType);
		params.put("StartDay", startDay.toString());
		courseView.initSetting(params);
//		initClassData();
//		scheduleView.setWeek(NowWeek);
//		scheduleView.setClassList(classList);// ���γ���Ϣ���뵽�α���	
		courseView
				.setOnItemClassClickListener(new OnItemClassClickListener() {

					@Override
					public void onClick(ClassInfo classInfo) {
						Toast.makeText(getActivity(),
								"������Ŀγ��ǣ�" + classInfo.getClassname(),
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(getActivity(), Course_View.class);
						intent.putExtra("CourseID", classInfo.getClassid());
						startActivity(intent);

					}
				});		
		ScheduleFragment.instance.weekinfo_tv.setText("��"+NowWeek+"��");		
		showWeek=NowWeek;


		return rootView;
	}
	
	private void initSetting(){
		DBManager dbHelper;
		dbHelper = new DBManager(getActivity());
	    dbHelper.openDatabase();
	// Log.e("1",""+dbHelper.deleteDatabase(getActivity()));
		Cursor c=dbHelper.findList(true, "dbCourseSetting", null,
				null, null, null, null,null,null);
		
		String   time =sDateFormat.format(new   Date());
		DateTime Today=new DateTime(time);
		int weekday=Today.getWeekDay();
		boolean isNew=true;
		while (c.moveToNext()){
			 isNew=false;
			 String start=c.getString(c.getColumnIndex("StartDay"));
			 startDay=new DateTime(start);
			 showType=c.getInt(c.getColumnIndex("ShowType"));
			 totalWeek=c.getInt(c.getColumnIndex("TotalWeek"));
			 classTotal=c.getInt(c.getColumnIndex("MaxHour"));
			 startWeekday=c.getInt(c.getColumnIndex("StartWeekday"));	
			 int diff=startDay.numDaysFrom(Today);
			 if(diff>=0){
				 NowWeek=diff/7+1;
				 if(NowWeek>totalWeek){
					 totalWeek=NowWeek;
				 }
				 ScheduleFragment.instance.weekinfo_tv.setText("��"+NowWeek+"��");
			 }
		 }
		if(isNew){
			startDay=Today.minusDays(weekday-1);	
			showType=0;
			startWeekday=1;
			classTotal=12;
			totalWeek=24;		  
			int month=startDay.getMonth();
			int year=startDay.getYear();
			String terminfo;
			if(month<9){
				terminfo=(year-1)+"-"+year+" ����ѧ��";
			}else{
				terminfo=year+"-"+(year+1)+" �＾ѧ��";
			}
			ContentValues cv=new ContentValues(); 
	        cv.put("TermInfo", terminfo);
	        cv.put("StartDay", startDay.toString());  
	        cv.put("TotalWeek", 24);  
	        cv.put("MaxHour", 12);  
	        cv.put("ShowType", 0);  
	        cv.put("StartWeekday", 0);   
	        cv.put("Course_bg", "");
	       @SuppressWarnings("unused")
		long tt= dbHelper.insert("dbCourseSetting", cv);
		}
	    dbHelper.closeDatabase();
	}
	
	private void initClassData() {
		AllClassList = new ArrayList<ClassInfo>();
		ClassInfoDao dao =new ClassInfoDao(getActivity());
		AllClassList=dao.getClassInfoList();
	}

	private void getClassData(){
		classList= new ArrayList<ClassInfo>();
		for (ClassInfo cinfo : AllClassList) {
			if(cinfo.getWeeks().contains(showWeek)){
				classList.add(cinfo);
			}
		}
	}
	
	public void changeWeek(int week){
		courseView.setWeek(week);
		if(week==NowWeek){
		   ScheduleFragment.instance.weekinfo_tv.setText("��"+week+"��");
		}else{
		   ScheduleFragment.instance.weekinfo_tv.setText("��"+week+"��"+"(�Ǳ���)");
		}
		showWeek=week;
		getClassData();
		courseView.setClassList(classList);// ���γ���Ϣ���뵽�α���
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	  public void onActivityResult(int requestCode, int resultCode, Intent data)
		{
			super.onActivityResult(requestCode, resultCode, data);	  			
			if (requestCode == COURSE_SETTING&&data!=null)
			{
				
			
			}

    }
	  
	  @Override
	public void onStart(){
	    	super.onStart();
	    	initSetting();
	    	initClassData();
	    	getClassData();
	    	Map<String, Object> params=new HashMap<String, Object>();
			params.put("classTotal", classTotal);
			params.put("startWeekday", startWeekday);
			params.put("showType", showType);
		//	params.put("nowWeek", NowWeek);
			params.put("StartDay", startDay.toString());
			courseView.initSetting(params);
			courseView.setWeek(NowWeek);
			showWeek=NowWeek;
			courseView.setClassList(classList);// ���γ���Ϣ���뵽�α���
	    }
}
  		
     
