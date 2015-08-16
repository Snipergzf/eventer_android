package com.eventer.app.entity;

public class EventOp {
	String EventID;
	int operation;
	String operator;
	long optime;

	public EventOp() {

	}

	public String getEventID() {
		return EventID;
	}

	public void setEventID(String eventID) {
		EventID = eventID;
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String publisher) {
		this.operator = publisher;
	}

	public long getOpTime() {
		return optime;
	}

	public void setOpTime(long time) {
		this.optime = time;
	}

}
