package com.eventer.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.service.CheckInternetService;

/**
 * Created by LiuNana on 2015/12/11.
 * Monitor network changes
 */
public class NetworkReceiver extends BroadcastReceiver {
    protected Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        ConnectivityManager connectivityManager = (ConnectivityManager) context

                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetInfo != null && activeNetInfo.isAvailable()
                && activeNetInfo.isConnected()) { //net is available
            if(activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI){
                Constant.isWifiConnected=true;
            }else{
                Constant.isWifiConnected=false;
            }
            context.startService(new Intent(context, CheckInternetService.class));//make sure the net is truly available

        }else if(Constant.isConnectNet){
            Constant.isConnectNet=false;
            Toast.makeText(mContext, mContext.getText(R.string.no_network), Toast.LENGTH_SHORT).show();
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("net",false);
            broadcastIntent.setAction("android.net.conn.ISGOODORBAD");
            context.sendBroadcast(broadcastIntent);
            Log.e("1","no net");
        }
    }

}
