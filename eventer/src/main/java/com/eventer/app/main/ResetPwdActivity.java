package com.eventer.app.main;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.other.MyUserInfoActivity;
import com.eventer.app.ui.base.BaseActivity;
import com.eventer.app.util.PreferenceUtils;
import com.eventer.app.view.TitleBar;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class ResetPwdActivity extends BaseActivity implements OnClickListener{

	private EditText edit_pwd;
	private ImageButton btn_pwd_clear;
	private Button btn_next;
	private boolean IsPwdCheck=false;
	private Context context;
	private String TelString;
	private String pwd="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar.setTitleBar(this,"重置密码");
		setContentView(R.layout.activity_reset_pwd);
		edit_pwd=(EditText)findViewById(R.id.edit_pwd);
		btn_next=(Button)findViewById(R.id.btn_next);

		btn_pwd_clear=(ImageButton)findViewById(R.id.btn_pwd_clear);
		TelString=getIntent().getStringExtra("phone");
		if(TextUtils.isEmpty(TelString)){
			finish();
		}
		context=ResetPwdActivity.this;

		init();

	}
	/***
	 * 给控件添加事件响应
	 */
	public void init(){

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
				if(len>5&&len<20){
					IsPwdCheck=true;
				}else{
					IsPwdCheck=false;
				}
			}
		});
		//注册点击监听器
		btn_next.setOnClickListener(this);
		btn_pwd_clear.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			//确认修改密码
			case R.id.btn_next:
				if(IsPwdCheck){
					UserResetPwd();
				}else{
					Toast.makeText(context, "密码不合适，提示：6-20位",Toast.LENGTH_LONG ).show();
				}
				break;
			//清空密码输入框
			case R.id.btn_pwd_clear:
				edit_pwd.setText("");
				break;
			default:
				break;
		}
	}


	/**
	 * 注册账号
	 * 参数为“phone”和“pwd”
	 *
	 */
	public void UserResetPwd() {
		Map<String, String> params = new HashMap<String, String>();
		pwd=edit_pwd.getText().toString();
		params.put("phone", TelString);
		params.put("pwd", pwd);
		LoadDataFromHTTP task = new LoadDataFromHTTP(
				context, Constant.URL_RESET_PWD, params);
		task.getData(new DataCallBack() {

			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					int code = data.getInteger("status");
					switch (code) {
						case 0:
							PreferenceUtils.getInstance().setLoginUser(TelString);
							Toast.makeText(context, "密码重置成功！", Toast.LENGTH_LONG)
									.show();
							PreferenceUtils.getInstance().setLoginPwd("");
							if(MainActivity.instance!=null){
								finish();
								CheckPhoneActivity.instance.back();
								MyUserInfoActivity.instance.exit();
							}else{
								finish();
								CheckPhoneActivity.instance.back();
							}

							break;
						default:
							Toast.makeText(context, "操作失败，请稍后重试！！", Toast.LENGTH_LONG)
									.show();
							break;
					}

				} catch (JSONException e) {

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
	protected void onDestroy()
	{
		super.onDestroy();
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
