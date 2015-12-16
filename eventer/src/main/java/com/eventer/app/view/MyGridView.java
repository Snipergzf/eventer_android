package com.eventer.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class MyGridView extends GridView{

	public MyGridView(Context context) {
		super(context);
	}
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	//通过重新dispatchTouchEvent方法来禁止滑动
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return ev.getAction() == MotionEvent.ACTION_MOVE||super.dispatchTouchEvent(ev);
	}

}
