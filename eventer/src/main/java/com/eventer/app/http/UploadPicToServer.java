package com.eventer.app.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片异步加载类
 *
 * @author Leslie.Fang
 *
 */
@SuppressWarnings({"UnusedDeclaration"})
public class UploadPicToServer {

    private String url;
    private Map<String, String> map = null;
    List<String> members = new ArrayList<>();
    // 是否包含数组，默认是不包含
    private String imageuri;
    private String img;
    Context context;

    public UploadPicToServer(Context context, String url,
                             Map<String, String> map,String  imageuri ,String img) {
        this.url = url;
        this.map = map;
        this.imageuri=imageuri;
        this.img=img;
        this.context = context;
    }

    public UploadPicToServer(Context context, String url,
                             Map<String, String> map) {
        this.url = url;
        this.map = map;
        this.context = context;
    }

    //
    public UploadPicToServer(Context context, String url,
                             Map<String, String> map, List<String> members) {
        this.url = url;
        this.map = map;
        this.members = members;
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

            @SuppressWarnings("rawtypes")
            public void run() {
                String result;
                String end = "\r\n";
                String MULTIPART_FORM_DATA = "multipart/form-data";
                String BOUNDARY = "---------7d4a6d158c9"; //数据分隔线
                String imguri ="";
                if(imageuri!=null&&!imageuri.equals("")){
                    imguri = imageuri.substring(imageuri.lastIndexOf("/") + 1);//获得图片或文件名称
                }

//                   
                try {
                    URL URL = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) URL.openConnection();
                    conn.setDoInput(true);//允许输入
                    conn.setDoOutput(true);//允许输出
                    conn.setUseCaches(false);//不使用Cache
                    conn.setConnectTimeout(6000);// 6秒钟连接超时
                    conn.setReadTimeout(6000);// 6秒钟读数据超时
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);

                    StringBuilder sb = new StringBuilder();
                    //上传的表单参数部分，格式请参考文章
                    for (Map.Entry<String, String> entry : map.entrySet()) {//构建表单字段内容
                        sb.append("--");
                        sb.append(BOUNDARY);
                        sb.append("\r\n");
                        sb.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"\r\n\r\n");
                        sb.append(entry.getValue());
                        sb.append("\r\n");
                    }
                    sb.append("--");
                    sb.append(BOUNDARY);
                    sb.append("\r\n");
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    dos.write(sb.toString().getBytes());
                    if (imageuri!=null&&!imageuri.equals("")) {
                        dos.writeBytes("Content-Disposition: form-data; name=\""+img+"\"; filename=\"" + imguri + "\"" + "\r\n"+"Content-Type: image/jpeg\r\n\r\n");
                        FileInputStream fis = new FileInputStream(imageuri);
                        byte[] buffer = new byte[1024]; // 8k
                        int count;
                        while ((count = fis.read(buffer)) != -1)
                        {
                            dos.write(buffer, 0, count);
                        }
                        dos.writeBytes(end);
                        fis.close();
                    }
                    dos.writeBytes("--" + BOUNDARY + "--\r\n");
                    dos.flush();
                    InputStream is = conn.getInputStream();
                    Log.e("1", "status:"+conn.getResponseCode()+"");
                    InputStreamReader isr = new InputStreamReader(is, "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    result = br.readLine();
                    Log.e("1", "status:"+result+"");
                    String builder_BOM = jsonTokener(result);
                    JSONObject jsonObject ;
                    jsonObject = JSONObject.parseObject(builder_BOM);
                    Message msg = new Message();
                    msg.what = 111;
                    msg.obj = jsonObject;
                    handler.sendMessage(msg);

                }  catch (Exception e) {
                    e.printStackTrace();
                    Log.e("1",e.toString());
                }

            }
        }.start();

    }

    private String jsonTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }

    /**
     * 网路访问调接口
     *
     */
    public interface DataCallBack {
        void onDataCallBack(JSONObject data);
    }

}
