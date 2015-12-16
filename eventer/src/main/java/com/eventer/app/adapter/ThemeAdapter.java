package com.eventer.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eventer.app.R;

import java.util.List;
@SuppressWarnings({"UnusedDeclaration"})
public class ThemeAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	
	private int mPos = 0;
	private List<String> mList;

	public ThemeAdapter(Context ctx, List<String> list) {
		this.mInflater = LayoutInflater.from(ctx);
		this.mList = list;
	}

	@Override
	public int getCount() {
		return 8;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Viewholder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.info_theme_item, parent , false );
			holder = new Viewholder();
			holder.title = (TextView) convertView.findViewById(R.id.info_top_item_title);
			convertView.setTag(holder);
		} else {
			holder = (Viewholder) convertView.getTag();
		}

		if (position == mPos) {
			holder.title.setTextColor(Color.parseColor("#007aff"));
		} else {
			holder.title.setTextColor(Color.parseColor("#434343"));
		}
		holder.title.setText(mList.get(position));
		return convertView;
	}

	public void setPos(int position) {
		mPos = position;
	}

	private class Viewholder {
		TextView title;
	}

}
