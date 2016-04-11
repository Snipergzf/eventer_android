package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
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

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.DBManager;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.entity.Schedual;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import hirondelle.date4j.DateTime;

@SuppressLint("SetTextI18n")
public  class Fragment_AddSchedual extends Fragment implements OnClickListener{

	private Spinner eventrepeat,eventalarm;
	private TextView eventdate,eventtime,tv_add_more;
	private EditText eventtitle,eventplace,eventdetail;
	LinearLayout ll_add_info,extra_info;
	private ImageView iv_add_more;
	public int Repeat=2;
	private Long id;
	private Context context;
	private Schedual schedual;
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

		SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
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
		SchedualDao dao = new SchedualDao(context);
		schedual = dao.getSchedual(id+"");

		if(schedual != null){
			String start = schedual.getStarttime();
//			String end=c.getString(c.getColumnIndex("endTime"));
			String title = schedual.getTitle();
			String place = schedual.getPlace();
			String detail = schedual.getDetail();
			int remind = schedual.getRemind();
			int _f = schedual.getFrequency();

			String event_date=getActivity().getIntent().getStringExtra(Calendar_ViewSchedual.ARGUMENT_DATE);
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


			eventrepeat.setSelection(_f);
			eventalarm.setSelection(remind);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
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
		String start=eventdate.getText().toString()+ " "+eventtime.getText().toString();

		String remindtime=getRemindTime(start, eventalarm.getSelectedItemPosition());
		int status=getStatus(getTime(),start,remindtime);
		if(schedual == null){
			schedual = new Schedual();
		}
		schedual.setTitle(eventtitle.getText().toString());
		schedual.setPlace(eventplace.getText().toString());
		schedual.setDetail(eventdetail.getText().toString());
		schedual.setRemind(eventalarm.getSelectedItemPosition());
		schedual.setFrequency(eventrepeat.getSelectedItemPosition());
		schedual.setFriend("");
		schedual.setStarttime(start);
		schedual.setEndtime(start);
		schedual.setStatus(status);
		schedual.setType(2);
		schedual.setRemindtime(remindtime);

		if(IsNew){
			long Sid=System.currentTimeMillis()/1000;
			schedual.setSchdeual_ID(Sid);
			SchedualDao dao = new SchedualDao(context);
			dao.saveSchedualNoShare(schedual, 1);
			Intent intent1 = new Intent();
			intent1.putExtra(RESPONSE,true);
		} else{
			schedual.setShareId(null);
			Log.e("s---id",schedual.getSchdeual_ID()+"");
			SchedualDao dao = new SchedualDao(context);
			dao.saveSchedualNoShare(schedual, 1);
			Intent intent1 = new Intent();
			intent1.putExtra(RESPONSE, true);
			getActivity().setResult(Calendar_ViewSchedual.REQUEST_EDIT, intent1);
		}
		IsTodayEvent(eventrepeat.getSelectedItemPosition(),remindtime,start);
	}

	public void IsTodayEvent(int _f,String remind,String end){
		if(Constant.AlarmChange){
			return;
		}
		DateTime Remind_dt = new DateTime(remind + ":00");
		String[] remind_time = remind.split(" ");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
		String today_str = formatter.format(new Date());
		String[] today_time = today_str.split(" ");
		DateTime End_dt = new DateTime(end + ":00");
		DateTime today_dt = new DateTime(today_str + ":00");
		DateTime time_db = new DateTime(remind_time[0] + " 00:00:00");
		DateTime today_db = new DateTime(today_time[0] + " 00:00:00");
		int diff = today_db.numDaysFrom(time_db);
		int i,j;
		boolean isTodayEvent=false;
		switch (_f) {
			case 0:
				isTodayEvent = true;
				break;
			case 1:
				if (today_time[0].compareTo(remind_time[0]) >= 0)
					isTodayEvent= true;
				break;
			case 2:
				// int i=Remind_dt.getWeekDay();
				if (Remind_dt.getWeekDay() > 1 && Remind_dt.getWeekDay() < 7
						&& today_time[0].compareTo(remind_time[0]) >= 0)
					Constant.AlarmChange = true;
				break;
			case 3:
				i=today_dt.getWeekDay();
				j=Remind_dt.getWeekDay();
				if (i == j
						&& today_time[0].compareTo(remind_time[0]) >= 0)
					isTodayEvent= true;
				break;
			case 4:
				i=today_dt.getDay();
				j=Remind_dt.getDay();
				if (i == j
						&& today_time[0].compareTo(remind_time[0]) >= 0)
					isTodayEvent = true;
				break;
			case 5:
				int month1 = today_dt.getMonth();
				int day1 = today_dt.getDay();
				int month2 = Remind_dt.getMonth();
				int day2 = Remind_dt.getDay();
				if (month1 == month2 && day1 == day2
						&& today_time[0].compareTo(remind_time[0]) >= 0)
					isTodayEvent = true;
				break;
		}
		if(isTodayEvent){
			if (diff > 0) {
				End_dt = End_dt.plusDays(diff);
			}
			if (!today_dt.gt(End_dt)) {
				Constant.AlarmChange = true;
			}
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		return sdf.format(new Date());
	}

	private String getRemindTime(String start, int span) {
		// TODO Auto-generated method stub
		String rTime;
		DateTime begin=new DateTime(start+":00");
		DateTime r;
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
	@SuppressWarnings({"UnusedDeclaration"})
	private String getEndTime(String starttime, int span) {
		// TODO Auto-generated method stub
		String endTime;
		DateTime begin=new DateTime(starttime+":00");
		DateTime end;
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
  		
     
