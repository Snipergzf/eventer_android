 
package com.eventer.app.db;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eventer.app.entity.Comment;
import com.eventer.app.entity.Event;

@SuppressLint("DefaultLocale")
public class CommentDao {
	public static final String TABLE_NAME = "dbComment";
	public static final String COLUMN_NAME_ID = "commentId";
	public static final String COLUMN_NAME_EVNET = "eventId";
	public static final String COLUMN_NAME_TIME = "time";
    public static final String COLUMN_NAME_CONTENT = "content";
    public static final String COLUMN_NAME_SPEAKER = "userId";

	private DBManager dbHelper;
	private Context context;

	public CommentDao(Context context) {
		dbHelper = new DBManager(context);
		this.context= context;
	}
	
	

	public List<Comment> getCommentList(String eventID) {
		 List<Comment> list=new ArrayList<Comment>();
		 SQLiteDatabase db=dbHelper.getWritableDatabase();
		 if(db.isOpen()){
			 Cursor c=db.query(true, TABLE_NAME, null,COLUMN_NAME_EVNET+"=?",
		    			 new String[]{eventID}, null, null,COLUMN_NAME_TIME+" desc",null);	
	        while (c.moveToNext()) {
	        	String id = c.getString(c.getColumnIndex(COLUMN_NAME_ID));
				long time = c.getLong(c.getColumnIndex(COLUMN_NAME_TIME));
				String content = c.getString(c.getColumnIndex(COLUMN_NAME_CONTENT));
				String event = c.getString(c.getColumnIndex(COLUMN_NAME_EVNET));
				String speaker= c.getString(c.getColumnIndex(COLUMN_NAME_SPEAKER ));	
				Comment info=new Comment();
				info.setEventID(event);
				info.setCommentID(id);
				info.setContent(content);
				info.setSpeaker(speaker);
				info.setTime(time);			
				list.add(info);
	        }
		 }
	     dbHelper.closeDatabase();	
		return list;
	}
	
	public boolean deleteRecord(){
		dbHelper.openDatabase();
		boolean result=dbHelper.delete(TABLE_NAME, null, null);   
        dbHelper.closeDatabase();
        return result;
	}

	public boolean deleteComment(String deleteCondition,String[] deleteArgs){
		dbHelper.openDatabase();
		boolean result=dbHelper.delete(TABLE_NAME, deleteCondition, deleteArgs);   
        dbHelper.closeDatabase();
        return result;
	}
	
	public boolean deleteComment(String commentId){
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		int result=-1;
		if(db.isOpen()){
			result=db.delete(TABLE_NAME, COLUMN_NAME_ID+"=?", new String[]{commentId}); 
		}		  
        dbHelper.closeDatabase();
        return result>0;
	}

	public void saveComment(Comment comment) {
		// TODO Auto-generated method stub
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		if(db.isOpen()){
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_NAME_CONTENT, comment.getContent());
			cv.put(COLUMN_NAME_ID,  comment.getCommentID());
			cv.put(COLUMN_NAME_EVNET, comment.getEventID());
			cv.put(COLUMN_NAME_SPEAKER, comment.getSpeaker());
			cv.put(COLUMN_NAME_TIME, comment.getTime());			
			db.insert(TABLE_NAME, null,cv);
		}
	}


	public List<String> getEventIDList() {
		 dbHelper.openDatabase();
		 List<String> list=new ArrayList<String>();
		 Cursor c=dbHelper.findList(true, TABLE_NAME, new String[]{COLUMN_NAME_ID},
	    			null, null, null, null,null,null);
	      while (c.moveToNext()) {
	        	int id = c.getInt(c.getColumnIndex(COLUMN_NAME_ID));
	        	Log.e("1", id+"");
				list.add(id+"");
	     }
	    dbHelper.closeDatabase();	
		return list;
	}
	
	
	
}
