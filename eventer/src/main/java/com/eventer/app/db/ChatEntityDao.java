
package com.eventer.app.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.eventer.app.entity.ChatEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("DefaultLocale")
@SuppressWarnings({"UnusedDeclaration"})
public class ChatEntityDao {
	public static final String TABLE_NAME = "dbMessage";
	public static final String COLUMN_NAME_ID = "MsgId";
	public static final String COLUMN_NAME_FROM = "talker";
	public static final String COLUMN_NAME_STATUS = "status";
	public static final String COLUMN_NAME_TYPE = "MsgType";
	public static final String COLUMN_NAME_CONTENT= "MsgContent";
	public static final String COLUMN_NAME_PATH = "ImgPath";
	public static final String COLUMN_NAME_TIME = "addTime";
	public static final String COLUMN_NAME_SHARE="shareId";


	//private DbOpenHelper dbHelper;
	private DBManager dbHelper;
//	private Context context;

	public ChatEntityDao(Context context) {

		dbHelper = new DBManager(context);
	}


	@SuppressLint("DefaultLocale")
	public List<ChatEntity> getChatEntityList(String[] columns,String selection,String[] selectionArgs,String groupBy,String OrderBy) {
		List<ChatEntity> list=new ArrayList<>();
		dbHelper.openDatabase();
		//dbHelper.deleteDatabase(context);
		Cursor c=dbHelper.findList(true, TABLE_NAME, columns,
				selection, selectionArgs, groupBy, null,OrderBy,null);
		while (c.moveToNext()) {
			long id = c.getLong(c.getColumnIndex(COLUMN_NAME_ID));
			String talker = c.getString(c.getColumnIndex(COLUMN_NAME_FROM));
			String path = c.getString(c.getColumnIndex(COLUMN_NAME_PATH));
			String status=c.getString(c.getColumnIndex(COLUMN_NAME_STATUS));
			String time=c.getString(c.getColumnIndex(COLUMN_NAME_TIME));
			String cType=c.getString(c.getColumnIndex(COLUMN_NAME_TYPE));
			String content = c.getString(c.getColumnIndex(COLUMN_NAME_CONTENT ));
			String share=c.getString(c.getColumnIndex(COLUMN_NAME_SHARE));
			if(!TextUtils.isEmpty(talker)){
				int temp=c.getColumnIndex("NotRead");
				int unread=0;
				if(temp>-1){
					unread=c.getInt(temp);
				}

				ChatEntity cinfo=new ChatEntity();
				cinfo.setContent(content);
				cinfo.setFrom(talker);
				cinfo.setMsgID(id);//id为空时返回0值
				cinfo.setNotRead(unread);
				cinfo.setShareId(share);
				if(status!=null){
					cinfo.setStatus(Integer.parseInt(status));
				}else{
					cinfo.setStatus(-1);
				}
				if(cType!=null){
					cinfo.setType(Integer.parseInt(cType));
				}else{
					cinfo.setType(-1);
				}
				if(time!=null){
					cinfo.setMsgTime(Long.parseLong(time));
				}else{
					cinfo.setMsgTime(-1);
				}
				cinfo.setImgPath(path);

				list.add(cinfo);
			}

		}
		dbHelper.closeDatabase();
		return list;
	}

	@SuppressLint("DefaultLocale")
	public List<ChatEntity> getChatEntityList(String selection,String[] selectionArgs,String groupBy,String OrderBy) {
		List<ChatEntity> list=new ArrayList<>();
		dbHelper.openDatabase();
		//dbHelper.deleteDatabase(context);
		Cursor c=dbHelper.findList(true, TABLE_NAME, null,
				selection, selectionArgs, groupBy, null,OrderBy,null);
		while (c.moveToNext()) {
			long id = c.getLong(c.getColumnIndex(COLUMN_NAME_ID));
			String talker = c.getString(c.getColumnIndex(COLUMN_NAME_FROM));
			String path = c.getString(c.getColumnIndex(COLUMN_NAME_PATH));
			String status=c.getString(c.getColumnIndex(COLUMN_NAME_STATUS));
			String time=c.getString(c.getColumnIndex(COLUMN_NAME_TIME));
			String cType=c.getString(c.getColumnIndex(COLUMN_NAME_TYPE));
			String content = c.getString(c.getColumnIndex(COLUMN_NAME_CONTENT ));
			String share=c.getString(c.getColumnIndex(COLUMN_NAME_SHARE));
			int temp=c.getColumnIndex("NotRead");
			int unread=0;
			if(temp>-1){
				unread=c.getInt(temp);
			}

			ChatEntity cinfo=new ChatEntity();
			cinfo.setContent(content);
			cinfo.setFrom(talker);
			cinfo.setMsgID(id);//id为空时返回0值
			cinfo.setNotRead(unread);
			cinfo.setShareId(share);
			if(status!=null){
				cinfo.setStatus(Integer.parseInt(status));
			}else{
				cinfo.setStatus(-1);
			}
			if(cType!=null){
				cinfo.setType(Integer.parseInt(cType));
			}else{
				cinfo.setType(-1);
			}
			if(time!=null){
				cinfo.setMsgTime(Long.parseLong(time));
			}else{
				cinfo.setMsgTime(-1);
			}
			cinfo.setImgPath(path);

			list.add(cinfo);
		}
		dbHelper.closeDatabase();
		return list;
	}

