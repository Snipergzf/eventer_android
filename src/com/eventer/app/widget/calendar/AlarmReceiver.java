package com.eventer.app.widget.calendar;


import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.entity.Schedual;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.main.LoginActivity;
import com.eventer.app.main.MainActivity;
import com.eventer.app.other.Calendar_ViewSchedual;
import com.eventer.app.util.PreferenceUtils;

/**
 * 
 * @author ���ߣ�LiuNana
 * @version ����ʱ�䣺2015-06-16 ����3:31:43
 */
@SuppressLint({ "NewApi", "UseSparseArrays", "SimpleDateFormat" })
public class AlarmReceiver extends BroadcastReceiver { 
	private Context context;
	private Map<String, Schedual> Alarmlist= new HashMap<String, Schedual>();
	public static List<String> notify_id_list=new ArrayList<String>();
	private NotificationManager manager;
	private MediaPlayer mediaPlayer;// ���ֲ�����
	private Vibrator vibrator;
	private Bitmap icon;
	public static boolean isCancel=false;
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    @SuppressLint("SimpleDateFormat")
	@Override  
    public void onReceive(Context context, Intent intent) {  
    	//Log.e("1","clock----");
//    	String msg=intent.getStringExtra("msg");
//        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        this.context=context;
        
        long now=System.currentTimeMillis()/1000;
        if(now-Constant.LoginTime>3200){
        	String user=PreferenceUtils.getInstance().getLoginUser();
			String pwd=PreferenceUtils.getInstance().getLoginPwd();
			if (user!=null&&user!=""&&pwd!=null&&pwd!="") {
				UserLogin();
			}else{
				Constant.isLogin=false;
			}
        }
 
        String today=formatter.format(new Date());
        if (true) {
        	Alarmlist=MainActivity.Alarmlist;
        	Iterator iter = Alarmlist.entrySet().iterator();
        			
			while (iter.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String)entry.getKey();				
				Schedual schedual = (Schedual)entry.getValue();
				if(!notify_id_list.contains(key)){
					String end=schedual.getEndtime();
					if(today.compareTo(end)<=0){
						String remind=schedual.getRemindtime();
						if(today.compareTo(remind)>=0){
							setAlarm(schedual);
						}
					}
				}
        	
			}
			
		}
      
    }  
    
   
    public void showAlertDialog(Schedual s,int ID) {
        final Schedual schedual=s;
        final int id=ID;
		AlarmDialog.Builder builder = new AlarmDialog.Builder(context);
		
		//builder.setContentInfo(map);
		builder.setSchedual(schedual);
		//builder.setMessage("��������Զ������ʾ��");
		builder.setTitle("�ճ���ʾ");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
			int whichButton) {
		try {
			manager.cancel(id);
			SchedualDao sDao=new SchedualDao(context);
			schedual.setStatus(0);
			sDao.update(schedual);
			MainActivity.instance.setAlarmList();					
			dialog.dismiss();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }});

		builder.setNegativeButton("�Ժ�����",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
			int whichButton) {
		try {				
			dialog.dismiss();
			String sid=schedual.getSchdeual_ID()+"";
			if(MainActivity.Alarmlist.containsKey(sid)){
				Schedual schedual1=MainActivity.Alarmlist.get(sid);
			    MainActivity.Alarmlist.remove(sid);
			    Schedual schedual2=new Schedual();
			    String now=formatter.format(new Date());
				DateTime begin=new DateTime(now+":00");
				DateTime remind=begin.plus(0, 0, 0, 0, 6, 0, 0, null);
				schedual1.setRemindtime(remind.toString().substring(0, 16));
				schedual2=schedual1;
//			    schedual2.setSchdeual_ID(schedual1.getSchdeual_ID());
//				schedual2.setEndtime(schedual1.getEndtime());
//				schedual2.setRemindtime(remind.toString().substring(0, 16));
//				schedual2.setStatus(schedual1.getStatus());
//				schedual2.setStarttime(schedual1.getStarttime());
				MainActivity.Alarmlist.put(sid, schedual2);
			}

			Alarmlist=MainActivity.Alarmlist;
			if(notify_id_list.contains(id+""))
			notify_id_list.remove(id+"");
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
				  manager.cancel(id);
				  if(notify_id_list.contains(id+""))
				     notify_id_list.remove(id+"");
				}
			}
		});
		dia.show();
		
	}
	protected void setAlarm(Schedual schedual) {
		int notify_id=(int)schedual.getSchdeual_ID();			
		if(!notify_id_list.contains(notify_id)){
		    //�ж��ֻ�״̬ �Ƿ񲥷����֣��Ƿ���
				if (true) {			
					MediaThread thread_media=new MediaThread();//�����µ�Runnable��	
					Thread thread=new Thread(thread_media);//����Runnable��������Thread
					thread.start();
				}
				if (true) {
					Log.e("1","shake----");
					ShakeThread thread_shake=new ShakeThread();//�����µ�Runnable��	
					Thread thread=new Thread(thread_shake);//����Runnable��������Thread
					thread.start();
					
				}
		        setNotify(schedual);
				Log.e("1","����----");	
		}
	} 

	   public void setNotify(Schedual s) {
		    final Schedual schedual=s;
				 // ��ȡ֪ͨ����
		    manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);	
			icon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);	
			Notification.Builder mBuilder = new Notification.Builder(context);
