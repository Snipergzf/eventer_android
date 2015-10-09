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
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.receiver.SMSBroadcastReceiver;
import com.eventer.app.ui.base.BaseActivity;
import com.eventer.app.util.PreferenceUtils;
import com.eventer.app.view.MyCountTimer;
import com.eventer.app.view.TitleBar;

@SuppressLint("ShowToast")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class RegisterActivity extends BaseActivity implements OnClickListener, Callback {
    private EditText edit_tel,edit_code,edit_pwd;
    private TextView btn_send_code;
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
		TitleBar.setTitleBar(this,"ע��");
		setContentView(R.layout.activity_register);
		
		edit_tel=(EditText)findViewById(R.id.edit_tel);
		edit_code=(EditText)findViewById(R.id.edit_security_code);
		edit_pwd=(EditText)findViewById(R.id.edit_pwd);
		btn_next=(Button)findViewById(R.id.btn_next);
		btn_tel_clear=(ImageButton)findViewById(R.id.btn_tel_clear);
		btn_code_clear=(ImageButton)findViewById(R.id.btn_security_code_clear);
		btn_pwd_clear=(ImageButton)findViewById(R.id.btn_pwd_clear);
		
		btn_send_code=(TextView)findViewById(R.id.btn_send_code);
		context=RegisterActivity.this;
		
		init();
		initSMSSDK();
		
	}
	/***
	 * ���ؼ�����¼���Ӧ
	 */
	public void init(){
		/***
		 * �����˺�����������
		 */
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
		/***
		 * ������֤������������
		 */
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
		
		/***
		 * ������������������
		 */
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
	    //ע����������
		btn_next.setOnClickListener(this);
		btn_tel_clear.setOnClickListener(this);
		btn_code_clear.setOnClickListener(this);
		btn_pwd_clear.setOnClickListener(this);
		btn_send_code.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		//"ע��"��ť
		case R.id.btn_next:
			//������֤��ȷ��
			SMSSDK.submitVerificationCode("86", TelString, edit_code.getText().toString());
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("phone", edit_tel.getText().toString());
//	        params.put("pwd", edit_pwd.getText().toString());
//			UserRegister(params);
			break;
        case R.id.btn_tel_clear:
        	edit_tel.setText("");
			break;
        case R.id.btn_security_code_clear:
        	edit_code.setText("");
        	break;
        case R.id.btn_pwd_clear:
        	edit_pwd.setText("");
        	break;
        //"������֤��"��ť
        case R.id.btn_send_code:
        	if(IsUserCheck){
        		MyCountTimer timeCount = new MyCountTimer(btn_send_code, 0xff33b5e5, 0xff969696);//������������ɫֵ
        	    timeCount.start();
        	    if(mSMSBroadcastReceiver==null){
        	    	//ע����Ž��յ�BroadcastReceiver
    				initSMSReceiver();
    			}
        	    TelString=edit_tel.getText().toString();
        	   //������֤������
    			SMSSDK.getVerificationCode("86",TelString);
    			
        	}else{
        		Toast toast=Toast.makeText(RegisterActivity.this, "�������������ֻ����룡", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.TOP, 0, 250);
				toast.show();
        	}
        	
        	break;
		default:
			break;
		}
	}
	
	
	/**
	 * ע���˺�
	 * ����Ϊ��phone���͡�pwd��
	 *      
	 */
	public void UserRegister() {
		Map<String, String> params = new HashMap<String, String>();
		pwd=edit_pwd.getText().toString();
		params.put("phone", TelString);
        params.put("pwd", pwd);	
        LoadDataFromHTTP task = new LoadDataFromHTTP(
                context, Constant.URL_REGISTER, params);
        task.getData(new DataCallBack() {
			
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				 try {
	                    int code = data.getInteger("status");
	                    switch (code) {
						case 0:
							Log.e("1", "ע��ɹ���");
				        	PreferenceUtils.getInstance().setLoginUser(TelString);
				        	Toast.makeText(context, "ע��ɹ���", Toast.LENGTH_LONG)
							.show();
					        UserLogin();
							break;
						case 3:
							Toast.makeText(context, "���û����Ѵ��ڣ�", Toast.LENGTH_LONG)
							.show();
                    	   break;
						default:
							Toast.makeText(context, "ע��ʧ�ܣ����Ժ����ԣ���", Toast.LENGTH_LONG)
							.show();
							break;
						}

	                } catch (JSONException e) {

	                    Toast.makeText(context, "���ݽ�������...",
	                            Toast.LENGTH_SHORT).show();
	                    e.printStackTrace();
	                }
			}
		});
	}
	
	
	/**
	 * ִ���첽����
	 * ��¼ϵͳ
	 *  ����Ϊ��phone��,��pwd��  ,"imei"  
	 */
	public void UserLogin() {
		Map<String, String> params = new HashMap<String, String>();
		pwd=edit_pwd.getText().toString();
		params.put("phone", TelString);
        params.put("pwd", pwd);	
        params.put("imei", PreferenceUtils.getInstance().getDeviceId());
        LoadDataFromHTTP task = new LoadDataFromHTTP(
                context, Constant.URL_LOGIN, params);
        task.getData(new DataCallBack() {
			
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				 try {
	                    int code = data.getInteger("status");
	                    switch (code) {
						case 0:
							Log.e("1", "��¼�ɹ���");
				        	PreferenceUtils.getInstance().setLoginUser(user);
				        	PreferenceUtils.getInstance().setLoginPwd(pwd);
				        	Constant.isLogin=true;
				        	Constant.LoginTime=System.currentTimeMillis()/1000;
				        	String userinfo=data.getString("user_action");
				        	JSONObject jsonLogin= JSONObject.parseObject(userinfo);;
				        	Constant.UID=jsonLogin.getInteger("uid")+"";
				        	Constant.TOKEN=jsonLogin.getString("token");
				        	Intent intent = new Intent();
				    		intent.setClass(context, FillInUserInfoActivity.class);
				    		startActivity(intent);
							break;
						default:
							Toast.makeText(context, "�����쳣��", Toast.LENGTH_LONG)
							.show();	
				        	finish();
						}

	                } catch (JSONException e) {

	                    Toast.makeText(context, "���ݽ�������...",
	                            Toast.LENGTH_SHORT).show();
	                    e.printStackTrace();
	                }
			}
		});
	}
	
	/**
	 * ��ʼ�����Ŷ��Ž��յ�BroadcastReceiver
	 * ͨ����BroadcastReceiver��������֤���ţ�����ȡ��֤�룬�Զ���д��֤��
	 */
	private void initSMSReceiver(){
		mSMSBroadcastReceiver = new SMSBroadcastReceiver();

        //ʵ����������������Ҫ���˵Ĺ㲥
        IntentFilter intentFilter = new IntentFilter(ACTION);
        intentFilter.setPriority(Integer.MAX_VALUE);
        //ע��㲥
        this.registerReceiver(mSMSBroadcastReceiver, intentFilter);
        mSMSBroadcastReceiver.setOnReceivedMessageListener(new SMSBroadcastReceiver.MessageListener() {
            @Override
            public void onReceived(String message) {
            	edit_code.setText(getStringNum(message));
            }
        });
	}
     //��ʼ��������֤
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
			// ע��ص������ӿ�
			SMSSDK.registerEventHandler(eventHandler);
	    }

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
		
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.e("event", "event="+event);
			if (result == SMSSDK.RESULT_COMPLETE) {
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//��֤����֤�ɹ�		        
					UserRegister();
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					Toast.makeText(getApplicationContext(), "��֤���Ѿ�����", Toast.LENGTH_SHORT).show();
				}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//����֧�ַ�����֤��Ĺ����б�
					Toast.makeText(getApplicationContext(), "��ȡ�����б�ɹ�", Toast.LENGTH_SHORT).show();
					
				}
			} else {
				((Throwable) data).printStackTrace();
				try{
					String jsonString=data.toString().replace("java.lang.Throwable:", "").trim();
					JSONObject json= JSONObject.parseObject(jsonString);
					int err=json.getInteger("status");
					if(err==519){
						Toast.makeText(context, "������֤�������������", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(context, "��֤�����", Toast.LENGTH_SHORT).show();
					}
					Log.e("1", err+"");
				}catch(Exception ex){
					ex.printStackTrace();
					Log.e("1", data.toString());
					Toast.makeText(context, "��֤�����", Toast.LENGTH_SHORT).show();
				}
				
			}
			return false;
		}
		
		@Override
		protected void onDestroy()
		{
			super.onDestroy();
			//ע����֤��SDK���Ͷ��Ž��յ�BroadcastReceiver
			SMSSDK.unregisterEventHandler(eventHandler);
			if(mSMSBroadcastReceiver!=null){
				this.unregisterReceiver(mSMSBroadcastReceiver);
			}
			Log.e("1", "ע��Destroy");

		}
		/***
		 * ��ȡ�����е���֤��
		 * @param str
		 * @return
		 */
		public  String getStringNum(String str) {
			String regEx="[^0-9]";   
			Pattern p = Pattern.compile(regEx);   
			Matcher m = p.matcher(str);   
			return m.replaceAll("").trim();
	   }

}
