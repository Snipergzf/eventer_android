 
package com.eventer.app.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.easemob.util.HanziToPinyin;
import com.eventer.app.Constant;
import com.eventer.app.entity.InviteMessage;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserDetail;
import com.eventer.app.entity.UserInfo;

@SuppressLint("DefaultLocale")
public class UserDao {
	public static final String TABLE_NAME = "dbContact";
	public static final String COLUMN_NAME_ID = "userId";
	public static final String COLUMN_NAME_NICK = "nickname";
    public static final String COLUMN_NAME_SEX = "sex";
    public static final String COLUMN_NAME_AVATAR = "imagePath";
    public static final String COLUMN_NAME_SIGN = "sign";
    public static final String COLUMN_NAME_TEL = "tel";
    public static final String COLUMN_NAME_BEIZHU = "remarkName";
	public static final String COLUMN_NAME_IS_STRANGER = "type";
	public static final String COLUMN_NAME_IS_GRADE = "grade";
	public static final String COLUMN_NAME_IS_SCHOOL = "school";
	public static final String COLUMN_NAME_IS_MAJOR = "major";
	public static final String COLUMN_NAME_IS_CLASS = "class";
	public static final String COLUMN_NAME_IS_USERRANK = "user_rank";
	public static final String COLUMN_NAME_IS_EMAIL = "email";

	private DBManager dbHelper;

	public UserDao(Context context) {
		dbHelper = new DBManager(context);
	}

	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */
	public void saveContactList(List<User> contactList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {	
			for (User user : contactList) {
				db.delete(TABLE_NAME, COLUMN_NAME_ID+ " = ?", new String[]{user.getUsername()});
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, user.getUsername());
				values.put(COLUMN_NAME_NICK, user.getNick());
	            values.put(COLUMN_NAME_BEIZHU, user.getBeizhu());
		        values.put(COLUMN_NAME_TEL, user.getTel());
		        values.put(COLUMN_NAME_AVATAR, user.getAvatar());
		        values.put(COLUMN_NAME_SIGN, user.getSign());
		        values.put(COLUMN_NAME_IS_STRANGER, user.getType());
				Long i=db.insert(TABLE_NAME, null, values);	
				Log.e("1", i+"");
			}
		}
		db.close();
	}

	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public Map<String, User> getContactList() {
		dbHelper.openDatabase();
		Map<String, User> users = new HashMap<String, User>();
		Cursor cursor=dbHelper.findList(true, TABLE_NAME, null,
				COLUMN_NAME_IS_STRANGER+"=?", new String[]{"1"}, null, null,null,null);
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
				String sign = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SIGN));
				String beizhu = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BEIZHU));
				User user = new User();
				user.setUsername(username);
				user.setNick(nick);
				user.setBeizhu(beizhu);
				user.setSign(sign);
				user.setAvatar(avatar);
				users.put(username, user);
			}
//			cursor.close();
		dbHelper.closeDatabase();	
		return users;
	}
	
	public List<String> getContactIDList() {
		dbHelper.openDatabase();
		List<String> list = new ArrayList<String>();
		Cursor cursor=dbHelper.findList(true, TABLE_NAME, new String[]{COLUMN_NAME_ID},
    			"Type<20", null, null, null,null,null);
			while (cursor.moveToNext()) {
				String id = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				list.add(id);			
			}
			 dbHelper.closeDatabase();	
		return list;
	}
	
	public boolean isExistContactID(String id) {
		dbHelper.openDatabase();
		Cursor cursor=dbHelper.findList(true, TABLE_NAME, new String[]{COLUMN_NAME_ID},
    			"userId=?", new String[]{id}, null, null,null,null);
		boolean result=false;
		int index=0;
		while (cursor.moveToNext()) {
			    index++;
			    break;
		}
		if(index>0){
			result=true;
		}
//			 dbHelper.closeDatabase();	
		return result;
	}
	
	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public List<User> getFriendList() {
		dbHelper.openDatabase();
		 //dbHelper.deleteDatabase(context);
		 Cursor cursor=dbHelper.findList(true, TABLE_NAME, null,
	    			COLUMN_NAME_IS_STRANGER+"=?", new String[]{"1"}, null, null,null,null);
		List<User> users = new ArrayList<User>();
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
				String tel = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TEL));
				String sign = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SIGN));
				String beizhu = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BEIZHU));
				User user = new User();
				user.setUsername(username);
				user.setNick(nick);
				user.setBeizhu(beizhu);
				user.setSign(sign);
				user.setTel(tel);
				user.setAvatar(avatar);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getNick())) {
					headerName = user.getNick();
				} else {
					headerName = user.getUsername();
				}
				
