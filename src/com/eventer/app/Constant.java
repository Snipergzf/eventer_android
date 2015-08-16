 
package com.eventer.app;

import javax.net.ssl.SSLSocket;

import android.os.Environment;

public class Constant {
	//服务器链接
	  public final static String WEB_SERVICE_URL = "http://123.56.142.241/";
	  //public final static String WEB_SERVICE_URL = "http://104.236.74.226/";
	  public final static String DomainName="messagehive.dhc.house";
//	  public final static String DomainName="104.236.74.226";
//	  public final static String WEB_SERVICE_IP="123.56.142.241";
	  public final static String WEB_SERVICE_PORT="1430";
	  //  public final static String WEB_SERVICE_URL = "http://10.0.2.2:40587/Service1.asmx?wsdl";
	  public static String UID="";
	  public static String EventID="";
	  public static String TOKEN = "";
	  public static String Phone="";
	  public static boolean isLogin = false;
	  public static boolean isConnectNet = false;
	  public static boolean isWifiConnectNet = false;
	  public static String TAG_FAILURE;
	  public static String TAG_CONNECTFAILURE;
	  public static SSLSocket socket=null;
	  public static long LoginTime=0;
	  
	  public final static String SOCKER_ACTION = "com.eventer.app.Control";  
	  public final static  String SOCKER_RCV = "com.eventer.app.ReceiveStr"; 
	  
	  //mob短信验证码的 appkey,appsecret
	  
	  public final static String APPKEY="8b44a1ad343d";
	  public final static String APPSECRET="eaf57ad6cc5b431cbf345221572e088b";
   
		 //服务器端
	  public static final String URL_Register = "";   
    
	  
	  public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	  
	  public static final String ACCOUNT_REMOVED = "account_removed";
	  
	  public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
	  
	  public static final String URL_UPDATE_Nick = "http://123.56.142.241/v1/user/set_name";
	  public static final String URL_UPDATE_Avatar = "http://123.56.142.241/v1/user/set_avatar";
	  public static final String URL_GET_Avatar = "http://123.56.142.241/v1/user/get_avatar";
	  public static final String URL_GET_USERINFO ="http://123.56.142.241/v1/user/get_info";
	  public static final String URL_GET_SELFINFO="http://123.56.142.241/v1/user/search";
	  public static final String URL_UPDATE_SELFINFO="http://123.56.142.241/v1/user/update";
	  
//	  public static final String URL_Avatar = "http://123.56.142.241/images/avatar";
	  
	  public static final String URL_GET_FRIENDLIST="http://123.56.142.241/v1/friend/update";
	  public static final String URL_DEL_FRIEND="http://123.56.142.241/v1/friend/delete";
	  
	  public static final String URL_ADD_COMMENT ="http://123.56.142.241/v1/comment/add";
	  public static final String URL_DELETE_COMMENT ="http://123.56.142.241/v1/comment/delete";
	  public static final String URL_GET_COMMENT ="http://123.56.142.241/v1/comment/get";
	  
	  public static final String URL_GET_EVENT ="http://123.56.142.241/v1/event/search";
	  
	  
	  
	  public static final String IMAGE_PATH=Environment.getExternalStorageDirectory()+"/Eventer/Img/";
	  public static final String URL_UPDATE_Groupnanme = "";
	  
}
