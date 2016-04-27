package com.eventer.app.view;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.view.wheel.OnWheelScrollListener;
import com.eventer.app.view.wheel.WheelView;

@SuppressWarnings({"UnusedDeclaration"})
public class DialogView {

	
	
	private Context mContext;
	private LayoutInflater mInflater;
	
	private WheelView wheel;
	private String[] data ;
	
	public WheelView getWheel() {
		return wheel;
	}


	public void setWheel(WheelView wheel,String[] data) {
		this.wheel = wheel;
		this.data=data;
		initWheelView();
	}


	public DialogView(Context mContext) {
		this.mContext = mContext;
		this.mInflater=LayoutInflater.from(mContext);
	}

	int layout_resource=0;

	
	 
	TextView tv_title;
//	private TextView tv_message;
	public  Button btn_positive;
	public Button btn_negative;
	
	private WheelView wheelView ;
	
	
	private int width=300;
	public int getWidth() {
		return width;
	}


	public void setWidth(int width) {
		this.width = width;
	}





	public int getHeight() {
		return height;
	}





	public void setHeight(int height) {
		this.height = height;
	}

	private int height=300;
	public WheelView getWheelView() {
		return wheelView;
	}





	public  Dialog initDialog(String title,String message) {
		View mView;
		if(layout_resource==0)	{
			mView=mInflater.inflate(R.layout.layout_wheel_dialog, new LinearLayout(mContext),false);
		}
		else {
			mView=mInflater.inflate(layout_resource, null);
		}
		
		tv_title=(TextView) mView.findViewById(R.id.tv_title);
//		tv_message=(TextView) mView.findViewById(R.id.tv_message);
		btn_positive=(Button) mView.findViewById(R.id.btn_positive);
		btn_negative=(Button) mView.findViewById(R.id.btn_negative);
		
		btn_positive.setOnClickListener(mOnClickListener);
		btn_negative.setOnClickListener(mOnClickListener);
		
		tv_title.setText(title);
		
		
		 wheelView = (WheelView) mView.findViewById(R.id.wheel01);
		 wheelView.setVisibleItems(5);
		 
		 
//		tv_message.setText(message);
		
		Dialog dialog =new Dialog(mContext,R.style.MyAlertDialog);
				dialog.setContentView(mView);
				//点击其他区域不消�?
				dialog.setCanceledOnTouchOutside(false);
				
				setParams( dialog);
				
				Window window = dialog.getWindow(); 
				window.setWindowAnimations(R.style.wheelDialogAnimation); 
				
				
				return dialog;
				
				
	}	
	
	
	public void showDialog(Dialog dialog)	{
		if(dialog !=null)	{
			dialog.show();
		}
		
	}
	
	
	
	
	
	private void setParams(Dialog dialog)	{
		   /* 
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置,
         * 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属�?
         */
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        
        /*
         * lp.x与lp.y表示相对于原始位置的偏移.
         * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
         * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
         * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
         * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
         * 当参数值包含Gravity.CENTER_HORIZONTAL时
         * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
         * 当参数值包含Gravity.CENTER_VERTICAL时
         * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
         * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
         * Gravity.CENTER_VERTICAL.
         * 
         * 本来setGravity的参数值为Gravity.LEFT | Gravity.TOP时对话框应出现在程序的左上角,但在
         * 我手机上测试时发现距左边与上边都有一小段距离,而且垂直坐标把程序标题栏也计算在内了,
         * Gravity.LEFT, Gravity.TOP, Gravity.BOTTOM与Gravity.RIGHT都是如此,据边界有一小段距离
         */
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = width/10*9; // 宽度
//        lp.height = height; // 高度
        lp.height=LayoutParams.WRAP_CONTENT;
        
        lp.alpha = 1.0f; // 透明�?
      
        
        /*
         * 将对话框的大小按屏幕大小的百分比设置
         */
//        WindowManager m = mContext.getWindowManager();
//        Display d = m.getDefaultDisplay(); // 获取屏幕宽�?高用
//        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数�?
//        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
//        p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.65
//        dialogWindow.setAttributes(p);
        
        
        dialogWindow.setAttributes(lp);
	}
	
	private int wheelViewIndex=0;
	String wheelViewCurentText;
	private void initWheelView()	{
		wheel.addScrollingListener(new OnWheelScrollListener() {
			
			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				wheelViewIndex=wheel.getCurrentItem();
				
			}
		});
	}
	

	OnClickListener mOnClickListener = new OnClickListener() {
		   public void onClick(View v) { 
		        switch (v.getId()) {
		        case R.id.btn_positive:
		        	
		        	wheelViewCurentText=data[wheelViewIndex];
		        	
		        	if(btnPosClick!=null)	{
		        		btnPosClick.onClick(data[wheelViewIndex],wheelViewIndex);
		        	}
		        	
		        	break;
		        case R.id.btn_negative:
		        	if(btnNegClick!=null)	{
		        		btnNegClick.onClick(data[wheelViewIndex],wheelViewIndex);
		        	}
		        	
		        	break;
		    }
		  } 
		};
		
	
		
	onWheelBtnPosClick btnPosClick;	
	public onWheelBtnPosClick getBtnPosClick() {
		return btnPosClick;
	}


	public void setBtnPosClick(onWheelBtnPosClick btnPosClick) {
		this.btnPosClick = btnPosClick;
	}
	public interface onWheelBtnPosClick{
		 void onClick(String text,int position);
	}
	onWheelBtnNegClick btnNegClick;
	public onWheelBtnNegClick getBtnNegClick() {
		return btnNegClick;
	}


	public void setBtnNegClick(onWheelBtnNegClick btnNegClick) {
		this.btnNegClick = btnNegClick;
	}
	public interface onWheelBtnNegClick{
		 void onClick(String text,int position);
	}
	public void setHint(int index) {
		// TODO Auto-generated method stub
		wheelViewIndex=index;
	}
	
}
