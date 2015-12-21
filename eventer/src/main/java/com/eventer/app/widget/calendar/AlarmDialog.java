package com.eventer.app.widget.calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.entity.Schedual;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
@SuppressLint("SetTextI18n")
@SuppressWarnings({"UnusedDeclaration"})
public class AlarmDialog extends Dialog {
    private Context context;
	public AlarmDialog(Context context) {
		super(context);
	}

	public AlarmDialog(Context context, int theme) {
		super(context, theme);
	}


	public static class Builder {
		private Context context;
		private String title;
		String message;
		private String positiveButtonText;
		private String negativeButtonText;
		public String MSGTITLE="title";
		public String MSGTIME="time";
		public String MSGTDETAIL="detail";
		private OnDetailListener mDetail;
		private View contentView;
		String msgTitle,msgTime,msgDetail;
		private Schedual schedual;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setSchedual(Schedual schedual) {
			this.schedual = schedual;
			return this;
		}


		/**
		 * Set the Dialog message from resource
		 *
		 * @param message title msg title
		 * @return Builder
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 *
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 *
		 */

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentInfo(Map<String,String> map){
			this.msgTitle = map.get(MSGTITLE);
			this.msgTime=map.get(MSGTIME);
			this.msgDetail=map.get(MSGTDETAIL);
			return this;
		}
		public Builder setContentView(View v) {
			this.contentView=v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 *
		 */
		public Builder setPositiveButton(int positiveButtonText,
										 DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
										 DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
										 DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
										 DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setOnDetailListen(OnDetailListener listener){
			this.mDetail = listener;
			return this;
		}

		public AlarmDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final AlarmDialog dialog = new AlarmDialog(context,R.style.Dialog);
			View layout = inflater.inflate(R.layout.calendar_alarm_dialog, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					(layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					( layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			// set the content message
			if (schedual != null) {
//				Map<String,String> map=new HashMap<>();
				String title=schedual.getTitle();
				String start=schedual.getStarttime();
				String end=schedual.getEndtime();
				final long id=schedual.getSchdeual_ID();

				if(title!=null&&title.trim().length() != 0){
					((TextView) layout.findViewById(R.id.messagetitle)).setText(title);
				}else{
					((TextView) layout.findViewById(R.id.messagetitle)).setText("无标题");
				}
				SimpleDateFormat  sDateFormat  = new   SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
				SimpleDateFormat  DateFormat  = new   SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
				//final String date=sDateFormat.format(new Date());	

				String startdate=start.substring(0, 10);
				String enddate=end.substring(0,10);
				String sDate=startdate;
				try {
					Date date = DateFormat.parse(startdate);
					sDate=sDateFormat.format(date);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(startdate.equals(enddate)){
					((TextView) layout.findViewById(R.id.messagetime)).setText(sDate+" "+end.substring(11));
				}else{
					startdate=startdate.replace('-', '/');
					enddate=enddate.replace('-', '/');
					((TextView) layout.findViewById(R.id.messagetime)).setText("开始:" + startdate + " " + start.substring(11) + "\r\n结束:" + enddate + " " + end.substring(11));
				}
				(layout.findViewById(R.id.messagedetail)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							dialog.dismiss();

							if (mDetail != null) {
								mDetail.onDetail();
							}
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

			} else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));
			}
			dialog.setContentView(layout);
			return dialog;
		}

	}

	public interface OnDetailListener {
		void onDetail();
	}

}
