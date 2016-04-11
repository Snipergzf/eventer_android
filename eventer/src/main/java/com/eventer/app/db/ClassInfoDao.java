
package com.eventer.app.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.eventer.app.R;
import com.eventer.app.entity.ClassInfo;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("DefaultLocale")
@SuppressWarnings({"UnusedDeclaration"})
public class ClassInfoDao {
	public static final String TABLE_NAME = "dbCourse";
	public static final String COLUMN_NAME_ID = "CourseID";
	public static final String COLUMN_NAME_NAME = "CourseName";
	public static final String COLUMN_NAME_PLACE = "Place";
	public static final String COLUMN_NAME_WEEK = "Week";
	public static final String COLUMN_NAME_STATUS = "Status";
	public static final String COLUMN_NAME_WEEKDAY = "Weekday";
	public static final String COLUMN_NAME_START= "StratClass";
	public static final String COLUMN_NAME_LEN = "ClassNum";


	//private DbOpenHelper dbHelper;
	private DBManager dbHelper;
	private Context context;

	public ClassInfoDao(Context context) {
		dbHelper = new DBManager(context);
		this.context= context;
		//dbHelper = DbOpenHelper.getInstance(context);
	}


	@SuppressLint("DefaultLocale")
	public List<ClassInfo> getClassInfoList() {
		List<ClassInfo> classlist=new ArrayList<>();
		dbHelper.openDatabase();
		//dbHelper.deleteDatabase(context);
		Cursor c=dbHelper.findList(true, TABLE_NAME, null,
				null, null, null, null,null,null);
		while (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex(COLUMN_NAME_ID));
			String name = c.getString(c.getColumnIndex(COLUMN_NAME_NAME));
			String place = c.getString(c.getColumnIndex(COLUMN_NAME_PLACE ));
			String week = c.getString(c.getColumnIndex(COLUMN_NAME_WEEK));
			int kcweekday,kcStart,kcLen;
			kcweekday=c.getInt(c.getColumnIndex(COLUMN_NAME_WEEKDAY));
			kcStart=c.getInt(c.getColumnIndex(COLUMN_NAME_START));
			kcLen=c.getInt(c.getColumnIndex(COLUMN_NAME_LEN));


			List<Integer> wlist=new ArrayList<>();
			String[] weeklist=week.split(";");
			for (String string : weeklist) {
				String[] weekSpan=string.split("-");
				int weekStart=Integer.parseInt(weekSpan[0]);
				int weekEnd=weekStart;
				if(weekSpan.length!=1){
					weekEnd=Integer.parseInt(weekSpan[1]);
				}

				for(int i=weekStart;i<weekEnd+1;i++){
					if(!wlist.contains(i))
						wlist.add(i);
				}
			}
			ClassInfo cinfo=new ClassInfo();
			cinfo.setClassid(id);
			cinfo.setClassname(name);
			cinfo.setClassRoom(place);
			cinfo.setWeekday(kcweekday);
			cinfo.setFromClassNum(kcStart);
			cinfo.setClassNumLen(kcLen);
			cinfo.setWeeks(wlist);
			classlist.add(cinfo);
		}
		dbHelper.closeDatabase();
		return classlist;
	}

	private int getWeekday(String weekday){
		String[] weeks=context.getResources().getStringArray(R.array.weeks);
		int len=weeks.length;
		int week=0;
		for(int i=0;i<len;i++){
			if(weeks[i].equals(weekday))
				week=i;
		}
		return week;
	}

}
