package com.eventer.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.main.LoginActivity;
import com.eventer.app.util.PreferenceUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
        if(Constant.isWifiConnected){
            new Thread(checkWifiRunnable).start();
        }else{
            new Thread(pingRunnable).start();
        }
    }

    Runnable pingRunnable = new Runnable() {
        @Override
        public void run() {
            boolean s= Ping("www.baidu.com");//ping website
            sendNetBroadcast(s);
        }
    };

    Runnable checkWifiRunnable = new Runnable() {
        @Override
        public void run() {
            boolean s= checkWifi();//ping website
            sendNetBroadcast(s);
        }
    };

    private void sendNetBroadcast(boolean susseed){
        if (!susseed) {//ping failed
            Constant.isConnectNet=false;
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("net",false);
            broadcastIntent.setAction("android.net.conn.ISGOODORBAD");
            sendBroadcast(broadcastIntent);
        }else{//ping succeed
            Constant.isConnectNet=true;
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("net",true);
            broadcastIntent.setAction("android.net.conn.ISGOODORBAD");
            sendBroadcast(broadcastIntent);
        }
    }

    public boolean Ping(String str) {
        boolean result;
        Process p;
        try {
            // ping -c 3 -w 100:-c frequency of ping, 3 means ping 3 times ，-w 100
            // timeout interval is 100s
            p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + str);
            int status = p.waitFor();
            Log.e("ping", "status:" + status); //status：0  success ；1 permission denied
            if (status == 0) {
                result = true;//ping success
                if(!Constant.isConnectNet){
//                    Toast.makeText(getApplicationContext(),"网络恢复正常~", Toast.LENGTH_SHORT).show();
                    if(!Constant.isLogin){
                        String user= PreferenceUtils.getInstance().getLoginUser();
                        String pwd=PreferenceUtils.getInstance().getLoginPwd();
                        if (user!=null&& !user.equals("") &&pwd!=null&& !pwd.equals("")) {
                            UserLogin();
                        }else{
                            Constant.isLogin=false;
                        }
                    }

                }

            } else {
                result = false;//ping failed
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            result = false;//ping failed
        }
        return result;
    }

    public boolean checkWifi(){
        try{
            return isCaptivePortal();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public static boolean isCaptivePortal() throws Exception{
        URL url = new URL("http://www.baidu.com");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        int responseCode;
        con.setConnectTimeout(6000);
        con.setReadTimeout( 6000 );
        responseCode = con.getResponseCode();
        return responseCode == 200;
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
                getApplicationContext(), Constant.URL_LOGIN, params);
        task.getData(new LoadDataFromHTTP.DataCallBack() {

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
                            if(!LoginActivity.isActive)
                                getApplicationContext().startActivity(new Intent().setClass(getApplicationContext(), LoginActivity.class));
                            break;
                        default:
                            if(!LoginActivity.isActive)
                                getApplicationContext().startActivity(new Intent().setClass(getApplicationContext(), LoginActivity.class));
                            break;
                    }

                } catch (JSONException e) {

                    Toast.makeText(getApplicationContext(), "数据解析错误...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    getApplicationContext().startActivity(new Intent().setClass(getApplicationContext(), LoginActivity.class));
                }catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
    }
}
