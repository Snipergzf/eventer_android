package com.eventer.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eventer.app.service.BootService;

/**
 * Created by LiuNana on 2015/12/16.
 * do something when the phone is
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //后边的XXX.class就是要启动的服务
        Intent service = new Intent(context, BootService.class);
        context.startService(service);
        //start the app
//        Intent intent1 = context.getPackageManager().getLaunchIntentForPackage("com.eventer.app");
//        context.startActivity(intent1);
    }

}
