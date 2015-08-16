package com.eventer.app.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.util.DateUtils;
import com.eventer.app.R;
import com.eventer.app.entity.Event;

@SuppressLint("ViewHolder")
public class EventAdapter extends BaseAdapter {   
		    private Context context;                        //运行上下文   
		    private List<Event> listItems;      
		    private LayoutInflater mInflater;            //视图容器   
		    private boolean[] hasChecked;  
		    private android.view.animation.Animation animation;
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
		    animation=AnimationUtils.loadAnimation(context,R.anim.nn);
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
		    public TextView content;
		    public TextView time;
		    public Button Good;
		    public Button Bad;
		    public TextView readCount;
		    public TextView plus;
		    public TextView publisher;
		    
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
			Event card =  listItems.get(position);
			final View view = mInflater.inflate(R.layout.item_event_list, null);
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

			holder.content = (TextView) view.findViewById(R.id.tv_content);
			holder.time = (TextView) view.findViewById(R.id.tv_time);
			holder.publisher=(TextView)view.findViewById(R.id.tv_publisher);
		    
		    
		    //holder.readCount.setText("阅读(" + card.getReadCount() + ")");
		    long pubtime=card.getIssueTime();
		    String piblisher=card.getPublisher();
		    String theme=card.getTheme();
		    String title=card.getTitle();
//		    if(!TextUtils.isEmpty(theme)){
//		    	title="【"+theme+"】"+title;
//		    }
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
			
			
			return view;
		}
}