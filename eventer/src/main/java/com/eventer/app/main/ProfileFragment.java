package com.eventer.app.main;

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
import com.eventer.app.util.PreferenceUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.util.HashMap;
import java.util.Map;


public  class ProfileFragment extends Fragment implements OnClickListener {

	RelativeLayout re_myinfo;
	RelativeLayout re_collect,re_history,re_msg_alert;
	RelativeLayout re_assist,re_version,re_about,re_feedback,re_course;
	private Context context;
	TextView tv_name;
	private ImageView iv_avatar;
	private View iv_version_alert;
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
		iv_version_alert=rootView.findViewById(R.id.iv_version_alert);
		re_myinfo=(RelativeLayout)rootView.findViewById(R.id.re_myinfo);
		re_collect=(RelativeLayout)rootView.findViewById(R.id.re_collect);
		re_course=(RelativeLayout) rootView.findViewById(R.id.re_course);
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

		boolean isExistNewVersion=PreferenceUtils.getInstance().getVersionAlert();
		if(isExistNewVersion){
			iv_version_alert.setVisibility(View.VISIBLE);
		}
		//设置监听器
		re_collect.setOnClickListener(this);
		re_course.setOnClickListener(this);
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
	 * 页面控件的点击事件
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.re_myinfo://我的信息
				Intent intent=new Intent();
				intent.setClass(context, MyUserInfoActivity.class);
				getActivity().startActivityForResult(intent, IS_EXIT);
				break;
			case R.id.re_collect://我的收藏
				startActivity(new Intent().setClass(context, CollectActivity.class));
				break;
			case R.id.re_course:
				break;
			case R.id.re_history://浏览历史
				startActivity(new Intent().setClass(context, BrowserHistoryActivity.class));
				break;
			case R.id.re_about://关于我们
				startActivity(new Intent().setClass(context, AboutActivity.class));
				break;
			case R.id.re_assist://辅助功能
				startActivity(new Intent().setClass(context, AssistFunctionActivity.class));
				break;
			case R.id.re_version_info://检查更新
				if(Constant.isConnectNet){
					PreferenceUtils.getInstance().setVersionAlert(false);
					iv_version_alert.setVisibility(View.GONE);
					UmengUpdateAgent.setDefault();
					UmengUpdateAgent.setSlotId("54357");
					UmengUpdateAgent.forceUpdate(context);
					UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
						@Override
						public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
							switch (updateStatus) {
								// 有更新
								case UpdateStatus.Yes:
									break;
								// 没有更新
								default:
									Toast.makeText(context, "当前 版本是最新版本！", Toast.LENGTH_SHORT).show();
									break;
							}
						}
					});
				}else{
					Toast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
				}

				break;
			case R.id.re_feedback://意见反馈
				startActivity(new Intent().setClass(context, FeedbackActivity.class));
				break;
			case R.id.re_msg_alert://消息提醒
				startActivity(new Intent().setClass(context, MsgAlertActivity.class));
				break;
			default:
				break;
		}
	}

//	 private void checkVersion() {
//		// TODO Auto-generated method stub
//		 PackageManager pm = context.getPackageManager();//context为当前Activity上下文 
//		 PackageInfo pi;
//		 String version="";
//		 int versionCode=0;
//		try {
//			pi = pm.getPackageInfo(context.getPackageName(), 0);
//		    version = pi.versionName;
//		    versionCode=pi.versionCode;
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	/***
	 * 显示头像
	 * @param iamgeView  头像显示的控件
	 * @param avatar  头像地址
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
			Map<String, String> map = new HashMap<>();
			map.put("uid", Constant.UID+"");
			GetAvatar(map);
		}
	}
	/***
	 * 获取头像
	 */
	public void GetAvatar(final Object... params) {
		new AsyncTask<Object, Object,Map<String, Object>>() {
			@SuppressWarnings("unchecked")
			@Override
			protected Map<String, Object> doInBackground(Object... params) {
				Map<String, Object> status;
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
			}

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
		MobclickAgent.onPageStart("MainScreen"); //统计页面
		Map<String, String> map_value = new HashMap<>();
		map_value.put("type", "popular");
		map_value.put("artist", "JJLin");

		MobclickAgent.onEventValue(context, "collect", map_value, 12000);
	}


	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}


}
  		
     
