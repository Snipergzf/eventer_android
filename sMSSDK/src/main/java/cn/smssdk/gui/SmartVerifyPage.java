/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2014年 mob.com. All rights reserved.
 */
package cn.smssdk.gui;

import android.app.Dialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mob.tools.FakeActivity;

import java.util.HashMap;

import cn.smssdk.gui.layout.BackVerifyDialogLayout;
import cn.smssdk.gui.layout.IdentifyNumPageLayout;
import cn.smssdk.gui.layout.Res;

import static com.mob.tools.utils.R.getStringRes;
import static com.mob.tools.utils.R.getStyleRes;

/** 智能验证码页面*/
@SuppressWarnings({"UnusedDeclaration"})
public class SmartVerifyPage extends FakeActivity implements OnClickListener {
	private static final int RETRY_INTERVAL = 60;

	private String phone;
	private String code;
	private int time = RETRY_INTERVAL;

	private boolean showSmart = false;

	public void onCreate() {
		IdentifyNumPageLayout page = new IdentifyNumPageLayout(activity);
		LinearLayout layout = page.getLayout();

		if (layout != null) {
			activity.setContentView(layout);
			countDown();
		}

	}

	/** 倒数计时 */
	private void countDown() {
		runOnUIThread(new Runnable() {
			public void run() {
				time--;
				if (time == RETRY_INTERVAL - 2) {



					showSmart = true;
					time = RETRY_INTERVAL;
				} else {

					runOnUIThread(this, 1000);
				}
			}
		}, 1000);
	}

	public void onClick(View v) {
		int id = v.getId();
		int id_ll_back = Res.id.ll_back;
		int id_btn_submit = Res.id.btn_submit;


		if (id == id_ll_back) {
			if(showSmart) {
				finish();
				return;
			}
			runOnUIThread(new Runnable() {
				public void run() {
					showNotifyDialog();
				}
			});
		} else if (id == id_btn_submit) {
			// 提交验证码
			HashMap<String, Object> resp = new HashMap<String, Object>();
			resp.put("country", code);
			resp.put("phone", phone);
			afterSubmit(resp);

		}
	}


	/**
	 * 提交验证码成功后的执行事件
	 *
	 */
	private void afterSubmit(final Object data) {
		runOnUIThread(new Runnable() {
			public void run() {


				HashMap<String, Object> res = new HashMap<String, Object>();
				res.put("res", true);
				res.put("page", 2);
				res.put("phone", data);
				setResult(res);
				finish();

			}
		});
	}

	/** 按返回键时，弹出的提示对话框 */
	private void showNotifyDialog() {
		int resId = getStyleRes(activity, "CommonDialog");
		if (resId > 0) {
			final Dialog dialog = new Dialog(getContext(), resId);

			LinearLayout layout = BackVerifyDialogLayout.create(activity);

			if (layout != null) {
				dialog.setContentView(layout);

				resId = Res.id.tv_dialog_hint;
				TextView tv = (TextView) dialog.findViewById(resId);
				resId = getStringRes(activity,
						"smssdk_close_identify_page_dialog");
				if (resId > 0) {
					tv.setText(resId);
				}
				resId = Res.id.btn_dialog_ok;
				Button waitBtn = (Button) dialog.findViewById(resId);
				resId = getStringRes(activity, "smssdk_wait");
				if (resId > 0) {
					waitBtn.setText(resId);
				}
				waitBtn.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				resId = Res.id.btn_dialog_cancel;
				Button backBtn = (Button) dialog.findViewById(resId);
				resId = getStringRes(activity, "smssdk_back");
				if (resId > 0) {
					backBtn.setText(resId);
				}
				backBtn.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
						finish();
					}
				});
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
		}
	}

	@Override
	public boolean onKeyEvent(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
			return true;
		} else {
			return false;
		}
	}

}
