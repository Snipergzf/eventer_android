package com.eventer.app.entity;

public class Event extends EventOp {

	String content;
	String publisher;
	String time;
	String title;
	String theme;
	String place;
	int type;
	int myOPt;
	long issueTime;
	int UpCount;
	int DownCount;
	int readCount;


	public Event() {

	}
	
	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMyOPt() {
		return myOPt;
	}
	public void setMyOPt(int myOPt) {
		this.myOPt = myOPt;
	}
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public int getUpCount() {
		return UpCount;
	}

	public void setUpCount(int upCount) {
		UpCount = upCount;
	}

	public int getDownCount() {
		return DownCount;
	}

	public void setDownCount(int downCount) {
		DownCount = downCount;
	}

	public int getReadCount() {
		return readCount;
	}

	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}

	public long getIssueTime() {
		return issueTime;
	}

	public void setIssueTime(long issueTime) {
		this.issueTime = issueTime;
	}

	@Override
	public String toString() {
		return "Event [�ID=" + EventID + "\r\n ����=" + content
				+ "\r\n ������=" + publisher + "\r\n ʱ��=" + time + "\r\n ����="
				+ title + "\r\n ����=" + theme + "\r\n �ص�=" + place +"\r\n ����ʱ��=" + issueTime
				+ "]";
	}

	
}
