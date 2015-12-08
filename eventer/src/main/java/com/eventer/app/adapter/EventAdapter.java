package com.eventer.app.adapter;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.EventOpDao;
import com.eventer.app.entity.Event;
import com.eventer.app.util.FileUtil;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint({ "ViewHolder", "SimpleDateFormat" })
public class EventAdapter extends BaseAdapter {
	private Context context;                        //运行上下文
	private List<Event> listItems;
	private LayoutInflater mInflater;
	private Dialog mDialog;//视图容器
	private String image="";
	private FileUtil fileUtil;

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
		fileUtil = new FileUtil(context, Constant.IMAGE_PATH);
	}


	@Override
	public int getCount() {
		return  listItems.size();
	}


	public void addItem(Event e) {
		listItems.add(0,e);
	}

	public void clearItem() {
		listItems=new ArrayList<Event>();
	}


	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder {
		TextView time,place,theme,content;
		TextView publisher;
		LinearLayout li_collect,li_share,li_comment;
		ImageView iv_collect;
		ImageView iv_pic;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自动生成的方法存根
		ViewHolder holder = null;
		final Event card =  listItems.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_event_list, null);
			holder=new ViewHolder();

			holder.content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.place=(TextView)convertView.findViewById(R.id.tv_place);
			holder.theme=(TextView)convertView.findViewById(R.id.tv_theme);
//				holder.publisher=(TextView)convertView.findViewById(R.id.tv_publisher);
			holder.li_collect=(LinearLayout)convertView.findViewById(R.id.li_collect);
			holder.li_comment=(LinearLayout)convertView.findViewById(R.id.li_comment);
			holder.li_share=(LinearLayout)convertView.findViewById(R.id.li_share);
			holder.iv_collect=(ImageView)convertView.findViewById(R.id.iv_collect);
			holder.iv_pic=(ImageView)convertView.findViewById(R.id.iv_pic);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}

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


		holder.iv_pic.setImageResource(R.color.transparent);

		String place=card.getPlace();
		holder.place.setText("地点: "+place);
		String theme=card.getTheme();
		holder.theme.setText(theme);

		String time=card.getTime();
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
		}//时间
		holder.time.setText("时间: "+timeString);
		String title=card.getTitle();
		holder.content.setText(title);

		EventOpDao dao=new EventOpDao(context);
		boolean isCheck=dao.getIsVisit(card.getEventID());
		if(isCheck){
//		    	convertView.setAlpha(0.75f);
//		    	holder.iv_pic.setAlpha(0.6f);
//		    	holder.theme.setAlpha(0.6f);
			holder.content.setAlpha(0.6f);
			holder.place.setAlpha(0.6f);
			holder.time.setAlpha(0.6f);
		}else{
//		    	convertView.setAlpha(1f);
//		    	holder.iv_pic.setAlpha(1f);
//		    	holder.theme.setAlpha(1f);
			holder.content.setAlpha(1f);
			holder.place.setAlpha(1f);
			holder.time.setAlpha(1f);

		}

		image="";
		Spanned sp = Html.fromHtml(card.getContent(), new Html.ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				if(TextUtils.isEmpty(image))
					image=source;
				return null;
			}

		}, null);
		showEventPicture(holder.iv_pic,image);

		//holder.readCount.setText("阅读(" + card.getReadCount() + ")");
//		    long pubtime=card.getIssueTime();
//		    String piblisher=card.getPublisher();
//		    
//		    if(pubtime>0)
//			holder.time.setText(DateUtils.getTimestampString(new Date(pubtime*1000)));
//		    else{
//		    	holder.time.setText("");
//		    }

//			if(!TextUtils.isEmpty(piblisher)){
//				holder.publisher.setText(card.getPublisher());
//			}else{
//				holder.publisher.setText("匿名发布");
//			}

