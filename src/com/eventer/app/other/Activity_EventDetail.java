package com.eventer.app.other;

import hirondelle.date4j.DateTime;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import com.eventer.app.http.HTTPService;
import com.eventer.app.task.LoadDataFromHTTP;
import com.eventer.app.task.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.util.FileUtil;
import com.eventer.app.util.PreferenceUtils;
import com.eventer.app.widget.swipeback.SwipeBackActivity;

public class Activity_EventDetail  extends SwipeBackActivity  implements OnClickListener {
	
	private ImageView iv_collect,iv_share,iv_comment;
	private TextView tv_theme,tv_title,tv_time,tv_source,tv_pubtime,tv_place;
	private TextView tv;
	private ImageView[] ivlist;
    private Event event;
    private String id;
    private Context context;
    private EventOpDao dao;
    private Dialog mDialog;
    private int op=0;
    private int index=0;
    private FileUtil fileUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_detail);
		context=this;	
		dao=new EventOpDao(context);
		fileUtil = new FileUtil(context, Constant.IMAGE_PATH);
		initView();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		iv_collect=(ImageView)findViewById(R.id.iv_collect);
		iv_share=(ImageView)findViewById(R.id.iv_share);
		iv_comment=(ImageView)findViewById(R.id.iv_comment);
		tv_pubtime=(TextView)findViewById(R.id.tv_pubtime);
		tv_source=(TextView)findViewById(R.id.tv_source);
		tv_theme=(TextView)findViewById(R.id.tv_tag);
		tv_time=(TextView)findViewById(R.id.tv_time);
		tv_place=(TextView)findViewById(R.id.tv_place);
		tv=(TextView)findViewById(R.id.tv);
		tv_title=(TextView)findViewById(R.id.tv_title);
		iv_collect.setOnClickListener(this);
		iv_comment.setOnClickListener(this);
		iv_share.setOnClickListener(this);
		ivlist=new ImageView[]{iv_collect,iv_share,iv_comment};
