package com.eventer.app.other;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.eventer.app.R;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.EventDao;
import com.eventer.app.entity.Event;
import com.eventer.app.main.BaseActivity;
import com.eventer.app.view.MyToast;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AssistFunctionActivity extends BaseActivity implements OnClickListener{

	TextView tv_clear_msg,tv_clear_event;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assist_function);
		context=this;
		setBaseTitle(R.string.assist);
		initView();
	}

	/***
	 * 初始化控件，给控件添加事件响应
	 */
	private void initView() {
		tv_clear_msg = (TextView) findViewById(R.id.tv_clear_msg);
        tv_clear_event = (TextView) findViewById(R.id.tv_clear_event);

		tv_clear_msg.setOnClickListener(this);
		tv_clear_event.setOnClickListener(this);



	}

	/**
	 * 页面控件的点击事件
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.tv_clear_event: //清空过期活动
				delEvents();
				break;

			case R.id.tv_clear_msg: //清空聊天记录
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

	/**
	 * 清空过期活动
	 */
	private void delEvents() {
		try{
			List<Event> listItems;
			List<String> list = new ArrayList<>();
			EventDao dao = new EventDao(context);
			listItems = dao.getEventList();
			for (Event event : listItems) {
				event = getEventItem(event);
				if(event != null && !TextUtils.isEmpty(event.getEventID())){
					list.add(event.getEventID());
				}
			}
			if(list.size() > 0){
				dao.delEventIDList(list);
				MyToast.makeText(context, "已经清空所有过期活动！", Toast.LENGTH_SHORT).show();
			}else {
				MyToast.makeText(context, "没有过期的活动！", Toast.LENGTH_SHORT).show();
			}

		}catch (Exception e){
			e.printStackTrace();
			MyToast.makeText(context, "操作失败！", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 判断活动是否过期
	 */
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
				e.printStackTrace();
			}
			if(now < event.getEnd()){ //未过期
				event = null;
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
