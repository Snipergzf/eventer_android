package com.eventer.app.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.JsonReader;
import android.util.Log;

import com.eventer.app.Constant;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.Course;
import com.eventer.app.entity.User;

public class HttpUnit {


	public HttpUnit() {
		// TODO Auto-generated constructor stub
	}
	
	public static int sendLoginRequest( Map<String, String> params) throws Exception{
		String jsonString= sendPostRequest(Constant.URL_LOGIN, params);
        JSONObject jsonObject= new  JSONObject(jsonString);
        int status=jsonObject.getInt("status");
        if(status==0){
        	String userinfo=jsonObject.getString("user_action");
        	JSONObject jsonLogin= new  JSONObject(userinfo);;
        	Constant.UID=jsonLogin.getInt("uid")+"";
        	Constant.TOKEN=jsonLogin.getString("token");
        	Log.e("1","Http-Login-" +jsonLogin.getString("uid")+"-----"+jsonLogin.getString("token"));     	        
        } 
		return status;
	}
	
	public static Map<String,Object> sendFriendRequest( Map<String, String> params) throws Exception{
		String jsonString= sendPostRequest(Constant.WEB_SERVICE_URL+"v1/friend/add", params);
        JSONObject jsonObject= new  JSONObject(jsonString);
        Map<String,Object> map=new HashMap<String, Object>();       
        int status=jsonObject.getInt("status");
        String info="";
        if(status==0){
        	String finfo=jsonObject.getString("friend_action");
        	JSONObject jsonLogin= new  JSONObject(finfo);;
        	info=jsonLogin.getString("certificate");
        	Log.e("1","add-friend" +info);     	        
        }else if(status==4){
        	info="�ף����Ѿ���ú��ѷ��͹������ˣ�";
        }else if(status==5){
        	info="�����Ѿ��Ǻ����ˣ�";
        }else if(status==30001){
        	info="ϵͳ��������ά���С�����";
        }else{
        	info="��Ӻ���ʧ�ܡ�����";
        } 
        map.put("status",status);
        map.put("info", info);
		return map;
	}
	
	public static Map<String,Object> sendFriendComfirm( Map<String, String> params) throws Exception{
		String jsonString= sendPostRequest(Constant.WEB_SERVICE_URL+"v1/friend/confirm", params);
        JSONObject jsonObject= new  JSONObject(jsonString);
        Map<String,Object> map=new HashMap<String, Object>();       
        int status=jsonObject.getInt("status");
        String info="";
        if(status==0){
        	Log.e("1","add-friend" +info); 
//        	JSONObject obj=jsonObject.getJSONObject("friend_action");
//        	String result=obj.getString("result");
//        	if(result.equals("succeed")){
//        		status=-1;
//        		info="���ź�����Ĳ���ʧ���ˣ�";
//        	}
        }else if(status==30001){
        	info="ϵͳ��������ά���С�����";
        } else if(status==5){
        	info="�����Ѿ��Ǻ����ˣ�";
        }else{
        	info="��Ӻ���ʧ�ܣ�";
        }
        map.put("status",status);
        map.put("info", info);
		return map;
	}
	
	public static Map<String,Object> sendSerachFriendRequest( Map<String, String> params) throws Exception{
		String jsonString= sendPostRequest(Constant.WEB_SERVICE_URL+"v1/friend/search", params);
		Log.e("1",jsonString);
        JSONObject jsonObject= new  JSONObject(jsonString);
        Map<String,Object> map=new HashMap<String, Object>();       
        int status=jsonObject.getInt("status");
        String info="";
        if(status==0){
        	JSONObject obj=jsonObject.getJSONObject("friend_action");
        	info=obj.getString("info");
        	Log.e("1","add-friend" +info);     	        
        }else if(status==30001){
        	info="ϵͳ��������ά���С�����";
        } else if(status==1){
        	info="���ź������޴��ˣ�";
        }else{
        	info="��������ʧ�ܣ�";
        }
        map.put("status",status);
        map.put("info", info);
		return map;
	}
	
	
	public static Map<String,Object> sendSetAvatarRequest( Map<String, String> params) throws Exception{
		String jsonString= sendPostRequest(Constant.WEB_SERVICE_URL+"v1/user/set_avatar", params);
		Log.e("1",jsonString);
        JSONObject jsonObject= new  JSONObject(jsonString);
        Map<String,Object> map=new HashMap<String, Object>();       
        int status=jsonObject.getInt("status");
        String info="";
        if(status==0){
        	info=jsonObject.getString("friend_action");
        	Log.e("1","add-friend" +info);     	        
        }else if(status==30001){
        	info="ϵͳ��������ά���С�����";
        } else if(status==12){
        	info="���ź������޴��ˣ�";
        }else{
        	info="��������ʧ�ܣ�";
        }
        map.put("status",status);
        map.put("info", info);
		return map;
	}
	
