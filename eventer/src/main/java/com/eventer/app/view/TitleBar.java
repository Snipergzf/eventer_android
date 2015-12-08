package com.eventer.app.view;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.eventer.app.R;

public class TitleBar {
	private static Activity mActivity;

	/**
	 * @see [自定义标题栏]
	 * @param activity
	 * @param title
	 */
	public static void setTitleBar(Activity activity,String title) {
		mActivity = activity;
		activity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		activity.setContentView(R.layout.titlebar);
		activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);
		TextView textView = (TextView) activity.findViewById(R.id.title);
		textView.setText(title);
		ImageView titleBackBtn = (ImageView) activity.findViewById(R.id.back);
		titleBackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				mActivity.finish();
			}
		});
	}
}
