package com.eventer.app.ui.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.view.View;


public class BaseActivity extends FragmentActivity {
    private static final int notifiId = 11;
    protected NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // onresumeʱ��ȡ��notification��ʾ
      //  EMChatManager.getInstance().activityResumed();

    }

    @Override
    protected void onStart() {
        super.onStart();
        

    }

    /**
     * ��Ӧ����ǰ̨ʱ�������ǰ��Ϣ�������ڵ�ǰ�Ự����״̬����ʾһ�� �������Ҫ��ע�͵�����
     * 
     * @param message
     */
//    protected void notifyNewMessage(EMMessage message) {
//        // ����������˲�����ֻ��ʾ��Ŀ��Ⱥ��(�����app�ﱣ��������ݵģ�demo�ﲻ���ж�)
//        // �Լ�������setShowNotificationInbackgroup:false(��Ϊfalse�󣬺�̨ʱsdkҲ���͹㲥)
//        if (!EasyUtils.isAppRunningForeground(this)) {
//            return;
//        }
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//                this).setSmallIcon(getApplicationInfo().icon)
//                .setWhen(System.currentTimeMillis()).setAutoCancel(true);
//
//        String ticker = CommonUtils.getMessageDigest(message, this);
//        if (message.getType() == Type.TXT)
//            ticker = ticker.replaceAll("\\[.{2,3}\\]", "[����]");
//        // ����״̬����ʾ
//        mBuilder.setTicker(message.getFrom() + ": " + ticker);
//
//        // ��������pendingintent��������2.3�Ļ����ϻ���bug
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, notifiId,
//                intent, PendingIntent.FLAG_ONE_SHOT);
//        mBuilder.setContentIntent(pendingIntent);
//
//        Notification notification = mBuilder.build();
//        notificationManager.notify(notifiId, notification);
//        notificationManager.cancel(notifiId);
//    }

    /**
     * ����
     * 
     * @param view
     */
    public void back(View view) {
        finish();
    }

}
