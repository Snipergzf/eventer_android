package com.eventer.app.main;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.other.BrowserHistoryActivity;
import com.eventer.app.other.CollectActivity;
import com.eventer.app.other.MyUserInfoActivity;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.util.PreferenceUtils;


public  class ProfileFragment extends Fragment implements OnClickListener {
   
	private Button btn_exit;
	private RelativeLayout re_myinfo;
	private TextView clear_message,tv_collect,tv_history;
	private Context context;
	private TextView tv_name;
	private ImageView iv_avatar;
	private LoadUserAvatar avatarLoader;
	
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
		btn_exit=(Button)rootView.findViewById(R.id.btn_exit);
		clear_message=(TextView)rootView.findViewById(R.id.clear_massage);
		tv_name=(TextView)rootView.findViewById(R.id.tv_name);
		iv_avatar=(ImageView)rootView.findViewById(R.id.iv_avatar);
		re_myinfo=(RelativeLayout)rootView.findViewById(R.id.re_myinfo);
		tv_collect=(TextView)rootView.findViewById(R.id.tv_collect);
		tv_history=(TextView)rootView.findViewById(R.id.tv_history);
		String name = LocalUserInfo.getInstance(context)
                .getUserInfo("nick");
		if(name!=null){
			tv_name.setText(name);
		}
		
		
		btn_exit.setOnClickListener(this);
		tv_collect.setOnClickListener(this);
		clear_message.setOnClickListener(this);
		re_myinfo.setOnClickListener(this);
		tv_history.setOnClickListener(this);
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

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_exit:
			PreferenceUtils.getInstance().setLoginPwd("");
			System.exit(0);
			break;
		case R.id.clear_massage:
			ChatEntityDao dao=new ChatEntityDao(context);
			if(dao.deleteAllMsg()){
				Toast.makeText(context, "已经清空所有数据", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(context, "操作失败！", Toast.LENGTH_LONG).show();
			}
			
			break;
		case R.id.re_myinfo:
			Intent intent=new Intent();
			intent.setClass(context, MyUserInfoActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_collect:
			startActivity(new Intent().setClass(context, CollectActivity.class));
			break;
		case R.id.tv_history:
			startActivity(new Intent().setClass(context, BrowserHistoryActivity.class));
			break;
		default:
			break;
		}
	}
	
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
					        	Log.e("1", "获取头像地址成功！");
		                        LocalUserInfo.getInstance(context)
	                          .setUserInfo("avatar", info);
					        	showUserAvatar(iv_avatar, info);		        					        	
					        }else {
					        	Toast.makeText(context, "头像获取失败！", Toast.LENGTH_LONG)
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
  		
     
