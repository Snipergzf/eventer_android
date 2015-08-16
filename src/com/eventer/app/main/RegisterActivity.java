package com.eventer.app.main;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.receiver.SMSBroadcastReceiver;
import com.eventer.app.task.MyCountTimer;
import com.eventer.app.ui.base.BaseActivity;
import com.eventer.app.util.PreferenceUtils;
import com.eventer.app.view.TitleBar;

@SuppressLint("ShowToast")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class RegisterActivity extends BaseActivity implements OnClickListener, Callback {
    private EditText edit_tel,edit_code,edit_pwd;
    private TextView tv_protocol,btn_send_code;
    private ImageButton btn_tel_clear,btn_code_clear,btn_pwd_clear;
    private Button btn_next;
    private boolean IsUserCheck=false;
    private boolean IsCodeCheck=false;
    private boolean IsPwdCheck=false;
    private Context context;
    private EventHandler eventHandler;
    private String TelString;
    private SMSBroadcastReceiver mSMSBroadcastReceiver;	
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private String user="",pwd="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar.setTitleBar(this,"填写手机号码");
		setContentView(R.layout.activity_register);
		
		edit_tel=(EditText)findViewById(R.id.edit_tel);
		edit_code=(EditText)findViewById(R.id.edit_security_code);
		edit_pwd=(EditText)findViewById(R.id.edit_pwd);
		btn_next=(Button)findViewById(R.id.btn_next);
		btn_tel_clear=(ImageButton)findViewById(R.id.btn_tel_clear);
		btn_code_clear=(ImageButton)findViewById(R.id.btn_security_code_clear);
		btn_pwd_clear=(ImageButton)findViewById(R.id.btn_pwd_clear);
		tv_protocol=(TextView)findViewById(R.id.tv_protocol);
		btn_send_code=(TextView)findViewById(R.id.btn_send_code);
		context=RegisterActivity.this;
		
		init();
		initSMSSDK();
		
	}
	
	public void init(){
			
		String protocol = "<font color=" + "\"" + "#AAAAAA" + "\">"   + "我已阅读并同意" + "</font>" 
                + "<font color=" + "\"" + "#576B95" + "\">" + "Eventer的软件许可及服务协议"
                + "</font>" ;

		tv_protocol.setText(Html.fromHtml(protocol));
		
		edit_tel.addTextChangedListener(new TextWatcher() {
			
			@SuppressLint("NewApi")
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				int len=s.length();
				if(len>0) {
					btn_tel_clear.setVisibility(View.VISIBLE);
				}
				else{
					btn_tel_clear.setVisibility(View.GONE);
				}
				if(len>10){
					IsUserCheck=true;
				}else if(len<11){
					IsUserCheck=false;
				}
				if(IsUserCheck&&IsCodeCheck&&IsPwdCheck){
					btn_next.setBackground(getResources().getDrawable(R.drawable.button_blue));
					btn_next.setTextColor(getResources().getColor(R.color.caldroid_white));
					btn_next.setClickable(true);
				}else{
					btn_next.setBackground(getResources().getDrawable(R.drawable.button_gray));
					btn_next.setTextColor(getResources().getColor(R.color.caldroid_darker_gray));
					btn_next.setClickable(false);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub			
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		edit_code.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				int len=s.length();
				if(len>0) {
					btn_code_clear.setVisibility(View.VISIBLE);
				}
				else{
					btn_code_clear.setVisibility(View.GONE);
				}
				if(len>3){
					IsCodeCheck=true;
				}else if(len<4){
					IsCodeCheck=false;
				}
				if(IsUserCheck&&IsCodeCheck&&IsPwdCheck){
					btn_next.setBackground(getResources().getDrawable(R.drawable.button_blue));
					btn_next.setTextColor(getResources().getColor(R.color.caldroid_white));
					btn_next.setClickable(true);
				}else{
					btn_next.setBackground(getResources().getDrawable(R.drawable.button_gray));
					btn_next.setTextColor(getResources().getColor(R.color.caldroid_darker_gray));
					btn_next.setClickable(false);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		edit_pwd.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				int len=s.toString().length();
				if(len>0) {
					btn_pwd_clear.setVisibility(View.VISIBLE);
				}
				else{
					btn_pwd_clear.setVisibility(View.GONE);
				}
				if(len>5){
					IsPwdCheck=true;
				}else if(len<6){
					IsPwdCheck=false;
				}
				if(IsUserCheck&&IsCodeCheck&&IsPwdCheck){
					btn_next.setBackground(getResources().getDrawable(R.drawable.button_blue));
					btn_next.setTextColor(getResources().getColor(R.color.caldroid_white));
					btn_next.setClickable(true);
				}else{
					btn_next.setBackground(getResources().getDrawable(R.drawable.button_gray));
					btn_next.setTextColor(getResources().getColor(R.color.caldroid_darker_gray));
					btn_next.setClickable(false);
				}
			}
		});
			
		btn_next.setOnClickListener(this);
		btn_tel_clear.setOnClickListener(this);
		btn_code_clear.setOnClickListener(this);
		btn_pwd_clear.setOnClickListener(this);
		tv_protocol.setOnClickListener(this);
		btn_send_code.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_next:
			//SMSSDK.submitVerificationCode("86", TelString, edit_code.getText().toString());
			Map<String, String> params = new HashMap<String, String>();
			params.put("phone", edit_tel.getText().toString());
	        params.put("pwd", edit_pwd.getText().toString());
			UserRegister(params);
			break;
        case R.id.btn_tel_clear:
        	edit_tel.setText("");
			break;
			
        case R.id.tv_protocol:
        	Uri uri = Uri.parse("http://baidu.com");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
        	break;
        case R.id.btn_security_code_clear:
        	edit_code.setText("");
        	break;
        case R.id.btn_pwd_clear:
        	edit_pwd.setText("");
        	break;
        case R.id.btn_send_code:
        	if(IsUserCheck){
        		MyCountTimer timeCount = new MyCountTimer(btn_send_code, 0xff33b5e5, 0xff969696);//传入了文字颜色值
        	    timeCount.start();
        	    if(mSMSBroadcastReceiver==null){
    				initSMSReceiver();
    			}
        	    TelString=edit_tel.getText().toString();
    			SMSSDK.getVerificationCode("86",TelString);
    			
        	}else{
        		Toast toast=Toast.makeText(RegisterActivity.this, "请输入完整的手机号码！", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.TOP, 0, 250);
				toast.show();
        	}
        	
        	break;
		default:
			break;
		}
	}
	
	
	/**
	 * 执行异步任务
	 * 
	 * @param params
	 *      
	 */
	public void UserRegister(final Object... params) {
		new AsyncTask<Object, Object,Integer>() {

			@Override
			protected Integer doInBackground(Object... params) {
			  try {
		    	        @SuppressWarnings("unchecked")
						int status=com.eventer.app.http.HttpUnit.sendRegisterRequest((Map<String, String>) params[0]);
		    	        return status;					
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Log.e("1", e.toString());
					e.printStackTrace();
					return -1;
				}
			}
			protected void onPostExecute(Integer status) {
				 if(status==0){
			        	Log.e("1", "注册成功！");
			        	PreferenceUtils.getInstance().setLoginUser(TelString);
			        	Toast.makeText(context, "注册成功！", Toast.LENGTH_LONG)
						.show();
			        	Map<String, String> params = new HashMap<String, String>();
						params.put("phone", TelString);
				        params.put("pwd", pwd);
				        UserLogin(params);
			        }else if(status==3){
			        	Toast.makeText(context, "该用户名已存在！", Toast.LENGTH_LONG)
						.show();
			        }else  if(status==-1){	
		    			Toast toast=Toast.makeText(context, "网络不稳定，请稍后再试", Toast.LENGTH_LONG);
		    			//toast.setGravity(Gravity.CENTER, 0, 0);   
		    	        toast.show(); 
		            }else{
			        	Toast.makeText(context, "注册失败，请稍后重试！！", Toast.LENGTH_LONG)
						.show();
			        } 
			};

		}.execute(params);}
	
	/**
	 * 执行异步任务
	 * 
	 * @param params
	 *      
	 */
	public void UserLogin(final Object... params) {
		new AsyncTask<Object, Object,Integer>() {

			@SuppressWarnings("unchecked")
			@Override
			protected Integer doInBackground(Object... params) {
				int status=-1;
			  try {
		    	        status=HttpUnit.sendLoginRequest((Map<String, String>) params[0]);
		    	        
		    	        Constant.isLogin=false;
		    	        return status;
					
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Log.e("1", e.toString());
					return -1;
//					e.printStackTrace();
				}
			}
			protected void onPostExecute(Integer status) {
				 if(status==0){
			        	Log.e("1", "登录成功！");
			        	PreferenceUtils.getInstance().setLoginUser(user);
			        	PreferenceUtils.getInstance().setLoginPwd(pwd);
			        	Constant.isLogin=true;
			        	Constant.LoginTime=System.currentTimeMillis()/1000;
			        	Intent intent = new Intent();
			    		intent.setClass(context, FillInUserInfoActivity.class);
			    		startActivity(intent);
			        }
			        else{
			        	Toast.makeText(context, "发生异常！", Toast.LENGTH_LONG)
						.show();	
			        	finish();
			        }
				
			    };

		}.execute(params);}
	
	
	private void initSMSReceiver(){
		mSMSBroadcastReceiver = new SMSBroadcastReceiver();

        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter(ACTION);
        intentFilter.setPriority(Integer.MAX_VALUE);
        //注册广播
        this.registerReceiver(mSMSBroadcastReceiver, intentFilter);

        mSMSBroadcastReceiver.setOnReceivedMessageListener(new SMSBroadcastReceiver.MessageListener() {
            @Override
            public void onReceived(String message) {
            	edit_code.setText(getStringNum(message));
            }
        });
	}
     //初始化短信验证
	 public void initSMSSDK(){
	    	SMSSDK.initSDK(this, Constant.APPKEY, Constant.APPSECRET);
			final Handler handler = new Handler(this);
			eventHandler = new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					Message msg = new Message();
					msg.arg1 = event;
					msg.arg2 = result;
					msg.obj = data;
					handler.sendMessage(msg);
				}
			};
			// 注册回调监听接口
			SMSSDK.registerEventHandler(eventHandler);
	    }
	// 提交用户信息
