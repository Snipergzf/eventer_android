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

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
                    if(jsonString != null){
                        JSONObject jsonObject;
                        jsonObject = JSONObject.parseObject(jsonString);
                        Message msg = new Message();
                        msg.what = 111;
                        msg.obj = jsonObject;
                        handler.sendMessage(msg);
                    }else {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("status", -1);
                        Message msg = new Message();
                        msg.what = 111;
                        msg.obj = jsonObject;
                        handler.sendMessage(msg);
                    }


                } catch (HttpHostConnectException e) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("status", -1);
                    Message msg = new Message();
                    msg.what = 111;
                    msg.obj = jsonObject;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("status", -1);
                    Message msg = new Message();
                    msg.what = 111;
                    msg.obj = jsonObject;
                    handler.sendMessage(msg);
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
            Log.e("http result",result);
            return result;
        }
        return null;
    }

    public interface DataCallBack {
        void onDataCallBack(JSONObject data);
    }

    public void sendHttpRequest(String urlString, Map<String, String> requestParams){
        HttpURLConnection conn = null;
        try{
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            //设置请求属性和请求参数
            StringBuilder params = new StringBuilder();
            for(Map.Entry<String, String> entry : requestParams.entrySet()){
                params.append(entry.getKey());
                params.append("=");
                params.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                params.append("&");
            }
            if (params.length() > 0) {
                params.deleteCharAt(params.length() - 1);
            }
            byte[] data = params.toString().getBytes();
            conn.setDoOutput(true);//发送POST请求必须设置允许输出
            conn.setUseCaches(false);//不使用Cache
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection",  "Keep-Alive");
            conn.setRequestProperty("Charset",  "UTF-8");
            conn.setRequestProperty("Content-Length",String.valueOf(data.length)); conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); DataOutputStream outStream = new DataOutputStream(conn.getOutputStream()); outStream.write(data);
            outStream.flush();
            if( conn.getResponseCode() == 200 ){
                InputStream result = conn.getInputStream();
                outStream.close();
                //do something 处理返回数据流
            }
        }catch (Exception e ){
            e.printStackTrace();
        } finally{
            conn.disconnect();
        }
    }





}
