package com.eventer.app.other;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.main.BaseActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;
import java.util.Date;

public class AboutActivity extends BaseActivity{

    TextView tv_app_info,tv_copyright;
    private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		setBaseTitle(R.string.about);
		context=this;
		initView();
	}
	private void initView() {
		tv_app_info = (TextView) findViewById(R.id.tv_app_info);
		tv_copyright = (TextView) findViewById(R.id.tv_copyright);
		
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		String version="";
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		    version = pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		String app_name=tv_app_info.getText().toString();
		String info=app_name;
		if(!TextUtils.isEmpty(version)){
			info=app_name+" v"+version;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("©2015-");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int year = calendar.get(Calendar.YEAR);
		sb.append(year);
		sb.append(" eventer, all rights reserved");
		tv_copyright.setText(sb.toString());
		tv_app_info.setText(info);

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