	public static Map<String,Object> sendGetAvatarRequest( Map<String, String> params) throws Exception{
		String jsonString= sendPostRequest(Constant.WEB_SERVICE_URL+"v1/user/get_avatar", params);
		Log.e("1",jsonString);
        JSONObject jsonObject= new  JSONObject(jsonString);
        Map<String,Object> map=new HashMap<String, Object>();       
        int status=jsonObject.getInt("status");
        String info="";
        if(status==0){
        	JSONObject userinfo=jsonObject.getJSONObject("user_action");
        	info=userinfo.getString("avatar");       	 	        
        }else if(status==30001){
        	info="ϵͳ��������ά���С�����";
        } 
        map.put("status",status);
        map.put("info", info);
		return map;
	}
	
	public static List<Course> sendCourseRequest( Map<String, String> params) throws Exception{
		String jsonString= sendPostRequest(Constant.WEB_SERVICE_URL+"v1/course/searchOne", params);
		Log.e("1", jsonString);
        JSONObject jsonObject= new  JSONObject(jsonString);
        
        List<Course> list=new ArrayList<Course>();
        int status=jsonObject.getInt("status");
        if(status==0){
        	JSONObject info=jsonObject.getJSONObject("course_action");
        	//JSONObject course=info.getJSONObject("course");
        	JSONArray arraylist=info.getJSONArray("course");
        	for(int i=0;i<arraylist.length();i++){
        		JSONObject course=arraylist.getJSONObject(i);
        		String teacher=course.getString("t_name");
            	String c_name=course.getString("c_name");
            	int c_id=course.getInt("_id");           	            	
            	String major=course.getString("s_specialty");
//            	String grade=course.getString("s_grade");
//            	
//                String faculty=course.getString("s_faculty");
            	JSONObject classjson=course.getJSONObject("s_class");
            	String s_class="";
            	Iterator<String> it=classjson.keys();
            	int index=0;
            	while (it.hasNext()) {
            		s_class+=classjson.getString(index+"")+"  ";	
            		index++;
            		it.next();
				}  
     	
            	String detail=course.getString("c_detail");
            	
            	Course c=new Course();
        		c.setInfo(detail);
        		c.setClassname(c_name);
        		c.setTeacher(teacher);
        		c.setClassid(c_id);
        		c.setS_class(s_class);
        		if(major==null){
        			major="";
        		}
        		c.setMajor(major);
        		list.add(c);
            		
        	}
        	        
        } 
		return list;
	}
	

	public static String sendGetRequest(String path, Map<String, String> params) throws Exception{
		HttpClient httpClient = new DefaultHttpClient();
		StringBuilder sb = new StringBuilder(path);
		sb.append('?');
		// ?method=save&title=435435435&timelength=89
		for(Map.Entry<String, String> entry : params.entrySet()){
			sb.append(entry.getKey()).append('=')
				.append(entry.getValue()).append('&');
		}
		sb.deleteCharAt(sb.length()-1);
		
		URI url = new URI(sb.toString());
		Log.e("1",url.toString());
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = httpClient.execute(httpGet); 
		
		String result=null;
		//�ڶ�����ʹ��execute��������HTTP GET���󣬲�����HttpResponse����
		 if (httpResponse.getStatusLine().getStatusCode() == 200)
		 {  
		     result = EntityUtils.toString(httpResponse.getEntity());
		     return result;
		 }
		return null;
	}
	
