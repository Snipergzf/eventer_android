package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.easemob.util.DateUtils;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.EventDao;
import com.eventer.app.db.EventOpDao;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.entity.Event;
import com.eventer.app.entity.EventOp;
import com.eventer.app.entity.Schedual;
import com.eventer.app.http.HttpParamUnit;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.view.MyToast;
import com.eventer.app.view.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import hirondelle.date4j.DateTime;

@SuppressLint({"SimpleDateFormat","SetTextI18n"})
public class Activity_EventDetail  extends SwipeBackActivity  implements OnClickListener {

	ImageView iv_collect,iv_share,iv_comment;
	private TextView tv_theme,tv_title,tv_pubtime,tv_begin,tv_end,tv_place,tv_source;
	private TextView tv_collect_num,tv_share_num,tv_comment_num;
	private RelativeLayout info_area;
	private WebView web;
	ImageView[] ivlist;
	private Event event;
	private String id;
	private Context context;
	private EventOpDao dao;
	private Dialog mDialog;
	private boolean isCollect=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_detail);
		context=this;
		dao=new EventOpDao(context);
		initView();
	}

	/***
	 * 初始化控件，给控件添加事件响应
	 */
	private void initView() {

		iv_collect = (ImageView)findViewById(R.id.iv_collect);
		iv_share = (ImageView)findViewById(R.id.iv_share);
		iv_comment = (ImageView)findViewById(R.id.iv_comment);
		tv_pubtime = (TextView)findViewById(R.id.tv_pubtime);
		tv_theme = (TextView)findViewById(R.id.tv_tag);
		tv_begin = (TextView) findViewById(R.id.tv_begin_time);
		tv_end = (TextView) findViewById(R.id.tv_end_time);
		tv_place = (TextView) findViewById(R.id.tv_place);
		info_area = (RelativeLayout) findViewById(R.id.re_temp);
		tv_source = (TextView) findViewById(R.id.tv_source);


		tv_collect_num = (TextView)findViewById(R.id.tv_collection_num);
		tv_share_num = (TextView)findViewById(R.id.tv_share_num);
		tv_comment_num = (TextView)findViewById(R.id.tv_comment_num);
		web = (WebView) findViewById(R.id.web);
		tv_title=(TextView)findViewById(R.id.tv_title);
		iv_collect.setOnClickListener(this);
		iv_comment.setOnClickListener(this);
		iv_share.setOnClickListener(this);
		ivlist=new ImageView[]{iv_collect,iv_share,iv_comment};
		id=getIntent().getStringExtra("event_id");
		if(TextUtils.isEmpty(id)){
			MyToast.makeText(context, "活动不存在！", Toast.LENGTH_SHORT).show();
			finish();
		}
		//从数据库中读取活动
		EventDao d=new EventDao(context);
		event=d.getEvent(id);
		if(event != null
				&& event.getContent() != null
				&& !TextUtils.isEmpty(event.getContent().trim())){
			initData();
		}else{
			loadevent();
		}
	}

	/***
	 * 初始化页面的数据
	 */
	private void initData() {
			String content=event.getContent();
			String customHtml = "<html><head>\n" +
					"<style type=\"text/css\">img {width:100%;height:auto;}</style></head>" +
					"<body>"+content+"</body></html>";
			web.getSettings().setDefaultTextEncodingName("UTF-8");
			web.loadData(customHtml, "text/html; ; charset=UTF-8", null);
			long pubtime=event.getIssueTime();

			if(pubtime>0)
				tv_pubtime.setText(DateUtils.getTimestampString(new Date(pubtime*1000)));
			else {
				tv_pubtime.setText("");
			}
		    String place = event.getPlace();
		    if (!TextUtils.isEmpty(place) && !"无".equals(place)){
				tv_place.setText(place);
				String time = event.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
				try {
					JSONArray time1 = new JSONArray(time);
					long begin_time = time1.getLong(0);
					long end_time = time1.getLong(1);
					tv_begin.setText(
							sdf.format(new Date(begin_time*1000)));
					tv_end.setText(
							sdf.format(new Date(end_time*1000)));
				} catch (JSONException e) {
					info_area.setVisibility(View.GONE);
					e.printStackTrace();
				}
			} else{
				info_area.setVisibility(View.GONE);
			}

		    String source = event.getPublisher();
		    if ( !TextUtils.isEmpty(source) && !"无".equals(source)) {
				tv_source.setText("活动来自--"+source);
			} else{
				tv_source.setVisibility(View.GONE);
			}


		    //初始化各个控件的数据
			String theme = event.getTheme();
			if(theme != null&&!theme.equals(""))
				tv_theme.setText(event.getTheme());
			tv_title.setText(event.getTitle());

			//用户查看了活动，将数据存数据库，并通知服务器
			EventOp e = new EventOp();
			e.setEventID(id);
			e.setOperation(4);
			e.setOperator(Constant.UID);
			e.setOpTime(System.currentTimeMillis() / 1000);
			dao.saveEventOp(e);
			ClickFeedBack();
			//判断用户是否收藏该活动
			isCollect = dao.getIsCollect(id);
			if(isCollect){
				iv_collect.setSelected(true);
			}
	}


	public String getTime(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date(now));
	}

	/**
	 * 页面控件的点击事件
	 */
	@Override
	public void onClick(View v) {
		if("0".equals(Constant.UID)){
			MyToast.makeText(context, "请登录！", Toast.LENGTH_SHORT).show();
			return;
		}
		switch (v.getId()) {
			case R.id.iv_comment:
				Intent intent=new Intent();
				intent.setClass(context, Activity_EventComment.class);
				intent.putExtra("event_id", event.getEventID());
				startActivity(intent);
				break;
			case R.id.iv_share:
				if (mDialog == null) {
					mDialog = new Dialog(context, R.style.login_dialog);
					mDialog.setCanceledOnTouchOutside(true);
					Window win = mDialog.getWindow();
					LayoutParams params = new LayoutParams();
					params.width = LayoutParams.MATCH_PARENT;
					params.height = LayoutParams.WRAP_CONTENT;
					params.x = 0;
					params.y = 0;
					win.setAttributes(params);
					mDialog.setContentView(R.layout.dialog_share);
					mDialog.findViewById(R.id.share_by_chatroom).setOnClickListener(this);
					mDialog.findViewById(R.id.share_by_user).setOnClickListener(this);
					mDialog.findViewById(R.id.share_cancel).setOnClickListener(this);
					mDialog.findViewById(R.id.share_layout).setOnClickListener(this);
				}
				mDialog.show();
				break;
			case R.id.iv_collect:

				SchedualDao sdao=new SchedualDao(context);
				if(isCollect&&iv_collect.isSelected()){
					DelCollectFeedBack();
					sdao.delSchedualByEventId(event.getEventID());
					dao.delCancelCollect(event.getEventID());
					iv_collect.setSelected(false);
					tv_collect_num.setVisibility(View.VISIBLE);
					String str = tv_collect_num.getText().toString();
					if (TextUtils.isEmpty(str)) {
						tv_collect_num.setVisibility(View.GONE);
					} else {
						try {
							int num = Integer.parseInt(str);
							num=num-1;
							if(num>0){
								tv_collect_num.setText(num+"");
								tv_collect_num.setVisibility(View.VISIBLE);
							}else{
								tv_collect_num.setVisibility(View.GONE);
							}
						} catch (Exception e) {
							e.printStackTrace();
							tv_collect_num.setVisibility(View.GONE);
						}
					}

					Animation scale_anim_1 = AnimationUtils.loadAnimation(context, R.anim.scale);
					tv_collect_num.startAnimation(scale_anim_1);
					iv_collect.startAnimation(scale_anim_1);
					isCollect=false;
					Constant.AlarmChange=true;
					MyToast.makeText(context,
							"已取消收藏", Toast.LENGTH_SHORT ).show();
				}else{

					MobclickAgent.onEvent(context, "collect");
					CollectFeedBack();
					iv_collect.setSelected(true);
					Animation scale_anim_1 = AnimationUtils.loadAnimation(context, R.anim.scale);
					tv_collect_num.setVisibility(View.VISIBLE);
					String str = tv_collect_num.getText().toString();
					if (TextUtils.isEmpty(str)) {
						tv_collect_num.setText("1");
					} else {
						try {
							int num = Integer.parseInt(str);
							tv_collect_num.setText(String.valueOf(++num));
						} catch (Exception e) {
							e.printStackTrace();
							tv_collect_num.setText("1");
						}
					}
					tv_collect_num.startAnimation(scale_anim_1);
					iv_collect.startAnimation(scale_anim_1);
					isCollect=true;

					EventOp e=new EventOp();
					e.setEventID(event.getEventID());
					e.setOperator(Constant.UID);
					e.setOperation(1);//以表示收藏
					e.setOpTime(System.currentTimeMillis()/1000);
					dao.saveEventOp(e);

					String time=event.getTime();
					Log.e("1", time);

					try {
						JSONArray json = new JSONArray(time);
						for(int i = 0 ; i < json.length()/2 ;i++){
							Schedual s = new Schedual();
							s.setSchdeual_ID(System.currentTimeMillis()/1000);
							s.setEventId(event.getEventID());
							String starttime = getFullTime(json.getLong(2*i)*1000);
							String endtime = getFullTime(json.getLong(2*i+1)*1000);
							s.setStarttime(starttime);
							s.setEndtime(endtime);
							s.setRemindtime(starttime);
							s.setTitle(event.getTitle());
							s.setPlace(event.getPlace());
							s.setStatus(1);
							s.setType(1);
							s.setFrequency(0);
							sdao.saveSchedual(s);
							IsTodayEvent(starttime);
						}
						MyToast.makeText(context,
								"已收藏", Toast.LENGTH_SHORT ).show();
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}

				break;
			case R.id.share_by_chatroom:
				Intent intent1=new Intent();
				intent1.putExtra("event_id", event.getEventID());
				intent1.putExtra("sharetype", ShareToSingleActivity.SHARE_EVENT);
				intent1.setClass(context, ShareToGroupActivity.class);
				startActivity(intent1);
				mDialog.dismiss();
				break;
			case R.id.share_by_user:
				Intent intent2=new Intent();
				intent2.putExtra("event_id", event.getEventID());
				intent2.putExtra("sharetype", ShareToSingleActivity.SHARE_EVENT);
				intent2.setClass(context, ShareToSingleActivity.class);
				startActivity(intent2);
				mDialog.dismiss();
				break;
			case R.id.share_cancel:
			case R.id.share_layout:
				mDialog.dismiss();
				break;
			default:
				break;
		}
	}

	/**
	 * 从服务器端获得活动信息
	 */
	private void loadevent() {
		// TODO Auto-generated method stub
		Map<String,String> map=new HashMap<>();
		map.put("uid", Constant.UID);
		map.put("event_id", id);
		LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_GET_EVENT, map);
		task.getData(new DataCallBack() {

			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					int status=data.getInteger("status");
					switch (status) {
						case 0:
							JSONObject event_action=data.getJSONObject("event_action");
							JSONObject event_obj=event_action.getJSONObject("event");
							event=new Event();
							event.setContent(event_obj.getString("cEvent_content"));
							event.setEventID(id);
							event.setPlace(event_obj.getString("cEvent_place"));
							event.setTime(event_obj.getString("cEvent_time"));
							event.setTitle(event_obj.getString("cEvent_name"));
							event.setPublisher(event_obj.getString("cEvent_provider"));
							String pubtime=event_obj.getString("cEvent_publish");
							event.setIssueTime(Long.parseLong(pubtime));
							event.setTheme(event_obj.getString("cEvent_theme"));
							EventDao dao=new EventDao(context);
							dao.saveEvent(event);
							initData();
							break;
						case 24:
							MyToast.makeText(context, "活动已过期或者已删除!", Toast.LENGTH_SHORT).show();
							finish();
						default:
							if(!Constant.isConnectNet){
								MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
							}
							break;
					}

				} catch (Exception e) {
					e.printStackTrace();
					if(!Constant.isConnectNet){
						MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

	}




	/***
	 * 向服务器发送请求，活动活动的统计信息：评论数，收藏数等
	 */
	private void UpdateFeedBack(){
		Map<String,String> map = new HashMap<>();
		map.put("event_id", id);
		map.put("token", Constant.TOKEN);
		map.put("uid", Constant.UID);
		LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_UPDATE_EVENT_FEEDBACK, map);
		task.getData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					int status=data.getInteger("status");
					if(status==0){
						JSONObject action=data.getJSONObject("event_action");
						JSONObject json=action.getJSONObject("event");
						int participate_num=json.getInteger("participate_num");
						int share_num=json.getInteger("share_num");
						int comment_num=0;
						try{
							comment_num=json.getInteger("size");
						}catch (Exception e){
							e.printStackTrace();
						}

						if(participate_num>0){
							tv_collect_num.setText(participate_num+"");
							tv_collect_num.setVisibility(View.VISIBLE);
						}else{
							tv_collect_num.setVisibility(View.GONE);
						}

						if(share_num>0){
							tv_share_num.setText(share_num+"");
							tv_share_num.setVisibility(View.VISIBLE);
						}else{
							tv_share_num.setVisibility(View.GONE);
						}

						if(comment_num>0){
							tv_comment_num.setText(comment_num+"");
							tv_comment_num.setVisibility(View.VISIBLE);
						}else{
							tv_comment_num.setVisibility(View.GONE);
						}

					}else if(status == 24){
						MyToast.makeText(context,
								"活动已过期或者已删除!", Toast.LENGTH_SHORT ).show();
					}else{
						if(!Constant.isConnectNet){
							MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
						}

					}
				} catch (Exception e) {
					MyToast.makeText(context,
							"更新数据失败！", Toast.LENGTH_SHORT ).show();
				}
			}
		});
	}


	/***
	 * 向服务器发送统计数据，用户查看了该活动
	 */
	private void ClickFeedBack() {
		Map<String,String> map= HttpParamUnit.eventAddFeedback(id, "", "1", "");
		LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_SEND_EVENT_FEEDBACK, map);
		task.getData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				Log.e("event click feedback", data.toJSONString());
			}
		});

	}

	/***
	 * 向服务器发送统计数据，用户收藏该活动
	 */
	private void CollectFeedBack(){

		Map<String,String> map= HttpParamUnit.eventAddFeedback(id, "", "", "1");
		LoadDataFromHTTP task=new LoadDataFromHTTP(context,
				Constant.URL_SEND_EVENT_FEEDBACK, map);
		task.getData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					int status=data.getInteger("status");
					if(status == 24){
						MyToast.makeText(context,
								"活动已过期或者已删除!", Toast.LENGTH_SHORT ).show();
					}else if(status!=0){
						if(!Constant.isConnectNet){
							MyToast.makeText(context,
									getText(R.string.no_network), Toast.LENGTH_SHORT).show();
						}else{
							MyToast.makeText(context,
									"操作失败！", Toast.LENGTH_SHORT ).show();
						}
					}

				} catch (Exception e) {
					if(!Constant.isConnectNet){
						MyToast.makeText(context,
								getText(R.string.no_network), Toast.LENGTH_SHORT).show();
					}else{
						MyToast.makeText(context,
								"操作失败！", Toast.LENGTH_SHORT ).show();
					}
				}
			}
		});
	}


	/***
	 * 向服务器发送统计数据，用户取消收藏该活动
	 */
	private void DelCollectFeedBack(){
		Map<String,String> map=new HashMap<>();
		map.put("event_id", id);
		map.put("participate_num", "1");
		map.put("token", Constant.TOKEN);
		map.put("uid", Constant.UID);
		LoadDataFromHTTP task=new LoadDataFromHTTP(context,
				Constant.URL_DEL_EVENT_FEEDBACK, map);
		task.getData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				try {
					int status=data.getInteger("status");
					if(status == 24){
						MyToast.makeText(context,
								"活动已过期或者已删除!", Toast.LENGTH_SHORT ).show();
					}else if(status!=0){
						if(!Constant.isConnectNet){
							MyToast.makeText(context, getText(R.string.no_network),
									Toast.LENGTH_SHORT).show();
						}else{
							MyToast.makeText(context,
									"操作失败！", Toast.LENGTH_SHORT ).show();
						}
					}
				} catch (Exception e) {
					if(!Constant.isConnectNet){
						MyToast.makeText(context,
								getText(R.string.no_network), Toast.LENGTH_SHORT).show();
					}else{
						MyToast.makeText(context,
								"操作失败！", Toast.LENGTH_SHORT ).show();
					}
				}
			}
		});
	}


	public String getFullTime(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date(now));
	}



    //按返回键，退出页面
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}


	public void IsTodayEvent(String remind){
		if(Constant.AlarmChange){
			return;
		}
		DateTime Remind_dt = new DateTime(remind + ":00");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		String today_str = formatter.format(new Date());
		DateTime today_dt = new DateTime(today_str + ":00");

		if (!today_dt.gt(Remind_dt)) {
			Constant.AlarmChange=true;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		UpdateFeedBack();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}


}
