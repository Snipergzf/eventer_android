package com.eventer.app.other;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.eventer.app.R;

public class moreselected extends FragmentActivity implements OnClickListener {
	private Button button01, button02, button03, button04,button05;
	private android.support.v4.app.FragmentManager fm;
	private android.support.v4.app.FragmentTransaction ft;

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moreselected);
		initView();

	}

	private void initView() {
		
		button01 = (Button) findViewById(R.id.activity_tab_1);
		button02 = (Button) findViewById(R.id.activity_tab_2);
		button03 = (Button) findViewById(R.id.activity_tab_3);
		button04 = (Button) findViewById(R.id.activity_tab_4);
		button05 = (Button) findViewById(R.id.activity_tab_5);

		button01.setBackgroundColor(Color.BLUE);
		button02.setBackgroundColor(Color.WHITE);
		button03.setBackgroundColor(Color.WHITE);
		button04.setBackgroundColor(Color.WHITE);
		button05.setBackgroundColor(Color.WHITE);
	
	     
		fm = getSupportFragmentManager();
		ft = getSupportFragmentManager().beginTransaction();
		/**
		 * 应用进入后，默认选择点击Fragment01
		 */
//		ft.replace(R.id.moreselectedPager, new Activity_commend());
//		ft.commit();

		button01.setOnClickListener(this);
		button02.setOnClickListener(this);
		button03.setOnClickListener(this);
		button04.setOnClickListener(this);
		button05.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		switch (v.getId()) {

//		case R.id.activity_tab_1:
//			button01.setBackgroundColor(Color.BLUE);
//			button02.setBackgroundColor(Color.WHITE);
//			button03.setBackgroundColor(Color.WHITE);
//			button04.setBackgroundColor(Color.WHITE);
//			button05.setBackgroundColor(Color.WHITE);
//
//			ft.replace(R.id.moreselectedPager, new Activity_commend());
//			break;
//
//		case R.id.activity_tab_2:
//			button01.setBackgroundColor(Color.WHITE);
//			button02.setBackgroundColor(Color.BLUE);
//			button03.setBackgroundColor(Color.WHITE);
//			button04.setBackgroundColor(Color.WHITE);
//			button05.setBackgroundColor(Color.WHITE);
//
//			ft.replace(R.id.moreselectedPager, new Activity_study());
//			break;
//
//		case R.id.activity_tab_3:
//			button01.setBackgroundColor(Color.WHITE);
//			button02.setBackgroundColor(Color.WHITE);
//			button03.setBackgroundColor(Color.BLUE);
//			button04.setBackgroundColor(Color.WHITE);
//			button05.setBackgroundColor(Color.WHITE);
//
//			ft.replace(R.id.moreselectedPager, new Activity_work());
//			break;
//
//		case R.id.activity_tab_4:
//			button01.setBackgroundColor(Color.WHITE);
//			button02.setBackgroundColor(Color.WHITE);
//			button03.setBackgroundColor(Color.WHITE);
//			button04.setBackgroundColor(Color.BLUE);
//			button05.setBackgroundColor(Color.WHITE);
//
//			ft.replace(R.id.moreselectedPager, new Activity_entertainment());
//			break;
//			
//		case R.id.activity_tab_5:
//			button01.setBackgroundColor(Color.WHITE);
//			button02.setBackgroundColor(Color.WHITE);
//			button03.setBackgroundColor(Color.WHITE);
//			button04.setBackgroundColor(Color.WHITE);
//			button05.setBackgroundColor(Color.BLUE);
//			
//
//			ft.replace(R.id.moreselectedPager, new Activity_life());
//			break;
			
			
			
			
			
		default:
			break;
		}
		//提交
		ft.commit();
	}

}
