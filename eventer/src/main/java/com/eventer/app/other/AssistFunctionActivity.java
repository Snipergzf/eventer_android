package com.eventer.app.other;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.eventer.app.R;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.EventDao;
import com.eventer.app.entity.Event;
import com.eventer.app.main.ActivityFragment;
import com.eventer.app.util.PreferenceUtils;
import com.eventer.app.widget.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;
import com.zcw.togglebutton.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AssistFunctionActivity extends SwipeBackActivity implements OnClickListener{

	TextView tv_clear_msg,tv_clear_event;
	ToggleButton toggle_event;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assist_function);
		context=this;
		setBaseTitle(R.string.assist);
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		tv_clear_msg = (TextView) findViewById(R.id.tv_clear_msg);
        tv_clear_event = (TextView) findViewById(R.id.tv_clear_event);
		toggle_event = (ToggleButton) findViewById(R.id.toggle_event);

		tv_clear_msg.setOnClickListener(this);
		tv_clear_event.setOnClickListener(this);
		toggle_event.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				// TODO Auto-generated method stub
				PreferenceUtils.getInstance().setDisplayUserlessEvent(on);
				if (ActivityFragment.instance != null) {
					ActivityFragment.instance.Refresh();
				}

			}
		});
		boolean alert=PreferenceUtils.getInstance().getDisplayUserlessEvent();
		if(alert){
			toggle_event.toggle();
		}

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.tv_clear_event:
				delEvents();
				boolean alert=PreferenceUtils.getInstance().getDisplayUserlessEvent();
				if (ActivityFragment.instance != null&&alert) {
					ActivityFragment.instance.Refresh();
				}
				break;

			case R.id.tv_clear_msg:
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
	private void delEvents() {
		try{
			List<Event> listItems;
			List<String> list = new ArrayList<>();
			EventDao dao=new EventDao(context);
			listItems=dao.getEventList();
			for (Event event : listItems) {
				event=getEventItem(event);
				if(event==null){
					list.add(event.getEventID());
				}
			}
			dao.delEventIDList(list);
		}catch (Exception e){
			e.printStackTrace();
			Toast.makeText(context,"操作失败！",Toast.LENGTH_SHORT).show();
		}
	}
	private Event getEventItem(Event event) {
			String time=event.getTime();
			JSONArray time1;
			long now=System.currentTimeMillis()/1000;
			try {
				time1 = new JSONArray(time);
				for(int i=0;i<time1.length()/2;i++){
					long end=time1.getLong(2*i+1);
					if(i==0){
						event.setEnd(end);
					}

					if(end>event.getEnd()){
						event.setEnd(end);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(now>event.getEnd()){
				event=null;
			}
		    return event;
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
