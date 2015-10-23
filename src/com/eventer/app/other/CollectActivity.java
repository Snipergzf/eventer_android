package com.eventer.app.other;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.EventDao;
import com.eventer.app.db.EventOpDao;
import com.eventer.app.entity.Event;
import com.umeng.analytics.MobclickAgent;

public class CollectActivity extends Activity {
	private ListView listview;
	private MyEventAadpter adapter;
	private Context context;
	private List<Event> mData=new ArrayList<Event>();
	private EventOpDao dao;
	private Dialog mDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect);
		context=this;
		dao=new EventOpDao(context);
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
        listview=(ListView)findViewById(R.id.listview);
		adapter=new MyEventAadpter(context);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Event e=mData.get(position);
				Intent intent=new Intent();
				intent.setClass(context, Activity_EventDetail.class);
				intent.putExtra("event_id", e.getEventID());
//				MyApplication.getInstance().setValueByKey("event_detail", e);
				startActivity(intent);
			}
		});
	}
	/***
	 * 我的收藏列表的适配器
	 * @author LiuNana
	 *
	 */
	public class MyEventAadpter extends BaseAdapter{
	    private LayoutInflater mInflater;  
		  
	    public MyEventAadpter(Context context) {  
	            this.mInflater = LayoutInflater.from(context);  
	     }  
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();  
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			Event event=mData.get(position);
			if (convertView == null) {                
                holder=new ViewHolder();                    
                //可以理解为从vlist获取view  之后把view返回给ListView                  
                convertView = mInflater.inflate(R.layout.item_collect_eventlist, null);  
                holder.title = (TextView)convertView.findViewById(R.id.tv_title); 
                holder.iv=(ImageView)convertView.findViewById(R.id.iv_collect);
                holder.info=(TextView)convertView.findViewById(R.id.tv_info);
                holder.option=(ImageView)convertView.findViewById(R.id.iv_option);
                convertView.setTag(holder);               
            }else {               
                holder = (ViewHolder)convertView.getTag();  
            }  
			String tilte=event.getTitle();
			String theme=event.getTheme();
			String publisher=event.getPublisher();
			String temp_title="";
			String temp_info="";
			if(!TextUtils.isEmpty(theme)){
				temp_title+="【"+theme+"】 ";
			}
			temp_title+=tilte;
			holder.title.setText(temp_title);
			if(!TextUtils.isEmpty(publisher)){
				temp_info+="发布者: "+publisher;
			}
			holder.info.setText(temp_info);
			holder.option.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
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
						mDialog.setContentView(R.layout.dialog_collect_option);
						mDialog.findViewById(R.id.iv_cancel_collect).setOnClickListener(new OnClickListener() {							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								dao.deleteRecord(EventOpDao.COLUMN_NAME_ID+"=? and "+EventOpDao.COLUMN_NAME_OPERATOR+"=?",
										new String[]{mData.get(position).getEventID()+"",Constant.UID+""});
								mData.remove(position);
								notifyDataSetChanged();
								mDialog.dismiss();
							}
						});
						mDialog.findViewById(R.id.iv_comment).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent=new Intent();
								intent.setClass(context, Activity_EventComment.class);
								intent.putExtra("event_id", mData.get(position).getEventID());
				                startActivity(intent);
								mDialog.dismiss();
							}
						});
                       mDialog.findViewById(R.id.iv_share).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent=new Intent();
								intent.putExtra("event_id", mData.get(position).getEventID());
								intent.setClass(context, ShareToSingleActivity.class);
								startActivity(intent);
								mDialog.dismiss();
							}
						});
						mDialog.findViewById(R.id.share_cancel).setOnClickListener(new OnClickListener() {						
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								mDialog.dismiss();
							}
						});
						mDialog.findViewById(R.id.share_layout).setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								// TODO Auto-generated method stub
								mDialog.dismiss();
							}
						});
					}
					mDialog.show();
				}
			});
			
			return convertView;
		}
		
	}
	
	private void refresh(){
		EventDao d=new EventDao(context);
		mData=d.getEventListByInfo(new String[]{Constant.UID+"","1"});
		adapter.notifyDataSetChanged();
		Log.e("1", mData.size()+"");
	}
	//提取出来方便点  
    public final class ViewHolder {   
        public TextView title; 
        public ImageView iv;
        public TextView info;
        public ImageView option;
    }
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		refresh();
	}
	public void back(View v){
		finish();
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	
}
