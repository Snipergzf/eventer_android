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

import com.eventer.app.R;
import com.umeng.analytics.MobclickAgent;

public class FeedbackActivity extends Activity {
	private EditText et_title,et_content,et_contact;
	private TextView tv_send;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		context=this;

		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		et_contact=(EditText)findViewById(R.id.et_contact);
		et_content=(EditText)findViewById(R.id.et_content);
		et_title=(EditText)findViewById(R.id.et_title);
		tv_send=(TextView)findViewById(R.id.tv_send);

		tv_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String tite=et_title.getText().toString();
				String content=et_content.getText().toString();
				String contact=et_contact.getText().toString();
				if(!TextUtils.isEmpty(content)){
					//发布反馈
				}else{
					Toast.makeText(context, "请写上一些意见！", Toast.LENGTH_SHORT).show();
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