//					//PendingIntent ��ת����
			Intent intent=new Intent(context,Calendar_ViewSchedual.class);
			 Bundle bundle = new Bundle();                           //����Bundle����   
			 String date_str=s.getStarttime().substring(0,10);
			 bundle.putString(Calendar_ViewSchedual.ARGUMENT_ID, s.getSchdeual_ID()+"");     //װ������   
			 bundle.putString(Calendar_ViewSchedual.ARGUMENT_DATE, date_str);
			 bundle.putInt(Calendar_ViewSchedual.ARGUMENT_LOC, -1);
			 intent.putExtras(bundle); 
			 String title=s.getTitle();
			 if(title!=null&&title.trim().length()!= 0){       	     
        	}else{
        		title="(�ޱ���)";
        	}
			SimpleDateFormat  sDateFormat  = new   SimpleDateFormat("yyyy��MM��dd��"); 
			SimpleDateFormat  DateFormat  = new   SimpleDateFormat("yyyy-MM-dd"); 
			String date=sDateFormat.format(new Date());
			String today_str=DateFormat.format(new Date());
			String start=schedual.getStarttime();
			String end=schedual.getEndtime();
			String time="";
			if(end.substring(0,10).equals(start.substring(0,10))){
				time=date+" "+start.substring(11)+"-"+end.substring(11);
			}else{				
				DateTime end_dt=new DateTime(end.substring(0,10)+" 00:00:00");
				DateTime start_dt=new DateTime(start.substring(0,10)+" 00:00:00");
				DateTime today_dt=new DateTime(today_str+" 00:00:00");
				int diff=today_dt.numDaysFrom(end_dt);
				if(diff>0){
					end_dt.plusDays(diff);
					start_dt.plusDays(diff);
					time=start_dt.getYear()+"��"+(start_dt.getMonth()+1)+"��"+start_dt.getDay()+"��";
				    time+=end_dt.getYear()+"��"+(end_dt.getMonth()+1)+"��"+end_dt.getDay()+"��";
				}
				
			}			
			PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);  
			mBuilder.setSmallIcon(R.drawable.ic_launcher)
					.setTicker("�ճ�����")
					.setContentTitle(title)
					.setContentText(time)
					.setContentIntent(pendingIntent)
					;
			final Notification mNotification = mBuilder.build();
			//����֪ͨ  ��Ϣ  ͼ��  
			mNotification.icon = R.drawable.ic_launcher;
			//��֪ͨ���ϵ����֪ͨ���Զ������֪ͨ
			mNotification.flags = Notification.FLAG_AUTO_CANCEL;//FLAG_ONGOING_EVENT �ڶ�����פ�����Ե���������������ȥ��  FLAG_AUTO_CANCEL  ������������ȥ��
			//������ʾ֪ͨʱ��Ĭ�ϵķ������𶯡�LightЧ��  
			//mNotification.defaults = Notification.DEFAULT_VIBRATE;
			mNotification.defaults= Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS;
			//���÷�����Ϣ������
			mNotification.tickerText = "�ճ�����";
			//���÷���֪ͨ��ʱ��  
			mNotification.when=System.currentTimeMillis(); 