//		event=(Event) MyApplication.getInstance().getValueByKey("event_detail");
		id=getIntent().getStringExtra("event_id");
		if(TextUtils.isEmpty(id)){
			Toast.makeText(context, "活动不存在！", Toast.LENGTH_SHORT).show();
			finish();
		}		
		EventDao d=new EventDao(context);
		event=d.getEvent(id+"");
		if(event!=null){
			initData();
		}else{
			loadevent();
		}		
	}

	private void initData() {
		// TODO Auto-generated method stub
		
		if(event!=null&&!TextUtils.isEmpty(event.getContent().trim())){
			String content=event.getContent();
			if(!TextUtils.isEmpty(content.trim()))
			     content=ToDBC(content);
		    Spanned sp = Html.fromHtml(content, new Html.ImageGetter() {
				@Override
				public Drawable getDrawable(String source) {
					
						DisplayMetrics dm = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dm);
						Drawable d = context.getResources().getDrawable(R.color.caldroid_holo_blue_light_1);
						d.setBounds(0, 0, dm.widthPixels,
								320);
						return d;
						}
					
			}, null);
		    long pubtime=event.getIssueTime();
		    String place=event.getPlace();
		    
//		    int len=(new Long(pubtime)).toString().length();
		    if(pubtime>0)
		       tv_pubtime.setText(DateUtils.getTimestampString(new Date(pubtime*1000)));
		    else {
		    	tv_pubtime.setText("");
			}
		    String time=event.getTime();
		    JSONArray time1;
		    String timeString="";
			try {
				time1 = new JSONArray(time);
				
				for(int i=0;i<time1.length()/2;i++){
				long begin_time=time1.getLong(2*i);
				long end_time=time1.getLong(2*i+1);
				timeString+=getTimeSpan(begin_time,end_time);
				if(i!=time1.length()/2-1){
					timeString+="\n";
				}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//时间成对，可能有多个时间

		    tv_time.setText(timeString);
		    String theme=event.getTheme();
		    if(theme!=null&&!theme.equals(""))
		       tv_theme.setText(event.getTheme());
		    tv_source.setText(event.getPublisher());
		    tv_title.setText(event.getTitle());
		    if(!TextUtils.isEmpty(place)){
		    	tv_place.setText(place);
		    }		    
		    tv.setText(sp);
//		    tv.setAutoLinkMask(Linkify.ALL); 
		    tv.setMovementMethod(LinkMovementMethod.getInstance()); 
		    DownPage(content);
			
			op=dao.getMyOp(event.getEventID());
			EventOp e=new EventOp();
			e.setEventID(id);
			e.setOperation(4);
			e.setOperator(Constant.UID);
			e.setOpTime(System.currentTimeMillis()/1000);
			dao.saveEventOp(e);
			switch (op) {
			case 1:
				iv_collect.setSelected(true);
				break;

			default:
				break;
			}
		}else{
			Toast.makeText(context, "活动为空！", Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	private String getTimeSpan(long begin_time, long end_time) {
		// TODO Auto-generated method stub
		String begin=getTime(begin_time*1000);
		String end=getTime(end_time*1000);
		String beginString=begin.substring(0, 10);
		String endString=end.substring(0, 10);
		String time="";
		if(beginString.equals(endString)){
			time=begin+" ~"+end.substring(10);
		}else{
			time=begin+" ~ "+end;
		}
		return time;
	}

	private void loadevent() {
		// TODO Auto-generated method stub
		Map<String,String> map=new HashMap<String, String>();
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
						Toast.makeText(context, "活动已过期!", Toast.LENGTH_SHORT).show();
						finish();
					default:
						break;
					}
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		
	}

	private String ToDBC(String input) {  
		   char[] c = input.toCharArray();  
		   for (int i = 0; i< c.length; i++) {  
		       if (c[i] == 12288) {  
		         c[i] = (char) 32;  
		         continue;  
		       }if (c[i]> 65280&& c[i]< 65375)  
		          c[i] = (char) (c[i] - 65248);  
		       }  
		   return new String(c);  
		}
	/**
	 * 执行异步任务
	 * 
	 * @param params
	 *      
	 */
	public void DownPage(String... params) {
		new AsyncTask<String, Object,Spanned>() {

			@Override
			protected Spanned doInBackground(String... params) {
				Spanned sp = Html.fromHtml(params[0], new Html.ImageGetter() {
					@Override
					public Drawable getDrawable(String source) {
						InputStream is = null;	
						 String filename = source
					                .substring(source.lastIndexOf("/") + 1);
					    String filepath = fileUtil.getAbsolutePath() + filename;
						try {
							Drawable d;					
							Log.e("1", "url:"+source);
//							BitmapCache bitmapCache = new BitmapCache();
//							Bitmap bitmap = bitmapCache.getBitmap(source);
							Bitmap bitmap = BitmapFactory.decodeFile(filepath);
							if(bitmap!=null){
								d = new BitmapDrawable(context.getResources(),bitmap);
								Log.e("1","have "+source);
							}else{
								is = (InputStream) new URL(source).getContent();							
								BitmapFactory.Options options = new BitmapFactory.Options();
		                        options.inJustDecodeBounds = false;
		                        options.inSampleSize = 5; // width，hight设为原来的十分一
		                        
		                        bitmap = BitmapFactory.decodeStream(is,
		                                null, options);
		                        
//								d = Drawable.createFromStream(is, "src");
//								BitmapDrawable bd = (BitmapDrawable) d;
//								bitmap=bd.getBitmap();
//								bitmapCache.putBitmap(source, bitmap);
								fileUtil.saveBitmap(filename, bitmap);
//								if(bitmapCache.getBitmap(source)!=null){
//									Log.e("1","rrrr "+source);
//								}
								is.close();
								d = new BitmapDrawable(context.getResources(),bitmap);
							}
							
							DisplayMetrics dm = new DisplayMetrics();
							getWindowManager().getDefaultDisplay().getMetrics(dm);
							int width=dm.widthPixels-20;
							int heigt=(width*d.getIntrinsicHeight())/d.getIntrinsicWidth();
							d.setBounds(10, 0, dm.widthPixels-10,
									heigt);
							
							return d;
							
						} catch (Exception e) {
							Log.e("1", e.toString());
							DisplayMetrics dm = new DisplayMetrics();
							getWindowManager().getDefaultDisplay().getMetrics(dm);
							Drawable d = context.getResources().getDrawable(R.color.caldroid_holo_blue_light_1);
							d.setBounds(0, 0, dm.widthPixels,
									320);
							return d;
						}
					}
				}, null);
				return sp;
			}
			protected void onPostExecute(Spanned sp) {
				tv.setText(sp);	
			};
		}.execute(params);}

	public String getTime(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = sdf.format(new Date(now));
		return date;
	}
	
	public String getFullTime(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = sdf.format(new Date(now));
		return date;
	}
	
	private int getStatus( String end, String remindtime) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowtime = sdf.format(new Date());
		int status=0;
		DateTime now=new DateTime(nowtime+":00");
		DateTime finish=new DateTime(end+":00");
		DateTime remind=new DateTime(remindtime+":00");
		if(now.gteq(remind)&&now.lteq(finish)){
			status=1;
		}else if(now.lt(remind)){
			status=0;
		}else if(now.gt(finish)){
			status=2;
		}
		return status;
	}
	
	public void back(View v){
		finish();
	}
	
     public void onBackPressed() {
	    	super.onBackPressed();
            finish();     
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.iv_comment:
				Intent intent=new Intent();
				intent.setClass(context, EventCommentActivity.class);
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
				if(op==1&&iv_collect.isSelected()){
						sdao.delSchedualByEventId(event.getEventID());
						dao.delByEventId(event.getEventID());
						iv_collect.setSelected(false);
						Animation scale_anim_1 = AnimationUtils.loadAnimation(context, R.anim.scale);
						iv_collect.startAnimation(scale_anim_1);
						op=0;
						Toast.makeText(context,
		                        "已取消收藏", Toast.LENGTH_SHORT ).show();
				}else{
//					if(op>0){
//						ivlist[op-1].setSelected(false);
//					}
					iv_collect.setSelected(true);
					Animation scale_anim_1 = AnimationUtils.loadAnimation(context, R.anim.scale);
					iv_collect.startAnimation(scale_anim_1);
					op=1;
//					ivlist[op-1].setSelected(true);
					EventOp e=new EventOp();
					e.setEventID(event.getEventID());
					e.setOperator(Constant.UID);
					e.setOperation(1);//以表示收藏
					e.setOpTime(System.currentTimeMillis()/1000);
					dao.saveEventOp(e);	
					
					String time=event.getTime();
					Log.e("1", time);
					
					try {
						JSONArray json=new JSONArray(time);	
						for(int i=0;i<json.length()/2;i++){
							Schedual s=new Schedual();
							s.setSchdeual_ID(System.currentTimeMillis()/1000);
							s.setEventId(event.getEventID());
							String starttime=getFullTime(json.getLong(2*i)*1000);
							String endtime=getFullTime(json.getLong(2*i+1)*1000);
							s.setStarttime(starttime);
							s.setEndtime(endtime);
							s.setRemindtime(starttime);
							s.setTitle(event.getTitle());	
							s.setPlace(event.getPlace());
							int status=getStatus(endtime,starttime);
							
							s.setStatus(status);
							if(status!=0){
								s.setStatus(1);
							}
							s.setFrequency(0);
							sdao.saveSchedual(s);
						}
						Toast.makeText(context,
		                        "已收藏", Toast.LENGTH_SHORT ).show();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
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


}
