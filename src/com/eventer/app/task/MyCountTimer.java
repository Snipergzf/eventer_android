package com.eventer.app.task;

import android.os.CountDownTimer;
import android.widget.TextView;

public class MyCountTimer extends CountDownTimer {
		public static final int TIME_COUNT = 61000;//时间防止从119s开始显示（以倒计时120s为例子）
		private TextView btn;
		private int normalColor, timingColor;//未计时的文字颜色，计时期间的文字颜色
		 
		/**	 
		        * 参数 btn           点击的按钮(因为Button是TextView子类，为了通用我的参数设置为TextView）		 
		        * 参数 normalColor   倒计时结束后，按钮对应显示的文字颜色
		        * 参数timingColor   倒计时时的颜色
		*/
		
		
		 
		public MyCountTimer (TextView btn) {
			super(TIME_COUNT, 1000);
			this.btn = btn;
		}
		 
		 
		public MyCountTimer (TextView tv_varify, int normalColor, int timingColor) {
			this(tv_varify);
			this.normalColor = normalColor;
			this.timingColor = timingColor;
		}
		 
		// 计时完毕时触发
		@Override
		public void onFinish() {
			btn.setTextColor(normalColor);	
			btn.setText("重新发送");
			btn.setEnabled(true);
		}
		 
		// 计时过程显示
		@Override
		public void onTick(long millisUntilFinished) {
			btn.setTextColor(timingColor);
			btn.setEnabled(false);
			btn.setText("重新发送("+millisUntilFinished / 1000 + "s)");
		}
}
