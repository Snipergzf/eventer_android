package com.eventer.app.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.main.CourseFragment;
import com.eventer.app.main.ScheduleFragment;

public class PopMenu {
	private ArrayList<String> itemList;
	private Context context;
	private PopupWindow popupWindow ;
	private ListView listView;
	private static int checkedId=0;
	//private OnItemClickListener listener;
	

	public PopMenu(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;

		itemList = new ArrayList<String>();
		
		View view = LayoutInflater.from(context).inflate(R.layout.popmenu, null);
        
        //设置 listview
        listView = (ListView)view.findViewById(R.id.listView);
        listView.setAdapter(new PopAdapter());
        listView.setFocusableInTouchMode(true);
        listView.setFocusable(true);
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.e("1", "position");
				popupWindow.dismiss();
			}
		});
        
        popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        popupWindow = new PopupWindow(view,
        		context.getResources().getDimensionPixelSize(R.dimen.popmenu_width), 
        		context.getResources().getDimensionPixelSize(R.dimen.popmenu_height));
        
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
       popupWindow.setBackgroundDrawable(new BitmapDrawable());
       // popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), Bitmap.createBitmap(null)));
      
	}

	//设置菜单项点击监听器
	public void setOnItemClickListener(OnItemClickListener listener) {
		//this.listener = listener;
		listView.setOnItemClickListener(listener);
	}

//	OnItemClickListener listener=new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) {
//			// TODO Auto-generated method stub
//			 
//			// popupWindow.dismiss();
//			// Activity_Course.instance.changeWeek(position);
//		}
//	};
	
	//批量添加菜单项
	public void addItems(String[] items) {
		for (String s : items)
			itemList.add(s);
	}

	//单个添加菜单项
	public void addItem(String item) {
		itemList.add(item);
	}
	
	//单个添加菜单项
	public void setCheckedItem(int position) {
		checkedId=position-1;
	}

	//下拉式 弹出 pop菜单 parent 右下角
	public void showAsDropDown(View parent) {
		popupWindow.showAsDropDown(parent, parent.getWidth()/2-popupWindow.getWidth()/2, 
				//保证尺寸是根据屏幕像素密度来的
				context.getResources().getDimensionPixelSize(R.dimen.popmenu_yoff));
		
		// 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //刷新状态
        popupWindow.update();
	}
	
	//隐藏菜单
	public void dismiss() {
		popupWindow.dismiss();
	}

	// 适配器
	private final class PopAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return itemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.pomenu_item, null);
				holder = new ViewHolder();

				convertView.setTag(holder);

				holder.groupItem = (TextView) convertView.findViewById(R.id.textView);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.groupItem.setText(itemList.get(position));
			if(position==checkedId){
				holder.groupItem.setBackgroundColor(Color.parseColor(context.getResources().getString(R.color.caldroid_holo_blue_light)));
				holder.groupItem.setTextColor(Color.parseColor(context.getResources().getString(R.color.caldroid_white)));
			}else{
				holder.groupItem.setBackgroundColor(Color.TRANSPARENT);
				holder.groupItem.setTextColor(Color.parseColor(context.getResources().getString(R.color.caldroid_holo_blue_light)));
			}
			holder.groupItem.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.e("1", "po"+position);
					 popupWindow.dismiss();
					 CourseFragment.instance.changeWeek(position+1);
				}
			});
			return convertView;
		}

		private final class ViewHolder {
			TextView groupItem;
		}
	}
}
