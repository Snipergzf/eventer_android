
package com.eventer.app.util;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;


public class PreferenceUtils {
	/**
	 * 保存Preference的name
	 */
	private static PreferenceUtils mPreferenceUtils;
	private static SharedPreferences mSharedPreferences;
	private static SharedPreferences.Editor editor;
	private String SHARED_KEY_USER = "shared_key_user";
	private String SHARED_KEY_PWD= "shared_key_pwd";
	private Context context;
	private String deviceId;
	private PreferenceUtils() {
	}
	private PreferenceUtils(Context cxt) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(cxt);;
		editor = mSharedPreferences.edit();
		context=cxt;
	}
	/**
	 * 单例模式，获取instance实例
	 * 
	 * @param cxt
	 * @return
	 */
	public  static PreferenceUtils getInstance() {
		if (mPreferenceUtils == null) {
			throw new RuntimeException("please init first!");
		}		
		return mPreferenceUtils;
	}
	
	public static synchronized void init(Context cxt){
	    if(mPreferenceUtils == null){
	        mPreferenceUtils = new PreferenceUtils(cxt);
	    }
	}
	

    public  String getPrefString(String key, final String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public  void setPrefString(final String key, final String value) {
    	editor.putString(key, value).commit();
    }
    
    public String getLoginUser(){   	  
    	return mSharedPreferences.getString(SHARED_KEY_USER, null);
    }
    
    public void setLoginUser(String paramString){
    	editor.putString(SHARED_KEY_USER, paramString).commit();
    }
    
    public String getLoginPwd(){
    	return mSharedPreferences.getString(SHARED_KEY_PWD, null);
    }
    
    public void setLoginPwd(String paramString){
    	editor.putString(SHARED_KEY_PWD, paramString).commit();
    }
    

    public  boolean hasKey(final String key) {
        return mSharedPreferences.contains(key);
    }
    
    public String getDeviceId(){
    	if(TextUtils.isEmpty(deviceId)){
    		deviceId=getuniqueId();
    		Log.e("1", "deviceId:"+deviceId);
    	}
		return deviceId;
    }
    
    String getuniqueId(){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei=tm.getDeviceId();	
		String simSerialNumber=tm.getSimSerialNumber();		
		String androidId =android.provider.Settings.Secure.getString(		
		context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);		
		UUID deviceUuid =new UUID(androidId.hashCode(), ((long)imei.hashCode() << 32) |simSerialNumber.hashCode());		
		String uniqueId= deviceUuid.toString();		
		return uniqueId;
}

    public  void clearPreference() {
        editor.clear();
        editor.commit();
    }
}