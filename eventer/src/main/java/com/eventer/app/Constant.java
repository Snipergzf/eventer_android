 
package com.eventer.app;

import android.os.Environment;

import javax.net.ssl.SSLSocket;
@SuppressWarnings({"UnusedDeclaration"})
public class Constant {
	  public final static String WEB_SERVICE_URL = "http://api.eventer.com.cn/";
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
	  public static String Tourist_TOKEN="tourists";
	  public static boolean isLogin = false;
	  public static boolean isExist = false;
	  public static boolean isConnectNet = true;

	  public static boolean isWifiConnected = false;

//	  public static boolean isWifiConnectNet = false;
	  public static String TAG_FAILURE;
	  public static String TAG_CONNECTFAILURE;
	  public static SSLSocket socket=null;
	  public static long LoginTime=0;
	  public static boolean AlarmChange=false;
	  
	  public final static String SOCKER_ACTION = "com.eventer.app.Control";  
	  public final static  String SOCKER_RCV = "com.eventer.app.ReceiveStr"; 
	  
	  //mob-appkey,appsecret
	  
	  public final static String APPKEY="b8a291e1c518";
	  public final static String APPSECRET="da865cccfca9f4634f1d4d688731d739";
   
		 //for server
	  
	  public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	  
	  public static final String ACCOUNT_REMOVED = "account_removed";
	  
	  public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
	  
	  public static final String URL_REGISTER= WEB_SERVICE_URL + "v1/user/register";
//	  public static final String URL_LOGIN= WEB_SERVICE_URL + "v1/user/login";
	  public static final String URL_LOGIN_NEW= WEB_SERVICE_URL + "v1/user/login_new";
	  public static final String URL_RESET_PWD = WEB_SERVICE_URL+"v1/user/reset_pwd";
	  
	  public static final String URL_UPDATE_Nick = WEB_SERVICE_URL+"v1/user/set_name";
	  public static final String URL_UPDATE_Avatar = WEB_SERVICE_URL+"v1/user/set_avatar";
	  public static final String URL_GET_Avatar = WEB_SERVICE_URL+"v1/user/get_avatar";
	  public static final String URL_GET_USERINFO = WEB_SERVICE_URL + "v1/user/get_info";
	  public static final String URL_GET_SELFINFO= WEB_SERVICE_URL + "v1/user/search";
	  public static final String URL_UPDATE_SELFINFO= WEB_SERVICE_URL + "v1/user/update";
	  
//	  public static final String URL_Avatar = WEB_SERVICE_URL+"images/avatar";
//	  public static final String URL_Avatar  = "www.eventer.com.cn/"+ "images/avatar";
	  public static final String URL_GET_FRIENDLIST= WEB_SERVICE_URL + "v1/friend/update";
	  public static final String URL_GET_FRIENDINFO= WEB_SERVICE_URL + "v1/friend/search";
	  public static final String URL_DEL_FRIEND= WEB_SERVICE_URL + "v1/friend/delete";
	  
	  public static final String URL_ADD_COMMENT = WEB_SERVICE_URL + "v1/comment/add";
	  public static final String URL_DELETE_COMMENT = WEB_SERVICE_URL + "v1/comment/delete";
	  public static final String URL_GET_COMMENT = WEB_SERVICE_URL + "v1/comment/get";
	  
	  public static final String URL_GET_EVENT = WEB_SERVICE_URL + "v1/event/search";

	  
	  public static final String URL_SEND_EVENT_FEEDBACK = WEB_SERVICE_URL + "v1/event/feedback_add";
	  public static final String URL_DEL_EVENT_FEEDBACK = WEB_SERVICE_URL + "v1/event/feedback_del";
	  public static final String URL_UPDATE_EVENT_FEEDBACK = WEB_SERVICE_URL + "v1/event/update";
	  
	  public static final String URL_GET_GROUP_MEMBER = WEB_SERVICE_URL + "v1/group/search_member";
	  
	  
	  
	  public static final String IMAGE_PATH=Environment.getExternalStorageDirectory()+"/Eventer/Img/";
	  public static final String URL_UPDATE_GROUP_UPDATE = WEB_SERVICE_URL + "v1/group/update";
	  public static final String URL_GET_GROUP_INFO = WEB_SERVICE_URL + "v1/group/check_info" ;

	  public static final String URL_ACTIVITY_CREATE = WEB_SERVICE_URL + "v1/activity/create" ;
	  public static final String URL_ACTIVITY_JOIN = WEB_SERVICE_URL + "v1/activity/join" ;
	  public static final String URL_ACTIVITY_EXIT = WEB_SERVICE_URL + "v1/activity/exit" ;
	  public static final String URL_ACTIVITY_DELETE = WEB_SERVICE_URL + "v1/activity/delete" ;
	  public static final String URL_ACTIVITY_CHECK = WEB_SERVICE_URL + "v1/activity/check" ;


	  public static final int GROUP_CREATED_NOTIFICATION = 6;
	  public static final int GROUP_INVITE_NOTIFICATION = 8;
	  public static final int GROUP_LEAVE_NOTIFICATION = 9;

	  public static final int GROUP_ACTIVITY_CREATE = 24;
	  public static final int GROUP_ACTIVITY_JOIN = 21;
	  public static final int GROUP_ACTIVITY_EXIT = 22;
	  public static final int GROUP_ACTIVITY_DELETE = 23;
	  
}
