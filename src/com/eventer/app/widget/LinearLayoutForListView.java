package com.eventer.app.widget;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.eventer.app.adapter.CourseTimeAdapter;

/**
* 取代ListView的LinearLayout，使之能够成功嵌套在ScrollView中
*/
public class LinearLayoutForListView extends LinearLayout {

    private CourseTimeAdapter adapter;
    private OnClickListener onClickListener = null;

    /**
     * 绑定布局
     */
    public void bindLinearLayout() {
        int count = adapter.getCount();
        this.removeAllViews();
        for (int i = 0; i < count; i++) {
            View v = adapter.getView(i, null, null);

            v.setOnClickListener(this.onClickListener);
            addView(v, i);
        }
       Log.v("countTAG", "" + count);
    }
    
    public void setAdapter(CourseTimeAdapter adapter){
    	this.adapter=adapter;
    }

    public LinearLayoutForListView(Context context) {
        super(context);}
    }