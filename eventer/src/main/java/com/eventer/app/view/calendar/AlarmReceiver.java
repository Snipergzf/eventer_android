package com.eventer.app.view.calendar;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.DBManager;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.entity.Schedual;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.main.LoginActivity;
import com.eventer.app.main.MainActivity;
import com.eventer.app.other.Calendar_ViewSchedual;
import com.eventer.app.service.CheckInternetService;
import com.eventer.app.util.PreferenceUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hirondelle.date4j.DateTime;

/**
 *
 * @author 作者：LiuNana
 * @version 创建时间：2015-06-16 下午3:31:43
 */
@SuppressLint({ "NewApi", "UseSparseArrays", "SimpleDateFormat" })
public class AlarmReceiver extends BroadcastReceiver {
	private Context context;
	public static Map<String, Schedual> Alarmlist= new HashMap<>();
	public static List<String> notify_id_list=new ArrayList<>();
	private NotificationManager manager;
	Vibrator vibrator;
	Bitmap icon;
	public static boolean isCancel=false;


	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	@SuppressLint("SimpleDateFormat")
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("1", "calendar_clock----");
//    	String msg=intent.getStringExtra("msg");
//        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		this.context=context;
		long now=System.currentTimeMillis()/1000;
		if((now-Constant.LoginTime>3200||(!Constant.isLogin&&MainActivity.instance!=null))&&Constant.isConnectNet){
			String user=PreferenceUtils.getInstance().getLoginUser();
			String pwd=PreferenceUtils.getInstance().getLoginPwd();
			if (user!=null&& !user.equals("") &&pwd!=null&& !pwd.equals("")) {
				UserLogin();
			}else{
				Constant.isLogin=false;
			}
		}

		if(!Constant.isConnectNet){
			context.startService(new Intent(context, CheckInternetService.class));
		}


		String today=formatter.format(new Date());
        if(Constant.AlarmChange){
			setAlarmList();
			Constant.AlarmChange=false;
		}

		for (Object o : Alarmlist.entrySet()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) o;
			String key = (String) entry.getKey();
			Schedual schedual = (Schedual) entry.getValue();
			if (!notify_id_list.contains(key)) {
				String end = schedual.getEndtime();
				if (today.compareTo(end) <= 0) {
					String remind = schedual.getRemindtime();
					if (today.compareTo(remind) >= 0) {
						setAlarm(schedual);
					}
				}
			}

		}

	}


	public void showAlertDialog(Schedual s, final int ID) {
		final Schedual schedual=s;
		AlarmDialog.Builder builder = new AlarmDialog.Builder(context);

		//builder.setContentInfo(map);
		builder.setSchedual(schedual);
		//builder.setMessage("这个就是自定义的提示框");
		builder.setTitle("日程提示");
		builder.setOnDetailListen(new AlarmDialog.OnDetailListener() {
			@Override
			public void onDetail() {
				SimpleDateFormat  DateFormat  = new   SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				final String now= DateFormat.format(new Date());
				SchedualDao sDao = new SchedualDao(context);
				schedual.setStatus(0);
				sDao.update(schedual);
				AlarmReceiver.isCancel = true;
				Constant.AlarmChange = true;
				Intent intent = new Intent();
				intent.setClass(context, Calendar_ViewSchedual.class);
				Bundle bundle = new Bundle(); // 创建Bundle对象
				bundle.putString(Calendar_ViewSchedual.ARGUMENT_ID, schedual.getSchdeual_ID()+""); // 装入数据
				bundle.putString(Calendar_ViewSchedual.ARGUMENT_DATE, now);
				bundle.putInt(Calendar_ViewSchedual.ARGUMENT_LOC, -1);
				intent.putExtras(bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
				context.startActivity(intent);
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
								int whichButton) {
				try {
					isCancel=true;
					SchedualDao sDao=new SchedualDao(context);
					schedual.setStatus(0);
					sDao.update(schedual);
					Constant.AlarmChange=true;
					dialog.dismiss();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}});

		builder.setNegativeButton("稍后提醒",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
								int whichButton) {
				try {
					dialog.dismiss();
					String sid=schedual.getSchdeual_ID()+"";
					if(MainActivity.Alarmlist.containsKey(sid)){
						Schedual schedual1=MainActivity.Alarmlist.get(sid);
						MainActivity.Alarmlist.remove(sid);
						Schedual schedual2;
						String now=formatter.format(new Date());
						DateTime begin=new DateTime(now+":00");
						DateTime remind=begin.plus(0, 0, 0, 0, 6, 0, 0, null);
						schedual1.setRemindtime(remind.toString().substring(0, 16));
						schedual2=schedual1;
						MainActivity.Alarmlist.put(sid, schedual2);
					}

					Alarmlist=MainActivity.Alarmlist;
					if(notify_id_list.contains(ID+""))
						notify_id_list.remove(ID+"");
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}});
			AlarmDialog dia = builder.create();
			dia.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dia.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					if(isCancel){
						manager.cancel(ID);
						if(notify_id_list.contains(ID+""))
							notify_id_list.remove(ID+"");
					}
				}
			});
			dia.show();
	}
	protected void setAlarm(Schedual schedual) {
		int notify_id=(int)schedual.getSchdeual_ID();
		if(!notify_id_list.contains(notify_id+"")){
			//判断手机状态 是否播放音乐，是否振动
			Log.e("1","shake----");
			ShakeThread thread_shake=new ShakeThread();//创建新的Runnable，
			Thread thread=new Thread(thread_shake);//利用Runnable对象生成Thread
			thread.start();
			setNotify(schedual);
		}
	}

	public void setNotify(Schedual s) {
		final Schedual schedual;
		schedual = s;
		// 获取通知服务
		manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		icon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);
		Notification.Builder mBuilder = new Notification.Builder(context);
