 
package com.eventer.app.entity;

@SuppressWarnings({"UnusedDeclaration"})
public class User extends UserInfo{
	private int unreadMsgCount;
	private String header;
    private String sex;
    private String tel;
    private String fxid;
    private String region;
    private String sign;
    private String beizhu;
    private String user_rank;
 
    
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}

	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}

    public void setTel(String tel){
        this.tel=tel;
    }
    public String getTel(){
        return tel;
    }
    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return sex;
    }
    public void setFxid(String fxid) {
        this.fxid = fxid;
    }

    public String getFxid() {
        return fxid;
    }
    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }
    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getBeizhu() {
        return beizhu;
    }

	
}
