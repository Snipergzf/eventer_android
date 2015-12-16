package com.eventer.app.other;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eventer.app.R;
import com.eventer.app.ui.base.BaseActivityTest;
import com.eventer.app.util.PreferenceUtils;
import com.umeng.analytics.MobclickAgent;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

public class MsgAlertActivity extends BaseActivityTest {
	ToggleButton toggle_alert,toggle_detail,toggle_voice,toggle_shake;
	Context context;
	private LinearLayout li_alert;
	private RelativeLayout re_alert_detail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_alert);
		context=this;
		setBaseTitle(R.string.msg_alert);
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		toggle_alert=(ToggleButton)findViewById(R.id.toggle_alert);
		toggle_detail=(ToggleButton)findViewById(R.id.toggle_detail);
		toggle_shake=(ToggleButton)findViewById(R.id.toggle_shake);
		toggle_voice=(ToggleButton)findViewById(R.id.toggle_voice);
		li_alert=(LinearLayout)findViewById(R.id.alert);
		re_alert_detail=(RelativeLayout)findViewById(R.id.re_alert_detail);

		toggle_alert.toggle();

		toggle_alert.setOnToggleChanged(new OnToggleChanged(){
			@Override
			public void onToggle(boolean on) {
				if(!on){
					li_alert.setVisibility(View.GONE);
					re_alert_detail.setVisibility(View.GONE);
				}else{
					li_alert.setVisibility(View.VISIBLE);
					re_alert_detail.setVisibility(View.VISIBLE);
				}
				PreferenceUtils.getInstance().setMsgAlert(on);
			}
		});
		toggle_detail.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				// TODO Auto-generated method stub
				PreferenceUtils.getInstance().setMsgAlertDetail(on);
			}
		});
		toggle_shake.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				// TODO Auto-generated method stub
				PreferenceUtils.getInstance().setMsgAlertShake(on);
			}
		});
		toggle_voice.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				// TODO Auto-generated method stub
				PreferenceUtils.getInstance().setMsgAlertVoice(on);
			}
		});

		boolean alert=PreferenceUtils.getInstance().getMsgAlert();
		boolean alert_detail=PreferenceUtils.getInstance().getMsgAlertDetail();
		boolean alert_shake=PreferenceUtils.getInstance().getMsgAlertShake();
		boolean alert_voice=PreferenceUtils.getInstance().getMsgAlertVoice();
		if(!alert){
			toggle_alert.toggle();
		}
		if(alert_detail){
			toggle_detail.toggle();
		}
		if(alert_shake){
			toggle_shake.toggle();
		}
		if (alert_voice) {
			toggle_voice.toggle();
		}
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
