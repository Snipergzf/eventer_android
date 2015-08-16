package com.eventer.app.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

/**
 * 图片异步加载类
 * 
 * @author Leslie.Fang
 * 
 */
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
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 111 && dataCallBack != null) {
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    if (jsonObject != null) {

                        dataCallBack.onDataCallBack(jsonObject);

                    } else {

                        Toast.makeText(context, "访问服务器出错...", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        };

        new Thread() {
            public void run() {                 
                 try { 
                	  String jsonString= sendPostRequest(url, map);
                       JSONObject jsonObject = new JSONObject();
                       jsonObject = JSONObject.parseObject(jsonString);
	                   Message msg = new Message();
	                   msg.what = 111;
	                   msg.obj = jsonObject;
	                   handler.sendMessage(msg);

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    Log.e("1",e.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("1",e.toString());
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
		 //第一步，创建HttpPost对象
		   HttpPost httpPost = new HttpPost(path);
		   Log.e("1",path);
		   //设置HTTP POST请求参数必须用NameValuePair对象
		   List postparam = new ArrayList();
		   for(Map.Entry<String, String> entry : (params).entrySet()){
				postparam.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				Log.e("1",entry.getKey()+"=="+ entry.getValue());
			}
		   
		   //设置httpPost请求参数
		   httpPost.setEntity(new UrlEncodedFormEntity(postparam, HTTP.UTF_8));
		   HttpResponse httpResponse = httpClient.execute(httpPost);
		   Log.e("1",path+httpResponse.getStatusLine().getStatusCode());
		   //第二步，使用execute方法发送HTTP GET请求，并返回HttpResponse对象
		   if (httpResponse.getStatusLine().getStatusCode() == 200)
		   {
		        //第三步，使用getEntity方法活得返回结果
		        String result = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
		        Log.e("1",result);
		        return result;   
		   }
		   return null;
	}

    /**
     * 网路访问调接口
     * 
     */
    public interface DataCallBack {
        void onDataCallBack(JSONObject data);
    }
    
    

}