//			EventOpDao dao=new EventOpDao(context);
//			boolean isCollect=dao.getIsCollect(card.getEventID());
//			if(isCollect){
//				holder.iv_collect.setSelected(true);
//			}else{
//				holder.iv_collect.setSelected(false);
//			}
//			//活动评论
//			holder.li_comment.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Intent intent=new Intent();
//					intent.setClass(context, Activity_EventComment.class);
//					intent.putExtra("event_id", card.getEventID());
//	                context.startActivity(intent);
//				}
//			});
//			//活动收藏
//			holder.li_collect.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					SchedualDao sdao=new SchedualDao(context);
//					EventOpDao dao=new EventOpDao(context);
//					boolean isCollect=dao.getIsCollect(card.getEventID());
//					ImageView iv_collect=(ImageView)v.findViewById(R.id.iv_collect);
//					if(isCollect){
//							sdao.delSchedualByEventId(card.getEventID());
//							dao.cancelCollect(card.getEventID());
//							iv_collect.setSelected(false);
//							Animation scale_anim_1 = AnimationUtils.loadAnimation(context, R.anim.scale);
//							iv_collect.startAnimation(scale_anim_1);
//							CalendarFragment.instance.refreshView();
//							
//					}else{
//						iv_collect.setSelected(true);
//						Animation scale_anim_1 = AnimationUtils.loadAnimation(context, R.anim.scale);
//						iv_collect.startAnimation(scale_anim_1);
//						EventOp e=new EventOp();
//						e.setEventID(card.getEventID());
//						e.setOperator(Constant.UID);
//						e.setOperation(1);//以表示收藏
//						e.setOpTime(System.currentTimeMillis()/1000);
//						dao.saveEventOp(e);							
//						String time=card.getTime();
//						Log.e("1", time);						
//						try {
//							JSONArray json=new JSONArray(time);	
//							for(int i=0;i<json.length()/2;i++){
//								Schedual s=new Schedual();
//								s.setSchdeual_ID(System.currentTimeMillis()/1000);
//								s.setEventId(card.getEventID());
//								String starttime=getFullTime(json.getLong(2*i)*1000);
//								String endtime=getFullTime(json.getLong(2*i+1)*1000);
//								s.setStarttime(starttime);
//								s.setEndtime(endtime);
//								s.setRemindtime(starttime);
//								s.setTitle(card.getTitle());	
//								s.setPlace(card.getPlace());
//								int status=getStatus(endtime,starttime);								
//								s.setStatus(status);
//								s.setType(1);
//								s.setFrequency(0);
//								sdao.saveSchedual(s);
//								CalendarFragment.instance.refreshView();
//							}
//							
//						} catch (JSONException e1) {
//							// TODO Auto-generated catch block
//							//e1.printStackTrace();
//						}
//					}
//					
//				}
//			});
//			//活动分享
//			holder.li_share.setOnClickListener(new OnClickListener() {				
//				@Override
//				public void onClick(View v) {
//					if (mDialog == null) {
//						mDialog = new Dialog(context, R.style.login_dialog);
//						mDialog.setCanceledOnTouchOutside(true);
//						Window win = mDialog.getWindow();
//						LayoutParams params = new LayoutParams();
//						params.width = LayoutParams.MATCH_PARENT;
//						params.height = LayoutParams.WRAP_CONTENT;
//						params.x = 0;
//						params.y = 0;
//						win.setAttributes(params);
//						mDialog.setContentView(R.layout.dialog_share);
//						mDialog.findViewById(R.id.share_by_chatroom).setOnClickListener(new OnClickListener() {
//							
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//								Intent intent1=new Intent();
//								intent1.putExtra("event_id", card.getEventID());
//								intent1.putExtra("sharetype", ShareToSingleActivity.SHARE_EVENT);
//								intent1.setClass(context, ShareToGroupActivity.class);
//								context.startActivity(intent1);
//								mDialog.dismiss();
//							}
//						});
//						mDialog.findViewById(R.id.share_by_user).setOnClickListener(new OnClickListener() {
//							
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//								Intent intent2=new Intent();
//								intent2.putExtra("event_id", card.getEventID());
//								intent2.putExtra("sharetype", ShareToSingleActivity.SHARE_EVENT);
//								intent2.setClass(context, ShareToSingleActivity.class);
//								context.startActivity(intent2);
//								mDialog.dismiss();
//							}
//						});
//						mDialog.findViewById(R.id.share_cancel).setOnClickListener(new OnClickListener() {
//							
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//								mDialog.dismiss();
//							}
//						});
//						mDialog.findViewById(R.id.share_layout).setOnClickListener(new OnClickListener() {
//							
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated method stub
//								mDialog.dismiss();
//							}
//						});
//					}
//					mDialog.show();// TODO Auto-generated method stub
//					
//				}
//			});
		return convertView;
	}

	private void showEventPicture(final ImageView iv_pic, String url) {
		// TODO Auto-generated method stub
		if(url==null||url.equals("")) {
			iv_pic.setImageResource(R.drawable.default_avatar);
			return;
		}
		final String url_avatar = url;
		iv_pic.setTag(url_avatar);
		new AsyncTask<String, Object,Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {

				InputStream is = null;
				String filename = url_avatar
						.substring(url_avatar.lastIndexOf("/") + 1)+"_e";
				String filepath = fileUtil.getAbsolutePath() + filename;
				try {
					Bitmap bitmap = BitmapFactory.decodeFile(filepath);
					if(bitmap!=null){
//								d = new BitmapDrawable(context.getResources(),bitmap);
					}else{
						is = (InputStream) new URL(url_avatar).getContent();
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
				if(bitmap!=null&&iv_pic.getTag().equals(url_avatar)){
					iv_pic.setImageBitmap(bitmap);
				}
			};
		}.execute(new String[]{});
	}


	public String getFullTime(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = sdf.format(new Date(now));
		return date;
	}

	public String getDate(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(now));
		return date;
	}

	private String getTimeSpan(long begin_time, long end_time) {
		// TODO Auto-generated method stub
		String begin=getDate(begin_time*1000);
		String end=getDate(end_time*1000);
		String time="";
		if(begin.equals(end)){
			time=begin;
		}else{
			time=begin+" ~ "+end;
		}
		return time;
	}
}