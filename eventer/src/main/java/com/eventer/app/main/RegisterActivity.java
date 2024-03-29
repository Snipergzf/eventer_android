package com.eventer.app.main;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
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

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.http.HttpParamUnit;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.receiver.SMSBroadcastReceiver;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.util.MD5Util;
import com.eventer.app.util.PreferenceUtils;
import com.eventer.app.util.RandomUtil;
import com.eventer.app.view.MyCountTimer;
import com.eventer.app.view.MyToast;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

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
	private String user = "",pwd = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
        setBaseTitle(R.string.register);
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
	 * 初始化控件，给控件添加事件响应
	 */
	public void init(){
		/***
		 * 监听账号输入框的输入
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
					btn_next.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));
					btn_next.setTextColor(ContextCompat.getColor(context, R.color.caldroid_white));
					btn_next.setClickable(true);
				}else{
					btn_next.setBackground(ContextCompat.getDrawable(context, R.drawable.button_gray));
					btn_next.setTextColor(ContextCompat.getColor(context, R.color.caldroid_darker_gray));
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
		 * 监听验证码输入框的输入
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
					btn_next.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));
					btn_next.setTextColor(ContextCompat.getColor(context, R.color.caldroid_white));
					btn_next.setClickable(true);
				}else{
					btn_next.setBackground(ContextCompat.getDrawable(context, R.drawable.button_gray));
					btn_next.setTextColor(ContextCompat.getColor(context, R.color.caldroid_darker_gray));
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
		 * 监听密码输入框的输入
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
					btn_next.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));
					btn_next.setTextColor(ContextCompat.getColor(context, R.color.caldroid_white));
					btn_next.setClickable(true);
				}else{
					btn_next.setBackground(ContextCompat.getDrawable(context, R.drawable.button_gray));
					btn_next.setTextColor(ContextCompat.getColor(context, R.color.caldroid_darker_gray));
					btn_next.setClickable(false);
				}
			}
		});
		//注册点击监听器
		btn_next.setOnClickListener(this);
		btn_tel_clear.setOnClickListener(this);
		btn_code_clear.setOnClickListener(this);
		btn_pwd_clear.setOnClickListener(this);
		btn_send_code.setOnClickListener(this);
	}

	/**
	 *处理各个控件的点击事件
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			//"注册"按钮
			case R.id.btn_next:
			    TelString = edit_tel.getText().toString();
				pwd = edit_pwd.getText().toString();
//			    UserRegister();
				//发送验证码确认
				SMSSDK.submitVerificationCode("86", TelString, edit_code.getText().toString());
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
			//"发送验证码"按钮
			case R.id.btn_send_code:
				if(IsUserCheck){
					MyCountTimer timeCount = new MyCountTimer(btn_send_code, 0xff33b5e5, 0xff969696);//传入了文字颜色值
					timeCount.start();
					if(mSMSBroadcastReceiver==null){
						//注册短信接收的BroadcastReceiver
						initSMSReceiver();
					}
					TelString = edit_tel.getText().toString();
					pwd = edit_pwd.getText().toString();
					//发送验证码请求
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
	 * 注册账号
	 * 参数为“phone”和“pwd”
	 *pwd
	 */
	public void UserRegister() {

		if(TextUtils.isEmpty(pwd)){
			MyToast.makeText(context, "请填写密码~", Toast.LENGTH_SHORT).show();
			return;
		}
		LoadDataFromHTTP task = new LoadDataFromHTTP(
				context, Constant.URL_REGISTER, HttpParamUnit.register(TelString, pwd));
		task.getData(new DataCallBack() {

			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					int code = data.getInteger("status");
					switch (code) {
						case 0:
							Log.e("1", "注册成功！");
							PreferenceUtils.getInstance().setLoginUser(TelString);
							MyToast.makeText(context, "注册成功！", Toast.LENGTH_LONG)
									.show();
							UserLogin();
							break;
						case 3:
							MyToast.makeText(context, "该用户已注册过！", Toast.LENGTH_LONG)
									.show();
							break;
						case 25:
							MyToast.makeText(context, "系统维护中，请稍候再来注册~", Toast.LENGTH_LONG)
									.show();
							break;
						default:
							if(!Constant.isConnectNet){
								MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
							}else{
								MyToast.makeText(context, "注册失败，请稍后重试！！", Toast.LENGTH_LONG)
										.show();
							}

							break;
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}catch (Exception e) {
					// TODO: handle exception
					MyToast.makeText(context, "注册失败，请稍后重试！！", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}


	/**
	 * 执行异步任务
	 * 登录系统
	 *  参数为“phone”,“pwd”  ,"imei"  
	 */
	public void UserLogin() {
		final String pwdMd5 = MD5Util.getMD5(pwd);
		LoadDataFromHTTP task = new LoadDataFromHTTP(
				context, Constant.URL_LOGIN_NEW, HttpParamUnit.login(TelString, pwdMd5));
		task.getData(new DataCallBack() {

			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					int code = data.getInteger("status");
					switch (code) {
						case 0:
							PreferenceUtils.getInstance().setLoginUser(user);
							PreferenceUtils.getInstance().setLoginPwd(pwdMd5);
							Constant.isLogin = true;
							Constant.LoginTime = System.currentTimeMillis()/1000;
							String userinfo = data.getString("user_action");
							JSONObject jsonLogin = JSONObject.parseObject(userinfo);
							Constant.UID = jsonLogin.getInteger("uid") + "";
							Constant.TOKEN = jsonLogin.getString("token");
							initNick();
							break;
						default:
							Intent intent = new Intent();
							intent.setClass(context, LoginActivity.class);
							startActivity(intent);
							finish();
					}

				} catch (Exception e) {
					e.printStackTrace();
					Intent intent = new Intent();
					intent.setClass(context, LoginActivity.class);
					startActivity(intent);
				}
			}
		});
	}


	/**
	 * 为用户设置随机的昵称
	 */
	public void initNick() {
		final String newNick = RandomUtil.getRandomNick();
		Map<String, String> map = new HashMap<>();
		map.put("sex", "");
		map.put("uid", Constant.UID+"");
		map.put("token", Constant.TOKEN);
		map.put("name", newNick);
		map.put("email", "");
		map.put("grade","");
		map.put("school", "");
		map.put("major", "");
		map.put("class", "");
		map.put("user_rank", "0");
		LoadDataFromHTTP task = new LoadDataFromHTTP(
				context, Constant.URL_UPDATE_SELFINFO, map);

		task.getData(new com.eventer.app.http.LoadDataFromHTTP.DataCallBack() {

			@SuppressLint("ShowToast")
			@Override
			public void onDataCallBack(JSONObject data) {
				try {
					int code = data.getInteger("status");
					if (code == 0) {
						LocalUserInfo.getInstance(context)
								.setUserInfo("nick", newNick);
					} else {
						if(!Constant.isConnectNet){
							Toast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();

						}
					}
				} catch (JSONException e) {

					Toast.makeText(context, "数据解析错误...",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Intent intent = new Intent();
					intent.setClass(context, MainActivity.class);
					startActivity(intent);
				}

			}

		});
	}



	/**
	 * 初始化短信短信接收的BroadcastReceiver
	 * 通过该BroadcastReceiver，接收验证短信，并获取验证码，自动填写验证码
	 */
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

	/***
	 * 处理短信验证码的结果
	 */
	@Override
	public boolean handleMessage(Message msg) {

		int event = msg.arg1;
		int result = msg.arg2;
		Object data = msg.obj;
		Log.e("event", "event="+event);
		if (result == SMSSDK.RESULT_COMPLETE) {
			if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//验证码验证成功		        
				UserRegister();
			} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
				Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
			}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
				Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();

			}
		} else {
			((Throwable) data).printStackTrace();
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

	/***
	 * 获取短信中的验证码
	 */
	public  String getStringNum(String str) {
		String regEx="[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}


	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//注销验证码SDK，和短信接收的BroadcastReceiver
		SMSSDK.unregisterEventHandler(eventHandler);
		if(mSMSBroadcastReceiver!=null){
			this.unregisterReceiver(mSMSBroadcastReceiver);
		}
		Log.e("1", "注册Destroy");

	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
