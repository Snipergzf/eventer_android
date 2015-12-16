package com.eventer.app.task;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
@SuppressWarnings({"UnusedDeclaration"})
public class Contact {
	private Context context;


	public Contact(Context context){
		this.context = context;
	}
	public Map<String, String> getContacts(){
		Map<String, String> map=getPhoneContacts();
		Map<String, String> map2 =GetSimContact();
		Set<String> set = map2.keySet();
		for (String aSet : set) {
			String key;
			String value;
			key = aSet;
			if (!map.containsKey(key)) {
				value = map.get(key);
				map.put(key, value);
			}
		}

		return map;
	}

	public Map<String, String> getPhoneContacts() {
		Map<String, String> map = new HashMap<>();
		ContentResolver cr =context.getContentResolver();
		//取得电话本中开始一项的光标，必须先moveToNext()
		Cursor cursor =cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
		if(cursor==null){
			return map;
		}
		while(cursor.moveToNext()){
			//取得联系人的名字索引
			int nameIndex  =cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			String name = cursor.getString(nameIndex);
			//取得联系人的ID索引值
			String contactId =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			//查询该位联系人的电话号码，类似的可以查询email，photo
			Cursor phone =cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = "
							+ contactId, null, null);//第一个参数是确定查询电话号，第三个参数是查询具体某个人的过滤值
			//一个人可能有几个号码

//	             //得到联系人头像ID  
//	             Long photoid = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));  
//	              Log.e("1","1------------------"+photoid); 
//	             //得到联系人头像Bitamp  
//	             Bitmap contactPhoto = null;       
//	             //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的  
//	             if(photoid > 0 ) {  
//	                 Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,Long.parseLong(contactId));  
//	                 InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);  
//	                 contactPhoto = BitmapFactory.decodeStream(input);  
//	             }else {  
//	                 contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.bg_head);  
//	             }  
			if(phone==null){
				break;
			}
			while(phone.moveToNext()){
				String phoneNumber =phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

				if(!map.containsKey(phoneNumber)){
					map.put(phoneNumber, name);
				}
			}
			phone.close();
		}
		cursor.close();
		return map;
	}

	public Map<String, String> GetSimContact(){
		Map<String, String> list = new HashMap<>();
		try{
			Intent intent = new Intent();
			intent.setData(Uri.parse("content://icc/adn"));
			Uri uri = intent.getData();
			ContentResolver cr = context.getContentResolver();
			Cursor cursor =context.getContentResolver().query(uri, null, null, null, null);
			Map<String, String> map = new HashMap<>();
			if (cursor != null) {
				while(cursor.moveToNext()){
					//取得联系人的名字索引
					int nameIndex  = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
					String name = cursor.getString(nameIndex);
					//取得联系人的ID索引值
					String contactId =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
					//查询该位联系人的电话号码，类似的可以查询email，photo
					Cursor phone =cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = "
									+ contactId, null, null);//第一个参数是确定查询电话号，第三个参数是查询具体某个人的过滤值
					//一个人可能有几个号码
					if(phone==null){
						break;
					}
					while(phone.moveToNext()){
						String phoneNumber =phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						if(!map.containsKey(phoneNumber)){
							map.put(phoneNumber, name);
						}
						Log.e("1","3------------"+name+"------"+phone);
					}

					phone.close();
				}
				cursor.close();
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}



	public List<Map<String, String>> getPhoneContactsList() {
		List<Map<String, String>> list = new ArrayList<>();
		ContentResolver cr =context.getContentResolver();
		//取得电话本中开始一项的光标，必须先moveToNext()
		Cursor cursor =cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
		if (cursor==null){
			return list;
		}
		while(cursor.moveToNext()){
			//取得联系人的名字索引
			int nameIndex  =cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			String name = cursor.getString(nameIndex);
			Log.e("1","1-------------name---"+name);
			//取得联系人的ID索引值
			String contactId =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			//查询该位联系人的电话号码，类似的可以查询email，photo
			Cursor phone =cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = "
							+ contactId, null, null);//第一个参数是确定查询电话号，第三个参数是查询具体某个人的过滤值
			//一个人可能有几个号码

			//          //得到联系人头像ID
			//          Long photoid = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
			//           Log.e("1","1------------------"+photoid);
			//          //得到联系人头像Bitamp
			//          Bitmap contactPhoto = null;
			//          //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
			//          if(photoid > 0 ) {
			//              Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,Long.parseLong(contactId));
			//              InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
			//              contactPhoto = BitmapFactory.decodeStream(input);
			//          }else {
			//              contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.bg_head);
			//          }
			if(phone==null){
				break;
			}
			while(phone.moveToNext()){
				String phoneNumber =phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				Log.e("1","1----------phone---"+phoneNumber);
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

	public List<Map<String, String>> GetSimContactList(){
		List<Map<String, String>> list = new ArrayList<>();
		try{
			Intent intent = new Intent();
			intent.setData(Uri.parse("content://icc/adn"));
			Uri uri = intent.getData();
			ContentResolver cr = context.getContentResolver();
			Cursor cursor =context.getContentResolver().query(uri, null, null, null, null);
			Map<String, String> map ;
			if (cursor != null) {
				while(cursor.moveToNext()){
					//取得联系人的名字索引
					int nameIndex  = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
					String name = cursor.getString(nameIndex);
					//取得联系人的ID索引值
					String contactId =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
					//查询该位联系人的电话号码，类似的可以查询email，photo
					Cursor phone =cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = "
									+ contactId, null, null);//第一个参数是确定查询电话号，第三个参数是查询具体某个人的过滤值
					//一个人可能有几个号码
					if(phone==null){
						break;
					}
					while(phone.moveToNext()){
						String phoneNumber =phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						map = new HashMap<>();
						map.put("name", name);
						map.put("phone", phoneNumber);
						Log.e("1","3------------"+name+"------"+phone);
						list.add(map);
					}

					phone.close();
				}
				cursor.close();
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
}