//		private void registerUser(String country, String phone) {
//			Random rnd = new Random();
//			int id = Math.abs(rnd.nextInt());
//			String uid = String.valueOf(id);
//			String nickName = "SmsSDK_User_" + uid;
////			String avatar = AVATARS[id % 12];
//			SMSSDK.submitUserInfo(uid, nickName, null, country, phone);
//		}

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
		
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.e("event", "event="+event);
			if (result == SMSSDK.RESULT_COMPLETE) {
				//短信注册成功后，返回MainActivity,然后提示新好友
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
					Map<String, String> params = new HashMap<String, String>();
					pwd=edit_pwd.getText().toString();
					params.put("phone", TelString);
			        params.put("pwd", pwd);
			        
					UserRegister(params);
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
				}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
					Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
					
				}
			} else {
				((Throwable) data).printStackTrace();
//				int resId = getStringRes(context, "smssdk_network_error");
				try{
					String jsonString=data.toString().replace("java.lang.Throwable:", "").trim();
					JSONObject json= JSONObject.parseObject(jsonString);
					int err=json.getInteger("status");
					if(err==519){
						Toast.makeText(context, "今日验证请求次数超上限", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(context, "验证码错误！", Toast.LENGTH_SHORT).show();
					}
					Log.e("1", err+"");
				}catch(Exception ex){
					ex.printStackTrace();
					Log.e("1", data.toString());
					Toast.makeText(context, "验证码错误！", Toast.LENGTH_SHORT).show();
				}
				
			}
			return false;
		}
		
		@Override
		protected void onDestroy()
		{
			super.onDestroy();
			SMSSDK.unregisterEventHandler(eventHandler);
			if(mSMSBroadcastReceiver!=null){
				this.unregisterReceiver(mSMSBroadcastReceiver);
			}
			Log.e("1", "注册Destroy");

		}
		
		public  String getStringNum(String str) {
			String regEx="[^0-9]";   
			Pattern p = Pattern.compile(regEx);   
			Matcher m = p.matcher(str);   
			return m.replaceAll("").trim();
	   }

}
