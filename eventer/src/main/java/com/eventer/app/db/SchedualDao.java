
package com.eventer.app.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.entity.Schedual;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("DefaultLocale")
@SuppressWarnings({"UnusedDeclaration"})
public class SchedualDao {
	public static final String TABLE_NAME = "dbSchedule";
	public static final String COLUMN_NAME_ID = "scheduleID";
	public static final String COLUMN_NAME_START = "startTime";
	public static final String COLUMN_NAME_END = "endTime";
	public static final String COLUMN_NAME_TITLE = "title";
	public static final String COLUMN_NAME_PLACE = "place";
	public static final String COLUMN_NAME_DETAIL = "detail";
	public static final String COLUMN_NAME_REMIND = "remind";
	public static final String COLUMN_NAME_REMINDTIME = "remindTime";
	public static final String COLUMN_NAME_STATUS = "status";
	public static final String COLUMN_NAME_FREQUENCY = "frequency";
	//	public static final String COLUMN_NAME_IS_TIMESPAN = "TimeSpan";
	public static final String COLUMN_NAME_IS_COMPANION = "companion";
	public static final String COLUMN_NAME_EVENTID = "eventID";
	public static final String COLUMN_NAME_TYPE="type";
	public static final String COLUMN_NAME_SHARE="shareId";

	public static final String COLUMN_NAME_FLAG="flag";


	//private DbOpenHelper dbHelper;
	private DBManager dbHelper;
	Context context;

