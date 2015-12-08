 
package com.eventer.app.entity;


public class UserDetail extends UserInfo{
	private int userrank;
	private String grade;
    private String school;
    private String major;
    private String c_class;
    private String sex;
    private String email;
	public int getUserrank() {
		return userrank;
	}
	public void setUserrank(int userrank) {
		this.userrank = userrank;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public String getC_class() {
		return c_class;
	}
	public void setC_class(String c_class) {
		this.c_class = c_class;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
    
    
}
