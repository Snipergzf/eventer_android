 
package com.eventer.app.entity;


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
	
}
