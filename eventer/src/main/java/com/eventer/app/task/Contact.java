package com.eventer.app.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contact {
	private Context context;


	public Contact(Context context){
		this.context = context;
	}


	public List<Map<String, String>> getPhoneContactsList() {
		List<Map<String, String>> list = new ArrayList<>();
		ContentResolver cr = context.getContentResolver();
		//取得电话本中开始一项的光标，必须先moveToNext()
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
		if (cursor == null){
			return list;
		}
		while(cursor.moveToNext()){
			//取得联系人的名字索引
			int nameIndex  = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			String name = cursor.getString(nameIndex);
			//取得联系人的ID索引值
			String contactId = cursor.getString( cursor.
					getColumnIndex(ContactsContract.Contacts._ID));
			//查询该位联系人的电话号码，类似的可以查询email，photo
			Cursor phone =cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = "
							+ contactId, null, null);//第一个参数是确定查询电话号，第三个参数是查询具体某个人的过滤值
			if(phone == null){
				break;
			}
			while(phone.moveToNext()){
				String phoneNumber = phone.getString(phone.
						getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				Map<String, String> map = new HashMap<>();
				map.put("name", name);
				map.put("phone", phoneNumber);
				list.add(map);
			}
			phone.close();
		}
		cursor.close();
		return list;
	}


}
