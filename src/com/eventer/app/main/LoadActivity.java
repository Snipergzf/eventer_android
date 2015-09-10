package com.eventer.app.main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.eventer.app.R;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
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
	       //���ر�����
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //����״̬��
        //����ȫ������
        int flag=WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //��õ�ǰ�������
        Window window=LoadActivity.this.getWindow();
        //���õ�ǰ����Ϊȫ����ʾ
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
		
		/***
		 * �жϵ�¼״̬
		 */
		new Thread(new Runnable() {
			public void run() {
				//��ȡPreferenceUtils�еĵ�¼״̬
				user=PreferenceUtils.getInstance().getLoginUser();
				pwd=PreferenceUtils.getInstance().getLoginPwd();
				if (user!=null&&user!=""&&pwd!=null&&pwd!="") {
					// �������˺ź����룬����ת����¼���棬ֱ�ӵ�¼
					long start = System.currentTimeMillis();
					//��������
					Map<String, String> params = new HashMap<String, String>();  
			        params.put("pwd", pwd);
			        params.put("phone", user);
			        //��ȡ�ֻ���Ψһ��ʶ��
			        params.put("imei", PreferenceUtils.getInstance().getDeviceId());			        
					UserLogin(params);
					long costTime = System.currentTimeMillis() - start;
					//�ȴ�sleeptimeʱ��
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}else if(user!=null){
					//ֻ�������˺ţ��л��˺ţ�����ת����¼����
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(LoadActivity.this, LoginActivity.class));
					finish();
				}else{
					//���δ�ϵͳ����ʾ��¼��ע��ѡ��
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
	  * ��¼��ť����Ӧ�¼�
	  * ��ת����½����
	  * @param v
	  */
	public void login(View v) {
		Intent intent = new Intent();
		intent.setClass(LoadActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
    /***
     * ע�ᰴť����Ӧ�¼�
     * ��ת��ע�����
     * @param v
     */
	public void register(View v) {
		Intent intent = new Intent();
		intent.setClass(LoadActivity.this, RegisterActivity.class);
		startActivity(intent);
	}
		
		
		/**
		 * ִ���첽����
		 * ��¼ϵͳ
		 * ����Ϊphone,pwd,imei
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
				        	Log.e("1", "��¼�ɹ���");
				        	PreferenceUtils.getInstance().setLoginUser(user);
				        	PreferenceUtils.getInstance().setLoginPwd(pwd);
				        	Constant.isLogin=true;
				        	Constant.LoginTime=System.currentTimeMillis()/1000;
				        	//��ʼ��������Ϣ
				        	initSelfInfo();
				        }else if(status==1){
				        	Toast.makeText(context, "���û������ڣ�", Toast.LENGTH_LONG)
							.show();
				        }else  if(status==23){
				    			Toast toast=Toast.makeText(context, "��¼ʧ�ܣ����û��Ѿ��������豸��¼��", Toast.LENGTH_LONG);  
				    	        toast.show(); 
				    	        startActivity(new Intent().setClass(LoadActivity.this, LoginActivity.class));
				        } else{
				        	Toast.makeText(context, "��¼����", Toast.LENGTH_LONG)
							.show();	
				        	Constant.isLogin=false;
				        	Intent intent = new Intent();
				    		intent.setClass(LoadActivity.this, MainActivity.class);
				    		startActivity(intent);
				    		finish();
				        }
					
				    };

			}.execute(params);}
		/***
		 * �ӷ�������ȡ������Ϣ��
		 * ����Ϣд��LocalUserInfo
		 */
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
		                    		//���û���δ��д�ǳƣ���ת��������Ϣ���ƽ���
		                    		Toast.makeText(context, "����δ���Ƹ�����Ϣ�������Ƹ�����Ϣ��",
			                                Toast.LENGTH_SHORT).show();
		                    		Intent intent = new Intent();
		    			    		intent.setClass(context, FillInUserInfoActivity.class);
		    			    		startActivity(intent);
		    			    		finish();
		                    	}
	                            
		                    } else if(code==1){
		                    	Toast.makeText(context, "���û������ڣ�",
		                                Toast.LENGTH_SHORT).show();
		                    }else if (code == 2) {

		                        Toast.makeText(context, "��Ϣ��ȡʧ��ʧ��...",
		                                Toast.LENGTH_SHORT).show();
		                    } else if (code == 3) {

		                        Toast.makeText(context, "ͼƬ�ϴ�ʧ��...",
		                                Toast.LENGTH_SHORT).show();

		                    } else {

		                        Toast.makeText(context, "��������æ������...",
		                                Toast.LENGTH_SHORT).show();
		                    }

		                } catch (JSONException e) {

		                    Toast.makeText(context, "���ݽ�������...",
		                            Toast.LENGTH_SHORT).show();
		                    e.printStackTrace();
		                }

		            }

		        });

		}
		
}
