package com.eventer.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.easemob.util.DateUtils;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.entity.Comment;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.other.Activity_UserInfo;
import com.eventer.app.other.MyUserInfoActivity;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.util.LocalUserInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends BaseAdapter {

	private Context context;
	private List<Comment> commentlist;
	private LayoutInflater inflater;
	private LoadUserAvatar avatarLoader;

	public CommentAdapter(Context context, List<Comment> commentlist) {
		this.context = context;
		this.commentlist = commentlist;
		inflater = LayoutInflater.from(context);
		avatarLoader = new LoadUserAvatar(context, Constant.IMAGE_PATH);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return commentlist.size();
	}

	public void setData(List<Comment> commentlist){
		this.commentlist = commentlist;
	}

	@Override
	public Comment getItem(int position) {
		// TODO Auto-generated method stub
		return commentlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Comment comment=commentlist.get(position);

		if (convertView == null) {
			holder=new ViewHolder();
			convertView = inflater.inflate(R.layout.item_comment_single, new LinearLayout(context),false);
			holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}
		final View view=convertView;
		holder.iv_avatar.setImageResource(R.drawable.default_avatar);
		holder.tv_name.setText("匿名");
		holder.tv_content.setText(comment.getContent());
		holder.tv_time.setText(DateUtils.getTimestampString(new Date(comment.getTime()*1000)));
		final String speaker=comment.getSpeaker();
		if(speaker.equals(Constant.UID)){
			String nick=LocalUserInfo.getInstance(context).getUserInfo("nick");
			String avatar=LocalUserInfo.getInstance(context).getUserInfo("avatar");
			if(!TextUtils.isEmpty(nick)){
				holder.tv_name.setText(nick);
			}
			showUserAvatar(holder.iv_avatar, avatar);
		}else if(MyApplication.getInstance().getContactList().containsKey(speaker)){
			User u=MyApplication.getInstance().getContactList().get(speaker);
			String beizhu=u.getBeizhu();
			String nick=u.getNick();
			String avatar=u.getAvatar();
			if(!TextUtils.isEmpty(beizhu))
				holder.tv_name.setText(beizhu);
			else if(!TextUtils.isEmpty(nick))
				holder.tv_name.setText(nick);
			showUserAvatar(holder.iv_avatar, avatar);
		}else if(MyApplication.getInstance().getUserList().containsKey(speaker)){
			UserInfo u=MyApplication.getInstance().getUserList().get(speaker);
			String nick=u.getNick();
			String avatar=u.getAvatar();
			if(!TextUtils.isEmpty(nick))
				holder.tv_name.setText(nick);
			showUserAvatar(holder.iv_avatar, avatar);
		}else{
			Map<String,String> map=new HashMap<>();
			map.put("uid", speaker);
			LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_GET_USERINFO, map);
			task.getData(new DataCallBack() {
				@Override
				public void onDataCallBack(JSONObject data) {
					// TODO Auto-generated method stub
					try{
						int status=data.getInteger("status");
						switch (status) {
							case 0:
								JSONObject user_action=data.getJSONObject("user_action");
								JSONObject info=user_action.getJSONObject("info");
								String name=info.getString("name");
								String avatar=info.getString("avatar");
								Log.e("1", name+avatar);
								if(!TextUtils.isEmpty(name))
									((TextView) view.findViewById(R.id.tv_name)).setText(name);
								showUserAvatar(((ImageView) view.findViewById(R.id.iv_avatar)), avatar);
								UserInfo user=new UserInfo();
								user.setAvatar(avatar);
								user.setNick(name);
								user.setType(22);
								user.setUsername(speaker);
								MyApplication.getInstance().addUser(user);
								break;
							default:
//							Toast.makeText(context, "获取用户信息失败！", Toast.LENGTH_SHORT).show();
								Log.e("1", "获取用户信息失败：");
								break;
						}
					}catch(Exception e){
						e.printStackTrace();
					}

				}
			});
		}
		holder.iv_avatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(! speaker.equals(Constant.UID))
					context.startActivity(new Intent().setClass(context, Activity_UserInfo.class)
							.putExtra("user", speaker));
				else
					context.startActivity(new Intent().setClass(context, MyUserInfoActivity.class));
			}
		});
		return view;
	}

	private static class ViewHolder {

		TextView tv_name;
		ImageView iv_avatar;
		TextView tv_time;
		TextView tv_content;

	}

	private void showUserAvatar(ImageView iamgeView, String avatar) {
		if(avatar==null||avatar.equals("")||avatar.equals("default")) return;
		final String url_avatar =avatar;
		iamgeView.setTag(url_avatar);

		Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
				new ImageDownloadedCallBack() {

					@Override
					public void onImageDownloaded(ImageView imageView,
												  Bitmap bitmap,int status) {
						if(status==-1){
							if (imageView.getTag() == url_avatar) {
								imageView.setImageBitmap(bitmap);
							}
						}
					}
				});
		if (bitmap != null)
			iamgeView.setImageBitmap(bitmap);

	}

}
