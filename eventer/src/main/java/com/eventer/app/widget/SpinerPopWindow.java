package com.eventer.app.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.eventer.app.R;
import com.eventer.app.widget.AbstractSpinerAdapter.IOnItemSelectListener;

import java.util.List;

public class SpinerPopWindow extends PopupWindow implements OnItemClickListener{

	private Context mContext;
	ListView mListView;
	private NormalSpinerAdapter mAdapter;
	private IOnItemSelectListener mItemSelectListener;
	
	
	public SpinerPopWindow(Context context)
	{
		super(context);
		
		mContext = context;
		init();
	}
	
	
	public void setItemListener(IOnItemSelectListener listener){
		mItemSelectListener = listener;
	}

	
	private void init()
	{
		View view = LayoutInflater.from(mContext).inflate(R.layout.spiner_window_layout,new LinearLayout(mContext),false);
		setContentView(view);		
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		
		setFocusable(true);
    	ColorDrawable dw = new ColorDrawable(0x00);
		setBackgroundDrawable(dw);
	
		
		mListView = (ListView) view.findViewById(R.id.listview);
		

		mAdapter = new NormalSpinerAdapter(mContext);	
		mListView.setAdapter(mAdapter);	
		mListView.setOnItemClickListener(this);
	}
	
	
	public void refreshData(List<String> list, int selIndex)
	{
		if (list != null && selIndex  != -1)
		{
			mAdapter.refreshData(list, selIndex);
		}
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		dismiss();
		if (mItemSelectListener != null){
			mItemSelectListener.onItemClick(pos);
		}
	}


	
}
