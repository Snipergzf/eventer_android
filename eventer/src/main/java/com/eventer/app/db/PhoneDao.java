
package com.eventer.app.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eventer.app.entity.Phone;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("DefaultLocale")
public class PhoneDao {
	public static final String TABLE_NAME = "dbPhone";
	public static final String COLUMN_NAME_ID = "userId";
	public static final String COLUMN_NAME_TYPE = "type";
	public static final String COLUMN_NAME_Name = "realName";
	public static final String COLUMN_NAME_TEL="tel";

	private DBManager dbHelper;

	public PhoneDao(Context context) {
		dbHelper = new DBManager(context);
	}


	@SuppressLint("DefaultLocale")
	public List<Phone> getPhoneList() {
		List<Phone> list=new ArrayList<>();
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		if(db.isOpen()){
			Cursor c=db.query(true, TABLE_NAME, null,
					null, null, null, null,null,null);
			while (c.moveToNext()) {
				String id = c.getString(c.getColumnIndex(COLUMN_NAME_ID));
				String relName = c.getString(c.getColumnIndex(COLUMN_NAME_Name));
				int type = c.getInt(c.getColumnIndex(COLUMN_NAME_TYPE));
				String tel= c.getString(c.getColumnIndex(COLUMN_NAME_TEL ));

				Phone p=new Phone();
				p.setRelName(relName);
				p.setTel(tel);
				p.setType(type);
				p.setUserId(id);

				list.add(p);
			}
			c.close();
		}
		db.close();
		return list;
	}
	@SuppressWarnings({"UnusedDeclaration"})
	public void savePhoneList(List<Phone> list) {
		// TODO Auto-generated method stub
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		if(db.isOpen()){
			for (Phone phone : list) {
				ContentValues cv = new ContentValues();
				cv.put(COLUMN_NAME_ID, phone.getUserId());
				cv.put(COLUMN_NAME_Name, phone.getRelName());
				cv.put(COLUMN_NAME_TEL, phone.getTel());
				cv.put(COLUMN_NAME_TYPE, phone.getType());
				db.insert(TABLE_NAME, null,cv);
			}
		}
		db.close();
	}


	public List<String> getTelList() {
		List<String> list=new ArrayList<>();
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		if(db.isOpen()){
			Cursor c=db.query(true, TABLE_NAME, new String[]{COLUMN_NAME_TEL},
					null, null, null, null,null,null);
			while (c.moveToNext()) {
				String id = c.getString(c.getColumnIndex(COLUMN_NAME_TEL));
				list.add(id);
			}
			c.close();
		}
		db.close();
		return list;
	}


	public void savePhone(Phone phone) {
		// TODO Auto-generated method stub
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_TEL+"=?", new String[]{phone.getTel()});
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_NAME_ID, phone.getUserId());
			cv.put(COLUMN_NAME_Name, phone.getRelName());
			cv.put(COLUMN_NAME_TEL, phone.getTel());
			cv.put(COLUMN_NAME_TYPE, phone.getType());
			db.insert(TABLE_NAME, null,cv);
		}
		db.close();
	}




}
