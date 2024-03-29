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
import android.widget.DatePicker;
import android.widget.EditText;
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


@SuppressLint("SimpleDateFormat")
public  class Fragment_AddTodo extends Fragment implements OnClickListener{


	private TextView eventdate, eventtime;
	private EditText eventtitle, eventplace, eventdetail;
	private Long id;
	private Context context;
	private boolean IsNew=true;
	public static final String RESPONSE = "response";
	public static  Fragment_AddTodo instance;
	private Schedual schedual;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView=inflater.inflate(R.layout.fragment_add_todo, container, false);
		context=getActivity();
		instance=this;
		initView(rootView);
		id = getActivity().getIntent()
				.getLongExtra(Calendar_ViewSchedual.ARGUMENT_ID, -1);
		int type = getActivity().getIntent()
				.getIntExtra(Calendar_ViewSchedual.ARGUMENT_TYPE, -1);
		if(id == -1){ //新建待办
			IsNew=true;
		}else if(type == 3){ //编辑待办
			setData();
			IsNew = false;
		}
		return rootView;
	}


	/***
	 * 初始化控件，给控件添加事件响应
	 */
	private void initView(View rootView) {
		eventdate=(TextView)rootView.findViewById(R.id.addevent_datestart);
		eventtime=(TextView)rootView.findViewById(R.id.addevent_timestart);
		eventtitle=(EditText)rootView.findViewById(R.id.addevent_title);
		eventplace=(EditText)rootView.findViewById(R.id.addevent_location);
		eventdetail=(EditText)rootView.findViewById(R.id.addevent_detail);

		eventdate.setOnClickListener(this);
		eventtime.setOnClickListener(this);


		SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm");
		String   time =sDateFormat.format(new   Date());
		String[] nowtime=time.split(" ");
		String date=getActivity().getIntent().getStringExtra(Calendar_ViewSchedual.ARGUMENT_DATE);
		eventdate.setText(date);
		eventtime.setText(nowtime[1]);

	}


	/***
	 * 编辑日程时
	 * 初始化页面数据
	 */
	public void setData(){
		SchedualDao dao = new SchedualDao(context);
		schedual = dao.getSchedual(id+"");

		if(schedual != null){
			String start = schedual.getStarttime();
			String title = schedual.getTitle();
			String place = schedual.getPlace();
			String detail = schedual.getDetail();

			String event_date = getActivity().getIntent()
					.getStringExtra(Calendar_ViewSchedual.ARGUMENT_DATE);
			if(start != null && start.trim().length() != 0){
				eventdate.setText(event_date);
				String[] time = start.split(" ");
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
	}

	/***
	 * 为页面控件添加点击事件
	 */
	@Override
	public void onClick(View v) {

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

	/***
	 * 保存待办，并退出
	 */
	public void finish(){
		DBManager dbHelper;
		dbHelper = new DBManager(context);
		dbHelper.openDatabase();

		String start=eventdate.getText().toString()+ " "+eventtime.getText().toString();

		String remindtime=getRemindTime(start,1);
		int status=getStatus(getTime(),start,remindtime);

		if(schedual == null){
			schedual = new Schedual();
		}
		schedual.setTitle(eventtitle.getText().toString());
		schedual.setPlace(eventplace.getText().toString());
		schedual.setDetail(eventdetail.getText().toString());
		schedual.setRemind(1);
		schedual.setFrequency(0);
		schedual.setFriend("");
		schedual.setStarttime(start);
		schedual.setEndtime(start);
		schedual.setStatus(status);
		schedual.setType(3);
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
		if(status>0)
			IsTodayEvent(remindtime);
	}

	public void IsTodayEvent(String remind){
		if(Constant.AlarmChange){
			return;
		}
		DateTime Remind_dt = new DateTime(remind + ":00");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		String today_str = formatter.format(new Date());
		DateTime today_dt = new DateTime(today_str + ":00");

		if (!today_dt.gt(Remind_dt)) {
			Constant.AlarmChange=true;
		}

	}

	/***
	 * 日期选择器
	 */
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

	/***
	 * 事件选择器
	 */
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


	/***
	 * 获取日程状态
	 */
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

	/***
	 * 获取提醒时间
	 */
	private String getRemindTime(String start, int span) {
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


}
  		
     
