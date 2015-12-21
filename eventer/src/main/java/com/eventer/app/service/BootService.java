package com.eventer.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.eventer.app.widget.calendar.AlarmReceiver;

/**
 * Created by Administrator on 2015/12/16.
 * check whether the internet is available while the net changed
 */
public class BootService extends Service{
    //public static String PHONENO;

    public class LocalBinder extends Binder {
        BootService getService(){
            return BootService.this;
        }
    }
    public IBinder onBind(Intent intent){
        return null;
    }
    private void registerIntentReceiver(){

        Log.e("1", "service");
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);// Msg Manager
        try {
            Intent intent = new Intent(getApplicationContext(),
                    AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Long now_time = System.currentTimeMillis();
            am.setRepeating(AlarmManager.RTC_WAKEUP, now_time + 3, 60 * 1000,
                    sender);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate() {
        registerIntentReceiver();
    }
}
