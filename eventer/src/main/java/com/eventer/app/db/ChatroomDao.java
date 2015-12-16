
package com.eventer.app.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.eventer.app.entity.ChatRoom;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("DefaultLocale")
public class ChatroomDao {
	public static final String TABLE_NAME = "dbChatRoom";
	public static final String COLUMN_NAME_ID = "chatroomname";
	public static final String COLUMN_NAME_TIME = "modifytime";
	public static final String COLUMN_NAME_MEMVBER = "memberlist";
	public static final String COLUMN_NAME_MEMBERNAME = "displayname";
	public static final String COLUMN_NAME_OWNER = "roomowner";
	public static final String COLUMN_NAME_ROOMNAME = "roomdisplayname";

	private DBManager dbHelper;
	Context context;

	public ChatroomDao(Context context) {
		dbHelper = new DBManager(context);
		this.context= context;
	}






	/**
	 * 保存User
	 */
	public synchronized void saveChatROOM(ChatRoom room){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID+ " = ?", new String[]{room.getRoomId()});
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME_ID, room.getRoomId());
			values.put(COLUMN_NAME_TIME, room.getTime());
			values.put(COLUMN_NAME_MEMVBER,ListToString(room.getMember()));
			values.put(COLUMN_NAME_OWNER, room.getOwner());
			values.put(COLUMN_NAME_ROOMNAME, room.getRoomname());
			values.put(COLUMN_NAME_MEMBERNAME, ListToString(room.getDisplayname()));
			db.insert(TABLE_NAME, null, values);
		}db.close();
	}

	public synchronized void update(ContentValues values,String room){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		boolean isSuccess;
		if(db.isOpen()){
			isSuccess=db.update(TABLE_NAME, values,COLUMN_NAME_ID+ " = ?", new String[]{room})>-1;
			if(!isSuccess){
				values.put(COLUMN_NAME_ID,room);
				String owner=room.split("@")[0];
				values.put(COLUMN_NAME_OWNER, owner);
				db.insert(TABLE_NAME, null, values);
			}
		}
		db.close();
	}


	private String ListToString(String[] list){
		String str="";
		for(int i=0;i<list.length;i++){
			if(i<list.length-1){
				str+=list[i]+",";
			}else{
				str+=list[i];
			}
		}
		return str;
	}



	public List<ChatRoom> getRoomList() {
		// TODO Auto-generated method stub
		List<ChatRoom> list=new ArrayList<>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			Cursor c=db.query(TABLE_NAME, null,  null, null, null, null,null);
			while (c.moveToNext()) {
				ChatRoom room=new ChatRoom();
				room.setRoomId(c.getString(c.getColumnIndex(COLUMN_NAME_ID)));
				room.setOwner(c.getString(c.getColumnIndex(COLUMN_NAME_OWNER)));
				room.setTime(c.getLong(c.getColumnIndex(COLUMN_NAME_TIME)));
				String member=c.getString(c.getColumnIndex(COLUMN_NAME_MEMVBER));
				room.setMember(member.split(","));
				String displayname=c.getString(c.getColumnIndex(COLUMN_NAME_MEMBERNAME));
				room.setDisplayname(displayname.split(","));
				String roomname=c.getString(c.getColumnIndex(COLUMN_NAME_ROOMNAME));
				if(!TextUtils.isEmpty(roomname)){
					room.setRoomname(roomname);
				}else{
					room.setRoomname(displayname);
				}
				list.add(room);
			}
			c.close();
		}
		return list;
	}


	public ChatRoom getRoom(String talker) {
		// TODO Auto-generated method stub
		ChatRoom room=new ChatRoom();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			//db.rawQuery(sql, selectionArgs);
			Cursor c=db.query(TABLE_NAME, null,  COLUMN_NAME_ID+ " = ?", new String[]{talker}, null, null,null);
			while (c.moveToNext()) {
				room.setRoomId(talker);
				room.setOwner(c.getString(c.getColumnIndex(COLUMN_NAME_OWNER)));
				room.setTime(c.getLong(c.getColumnIndex(COLUMN_NAME_TIME)));
				String member=c.getString(c.getColumnIndex(COLUMN_NAME_MEMVBER));
				String[] memberlist=member.split(",");
				room.setMember(memberlist);
				String displayname=c.getString(c.getColumnIndex(COLUMN_NAME_MEMBERNAME));
				room.setDisplayname(displayname.split(","));
				String roomname=c.getString(c.getColumnIndex(COLUMN_NAME_ROOMNAME));
				if(!TextUtils.isEmpty(roomname)){
					room.setRoomname(roomname);
				}else{
					room.setRoomname(displayname+"("+memberlist.length+")");
				}
			}
			c.close();
		}
		return room;
	}

}
