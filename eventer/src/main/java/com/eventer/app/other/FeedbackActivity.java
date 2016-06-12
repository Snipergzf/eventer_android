package com.eventer.app.other;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.main.MainActivity;
import com.eventer.app.view.MyToast;
import com.umeng.analytics.MobclickAgent;

public class FeedbackActivity extends Activity {
	private EditText et_title,et_content,et_contact;
	TextView tv_send;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		context=this;
		initView();
	}


	/***
	 * 初始化控件，给控件添加事件响应
	 */
	private void initView() {

		et_contact=(EditText)findViewById(R.id.et_contact);
		et_content=(EditText)findViewById(R.id.et_content);
		et_title=(EditText)findViewById(R.id.et_title);
		tv_send=(TextView)findViewById(R.id.tv_send);

		tv_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) { //发送反馈意见

				String tite = et_title.getText().toString();
				String content = et_content.getText().toString();
				String contact = et_contact.getText().toString();
				if( !TextUtils.isEmpty(content) ){
					//发布反馈
					if (Constant.isConnectNet) {
						JSONObject send_json = new JSONObject();
						try {
							send_json.put("action", "send");
							send_json.put("data",tite+"\n"+ content+"\n"+contact);
							send_json.put("type", 1);
							String send_body = send_json.toString();
							MainActivity.instance
									.newMsg("1", "1", send_body, 1 | 16);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						et_contact.setText("");
						MyToast.makeText(context, "感谢您的建议！", Toast.LENGTH_SHORT).show();
						finish();
					}else if(!Constant.isConnectNet){
						MyToast.makeText(context,getText(R.string.no_network),Toast.LENGTH_SHORT).show();
					}
				}else{
					MyToast.makeText(context, "请写上一些建议！", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}


	public void back(View v){
		finish();
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