//					//PendingIntent 跳转动作
		Intent intent=new Intent(context,Calendar_ViewSchedual.class);
		Bundle bundle = new Bundle();                           //创建Bundle对象
		String date_str=s.getStarttime().substring(0,10);
		bundle.putString(Calendar_ViewSchedual.ARGUMENT_ID, s.getSchdeual_ID()+"");     //装入数据
		bundle.putString(Calendar_ViewSchedual.ARGUMENT_DATE, date_str);
		bundle.putInt(Calendar_ViewSchedual.ARGUMENT_LOC, -1);
		intent.putExtras(bundle);
		String title=s.getTitle();
		if (title == null || title.trim().length() == 0) {
			title="(无标题)";
		}
		SimpleDateFormat  sDateFormat  = new   SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat  DateFormat  = new   SimpleDateFormat("yyyy-MM-dd");
		String date=sDateFormat.format(new Date());
		String today_str=DateFormat.format(new Date());
		String start=schedual.getStarttime();
		String end=schedual.getEndtime();
		String time="";
		if(end.substring(0,10).equals(start.substring(0,10))){
			time=date+" "+end.substring(11);
		}else{
			DateTime end_dt=new DateTime(end.substring(0,10)+" 00:00:00");
			DateTime start_dt=new DateTime(start.substring(0,10)+" 00:00:00");
			DateTime today_dt=new DateTime(today_str+" 00:00:00");
			int diff=today_dt.numDaysFrom(end_dt);
			if(diff>0){
				end_dt.plusDays(diff);
				start_dt.plusDays(diff);
				time=start_dt.getYear()+"年"+(start_dt.getMonth()+1)+"月"+start_dt.getDay()+"日 ";
				time+=end_dt.getYear()+"年"+(end_dt.getMonth()+1)+"月"+end_dt.getDay()+"日";
			}

		}
		PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setSmallIcon(R.drawable.ic_launcher)
				.setTicker("日程提醒")
				.setContentTitle(title)
				.setContentText(time)
				.setContentIntent(pendingIntent)
		;
		final Notification mNotification = mBuilder.build();
		//设置通知  消息  图标
		mNotification.icon = R.drawable.ic_launcher;
		//在通知栏上点击此通知后自动清除此通知
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;//FLAG_ONGOING_EVENT 在顶部常驻，可以调用下面的清除方法去除  FLAG_AUTO_CANCEL  点击和清理可以去调
		//设置显示通知时的默认的发声、震动、Light效果
		//mNotification.defaults = Notification.DEFAULT_VIBRATE;
		mNotification.defaults= Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS;
		//设置发出消息的内容
		mNotification.tickerText = "日程提醒";
		//设置发出通知的时间
		mNotification.when=System.currentTimeMillis();
