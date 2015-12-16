package com.eventer.app.other;

import android.os.Bundle;

import com.eventer.app.R;
import com.eventer.app.ui.base.BaseActivityTest;
import com.umeng.analytics.MobclickAgent;

public class OurTeamActivity extends BaseActivityTest {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_our_team);
		setBaseTitle(R.string.our_team);
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
