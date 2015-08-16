package com.eventer.app.other;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.adapter.CourseAdapter;
import com.eventer.app.db.CourseDao;
import com.eventer.app.db.DBManager;
import com.eventer.app.entity.Course;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.main.LoginActivity;
import com.eventer.app.main.MainActivity;
import com.eventer.app.other.Fragment_AddSchedual.MyDatePickerDialog;
import com.eventer.app.other.Fragment_AddSchedual.MyTimePickerDialog;
import com.eventer.app.util.PreferenceUtils;


public  class Fragment_AddTodo extends Fragment implements OnClickListener{

	
	private TextView eventdate,eventtime,tv_add_more;
	private EditText eventtitle,eventplace,eventdetail;	
	private ImageView iv_add_more;
	public int Repeat=2,Remind=2;
	private Long id;
	private Context context;
	private boolean IsNew=true;
	public static final String RESPONSE = "response";
	public static  Fragment_AddTodo instance;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView=inflater.inflate(R.layout.fragment_add_todo, container, false);
		context=getActivity();
		instance=this;
		initView(rootView);
		id=getActivity().getIntent().getLongExtra(Calendar_ViewSchedual.ARGUMENT_ID, -1);
		if(id==-1){
			IsNew=true;
        }else{
        	setData();
        	IsNew=false;
        }
		return rootView;
	}
	
	

	private void initView(View rootView) {
		// TODO Auto-generated method stub
		eventdate=(TextView)rootView.findViewById(R.id.addevent_datestart);
		eventtime=(TextView)rootView.findViewById(R.id.addevent_timestart);
		eventtitle=(EditText)rootView.findViewById(R.id.addevent_title);
		eventplace=(EditText)rootView.findViewById(R.id.addevent_location);
		eventdetail=(EditText)rootView.findViewById(R.id.addevent_detail);
		
		iv_add_more=(ImageView)rootView.findViewById(R.id.iv_add_more);
		tv_add_more=(TextView)rootView.findViewById(R.id.tv_add_more);
		
		 
		eventdate.setOnClickListener(this);
		eventtime.setOnClickListener(this);		
		
		
		SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm");     
		String   time =sDateFormat.format(new   Date());
		String[] nowtime=time.split(" ");	
		String date=getActivity().getIntent().getStringExtra(Calendar_AddSchedual.ARGUMENT);
		eventdate.setText(date);
		eventtime.setText(nowtime[1]);		
		
	}
	
   
   
   public void setData(){
		DBManager dbHelper;
		dbHelper = new DBManager(context);
       dbHelper.openDatabase();	 
   	Cursor c=dbHelper.findList(true, "dbSchedule", null,
   			"ScheduleID=?", new String[]{id+""}, null, null,null,null);
       while (c.moveToNext()) {
       	String start=c.getString(c.getColumnIndex("startTime"));
       	String end=c.getString(c.getColumnIndex("endTime"));
       	String title=c.getString(c.getColumnIndex("title"));
       	String place=c.getString(c.getColumnIndex("place"));
       	String detail =c.getString(c.getColumnIndex("detail"));
       	String remind=c.getString(c.getColumnIndex("remind"));
       	String _f=c.getString(c.getColumnIndex("frequency"));
	        String friend= c.getString(c.getColumnIndex("companion"));
	        String event_date=getActivity().getIntent().getStringExtra(Calendar_ViewSchedual.ARGUMENT_DATE);
	       //Log.e("1",id+"");
	       if(start!=null&&start.trim().length() != 0){
	    	  eventdate.setText(event_date);
	    	  String[] time=start.split(" ");
	    	  if(time.length>0){
	    		 eventdate.setText(time[0]);
	    	     eventtime.setText(time[1]);
	    	  }
	    	   
     	     eventtitle.setText(title);
       	}
           if(title!=null&&title.trim().length() != 0){
       	     eventtitle.setText(title);
       	}
	        if(place!=null&&place.trim().length() != 0){
		        eventplace.setText(place);
	        }
	        if(detail!=null&&detail.trim().length() != 0){
		         eventdetail.setText(detail);
	        }

	       
       }  
       dbHelper.closeDatabase();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.addevent_datestart: 			
		    String date_str=eventdate.getText().toString();
		    String[] date= date_str.split("-");
		    new MyDatePickerDialog(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,new OnDateSetListener() {
				
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					// TODO Auto-generated method stub
					int month=monthOfYear+1;
					if(month>9)
						eventdate.setText(year+"-"+(month)+"-"+dayOfMonth);
					else
						eventdate.setText(year+"-0"+(month)+"-"+dayOfMonth);					
				}
			},Integer.parseInt(date[0]), Integer.parseInt(date[1])-1, Integer.parseInt(date[2])).show();
			break;
		
		case R.id.addevent_timestart:
			String time_str=eventtime.getText().toString();
		    String[] time= time_str.split(":");
             new MyTimePickerDialog(context,  AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new OnTimeSetListener() {
 				
 				@Override
 				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
 					// TODO Auto-generated method stub
 					if(hourOfDay>9){
 						if(minute<10){
 	 						eventtime.setText(hourOfDay+":0"+minute);
 	 					}else{
 	 						eventtime.setText(hourOfDay+":"+minute);
 	 					}
 					}
 					else{
 						if(minute<10){
 	 						eventtime.setText("0"+hourOfDay+":0"+minute);
 	 					}else{
 	 						eventtime.setText("0"+hourOfDay+":"+minute);
 	 					}
 					}	
 				}
 			}, Integer.parseInt(time[0]), Integer.parseInt(time[1]), true).show();
			break;
		
		default:
			break;
		}
	}
	
	public void finish(){
		DBManager dbHelper;
		dbHelper = new DBManager(context);
        dbHelper.openDatabase();

    	String start=eventdate.getText().toString()+ " "+eventtime.getText().toString();
    	
    	String remindtime=getRemindTime(start,1);
    	int status=getStatus(getTime(),start,remindtime);
		ContentValues cv=new ContentValues();            
        cv.put("title", eventtitle.getText().toString());
        cv.put("Place", eventplace.getText().toString());  
        cv.put("Detail", eventdetail.getText().toString());   
        cv.put("Companion", "");  
        cv.put("frequency", 0);  
        cv.put("StartTime",getTime());  
        cv.put("EndTime", start);
        cv.put("Status", 3);
//        if(status!=0){
//        	cv.put("Status", 3);
//        }
        cv.put("RemindTime", remindtime);
        cv.put("Remind", 1);
        if(IsNew){
          long Sid=System.currentTimeMillis()/1000;
          cv.put("ScheduleID", Sid);
          dbHelper.insert("dbSchedule", cv);
          dbHelper.closeDatabase();  
          Log.e("1","22222222222222222222222222222");
          Intent intent1 = new Intent();
          intent1.putExtra(RESPONSE,true);
//          this.setResult(HomeFragment_1.REQUEST_DETAIL, intent1);
        } else{
        	dbHelper.update("DbSchedule", cv, "ScheduleID=?", new String[]{id+""});
        	dbHelper.closeDatabase(); 
        	Intent intent1 = new Intent();
	        intent1.putExtra(RESPONSE,true);
	        Log.e("1",remindtime+ "-"+status);
	        getActivity().setResult(Calendar_ViewSchedual.REQUEST_EDIT, intent1);
        }   
	}
	
	class MyDatePickerDialog extends DatePickerDialog {

		public MyDatePickerDialog(Context context, int theme,  OnDateSetListener callBack,
				int year, int monthOfYear, int dayOfMonth) {
			super(context,theme, callBack, year, monthOfYear, dayOfMonth);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}

		@Override
		protected void onStop() {
			super.onStop();
		}
	}
	
	class MyTimePickerDialog extends TimePickerDialog {

		public MyTimePickerDialog(Context context, int theme,  OnTimeSetListener callBack,
				int hourOfDay, int minute, boolean is24HourView) {
			super(context,theme, callBack, hourOfDay, minute, is24HourView);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}

		@Override
		protected void onStop() {
			super.onStop();
		}
	}

	private int getStatus(String nowtime, String end, String remindtime) {
		// TODO Auto-generated method stub
		int status=0;
		DateTime now=new DateTime(nowtime+":00");
		DateTime finish=new DateTime(end+":00");
		DateTime remind=new DateTime(remindtime+":00");
		if(now.gteq(remind)&&now.lteq(finish)){
			status=1;
		}else if(now.lt(remind)){
			status=0;
		}else if(now.gt(finish)){
			status=2;
		}
		return status;
	}
	
	public String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = sdf.format(new Date());
		return date;
	}

	private String getRemindTime(String start, int span) {
		// TODO Auto-generated method stub
		String rTime=null;
		DateTime begin=new DateTime(start+":00");
		DateTime r=null;
		switch(span){
			case 1:
				r=begin.plus(0, 0, 0, 0, 0, 0, 0, null);
			    break;
			case 2:
				r=begin.minus(0, 0, 0, 0, 10, 0, 0, null);
			    break;
			case 3:
				r=begin.minus(0, 0, 0, 0, 30, 0, 0, null);
			    break;
			case 4:
				r=begin.minus(0, 0, 0, 1, 0, 0, 0, null);
			    break;
			case 5:
				r=begin.minus(0, 0, 1, 0, 0, 0, 0, null);
			    break;
			default:
				r=begin.minus(0, 0, 0, 0, 0, 0, 0, null);
			    break;	
		}
		rTime=r.toString().substring(0, 16);
		return rTime;
	}

	@SuppressLint("SimpleDateFormat")
	private String getEndTime(String starttime, int span) {
		// TODO Auto-generated method stub
		String endTime=null;
		DateTime begin=new DateTime(starttime+":00");
		DateTime end=null;
		switch(span){
			case 0:
				end=begin.plus(0, 0, 0, 0, 0, 0, 0, null);
			    break;
			case 1:
				end=begin.plus(0, 0, 0, 0, 30, 0, 0, null);
			    break;
			case 2:
				end=begin.plus(0, 0, 0, 1, 0, 0, 0, null);
			    break;
			case 3:
				end=begin.plus(0, 0, 0, 2, 0, 0, 0, null);
			    break;
			case 4:
				end=begin.plus(0, 0, 1, 0, 0, 0, 0, null);
			    break;
			default:
				end=begin.plus(0, 0, 0, 0, 0, 0, 0, null);
			    break;	
		}
		endTime=end.toString().substring(0, 16);
		return endTime;
	}
	
}
  		
     
