package com.eventer.app.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.service.CheckInternetService;
import com.eventer.app.ui.base.BaseFragmentActivity;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.util.PreferenceUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LoadActivity extends BaseFragmentActivity {

	private static final int sleepTime = 2000;
	private Context context;
	private LinearLayout ll_login;
	//	private RelativeLayout load_root;
	private String user,pwd;
	//	CircleProgressBar progress;
	@Override
	protected void onCreate(Bundle arg0) {
		//隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//隐藏状态栏
		//定义全屏参数
		int flag=WindowManager.LayoutParams.FLAG_FULLSCREEN;
		//获得当前窗体对象
		Window window=LoadActivity.this.getWindow();
		//设置当前窗体为全屏显示
		window.setFlags(flag, flag);
		final View view = View.inflate(this, R.layout.activity_load, null);
		setContentView(view);
		super.onCreate(arg0);
		context=this;
		Constant.isExist=false;
		PreferenceUtils.init(context);
		initFile() ;
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		view.startAnimation(animation);
		ll_login=(LinearLayout)view.findViewById(R.id.login_ll);
//		load_root=(RelativeLayout)view.findViewById(R.id.load_root);
//		progress=(CircleProgressBar)view.findViewById(R.id.progress);
//		progress.setColorSchemeResources(android.R.color.holo_orange_light);
		context.startService(new Intent(context, CheckInternetService.class));//make sure the net is truly available

		/***
		 * 判断登录状态
		 */
		new Thread(new Runnable() {
			public void run() {
				//读取PreferenceUtils中的登录状态
				user=PreferenceUtils.getInstance().getLoginUser();
				pwd=PreferenceUtils.getInstance().getLoginPwd();
				if (user!=null&& !user.equals("") &&pwd!=null&& !pwd.equals("")) {
					// 保存了账号和密码，不跳转至登录界面，直接登录
					long start = System.currentTimeMillis();
					//加载数据
					Map<String, String> params = new HashMap<>();
					params.put("pwd", pwd);
					params.put("phone", user);
					//获取手机的唯一标识码
					params.put("imei", PreferenceUtils.getInstance().getDeviceId());
					UserLogin(params);
					long costTime = System.currentTimeMillis() - start;
					//等待sleeptime时长
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}else if(user!=null){
					//只保存了账号（切换账号），跳转至登录界面
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					startActivity(new Intent(LoadActivity.this, LoginActivity.class));
					finish();
				}else{
					//初次打开系统，显示登录或注册选项
					ll_login.setVisibility(View.VISIBLE);
				}
			}
		}).start();

	}

	@Override
	protected void onStart() {
		super.onStart();

	}
	@SuppressLint("SdCardPath")
	public void initFile() {

		File dir = new File("/sdcard/eventer");

		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	/***
	 * 登录按钮的响应事件
	 * 跳转至登陆界面
	 */
	public void login(View v) {
		Intent intent = new Intent();
		intent.setClass(LoadActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	/***
	 * 注册按钮的响应事件
	 * 跳转至注册界面
	 */
	public void register(View v) {
		Intent intent = new Intent();
		intent.setClass(LoadActivity.this, RegisterActivity.class);
		startActivity(intent);
	}


	/**
	 * 执行异步任务
	 * 登录系统
	 * 参数为phone,pwd,imei
	 *
	 */
	public void UserLogin(final Object... params) {
		new AsyncTask<Object, Object,Integer>() {
			@SuppressWarnings("unchecked")
			@Override
			protected Integer doInBackground(Object... params) {
				int status;
				try {
					status=HttpUnit.sendLoginRequest((Map<String, String>) params[0]);
					Constant.isLogin=false;
					return status;
				} catch (Exception e) {
					Log.e("1", e.toString());
					return -1;
				} catch (Throwable e) {
					// TODO: handle exception
					return -1;
				}
			}
			protected void onPostExecute(Integer status) {
				if(status==0){
					Log.e("1", "登录成功！"+Constant.UID);
					PreferenceUtils.getInstance().setLoginUser(user);
					PreferenceUtils.getInstance().setLoginPwd(pwd);
					Constant.isLogin=true;
					Constant.LoginTime=System.currentTimeMillis()/1000;

					MobclickAgent.onProfileSignIn(Constant.UID);
					//初始化个人信息
					initSelfInfo();

				}else if(status==1){
					Toast.makeText(context, "该用户不存在！", Toast.LENGTH_LONG)
							.show();
					Constant.isLogin=false;
					startActivity(new Intent().setClass(LoadActivity.this, LoginActivity.class));
					finish();
				}else  if(status==23){
					Toast toast=Toast.makeText(context, "登录失败！该用户已经在其他设备登录！", Toast.LENGTH_LONG);
					toast.show();
					Constant.isLogin=false;
					startActivity(new Intent().setClass(LoadActivity.this, LoginActivity.class));
					finish();
				}else  if(status==-1){
					String uid=PreferenceUtils.getInstance().getUserId();
					if(!TextUtils.isEmpty(uid)){
						startActivity(new Intent().setClass(LoadActivity.this, MainActivity.class));
						Constant.UID=uid;
						finish();
					}else{
						startActivity(new Intent().setClass(LoadActivity.this, LoginActivity.class));
						finish();
					}
					Toast toast=Toast.makeText(context, getText(R.string.no_network), Toast.LENGTH_LONG);
					toast.show();
					Constant.isLogin=false;
				} else{
					Toast.makeText(context, "登录错误！", Toast.LENGTH_LONG)
							.show();
					Constant.isLogin=false;
					Intent intent = new Intent();
					intent.setClass(LoadActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}

			}

		}.execute(params);}
	/***
	 * 从服务器获取个人信息，
	 * 将信息写入LocalUserInfo
	 */
	private void initSelfInfo(){
//			load_root.getBackground().setAlpha(100);
//			progress.setVisibility(View.VISIBLE);
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
							Intent intent = new Intent();
							intent.setClass(context, MainActivity.class);
							startActivity(intent);
							finish();
						}else{
							//该用户还未填写昵称，跳转至个人信息完善界面
							Toast.makeText(context, "您尚未完善个人信息，请完善个人信息！",
									Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							intent.setClass(context, FillInUserInfoActivity.class);
							startActivity(intent);
							finish();
						}

					} else if(code==1){
						Toast.makeText(context, "该用户不存在！",
								Toast.LENGTH_SHORT).show();
						startActivity(new Intent().setClass(LoadActivity.this, LoginActivity.class));
						finish();
					}else if (code == 2) {

						Toast.makeText(context, "信息获取失败失败...",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(context, MainActivity.class);
						startActivity(intent);
						finish();
					} else if (code == 3) {

						Toast.makeText(context, "图片上传失败...",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(context, MainActivity.class);
						startActivity(intent);
						finish();

					} else {
						if(!Constant.isConnectNet){
							Toast.makeText(context, getText(R.string.no_network),
									Toast.LENGTH_SHORT).show();
						}
						String uid=PreferenceUtils.getInstance().getUserId();
						if(!TextUtils.isEmpty(uid)){
							startActivity(new Intent().setClass(LoadActivity.this, MainActivity.class));
							Constant.UID=uid;
							finish();
						}else{
							startActivity(new Intent().setClass(LoadActivity.this, LoginActivity.class));
							finish();
						}
					}

				} catch (JSONException e) {
					Toast.makeText(context, "数据解析错误...",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					startActivity(new Intent().setClass(LoadActivity.this, MainActivity.class));
				}catch (Exception e) {
					// TODO: handle exception
				}

			}

		});

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("LoadScreen");
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("LoadScreen");
		MobclickAgent.onResume(this);
	}


}
