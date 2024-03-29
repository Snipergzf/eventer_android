package com.eventer.app.receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;


/**
 * 短信监听
 *
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {

	private static MessageListener mMessageListener;
	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

	public SMSBroadcastReceiver() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			if(pdus!=null){
				for(Object pdu:pdus) {
					SmsMessage smsMessage = SmsMessage.createFromPdu((byte [])pdu);
					String sender = smsMessage.getDisplayOriginatingAddress();
					//短信内容
					String content = smsMessage.getDisplayMessageBody();

					//过滤不需要读取的短信的发送号码
					if ("106571207117008".equals(sender)||("10657120610111").equals(sender)) {
						mMessageListener.onReceived(content);
						abortBroadcast();
					}
				}
			}

		}

	}

	//回调接口
	public interface MessageListener {
		 void onReceived(String message);
	}

	public void setOnReceivedMessageListener(MessageListener messageListener) {
		this.mMessageListener = messageListener;
	}
}