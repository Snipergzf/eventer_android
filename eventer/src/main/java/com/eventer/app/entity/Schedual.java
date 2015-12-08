 
package com.eventer.app.entity;


public class Schedual {
	private long Schdeual_ID;
	private String title;
    private String place;
    private String detail;
    private int remind;
    private String remindtime;
    private String starttime;
    private String endtime;
    private String timespan;
    private String eventId;
    private int frequency;
    private int status;
    private int type;
    private String shareId;
    private String friend;
    
	
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public long getSchdeual_ID() {
		return Schdeual_ID;
	}
	public void setSchdeual_ID(long id) {
		Schdeual_ID = id;
	}
	@Override
	public String toString() {
		return "Schedual [Schdeual_ID=" + Schdeual_ID + ", title=" + title
				+ ", place=" + place + ", detail=" + detail + ", remind="
				+ remind + ", remindtime=" + remindtime + ", starttime="
				+ starttime + ", endtime=" + endtime + ", timespan=" + timespan
				+ ", frequency=" + frequency + ", status=" + status
				+ ", friend=" + friend + "]";
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public int getRemind() {
		return remind;
	}
	public void setRemind(int remind) {
		this.remind = remind;
	}
	public String getRemindtime() {
		return remindtime;
	}
	public void setRemindtime(String remindtime) {
		this.remindtime = remindtime;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getTimespan() {
		return timespan;
	}
	public void setTimespan(String timespan) {
		this.timespan = timespan;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getFriend() {
		return friend;
	}
	public void setFriend(String friend) {
		this.friend = friend;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getShareId() {
		return shareId;
	}
	public void setShareId(String shareId) {
		this.shareId = shareId;
	}
 
    
	
}
