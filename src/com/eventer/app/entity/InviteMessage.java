
package com.eventer.app.entity;


public class InviteMessage {
	private String from;
	//ʱ��
	private long time;
	//�������
	private String reason;
	
	//δ��֤����ͬ���״̬
	private InviteMesageStatus status;
	//Ⱥid
	private String groupId;
	//Ⱥ����
	private String groupName;
	
	private String certification;
	
	private int id;
	
	private String name;
	private String avatar;
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getCertification() {
		return certification;
	}

	public void setCertification(String certification) {
		this.certification = certification;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}


	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public InviteMesageStatus getStatus() {
		return status;
	}

	public void setStatus(InviteMesageStatus status) {
		this.status = status;
	}

	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}



	public enum InviteMesageStatus{
		/**�Է�����*/
		BEAPPLYED,
		/**��ͬ���˶Է�������*/
		AGREED,
		/**�Ҿܾ��˶Է�������*/
		REFUSED,
		/**������*/
		BEINVITEED,
		/**���ܾ�*/
		BEREFUSED,
		/**�Է�ͬ��*/
		BEAGREED,
		/**����*/
		INVITE
		
		
	}
	
}