	public static List<Course> sendCourseTableRequest( Map<String, String> params) throws Exception {
		// TODO Auto-generated method stub
		String jsonString= sendPostRequest(Constant.WEB_SERVICE_URL+"v1/course/searchAll", params);
		Log.e("1", jsonString);
        JSONObject jsonObject= new  JSONObject(jsonString);       
        List<Course> list=new ArrayList<Course>();
        int status=jsonObject.getInt("status");
        if(status==0){
        	JSONObject info=jsonObject.getJSONObject("course_action");
        	JSONArray arraylist=info.getJSONArray("course");
        	for(int i=0;i<arraylist.length();i++){
        		JSONObject course=arraylist.getJSONObject(i);
        		String teacher=course.getString("t_name");
            	String c_name=course.getString("c_name");
            	int c_id=course.getInt("_id");  
            	String major=course.getString("s_specialty");
            	String grade=course.getString("s_grade");
            	
                String faculty=course.getString("s_faculty");
            	String detail=course.getString("c_detail");
            	JSONObject classjson=course.getJSONObject("s_class");
            	String s_class="";
            	Iterator<String> it=classjson.keys();
            	int index=0;
            	while (it.hasNext()) {
            		s_class+=classjson.getString(index+"")+"  ";	
            		index++;
            		it.next();
				}         	
            	Course c=new Course();
        		c.setInfo(detail);
        		c.setClassname(c_name);
        		c.setTeacher(teacher);
        		c.setClassid(c_id);
        		if(grade==null){
        			grade="";
        		}
        		c.setGrade(grade);
        		if(major==null){
        			major="";
        		}
        		c.setMajor(major);
        		c.setFaculty(faculty);
        		c.setS_class(s_class);
        		list.add(c);
        	}
        	        
        } else{
        	return null;
        }
		return list;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String sendPostRequest(String path, Map<String, String> params) throws Exception{
		HttpClient httpClient = new DefaultHttpClient();
		 //��һ��������HttpPost����
		   HttpPost httpPost = new HttpPost(path);
		   Log.e("1",path);
		   //����HTTP POST�������������NameValuePair����
		   List postparam = new ArrayList();
		   for(Map.Entry<String, String> entry : (params).entrySet()){
				postparam.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				Log.e("1",entry.getKey()+"=="+ entry.getValue());
			}
		   
		   //����httpPost�������
		   httpPost.setEntity(new UrlEncodedFormEntity(postparam, HTTP.UTF_8));
		   HttpResponse httpResponse = httpClient.execute(httpPost);
		   Log.e("1",path+httpResponse.getStatusLine().getStatusCode());
		   //�ڶ�����ʹ��execute��������HTTP GET���󣬲�����HttpResponse����
		   if (httpResponse.getStatusLine().getStatusCode() == 200)
		   {
		        //��������ʹ��getEntity������÷��ؽ��
		        String result = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
		        Log.e("1",result);
		        return result;   
		   }
		   return null;
	}
	
	
	public static boolean isCaptivePortal() throws Exception{
	    //mUrlʵ�ʷ��ʵĵ�ַ�ǣ�http://clients3.google.com/generate_204
	    String mUrl = "http://g.cn/generate_204";
	    /*
	    Captive Portal�Ĳ��Էǳ��򵥣�������mUrl����һ��HTTP GET����������������ṩ��û������Portal
	    Check����HTTP GET���󽫷���204��204��ʾ������ɹ�����û�����ݷ��ء�������������ṩ��������
	    Portal Check������һ�����ض���ĳ���ض���ҳ��������HTTP GET�ķ���ֵ�Ͳ���204
	   */	
	    HttpClient httpClient = new DefaultHttpClient();
		URI url = new URI(mUrl);
		Log.e("1",url.toString());
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		Log.e("1","----------------");
		//�ڶ�����ʹ��execute��������HTTP GET���󣬲�����HttpResponse����
		 httpResponse = new DefaultHttpClient().execute(httpGet);
		 Log.e("1",httpResponse.getStatusLine().getStatusCode()+"");
		 if (httpResponse.getStatusLine().getStatusCode() == 204)
		 {  
		     return true;
		 }
		 return false;
	}

	
	
	 /** 
     * @param ֻ������ͨ����,���ô˷��� 
     * @param urlString ��Ӧ��Php ҳ�� 
     * @param params ��Ҫ���͵�������� �������õķ��� 
     * @param imageuri ͼƬ���ļ��ֻ��ϵĵ�ַ ��:sdcard/photo/123.jpg 
     * @param img ͼƬ���� 
     * @return Json 
     */  
    public static String sendPictureREquest(Map<String, Object> params,String  imageuri ,String img){  
        String result="";  
          
        String end = "\r\n";          
        String uploadUrl=Constant.WEB_SERVICE_URL+"v1/user/set_avatar";//new BingoApp().URLIN ���Ҷ�����ϴ�URL  
        String MULTIPART_FORM_DATA = "multipart/form-data";   
        String BOUNDARY = "---------7d4a6d158c9"; //���ݷָ���  
        String imguri ="";  
        if (!imageuri.equals("")) {  
            imguri = imageuri.substring(imageuri.lastIndexOf("/") + 1);//���ͼƬ���ļ�����  
        }           
        try {  
            URL url = new URL(uploadUrl);    
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();    
            conn.setDoInput(true);//��������    
            conn.setDoOutput(true);//�������    
            conn.setUseCaches(false);//��ʹ��Cache     
            conn.setConnectTimeout(6000);// 6�������ӳ�ʱ  
            conn.setReadTimeout(6000);// 6���Ӷ����ݳ�ʱ  
            conn.setRequestMethod("POST");              
            conn.setRequestProperty("Connection", "Keep-Alive");    
            conn.setRequestProperty("Charset", "UTF-8");    
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);  
              
            StringBuilder sb = new StringBuilder();                  
            //�ϴ��ı��������֣���ʽ��ο�����    
            for (Map.Entry<String, Object> entry : params.entrySet()) {//�������ֶ�����    
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
            if (!imageuri.equals("")&&!imageuri.equals(null)) {  
                 dos.writeBytes("Content-Disposition: form-data; name=\""+img+"\"; filename=\"" + imguri + "\"" + "\r\n"+"Content-Type: image/jpeg\r\n\r\n");  
                  FileInputStream fis = new FileInputStream(imageuri);  
                  byte[] buffer = new byte[1024]; // 8k  
                  int count = 0;  
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
//            Log.e("1", "status:"+conn.getResponseCode()+"");
              InputStreamReader isr = new InputStreamReader(is, "utf-8");  
              BufferedReader br = new BufferedReader(isr);  
              result = br.readLine();  
        }catch (Exception e) {  
            result = "{\"ret\":\"898\"}";  
            Log.e("1","sendPic_err:--"+e.toString());
        }  
        return result;  
    }

	public static List<User> searchFriendListRequest(List<String> list) {
		// TODO Auto-generated method stub
		List<User> users=new ArrayList<User>();
		for (String string : list) {
			Map<String,String> map=new HashMap<String, String>();
			map.put("uid", string);
			try {
				String jsonString= sendPostRequest(Constant.URL_GET_SELFINFO, map);
				JSONObject data=new JSONObject(jsonString);
				JSONObject json=data.getJSONObject("user_action");
            	JSONObject info=json.getJSONObject("info");
            	User u=new User();
            	u.setAvatar(info.getString("avatar"));
            	u.setNick(info.getString("name"));
            	u.setType(1);
            	u.setUsername(string);
            	u.setSex(info.getString("sex"));
            	users.add(u);
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				Log.e("1", "refresh_friend_failed");
			}
		}
		return users;
	}  



	
}