	public SchedualDao(Context context) {
		dbHelper = new DBManager(context);
		dbHelper.openDatabase();

		Log.e("......",dbHelper.isColumnExist(TABLE_NAME, "flag")+"");
		if(!dbHelper.isColumnExist(TABLE_NAME, "flag")){

			dbHelper.execSQL("alter TABLE dbSchedule add flag INT DEFAULT(1);");

		}
		dbHelper.closeDatabase();
		this.context= context;
		//dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 *
	 */
	public void saveEventList(List<Schedual> eventList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			for (Schedual s :eventList) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, s.getSchdeual_ID());
//				if (user.getNick() != null) {
//                    values.put(COLUMN_NAME_NICK, user.getNick());
//                }
//                if (user.getBeizhu() != null) {
//                    values.put(COLUMN_NAME_BEIZHU, user.getBeizhu());
//                }
//                if (user.getTel() != null) {
//                    values.put(COLUMN_NAME_TEL, user.getTel());
//                }
//                if (user.getSex() != null) {
//                    values.put(COLUMN_NAME_SEX, user.getSex());
//                }
//                if (user.getAvatar() != null) {
//                    values.put(COLUMN_NAME_AVATAR, user.getAvatar());
//                }
//                if (user.getSign() != null) {
//                    values.put(COLUMN_NAME_SIGN, user.getSign());
//                }
//                if (user.getFxid() != null) {
//                    values.put(COLUMN_NAME_FXID, user.getFxid());
//                }
//                if (user.getRegion()!= null) {
//                    values.put(COLUMN_NAME_REGION, user.getRegion());
//                }
				db.replace(TABLE_NAME, null, values);
			}
		}
	}


	public void deleteSchedual(String ID){
		dbHelper.openDatabase();
		ContentValues cv=new ContentValues();
		cv.put(COLUMN_NAME_FLAG, 0);
		Log.e("1",""+ dbHelper.update("dbSchedule", cv, "scheduleID=?", new String[]{ID}));
		dbHelper.closeDatabase();
	}

	public void delSchedualByEventId(String eventId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_EVENTID + " = ?", new String[]{eventId+""});
		}
	}

	public void saveSchedual(Schedual s, int flag){
		dbHelper.openDatabase();
		dbHelper.openDatabase();
		Cursor c=dbHelper.findList(true, "dbSchedule", null,
				"scheduleID=?",
				new String[]{s.getSchdeual_ID()+""}, null, null,null,null);
		if(c.moveToNext()){
			dbHelper.delete("dbSchedule",  "scheduleID=?",
					new String[]{s.getSchdeual_ID()+""});
		}
		String share = s.getShareId();
		if(!TextUtils.isEmpty(share)){
			c=dbHelper.findList(true, "dbSchedule", null,
					"shareId=?",
					new String[]{share}, null, null,null,null);
			if(c.moveToNext()){
				dbHelper.delete("dbSchedule",  "shareId=?",
						new String[]{share});
			}
		}


		if(s.getSchdeual_ID() == 0){
			s.setSchdeual_ID(System.currentTimeMillis()/1000);
		}

		ContentValues cv=new ContentValues();
		cv.put(COLUMN_NAME_DETAIL,s.getDetail());
		cv.put(COLUMN_NAME_END,s.getEndtime());
		cv.put(COLUMN_NAME_FREQUENCY,s.getFrequency());
		cv.put(COLUMN_NAME_ID,s.getSchdeual_ID());
		cv.put(COLUMN_NAME_IS_COMPANION,s.getFriend());
		cv.put(COLUMN_NAME_PLACE,s.getPlace());
		cv.put(COLUMN_NAME_REMIND,s.getRemind());
		cv.put(COLUMN_NAME_REMINDTIME,s.getRemindtime());
		cv.put(COLUMN_NAME_START,s.getStarttime());
		cv.put(COLUMN_NAME_STATUS,s.getStatus());
		cv.put(COLUMN_NAME_TITLE, s.getTitle());
		cv.put(COLUMN_NAME_EVENTID, s.getEventId());
		cv.put(COLUMN_NAME_TYPE, s.getType());
		cv.put(COLUMN_NAME_SHARE, s.getShareId());
		cv.put(COLUMN_NAME_FLAG, flag);
		dbHelper.insert(TABLE_NAME, cv);
		dbHelper.closeDatabase();
	}

	public void saveSchedualNoShare(Schedual s, int flag){
		dbHelper.openDatabase();
		dbHelper.openDatabase();
		Cursor c=dbHelper.findList(true, "dbSchedule", null,
				"scheduleID=? and shareId is NULL",
				new String[]{s.getSchdeual_ID()+""}, null, null,null,null);
		if(c.moveToNext()){
			dbHelper.delete("dbSchedule",  "scheduleID=? and shareId is NULL",
					new String[]{s.getSchdeual_ID()+""});
		}

		ContentValues cv=new ContentValues();
		cv.put(COLUMN_NAME_DETAIL,s.getDetail());
		cv.put(COLUMN_NAME_END,s.getEndtime());
		cv.put(COLUMN_NAME_FREQUENCY,s.getFrequency());
		cv.put(COLUMN_NAME_ID,s.getSchdeual_ID());
		cv.put(COLUMN_NAME_IS_COMPANION,s.getFriend());
		cv.put(COLUMN_NAME_PLACE,s.getPlace());
		cv.put(COLUMN_NAME_REMIND,s.getRemind());
		cv.put(COLUMN_NAME_REMINDTIME,s.getRemindtime());
		cv.put(COLUMN_NAME_START,s.getStarttime());
		cv.put(COLUMN_NAME_STATUS,s.getStatus());
		cv.put(COLUMN_NAME_TITLE, s.getTitle());
		cv.put(COLUMN_NAME_EVENTID, s.getEventId());
		cv.put(COLUMN_NAME_TYPE, s.getType());
		cv.put(COLUMN_NAME_SHARE, s.getShareId());
		cv.put(COLUMN_NAME_FLAG, flag);
		dbHelper.insert(TABLE_NAME, cv);
		dbHelper.closeDatabase();
	}



	public void saveSchedual(Schedual s){
		saveSchedual(s, 1);
	}
	public Schedual getBriefSchedual(long id){
		Schedual schedual=new Schedual();
		dbHelper.openDatabase();
		Cursor c=dbHelper.findList(true, "dbSchedule", null,
				"scheduleID=? and flag>0", new String[]{id+""}, null, null,null,null);
		if (c.moveToNext()) {
			String start=c.getString(c.getColumnIndex("startTime"));
			String end=c.getString(c.getColumnIndex("endTime"));
			String title=c.getString(c.getColumnIndex("title"));
			String detail =c.getString(c.getColumnIndex("detail"));
			String eid =c.getString(c.getColumnIndex(COLUMN_NAME_EVENTID));
			schedual.setStarttime(start);
			schedual.setEndtime(end);
			schedual.setTitle(title);
			schedual.setDetail(detail);
			schedual.setSchdeual_ID(id);
			schedual.setEventId(eid);
			return schedual;
		}
		return schedual;
	}
	public void update(Schedual schedual){
		dbHelper.openDatabase();
		ContentValues cv=new ContentValues();
		cv.put(COLUMN_NAME_STATUS, schedual.getStatus());
		Log.e("1",""+ dbHelper.update("dbSchedule", cv, "scheduleID=?", new String[]{schedual.getSchdeual_ID()+""}));
		dbHelper.closeDatabase();
	}

