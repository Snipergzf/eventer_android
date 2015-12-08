package com.eventer.app.util;

import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.view.DialogView_Two;
import com.eventer.app.view.DialogView_Two.onWheelBtnNegClick;
import com.eventer.app.view.DialogView_Two.onWheelBtnPosClick;
import com.eventer.app.widget.wheel.WheelListAdapter;
import com.eventer.app.widget.wheel.WheelView;

/**
 * 封装了滚轮操作的类
 *
 * */

public class WheelDialogTwoShowUtil {


	private Context mContext;

	private String title;
	private Map<Integer,String[]> data;

	private WheelView wheelView1,wheelView2;
	private Dialog dialog;
	public DialogView_Two dialogView;

	private int visibleItems=5;

	public int getVisibleItems() {
		return visibleItems;
	}


	public void setVisibleItems(int visibleItems) {
		this.visibleItems = visibleItems;
	}


	public WheelView getWheelView(int i) {
		switch (i) {
			case 1:
				return wheelView1;
			case 2:
				return wheelView2;
			default:
				return null;
		}

	}
	public void setTitle(String title) {
		this.title = title;
	}

	public WheelDialogTwoShowUtil(Context mContext,Display mDisplay,Map<Integer,String[]> data,String title) {

		this.mContext = mContext;
		this.data=data;
		this.title=title;

		dialogView=new DialogView_Two(mContext);
		dialogView.setWidth(mDisplay.getWidth());
		dialogView.setHeight(mDisplay.getHeight()/100*40);

		//默认的点击事件
		dialogView.setBtnNegClick(new onWheelBtnNegClick() {

			@Override
			public void onClick(String text, int position[]) {
				// TODO Auto-generated method stub
				dissmissWheel();
			}
		});

		//默认的点击事件
		dialogView.setBtnPosClick(new onWheelBtnPosClick() {

			@Override
			public void onClick(String text, int[] position) {
				// TODO Auto-generated method stub
				dissmissWheel();
			}
		});

		initDialog( dialogView);

	}


	private Dialog initDialog(DialogView_Two dialogView2)	{
		dialog =dialogView2.initDialog(title, "内容");
		initWheel(dialogView2.getWheelView(1),dialogView2.getWheelView(2),data);
		return dialog;
	}


	public void showWheel()	{
		if(dialog !=null)	{
			dialog.show();
		}

	}


	public void dissmissWheel()	{
		if(dialog !=null && dialog.isShowing())	{
			dialog.dismiss();
		}

	}

	public boolean isShowing()	{
		if(dialog !=null && dialog.isShowing())	{
			return true;
		}
		return false;
	}


	public void setWheelHint(int index1,int index2)	{
		wheelView1.setCurrentItem(index1);
		wheelView2.setCurrentItem(index2);
		dialogView.SetHint(index1, index2);
	}

	public void setWindowAlpha(Activity mActivity)	{
		WindowManager.LayoutParams lp =mActivity.getWindow().getAttributes();
		lp.alpha =0.1f;
		mActivity.getWindow().setAttributes(lp);

	}

	private WheelListAdapter mAdapter;
	// Scrolling flag
	@SuppressLint("NewApi")
	private void initWheel(WheelView wheel1,WheelView wheel2,final Map<Integer,String[]> data )	{

		//为dialog的确定和取消按钮设置数据
		dialogView.setWheel(wheel1,wheel2, data);
		wheelView1=wheel1;

		wheelView2=wheel2;

		wheel1.setVisibleItems(visibleItems);
		mAdapter =new WheelListAdapter(mContext,data.get(1), R.layout.wheel_layout, wheel1);
		wheel1.setViewAdapter(mAdapter);

		wheel2.setVisibleItems(visibleItems);
		mAdapter =new WheelListAdapter(mContext,data.get(2), R.layout.wheel_layout, wheel2);
		wheel2.setViewAdapter(mAdapter);

	}

	/**
	 * 在选择完以后要执行的事件
	 * @param view
	 * @param text
	 */
	public  void setTextToView(View view,String text)	{

		if(view instanceof TextView)	{
			TextView mTextView=(TextView)view;
			mTextView.setText(text);
		}

		else if(view instanceof EditText)	{
			EditText mEditText=(EditText)view;
			mEditText.setText(text);
		}
	}


	public void setConnectable(boolean b) {
		// TODO Auto-generated method stub
		dialogView.setConnectable(b);
	}

}