//				if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)) {
//					user.setHeader("");
//				} else 
					if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1))
							.get(0).target.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}
				users.add(user);
			}
//			cursor.close();
		dbHelper.closeDatabase();	
		return users;
	}
	
//	/**
//	 * 获取好友list
//	 * 
//	 * @return
//	 */
//	@SuppressLint("DefaultLocale")
//	public List<User> getUserList() {
//		dbHelper.openDatabase();
//		 //dbHelper.deleteDatabase(context);
//		 Cursor cursor=dbHelper.findList(true, TABLE_NAME, null,
//	    			null, null, null, null,null,null);
//		List<User> users = new ArrayList<User>();
//			while (cursor.moveToNext()) {
//				String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
//				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
//				String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
//				String tel = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TEL));
//				String sign = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SIGN));
//				String sex = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SEX));
//				String region = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_REGION));
//				String beizhu = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BEIZHU));
//				User user = new User();
//				user.setUsername(username);
//				user.setNick(nick);
//				user.setBeizhu(beizhu);
//				user.setRegion(region);
//				user.setSex(sex);
//				user.setSign(sign);
//				user.setTel(tel);
//				user.setAvatar(avatar);
//				String headerName = null;
//				if (!TextUtils.isEmpty(user.getNick())) {
//					headerName = user.getNick();
//				} else {
//					headerName = user.getUsername();
//				}
//				
////				if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)) {
////					user.setHeader("");
////				} else 
//					if (Character.isDigit(headerName.charAt(0))) {
//					user.setHeader("#");
//				} else {
//					user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1))
//							.get(0).target.substring(0, 1).toUpperCase());
//					char header = user.getHeader().toLowerCase().charAt(0);
//					if (header < 'a' || header > 'z') {
//						user.setHeader("#");
//					}
//				}
//				users.add(user);
//			}
////			cursor.close();
//		dbHelper.closeDatabase();	
//		return users;
//	}
	
	

	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public User getUser(String userId) {
		dbHelper.openDatabase();
		 //dbHelper.deleteDatabase(context);
		 Cursor cursor=dbHelper.findList(true, TABLE_NAME, null,
	    			"userId=?", new String[]{userId}, null, null,null,null);
		 User user = new User();
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
				String tel = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TEL));
				String sign = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SIGN));
				String beizhu = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BEIZHU));
				
				user.setUsername(username);
				user.setNick(nick);
				user.setBeizhu(beizhu);
				user.setSign(sign);
				user.setTel(tel);
				user.setAvatar(avatar);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getNick())) {
					headerName = user.getNick();
				} else {
					headerName = user.getUsername();
				}
				
//				if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)) {
//					user.setHeader("");
//				} else if (Character.isDigit(headerName.charAt(0))) {
//					user.setHeader("#");
//				} else {
//					user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1))
//							.get(0).target.substring(0, 1).toUpperCase());
//					char header = user.getHeader().toLowerCase().charAt(0);
//					if (header < 'a' || header > 'z') {
//						user.setHeader("#");
//					}
//				}
				break;
			}
