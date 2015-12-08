
package com.eventer.app.db;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

@SuppressLint("DefaultLocale")
public class SelfInfoDao {
	public static final String TABLE_NAME = "dbContact";
	public static final String COLUMN_NAME_KEY = "Key";
	public static final String COLUMN_NAME_VALUE = "Value";
	public static final String COLUMN_NAME_TYPE = "Type";


	private DBManager dbHelper;
	private Context context;

	public SelfInfoDao(Context context) {
		dbHelper = new DBManager(context);
		this.context= context;
	}


	/**
	 * 获取好友list
	 *
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public Map<String,Object> getUserInfo(String info) {
		dbHelper.openDatabase();
		//dbHelper.deleteDatabase(context);
		Map<String,Object> map=new HashMap<String, Object>();
		Cursor cursor=dbHelper.findList(true, TABLE_NAME, null,
				COLUMN_NAME_KEY+"=?", new String[]{info}, null, null,null,null);
		int index=0;
		while (cursor.moveToNext()) {
			String type = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TYPE));
			String value = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_VALUE));
			map.put("type", type);
			map.put("value", value);
			index++;
			break;
		}
//			cursor.close();
		if(index>0){
			map=null;
		}
		dbHelper.closeDatabase();
		return map;
	}


}
