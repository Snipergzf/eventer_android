package com.eventer.app.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.view.DialogView_ClassHour;
import com.eventer.app.view.DialogView_ClassHour.onWheelBtnNegClick;
import com.eventer.app.view.DialogView_ClassHour.onWheelBtnPosClick;
import com.eventer.app.view.wheel.WheelListAdapter;
import com.eventer.app.view.wheel.WheelView;

import java.util.Map;

/**
 * 封装了滚轮操作的类
 *
 * */
@SuppressWarnings({"UnusedDeclaration"})
public class WheelDialogClassHourShowUtil {


	private Context mContext;

	private String title;
	private Map<Integer,String[]> data;

	private WheelView wheelView1,wheelView2,wheelView3;
	private Dialog dialog;
	public DialogView_ClassHour dialogView;

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
			case 3:
				return wheelView3;
			default:
				return null;
		}

	}
	public void setTitle(String title) {
		this.title = title;
	}

	public WheelDialogClassHourShowUtil(Context mContext,Display mDisplay,Map<Integer,String[]> data,String title) {

		this.mContext = mContext;
		this.data=data;
		this.title=title;

		dialogView=new DialogView_ClassHour(mContext);
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
			public void onClick(String[] text, int[] position) {
				// TODO Auto-generated method stub
				dissmissWheel();
			}
		});

		initDialog( dialogView);

	}


	private Dialog initDialog(DialogView_ClassHour dialogView2)	{
		dialog =dialogView2.initDialog(title, "内容");
		initWheel(dialogView2.getWheelView(1),dialogView2.getWheelView(2),dialogView2.getWheelView(3),data);
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
		return dialog != null && dialog.isShowing();
	}


	public void setWheelHint(int index1,int index2,int index3)	{
		wheelView1.setCurrentItem(index1);
		wheelView2.setCurrentItem(index2);
		wheelView3.setCurrentItem(index3);
		dialogView.SetHint(index1, index2,index3);
	}

	public void setWindowAlpha(Activity mActivity)	{
		WindowManager.LayoutParams lp =mActivity.getWindow().getAttributes();
		lp.alpha =0.1f;
		mActivity.getWindow().setAttributes(lp);

	}

	WheelListAdapter mAdapter;
	// Scrolling flag
	@SuppressLint("NewApi")
	private void initWheel(WheelView wheel1,WheelView wheel2,WheelView wheel3,final Map<Integer,String[]> data )	{

		//为dialog的确定和取消按钮设置数据
		dialogView.setWheel(wheel1,wheel2, wheel3,data);
		wheelView1=wheel1;

		wheelView2=wheel2;
		wheelView3=wheel3;

		wheel1.setVisibleItems(visibleItems);
		mAdapter =new WheelListAdapter(mContext,data.get(1), R.layout.wheel_layout, wheel1);
		wheel1.setViewAdapter(mAdapter);

		wheel2.setVisibleItems(visibleItems);
		mAdapter =new WheelListAdapter(mContext,data.get(2), R.layout.wheel_layout, wheel2);
		wheel2.setViewAdapter(mAdapter);

		wheel3.setVisibleItems(visibleItems);
		mAdapter =new WheelListAdapter(mContext,data.get(3), R.layout.wheel_layout, wheel3);
		wheel3.setViewAdapter(mAdapter);

	}

	/**
	 * 在选择完以后要执行的事件
	 */
	public  void setTextToView(View view,String text)	{

		if(view instanceof TextView)	{
			TextView mTextView=(TextView)view;
			mTextView.setText(text);
		}

//		else if(view instanceof EditText)	{
//			EditText mEditText=(EditText)view;
//			mEditText.setText(text);
//		}
	}

}