//					mNotification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
//					mNotification.setLatestEventInfo(this, "常驻测试", "使用cancel()方法才可以把我去掉哦", null); //设置详细的信息  ,这个方法现在已经不用了 
		int notify_id=(int)s.getSchdeual_ID();
		if(!notify_id_list.contains(notify_id+"")){
			notify_id_list.add(notify_id+"");
			manager.notify(notify_id, mNotification);
			showAlertDialog(schedual,notify_id);
		}

	}


	class ShakeThread implements Runnable{
		public void run() {
			vibrator = (Vibrator) context.getSystemService(
					Service.VIBRATOR_SERVICE);
			vibrator.vibrate(new long[] { 1000, 100, 100, 1000 }, -1);
		}
	}



	/**
	 * 执行异步任务
	 * 登录系统
	 *  参数为“phone”,“pwd”  ,"imei"
	 */
	public void UserLogin() {
		final String user=PreferenceUtils.getInstance().getLoginUser();
		final String pwd=PreferenceUtils.getInstance().getLoginPwd();
		Map<String, String> params = new HashMap<>();
		params.put("pwd", pwd);
		params.put("phone", user);
		params.put("imei", PreferenceUtils.getInstance().getDeviceId());
		LoadDataFromHTTP task = new LoadDataFromHTTP(
				context, Constant.URL_LOGIN_NEW, params);
		task.getData(new DataCallBack() {

			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					int code = data.getInteger("status");
					switch (code) {
						case 0:
							Log.e("1", "登录成功！");
							PreferenceUtils.getInstance().setLoginUser(user);
							PreferenceUtils.getInstance().setLoginPwd(pwd);
							Constant.isLogin=true;
							Constant.LoginTime=System.currentTimeMillis()/1000;
							JSONObject jsonLogin= data.getJSONObject("user_action");
							Constant.UID=jsonLogin.getInteger("uid")+"";
							Constant.TOKEN=jsonLogin.getString("token");
							break;
						case 1:
						case 2:
						case 23:
							break;
						default:
							if(!LoginActivity.isActive&&Constant.isConnectNet)
								context.startActivity(new Intent().setClass(context, LoginActivity.class));
							break;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 初始化日程提醒的数据
	 */
	public void setAlarmList() {
		Alarmlist = new HashMap<>();
		new SchedualDao(context);
		DBManager dbHelper;
		dbHelper = new DBManager(context);

		dbHelper.openDatabase();
		String today = formatter.format(new Date());
		String[] today_time = today.split(" ");
		DateTime today_dt = new DateTime(today + ":00");
		Cursor c = dbHelper.findList(true, "dbSchedule", new String[] {"title",
						"scheduleID", "status", "endTime", "startTime", "remindTime",
						"frequency" }, "status>?", new String[] { "0" }, null, null,
				"remindTime", null);
		while (c.moveToNext()) {
			int _f = c.getInt(c.getColumnIndex("frequency"));
			long id = c.getLong(c.getColumnIndex("scheduleID"));
			int status = c.getInt(c.getColumnIndex("status"));
			String EndTime = c.getString(c.getColumnIndex("endTime"));
			String StartTime = c.getString(c.getColumnIndex("startTime"));
			String title=c.getString(c.getColumnIndex("title"));
			// String[] end_time=EndTime.split(" ");

			DateTime End_dt = new DateTime(EndTime + ":00");
			DateTime Satrt_dt = new DateTime(StartTime + ":00");
			String RemindTime = c.getString(c.getColumnIndex("remindTime"));
			String[] remind_time = RemindTime.split(" ");
			DateTime Remind_dt = new DateTime(RemindTime + ":00");
			DateTime time_db = new DateTime(remind_time[0] + " 00:00:00");
			DateTime today_db = new DateTime(today_time[0] + " 00:00:00");
			int diff = today_db.numDaysFrom(time_db);

			Schedual s = new Schedual();
			s.setTitle(title);
			boolean IsTodayEvent = false;
			int i,j;
			switch (_f) {
				case 0:
					IsTodayEvent = true;
					break;
				case 1:
					if (today_time[0].compareTo(remind_time[0]) >= 0) {IsTodayEvent = true;
					}
					break;
				case 2:
					// int i=Remind_dt.getWeekDay();
					if (Remind_dt.getWeekDay() > 1 && Remind_dt.getWeekDay() < 7
							&& today_time[0].compareTo(remind_time[0]) >= 0) {
						IsTodayEvent = true;
					}
					break;
				case 3:
					i=today_dt.getWeekDay();
					j=Remind_dt.getWeekDay();
					if (i == j
							&& today_time[0].compareTo(remind_time[0]) >= 0) {
						IsTodayEvent = true;
					}
					break;
				case 4:
					i=today_dt.getDay();
					j=Remind_dt.getDay();
					if (i == j
							&& today_time[0].compareTo(remind_time[0]) >= 0) {
						IsTodayEvent = true;
					}
					break;
				case 5:
					int month1 = today_dt.getMonth();
					int day1 = today_dt.getDay();
					int month2 = Remind_dt.getMonth();
					int day2 = Remind_dt.getDay();
					if (month1 == month2 && day1 == day2
							&& today_time[0].compareTo(remind_time[0]) >= 0) {
						IsTodayEvent = true;
					}
					break;
			}
			// 如果该日程是今日的日程
			if (IsTodayEvent) {
				if (diff > 0) {
					Remind_dt = Remind_dt.plusDays(diff);
					End_dt = End_dt.plusDays(diff);
					Satrt_dt = Satrt_dt.plusDays(diff);
				}

				if (!today_dt.gt(End_dt)) {
					if (!Alarmlist.containsKey(id + "")) {
						s.setSchdeual_ID(id);
						s.setEndtime(End_dt.toString().substring(0, 16));
						s.setRemindtime(Remind_dt.toString().substring(0, 16));
						s.setStarttime(Satrt_dt.toString().substring(0, 16));
						s.setStatus(status);
						Alarmlist.put(id + "", s);
					}
				}
			}
		}
		dbHelper.closeDatabase();
		Constant.AlarmChange=false;
	}

}  