	public int getUnreadMsgCount() {
		List<ChatEntity> list=new ArrayList<>();
		dbHelper.openDatabase();
		//dbHelper.deleteDatabase(context);
		Cursor c=dbHelper.findOne(true, TABLE_NAME, new String[]{"count(*) as count"},
				"status=1", null, null, null,null,null);
		int count=c.getInt(c.getColumnIndex("count"));
		dbHelper.closeDatabase();
		return count;
	}


	/**
	 * 保存一条消息
	 * @param msg save info
	 */
	public boolean saveMessage(ChatEntity msg){
		dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, msg.getMsgID());
		if (msg.getContent() != null) {
			values.put(COLUMN_NAME_CONTENT, msg.getContent());
		}
		if (msg.getFrom() != null) {
			values.put(COLUMN_NAME_FROM, msg.getFrom());
		}
		if (msg.getImgPath() != null) {
			values.put(COLUMN_NAME_PATH, msg.getImgPath());
		}
		if (msg.getMsgTime() >0 ) {
			values.put(COLUMN_NAME_TIME, msg.getMsgTime());
		}
		values.put(COLUMN_NAME_STATUS, msg.getStatus());
		values.put(COLUMN_NAME_TYPE, msg.getType());
		values.put(COLUMN_NAME_SHARE, msg.getShareId());
		long result=dbHelper.insert(TABLE_NAME, values);

		dbHelper.closeDatabase();

