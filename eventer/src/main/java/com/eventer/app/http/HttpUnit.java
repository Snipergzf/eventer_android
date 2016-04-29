package com.eventer.app.http;

import android.util.Log;

import com.eventer.app.Constant;
import com.eventer.app.entity.Course;
import com.eventer.app.entity.User;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUnit {


	public HttpUnit() {
		// TODO Auto-generated constructor stub
	}

	public static int sendLoginRequest( Map<String, String> params) throws Exception{
		String jsonString= sendPostRequest(Constant.URL_LOGIN_NEW, params);
		JSONObject jsonObject= new  JSONObject(jsonString);
		int status=jsonObject.getInt("status");
		if(status==0){
			String userinfo=jsonObject.getString("user_action");
			JSONObject jsonLogin= new  JSONObject(userinfo);
			Constant.UID=jsonLogin.getInt("uid")+"";
			Constant.TOKEN=jsonLogin.getString("token");
			Log.e("1","Http-Login-" +jsonLogin.getString("uid")+"-----"+jsonLogin.getString("token"));
		}
		return status;
	}

	public static Map<String,Object> sendFriendRequest( Map<String, String> params) throws Exception{
		String jsonString= sendPostRequest(Constant.WEB_SERVICE_URL+"v1/friend/add", params);
		JSONObject jsonObject= new  JSONObject(jsonString);
		Map<String,Object> map=new HashMap<>();
		int status=jsonObject.getInt("status");
		String info;
		if(status==0){
			String finfo=jsonObject.getString("friend_action");
			JSONObject jsonLogin= new  JSONObject(finfo);
			info=jsonLogin.getString("certificate");
			Log.e("1","add-friend" +info);
		}else if(status==4){
			info="亲，您已经向该好友发送过请求了！";
		}else if(status==5){
			info="你们已经是好友了！";
		}else if(status==30001){
			info="系统正在玩命维护中。。。";
		}else{
			info="添加好友失败。。。";
		}
		map.put("status",status);
		map.put("info", info);
		return map;
	}

	public static Map<String,Object> sendFriendComfirm( Map<String, String> params) throws Exception{
		String jsonString= sendPostRequest(Constant.WEB_SERVICE_URL+"v1/friend/confirm", params);
		JSONObject jsonObject= new  JSONObject(jsonString);
		Map<String,Object> map=new HashMap<>();
		int status=jsonObject.getInt("status");
		String info="";
		if(status==0){
			Log.e("1","add-friend" +info);
//        	JSONObject obj=jsonObject.getJSONObject("friend_action");
//        	String result=obj.getString("result");
//        	if(result.equals("succeed")){
//        		status=-1;
//        		info="很遗憾，你的操作失败了！";
//        	}
		}else if(status==30001){
			info="系统正在玩命维护中。。。";
		} else if(status==5){
			info="你们已经是好友了！";
		}else{
			info="添加好友失败！";
		}
		map.put("status",status);
		map.put("info", info);
		return map;
	}

	public static Map<String,Object> sendSerachFriendRequest( Map<String, String> params) throws Exception{
		String jsonString= sendPostRequest(Constant.URL_GET_FRIENDINFO, params);
		Log.e("1",jsonString);
		JSONObject jsonObject= new  JSONObject(jsonString);
		Map<String,Object> map=new HashMap<>();
		int status=jsonObject.getInt("status");
		String info;
		if(status==0){
			JSONObject obj=jsonObject.getJSONObject("friend_action");
			info=obj.getString("info");
			Log.e("1","add-friend" +info);
		}else if(status==30001){
			info="系统正在玩命维护中。。。";
		}else if(status==1){
			info="很遗憾，查无此人！";
		}else{
			info="搜索好友失败！";
		}
		map.put("status",status);
		map.put("info", info);
		return map;
	}

	public static Map<String,Object> sendGetAvatarRequest( Map<String, String> params) throws Exception{
		String jsonString= sendPostRequest(Constant.WEB_SERVICE_URL+"v1/user/get_avatar", params);
		Log.e("1",jsonString);
		JSONObject jsonObject= new  JSONObject(jsonString);
		Map<String,Object> map=new HashMap<>();
		int status=jsonObject.getInt("status");
		String info="";
		if(status==0){
			JSONObject userinfo=jsonObject.getJSONObject("user_action");
			info=userinfo.getString("avatar");
		}else if(status==30001){
			info="系统正在玩命维护中。。。";
		}
		map.put("status",status);
		map.put("info", info);
		return map;
	}

	public static List<Course> sendCourseRequest( Map<String, String> params) throws Exception{
		String jsonString= sendPostRequest(Constant.WEB_SERVICE_URL+"v1/course/searchOne", params);
		Log.e("1", jsonString);
		JSONObject jsonObject= new  JSONObject(jsonString);

		List<Course> list=new ArrayList<>();
		int status=jsonObject.getInt("status");
		if(status==0){
			JSONObject info=jsonObject.getJSONObject("course_action");
			JSONArray arraylist=info.getJSONArray("course");
			for(int i=0;i<arraylist.length();i++){
				try{
					JSONObject course=arraylist.getJSONObject(i);
					String teacher=course.getString("t_name");
					String c_name=course.getString("c_name");
					String c_id=course.getString("_id");
					String major=course.getString("s_specialty");
//            	String grade=course.getString("s_grade");
                 String s_class="";
					try{
						JSONArray classjson=course.getJSONArray("s_class");
						for (int k = 0; k< classjson.length(); k++){
							s_class+=classjson.get(k)+"  ";
						}
					}catch (Exception e){
						e.printStackTrace();
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
				}catch (Exception e){
					e.printStackTrace();
				}
			}

		}
		return list;
	}




	public static List<Course> sendCourseTableRequest( Map<String, String> params) throws Exception {
		// TODO Auto-generated method stub
		String jsonString= sendPostRequest(Constant.WEB_SERVICE_URL+"v1/course/searchAll", params);
		Log.e("1", jsonString);
		JSONObject jsonObject= new  JSONObject(jsonString);
		List<Course> list=new ArrayList<>();
		int status=jsonObject.getInt("status");
		if(status==0){
			JSONObject info=jsonObject.getJSONObject("course_action");
			JSONArray arraylist=info.getJSONArray("course");
			for(int i=0;i<arraylist.length();i++){
				try {
					JSONObject course=arraylist.getJSONObject(i);
					String teacher=course.getString("t_name");
					String c_name=course.getString("c_name");
					String c_id=course.getString("_id");
					String major=course.getString("s_specialty");
					String grade=course.getString("s_grade");

					String faculty=course.getString("s_faculty");
					String detail=course.getString("c_detail");
					String s_class="";
					try{
						JSONArray classjson=course.getJSONArray("s_class");
						for (int k = 0; k< classjson.length(); k++){
							s_class+=classjson.get(k)+"  ";
						}
					}catch (Exception e){
						e.printStackTrace();
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
				} catch (Exception e){
					e.printStackTrace();
				}

			}

		} else{
			return null;
		}
		return list;
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
		// 请求超时
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		// 读取超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
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



	public static Map<String, Object> getStream(String url) {
		HttpParams httpparams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpparams,
				30000);
		HttpClient client = new DefaultHttpClient(httpparams);
		HttpGet get = new HttpGet(url);

		try {
			HttpResponse response = client.execute(get);
			int status = response.getStatusLine().getStatusCode();
			Log.e("1", status+"");
			Map<String, Object>  map=new HashMap<>();
			if (200 == status) {
				map.put("status", 200);
				map.put("inputstream", response.getEntity().getContent());
				return map;
			}else if(status==404){
				map.put("status", 404);
				return map;
			}
		} catch (Exception e) {
			Log.e("1", e.getMessage()+"dd"+e.toString());
		}

		return null;
	}


	/**

	 * @param params 需要发送的相关数据 包括调用的方法
	 * @param imageuri 图片或文件手机上的地址 如:sdcard/photo/123.jpg
	 * @param img 图片名称
	 * @return Json
	 */
	public static String sendPictureREquest(Map<String, Object> params,String  imageuri ,String img){
		String result;

		String end = "\r\n";
		String uploadUrl=Constant.WEB_SERVICE_URL+"v1/user/set_avatar";//new BingoApp().URLIN 是我定义的上传URL
		String MULTIPART_FORM_DATA = "multipart/form-data";
		String BOUNDARY = "---------7d4a6d158c9"; //数据分隔线
		String imguri ="";
		if (!imageuri.equals("")) {
			imguri = imageuri.substring(imageuri.lastIndexOf("/") + 1);//获得图片或文件名称
		}
		try {
			URL url = new URL(uploadUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
			for (Map.Entry<String, Object> entry : params.entrySet()) {//构建表单字段内容
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
		List<User> users=new ArrayList<>();
		for (String string : list) {
			Map<String,String> map=new HashMap<>();
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