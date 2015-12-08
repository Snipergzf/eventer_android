
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
	private String SHARED_KEY_ALERT= "shared_key_alert";
	private String SHARED_KEY_ALERT_DETAIL= "shared_key_alert_detail";
	private String SHARED_KEY_ALERT_VOICE= "shared_key_alert_voice";
	private String SHARED_KEY_ALERT_SHAKE= "shared_key_alert_shake";
	private String EXIST_NEW_VERSION="exist_new_version";
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

	public boolean getMsgAlert(){
		return mSharedPreferences.getBoolean(SHARED_KEY_ALERT, true);
	}

	public void setMsgAlert(boolean param){
		editor.putBoolean(SHARED_KEY_ALERT, param).commit();
	}


	public void setMsgAlertDetail(boolean param){
		editor.putBoolean(SHARED_KEY_ALERT_DETAIL, param).commit();
	}
	public boolean getMsgAlertDetail(){
		return mSharedPreferences.getBoolean(SHARED_KEY_ALERT_DETAIL, true);
	}

	public void setMsgAlertVoice(boolean param){
		editor.putBoolean(SHARED_KEY_ALERT_VOICE, param).commit();
	}
	public boolean getMsgAlertVoice(){
		return mSharedPreferences.getBoolean(SHARED_KEY_ALERT_VOICE, true);
	}

	public void setMsgAlertShake(boolean param){
		editor.putBoolean(SHARED_KEY_ALERT_SHAKE, param).commit();
	}

	public void set(String param){
		editor.putString("msg", param).commit();
	}

	public boolean getMsgAlertShake(){
		return mSharedPreferences.getBoolean(SHARED_KEY_ALERT_SHAKE, true);
	}

	public  boolean hasKey(final String key) {
		return mSharedPreferences.contains(key);
	}

	public boolean getVersionAlert(){
		return mSharedPreferences.getBoolean(EXIST_NEW_VERSION, false);
	}

	public void setVersionAlert(boolean param){
		editor.putBoolean(EXIST_NEW_VERSION, param).commit();
	}

	public String getDeviceId(){
		if(TextUtils.isEmpty(deviceId)){
			deviceId=getuniqueId();
			Log.e("1", "deviceId:"+deviceId);
		}
		return deviceId;
	}

	String getuniqueId(){
		try{
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String imei=tm.getDeviceId();
			String simSerialNumber=tm.getSimSerialNumber();
			String androidId =android.provider.Settings.Secure.getString(
					context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
			if(TextUtils.isEmpty(simSerialNumber)){
				simSerialNumber="sim";
			}
			UUID deviceUuid =new UUID(androidId.hashCode(), ((long)imei.hashCode() << 32) |simSerialNumber.hashCode());
			String uniqueId= deviceUuid.toString();
			if(!TextUtils.isEmpty(uniqueId)){
				return uniqueId;
			}else{
				return "";
			}

		}catch(Exception e){
			return "";
		}

	}

	public  void clearPreference() {
		editor.clear();
		editor.commit();
	}
}