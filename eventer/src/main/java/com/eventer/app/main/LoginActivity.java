package com.eventer.app.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.util.MD5Util;
import com.eventer.app.util.PreferenceUtils;
import com.eventer.app.view.CircleProgressBar;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends BaseFragmentActivity implements OnClickListener {

	Button btn_login;
	private ImageButton btn_user_clear,btn_pwd_clear;
	private EditText edit_user,edit_pwd;
	TextView tv_login_help,tv_newuser;
	AlertDialog dialog;
	private Context context;
	private String pwd;

	public static boolean isActive=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		isActive=true;
		context=this;
		Constant.isExist=false;
//		SMSSDK.initSDK(LoginActivity.this, Constant.APPKEY, Constant.APPSECRET);
		initView();
	}

	private void initView() {
		btn_login = (Button)findViewById(R.id.btn_login);
		btn_pwd_clear = (ImageButton)findViewById(R.id.btn_pwd_clear);
		btn_user_clear =(ImageButton)findViewById(R.id.btn_user_clear);
		edit_user = (EditText)findViewById(R.id.edit_user);
		edit_pwd = (EditText)findViewById(R.id.edit_pwd);
		tv_login_help =(TextView)findViewById(R.id.tv_login_help);
		tv_newuser =(TextView)findViewById(R.id.tv_login_newuser);
		TextView tv_tourist = (TextView) findViewById(R.id.tv_tourist);
		tv_tourist.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_tourist.getPaint().setAntiAlias(true);

		/***
		 * 监听账号输入框的输入
		 * 不为空时，显示清除按钮，否则，隐藏
		 */
		edit_user.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int len=s.length();
				if(len>0){
					btn_user_clear.setVisibility(View.VISIBLE);
				}else{
					btn_user_clear.setVisibility(View.GONE);
					edit_pwd.setText("");
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
		 * 不为空时，显示清除按钮，否则，隐藏
		 */
		edit_pwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				int len=s.length();
				if(len>0){
					btn_pwd_clear.setVisibility(View.VISIBLE);
				}else{
					btn_pwd_clear.setVisibility(View.GONE);
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
		String user=PreferenceUtils.getInstance().getLoginUser();
		if(user!=null && !user.equals("") && !user.equals("0")){
			edit_user.setText(user);
			edit_pwd.setFocusable(true);
			edit_pwd.requestFocus();
			edit_pwd.setText("");
		}
		btn_pwd_clear.setOnClickListener(this);
		btn_user_clear.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		tv_login_help.setOnClickListener(this);
		tv_newuser.setOnClickListener(this);
		tv_tourist.setOnClickListener(this);
	}


	private void showDialog(){
		dialog = new AlertDialog.Builder(this).create();
		dialog.show();
		Window window = dialog.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.upload_dialog);
		CircleProgressBar progress=(CircleProgressBar)window.findViewById(R.id.progress);
		progress.setColorSchemeResources(android.R.color.holo_orange_light);
		TextView info=(TextView)window.findViewById(R.id.tv_info);
		info.setText("正在登录中~");
	}
	/***
	 * 各个控件的点击事件
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btn_login:
				if(!TextUtils.isEmpty(edit_user.getText())&&!TextUtils.isEmpty(edit_pwd.getText())){
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
					showDialog();
					UserLogin();
				}else{
					Toast.makeText(context, "请完善登录信息！", Toast.LENGTH_SHORT).show();
				}
				break;
			//清除密码输入框
			case R.id.btn_pwd_clear:
				edit_pwd.setText("");
				break;
			//清除账号输入框
			case R.id.btn_user_clear:
				edit_user.setText("");
				edit_pwd.setText("");
				break;
			//"无法登陆"按钮？
			case R.id.tv_login_help:
				startActivity(new Intent().setClass(context, CheckPhoneActivity.class));
				break;
			//"新用户"按钮，跳转至注册界面
			case R.id.tv_login_newuser:
				Intent intent1=new Intent();
				intent1.setClass(context, RegisterActivity.class);
				startActivity(intent1);
				break;
			case R.id.tv_tourist:
				Intent intent = new Intent();
				intent.setClass(context, MainActivity.class);
				Constant.UID = "0";
				Constant.TOKEN = "tourists";
				startActivity(intent);
				finish();
				break;
			default:
				break;
		}
	}

	/**
	 * 执行异步任务
	 * 登录系统
	 *  参数为“phone”,“pwd”  ,"imei"
	 */
	public void UserLogin() {
		pwd=edit_pwd.getText().toString();

		if(TextUtils.isEmpty(pwd)){
			Toast.makeText(context, "请填写密码~", Toast.LENGTH_SHORT).show();
			return;
		}else{
			pwd = MD5Util.getMD5(pwd);
		}
		Map<String, String> params = new HashMap<>();
		params.put("phone", edit_user.getText().toString());
		params.put("pwd", pwd);
		params.put("imei", PreferenceUtils.getInstance().getDeviceId());
		LoadDataFromHTTP task = new LoadDataFromHTTP(
				context, Constant.URL_LOGIN_NEW, params);
		task.getData(new DataCallBack() {

			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					int code = data.getInteger("status");
					switch (code) {
						case 0:
							Log.e("1", "登录成功！");
							PreferenceUtils.getInstance().setLoginUser(edit_user.getText().toString());
							PreferenceUtils.getInstance().setLoginPwd(pwd);
							Constant.isLogin = true;
							Constant.LoginTime = System.currentTimeMillis() / 1000;
							JSONObject jsonLogin = data.getJSONObject("user_action");
							Constant.UID = jsonLogin.getInteger("uid") + "";
							PreferenceUtils.getInstance().setUserId(Constant.UID);
							Log.e("1", Constant.UID + "---" + PreferenceUtils.getInstance().getUserId());
							Constant.TOKEN = jsonLogin.getString("token");
							initSelfInfo();
							MobclickAgent.onProfileSignIn(Constant.UID);
							break;
						case 1:
							if(dialog!=null)
								dialog.cancel();
							Toast.makeText(context, "不存在该用户", Toast.LENGTH_LONG)
									.show();
							break;
						case 2:
							if(dialog!=null)
								dialog.cancel();
							Toast.makeText(context, "密码错误！", Toast.LENGTH_LONG)
									.show();
						case 23:
							if(dialog!=null)
								dialog.cancel();
							Toast toast = Toast.makeText(context, "登录失败！该用户已经在其他设备登录！", Toast.LENGTH_LONG);
							//toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							break;
						case -1:
							if(dialog!=null)
								dialog.cancel();
							Toast.makeText(context, "登录失败", Toast.LENGTH_LONG)
									.show();
							break;
						default:
							if(dialog!=null)
								dialog.cancel();
							Toast.makeText(context, "登录失败，请稍后重试！！", Toast.LENGTH_LONG)
									.show();
					}

				} catch (JSONException e) {

					Toast.makeText(context, "数据解析错误...",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					if(dialog!=null)
						dialog.cancel();
				} catch (Exception e) {
					// TODO: handle exception
					if(dialog!=null)
						dialog.cancel();
				}finally {
					if(dialog!=null)
						dialog.cancel();
				}
			}
		});
	}
	/***
	 * 从服务器获取个人信息，
	 * 将信息写入LocalUserInfo
	 */
	private void initSelfInfo(){
		Map<String, String> maps = new HashMap<>();
		maps.put("uid", Constant.UID+"");
		LoadDataFromHTTP task = new LoadDataFromHTTP(
				context, Constant.URL_GET_SELFINFO, maps);
		task.getData(new DataCallBack() {

			@SuppressLint("ShowToast")
			@Override
			public void onDataCallBack(JSONObject data) {
				try {
					int code = data.getInteger("status");
					Log.e("1", code+"");
					if (code == 0) {
						JSONObject json=data.getJSONObject("user_action");
						JSONObject info=json.getJSONObject("info");
						String name=info.getString("name");
						if(name!=null&& !name.equals("")){
							LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("nick", name);
							LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("sex", info.getString("sex"));
							LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("email", info.getString("email"));
							LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("user_rank", "0");
							LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("grade", info.getString("grade"));
							LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("school", info.getString("school"));
							LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("class", info.getString("class"));
							LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("major", info.getString("major"));
							LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("avatar", info.getString("avatar"));

							Intent intent = new Intent();
							intent.setClass(context, MainActivity.class);
							startActivity(intent);
							finish();
						}else{
							Toast.makeText(context, "您尚未完善个人信息，请完善个人信息！",
									Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							intent.setClass(context, FillInUserInfoActivity.class);
							startActivity(intent);
							finish();
						}

					}  else {
                       if(!Constant.isConnectNet){
						   Toast.makeText(context, getText(R.string.no_network),
								   Toast.LENGTH_SHORT).show();
					   }else{
						   Toast.makeText(context, "服务器繁忙请重试...",
								   Toast.LENGTH_SHORT).show();
					   }
					}

				}catch (JSONException e) {

					Toast.makeText(context, "数据解析错误...",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}catch (Exception e) {
					// TODO: handle exception
				}

			}

		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(dialog!=null)
			dialog.cancel();
		Log.e("1", "login--destory");
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("LoginScreen");
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("LoginScreen");
		MobclickAgent.onResume(this);
	}


}
