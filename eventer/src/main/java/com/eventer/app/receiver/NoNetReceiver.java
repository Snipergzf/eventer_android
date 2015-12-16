package com.eventer.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by LiuNana on 2015/12/11.
 * if the network is not available,do something
 */
public class NoNetReceiver extends BroadcastReceiver {

    protected Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String  action=intent.getAction();

        if(action.equals("android.net.conn.ISGOODORBAD")){
//            Toast.makeText(mContext,"无法连接到服务器？网络不稳定？！", Toast.LENGTH_SHORT).show();
        }


    }
}