//			cursor.close();
		dbHelper.closeDatabase();	
		return user;
	}
	
	
	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public Map<String,String> getUserInfo(String userId) {
		 Map<String,String> map=new HashMap<String, String>();
		dbHelper.openDatabase();
		 //dbHelper.deleteDatabase(context);
		String name="";
		String avatar="";
		 Cursor cursor=dbHelper.findList(true, TABLE_NAME, new String[]{COLUMN_NAME_AVATAR,COLUMN_NAME_NICK,COLUMN_NAME_BEIZHU},
	    			"userId=?", new String[]{userId}, null, null,null,null);
			while (cursor.moveToNext()) {
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));			
				String beizhu = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BEIZHU));
				avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));

				if (beizhu!=null&&beizhu!="") {
					name = beizhu;
				} else if (nick!=null&&nick!=""){
					name=nick;
				}else{
					name=userId;
				}	
				map.put("avatar", avatar);
				map.put("name", name);
                return map;
			}
//			cursor.close();
		dbHelper.closeDatabase();	
		return map;
	}

	
	/**
	 * 删除一个联系人
	 * @param username
	 */
	public void deleteContact(String username){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?", new String[]{username});
		}
	}
	
	/**
	 * 保存一个联系人
	 * @param user
	 */
	public void saveContact(User user){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, user.getUsername());
		if (user.getNick() != null) {
            values.put(COLUMN_NAME_NICK, user.getNick());
        }
        if (user.getBeizhu() != null) {
            values.put(COLUMN_NAME_BEIZHU, user.getBeizhu());
        }
        if (user.getTel() != null) {
            values.put(COLUMN_NAME_TEL, user.getTel());
        }
        if (user.getAvatar() != null) {
            values.put(COLUMN_NAME_AVATAR, user.getAvatar());
        }
        if (user.getSign() != null) {
            values.put(COLUMN_NAME_SIGN, user.getSign());
        }
        values.put(COLUMN_NAME_IS_STRANGER, user.getType());
		if(db.isOpen()){
			db.insert(TABLE_NAME, null, values);
		}
	}
	
	/**
	 * 保存User
	 * @param user
	 */
	public synchronized void saveUser(User user){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID+ " = ?", new String[]{user.getUsername()});
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME_ID, user.getUsername());
			values.put(COLUMN_NAME_NICK, user.getNick());
            values.put(COLUMN_NAME_BEIZHU, user.getBeizhu());
	        values.put(COLUMN_NAME_TEL, user.getTel());
	        values.put(COLUMN_NAME_AVATAR, user.getAvatar());
	        values.put(COLUMN_NAME_SIGN, user.getSign());
	        values.put(COLUMN_NAME_IS_STRANGER, user.getType());
			db.insert(TABLE_NAME, null, values);			
		}
	}
	
	public void saveUserInfo(UserInfo user){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID+ " = ?", new String[]{user.getUsername()});
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME_ID, user.getUsername());
			values.put(COLUMN_NAME_NICK, user.getNick());         
	        values.put(COLUMN_NAME_AVATAR, user.getAvatar());
	        values.put(COLUMN_NAME_IS_STRANGER, user.getType());
			db.insert(TABLE_NAME, null, values);			
		}
		db.close();
	}

	public void updateUsers(List<String> delFriend) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			for (String string : delFriend) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_IS_STRANGER, 22);
				db.update(TABLE_NAME, values, COLUMN_NAME_ID+"=?", new String[]{string});
			}	
		}
		db.close();
	}
	
	public boolean updateBeizhu(String id,String name) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int result=0;
		if(db.isOpen()){
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_BEIZHU, name);
				result=db.update(TABLE_NAME, values, COLUMN_NAME_ID+"=?", new String[]{id});
		}
		db.close();
		return result>0;
	}

	public Map<String, UserInfo> getUserInfoList() {
		// TODO Auto-generated method stub
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		Map<String, UserInfo> users = new HashMap<String, UserInfo>();
		if(db.isOpen()){
			Cursor cursor=db.query(true, TABLE_NAME, new String[]{COLUMN_NAME_ID,COLUMN_NAME_NICK,COLUMN_NAME_AVATAR},
					null, null, null, null,null,null);
				while (cursor.moveToNext()) {
					String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
					String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
					String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));				
					UserInfo user = new UserInfo();
					user.setUsername(username);
					user.setNick(nick);
					user.setAvatar(avatar);
					users.put(username, user);
				} 
			cursor.close();
		}		
		db.close();
		return users;

	}
	
	public UserInfo getInfo(String userId) {
		UserInfo user=null;
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		Map<String, UserInfo> users = new HashMap<String, UserInfo>();
		if(db.isOpen()){
			Cursor cursor=db.query(true, TABLE_NAME, new String[]{COLUMN_NAME_ID,COLUMN_NAME_NICK,COLUMN_NAME_AVATAR,COLUMN_NAME_BEIZHU},
					"userId=?", new String[]{userId}, null, null,null,null);			
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
				String beizhu = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BEIZHU));
				if (beizhu!=null&&!beizhu.equals("")) {
					nick = beizhu;
				}	
				user = new UserInfo();
				user.setUsername(username);
				user.setNick(nick);
				user.setAvatar(avatar);
				users.put(username, user);
			}
			cursor.close();
		}		
		db.close();		 	
		return user;
	}
	
	
	public UserDetail getUserDetail(String userId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			Cursor cursor=db.rawQuery("select b.*,a.nickname,a.imagePath,a.type from dbContact a,dbUserInfo b where a.userId=b.userId and a.userId=? ",
					new String[]{userId});
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
				String c_class=cursor.getString(cursor.getColumnIndex(COLUMN_NAME_IS_CLASS));
				String grade=cursor.getString(cursor.getColumnIndex(COLUMN_NAME_IS_GRADE));
				String school=cursor.getString(cursor.getColumnIndex(COLUMN_NAME_IS_SCHOOL));
				String major=cursor.getString(cursor.getColumnIndex(COLUMN_NAME_IS_MAJOR));
				String email=cursor.getString(cursor.getColumnIndex(COLUMN_NAME_IS_EMAIL));
				int user_rank=cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_IS_USERRANK));
				String sex=cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SEX));
				int type=cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_IS_STRANGER));
				UserDetail user = new UserDetail();
				user.setUsername(username);
				user.setNick(nick);
				user.setAvatar(avatar);
				user.setC_class(c_class);
				user.setEmail(email);
				user.setGrade(grade);
				user.setMajor(major);
				user.setSchool(school);
				user.setSex(sex);
				user.setUserrank(user_rank);
				user.setType(type);
				return user;
			} 
		}
		db.close();
		return null;
	}
	
	public void saveDetail(UserDetail user){
		UserInfo u=new UserInfo();
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			Cursor cursor=db.query(true, TABLE_NAME, new String[]{COLUMN_NAME_ID},
	    			"userId=?", new String[]{user.getUsername()}, null, null,null,null);
			int index=0;
			while (cursor.moveToNext()) {
				    index++;
				    break;
			}
			if(index==0){
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, user.getUsername());
				values.put(COLUMN_NAME_NICK, user.getNick());         
		        values.put(COLUMN_NAME_AVATAR, user.getAvatar());
		        values.put(COLUMN_NAME_IS_STRANGER, user.getType());
				db.insert(TABLE_NAME, null, values);
			}
			db.delete("dbUserInfo", COLUMN_NAME_ID+ " = ?", new String[]{user.getUsername()});
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME_ID, user.getUsername());
			values.put(COLUMN_NAME_IS_CLASS, user.getC_class());         
	        values.put(COLUMN_NAME_IS_EMAIL, user.getEmail());
	        values.put(COLUMN_NAME_IS_GRADE, user.getGrade());
	        values.put(COLUMN_NAME_IS_MAJOR, user.getMajor());
	        values.put(COLUMN_NAME_IS_SCHOOL, user.getSchool());
	        values.put(COLUMN_NAME_IS_USERRANK, user.getUserrank());
	        values.put(COLUMN_NAME_SEX, user.getSex());
			db.insert("dbUserInfo", null, values);	
			
		}
		db.close();
	}
}