//	public void updateByShare(Schedual schedual ,int flag){
//		dbHelper.openDatabase();
//		Cursor c=dbHelper.findList(true, "dbSchedule", null,
//				"scheduleID=? and shareId=?",
//				new String[]{schedual.getSchdeual_ID()+"",schedual.getShareId()}, null, null,null,null);
//		if(c.moveToNext()){
//			dbHelper.delete("dbSchedule",  "scheduleID=? and shareId=?",
//					new String[]{schedual.getSchdeual_ID()+"",schedual.getShareId()});
//		}
//		saveSchedual(schedual, flag);
//		dbHelper.closeDatabase();
//	}

	public void updateShareInfo(Schedual schedual){
		dbHelper.openDatabase();
		ContentValues cv=new ContentValues();
		cv.put(COLUMN_NAME_IS_COMPANION, schedual.getFriend());
		cv.put(COLUMN_NAME_SHARE, schedual.getShareId());
//		cv.put(COLUMN_NAME_SHARETO, schedual.getShareTo());
		Log.e("1",""+ dbHelper.update("dbSchedule", cv, "scheduleID=?", new String[]{schedual.getSchdeual_ID()+""}));
		dbHelper.closeDatabase();
	}

	public Schedual getSchedual(String sid) {
		// TODO Auto-generated method stub
		Schedual schedual=new Schedual();
		dbHelper.openDatabase();
		Cursor c=dbHelper.findList(true, "dbSchedule", null,
				"scheduleID=?  and flag>0", new String[]{sid}, null, null,null,null);
		if (c.moveToNext()) {
			String start=c.getString(c.getColumnIndex("startTime"));
			String end=c.getString(c.getColumnIndex("endTime"));
			String title=c.getString(c.getColumnIndex("title"));
			String detail =c.getString(c.getColumnIndex("detail"));
			String place=c.getString(c.getColumnIndex(COLUMN_NAME_PLACE));
			int f=c.getInt(c.getColumnIndex(COLUMN_NAME_FREQUENCY));
			String eid =c.getString(c.getColumnIndex(COLUMN_NAME_EVENTID));
			int type=c.getInt(c.getColumnIndex(COLUMN_NAME_TYPE));
			int remind=c.getInt(c.getColumnIndex(COLUMN_NAME_REMIND));
			String shareId=c.getString(c.getColumnIndex(COLUMN_NAME_SHARE));
			String friend=c.getString(c.getColumnIndex(COLUMN_NAME_IS_COMPANION));
			String remindtime=c.getString(c.getColumnIndex(COLUMN_NAME_REMINDTIME));
			int status = c.getInt(c.getColumnIndex(COLUMN_NAME_STATUS));
			schedual.setStarttime(start);
			schedual.setFriend(friend);
			schedual.setEndtime(end);
			schedual.setTitle(title);
			schedual.setDetail(detail);
			schedual.setSchdeual_ID(Long.parseLong(sid));
			schedual.setEventId(eid);
			schedual.setFrequency(f);
			schedual.setPlace(place);
			schedual.setType(type);
			schedual.setShareId(shareId);
			schedual.setRemind(remind);
			schedual.setStatus(status);
			schedual.setRemindtime(remindtime);

			return schedual;
		}
		return null;
	}

	public Schedual getSchedualById(String sid) {
		// TODO Auto-generated method stub
		Schedual schedual=new Schedual();
		dbHelper.openDatabase();
		Cursor c=dbHelper.findList(true, "dbSchedule", null,
				"scheduleID=?", new String[]{sid}, null, null,null,null);
		if (c.moveToNext()) {
			String start=c.getString(c.getColumnIndex("startTime"));
			String end=c.getString(c.getColumnIndex("endTime"));
			String title=c.getString(c.getColumnIndex("title"));
			String detail =c.getString(c.getColumnIndex("detail"));
			String place=c.getString(c.getColumnIndex(COLUMN_NAME_PLACE));
			int f=c.getInt(c.getColumnIndex(COLUMN_NAME_FREQUENCY));
			String eid =c.getString(c.getColumnIndex(COLUMN_NAME_EVENTID));
			int type=c.getInt(c.getColumnIndex(COLUMN_NAME_TYPE));
			int remind=c.getInt(c.getColumnIndex(COLUMN_NAME_REMIND));
			String shareId=c.getString(c.getColumnIndex(COLUMN_NAME_SHARE));
			String friend=c.getString(c.getColumnIndex(COLUMN_NAME_IS_COMPANION));
			String remindtime=c.getString(c.getColumnIndex(COLUMN_NAME_REMINDTIME));
			int status = c.getInt(c.getColumnIndex(COLUMN_NAME_STATUS));
			schedual.setStarttime(start);
			schedual.setFriend(friend);
			schedual.setEndtime(end);
			schedual.setTitle(title);
			schedual.setDetail(detail);
			schedual.setSchdeual_ID(Long.parseLong(sid));
			schedual.setEventId(eid);
			schedual.setFrequency(f);
			schedual.setPlace(place);
			schedual.setType(type);
			schedual.setStatus(status);
			schedual.setShareId(shareId);
			schedual.setRemind(remind);
			schedual.setRemindtime(remindtime);

			return schedual;
		}
		return null;
	}


	public Map<String , String> getSchedualShareId(String sid) {
		// TODO Auto-generated method stub
		Map<String , String> map = new HashMap<>();
		dbHelper.openDatabase();
		Cursor c=dbHelper.findList(true, "dbSchedule", new String[]{COLUMN_NAME_SHARE, COLUMN_NAME_IS_COMPANION},
				"scheduleID=?", new String[]{sid}, null, null,null,null);
		while (c.moveToNext()) {

			String shareId=c.getString(c.getColumnIndex(COLUMN_NAME_SHARE));
			String friend=c.getString(c.getColumnIndex(COLUMN_NAME_IS_COMPANION));

            try{
				JSONObject json = JSONObject.parseObject(friend);
				String share =json.getString("share");
				if(!map.containsKey(share))
				   map.put(share, shareId);

			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return map;
	}


	public Schedual getSchedualByShare(String shareId) {
		// TODO Auto-generated method stub
		Schedual schedual=new Schedual();
		dbHelper.openDatabase();
		Cursor c=dbHelper.findList(true, "dbSchedule", null,
				COLUMN_NAME_SHARE+"=?", new String[]{shareId}, null, null,null,null);
		if (c.moveToNext()) {
			long sid=c.getLong(c.getColumnIndex(COLUMN_NAME_ID));
			String start=c.getString(c.getColumnIndex("startTime"));
			String end=c.getString(c.getColumnIndex("endTime"));
			String title=c.getString(c.getColumnIndex("title"));
			String detail =c.getString(c.getColumnIndex("detail"));
			String place=c.getString(c.getColumnIndex(COLUMN_NAME_PLACE));
			int f=c.getInt(c.getColumnIndex(COLUMN_NAME_FREQUENCY));
			String eid =c.getString(c.getColumnIndex(COLUMN_NAME_EVENTID));
			int type=c.getInt(c.getColumnIndex(COLUMN_NAME_TYPE));
			String friend=c.getString(c.getColumnIndex(COLUMN_NAME_IS_COMPANION));
			schedual.setStarttime(start);
			schedual.setEndtime(end);
			schedual.setTitle(title);
			schedual.setDetail(detail);
			schedual.setSchdeual_ID(sid);
			schedual.setEventId(eid);
			schedual.setFrequency(f);
			schedual.setPlace(place);
			schedual.setType(type);
			schedual.setShareId(shareId);
			schedual.setFriend(friend);
			return schedual;
		}
		return null;
	}

	public void delSchedualByShareId(String shareId) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_SHARE + "=?", new String[]{shareId});
		}
	}
}
