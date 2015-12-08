
package com.eventer.app.db;

import java.util.ArrayList;
import java.util.List;

import android.R.bool;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eventer.app.Constant;
import com.eventer.app.entity.Event;
import com.eventer.app.entity.EventOp;

@SuppressLint("DefaultLocale")
public class EventOpDao {
	public static final String TABLE_NAME = "dbEvent";
	public static final String COLUMN_NAME_ID = "Id";
	public static final String COLUMN_NAME_OPERATION = "operation";
	public static final String COLUMN_NAME_OPERATOR = "operator";
	public static final String COLUMN_NAME_OPERATE_TIME = "operate_time";



	//private DbOpenHelper dbHelper;
	private DBManager dbHelper;

	public EventOpDao(Context context) {
		dbHelper = new DBManager(context);

	}


	@SuppressLint("DefaultLocale")
	public List<Event> getEventOpList() {
		List<Event> list=new ArrayList<Event>();
		dbHelper.openDatabase();

		//dbHelper.deleteDatabase(context);
		Cursor c=dbHelper.findList(true, TABLE_NAME, null,
				null, null, null, null,COLUMN_NAME_OPERATE_TIME+" desc",null);
		while (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex(COLUMN_NAME_ID));
			Event info=new Event();
			info.setEventID(id);
			list.add(info);
		}
		dbHelper.closeDatabase();
		return list;
	}

	public boolean getIsVisit(String eid) {
		boolean isVisit=false;
		dbHelper.openDatabase();
		//dbHelper.deleteDatabase(context);
		Cursor c=dbHelper.findList(true, TABLE_NAME, null,
				COLUMN_NAME_ID+"=? and "+COLUMN_NAME_OPERATOR+"=?", new String[]{eid,Constant.UID+""}, null, null,
				null,null);
		while (c.moveToNext()) {
			isVisit=true;
			break;
		}
		dbHelper.closeDatabase();
		return isVisit;
	}

	public boolean getIsCollect(String eid) {
		dbHelper.openDatabase();
		//dbHelper.deleteDatabase(context);
		Cursor c=dbHelper.findList(true, TABLE_NAME, null,
				COLUMN_NAME_ID+"=? and "+COLUMN_NAME_OPERATOR+"=?", new String[]{eid,Constant.UID+""}, null, null,
				null,null);
		while (c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex(COLUMN_NAME_OPERATION));
			if(id==1){
				return true;
			}
		}
		dbHelper.closeDatabase();
		return false;
	}

	public void cancelCollect(String eid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID+ " = ? and "+COLUMN_NAME_OPERATOR+ " = ? and "+COLUMN_NAME_OPERATION+"=?" , new String[]{eid,Constant.UID,"1"});
		}
	}

	public boolean deleteRecord(){
		dbHelper.openDatabase();
		boolean result=dbHelper.delete(TABLE_NAME, null, null);
		dbHelper.closeDatabase();
		return result;
	}

	public void delByEventId(String eventId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID+ " = ?", new String[]{eventId});
		}
	}

	public void delCancelCollect(String eventId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID+ " = ? and "+COLUMN_NAME_OPERATION+"=?", new String[]{eventId,"1"});
		}
	}

	public void delOperation(String uid,String op){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_OPERATOR+ " = ? and "+COLUMN_NAME_OPERATION+"=?", new String[]{uid,op});
		}
	}

	public void saveEventOp(EventOp o) {
		// TODO Auto-generated method stub
		dbHelper.openDatabase();
//		boolean result=dbHelper.delete(TABLE_NAME, COLUMN_NAME_ID+"=? and "+COLUMN_NAME_OPERATOR+"=?", 
//				new String[]{o.getEventID()+"",o.getOperator()+""});   
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NAME_ID,  o.getEventID());
		cv.put(COLUMN_NAME_OPERATE_TIME,  o.getOpTime());
		cv.put(COLUMN_NAME_OPERATION,  o.getOperation());
		cv.put(COLUMN_NAME_OPERATOR,  o.getOperator());
		dbHelper.insert(TABLE_NAME, cv);
		dbHelper.closeDatabase();
	}


	public List<String> getEventIDList() {
		dbHelper.openDatabase();
		List<String> list=new ArrayList<String>();
		Cursor c=dbHelper.findList(true, TABLE_NAME, new String[]{COLUMN_NAME_ID},
				null, null, null, null,null,null);
		while (c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex(COLUMN_NAME_ID));
			list.add(id+"");
		}
		dbHelper.closeDatabase();
		return list;
	}


	public boolean deleteRecord(String deleteCondition,String[] deleteArgs){
		dbHelper.openDatabase();
		boolean result=dbHelper.delete(TABLE_NAME, deleteCondition, deleteArgs);
		dbHelper.closeDatabase();
		return result;
	}

}
