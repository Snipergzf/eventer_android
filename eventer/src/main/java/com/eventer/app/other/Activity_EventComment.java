package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.adapter.CommentAdapter;
import com.eventer.app.db.CommentDao;
import com.eventer.app.db.EventDao;
import com.eventer.app.entity.Comment;
import com.eventer.app.entity.Event;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.util.FileUtil;
import com.eventer.app.view.MyToast;
import com.eventer.app.view.refreshlist.IXListViewLoadMore;
import com.eventer.app.view.refreshlist.IXListViewRefreshListener;
import com.eventer.app.view.refreshlist.XListView;
import com.eventer.app.view.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
@SuppressLint("SetTextI18n")
public class Activity_EventComment extends SwipeBackActivity {
	private String eid;
	private Event event;
	private Context context;
	Button btn_comment_send;
	private EditText et_comment;
	private TextView tv_title,tv_time,tv_place;
	private XListView listview;
	private CommentAdapter adapter;
	private ImageView iv_event_cover;
	RelativeLayout event_datail;
	private List<Comment> mData = new ArrayList<>();
	private List<String> id_list = new ArrayList<>();
	private String image = "";
	private FileUtil fileUtil;
	private final int LODING_MORE = 0;
	private final int REFRESH_MORE = 1;
	private int comment_sum=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_comment);
		context=this;
		setBaseTitle(R.string.comment);
		fileUtil = new FileUtil(context, Constant.IMAGE_PATH);
		eid=getIntent().getStringExtra("event_id");
		if(TextUtils.isEmpty(eid)){
			MyToast.makeText(context, "活动不存在！", Toast.LENGTH_SHORT).show();
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
	}

	/***
	 * 初始化控件，给控件添加事件响应
	 */
	private void initView() {
		btn_comment_send=(Button)findViewById(R.id.comment_send);
		et_comment=(EditText)findViewById(R.id.comment_et);
		event_datail=(RelativeLayout)findViewById(R.id.event_detail);
		listview=(XListView)findViewById(R.id.listview);
		tv_place=(TextView)findViewById(R.id.tv_place);
		tv_time=(TextView)findViewById(R.id.tv_time);
		tv_title=(TextView)findViewById(R.id.tv_title);
//		tv_empty=(TextView)findViewById(R.id.tv_empty);
		iv_event_cover=(ImageView)findViewById(R.id.iv_event_cover);

		btn_comment_send.setOnClickListener(new MyListener());
		listview.setEmptyView(findViewById(R.id.tv_empty));
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
										   int position, long id) {
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
				Comment comment=mData.get(position-1);
				if(comment.getSpeaker().equals(Constant.UID))
					showMyDialog("评论",comment,position-1);

			}
		});

		listview.setPullRefreshEnable(new IXListViewRefreshListener() {

			@Override
			public void onRefresh() {
				mHandler.sendEmptyMessageDelayed(REFRESH_MORE, 800);
			}
		});
		listview.setPullLoadEnable(new IXListViewLoadMore() {

			@Override
			public void onLoadMore() {
				mHandler.sendEmptyMessageDelayed(LODING_MORE, 800);
			}
		});

		adapter=new CommentAdapter(context,mData);
		listview.setAdapter(adapter);
		event_datail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent().setClass(context, Activity_EventDetail.class)
						.putExtra("event_id", eid));
			}
		});

	}

	/***
	 * 初始化页面的数据
	 */
	private void initData() {
		if(event!=null&&!TextUtils.isEmpty(event.getContent().trim())){
			String content=event.getContent();
			Html.fromHtml(content, new Html.ImageGetter() {
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

				for(int i = 0; i < time1.length()/2;i++){
					long begin_time = time1.getLong(2*i);
					long end_time = time1.getLong(2*i+1);
					timeString += getTimeSpan(begin_time,end_time);
					timeString += "  ";
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
			MyToast.makeText(context, "活动为空！", Toast.LENGTH_SHORT).show();
			finish();
		}
		setEventImage();
	}

	/**
	 * 页面控件的点击事件
	 */
	class MyListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.comment_send:
					String comment=et_comment.getText().toString();
					if(!comment.equals("")){
						addComment(comment);
						et_comment.setText("");
					}else{
						MyToast.makeText(context, "评论内容不能为空！", Toast.LENGTH_SHORT).show();
					}
					break;

				default:
					break;
			}
		}

	}


	private Handler mHandler = new Handler(new Handler.Callback(){
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case REFRESH_MORE:
					mData=new ArrayList<>();
					id_list=new ArrayList<>();
					getComment(0);
					break;
				case LODING_MORE:
					getComment(mData.size());
					break;
				default:
					break;
			}
			return false;
		}
	});

	/**
	 * 初始化评论
	 */
	private void initCommentData() {
		getComment(0);
	}

	/**
	 * 弹出删除评论的窗口
	 */
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
				delComment(comment.getCommentID(), position);
				dlg.cancel();
			}
		});
	}

	/**
	 * 通过服务器获得活动
	 */
	private void loadevent() {
		Map<String,String> map=new HashMap<>();
		map.put("uid", Constant.UID);
		map.put("event_id", eid);
		LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_GET_EVENT, map);
		task.getData(new DataCallBack() {

			@Override
			public void onDataCallBack(JSONObject data) {
				try {
					int status = data.getInteger("status");
					switch (status) {
						case 0:
							JSONObject event_action = data.getJSONObject("event_action");
							JSONObject event_obj = event_action.getJSONObject("event");
							event = new Event();
							event.setContent(event_obj.getString("cEvent_content"));
							event.setEventID(eid);
							event.setPlace(event_obj.getString("cEvent_place"));
							event.setTime(event_obj.getString("cEvent_time"));
							event.setTitle(event_obj.getString("cEvent_name"));
							event.setPublisher(event_obj.getString("cEvent_provider"));
							String pubtime = event_obj.getString("cEvent_publish");
							event.setIssueTime(Long.parseLong(pubtime));
							event.setTheme(event_obj.getString("cEvent_theme"));
							EventDao dao = new EventDao(context);
							dao.saveEvent(event);
							initData();
							break;
						case 24:
							MyToast.makeText(context, "活动已过期!", Toast.LENGTH_SHORT).show();
							finish();
						default:
							if (!Constant.isConnectNet) {
								MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
							}
							break;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * 执行异步任务
	 * 下载活动的图片
	 */
	public void setEventImage(String... params) {
		new AsyncTask<String, Object,Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {

				InputStream is;
				String filename = image
						.substring(image.lastIndexOf("/") + 1)+"_e";
				String filepath = fileUtil.getAbsolutePath() + filename;
				try {
					Bitmap bitmap = BitmapFactory.decodeFile(filepath);
					if(bitmap==null){
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
					iv_event_cover.setImageBitmap(bitmap);
				}
			}
		}.execute(params);}


	/***
	 * 获取评论
	 *
	 */
	private void getComment(final int pos){
		Map<String, String> maps = new HashMap<>();
		maps.put("pos", pos+"");
		maps.put("count", "20");
		maps.put("event_id", eid);
		maps.put("sum_previous", comment_sum + "");
		LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_GET_COMMENT, maps);
		task.getData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {

				try {
					int status = data.getInteger("status");
					switch (status) {
						case 0:
							JSONObject obj = data.getJSONObject("comment");
							try {
								comment_sum = obj.getInteger("sum");
							} catch (Exception e) {
								comment_sum = 0;
							}

							JSONObject json = obj.getJSONObject("comments");
							JSONObject temp_json = json.getJSONObject("cEvent_comment");
							if (temp_json != null) {
								int size = temp_json.size();
								for (int i = 0; i < size; i++) {
									JSONObject c_json = temp_json.getJSONObject((size - 1 - i) + "");
									Comment c = new Comment();
									String id = c_json.getString("comment_id");
									c.setEventID(eid);
									c.setTime(c_json.getLong("comment_time"));
									c.setCommentID(id);
									c.setContent(c_json.getString("content"));
									c.setSpeaker(c_json.getString("speaker_id"));
									if (!id_list.contains(id)) {
										mData.add(c);
										id_list.add(id);
									}
								}
								adapter.setData(mData);
								adapter.notifyDataSetChanged();
								if (size < 20) {
									listview.hideFooter();
								} else {
									listview.showFooter();
								}
							}
							break;
						case 30:
							break;
						default:
							MyToast.makeText(context, "评论加载失败！", Toast.LENGTH_SHORT).show();
							break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (!Constant.isConnectNet) {
						MyToast.makeText(context, getText(R.string.no_network) + "无法获取评论", Toast.LENGTH_SHORT).show();
					} else {
						MyToast.makeText(context, "无法获取评论，活动可能已经过期！", Toast.LENGTH_LONG).show();
					}

				}

				listview.stopRefresh();
				listview.stopLoadMore();
			}
		});

	}



	/**
	 * 添加评论
	 */
	private void addComment(final String comment){
		Map<String, String> maps = new HashMap<>();
		maps.put("uid", Constant.UID);
		maps.put("token", Constant.TOKEN);
		maps.put("event_id", eid);
		maps.put("content", comment);
		LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_ADD_COMMENT, maps);
		task.getData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				Log.e("1", data.toJSONString());
				try {
					int status = data.getInteger("status");
					switch (status) {
						case 0:
							JSONObject obj = data.getJSONObject("comment");
							JSONObject json = obj.getJSONObject("comments");
							JSONObject c_json = json.getJSONObject("0");
							if (c_json != null) {
								Comment c = new Comment();
								c.setEventID(eid);
								c.setTime(c_json.getLong("comment_time"));
								c.setCommentID(c_json.getString("comment_id"));
								c.setContent(c_json.getString("content"));
								c.setSpeaker(c_json.getString("speaker_id"));
								mData.add(0, c);
								adapter.setData(mData);
								adapter.notifyDataSetChanged();
								et_comment.setText("");
							}
							MyToast.makeText(context, "评论已发送！", Toast.LENGTH_SHORT).show();
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
							break;
						case 10:
						default:
							MyToast.makeText(context, "非常抱歉，评论发表失败！", Toast.LENGTH_SHORT).show();
							et_comment.setText(comment);
							break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (!Constant.isConnectNet) {
						MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
					}
					e.printStackTrace();
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
		Map<String, String> maps = new HashMap<>();
		maps.put("uid", Constant.UID);
		maps.put("token", Constant.TOKEN);
		maps.put("event_id", eid);
		maps.put("comment_id", comment_id);
		LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_DELETE_COMMENT, maps);
		task.getData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					int status=data.getInteger("status");
					switch (status) {
						case 0:
							CommentDao dao=new CommentDao(context);
							dao.deleteComment(comment_id);
							mData.remove(position);
							adapter.notifyDataSetChanged();
							MyToast.makeText(context, "评论已删除！", Toast.LENGTH_SHORT).show();
							break;
						case 11:
						default:
							MyToast.makeText(context, "操作失败，请稍后重试！", Toast.LENGTH_SHORT).show();
							break;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if(!Constant.isConnectNet){
						MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
					}
					e.printStackTrace();
				}
			}
		});

	}

	private String getTimeSpan(long begin_time, long end_time) {
		String begin = getTime(begin_time*1000);
		String end = getTime(end_time*1000);
		String beginString = begin.substring(0, 10);
		String endString = end.substring(0, 10);
		String time;
		if(beginString.equals(endString)){
			time = begin + " ~"+end.substring(10);
		}else{
			time = begin + " ~ "+end;
		}
		return time;
	}

	public String getTime(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		return sdf.format(new Date(now));
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
