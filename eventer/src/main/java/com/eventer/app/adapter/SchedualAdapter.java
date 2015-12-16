package com.eventer.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.entity.Schedual;
import com.eventer.app.widget.swipemenulistview.BaseSwipListAdapter;

import java.util.List;

public class SchedualAdapter extends BaseSwipListAdapter {
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
	public boolean getReuseEnable() {
		// TODO Auto-generated method stub
		return true;
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
		Schedual item =  list.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_schedual_list, parent , false);
			new ViewHolder(convertView);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		String place=item.getPlace();
		String time=item.getStarttime();
		int status=item.getStatus();
		int type=item.getType();
		String title=item.getTitle();
		if(!TextUtils.isEmpty(title)){
			holder.tv_title.setText(title);
		}else{
			holder.tv_title.setText("(无标题)");
		}
		if(!TextUtils.isEmpty(place)){
			holder.tv_info.setText(place);
		}else{
			holder.tv_info.setText("");
		}
		if(!TextUtils.isEmpty(time)&&!time.equals("00:00")){
			holder.tv_time.setText(time);
		}else{
			holder.tv_time.setText("");
		}
		if(status==0){
			holder.tv_type.setText("完成");
			holder.tv_type.setTextColor(colorRes[0]);
			holder.view_temp.setBackgroundColor(colorRes[0]);
			holder.iv_status.setVisibility(View.VISIBLE);
		}else{
			switch (type) {
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
		}

		return convertView;
	}
	class ViewHolder {
		TextView tv_title;
		TextView tv_time;
		TextView tv_type;
		TextView tv_info;
		ImageView iv_status;
		View view_temp;

		public ViewHolder(View view) {
			iv_status=(ImageView)view.findViewById(R.id.iv_status);
			tv_time=(TextView)view.findViewById(R.id.tv_time);
			tv_info=(TextView)view.findViewById(R.id.tv_info);
			tv_title=(TextView)view.findViewById(R.id.tv_title);
			tv_type=(TextView)view.findViewById(R.id.tv_type);
			view_temp=view.findViewById(R.id.view_temp);
			view.setTag(this);
		}
	}

}
