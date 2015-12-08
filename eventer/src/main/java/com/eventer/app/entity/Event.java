package com.eventer.app.entity;

public class Event extends EventOp {

	String content;
	String publisher;
	String time;
	String title;
	String theme;
	String place;
	long start;
	long end;
	int type;
	int myOPt;
	long issueTime;
	int UpCount;
	int DownCount;
	int readCount;


	public Event() {

	}
	
	
	public long getStart() {
		return start;
	}


	public void setStart(long start) {
		this.start = start;
	}


	public long getEnd() {
		return end;
	}


	public void setEnd(long end) {
		this.end = end;
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
		return "Event [活动ID=" + EventID + "\r\n 内容=" + content
				+ "\r\n 发布者=" + publisher + "\r\n 时间=" + time + "\r\n 标题="
				+ title + "\r\n 主题=" + theme + "\r\n 地点=" + place +"\r\n 发布时间=" + issueTime
				+ "]";
	}

	
}
