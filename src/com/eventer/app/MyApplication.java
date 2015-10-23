package com.eventer.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.eventer.app.db.DBManager;
import com.eventer.app.db.EventDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.util.PreferenceUtils;

public class MyApplication extends Application{
	
	public static Context  context;
	private static MyApplication instance;
	private static  HashMap<String ,Object> Data=new HashMap<String, Object>();
	private static  HashMap<String ,Object> CacheData=new HashMap<String, Object>();
	private static  Map<String,User> ContactList=new HashMap<String,User>();
	private static Map<String,UserInfo> UserList=new HashMap<String,UserInfo>();

	@Override
	public void onCreate() {
		super.onCreate();
		context=this;
		instance=this;
		PreferenceUtils.init(context);
		Log.e("1", "app-start");
		
	}
	
	public static MyApplication getInstance() {
		return instance;
	}
	
	public void setValueByKey(String key,Object value){
		Data.put(key, value);
	}
	
	public Object getValueByKey(String key){
		return Data.get(key);
	}
	
	public void setCacheByKey(String key,Object value){
		CacheData.put(key, value);
	}
	
	public Object getCacheByKey(String key){
		return CacheData.get(key);
	}
	
	public List<String> getContactIDList(){
		List<String> list=new ArrayList<String>();
		UserDao dao=new UserDao(context);
		list=dao.getContactIDList();
		return list;
	}
	
	
	public void finish(){
		finish();
	}
	
	public void initEventList(){
		EventDao dao=new EventDao(getApplicationContext());
		List<String> list=dao.getEventIDList();
		CacheData.put("EventList", list);
	}

	public Map<String,User> getContactList() {
		// TODO Auto-generated method stub
		if(ContactList.isEmpty()){
			UserDao dao=new UserDao(context);
			ContactList=dao.getContactList();
		}
		return ContactList;

	}
	
	public void clearContact() {
		// TODO Auto-generated method stub
	    ContactList.clear();
	}
	
	public Map<String,UserInfo> getUserList(){
		if(UserList.isEmpty()){
			Map<String,UserInfo> map=new HashMap<String, UserInfo>();
			UserDao dao=new UserDao(context);
			map=dao.getUserInfoList();
			return map;
		}
		return UserList;
	}
	
	public void addUser(UserInfo info){
		UserList.put(info.getUsername(), info);
		UserDao dao=new UserDao(context);
		dao.saveUserInfo(info);
	}
	
	public void setUserList(Map<String,UserInfo> userlist){
		MyApplication.UserList=userlist;
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
//		DBManager.getInstance().closeDatabase();
	}
    

}
