package com.eventer.app.other;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eventer.app.R;
import com.eventer.app.R.id;
import com.eventer.app.R.layout;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.ui.base.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class AssistFunctionActivity extends BaseActivity implements OnClickListener{
    private RelativeLayout re_clear_cache,re_clear_msg;
    private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assist_function);
		context=this;
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		re_clear_cache=(RelativeLayout)findViewById(R.id.re_clear_cache);
		re_clear_msg=(RelativeLayout)findViewById(R.id.re_clear_msg);
		
		re_clear_cache.setOnClickListener(this);
		re_clear_msg.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.re_clear_cache:
			
			break;
		case R.id.re_clear_msg:
			ChatEntityDao dao=new ChatEntityDao(context);
			if(dao.deleteAllMsg()){
				Toast.makeText(context, "已经清空所有数据", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(context, "没有可清空的数据！", Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
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
