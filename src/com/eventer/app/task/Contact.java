package com.eventer.app.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

public class Contact {
	private Context context;
	public Contact() {
		// TODO Auto-generated constructor stub
	}
	
	 public Contact(Context context){
	        this.context = context;
	    }
	 public Map<String, String> getContacts(){
	    	Map<String, String> map=getPhoneContacts();
	    	Map<String, String> map2 =GetSimContact(); 
	    	Set<String> set = map2.keySet();
	        Iterator<String> iterator = set.iterator();
	         while (iterator.hasNext()) {
	             String key;    
	             String value;    
	             key=iterator.next().toString();
	             if(!map.containsKey(key)){
	            	 value=map.get(key);
	            	 map.put(key, value);
	             }         
	         }
	    	
	    	return map;
	    }
	 
	public Map<String, String> getPhoneContacts() {  
	    	 Map<String, String> map = new HashMap<String, String>();
	    	 ContentResolver cr =context.getContentResolver();
	         //ȡ�õ绰���п�ʼһ��Ĺ�꣬������moveToNext()
	         Cursor cursor =cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
	         while(cursor.moveToNext()){
	             //ȡ����ϵ�˵���������
	             int nameIndex  =cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
	             String name = cursor.getString(nameIndex);
	             //ȡ����ϵ�˵�ID����ֵ
	             String contactId =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
	             //��ѯ��λ��ϵ�˵ĵ绰���룬���ƵĿ��Բ�ѯemail��photo
	             Cursor phone =cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
	                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " 
	                     + contactId, null, null);//��һ��������ȷ����ѯ�绰�ţ������������ǲ�ѯ����ĳ���˵Ĺ���ֵ
	             //һ���˿����м�������
	             
//	             //�õ���ϵ��ͷ��ID  
//	             Long photoid = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));  
//	              Log.e("1","1------------------"+photoid); 
//	             //�õ���ϵ��ͷ��Bitamp  
//	             Bitmap contactPhoto = null;       
//	             //photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�  
//	             if(photoid > 0 ) {  
//	                 Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,Long.parseLong(contactId));  
//	                 InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);  
//	                 contactPhoto = BitmapFactory.decodeStream(input);  
//	             }else {  
//	                 contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.bg_head);  
//	             }  
	                        
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
	    	Map<String, String> list = new HashMap<String, String>();  
	        try{
	            Intent intent = new Intent();
	            intent.setData(Uri.parse("content://icc/adn"));
	            Uri uri = intent.getData();
	           ContentResolver cr = context.getContentResolver();
	            Cursor cursor =context.getContentResolver().query(uri, null, null, null, null);            
	            Map<String, String> map = new HashMap<String, String>(); 
	            if (cursor != null) {
	                while(cursor.moveToNext()){                  
	                  //ȡ����ϵ�˵���������
	                  int nameIndex  = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
	                  String name = cursor.getString(nameIndex); 
	                  //ȡ����ϵ�˵�ID����ֵ
	                  String contactId =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
	                  //��ѯ��λ��ϵ�˵ĵ绰���룬���ƵĿ��Բ�ѯemail��photo
	                  Cursor phone =cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
	                                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " 
	                          + contactId, null, null);//��һ��������ȷ����ѯ�绰�ţ������������ǲ�ѯ����ĳ���˵Ĺ���ֵ
	                  //һ���˿����м�������
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

	        }catch(Exception e){}
	        return list;
	    }
 
 

		 public List<Map<String, String>> getPhoneContactsList() {  
		 	 List<Map<String, String>> list = new ArrayList<Map<String, String>>();  
		 	 ContentResolver cr =context.getContentResolver();
		      //ȡ�õ绰���п�ʼһ��Ĺ�꣬������moveToNext()
		      Cursor cursor =cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
		      while(cursor.moveToNext()){
		          //ȡ����ϵ�˵���������
		          int nameIndex  =cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
		          String name = cursor.getString(nameIndex);
		          Log.e("1","1-------------name---"+name);
		          //ȡ����ϵ�˵�ID����ֵ
		          String contactId =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		          //��ѯ��λ��ϵ�˵ĵ绰���룬���ƵĿ��Բ�ѯemail��photo
		          Cursor phone =cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
		                             ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " 
		                  + contactId, null, null);//��һ��������ȷ����ѯ�绰�ţ������������ǲ�ѯ����ĳ���˵Ĺ���ֵ
		          //һ���˿����м�������
		          
		//          //�õ���ϵ��ͷ��ID  
		//          Long photoid = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));  
		//           Log.e("1","1------------------"+photoid); 
		//          //�õ���ϵ��ͷ��Bitamp  
		//          Bitmap contactPhoto = null;       
		//          //photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�  
		//          if(photoid > 0 ) {  
		//              Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,Long.parseLong(contactId));  
		//              InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);  
		//              contactPhoto = BitmapFactory.decodeStream(input);  
		//          }else {  
		//              contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.bg_head);  
		//          }  
		                     
		          while(phone.moveToNext()){
		              String phoneNumber =phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		              Log.e("1","1----------phone---"+phoneNumber);
		              Map<String, String> map = new HashMap<String, String>();
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
		 	List<Map<String, String>> list = new ArrayList<Map<String, String>>();  
		     try{
		         Intent intent = new Intent();
		         intent.setData(Uri.parse("content://icc/adn"));
		         Uri uri = intent.getData();
		        ContentResolver cr = context.getContentResolver();
		         Cursor cursor =context.getContentResolver().query(uri, null, null, null, null);            
		         Map<String, String> map = new HashMap<String, String>(); 
		         if (cursor != null) {
		             while(cursor.moveToNext()){                  
		               //ȡ����ϵ�˵���������
		               int nameIndex  = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
		               String name = cursor.getString(nameIndex); 
		               //ȡ����ϵ�˵�ID����ֵ
		               String contactId =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		               //��ѯ��λ��ϵ�˵ĵ绰���룬���ƵĿ��Բ�ѯemail��photo
		               Cursor phone =cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
		                                  ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " 
		                       + contactId, null, null);//��һ��������ȷ����ѯ�绰�ţ������������ǲ�ѯ����ĳ���˵Ĺ���ֵ
		               //һ���˿����м�������
		               while(phone.moveToNext()){
		                   String phoneNumber =phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		                   map = new HashMap<String, String>(); 
		                   map.put("name", name);
		                   map.put("phone", phoneNumber);
		                   Log.e("1","3------------"+name+"------"+phone);
		                   list.add(map);
		               }
		               
		               phone.close();
		           }
		           cursor.close();
		         }
		
		     }catch(Exception e){}
		     return list;
		 }
}
