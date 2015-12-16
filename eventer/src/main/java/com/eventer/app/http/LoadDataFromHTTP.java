package com.eventer.app.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadDataFromHTTP {

    private String url;
    private Map<String, String> map = null;
    Context context;

    public LoadDataFromHTTP(Context context, String url,
                            Map<String, String> map) {
        this.url = url;
        this.map = map;
        this.context = context;
    }



    @SuppressLint("HandlerLeak")
    public void getData(final DataCallBack dataCallBack) {
        final Handler handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 111 && dataCallBack != null) {
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if (jsonObject != null) {

                        dataCallBack.onDataCallBack(jsonObject);

                    } else {
                        dataCallBack.onDataCallBack(null);
                    }
                }
            }
        };

        new Thread() {
            public void run() {
                try {
                    String jsonString= sendPostRequest(url, map);
                    JSONObject jsonObject;
                    jsonObject = JSONObject.parseObject(jsonString);
                    Message msg = new Message();
                    msg.what = 111;
                    msg.obj = jsonObject;
                    handler.sendMessage(msg);

                } catch (HttpHostConnectException e) {
                    // TODO: handle exception
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("status", -1);
                    Message msg = new Message();
                    msg.what = 111;
                    msg.obj = jsonObject;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("1",e.toString());
                }

            }
        }.start();

    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String sendPostRequest(String path, Map<String, String> params) throws Exception{
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(path);
        Log.e("1",path);

        List postparam = new ArrayList();
        for(Map.Entry<String, String> entry : (params).entrySet()){
            postparam.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            Log.e("1",entry.getKey()+"=="+ entry.getValue());
        }


        httpPost.setEntity(new UrlEncodedFormEntity(postparam, HTTP.UTF_8));
        HttpResponse httpResponse = httpClient.execute(httpPost);
        Log.e("1",path+httpResponse.getStatusLine().getStatusCode());


        if (httpResponse.getStatusLine().getStatusCode() == 200)
        {

            String result = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
            Log.e("1",result);
            return result;
        }
        return null;
    }

    public interface DataCallBack {
        void onDataCallBack(JSONObject data);
    }



}
