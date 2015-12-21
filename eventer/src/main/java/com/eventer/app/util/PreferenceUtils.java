
package com.eventer.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.UUID;

@SuppressWarnings({"UnusedDeclaration"})
public class PreferenceUtils {
	/**
	 * 保存Preference的name
	 */
	private static PreferenceUtils mPreferenceUtils;
	private static SharedPreferences mSharedPreferences;
	private String SHARED_KEY_USER = "shared_key_user";
	private String SHARED_KEY_PWD= "shared_key_pwd";
	private String SHARED_KEY_UID = "shared_key_uid";
	private String SHARED_KEY_ALERT= "shared_key_alert";
	private String SHARED_KEY_ALERT_DETAIL= "shared_key_alert_detail";
	private String SHARED_KEY_ALERT_VOICE= "shared_key_alert_voice";
	private String SHARED_KEY_ALERT_SHAKE= "shared_key_alert_shake";
	private String SHARED_KEY_DISPLAY_EVENT= "shared_key_display_event";
	private String EXIST_NEW_VERSION="exist_new_version";
	private Context context;
	private String deviceId;

	private PreferenceUtils(Context cxt) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(cxt);
		context=cxt;
	}
	/**
	 * 单例模式，获取instance实例
	 *
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
		mSharedPreferences.edit().putString(key, value).apply();
	}

	public String getLoginUser(){
		return mSharedPreferences.getString(SHARED_KEY_USER, null);
	}

	public void setLoginUser(String paramString){
		mSharedPreferences.edit().putString(SHARED_KEY_USER, paramString).apply();
	}

	public void setUserId(String paramString){
		mSharedPreferences.edit().putString(SHARED_KEY_UID, paramString).apply();
	}

	public String getUserId(){
		return mSharedPreferences.getString(SHARED_KEY_UID, null);
	}

	public String getLoginPwd(){
		return mSharedPreferences.getString(SHARED_KEY_PWD, null);
	}

	public void setLoginPwd(String paramString){
		mSharedPreferences.edit().putString(SHARED_KEY_PWD, paramString).apply();
	}

	public boolean getMsgAlert(){
		return mSharedPreferences.getBoolean(SHARED_KEY_ALERT, true);
	}

	public void setMsgAlert(boolean param){
		mSharedPreferences.edit().putBoolean(SHARED_KEY_ALERT, param).apply();
	}


	public void setMsgAlertDetail(boolean param){
		mSharedPreferences.edit().putBoolean(SHARED_KEY_ALERT_DETAIL, param).apply();
	}
	public boolean getMsgAlertDetail(){
		return mSharedPreferences.getBoolean(SHARED_KEY_ALERT_DETAIL, true);
	}

	public void setMsgAlertVoice(boolean param){
		mSharedPreferences.edit().putBoolean(SHARED_KEY_ALERT_VOICE, param).apply();
	}
	public boolean getMsgAlertVoice(){
		return mSharedPreferences.getBoolean(SHARED_KEY_ALERT_VOICE, true);
	}

	public void setMsgAlertShake(boolean param){
		mSharedPreferences.edit().putBoolean(SHARED_KEY_ALERT_SHAKE, param).apply();
	}

	public void setDisplayUserlessEvent(boolean param){
		mSharedPreferences.edit().putBoolean(SHARED_KEY_DISPLAY_EVENT, param).apply();
	}

	public boolean getDisplayUserlessEvent(){
		return mSharedPreferences.getBoolean(SHARED_KEY_DISPLAY_EVENT, false);
	}

	public void set(String param){
		mSharedPreferences.edit().putString("msg", param).apply();
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
		mSharedPreferences.edit().putBoolean(EXIST_NEW_VERSION, param).apply();
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
		mSharedPreferences.edit().clear();
		mSharedPreferences.edit().apply();
	}
}