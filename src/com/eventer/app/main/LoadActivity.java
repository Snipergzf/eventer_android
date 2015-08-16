package com.eventer.app.main;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.EventDao;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.task.LoadDataFromHTTP;
import com.eventer.app.task.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.ui.base.BaseActivity;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.util.PreferenceUtils;

public class LoadActivity extends BaseActivity {
	private static final int sleepTime = 2000;
	private Context context;
	private LinearLayout ll_login;
	private String user,pwd;
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
		PreferenceUtils.init(context);
		initFile() ;
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		view.startAnimation(animation);
		ll_login=(LinearLayout)view.findViewById(R.id.login_ll);
	}	

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {
			public void run() {
				//PreferenceUtils.getInstance().clearPreference();
				user=PreferenceUtils.getInstance().getLoginUser();
				pwd=PreferenceUtils.getInstance().getLoginPwd();
				if (user!=null&&user!=""&&pwd!=null&&pwd!="") {
					// ** 免登陆情况 加载所有本地群和会话
					//不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
					//加上的话保证进了主页面会话和群组都已经load完毕
					long start = System.currentTimeMillis();
					//加载数据
					Map<String, String> params = new HashMap<String, String>();  
			        params.put("pwd", pwd);
			        params.put("phone", user);
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
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(LoadActivity.this, LoginActivity.class));
					finish();
				}else{
					ll_login.setVisibility(View.VISIBLE);
				}	
			}
		}).start();

	}
	
//	/**
//	 * 获取当前应用程序的版本号
//	 */
//	private String getVersion() {
//		PackageManager pm = getPackageManager();
//		try {
//			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
//			String version = packinfo.versionName;
//			return version;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//			return "版本号错误";
//		}
//	}
	 @SuppressLint("SdCardPath")
     public void initFile() {

      File dir = new File("/sdcard/eventer");

         if (!dir.exists()) {
             dir.mkdirs();
         }
     } 
	 
	public void login(View v) {
		Intent intent = new Intent();
		intent.setClass(LoadActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	public void register(View v) {
		Intent intent = new Intent();
		intent.setClass(LoadActivity.this, RegisterActivity.class);
		startActivity(intent);
	}
		
		
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
//						e.printStackTrace();
					}
				}
				protected void onPostExecute(Integer status) {
					 if(status==0){
				        	Log.e("1", "登录成功！");
				        	PreferenceUtils.getInstance().setLoginUser(user);
				        	PreferenceUtils.getInstance().setLoginPwd(pwd);
				        	//PreferenceUtils.getInstance().clearPreference();
				        	Constant.isLogin=true;
				        	Constant.LoginTime=System.currentTimeMillis()/1000;
//				        	EventDao dao=new EventDao(context);
//				        	List<String> list=dao.getEventIDList();
//				        	MyApplication.getInstance().setCacheByKey("EventList", list);
				        	initSelfInfo();
//				        	Intent intent = new Intent();
//				    		intent.setClass(LoadActivity.this, MainActivity.class);
//				    		startActivity(intent);
//				    		finish();
				        }else if(status==1){
				        	Toast.makeText(context, "该用户不存在！", Toast.LENGTH_LONG)
							.show();
				        }
				        else{
				        	Toast.makeText(context, "登录错误！", Toast.LENGTH_LONG)
							.show();	
				        	Constant.isLogin=false;
				        	Intent intent = new Intent();
				    		intent.setClass(LoadActivity.this, MainActivity.class);
				    		startActivity(intent);
				    		finish();
				        }
					
				    };

			}.execute(params);}
		
		
		private void initSelfInfo(){
			 Map<String, String> maps = new HashMap<String, String>();		 
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
		                    	if(name!=null&&name!=""){
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
		                    }else if (code == 2) {

		                        Toast.makeText(context, "信息获取失败失败...",
		                                Toast.LENGTH_SHORT).show();
		                    } else if (code == 3) {

		                        Toast.makeText(context, "图片上传失败...",
		                                Toast.LENGTH_SHORT).show();

		                    } else {

		                        Toast.makeText(context, "服务器繁忙请重试...",
		                                Toast.LENGTH_SHORT).show();
		                    }

		                } catch (JSONException e) {

		                    Toast.makeText(context, "数据解析错误...",
		                            Toast.LENGTH_SHORT).show();
		                    e.printStackTrace();
		                }

		            }

		        });

		}
		
}
