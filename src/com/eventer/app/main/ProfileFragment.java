package com.eventer.app.main;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.other.AboutActivity;
import com.eventer.app.other.AssistFunctionActivity;
import com.eventer.app.other.BrowserHistoryActivity;
import com.eventer.app.other.CollectActivity;
import com.eventer.app.other.FeedbackActivity;
import com.eventer.app.other.MsgAlertActivity;
import com.eventer.app.other.MyUserInfoActivity;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.util.LocalUserInfo;


public  class ProfileFragment extends Fragment implements OnClickListener {
   
	private RelativeLayout re_myinfo;
	private RelativeLayout re_collect,re_history,re_msg_alert;
	private RelativeLayout re_assist,re_version,re_about,re_feedback;
	private Context context;
	private TextView tv_name;
	private ImageView iv_avatar;
	private LoadUserAvatar avatarLoader;
	public static int IS_EXIT=0xfff; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
		context=getActivity();
		avatarLoader = new LoadUserAvatar(getActivity(), Constant.IMAGE_PATH);
		initView(rootView);		
		return rootView;
	}

	private void initView(View rootView) {
		// TODO Auto-generated method stub
		tv_name=(TextView)rootView.findViewById(R.id.tv_name);
		iv_avatar=(ImageView)rootView.findViewById(R.id.iv_avatar);
		re_myinfo=(RelativeLayout)rootView.findViewById(R.id.re_myinfo);
		re_collect=(RelativeLayout)rootView.findViewById(R.id.re_collect);
		re_history=(RelativeLayout)rootView.findViewById(R.id.re_history);
		re_about=(RelativeLayout)rootView.findViewById(R.id.re_about);
		re_assist=(RelativeLayout)rootView.findViewById(R.id.re_assist);
		re_feedback=(RelativeLayout)rootView.findViewById(R.id.re_feedback);
		re_msg_alert=(RelativeLayout)rootView.findViewById(R.id.re_msg_alert);
		re_version=(RelativeLayout)rootView.findViewById(R.id.re_version_info);
		String name = LocalUserInfo.getInstance(context)
                .getUserInfo("nick");
		if(name!=null){
			tv_name.setText(name);
		}
	    
		//���ü�����
		re_collect.setOnClickListener(this);
		re_myinfo.setOnClickListener(this);
		re_history.setOnClickListener(this);
		re_about.setOnClickListener(this);
		re_assist.setOnClickListener(this);
		re_feedback.setOnClickListener(this);
		re_msg_alert.setOnClickListener(this);
		re_version.setOnClickListener(this);
		String avatar = LocalUserInfo.getInstance(context)
                .getUserInfo("avatar");
		showUserAvatar(iv_avatar, avatar);
		MyApplication.getInstance().setValueByKey("set_avatar", false);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * ҳ��ؼ��ĵ���¼�
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.re_myinfo://�ҵ���Ϣ
			Intent intent=new Intent();
			intent.setClass(context, MyUserInfoActivity.class);
			getActivity().startActivityForResult(intent, IS_EXIT);
			break;
		case R.id.re_collect://�ҵ��ղ�
			startActivity(new Intent().setClass(context, CollectActivity.class));
			break;
		case R.id.re_history://�����ʷ
			startActivity(new Intent().setClass(context, BrowserHistoryActivity.class));
			break;
		case R.id.re_about://��������
			startActivity(new Intent().setClass(context, AboutActivity.class));
			break;
		case R.id.re_assist://��������
			startActivity(new Intent().setClass(context, AssistFunctionActivity.class));
			break;
		case R.id.re_version_info://������
		    checkVersion();	
			break;
		case R.id.re_feedback://�������
			startActivity(new Intent().setClass(context, FeedbackActivity.class));
			break;
		case R.id.re_msg_alert://��Ϣ����
			startActivity(new Intent().setClass(context, MsgAlertActivity.class));
			break;
		default:
			break;
		}
	}
	
	 private void checkVersion() {
		// TODO Auto-generated method stub
		 PackageManager pm = context.getPackageManager();//contextΪ��ǰActivity������ 
		 PackageInfo pi;
		 String version="";
		 int versionCode=0;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		    version = pi.versionName;
		    versionCode=pi.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
   /***
    * ��ʾͷ��
    * @param iamgeView  ͷ����ʾ�Ŀؼ�
    * @param avatar  ͷ���ַ
    */
	private void showUserAvatar(final ImageView iamgeView, String avatar) {
	        final String url_avatar = avatar;
	        iamgeView.setTag(url_avatar);
	        
	        if (url_avatar != null && !url_avatar.equals("")) {
	            Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
	                    new ImageDownloadedCallBack() {

	                        @Override
	                        public void onImageDownloaded(ImageView imageView,
	                                Bitmap bitmap,int status) {
	                        	if(status==-1){
	                        		if (imageView.getTag() == url_avatar) {
		                                imageView.setImageBitmap(bitmap);
		                            }
	                        	}else{
	                        	   LocalUserInfo.getInstance(context).setUserInfo("avatar", null);
	                           }
	                            
	                        }

	                    });
	            if (bitmap != null)
	                iamgeView.setImageBitmap(bitmap);

	        }else if(avatar.equals("default")){
	        	 iamgeView.setBackgroundResource(R.drawable.default_avatar);
	        }else{
	        	Map<String, String> map = new HashMap<String, String>();           
	            map.put("uid", Constant.UID+"");
	            GetAvatar(map);
	        }
  	    }
	/***
	 * ��ȡͷ�� 
	 * @param params
	 */
	 public void GetAvatar(final Object... params) {
			new AsyncTask<Object, Object,Map<String, Object>>() {
				@SuppressWarnings("unchecked")
				@Override
				protected Map<String, Object> doInBackground(Object... params) {
					Map<String, Object> status=new HashMap<String, Object>();
				  try {
			    	        status=HttpUnit.sendGetAvatarRequest((Map<String, String>) params[0]);
			    	        return status;					
					} catch (Throwable e) {
						Log.e("1", e.toString());
						return null;
					}
				}
				protected void onPostExecute(Map<String, Object> result) {
					 if(result!=null){
						 int status=(int)result.get("status");
						 String info=(String)result.get("info");
						 if(status==0){
					        	Log.e("1", "��ȡͷ���ַ�ɹ���");
		                        LocalUserInfo.getInstance(context)
	                          .setUserInfo("avatar", info);
					        	showUserAvatar(iv_avatar, info);		        					        	
					        }else {
					        	Toast.makeText(context, "ͷ���ȡʧ�ܣ�", Toast.LENGTH_LONG)
								.show();					        	
					        }
					  }	 				
				    };
				    
			}.execute(params);}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if((boolean) MyApplication.getInstance().getValueByKey("set_avatar")){
			String avatar = LocalUserInfo.getInstance(context)
	                .getUserInfo("avatar");
			showUserAvatar(iv_avatar, avatar);
			MyApplication.getInstance().setValueByKey("set_avatar", false);
		}
	}
	    
       	    

	 
}
  		
     
