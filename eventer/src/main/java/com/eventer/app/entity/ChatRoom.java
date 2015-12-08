 
package com.eventer.app.entity;

import android.text.TextUtils;

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
		if(!TextUtils.isEmpty(roomname)){
			return roomname;
		}else if(displayname!=null&&displayname.length>0){
			return ListToString(displayname)+"("+displayname.length+")";
		}else{
			return null;
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
	
}
