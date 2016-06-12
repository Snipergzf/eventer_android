package com.eventer.app.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.other.AboutActivity;
import com.eventer.app.other.AssistFunctionActivity;
import com.eventer.app.other.BrowserHistoryActivity;
import com.eventer.app.other.CollectActivity;
import com.eventer.app.other.FeedbackActivity;
import com.eventer.app.other.MsgAlertActivity;
import com.eventer.app.other.MyUserInfoActivity;
import com.eventer.app.task.LoadImage;
import com.eventer.app.task.LoadImage.ImageDownloadedCallBack;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.util.PreferenceUtils;
import com.eventer.app.util.RandomUtil;
import com.eventer.app.view.MyToast;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.util.HashMap;
import java.util.Map;


public  class ProfileFragment extends Fragment implements OnClickListener {

	RelativeLayout re_info;
	RelativeLayout re_collect,re_history,re_msg_alert;
	RelativeLayout re_assist,re_version,re_about,re_feedback;
	private Context context;
	TextView tv_name;
	private ImageView iv_avatar;
	private View iv_version_alert;
	private LoadImage avatarLoader;
	public static int IS_EXIT=0xfff;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
		context=getActivity();
		avatarLoader = new LoadImage(getActivity(), Constant.IMAGE_PATH);
		initView(rootView);
		return rootView;
	}

	/***
	 * 初始化控件，给控件添加事件响应
	 */
	private void initView(View rootView) {
		tv_name=(TextView)rootView.findViewById(R.id.tv_name);
		iv_avatar=(ImageView)rootView.findViewById(R.id.iv_avatar);
		iv_version_alert=rootView.findViewById(R.id.iv_version_alert);
		re_info=(RelativeLayout)rootView.findViewById(R.id.re_myinfo);
		re_collect=(RelativeLayout)rootView.findViewById(R.id.re_collect);
		re_history=(RelativeLayout)rootView.findViewById(R.id.re_history);
		re_about=(RelativeLayout)rootView.findViewById(R.id.re_about);
		re_assist=(RelativeLayout)rootView.findViewById(R.id.re_assist);
		re_feedback=(RelativeLayout)rootView.findViewById(R.id.re_feedback);
		re_msg_alert=(RelativeLayout)rootView.findViewById(R.id.re_msg_alert);
		re_version=(RelativeLayout)rootView.findViewById(R.id.re_version_info);
		if( !"0".equals(Constant.UID) ){
			String name = LocalUserInfo.getInstance(context)
					.getUserInfo("nick");
			if(name != null){
				tv_name.setText(name);
			} else{
				initNick();
			}
			String avatar = LocalUserInfo.getInstance(context)
					.getUserInfo("avatar");
			showUserAvatar(iv_avatar, avatar);

		} else{
			tv_name.setText(R.string.not_login);
		}
		MyApplication.getInstance().setValueByKey("set_avatar", false);
		boolean isExistNewVersion=PreferenceUtils.getInstance().getVersionAlert();
		if( isExistNewVersion ){
			iv_version_alert.setVisibility(View.VISIBLE);
		}
		//设置监听器
		re_collect.setOnClickListener(this);
		re_info.setOnClickListener(this);
		re_history.setOnClickListener(this);
		re_about.setOnClickListener(this);
		re_assist.setOnClickListener(this);
		re_feedback.setOnClickListener(this);
		re_msg_alert.setOnClickListener(this);
		re_version.setOnClickListener(this);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 页面控件的点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.re_myinfo://我的信息
				if(!"0".equals(Constant.UID)){
					Intent intent=new Intent();
					intent.setClass(context, MyUserInfoActivity.class);
					getActivity().startActivityForResult(intent, IS_EXIT);
				}else{
					Intent intent=new Intent();
					intent.setClass(context, LoginActivity.class);
					PreferenceUtils.getInstance().setLoginPwd("");
					Constant.isLogin=false;
					Constant.isExist=true;
					getActivity().startActivity(intent);
					getActivity().finish();
				}

				break;
			case R.id.re_collect://我的收藏
				startActivity(new Intent()
						.setClass(context, CollectActivity.class));
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
									MyToast.makeText(context, "当前版本是最新版本！", Toast.LENGTH_SHORT).show();
									break;
							}
						}
					});
				}else{
					MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
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

	/**
	 * 初始化昵称
	 * 当用户昵称未设置时，为用户设置随机的昵称
	 */
	public void initNick() {
		final String newNick = RandomUtil.getRandomNick();
		Map<String, String> map = new HashMap<>();
		map.put("sex", "");
		map.put("uid", Constant.UID+"");
		map.put("token", Constant.TOKEN);
		map.put("name", newNick);
		map.put("email", "");
		map.put("grade","");
		map.put("school", "");
		map.put("major", "");
		map.put("class", "");
		map.put("user_rank", "0");
		LoadDataFromHTTP task = new LoadDataFromHTTP(
				context, Constant.URL_UPDATE_SELFINFO, map);

		task.getData(new com.eventer.app.http.LoadDataFromHTTP.DataCallBack() {

			@Override
			public void onDataCallBack(JSONObject data) {
				try {
					int code = data.getInteger("status");
					if (code == 0) {
						LocalUserInfo.getInstance(context)
								.setUserInfo("nick", newNick);
						tv_name.setText(newNick);
					} else {
						if(!Constant.isConnectNet){
							MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
						}
					}
				}  catch (Exception e) {
					e.printStackTrace();
				}

			}

		});
	}


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
	 * 获取头像url
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
					e.printStackTrace();
					return null;
				}
			}
			protected void onPostExecute(Map<String, Object> result) {
				if(result!=null){
					int status=(int)result.get("status");
					String info=(String)result.get("info");
					if(status==0){
						LocalUserInfo.getInstance(context)
								.setUserInfo("avatar", info);
						showUserAvatar(iv_avatar, info);
					}else {
						MyToast.makeText(context, "头像获取失败！", Toast.LENGTH_LONG)
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
	}


	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}


}
  		
     
