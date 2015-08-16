package com.eventer.app.adapter;

import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.entity.Schedual;

public class SchedualAdapter extends BaseAdapter {
	Context context;
    List<Schedual> list;
    private LayoutInflater mInflater;
    private int[] colorRes;
    public SchedualAdapter(Context context, List<Schedual> list) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
        colorRes=new int[]{ Color.argb(200, 221, 221, 221),Color.argb(200, 71, 154, 199),
        		 Color.argb(200, 102, 204, 204), Color.argb(200, 51, 81, 229)};
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Schedual getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}
	
	public void setData(List<Schedual> list){
		this.list=list;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null; 
		Schedual item =  list.get(position);
		final int loc=position;		
		if (convertView == null) {  	
			holder=new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_schedual_list, null); 
			holder.iv_status=(ImageView)convertView.findViewById(R.id.iv_status);
			holder.tv_time=(TextView)convertView.findViewById(R.id.tv_time);
			holder.tv_info=(TextView)convertView.findViewById(R.id.tv_info);
			holder.tv_title=(TextView)convertView.findViewById(R.id.tv_title);
			holder.tv_type=(TextView)convertView.findViewById(R.id.tv_type);
			holder.view_temp=(View)convertView.findViewById(R.id.view_temp);
			convertView.setTag(holder);
			
		}else {  
	        holder = (ViewHolder)convertView.getTag();  
	   } 
		String place=item.getPlace();
		String eid=item.getEventId();
		String time=item.getStarttime();
		int status=item.getStatus();
		holder.tv_title.setText(item.getTitle());
		if(!TextUtils.isEmpty(place)){
			holder.tv_info.setText(place);
		}else{
			holder.tv_info.setText("");
		}
		if(!TextUtils.isEmpty(eid)){
			holder.tv_type.setText("活动");
		}else{
			holder.tv_type.setText("日程");
		}
		if(!TextUtils.isEmpty(time)&&!time.equals("00:00")){
			holder.tv_time.setText(time);
		}else{
			holder.tv_time.setText("");
		}
		
		switch (status) {
		case 0:
			holder.tv_type.setText("完成");
			holder.tv_type.setTextColor(colorRes[0]);
			holder.view_temp.setBackgroundColor(colorRes[0]);
			holder.iv_status.setVisibility(View.VISIBLE);
			break;
		case 1:
			holder.tv_type.setText("活动");
			holder.tv_type.setTextColor(colorRes[1]);
			holder.view_temp.setBackgroundColor(colorRes[1]);
			holder.iv_status.setVisibility(View.GONE);
			break;
		case 2:
			holder.tv_type.setText("日程");
			holder.tv_type.setTextColor(colorRes[2]);
			holder.view_temp.setBackgroundColor(colorRes[2]);
			holder.iv_status.setVisibility(View.GONE);
			break;
		case 3:
			holder.tv_type.setText("待办");
			holder.tv_type.setTextColor(colorRes[3]);
			holder.view_temp.setBackgroundColor(colorRes[3]);
			holder.iv_status.setVisibility(View.GONE);
            break;
		default:
			break;
		}
		
		
		return convertView;
	}
	
	private static class ViewHolder {

        TextView tv_title;
        TextView tv_time;
        TextView tv_type;
        TextView tv_info;
        ImageView iv_status;
        View view_temp;

    }

}
