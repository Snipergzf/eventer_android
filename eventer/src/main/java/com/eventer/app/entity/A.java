package com.eventer.app.entity;

public class A {
	public String RID;
	public String MID;
	public String body;
	public long time;
	public int type;
	
	public A(String MID, String RID, String body, int type){
		this.RID = RID;
		this.MID = MID;
		this.time =System.currentTimeMillis()/1000;
		this.body = body;
		this.type=type;
	}
}
