package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.eventer.app.R;
import com.eventer.app.main.MainActivity;

/**
 * 提示对话框
 * 
 * @author 作者：LiuJunGuang
 * @version 创建时间：2011-12-6 下午5:48:47
 */
public class AlarmAlert extends Activity {
	private static int messageNum = 0;
	private NotificationManager manager;
	private Bitmap icon;
	private MediaPlayer mediaPlayer;// 音乐播放器
	private Vibrator vibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		// 提示信息
		String remindMsg = bundle.getString("remindMsg");
		if (bundle.getBoolean("ring")) {
			// 播放音乐
			mediaPlayer = MediaPlayer.create(this, R.raw.tone);
			Log.e("1","ring----");
			try {
				mediaPlayer.setLooping(true);
				mediaPlayer.prepare();
			} catch (Exception e) {
				setTitle(e.getMessage());
			}
			mediaPlayer.start();// 开始播放
		}
		if (bundle.getBoolean("shake")) {
			Log.e("1","shake----");
			vibrator = (Vibrator) getApplication().getSystemService(
					Service.VIBRATOR_SERVICE);
			vibrator.vibrate(new long[] { 1000, 100, 100, 1000 }, -1);
		}
		init();
		showNormal();
		Log.e("1","提醒----");
		new AlertDialog.Builder(AlarmAlert.this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("提醒")
				.setMessage(remindMsg)
				.setPositiveButton("关 闭",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								AlarmAlert.this.finish();
								// 关闭音乐播放器
								if (mediaPlayer != null)
									mediaPlayer.stop();
								if (vibrator != null)
									vibrator.cancel();
							}
						}).show();
		

	}
	private void init() {
		// 获取通知服务
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);	
		icon = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
	}
	  @SuppressLint("NewApi")
		private void showNormal() {
//		  Log.e("1","notify----");
//			Notification notification = new Notification.Builder(Main.myListActivity)
//					.setLargeIcon(icon).setSmallIcon(R.drawable.weather)
//					.setTicker("showNormal").setContentInfo("contentInfo")
//					.setContentTitle("ContentTitle").setContentText("ContentText")
//					.setNumber(++messageNum)
//					.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
//					.build();
//			manager.notify(1, notification);
			
			Notification.Builder mBuilder = new Notification.Builder(this);
//			//PendingIntent 跳转动作
			PendingIntent pendingIntent=PendingIntent.getActivity(this, 0, getIntent(), 0);  
			mBuilder.setSmallIcon(R.drawable.ic_launcher)
					.setTicker("常驻通知来了")
					.setContentTitle("常驻测试")
					.setContentText("使用cancel()方法才可以把我去掉哦")
					.setContentIntent(pendingIntent);
			Notification mNotification = mBuilder.build();
			//设置通知  消息  图标  
			mNotification.icon = R.drawable.ic_launcher;
			//在通知栏上点击此通知后自动清除此通知
			mNotification.flags = Notification.FLAG_ONGOING_EVENT;//FLAG_ONGOING_EVENT 在顶部常驻，可以调用下面的清除方法去除  FLAG_AUTO_CANCEL  点击和清理可以去调
			//设置显示通知时的默认的发声、震动、Light效果  
			mNotification.defaults = Notification.DEFAULT_VIBRATE;
			//设置发出消息的内容
			mNotification.tickerText = "通知来了";
			//设置发出通知的时间  
			mNotification.when=System.currentTimeMillis(); 
//			mNotification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
//			mNotification.setLatestEventInfo(this, "常驻测试", "使用cancel()方法才可以把我去掉哦", null); //设置详细的信息  ,这个方法现在已经不用了 
			manager.notify(2, mNotification);
	}
}
