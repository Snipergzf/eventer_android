package com.eventer.app.main;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.HttpHostConnectException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import cn.smssdk.SMSSDK;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.ui.base.BaseActivity;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.util.PreferenceUtils;
import com.umeng.analytics.MobclickAgent;


public class LoginActivity extends BaseActivity implements OnClickListener {
	
	private Button btn_login;
	private ImageButton btn_user_clear,btn_pwd_clear;
	private EditText edit_user,edit_pwd;
	private TextView tv_login_help,tv_newuser;
	ProgressDialog dialog;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		context=this;
		SMSSDK.initSDK(LoginActivity.this, Constant.APPKEY, Constant.APPSECRET);
		btn_login=(Button)findViewById(R.id.btn_login);
		btn_pwd_clear=(ImageButton)findViewById(R.id.btn_pwd_clear);
		btn_user_clear=(ImageButton)findViewById(R.id.btn_user_clear);
		edit_user=(EditText)findViewById(R.id.edit_user);
		edit_pwd=(EditText)findViewById(R.id.edit_pwd);
		tv_login_help=(TextView)findViewById(R.id.tv_login_help);
		tv_newuser=(TextView)findViewById(R.id.tv_login_newuser);
		
		/***
		 * �����˺�����������
		 * ��Ϊ��ʱ����ʾ�����ť����������
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
		 * ������������������
		 * ��Ϊ��ʱ����ʾ�����ť����������
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
      String pwd=PreferenceUtils.getInstance().getLoginPwd();
      if(user!=null&&user!=""){
    	  edit_user.setText(user);
    	  edit_pwd.setFocusable(true);
    	  edit_pwd.requestFocus();
    	  if(pwd!=null&&pwd!=""){
        	  edit_pwd.setText(pwd);
          }
      }
      btn_pwd_clear.setOnClickListener(this);
      btn_user_clear.setOnClickListener(this);
      btn_login.setOnClickListener(this);
      tv_login_help.setOnClickListener(this);
      tv_newuser.setOnClickListener(this);
      dialog = new ProgressDialog(context);
	}
   /***
    * �����ؼ��ĵ���¼�
    */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_login:
			dialog.setMessage("���ڵ�¼...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
            UserLogin();	
			break;
		//������������
        case R.id.btn_pwd_clear:
			edit_pwd.setText("");
			break;
		//����˺������
		case R.id.btn_user_clear:
			edit_user.setText("");
			edit_pwd.setText("");
			break;
		//"�޷���½"��ť��
		case R.id.tv_login_help:
			Uri uri = Uri.parse("http://baidu.com");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			break;
		//"���û�"��ť����ת��ע�����
		case R.id.tv_login_newuser:
			Intent intent1=new Intent();
			intent1.setClass(context, RegisterActivity.class);
			startActivity(intent1);
			break;
		default:
			break;
		}
	}
    
	/**
	 * ִ���첽����
	 * ��¼ϵͳ
	 *  ����Ϊ��phone��,��pwd��  ,"imei"  
	 */
	public void UserLogin() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", edit_user.getText().toString());
        params.put("pwd", edit_pwd.getText().toString());	
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
							PreferenceUtils.getInstance().setLoginUser(edit_user.getText().toString());
				        	PreferenceUtils.getInstance().setLoginPwd(edit_pwd.getText().toString());
				        	Constant.isLogin=true;
				        	Constant.LoginTime=System.currentTimeMillis()/1000;
				        	JSONObject jsonLogin= data.getJSONObject("user_action");
				        	Constant.UID=jsonLogin.getInteger("uid")+"";
				        	Constant.TOKEN=jsonLogin.getString("token");
				        	initSelfInfo();
							break;
						case 1:
							dialog.dismiss();
				        	Toast.makeText(context, "�����ڸ��û�", Toast.LENGTH_LONG)
							.show();
							break;
						case 2:
							dialog.dismiss();
				        	Toast.makeText(context, "�������", Toast.LENGTH_LONG)
							.show();
						case 23:
							dialog.dismiss();
			    			Toast toast=Toast.makeText(context, "��¼ʧ�ܣ����û��Ѿ��������豸��¼��", Toast.LENGTH_LONG);
			    			//toast.setGravity(Gravity.CENTER, 0, 0);   
			    	        toast.show(); 
							break;
						case -1:
							dialog.dismiss();
							Toast.makeText(context, "�޷����ӵ������������粻�ȶ�����", Toast.LENGTH_LONG)
							.show();
							break;
						default:
							dialog.dismiss();
				        	Toast.makeText(context, "��¼ʧ�ܣ����Ժ����ԣ���", Toast.LENGTH_LONG)
							.show();
						}

	                } catch (JSONException e) {

	                    Toast.makeText(context, "���ݽ�������...",
	                            Toast.LENGTH_SHORT).show();
	                    e.printStackTrace();
	                } 
			}
		});
	}
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
	                    Log.e("1", code+"");
	                    if (code == 0) {
	                    	JSONObject json=data.getJSONObject("user_action");
	                    	JSONObject info=json.getJSONObject("info");
	                    	String name=info.getString("name");
//	                    	EventDao dao=new EventDao(context);
//				        	List<String> list=dao.getEventIDList();
//				        	MyApplication.getInstance().setCacheByKey("EventList", list);
	                    	if(name!=null&&name!=""){
	                    		LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("nick", name);
		                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("sex", info.getString("sex"));
		                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("email", info.getString("email"));
		                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("user_rank", "0");
		                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("grade", info.getString("grade"));
		                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("school", info.getString("school"));
		                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("class", info.getString("class"));
		                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("major", info.getString("major"));
		                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("avatar", info.getString("avatar"));
		                    	dialog.dismiss();
		                    	Intent intent = new Intent();
	    			    		intent.setClass(context, MainActivity.class);
	    			    		startActivity(intent);
	    			    		finish();
	                    	}else{
	                    		Toast.makeText(context, "����δ���Ƹ�����Ϣ�������Ƹ�����Ϣ��",
		                                Toast.LENGTH_SHORT).show();
	                    		Intent intent = new Intent();
	    			    		intent.setClass(context, FillInUserInfoActivity.class);
	    			    		startActivity(intent);
	    			    		finish();
	                    	}
                            
	                    } else if (code == 2) {

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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("1", "login--destory");
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