		return result>=0;
	}

	/**
	 * 删除一条消息
	 */
	public boolean deleteMessage(String id){
		dbHelper.openDatabase();
		boolean result=dbHelper.delete(TABLE_NAME, COLUMN_NAME_ID + " = ? ", new String[]{id});
		dbHelper.closeDatabase();
		return result;
	}

	public boolean deleteMessageByUser(String user){
		dbHelper.openDatabase();
		boolean result=dbHelper.delete(TABLE_NAME, COLUMN_NAME_FROM+" = ? ", new String[]{user});
		dbHelper.closeDatabase();
		return result;
	}

	public boolean deleteAllMsg(){
		dbHelper.openDatabase();
		boolean result=dbHelper.delete(TABLE_NAME, null, null);
		dbHelper.closeDatabase();
		return result;
	}


	public void ClearUnReadMsg(String username) {
		// TODO Auto-generated method stub
		dbHelper.openDatabase();
		ContentValues values = new ContentValues();
		values.put("status", 0);
		boolean result=dbHelper.update(TABLE_NAME, values, COLUMN_NAME_FROM + " = ? and status=?", new String[]{username, "1"});
		//dbHelper.execSQL("update dbMessage set status=1 where status=0 and ");
		dbHelper.closeDatabase();
	}


	public List<ChatEntity> getMsgList(String talker, long msgID, int pagesize) {
		// TODO Auto-generated method stub
		List<ChatEntity> list=new ArrayList<>();
		dbHelper.openDatabase();
		//dbHelper.deleteDatabase(context);
		Cursor c;
		if(msgID==0){
			c=dbHelper.findList(true, TABLE_NAME, null,
					COLUMN_NAME_FROM+"=?", new String[]{talker}, null, null,COLUMN_NAME_ID+" desc","0,"+pagesize);
		}else
			c=dbHelper.findList(true, TABLE_NAME, null,
					COLUMN_NAME_FROM+"=? and "+COLUMN_NAME_ID+"<?", new String[]{talker,msgID+""}, null, null,COLUMN_NAME_TIME+" desc","0,"+pagesize);
		while (c.moveToNext()) {
			long id = c.getLong(c.getColumnIndex(COLUMN_NAME_ID));
			String talkto = c.getString(c.getColumnIndex(COLUMN_NAME_FROM));
			String path = c.getString(c.getColumnIndex(COLUMN_NAME_PATH));
			String status=c.getString(c.getColumnIndex(COLUMN_NAME_STATUS));
			String time=c.getString(c.getColumnIndex(COLUMN_NAME_TIME));
			String cType=c.getString(c.getColumnIndex(COLUMN_NAME_TYPE));
			String content = c.getString(c.getColumnIndex(COLUMN_NAME_CONTENT ));
			String share=c.getString(c.getColumnIndex(COLUMN_NAME_SHARE));
			int temp=c.getColumnIndex("NotRead");
			int unread=0;
			if(temp>-1){
				unread=c.getInt(temp);
			}
			ChatEntity cinfo=new ChatEntity();
			cinfo.setContent(content);
			cinfo.setFrom(talkto);
			Log.e("1",id+"");
			cinfo.setMsgID(id);//id为空时返回0值
			cinfo.setNotRead(unread);
			cinfo.setShareId(share);
			if(status!=null){
				cinfo.setStatus(Integer.parseInt(status));
			}else{
				cinfo.setStatus(-1);
			}
			if(cType!=null){
				cinfo.setType(Integer.parseInt(cType));
			}else{
				cinfo.setType(-1);
			}
			if(time!=null){
				cinfo.setMsgTime(Long.parseLong(time));
			}else{
				cinfo.setMsgTime(-1);
			}
			cinfo.setImgPath(path);

			list.add(cinfo);
		}
		dbHelper.closeDatabase();
		return list;
	}


	public ChatEntity getLatestMsg(String shareId) {
		// TODO Auto-generated method stub
		ChatEntity cinfo=new ChatEntity();
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		if(db.isOpen()){
			Cursor c=db.query(true, TABLE_NAME, null,COLUMN_NAME_SHARE+"=?", new String[]{shareId}, null, null, COLUMN_NAME_TIME+" desc", "0,1");
           if(c.moveToNext()){
			   long id = c.getLong(c.getColumnIndex(COLUMN_NAME_ID));
			   String talkto = c.getString(c.getColumnIndex(COLUMN_NAME_FROM));
			   String path = c.getString(c.getColumnIndex(COLUMN_NAME_PATH));
			   String status=c.getString(c.getColumnIndex(COLUMN_NAME_STATUS));
			   String time=c.getString(c.getColumnIndex(COLUMN_NAME_TIME));
			   String cType=c.getString(c.getColumnIndex(COLUMN_NAME_TYPE));
			   String content = c.getString(c.getColumnIndex(COLUMN_NAME_CONTENT ));
			   String share=c.getString(c.getColumnIndex(COLUMN_NAME_SHARE));
			   int temp=c.getColumnIndex("NotRead");
			   int unread=0;
			   if(temp>-1){
				   unread=c.getInt(temp);
			   }
			   cinfo.setContent(content);
			   cinfo.setFrom(talkto);
			   Log.e("1",id+"");
			   cinfo.setMsgID(id);//id为空时返回0值
			   cinfo.setNotRead(unread);
			   cinfo.setShareId(share);
			   if(status!=null){
				   cinfo.setStatus(Integer.parseInt(status));
			   }else{
				   cinfo.setStatus(-1);
			   }
			   if(cType!=null){
				   cinfo.setType(Integer.parseInt(cType));
			   }else{
				   cinfo.setType(-1);
			   }
			   if(time!=null){
				   cinfo.setMsgTime(Long.parseLong(time));
			   }else{
				   cinfo.setMsgTime(-1);
			   }
			   cinfo.setImgPath(path);
		   }

			c.close();
			return cinfo;

		}
		db.close();
		return null;
	}


	public List<String> getShareList(String groupId) {
		// TODO Auto-generated method stub
		List<String> list=new ArrayList<>();
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		if(db.isOpen()){
			Cursor c=db.query(true, TABLE_NAME, new String[]{COLUMN_NAME_CONTENT},COLUMN_NAME_SHARE+">? and "+ COLUMN_NAME_FROM+"=?" , new String[]{"0",groupId}, COLUMN_NAME_SHARE, null, null, null);
			while (c.moveToNext()) {
				String content = c.getString(c.getColumnIndex(COLUMN_NAME_CONTENT ));
				list.add(content);
			}
			c.close();
		}
		db.close();
		return list;
	}

	public List<Map<String,String>> getShareScheduals(String groupId) {
		// TODO Auto-generated method stub
		List<Map<String,String>> list=new ArrayList<>();
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		if(db.isOpen()){
			Cursor c=db.query(true, TABLE_NAME, new String[]{COLUMN_NAME_CONTENT,COLUMN_NAME_SHARE},COLUMN_NAME_SHARE+">? and "+ COLUMN_NAME_FROM+"=?" , new String[]{"0",groupId}, COLUMN_NAME_SHARE, null, null, null);
			while (c.moveToNext()) {
				Map<String,String> map=new HashMap<>();
				String content = c.getString(c.getColumnIndex(COLUMN_NAME_CONTENT ));
				String share=c.getString(c.getColumnIndex(COLUMN_NAME_SHARE));
				map.put("shareId", share);
				map.put("content", content);
				list.add(map);
			}

			c.close();
		}
		db.close();
		return list;
	}

	public List<String> getShareIdList(String groupId) {
		// TODO Auto-generated method stub
		List<String> list=new ArrayList<>();
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		if(db.isOpen()){
			Cursor c=db.query(true, TABLE_NAME, new String[]{COLUMN_NAME_CONTENT,COLUMN_NAME_SHARE},COLUMN_NAME_SHARE+">? and "+ COLUMN_NAME_FROM+"=?" , new String[]{"0",groupId}, COLUMN_NAME_SHARE, null, null, null);
			while (c.moveToNext()) {

				String share=c.getString(c.getColumnIndex(COLUMN_NAME_SHARE));
				if(!list.contains(share)){
					list.add(share);
				}
			}
			c.close();
		}
		db.close();
		return list;
	}


}
