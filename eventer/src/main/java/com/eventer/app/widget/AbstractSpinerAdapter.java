package com.eventer.app.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eventer.app.R;

import java.util.ArrayList;
import java.util.List;
@SuppressWarnings({"UnusedDeclaration"})
public abstract class AbstractSpinerAdapter<T> extends BaseAdapter {
	public  interface IOnItemSelectListener{
		void onItemClick(int pos);
	}
	
	 private List<T> mObjects = new ArrayList<>();
	 private LayoutInflater mInflater;
	
	 public  AbstractSpinerAdapter(Context context){
		 init(context);
	 }
	 
	 public void refreshData(List<T> objects, int selIndex){
		 mObjects = objects;
	 }
	 
	 private void init(Context context) {
	        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }
	    
	    
	@Override
	public int getCount() {

		return mObjects.size();
	}

	@Override
	public Object getItem(int pos) {
		return mObjects.get(pos).toString();
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		 ViewHolder viewHolder;
    	 
	     if (convertView == null) {
	    	 convertView = mInflater.inflate(R.layout.spiner_item_layout, arg2,false);
	         viewHolder = new ViewHolder();
	         viewHolder.mTextView = (TextView) convertView.findViewById(R.id.textView);
	         convertView.setTag(viewHolder);
	     } else {
	         viewHolder = (ViewHolder) convertView.getTag();
	     }

	     
	     String item = (String) getItem(pos);
		 viewHolder.mTextView.setText(item);

	     return convertView;
	}

	public static class ViewHolder
	{
	    public TextView mTextView;
    }


}
