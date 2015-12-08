package com.eventer.app.other;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.eventer.app.R;
import com.eventer.app.db.DBManager;


public  class Fragment_AddSchedual extends Fragment implements OnClickListener{

	private Spinner eventrepeat,eventalarm;
	private TextView eventdate,eventtime,tv_add_more;
	private EditText eventtitle,eventplace,eventdetail;
	private LinearLayout ll_add_info,extra_info;
	private ImageView iv_add_more;
	public int Repeat=2,Remind=2;
	private Long id;
	private Context context;
	private boolean IsNew=true;
	public static final String RESPONSE = "response";
	public static  Fragment_AddSchedual instance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView=inflater.inflate(R.layout.fragment_add_schedual, container, false);
		context=getActivity();
		instance=this;
		initView(rootView);
		id=getActivity().getIntent().getLongExtra(Calendar_ViewSchedual.ARGUMENT_ID, -1);
		int type=getActivity().getIntent().getIntExtra(Calendar_ViewSchedual.ARGUMENT_TYPE, -1);
		if(id!=-1&&type==2){
			setData();
			IsNew=false;
		}else{
			setDefalt();
			IsNew=true;
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
		eventrepeat=(Spinner)rootView.findViewById(R.id.addevent_repeat);
		eventalarm=(Spinner)rootView.findViewById(R.id.addevent_alarm);
		ll_add_info=(LinearLayout)rootView.findViewById(R.id.ll_add_info);
		extra_info=(LinearLayout)rootView.findViewById(R.id.extra_info);
		iv_add_more=(ImageView)rootView.findViewById(R.id.iv_add_more);
		tv_add_more=(TextView)rootView.findViewById(R.id.tv_add_more);


		eventdate.setOnClickListener(this);
		eventtime.setOnClickListener(this);
		ll_add_info.setOnClickListener(this);

		SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm");
		String   time =sDateFormat.format(new   Date());
		String[] nowtime=time.split(" ");
		String date=getActivity().getIntent().getStringExtra(Calendar_ViewSchedual.ARGUMENT_DATE);
		eventdate.setText(date);
		eventtime.setText(nowtime[1]);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.eventspan,R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


		adapter = ArrayAdapter.createFromResource(getActivity(), R.array.eventrepeat,R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		eventrepeat.setAdapter(adapter);
		eventrepeat.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
				Repeat=arg2;
				arg0.setVisibility(View.VISIBLE);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		adapter = ArrayAdapter.createFromResource(getActivity(), R.array.eventalarm,R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		eventalarm.setAdapter(adapter);
	}

	public void setDefalt(){
		eventrepeat.setSelection(0);
		eventalarm.setSelection(2);
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
			if(friend!=null&&friend.trim().length() != 0){

			}
			if(_f!=null&&_f.trim().length() != 0){
				int loc=Integer.parseInt(_f);
				eventrepeat.setSelection(loc);
			}
			if(remind!=null&&remind.trim().length() != 0){
				int loc=Integer.parseInt(remind);

				eventalarm.setSelection(loc);


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
						String day;
						if(dayOfMonth>9){
							day=dayOfMonth+"";
						}else{
							day="0"+dayOfMonth;
						}
						if(month>9)
							eventdate.setText(year+"-"+(month)+"-"+day);
						else
							eventdate.setText(year+"-0"+(month)+"-"+day);
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
			case R.id.ll_add_info:
				if(extra_info.getVisibility()==View.GONE){
					extra_info.setVisibility(View.VISIBLE);
					iv_add_more.setImageResource(R.drawable.close);
					tv_add_more.setText("收起");

				}
				else{
					extra_info.setVisibility(View.GONE);
					iv_add_more.setImageResource(R.drawable.open);
					tv_add_more.setText("添加其他信息");
				}
				break;
			default:
				break;
		}
	}

	public void finish(){
		DBManager dbHelper;
		dbHelper = new DBManager(context);
		dbHelper.openDatabase();
		Log.e("1",dbHelper.isColumnExist("dbSchedule","title")+"");
		Log.e("1",dbHelper.isColumnExist("dbSchedule","cc")+""+eventalarm.getSelectedItemPosition());
		String start=eventdate.getText().toString()+ " "+eventtime.getText().toString();

		String remindtime=getRemindTime(start,eventalarm.getSelectedItemPosition());
		int status=getStatus(getTime(),start,remindtime);
		ContentValues cv=new ContentValues();
		cv.put("title", eventtitle.getText().toString());
		cv.put("place", eventplace.getText().toString());
		cv.put("detail", eventdetail.getText().toString());
		cv.put("remind", eventalarm.getSelectedItemPosition());
		cv.put("companion", "");
		cv.put("frequency", eventrepeat.getSelectedItemPosition());
		cv.put("startTime",start);
		cv.put("endTime", start);
		cv.put("status", status);
		cv.put("type",2);
		cv.put("RemindTime", remindtime);
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
			status=1;
		}else if(now.gt(finish)){
			status=0;
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
  		
     
