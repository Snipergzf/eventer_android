package com.eventer.app.view.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Customize the weekday gridview
 */
@SuppressLint("ResourceAsColor")
public class WeekdayArrayAdapter extends ArrayAdapter<String> {
	public static int textColor = Color.LTGRAY;
	Context context;

	public WeekdayArrayAdapter(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);
		this.context=context;
	}

	// To prevent cell highlighted when clicked
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	// Set color to gray and text size to 12sp
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// To customize text size and color
		TextView textView = (TextView) super.getView(position, convertView,
				parent);
		// Set content
		String item = getItem(position);

		// Show smaller text if the size of the text is 4 or more in some
		// locale
		if (item.length() <= 3) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		} else {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
		}
		if(position>0&&position<6)
			textView.setTextColor(Color.DKGRAY);
		else
			textView.setTextColor(Color.parseColor("#ffF89012"));	
		textView.setGravity(Gravity.TOP);
		return textView;
	}
//	private int dip2px(float dpValue) {  
//        final float scale = context.getResources().getDisplayMetrics().density;  
//        return (int) (dpValue * scale + 0.5f);  
//    } 

}
