package com.eventer.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.eventer.app.Constant;

import java.io.IOException;

/**
 * Created by LiuNana on 2015/12/11.
 * check whether the internet is available while the net changed
 */
public class CheckInternetService extends IntentService {
    public CheckInternetService() {
        super("ping");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String s= Ping("www.baidu.com");//ping website
        if (!s.trim().equals("")) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("android.net.conn.ISGOODORBAD");
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(broadcastIntent);
        }

    }

    public String Ping(String str) {
        String result = "";
        Process p;
        try {
            // ping -c 3 -w 100:-c frequency of ping, 3 means ping 3 times ，-w 100
            // timeout interval is 100s
            p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + str);
            int status = p.waitFor();
            Log.e("ping", "status:" + status); //status：0  success ；1 permission denied
            if (status == 0) {
                result = "";//ping success
                if(!Constant.isConnectNet){
                    Constant.isConnectNet=true;
                    Toast.makeText(getApplicationContext(),"网络恢复正常~", Toast.LENGTH_SHORT).show();
                }

            } else {
                result = "failed";//ping failed
                Constant.isConnectNet=false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
