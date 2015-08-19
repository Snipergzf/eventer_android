package com.eventer.app.adapter;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.util.DateUtils;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.EventOpDao;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.entity.Event;
import com.eventer.app.entity.EventOp;
import com.eventer.app.entity.Schedual;
import com.eventer.app.main.CalendarFragment;
import com.eventer.app.other.EventCommentActivity;
import com.eventer.app.other.ShareToGroupActivity;
import com.eventer.app.other.ShareToSingleActivity;

@SuppressLint("ViewHolder")
public class EventAdapter extends BaseAdapter {   
		    private Context context;                        //运行上下文   
		    private List<Event> listItems;      
		    private LayoutInflater mInflater; 
		    private Dialog mDialog;//视图容器   
		    private boolean[] hasChecked;  

		    public final class ListItemView{                //自定义控件集合     
		            public ImageView image;     
		            public TextView title;     
		            public TextView info;  
		            public Button detail;          
		     }     
		
		
		public EventAdapter(Context context,  List<Event>  listItems) {   
		    this.context = context;            
		    this.mInflater = LayoutInflater.from(context);   //创建视图容器并设置上下文   
		    this.listItems = listItems;   
		    hasChecked = new boolean[getCount()];   
		}   
		 
		  
		@Override
		public int getCount() {
			// TODO 自动生成的方法存根
			return  listItems.size();
		}
		

		public void addItem(Event e) {
			// TODO 自动生成的方法存根
			listItems.add(0,e);
		}
		
		public void clearItem() {
			// TODO 自动生成的方法存根
			listItems=new ArrayList<Event>();
		}
		
		
		@Override
		public Object getItem(int position) {
			// TODO 自动生成的方法存根
			return listItems.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			// TODO 自动生成的方法存根
			return position;
		}
		//提取出来方便点  
		public final class ViewHolder {   
		     TextView content;
		     TextView time;
		     TextView publisher;
		     LinearLayout li_collect,li_share,li_comment;
		     ImageView iv_collect;
		    
		}
		
		public String getTime(long now) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(new Date(now));
			return date;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO 自动生成的方法存根		
			ViewHolder holder = null; 
			final Event card =  listItems.get(position);
			convertView = mInflater.inflate(R.layout.item_event_list, null);
//			if (convertView == null) {  
//		      
//				holder=new ViewHolder();
//				convertView = mInflater.inflate(R.layout.list_item, null); 
//				holder.readCount = (TextView) convertView.findViewById(R.id.tv_readCount);
//				holder.content = (TextView) convertView.findViewById(R.id.tv_content);
//				holder.time = (TextView) convertView.findViewById(R.id.tv_time);
//				holder.Good=(Button) convertView.findViewById(R.id.bt_good);
//				holder.Bad=(Button) convertView.findViewById(R.id.bt_bad);
//			 }else {  
//		            holder = (ViewHolder)convertView.getTag();  
//		     } 
					
			holder=new ViewHolder();

			holder.content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.publisher=(TextView)convertView.findViewById(R.id.tv_publisher);
			holder.li_collect=(LinearLayout)convertView.findViewById(R.id.li_collect);
			holder.li_comment=(LinearLayout)convertView.findViewById(R.id.li_comment);
			holder.li_share=(LinearLayout)convertView.findViewById(R.id.li_share);
			holder.iv_collect=(ImageView)convertView.findViewById(R.id.iv_collect);
		    
		    
		    //holder.readCount.setText("阅读(" + card.getReadCount() + ")");
		    long pubtime=card.getIssueTime();
		    String piblisher=card.getPublisher();
		    String title=card.getTitle();
		    if(pubtime>0)
			holder.time.setText(DateUtils.getTimestampString(new Date(pubtime*1000)));
		    else{
		    	holder.time.setText("");
		    }
		    holder.content.setText(title);
			if(!TextUtils.isEmpty(piblisher)){
				holder.publisher.setText(card.getPublisher());
			}else{
				holder.publisher.setText("匿名发布");
			}
			
			EventOpDao dao=new EventOpDao(context);
			boolean isCollect=dao.getIsCollect(card.getEventID());
			if(isCollect){
				holder.iv_collect.setSelected(true);
			}else{
				holder.iv_collect.setSelected(false);
			}
			
			holder.li_comment.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent();
					intent.setClass(context, EventCommentActivity.class);
					intent.putExtra("event_id", card.getEventID());
	                context.startActivity(intent);
				}
			});
			
			holder.li_collect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SchedualDao sdao=new SchedualDao(context);
					EventOpDao dao=new EventOpDao(context);
					boolean isCollect=dao.getIsCollect(card.getEventID());
					ImageView iv_collect=(ImageView)v.findViewById(R.id.iv_collect);
					if(isCollect){
							sdao.delSchedualByEventId(card.getEventID());
							dao.cancelCollect(card.getEventID());
							iv_collect.setSelected(false);
							Animation scale_anim_1 = AnimationUtils.loadAnimation(context, R.anim.scale);
							iv_collect.startAnimation(scale_anim_1);
							CalendarFragment.instance.refreshView();
							
					}else{
						iv_collect.setSelected(true);
						Animation scale_anim_1 = AnimationUtils.loadAnimation(context, R.anim.scale);
						iv_collect.startAnimation(scale_anim_1);
						EventOp e=new EventOp();
						e.setEventID(card.getEventID());
						e.setOperator(Constant.UID);
						e.setOperation(1);//以表示收藏
						e.setOpTime(System.currentTimeMillis()/1000);
						dao.saveEventOp(e);							
						String time=card.getTime();
						Log.e("1", time);						
						try {
							JSONArray json=new JSONArray(time);	
							for(int i=0;i<json.length()/2;i++){
								Schedual s=new Schedual();
								s.setSchdeual_ID(System.currentTimeMillis()/1000);
								s.setEventId(card.getEventID());
								String starttime=getFullTime(json.getLong(2*i)*1000);
								String endtime=getFullTime(json.getLong(2*i+1)*1000);
								s.setStarttime(starttime);
								s.setEndtime(endtime);
								s.setRemindtime(starttime);
								s.setTitle(card.getTitle());	
								s.setPlace(card.getPlace());
								int status=getStatus(endtime,starttime);								
								s.setStatus(status);
								if(status!=0){
									s.setStatus(1);
								}
								s.setFrequency(0);
								sdao.saveSchedual(s);
								CalendarFragment.instance.refreshView();
							}
							
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							//e1.printStackTrace();
						}
					}
					
				}
			});
			holder.li_share.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
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
						mDialog.findViewById(R.id.share_by_chatroom).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent1=new Intent();
								intent1.putExtra("event_id", card.getEventID());
								intent1.putExtra("sharetype", ShareToSingleActivity.SHARE_EVENT);
								intent1.setClass(context, ShareToGroupActivity.class);
								context.startActivity(intent1);
								mDialog.dismiss();
							}
						});
						mDialog.findViewById(R.id.share_by_user).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent2=new Intent();
								intent2.putExtra("event_id", card.getEventID());
								intent2.putExtra("sharetype", ShareToSingleActivity.SHARE_EVENT);
								intent2.setClass(context, ShareToSingleActivity.class);
								context.startActivity(intent2);
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
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								mDialog.dismiss();
							}
						});
					}
					mDialog.show();// TODO Auto-generated method stub
					
				}
			});
			return convertView;
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
}