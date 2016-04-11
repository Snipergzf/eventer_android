 
package com.eventer.app.entity;


import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.http.LoadDataFromHTTP;

import java.util.HashMap;
import java.util.Map;

public class UserInfo {
	
	
    private String avatar;
    private String username;
    private String usernick;
    private int Type;
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNick() {
		return usernick;
	}
	public void setNick(String usernick) {
		this.usernick = usernick;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}


	public static void getUserInfo(Context context,String uid){
		Map<String,String> map=new HashMap<>();
		map.put("uid", uid);
		LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_GET_USERINFO, map);
		task.getData(new LoadDataFromHTTP.DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					int status=data.getInteger("status");
					switch (status) {
						case 0:
							JSONObject user_action=data.getJSONObject("user_action");
							JSONObject info=user_action.getJSONObject("info");
							String name=info.getString("name");
							String avatar=info.getString("avatar");
							UserInfo user=new UserInfo();
							user.setAvatar(avatar);
							user.setNick(name);
							user.setType(22);
							user.setUsername(name);
							MyApplication.getInstance().addUser(user);
							break;
						default:
							Log.e("1", "获取用户信息失败：");
							break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
