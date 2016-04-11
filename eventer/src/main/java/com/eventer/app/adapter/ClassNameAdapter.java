package com.eventer.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.entity.ClassInfo;

import java.util.List;

@SuppressWarnings({"UnusedDeclaration"})
public class ClassNameAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private int mPos = 0;
	private List<ClassInfo> mList;

	public ClassNameAdapter(Context ctx, List<ClassInfo> list) {
		this.mInflater = LayoutInflater.from(ctx);
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
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
			convertView = mInflater.inflate(R.layout.item_class_name, parent , false );
			holder = new Viewholder();
			holder.title = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (Viewholder) convertView.getTag();
		}
		String name = mList.get(position).getClassname();
		if(!TextUtils.isEmpty(name)){
			holder.title.setText(mList.get(position).getClassname());
		}else{
			holder.title.setText("(未命名)");
		}

		return convertView;
	}

	public void setPos(int position) {
		mPos = position;
	}

	private class Viewholder {
		TextView title;
	}

}
