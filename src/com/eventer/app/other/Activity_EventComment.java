package com.eventer.app.other;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.easemob.util.DateUtils;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.adapter.CommentAdapter;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.CommentDao;
import com.eventer.app.db.EventDao;
import com.eventer.app.entity.Comment;
import com.eventer.app.entity.Event;
import com.eventer.app.entity.EventOp;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.util.FileUtil;
import com.umeng.analytics.MobclickAgent;

public class Activity_EventComment extends Activity {
	private String eid;
	private Event event;
	private Context context;
	private Button btn_comment_send;
	private EditText et_comment;
	private TextView tv_title,tv_time,tv_place;
	private ListView listview;
	private CommentAdapter adapter;
	private ImageView iv_event_cover;
	private RelativeLayout event_datail;
	private List<Comment> mData=new ArrayList<Comment>();
	private String image="";
	 private FileUtil fileUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_comment);
		context=this;
		fileUtil = new FileUtil(context, Constant.IMAGE_PATH);
		eid=getIntent().getStringExtra("event_id");
		if(TextUtils.isEmpty(eid)){
			Toast.makeText(context, "活动不存在！", Toast.LENGTH_SHORT).show();
			finish();
		}	
		initView();
		initCommentData();
		//从数据库获取活动
		EventDao d=new EventDao(context);
		event=d.getEvent(eid);
		if(event!=null){
			initData();
		}else{
			loadevent();
		}	
		initData();
	}
	



	private void initView() {
		btn_comment_send=(Button)findViewById(R.id.comment_send);
		et_comment=(EditText)findViewById(R.id.comment_et);
		event_datail=(RelativeLayout)findViewById(R.id.event_detail);
		listview=(ListView)findViewById(R.id.listview);
		tv_place=(TextView)findViewById(R.id.tv_place);
		tv_time=(TextView)findViewById(R.id.tv_time);
		tv_title=(TextView)findViewById(R.id.tv_title);
		iv_event_cover=(ImageView)findViewById(R.id.iv_event_cover);
		
		adapter=new CommentAdapter(context,mData);
		listview.setAdapter(adapter);
			
		btn_comment_send.setOnClickListener(new MyListener());
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Comment comment=mData.get(position);
				if(comment.getSpeaker().equals(Constant.UID))
				   showMyDialog("评论",comment,position);
				return false;
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Comment comment=mData.get(position);
				if(comment.getSpeaker().equals(Constant.UID))
				     showMyDialog("评论",comment,position);
				
			}
		});
		
		event_datail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent().setClass(context, Activity_EventDetail.class)
						.putExtra("event_id", eid));
			}
		});
		
	}
	
	/**
	 * 
	 */
	private void initData() {
		// TODO Auto-generated method stub		
		if(event!=null&&!TextUtils.isEmpty(event.getContent().trim())){
			String content=event.getContent();
		    Spanned sp = Html.fromHtml(content, new Html.ImageGetter() {
				@Override
				public Drawable getDrawable(String source) {
					    if(TextUtils.isEmpty(image))
						   image=source;
						return null;
				}
					
			}, null);
		    String place=event.getPlace();
		    String time=event.getTime();
		    JSONArray time1;
		    String timeString="";
			try {
				time1 = new JSONArray(time);
				
				for(int i=0;i<time1.length()/2;i++){
				long begin_time=time1.getLong(2*i);
				long end_time=time1.getLong(2*i+1);
				timeString+=getTimeSpan(begin_time,end_time);
			    timeString+="  ";
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//时间成对，可能有多个时间

		    tv_time.setText("活动时间:"+timeString);
		    String theme=event.getTheme();
		    String title=event.getTitle();
		    if(theme!=null&&!theme.equals(""))
		         title="【"+theme+"】"+title;
		    tv_title.setText(title);
		    if(!TextUtils.isEmpty(place)){
		    	tv_place.setText("活动地点:"+place);
		    }		    
		}else{
			Toast.makeText(context, "活动为空！", Toast.LENGTH_SHORT).show();
			finish();
		}
		setEventImage();
	}
	
	/**
	 * 执行异步任务
	 * 
	 * @param params
	 *      
	 */
	public void setEventImage(String... params) {
		new AsyncTask<String, Object,Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				
				InputStream is = null;	
				 String filename = image
			                .substring(image.lastIndexOf("/") + 1);
			    String filepath = fileUtil.getAbsolutePath() + filename;
				try {
					Bitmap bitmap = BitmapFactory.decodeFile(filepath);
					if(bitmap!=null){
//						d = new BitmapDrawable(context.getResources(),bitmap);
					}else{
						is = (InputStream) new URL(image).getContent();							
                        bitmap = BitmapFactory.decodeStream(is,
                               null, null);
						fileUtil.saveBitmap(filename, bitmap);
						is.close();
					}					
					return bitmap;					
				} catch (Exception e) {
					return null;
				}
			}
			protected void onPostExecute(Bitmap bitmap) {
	               if(bitmap!=null){
//	            	   Drawable d = new BitmapDrawable(context.getResources(),bitmap);
//	            	   event_datail.setBackground(d);
	            	   iv_event_cover.setImageBitmap(bitmap);
	               }
			};
		}.execute(params);}

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
	
	public String getTime(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = sdf.format(new Date(now));
		return date;
	}

	  /**
	   * 初始化评论
	   */
	private void initCommentData() {
		// TODO Auto-generated method stub
		getComment(0);
//		CommentDao dao=new CommentDao(context);
//		mData=dao.getCommentList(eid);
//		adapter.setData(mData);
//		adapter.notifyDataSetChanged();
	}
   /**
    * 通过服务器获得活动
    */
	private void loadevent() {
		// TODO Auto-generated method stub
		Map<String,String> map=new HashMap<String, String>();
		map.put("uid", Constant.UID);
		map.put("event_id", eid);
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
						event.setEventID(eid);
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
	
	protected void showMyDialog(String title, final Comment comment, final int position) {
		 final AlertDialog dlg = new AlertDialog.Builder(context).create();
	        dlg.show();
	        Window window = dlg.getWindow();
	        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
	        window.setContentView(R.layout.alertdialog);
	        window.findViewById(R.id.ll_title).setVisibility(View.VISIBLE);
	        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
	        tv_title.setText(title);
	        TextView tv_content1 = (TextView) window.findViewById(R.id.tv_content1);
	        tv_content1.setVisibility(View.GONE);
	        TextView tv_content2 = (TextView) window.findViewById(R.id.tv_content2);
	        tv_content2.setText("删除该评论");
	        tv_content2.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	delComment(comment.getCommentID(),position);
	                dlg.cancel();
	            }
	        });
	}



	class MyListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.comment_send:
				String comment=et_comment.getText().toString();
				if(!comment.equals("")){
					addComment(comment);
				}else{
					Toast.makeText(context, "评论内容不能为空！", Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
		}
		
	}
	/**
   * 添加评论
   */
	private void addComment(String comment){
		 Map<String, String> maps = new HashMap<String, String>();		 
	     maps.put("uid", Constant.UID);
	     maps.put("token", Constant.TOKEN);
	     maps.put("event_id", eid);
	     maps.put("content", comment);
	     LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_ADD_COMMENT, maps);
	     task.getData(new DataCallBack() {		
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				Log.e("1",data.toJSONString());
				int status=data.getInteger("status");
				switch (status) {
				case 0:
					JSONObject obj=data.getJSONObject("comment");
					JSONObject json=obj.getJSONObject("comments");
					JSONObject c_json=json.getJSONObject("0");
					if(c_json!=null){
						Comment c=new Comment();
						c.setEventID(eid);
						c.setTime(c_json.getLong("comment_time"));
						c.setCommentID(c_json.getString("comment_id"));
						c.setContent(c_json.getString("content"));
						c.setSpeaker(c_json.getString("speaker_id"));
						CommentDao dao=new CommentDao(context);
						dao.saveComment(c);
						mData.add(0,c);
						adapter.setData(mData);
						adapter.notifyDataSetChanged();
						et_comment.setText("");
					}
					Toast.makeText(context, "评论已发送！", Toast.LENGTH_SHORT).show();
					break;
				case 10:
				default:
					Toast.makeText(context, "非常抱歉，评论发表失败！", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
		
	}
	/***
	 * 删除评论
	 * @param comment_id  待删除评论的ID
	 * @param position  待删除评论在listView中的位置
	 */
	  
	private void delComment(final String comment_id,final int position){
		 Map<String, String> maps = new HashMap<String, String>();		 
	     maps.put("uid", Constant.UID);
	     maps.put("token", Constant.TOKEN);
	     maps.put("event_id", eid);
	     maps.put("comment_id", comment_id);
	     LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_DELETE_COMMENT, maps);
	     task.getData(new DataCallBack() {		
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				int status=data.getInteger("status");
				switch (status) {
					case 0:
						CommentDao dao=new CommentDao(context);
						dao.deleteComment(comment_id);
						mData.remove(position);
		                adapter.notifyDataSetChanged();
						Toast.makeText(context, "评论已删除！", Toast.LENGTH_SHORT).show();
						break;
					case 11:
					default:
						Toast.makeText(context, "操作失败，请稍后重试！", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		});
		
	}
	
	
	private void getComment(int pos){
		 Map<String, String> maps = new HashMap<String, String>();		 
	     maps.put("pos", pos+"");
	     maps.put("count", "20");
	     maps.put("event_id", eid);
	     LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_GET_COMMENT, maps);
	     task.getData(new DataCallBack() {		
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				int status=data.getInteger("status");
				switch (status) {
				case 0:
					JSONObject obj=data.getJSONObject("comment");
					JSONObject json=obj.getJSONObject("comments");
					JSONObject temp_json=json.getJSONObject("cEvent_comment");
					if(temp_json!=null){
						int size=temp_json.size();					
						for(int i=0;i<size;i++){
							JSONObject c_json=temp_json.getJSONObject((size-1-i)+"");
							Comment c=new Comment();
							c.setEventID(eid);
							c.setTime(c_json.getLong("comment_time"));
							c.setCommentID(c_json.getString("comment_id"));
							c.setContent(c_json.getString("content"));
							c.setSpeaker(c_json.getString("speaker_id"));
//							CommentDao dao=new CommentDao(context);
//							dao.saveComment(c);
							mData.add(c);					
						}
						adapter.setData(mData);
						adapter.notifyDataSetChanged();		
					}
					break;
				default:
					Toast.makeText(context, "评论加载失败！", Toast.LENGTH_SHORT).show();
					break;
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
