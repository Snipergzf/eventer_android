package com.eventer.app.other;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.db.DBManager;
import com.eventer.app.entity.ClassInfo;
import com.eventer.app.main.CourseFragment;
import com.eventer.app.util.WheelDialogShowUtil;
import com.eventer.app.util.WheelDialogTwoShowUtil;
import com.eventer.app.view.CourseView;
import com.eventer.app.view.DialogView.onWheelBtnPosClick;

public class Activity_Course_Setting extends Activity  implements OnClickListener{

	private CourseView courseView;
	private ArrayList<ClassInfo> classList;
    private TextView termInfo_tv,totalWeek_tv,NowWeek_tv,StartWeekday_tv,classTotal_tv,showType_tv;
    private DateTime startDay;
    private int NowWeek=1;
	private int startWeekday,showType,classTotal,totalWeek;
	private String termInfo;
	private WheelDialogShowUtil wheelUtil ;
	private ImageView back_img;
	private boolean IsChange=false;
	private Context context;
	SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd"); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coursetable_setting);
		initData();
		termInfo_tv=(TextView)findViewById(R.id.coursetable_term);
		totalWeek_tv=(TextView)findViewById(R.id.coursetable_totalweek);
		NowWeek_tv=(TextView)findViewById(R.id.coursetable_week);
		StartWeekday_tv=(TextView)findViewById(R.id.coursetable_weekstart);
		classTotal_tv=(TextView)findViewById(R.id.coursetable_maxhour);
		showType_tv=(TextView)findViewById(R.id.coursetable_show);
		back_img=(ImageView)findViewById(R.id.courseTable_backImg);
		back_img.setOnClickListener(this);
		termInfo_tv.setText(termInfo);
		termInfo_tv.setOnClickListener(this);
		totalWeek_tv.setText(totalWeek+"周");
		totalWeek_tv.setOnClickListener(this);
		NowWeek_tv.setText("第"+NowWeek+"周");
		NowWeek_tv.setOnClickListener(this);
		StartWeekday_tv.setText(getResources().getStringArray(R.array.weeks)[startWeekday]);
		StartWeekday_tv.setOnClickListener(this);
		classTotal_tv.setText(classTotal+"节课");
		classTotal_tv.setOnClickListener(this);
	    showType_tv.setText(getResources().getStringArray(R.array.course_showtype)[showType]);
		showType_tv.setOnClickListener(this);
		context=Activity_Course_Setting.this;
	}
	
	private void initData(){
		DBManager dbHelper;
		dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        String   time =sDateFormat.format(new   Date());
		DateTime Today=new DateTime(time);
		int weekday=Today.getWeekDay();
    	Cursor c=dbHelper.findList(true, "dbCourseSetting", null,
    			null, null, null, null,null,null);
    	boolean isNew=true;
		while (c.moveToNext()){
			 isNew=false;
			 String start=c.getString(c.getColumnIndex("StartDay"));
			 
			 startDay=new DateTime(start);
			 termInfo=c.getString(c.getColumnIndex("TermInfo"));
			 showType=c.getInt(c.getColumnIndex("ShowType"));
			 totalWeek=c.getInt(c.getColumnIndex("TotalWeek"));
			 classTotal=c.getInt(c.getColumnIndex("MaxHour"));
			 startWeekday=c.getInt(c.getColumnIndex("StartWeekday"));	
			 int diff=startDay.numDaysFrom(Today);
			 if(diff>=0){
				 NowWeek=diff/7+1;
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
				terminfo=(year-1)+"-"+year+" 春季学期";
			}else{
				terminfo=year+"-"+(year+1)+" 秋季学期";
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
		    case R.id.courseTable_backImg:
		    	Intent intent=new Intent();
		    	intent.putExtra("IsChange", IsChange);
		    	setResult(CourseFragment.COURSE_SETTING, intent);
		    	this.finish();
			    break;
			case R.id.coursetable_bg:
				break;
			case R.id.coursetable_maxhour:
				String[] totalclass=new String[20];
				for(int i=5;i<25;i++){
					totalclass[i-5]=i+"";				
				}
				
				wheelUtil= new WheelDialogShowUtil(context,getWindowManager().getDefaultDisplay(),totalclass, "设置课表最大节数");
				wheelUtil.setWheelHint(classTotal-5); 
				wheelUtil.dialogView.setBtnPosClick(new onWheelBtnPosClick() {
					@Override
					public void onClick(String text, int position) {
						// TODO Auto-generated method stub
						wheelUtil.dissmissWheel();
						wheelUtil.setTextToView(classTotal_tv, text+"节课");
						if(classTotal!=position+5){
							classTotal=Integer.parseInt(text);								
							ContentValues cv=new ContentValues();
							cv.put("MaxHour", classTotal);
							DBManager dbHelper;
							dbHelper = new DBManager(context);
					        dbHelper.openDatabase();
							dbHelper.update("dbCourseSetting", cv, null, null);
							dbHelper.closeDatabase();
						}	
					}
				});
				wheelUtil.showWheel();
				break;
			case R.id.coursetable_show:
				wheelUtil= new WheelDialogShowUtil(context,getWindowManager().getDefaultDisplay(),getResources().getStringArray(R.array.course_showtype), "设置课程显示方式");
				wheelUtil.setWheelHint(showType); 
				wheelUtil.dialogView.setBtnPosClick(new onWheelBtnPosClick() {
					@Override
					public void onClick(String text, int position) {
						// TODO Auto-generated method stub
						wheelUtil.dissmissWheel();
						wheelUtil.setTextToView(showType_tv, text);
						if(showType!=position){
							showType=position;					
							ContentValues cv=new ContentValues();
							cv.put("ShowType", showType);
							DBManager dbHelper;
							dbHelper = new DBManager(context);
					        dbHelper.openDatabase();
							dbHelper.update("dbCourseSetting", cv, null, null);
							dbHelper.closeDatabase();
						}	
					}
				});
				wheelUtil.showWheel();	
				break;
			case R.id.coursetable_term:
				String   time =sDateFormat.format(new   Date());
				DateTime Today=new DateTime(time);
				int year=Today.getYear();
				Map<Integer,String[]> data=new HashMap<Integer,String[]>();
				String[] termyear=new String[10];
				for(int i=0;i<10;i++){
					termyear[i]=(year-5+i)+"~"+(year-4+i);
				}
				String[] term=new String[]{"春季学期","秋季学期"};
				data.put(1, termyear);
				data.put(2, term);
				final WheelDialogTwoShowUtil wheelUtil1;
				wheelUtil1= new WheelDialogTwoShowUtil(context,getWindowManager().getDefaultDisplay(),data, "选择课表的学年和学期");
				int index1=1,index2=1;
				if(termInfo!=null&&termInfo.trim().length()!=0){
					String tYear=termInfo.split(" ")[0];
					String tTerm=termInfo.split(" ")[1];
					for(int i=0;i<termyear.length;i++){
						if(tYear.equals(termyear[i]))
							index1=i;
					}
					for(int i=0;i<term.length;i++){
						if(tTerm.equals(term[i]))
							index2=i;
					}
				}
				final int hint1=index1,hint2=index2;
				wheelUtil1.setWheelHint(index1,index2); 			
				wheelUtil1.dialogView.setBtnPosClick(new com.eventer.app.view.DialogView_Two.onWheelBtnPosClick() {
					@Override
					public void onClick(String text, int[] position) {
						// TODO Auto-generated method stub
						wheelUtil1.dissmissWheel();
						wheelUtil1.setTextToView(termInfo_tv, text);
						if(position[0]!=hint1||position[1]!=hint2){							
							ContentValues cv=new ContentValues();
							cv.put("TermInfo", text);
							DBManager dbHelper;
							dbHelper = new DBManager(context);
					        dbHelper.openDatabase();
							dbHelper.update("dbCourseSetting", cv, null, null);
							dbHelper.closeDatabase();
							termInfo=text;
						}	
						
					}
				});
				wheelUtil1.showWheel();	
				break;
			case R.id.coursetable_totalweek:
				String[] totalweeks=new String[26];
				for(int i=5;i<31;i++){
					totalweeks[i-5]=i+"";				
				}
				
				wheelUtil= new WheelDialogShowUtil(context,getWindowManager().getDefaultDisplay(),totalweeks, "选择本学期总周数");
				wheelUtil.setWheelHint(classTotal-5); 
				wheelUtil.dialogView.setBtnPosClick(new onWheelBtnPosClick() {
					@Override
					public void onClick(String text, int position) {
						// TODO Auto-generated method stub
						wheelUtil.dissmissWheel();
						wheelUtil.setTextToView(totalWeek_tv, text+"周");
						if(classTotal!=position+5){
							totalWeek=Integer.parseInt(text);								
							ContentValues cv=new ContentValues();
							cv.put("TotalWeek", totalWeek);
							DBManager dbHelper;
							dbHelper = new DBManager(context);
					        dbHelper.openDatabase();
							dbHelper.update("dbCourseSetting", cv, null, null);
							dbHelper.closeDatabase();
						}	
					}
				});
				wheelUtil.showWheel();	
				break;
			case R.id.coursetable_week:
				String[] weeks=new String[totalWeek];
				for(int i=0;i<totalWeek;i++){
						weeks[i]="第"+(i+1)+"周";				
				}
				wheelUtil= new WheelDialogShowUtil(context,getWindowManager().getDefaultDisplay(),weeks, "选择当前周");
				wheelUtil.setWheelHint(NowWeek-1); 
				wheelUtil.dialogView.setBtnPosClick(new onWheelBtnPosClick() {
					@Override
					public void onClick(String text, int position) {
						// TODO Auto-generated method stub
						wheelUtil.dissmissWheel();
						wheelUtil.setTextToView(NowWeek_tv, text);
						if(NowWeek!=position+1){
							NowWeek=position+1;
							String   time =sDateFormat.format(new   Date());
							DateTime Today=new DateTime(time);
							int weekday=Today.getWeekDay();
							startDay=Today.minusDays(weekday-1);
							startDay=startDay.plusDays(startWeekday);
							startDay=startDay.minusDays((NowWeek-1)*7);
							
							
							ContentValues cv=new ContentValues();
							cv.put("StartDay", startDay.toString());
							Log.e("1","cousesetting 238 Startday--"+startDay.toString());
							DBManager dbHelper;
							dbHelper = new DBManager(context);
					        dbHelper.openDatabase();
							dbHelper.update("dbCourseSetting", cv, null, null);
							dbHelper.closeDatabase();
						}	
					}
				});
				wheelUtil.showWheel();				
				break;
			case R.id.coursetable_weekstart:
				String[] names=new String[]{"周日","周一"};
				wheelUtil= new WheelDialogShowUtil(context,getWindowManager().getDefaultDisplay(),names, "选择每周起始日");
				wheelUtil.setWheelHint(startWeekday); 
				wheelUtil.dialogView.setBtnPosClick(new onWheelBtnPosClick() {
					@Override
					public void onClick(String text, int position) {
						// TODO Auto-generated method stub
						wheelUtil.dissmissWheel();
						wheelUtil.setTextToView(StartWeekday_tv, text);
						if(startWeekday!=position){
							startWeekday=position;					
							ContentValues cv=new ContentValues();
							cv.put("StartWeekday", position);
							DBManager dbHelper;
							dbHelper = new DBManager(context);
					        dbHelper.openDatabase();
							dbHelper.update("dbCourseSetting", cv, null, null);
							dbHelper.closeDatabase();
						}	
					}
				});
				wheelUtil.showWheel();				
				break;
			default:
				break;
		}
		
	}
	
	
}
