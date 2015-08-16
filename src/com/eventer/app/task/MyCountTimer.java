package com.eventer.app.task;

import android.os.CountDownTimer;
import android.widget.TextView;

public class MyCountTimer extends CountDownTimer {
		public static final int TIME_COUNT = 61000;//ʱ���ֹ��119s��ʼ��ʾ���Ե���ʱ120sΪ���ӣ�
		private TextView btn;
		private int normalColor, timingColor;//δ��ʱ��������ɫ����ʱ�ڼ��������ɫ
		 
		/**	 
		        * ���� btn           ����İ�ť(��ΪButton��TextView���࣬Ϊ��ͨ���ҵĲ�������ΪTextView��		 
		        * ���� normalColor   ����ʱ�����󣬰�ť��Ӧ��ʾ��������ɫ
		        * ����timingColor   ����ʱʱ����ɫ
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
		 
		// ��ʱ���ʱ����
		@Override
		public void onFinish() {
			btn.setTextColor(normalColor);	
			btn.setText("���·���");
			btn.setEnabled(true);
		}
		 
		// ��ʱ������ʾ
		@Override
		public void onTick(long millisUntilFinished) {
			btn.setTextColor(timingColor);
			btn.setEnabled(false);
			btn.setText("���·���("+millisUntilFinished / 1000 + "s)");
		}
}
