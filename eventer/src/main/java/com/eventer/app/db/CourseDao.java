
package com.eventer.app.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.eventer.app.entity.Course;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressLint("DefaultLocale")
@SuppressWarnings({"UnusedDeclaration"})
public class CourseDao {
	public static final String TABLE_NAME = "dbCourse";
	public static final String COLUMN_NAME_ID = "CourseID";
	public static final String COLUMN_NAME_NAME = "CourseName";
	public static final String COLUMN_NAME_PLACE = "Place";
	public static final String COLUMN_NAME_WEEK = "Week";
	public static final String COLUMN_NAME_STATUS = "Status";
	public static final String COLUMN_NAME_WEEKDAY = "Weekday";
	public static final String COLUMN_NAME_START= "StratClass";
	public static final String COLUMN_NAME_LEN = "ClassNum";
	public static final String COLUMN_NAME_INFO="CourseInfo";


	private DBManager dbHelper;

	public CourseDao(Context context) {
		dbHelper = new DBManager(context);
	}


	public List<Course> getCourseList(String id) {
		// TODO Auto-generated method stub
		List<Course> list=new ArrayList<>();
		dbHelper.openDatabase();
		Cursor c=dbHelper.findList(true, TABLE_NAME, null,
				COLUMN_NAME_ID+"=?", new String[]{id}, null, null,"extra_Id",null);
		while (c.moveToNext()) {
			int classid = c.getInt(c.getColumnIndex(COLUMN_NAME_ID));
			String name = c.getString(c.getColumnIndex(COLUMN_NAME_NAME));
			String place = c.getString(c.getColumnIndex(COLUMN_NAME_PLACE ));
			String teacher = c.getString(c.getColumnIndex("Teacher"));
			String week = c.getString(c.getColumnIndex(COLUMN_NAME_WEEK));
			int ex_id = c.getInt(c.getColumnIndex("extra_Id"));
			int kcweekday,kcStart,kcLen;
			kcweekday=c.getInt(c.getColumnIndex(COLUMN_NAME_WEEKDAY));
			kcStart=c.getInt(c.getColumnIndex(COLUMN_NAME_START));
			kcLen=c.getInt(c.getColumnIndex(COLUMN_NAME_LEN));
			Course cinfo=new Course();
			cinfo.setClassid(classid);
			cinfo.setClassname(name);
			cinfo.setTeacher(teacher);
			cinfo.setLoction(place);
			cinfo.setWeek(week);
			cinfo.setDay(kcweekday);
			cinfo.setTime(kcStart+"-"+(kcStart+kcLen-1));
			cinfo.setExtra_ID(ex_id);
			list.add(cinfo);
		}
		dbHelper.closeDatabase();
		return list;
	}



	public void saveCourseByInfo(Course course) {
		List<Course> list=getTimeList(course);
		saveCourseList(list);
	}

	private List<Course> getTimeList(Course course) {
		// TODO Auto-generated method stub
		List<Course> list=new ArrayList<>();
		String teacher=course.getTeacher();
		String c_name=course.getClassname();
		int classid=course.getClassid();

		try {
			JSONObject json=new JSONObject(course.getInfo());
			Iterator<String> it=json.keys();
			int index=1;
			while(it.hasNext()){
				Course c=new Course();
				JSONObject detail=json.getJSONObject(it.next());
				c.setClassname(c_name);
				c.setTeacher(teacher);
				c.setClassid(classid);
				c.setLoction(detail.getString("place"));
				c.setTime(detail.getString("time"));
				c.setWeek(detail.getString("week"));
				c.setDay(detail.getInt("day"));
				c.setExtra_ID(index);
				list.add(c);
				index++;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public void deleteCourse(int classid) {
		// TODO Auto-generated method stub
		dbHelper.openDatabase();
		dbHelper.delete("dbCourse","CourseID=?",new String[]{classid+"" });
		dbHelper.closeDatabase();
	}

	public List<Integer> getCourseIdList(){
		dbHelper.openDatabase();
		List<Integer> list=new ArrayList<>();
		Cursor c=dbHelper.findList(true, TABLE_NAME,new String[]{COLUMN_NAME_ID},null,null,null,null,null, null);
		while (c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex(COLUMN_NAME_ID));
			if(id>0){
				list.add(id);
			}
		}
		dbHelper.closeDatabase();
		return list;
	}


	public void saveCourseList(List<Course> list) {
		// TODO Auto-generated method stub
		dbHelper.openDatabase();
		//dbHelper.deleteDatabase(context);
		saveList(list);
		dbHelper.closeDatabase();
	}

	public void saveList(List<Course> list){
		//dbHelper.deleteDatabase(context);
		int index=0;
		for (Course course : list) {
			String c_week=course.getWeek();
			String c_time=course.getTime();
			if(!TextUtils.isEmpty(c_time)&&!TextUtils.isEmpty(c_week)){
				ContentValues cv = new ContentValues();
				cv.put("CourseName", course.getClassname());
				cv.put("Place", course.getLoction());
				cv.put("Teacher", course.getTeacher());
				cv.put("Week", c_week);
				cv.put("Weekday",course.getDay());
				cv.put("CourseID", course.getClassid());
				cv.put("extra_Id",index);
				String[] time=c_time.split("-");
				if(time.length==1){
					cv.put("StratClass",Integer.parseInt(time[0]));
					cv.put("ClassNum",1);
				}else if(time.length==2){
					cv.put("StratClass",Integer.parseInt(time[0]));
					cv.put("ClassNum",Integer.parseInt(time[1])-Integer.parseInt(time[0])+1);
				}
				dbHelper.insert("dbCourse", cv);
				index++;
			}
		}
	}


	public void saveCourseListByInfo(List<Course> list) {
		// TODO Auto-generated method stub
		dbHelper.openDatabase();
		//dbHelper.deleteDatabase(context);
		for (Course course : list) {
			List<Course> clist=getTimeList(course);
			saveList(clist);
		}
		dbHelper.closeDatabase();
	}


	public boolean deleteAllCourse(){
		dbHelper.openDatabase();
		boolean result=dbHelper.delete(TABLE_NAME, null, null);
		dbHelper.closeDatabase();
		return result;
	}


}
