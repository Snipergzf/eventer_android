package com.eventer.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.eventer.app.Constant;

public class LocalUserInfo {

    /**
     * 保存Preference的name
     */
    public static final String PREFERENCE_NAME = "local_userinfo_"+Constant.UID;
    private static SharedPreferences mSharedPreferences;
    private static LocalUserInfo mPreferenceUtils;
    private static SharedPreferences.Editor editor;

    private LocalUserInfo(Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
    }

    /**
     * 单例模式，获取instance实例
     *

     */
    public static LocalUserInfo getInstance(Context cxt) {
        if (mPreferenceUtils == null) {
            mPreferenceUtils = new LocalUserInfo(cxt);
        }
        editor = mSharedPreferences.edit();
        return mPreferenceUtils;
    }

    //
    public void setUserInfo(String str_name, String str_value) {

        editor.putString(str_name, str_value);
        editor.commit();
    }

    public String getUserInfo(String str_name) {

        return mSharedPreferences.getString(str_name, "");

    }



}
