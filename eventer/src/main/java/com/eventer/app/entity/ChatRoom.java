 
package com.eventer.app.entity;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.ChatroomDao;
import com.eventer.app.http.LoadDataFromHTTP;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoom {
	private String roomId;
	private String[] member;
	private String[] displayname;
	private String owner;
	private long time;
	private String roomname;
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	public String[] getMember() {
		return member;
	}
	public void setMember(String[] member) {
		this.member = member;
	}
	public String[] getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String[] displayname) {
		this.displayname = displayname;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getRoomname() {
		return roomname;
	}

	public String getDefaultName() {
		if(!TextUtils.isEmpty(roomname)){
			return roomname;
		}else if(displayname!=null&&displayname.length>0){
			return ListToString(displayname)+"("+displayname.length+")";
		}else{
			return "群组";
		}

	}
	
	private String ListToString(String[] list){
		String str="";
		for(int i=0;i<list.length;i++){
			if(i<list.length-1){
				str+=list[i]+",";
			}else{
				str+=list[i];
			}
		}
		return str;
	}


	
	public void setRoomname(String roomname) {
		this.roomname = roomname;
	}

	public static void updateGroupMember(final Context context, final String groupId) {

		Map<String, String> map = new HashMap<>();
		map.put("group_id", groupId);
		map.put("uid", Constant.UID);
		LoadDataFromHTTP task = new LoadDataFromHTTP(
				context, Constant.URL_GET_GROUP_MEMBER, map);
		task.getData(new LoadDataFromHTTP.DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				try {
					int status = data.getInteger("status");
					if (status == 0) {
						JSONObject json=data.getJSONObject("group_action");
						String memberinfo=json.getString("members");
						memberinfo = memberinfo.replace(";",",");
						String[] member = memberinfo.split(",");
						List<String> list =  Arrays.asList(member);
						if (!list.contains(Constant.UID)){
							ChatEntityDao dao1 = new ChatEntityDao(context);
							dao1.deleteMessageByUser(groupId);
							ChatroomDao dao = new ChatroomDao(context);
							dao.delRoom(groupId);
						} else{
							for (String info : member) {
								Map<String, UserInfo> map = MyApplication.getInstance()
										.getUserList();
								if (!map.containsKey(info) && !Constant.UID.equals(info)) {
									UserInfo.getUserInfo(context, info);
								}
							}
							ContentValues values = new ContentValues();
							values.put(ChatroomDao.COLUMN_NAME_MEMVBER, memberinfo);
							ChatroomDao dao = new ChatroomDao(context);
							dao.update(values, groupId);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});


	}
}
