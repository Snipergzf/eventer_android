package com.eventer.app.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.adapter.CommentAdapter;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.CommentDao;
import com.eventer.app.entity.Comment;
import com.eventer.app.entity.Event;
import com.eventer.app.task.LoadDataFromHTTP;
import com.eventer.app.task.LoadDataFromHTTP.DataCallBack;

public class EventCommentActivity extends Activity {
	private String eid;
	private Event event;
	private Context context;
	private Button btn_comment_send;
	private EditText et_comment;
	private ListView listview;
	private CommentAdapter adapter;
	private List<Comment> mData=new ArrayList<Comment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_comment);
		context=this;
		eid=getIntent().getStringExtra("event_id");
		initView();
		initData();
	}
	



	private void initView() {
		btn_comment_send=(Button)findViewById(R.id.comment_send);
		et_comment=(EditText)findViewById(R.id.comment_et);
		listview=(ListView)findViewById(R.id.listview);
		
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
		
	}
	
	
	
	




	private void initData() {
		// TODO Auto-generated method stub
		getComment(0);
//		CommentDao dao=new CommentDao(context);
//		mData=dao.getCommentList(eid);
//		adapter.setData(mData);
//		adapter.notifyDataSetChanged();
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
					int size=temp_json.size();					
					for(int i=0;i<size;i++){
						JSONObject c_json=temp_json.getJSONObject((size-1-i)+"");
						Comment c=new Comment();
						c.setEventID(eid);
						c.setTime(c_json.getLong("comment_time"));
						c.setCommentID(c_json.getString("comment_id"));
						c.setContent(c_json.getString("content"));
						c.setSpeaker(c_json.getString("speaker_id"));
//						CommentDao dao=new CommentDao(context);
//						dao.saveComment(c);
						mData.add(c);					
					}
					adapter.setData(mData);
					adapter.notifyDataSetChanged();				
				
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

}
