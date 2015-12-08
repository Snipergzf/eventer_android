
package com.eventer.app.db;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.eventer.app.entity.Event;

@SuppressLint("DefaultLocale")
public class EventDao {
	public static final String TABLE_NAME = "dbEventDetail";
	public static final String COLUMN_NAME_ID = "Id";
	public static final String COLUMN_NAME_PUBTIME = "pubtime";
	public static final String COLUMN_NAME_TYPE = "type";
	public static final String COLUMN_NAME_TITLE = "title";
	public static final String COLUMN_NAME_SOURCE = "source";
	public static final String COLUMN_NAME_SOURCE_ICON = "source_icon";
	public static final String COLUMN_NAME_CONTENT = "content";
	public static final String COLUMN_NAME_STATUS = "status";
	public static final String COLUMN_NAME_OPERATION = "operation";
	public static final String COLUMN_NAME_OPERATOR = "operator";
	public static final String COLUMN_NAME_OPERATE_TIME = "operate_time";
	public static final String COLUMN_NAME_COVER = "cover";
	public static final String COLUMN_NAME_URL = "url";
	public static final String COLUMN_NAME_READCOUNT = "readCount";
	public static final String COLUMN_NAME_UPCOUNT = "upCount";
	public static final String COLUMN_NAME_DOWNCOUNT = "downCount";
	public static final String COLUMN_NAME_TIME = "time";
	public static final String COLUMN_NAME_MYOPERATION = "myOperation";
	public static final String COLUMN_NAME_THEME = "theme";
	public static final String COLUMN_NAME_PLACE = "place";


	//private DbOpenHelper dbHelper;
	private DBManager dbHelper;
	private Context context;

	public EventDao(Context context) {
		dbHelper = new DBManager(context);
		this.context = context;
		//dbHelper = DbOpenHelper.getInstance(context);
	}


