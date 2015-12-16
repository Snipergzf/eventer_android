
package com.eventer.app.entity;

@SuppressWarnings({"UnusedDeclaration"})
public class ChatEntity {
    private int Type; 
    private int status;
    private long MsgID;
    private long MsgTime;
    private int NotRead;
    private String ShareId;
    public int getNotRead() {
		return NotRead;
	}


	public void setNotRead(int notRead) {
		NotRead = notRead;
	}


	private String content;    
    private String From; 
    private String ImgPath;
    private boolean isComMeg = true;

    @Override
   	public String toString() {
   		return "ChatMsg [Type=" + Type + ", status=" + status + ", MsgID="
   				+ MsgID + ", MsgTime=" + MsgTime + ", content=" + content
   				+ ", From=" + From + ", ImgPath=" + ImgPath + ", isComMeg="
   				+ isComMeg + "]";
   	}


	public int getType() {
		return Type;
	}



	public void setType(int type) {
		Type = type;
	}



	public String getShareId() {
		return ShareId;
	}


	public void setShareId(String shareId) {
		ShareId = shareId;
	}


	public String getImgPath() {
		return ImgPath;
	}



	public void setImgPath(String imgPath) {
		ImgPath = imgPath;
	}



	public int getStatus() {
		return status;
	}



	public void setStatus(int status) {
		this.status = status;
	}



	public long getMsgID() {
		return MsgID;
	}



	public void setMsgID(long msgID) {
		MsgID = msgID;
	}



	public long getMsgTime() {
		return MsgTime;
	}



	public void setMsgTime(long msgTime) {
		MsgTime = msgTime;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}



	public String getFrom() {
		return From;
	}



	public void setFrom(String from) {
		From = from;
	}



	public boolean isComMeg() {
		return isComMeg;
	}



	public void setComMeg(boolean isComMeg) {
		this.isComMeg = isComMeg;
	}



	public ChatEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

    

}
