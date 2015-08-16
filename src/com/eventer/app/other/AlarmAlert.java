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
 * ��ʾ�Ի���
 * 
 * @author ���ߣ�LiuJunGuang
 * @version ����ʱ�䣺2011-12-6 ����5:48:47
 */
public class AlarmAlert extends Activity {
	private static int messageNum = 0;
	private NotificationManager manager;
	private Bitmap icon;
	private MediaPlayer mediaPlayer;// ���ֲ�����
	private Vibrator vibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		// ��ʾ��Ϣ
		String remindMsg = bundle.getString("remindMsg");
		if (bundle.getBoolean("ring")) {
			// ��������
			mediaPlayer = MediaPlayer.create(this, R.raw.tone);
			Log.e("1","ring----");
			try {
				mediaPlayer.setLooping(true);
				mediaPlayer.prepare();
			} catch (Exception e) {
				setTitle(e.getMessage());
			}
			mediaPlayer.start();// ��ʼ����
		}
		if (bundle.getBoolean("shake")) {
			Log.e("1","shake----");
			vibrator = (Vibrator) getApplication().getSystemService(
					Service.VIBRATOR_SERVICE);
			vibrator.vibrate(new long[] { 1000, 100, 100, 1000 }, -1);
		}
		init();
		showNormal();
		Log.e("1","����----");
		new AlertDialog.Builder(AlarmAlert.this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("����")
				.setMessage(remindMsg)
				.setPositiveButton("�� ��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								AlarmAlert.this.finish();
								// �ر����ֲ�����
								if (mediaPlayer != null)
									mediaPlayer.stop();
								if (vibrator != null)
									vibrator.cancel();
							}
						}).show();
		

	}
	private void init() {
		// ��ȡ֪ͨ����
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
//			//PendingIntent ��ת����
			PendingIntent pendingIntent=PendingIntent.getActivity(this, 0, getIntent(), 0);  
			mBuilder.setSmallIcon(R.drawable.ic_launcher)
					.setTicker("��פ֪ͨ����")
					.setContentTitle("��פ����")
					.setContentText("ʹ��cancel()�����ſ��԰���ȥ��Ŷ")
					.setContentIntent(pendingIntent);
			Notification mNotification = mBuilder.build();
			//����֪ͨ  ��Ϣ  ͼ��  
			mNotification.icon = R.drawable.ic_launcher;
			//��֪ͨ���ϵ����֪ͨ���Զ������֪ͨ
			mNotification.flags = Notification.FLAG_ONGOING_EVENT;//FLAG_ONGOING_EVENT �ڶ�����פ�����Ե���������������ȥ��  FLAG_AUTO_CANCEL  ������������ȥ��
			//������ʾ֪ͨʱ��Ĭ�ϵķ������𶯡�LightЧ��  
			mNotification.defaults = Notification.DEFAULT_VIBRATE;
			//���÷�����Ϣ������
			mNotification.tickerText = "֪ͨ����";
			//���÷���֪ͨ��ʱ��  
			mNotification.when=System.currentTimeMillis(); 
//			mNotification.flags = Notification.FLAG_AUTO_CANCEL; //��֪ͨ���ϵ����֪ͨ���Զ������֪ͨ
//			mNotification.setLatestEventInfo(this, "��פ����", "ʹ��cancel()�����ſ��԰���ȥ��Ŷ", null); //������ϸ����Ϣ  ,������������Ѿ������� 
			manager.notify(2, mNotification);
	}
}