	@SuppressLint("DefaultLocale")
	public List<Event> getEventList() {
		List<Event> list = new ArrayList<Event>();
		dbHelper.openDatabase();

		//dbHelper.deleteDatabase(context);
		Cursor c=dbHelper.findList(true, TABLE_NAME, null,
				null, null, null, null,COLUMN_NAME_PUBTIME+" desc",null);
		while (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex(COLUMN_NAME_ID));
			String title = c.getString(c.getColumnIndex(COLUMN_NAME_TITLE));
			String publisher = c.getString(c.getColumnIndex(COLUMN_NAME_SOURCE ));
			String content = c.getString(c.getColumnIndex(COLUMN_NAME_CONTENT ));
			String theme= c.getString(c.getColumnIndex(COLUMN_NAME_THEME ));
			String time= c.getString(c.getColumnIndex(COLUMN_NAME_TIME ));
			String place=c.getString(c.getColumnIndex(COLUMN_NAME_PLACE));
			long pubtime = c.getLong(c.getColumnIndex(COLUMN_NAME_PUBTIME));
			//				int readCount=-1,upCount=-1,downCount=-1;
			//				readCount=c.getInt(c.getColumnIndex(COLUMN_NAME_READCOUNT));
			//				upCount=c.getInt(c.getColumnIndex(COLUMN_NAME_UPCOUNT));
			//				downCount=c.getInt(c.getColumnIndex(COLUMN_NAME_DOWNCOUNT));


			Event info=new Event();
			info.setEventID(id);
			info.setTitle(title);
			info.setPublisher(publisher);
			info.setIssueTime(pubtime);
			info.setContent(content);
			info.setTheme(theme);
			info.setTime(time);
			info.setPlace(place);
			//				info.setReadCount(readCount);
			//				info.setUpCount(upCount);
			//				info.setDownCount(downCount);
			list.add(info);
		}
		dbHelper.closeDatabase();
		return list;
	}

	@SuppressLint("DefaultLocale")
	public Event getEvent(String eid) {
		Event info=null;
		dbHelper.openDatabase();

		//dbHelper.deleteDatabase(context);
		Cursor c=dbHelper.findList(true, TABLE_NAME, null,
				COLUMN_NAME_ID+"=?", new String[]{eid}, null, null,null,null);
		while (c.moveToNext()) {
			info=new Event();
			String id = c.getString(c.getColumnIndex(COLUMN_NAME_ID));
			String title = c.getString(c.getColumnIndex(COLUMN_NAME_TITLE));
			String publisher = c.getString(c.getColumnIndex(COLUMN_NAME_SOURCE ));
			String content = c.getString(c.getColumnIndex(COLUMN_NAME_CONTENT ));
			String theme= c.getString(c.getColumnIndex(COLUMN_NAME_THEME ));
			String time= c.getString(c.getColumnIndex(COLUMN_NAME_TIME ));
			String place=c.getString(c.getColumnIndex(COLUMN_NAME_PLACE));
			long pubtime = c.getLong(c.getColumnIndex(COLUMN_NAME_PUBTIME));

			info.setEventID(id);
			info.setTitle(title);
			info.setPublisher(publisher);
			info.setIssueTime(pubtime);
			info.setContent(content);
			info.setTheme(theme);
			info.setTime(time);
			info.setPlace(place);

		}
		dbHelper.closeDatabase();
		return info;
	}

	public boolean deleteRecord(){
		dbHelper.openDatabase();
		boolean result=dbHelper.delete(TABLE_NAME, null, null);
		dbHelper.closeDatabase();
		return result;
	}

	public boolean deleteRecord(String deleteCondition,String[] deleteArgs){
		dbHelper.openDatabase();
		boolean result=dbHelper.delete(TABLE_NAME, deleteCondition, deleteArgs);
		dbHelper.closeDatabase();
		return result;
	}

	public void saveEvent(Event event) {
		// TODO Auto-generated method stub
		dbHelper.openDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NAME_CONTENT, event.getContent());
		cv.put(COLUMN_NAME_ID,  event.getEventID());
		cv.put(COLUMN_NAME_PLACE, event.getPlace());
		cv.put(COLUMN_NAME_SOURCE, event.getPublisher());
		cv.put(COLUMN_NAME_THEME, event.getTheme());
		cv.put(COLUMN_NAME_TITLE, event.getTitle());
		cv.put(COLUMN_NAME_PUBTIME, event.getIssueTime());
		cv.put(COLUMN_NAME_TIME, event.getTime());
		dbHelper.insert(TABLE_NAME, cv);
		dbHelper.closeDatabase();
	}


	public List<String> getEventIDList() {
		dbHelper.openDatabase();
		List<String> list=new ArrayList<String>();
		Cursor c=dbHelper.findList(true, TABLE_NAME, new String[]{COLUMN_NAME_ID},
				null, null, null, null,null,null);
		while (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex(COLUMN_NAME_ID));
			list.add(id);
		}
		dbHelper.closeDatabase();
		return list;
	}

	//	public List<Event> getEventListByInfo(String[] args){
	//		List<Event> list=new ArrayList<Event>();
	//		 dbHelper.openDatabase();
	//		 //a.[operator]=? and a.[operation]=?;
	//		 Cursor c=dbHelper.rawQuery("select b.*,a.operator,a.operation,a.operate_time from dbEvent a,dbEventDetail b where a.Id=b.Id and a.operator=? and a.operation=?",
	//				 args);
	//	      while (c.moveToNext()) {
	//	    	    String id = c.getString(c.getColumnIndex(COLUMN_NAME_ID));
	//			 	String title = c.getString(c.getColumnIndex(COLUMN_NAME_TITLE));
	//				String publisher = c.getString(c.getColumnIndex(COLUMN_NAME_SOURCE ));
	//				String content = c.getString(c.getColumnIndex(COLUMN_NAME_CONTENT ));
	//				String theme= c.getString(c.getColumnIndex(COLUMN_NAME_THEME ));
	//				String time= c.getString(c.getColumnIndex(COLUMN_NAME_TIME ));				
	//				long pubtime = c.getLong(c.getColumnIndex(COLUMN_NAME_PUBTIME));
	//				String operator=c.getString(c.getColumnIndex(EventOpDao.COLUMN_NAME_OPERATOR));
	//				int operation=c.getInt(c.getColumnIndex(EventOpDao.COLUMN_NAME_OPERATION));
	//				int operatetime=c.getInt(c.getColumnIndex(EventOpDao.COLUMN_NAME_OPERATE_TIME));
	//				
	//				Event info=new Event();
	//				info.setEventID(id);
	//				info.setTitle(title);
	//				info.setPublisher(publisher);
	//				info.setIssueTime(pubtime);
	//				info.setContent(content);
	//				info.setTheme(theme);
	//				info.setTime(time);
	//				info.setOperation(operation);
	//				info.setOperator(operator);		
	//				info.setOpTime(operatetime);
	//				list.add(info);
	//	     }
	//	    dbHelper.closeDatabase();	
	//		return list;
	//	}

	public List<Event> getEventListByInfo(String[] args){
		List<Event> list=new ArrayList<Event>();
		dbHelper.openDatabase();
		//a.[operator]=? and a.[operation]=?;
		Cursor c=dbHelper.rawQuery("select b.*,a.operator,a.operation,a.operate_time from dbEvent a,dbEventDetail b where a.Id=b.Id and a.operator=? and a.operation=? order by operate_time desc",
				args);
		while (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex(COLUMN_NAME_ID));
			String title = c.getString(c.getColumnIndex(COLUMN_NAME_TITLE));
			String publisher = c.getString(c.getColumnIndex(COLUMN_NAME_SOURCE ));
			String content = c.getString(c.getColumnIndex(COLUMN_NAME_CONTENT ));
			String theme= c.getString(c.getColumnIndex(COLUMN_NAME_THEME ));
			String time= c.getString(c.getColumnIndex(COLUMN_NAME_TIME ));
			long pubtime = c.getLong(c.getColumnIndex(COLUMN_NAME_PUBTIME));
			String operator=c.getString(c.getColumnIndex(EventOpDao.COLUMN_NAME_OPERATOR));
			int operation=c.getInt(c.getColumnIndex(EventOpDao.COLUMN_NAME_OPERATION));
			int operatetime=c.getInt(c.getColumnIndex(EventOpDao.COLUMN_NAME_OPERATE_TIME));

			Event info=new Event();
			info.setEventID(id);
			info.setTitle(title);
			info.setPublisher(publisher);
			info.setIssueTime(pubtime);
			info.setContent(content);
			info.setTheme(theme);
			info.setTime(time);
			info.setOperation(operation);
			info.setOperator(operator);
			info.setOpTime(operatetime);
			list.add(info);
		}
		dbHelper.closeDatabase();
		return list;
	}


}
