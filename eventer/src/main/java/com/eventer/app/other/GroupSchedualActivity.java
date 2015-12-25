package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.widget.CircleImageView;
import com.eventer.app.widget.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
@SuppressLint("SetTextI18n")
public class GroupSchedualActivity extends SwipeBackActivity {

	ListView listview;
	private List<Map<String,String>> mData=new ArrayList<>();
	MyAadpter adapter;
	private Context context;
	private LoadUserAvatar avatarLoader;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_share);
		context=this;
		avatarLoader = new LoadUserAvatar(context, Constant.IMAGE_PATH);
		setBaseTitle(R.string.groupshare);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		listview=(ListView)findViewById(R.id.listview);
		final String groupId=getIntent().getStringExtra("groupId");
		ChatEntityDao dao=new ChatEntityDao(context);
		mData=dao.getShareScheduals(groupId);
		adapter=new MyAadpter(context);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				Map<String,String> map=mData.get(position);
				String share=map.get("shareId");
				context.startActivity(new Intent().setClass(context,ShareSchedualActivity.class)
						.putExtra("groupId", groupId)
						.putExtra("shareId", share));
			}
		});

	}

	public class MyAadpter extends BaseAdapter{
		private LayoutInflater mInflater;

		public MyAadpter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			Map<String,String> map=mData.get(position);
			if (convertView == null) {
				holder=new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_group_schedule, parent, false);
				holder.title = (TextView)convertView.findViewById(R.id.tv_title);
				holder.info=(TextView)convertView.findViewById(R.id.tv_info);
				holder.nick=(TextView)convertView.findViewById(R.id.tv_nick);
				holder.avatar=(CircleImageView)convertView.findViewById(R.id.iv_avatar);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			String content=map.get("content");
			final String publisher;
			int type=0;
			String title="",time="",place="",info="";
			int loc=content.indexOf("\n");
			if(loc!=-1){
				content=content.substring(loc+1);
				publisher=content.substring(0,loc);
			}else{
				publisher=Constant.UID;
			}
			Log.e("1",content);
			try{
				JSONObject json=JSONObject.parseObject(content);
				title=json.getString("schedule_title");
				type=json.getInteger("schedule_type");
				time=json.getString("schedule_end");
				place=json.getString("schedule_place");

			}catch(Exception e){
                e.printStackTrace();
			}
			Log.e("1",type+"-----"+title);
			if(TextUtils.isEmpty(title)){
				title="（无标题）";
			}
			if(!TextUtils.isEmpty(time)){
				time=formatDisplayTime(time, "yyyy-MM-dd HH:mm");
				info=time;
				if(!TextUtils.isEmpty(place)){
					info+=", "+place;
				}
			}else if(!TextUtils.isEmpty(place)){
				info=place;
			}

			switch (type) {
				case 2:
					title="[日程]"+title;
					break;
				case 3:
					title="[待办]"+title;
					break;
				default:
					break;
			}

			holder.title.setText(title);
			holder.info.setText(info);
            final View view=convertView;
			if(publisher.equals(Constant.UID)){
				String nick=LocalUserInfo.getInstance(context).getUserInfo("nick");
				String avatar=LocalUserInfo.getInstance(context).getUserInfo("avatar");
				holder.nick.setText(nick);
				showUserAvatar(holder.avatar,avatar);
			}else if(MyApplication.getInstance().getContactList().containsKey(publisher)){
				User u=MyApplication.getInstance().getContactList().get(publisher);
				String nick=u.getNick();
				String avatar=u.getAvatar();
				String beizhu=u.getBeizhu();
				if(!TextUtils.isEmpty(beizhu)){
					nick=beizhu;}
				holder.nick.setText(nick);
				showUserAvatar(holder.avatar, avatar);

			}else if(MyApplication.getInstance().getUserList().containsKey(publisher)) {
				UserInfo u = MyApplication.getInstance().getUserList().get(publisher);
				String nick = u.getNick();
				String avatar=u.getAvatar();
				holder.nick.setText(nick);
				showUserAvatar(holder.avatar, avatar);
			}else{
				Map<String,String> params=new HashMap<>();
				params.put("uid", publisher);
				LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_GET_USERINFO, params);
				task.getData(new LoadDataFromHTTP.DataCallBack() {
					@Override
					public void onDataCallBack(JSONObject data) {
						// TODO Auto-generated method stub
						try {
							int status = data.getInteger("status");
							switch (status) {
								case 0:
									JSONObject user_action = data.getJSONObject("user_action");
									JSONObject info = user_action.getJSONObject("info");
									String name = info.getString("name");
									String avatar = info.getString("avatar");
									showUserAvatar((ImageView) view.findViewById(R.id.iv_avatar), avatar);
									UserInfo user = new UserInfo();
									user.setAvatar(avatar);
									user.setNick(name);
									user.setType(22);
									user.setUsername(publisher);
									MyApplication.getInstance().addUser(user);
									break;
								default:
									Log.e("1", "获取用户信息失败：");
									break;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}

			return convertView;
		}
		//提取出来方便点
		public final class ViewHolder {
			TextView title;
			TextView info;
			TextView nick;
			CircleImageView avatar;
		}

	}
	/**
	 *
	 * @param time    需要格式化的时间 如"2014-07-14 19:01:45"
	 * @param pattern 输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
	 *                <p/>如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
	 * @return time为null，输出空字符"";或者时间格式不匹配，输出time
	 */
	public static String formatDisplayTime(String time, String pattern) {
		String display = "";

		if (time != null) {
			try {
				int thisyear= Calendar.getInstance().get(Calendar.YEAR);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
				ParsePosition pos = new ParsePosition(0);
				Date date = formatter.parse(time, pos);
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				int year = c.get(Calendar.YEAR);
				Date tDate=new SimpleDateFormat(pattern,Locale.getDefault()).parse(time);
				if(thisyear==year){
					SimpleDateFormat halfDf = new SimpleDateFormat("MM-dd HH:mm",Locale.getDefault());
					display = halfDf.format(tDate);
				}else{
					display = formatter.format(tDate);
				}
			} catch (Exception e) {
				e.printStackTrace();
				display=time;
			}
		}
		return display;
	}
	private void showUserAvatar(ImageView iamgeView, String avatar) {
		if(avatar==null||avatar.equals("")||avatar.equals("default")) return;
		final String url_avatar = avatar;
		iamgeView.setTag(url_avatar);

		Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
				new LoadUserAvatar.ImageDownloadedCallBack() {

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

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
