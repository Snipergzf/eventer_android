package com.eventer.app.other;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.widget.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;
import java.util.Date;

public class AboutActivity extends SwipeBackActivity implements OnClickListener{
    RelativeLayout re_tell_friend,re_our_team;
    TextView tv_app_info,tv_copyright;
    private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
//		TextView title=(TextView)findViewById(R.id.tv_title);
//		title.setText(R.string.about);
		setBaseTitle(R.string.about);

		context=this;
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		re_our_team = (RelativeLayout) findViewById(R.id.re_our_team);
		re_tell_friend = (RelativeLayout) findViewById(R.id.re_tell_friend);
		tv_app_info = (TextView) findViewById(R.id.tv_app_info);
		tv_copyright = (TextView) findViewById(R.id.tv_copyright);
		
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		String version="";
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		    version = pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		String appname=tv_app_info.getText().toString();
		String info=appname;
		if(!TextUtils.isEmpty(version)){
			info=appname+" v"+version;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("©2015-");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int year = calendar.get(Calendar.YEAR);
		sb.append(year);
		sb.append(" eventer, all rights reserved");
		tv_copyright.setText(sb.toString());

		tv_app_info.setText(info);
		re_our_team.setOnClickListener(this);
		re_tell_friend.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.re_our_team:
			startActivity(new Intent().setClass(context, OurTeamActivity.class));
			break;
        case R.id.re_tell_friend:
        	String msg=getResources().getString(R.string.tell_friend_msg);
        	String url="";
        	msg+=url;
        	doSendSMSTo(msg);
			break;
		default:
			break;
		}
	}

    public void doSendSMSTo(String message){  
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));            
            intent.putExtra("sms_body", message);            
            startActivity(intent);  
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