//					mNotification.flags = Notification.FLAG_AUTO_CANCEL; //��֪ͨ���ϵ����֪ͨ���Զ������֪ͨ
//					mNotification.setLatestEventInfo(this, "��פ����", "ʹ��cancel()�����ſ��԰���ȥ��Ŷ", null); //������ϸ����Ϣ  ,������������Ѿ������� 
			int notify_id=(int)s.getSchdeual_ID();			
			if(!notify_id_list.contains(notify_id+"")){
				notify_id_list.add(notify_id+"");
				manager.notify(notify_id, mNotification);
			    showAlertDialog(schedual,notify_id);
			}
			
			
//		   AlertDialog.Builder builder = new AlertDialog.Builder(context);
//				builder.setTitle("�ճ�����");
//				builder.setMessage(schedual.getTitle());
//				builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog,
//					int whichButton) {
//				try {
//					manager.cancel(id);
//					SchedualDao sDao=new SchedualDao(context);
//					schedual.setStatus(0);
//					sDao.update(schedual);
//					MainActivity.instance.setAlarmList();					
//					this.finalize();
//				} catch (Throwable e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			   }});
//				builder.setNegativeButton("�Ժ�����",new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog,
//					int whichButton) {
//				try {
//					
//					this.finalize();
//				} catch (Throwable e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			   }});
//				AlertDialog dia = builder.create();
//				dia.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//				dia.show();
  }
	  
	  class MediaThread implements Runnable{
			public void run() {
				mediaPlayer = MediaPlayer.create(context, R.raw.tone);
					Log.e("1","ring----");
					try {
						//MainActivity.mediaPlayer.setLooping(true);
						mediaPlayer.prepare();
					} catch (Exception e) {
						
					}
					mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
						
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							mp.stop();
							mp.release();
						}
					});
					mediaPlayer.start();// ��ʼ����		
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
		 * ִ���첽����
		 * ��¼ϵͳ
		 *  ����Ϊ��phone��,��pwd��  ,"imei"  
		 */
		public void UserLogin() {
			final String user=PreferenceUtils.getInstance().getLoginUser();
			final String pwd=PreferenceUtils.getInstance().getLoginPwd();
			Map<String, String> params = new HashMap<String, String>();  
	        params.put("pwd", pwd);
	        params.put("phone", user);
	        params.put("imei", PreferenceUtils.getInstance().getDeviceId());
	        LoadDataFromHTTP task = new LoadDataFromHTTP(
	                context, Constant.URL_LOGIN, params);
	        task.getData(new DataCallBack() {
				
				@Override
				public void onDataCallBack(JSONObject data) {
					// TODO Auto-generated method stub
					 try {
		                    int code = data.getInteger("status");
		                    switch (code) {
							case 0:
								Log.e("1", "��¼�ɹ���");
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
								context.startActivity(new Intent().setClass(context, LoginActivity.class));
								break;
							default:
								context.startActivity(new Intent().setClass(context, LoginActivity.class));
								break;
							}

		                } catch (JSONException e) {

		                    Toast.makeText(context, "���ݽ�������...",
		                            Toast.LENGTH_SHORT).show();
		                    e.printStackTrace();
		                    context.startActivity(new Intent().setClass(context, LoginActivity.class));
		                }
				}
			});
		}	
    
}  
