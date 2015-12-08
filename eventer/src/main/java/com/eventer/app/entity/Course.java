
package com.eventer.app.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Course implements Parcelable {

	@Override
	public String toString() {
		return "Course [classid=" + classid + ", classname=" + classname
				+ ", teacher=" + teacher + ", loction=" + loction + ", time="
				+ time + ", week=" + week + ", status=" + status + ", day="
				+ day + "]";
	}
	private int classid;
	private String classname;
	private String teacher;
	private String loction;
	private String time;
	private String week;
	private String Info;
	private String faculty;
	private String major;
	private String s_class;
	private String grade;
	private int extra_ID;
	private int status;
	private int day;
	public Course()
    {
        
    }
	
	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getS_class() {
		return s_class;
	}

	public void setS_class(String s_class) {
		this.s_class = s_class;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public int getExtra_ID() {
		return extra_ID;
	}
	public void setExtra_ID(int extra_ID) {
		this.extra_ID = extra_ID;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getInfo() {
		return Info;
	}
	public void setInfo(String info) {
		Info = info;
	}
	public int getClassid() {
		return classid;
	}
	public void setClassid(int classid) {
		this.classid = classid;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	public String getLoction() {
		return loction;
	}
	public void setLoction(String loction) {
		this.loction = loction;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		 out.writeInt(classid);
		 out.writeString(classname);
	     out.writeString(teacher);	     
	     out.writeString(loction);
	     out.writeString(time);
	     out.writeString(week);	     
	     out.writeString(Info);
	     out.writeInt(extra_ID);
	     out.writeInt(day);
	     out.writeInt(status);
	     out.writeString(faculty);
	     out.writeString(grade);
	     out.writeString(major);
	     out.writeString(s_class);
	}
	
	public static final Parcelable.Creator<Course> CREATOR = new Creator<Course>()
	 {

				@Override
				public Course createFromParcel(Parcel source) {
					// TODO Auto-generated method stub
					return new Course(source);
				}

				@Override
				public Course[] newArray(int size) {
					// TODO Auto-generated method stub
					return new Course[size];
				}
		        
	 };
		    
	 public Course(Parcel in)
	    {
		    classid = in.readInt();
	        classname = in.readString();
	        teacher = in.readString();
	        loction = in.readString();
	        time = in.readString();
	        week = in.readString();
	        Info = in.readString();
	        extra_ID=in.readInt();
	        day = in.readInt();
	        status = in.readInt();
	        faculty=in.readString();
	        grade=in.readString();
	        major=in.readString();
	        s_class=in.readString();
	    }
}
