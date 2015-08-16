package com.eventer.app.view;


import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.widget.wheel.OnWheelScrollListener;
import com.eventer.app.widget.wheel.WheelView;


public class DialogView_ClassHour {

	
	
	private Context mContext;
	private Handler msgHandler;
	private LayoutInflater mInflater;
	
	private WheelView wheel;
	private Map<Integer,String[]> data ;
	
	public WheelView getWheel() {
		return wheel;
	}


	public void setWheel(WheelView wheel1,WheelView wheel2,WheelView wheel3,Map<Integer,String[]> data) {
		this.wheelView1 = wheel1;
		this.wheelView2 = wheel2;
		this.wheelView3 = wheel3;
		this.data=data;
	}


	public DialogView_ClassHour(Context mContext) {
		this.mContext = mContext;
		this.mInflater=LayoutInflater.from(mContext); 
	}

	private String positiveBtnStr="确定";
	private String negativeBtnStr="取消";
	private int layout_resource=0;

	
	 
	private TextView tv_title;
//	private TextView tv_message;
	public  Button btn_positive;
	public Button btn_negative;
	
	private WheelView wheelView1, wheelView2 ,wheelView3;
	
	
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





	public  Dialog initDialog(String title,String message) {
		View mView = null;
		if(layout_resource==0)	{
			mView=mInflater.inflate(R.layout.layout_wheel_classhour, null); 
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
		
		
		 wheelView1 = (WheelView) mView.findViewById(R.id.wheel01);
		 wheelView2 = (WheelView) mView.findViewById(R.id.wheel02);
		 wheelView3 = (WheelView) mView.findViewById(R.id.wheel03);
		 wheelView1.setVisibleItems(5);
		 wheelView2.setVisibleItems(5);
		 wheelView3.setVisibleItems(5);
		 wheelView1.addScrollingListener(new OnWheelScrollListener() {
				
				@Override
				public void onScrollingStarted(WheelView wheel) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onScrollingFinished(WheelView wheel) {
					// TODO Auto-generated method stub
					wheelViewIndex1=wheel.getCurrentItem();
					
				}
			});
		 wheelView2.addScrollingListener(new OnWheelScrollListener() {
				
				@Override
				public void onScrollingStarted(WheelView wheel) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onScrollingFinished(WheelView wheel) {
					// TODO Auto-generated method stub
					wheelViewIndex2=wheel.getCurrentItem();
					if(wheelViewIndex3<wheelViewIndex2){
						wheelView3.setCurrentItem(wheelViewIndex2, true);
					}
					
				}
			});
		 

		 wheelView3.addScrollingListener(new OnWheelScrollListener() {
				
				@Override
				public void onScrollingStarted(WheelView wheel) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onScrollingFinished(WheelView wheel) {
					// TODO Auto-generated method stub
					wheelViewIndex3=wheel.getCurrentItem();
					if(wheelViewIndex3<wheelViewIndex2){
						wheel.setCurrentItem(wheelViewIndex2, true);
					}
					
				}
			});
		 
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
	
	public void SetHint(int index1,int index2,int index3){
		wheelViewIndex1=index1;
		wheelViewIndex2=index2;
		wheelViewIndex3=index3;
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
  
        dialogWindow.setAttributes(lp);
	}
	
	private int wheelViewIndex1=0,wheelViewIndex2=0,wheelViewIndex3=0;
	private String wheelViewCurentText1,wheelViewCurentText2,wheelViewCurentText3;


	OnClickListener mOnClickListener = new OnClickListener() {
		
		   public void onClick(View v) { 
			   wheelViewCurentText1=data.get(1)[wheelViewIndex1];		        	
	        	wheelViewCurentText2=data.get(2)[wheelViewIndex2];
	        	wheelViewCurentText3=data.get(3)[wheelViewIndex3];
	        	Log.e("1", "237--dialogterm---"+wheelViewIndex1+wheelViewCurentText1+" "+wheelViewCurentText2);
	        	int[] index=new int[]{wheelViewIndex1,wheelViewIndex2,wheelViewIndex3};
	        	String[] text=new String[]{wheelViewCurentText1,wheelViewCurentText2,wheelViewCurentText3};
		        switch (v.getId()) {
		        case R.id.btn_positive:
		        	
		        	
		        	if(btnPosClick!=null)	{
		        		btnPosClick.onClick(text,index);
		        	}
		        	
		        	break;
		        case R.id.btn_negative:
		        	if(btnNegClick!=null)	{
		        		btnNegClick.onClick(data.get(1)[wheelViewIndex1],index);
		        	}
		        	
		        	break;
		    }
		  } 
		};
		
	
		
	onWheelBtnPosClick btnPosClick;	
	public onWheelBtnPosClick getBtnPosClick() {
		return btnPosClick;
	}


	public void setBtnPosClick(onWheelBtnPosClick onWheelBtnPosClick) {
		this.btnPosClick = onWheelBtnPosClick;
	}
	public interface onWheelBtnPosClick{
		public void onClick(String[] text,int position[]);
	}
	onWheelBtnNegClick btnNegClick;
	public onWheelBtnNegClick getBtnNegClick() {
		return btnNegClick;
	}


	public void setBtnNegClick(onWheelBtnNegClick btnNegClick) {
		this.btnNegClick = btnNegClick;
	}
	public interface onWheelBtnNegClick{
		public void onClick(String text,int position[]);
	}
	
}
