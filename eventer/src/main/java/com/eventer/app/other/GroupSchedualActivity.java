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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.entity.Schedual;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.http.HttpParamUnit;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hirondelle.date4j.DateTime;

@SuppressLint("SetTextI18n")
public class GroupSchedualActivity extends SwipeBackActivity {

	ListView listview;
	private List<String> shareList=new ArrayList<>();
	private List<Schedual> mData = new ArrayList<>();
	MyAadpter adapter;
	private String groupId;
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
		groupId=getIntent().getStringExtra("groupId");
		ChatEntityDao dao=new ChatEntityDao(context);
		shareList=dao.getShareIdList(groupId);
		adapter=new MyAadpter(context);
		listview.setAdapter(adapter);
		getSchedualList(shareList);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub

				Schedual schedual = mData.get(position);
				String share = schedual.getShareId();
				context.startActivity(new Intent().setClass(context, ShareSchedualActivity.class)
						.putExtra("groupId", groupId)
						.putExtra("shareId", share));
			}
		});

	}

	private void getSchedualList(List<String> shareList) {
		for(String shareId: shareList){
			SchedualDao dao=new SchedualDao(context);
			Schedual schedual = dao.getSchedualByShare(shareId);
			if(schedual!=null){
				mData.add(schedual);
				Collections.sort(mData, new ScheduleComparator(){});
				adapter.notifyDataSetChanged();
			} else{
				checkGroupSchedual(shareId);
			}
		}

	}

	/***
	 * 通过拼音对用户进行排序
	 * @author LiuNana
	 *
	 */
	@SuppressLint("DefaultLocale")
	public class ScheduleComparator implements Comparator<Schedual> {

		@SuppressLint("DefaultLocale")
		@Override
		public int compare(Schedual o1, Schedual o2) {
			// TODO Auto-generated method stub
			String py1 = o1.getStarttime();
			String py2 = o2.getStarttime();
			if (isEmpty(py1) && isEmpty(py2))
				return 0;
			if (isEmpty(py1))
				return 1;
			if (isEmpty(py2))
				return -1;
			return py2.compareTo(py1);
		}

		private boolean isEmpty(String str) {
			return "".equals(str.trim());
		}
	}

	private void checkGroupSchedual(String shareId) {
		Map<String, String> map= HttpParamUnit.activityParam(shareId);
		LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_ACTIVITY_CHECK, map);
		task.getData(new LoadDataFromHTTP.DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				try {
					Schedual schedual = new Schedual();
					int status = data.getInteger("status");
					if (status == 0) {
						JSONObject action = data.getJSONObject("web_action");
						JSONObject activity = action.getJSONObject("activity");
						schedual.setTitle(activity.getString("a_name"));
						schedual.setPlace(activity.getString("a_place"));
						schedual.setShareId(activity.getString("a_id"));
						schedual.setDetail(activity.getString("a_desc"));
						schedual.setFrequency(Integer.parseInt(activity.getString("a_frequency")));
						schedual.setType(Integer.parseInt(activity.getString("a_type")));
						schedual.setSharer(activity.getString("uid"));
						String time = activity.getString("a_time");
						schedual.setStarttime(time);
						schedual.setEndtime(time);
						schedual.setRemind(1);
						schedual.setRemindtime(getRemindTime(time, 1));
						String participants = activity.getString("participants");
						JSONObject share_json = new JSONObject();
						JSONArray array = new JSONArray();
						String[] members = participants.split(";");
						boolean hasEnter = false;
						for (String user : members) {
							if (!array.contains(user) && !TextUtils.isEmpty(user)){
								array.add(user);
								if (user.equals(Constant.UID)){
									hasEnter = true;
								}
							}

						}
						share_json.put("friend", array);
						share_json.put("share", groupId);
						schedual.setFriend(share_json.toJSONString());
						SchedualDao dao = new SchedualDao(context);
						if(hasEnter){
							dao.saveSchedual(schedual,1);
						}else{
							dao.saveSchedual(schedual,0);
						}

						mData.add(schedual);
						Collections.sort(mData, new ScheduleComparator() {
						});
						adapter.notifyDataSetChanged();
					} else if (status == 35) {
//						Toast.makeText(context, "该群日程已删除~", Toast.LENGTH_SHORT).show();
						Log.e("error", "no such activity");
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.e("error", e.toString());
				}
			}
		});
	}

	private String getRemindTime(String start, int span) {
		// TODO Auto-generated method stub
		String rTime;
		start=start.substring(0,16);
		DateTime begin=new DateTime(start+":00");
		DateTime r;
		switch(span){
			case 1:
				r=begin.plus(0, 0, 0, 0, 0, 0, 0, null);
				break;
			case 2:
				r=begin.minus(0, 0, 0, 0, 10, 0, 0, null);
				break;
			case 3:
				r=begin.minus(0, 0, 0, 0, 30, 0, 0, null);
				break;
			case 4:
				r=begin.minus(0, 0, 0, 1, 0, 0, 0, null);
				break;
			case 5:
				r=begin.minus(0, 0, 1, 0, 0, 0, 0, null);
				break;
			default:
				r=begin.minus(0, 0, 0, 0, 0, 0, 0, null);
				break;
		}
		rTime=r.toString().substring(0, 16);
		return rTime;
	}
	public String getTime(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date(now));
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
			Schedual schedual = mData.get(position);
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

			final String publisher;
			int type=0;
			String title="",time="",place="",info="";
			String shareId = schedual.getShareId();
			int loc = shareId.indexOf("@");
			if(loc!=-1){
				publisher=shareId.substring(0,loc);
			}else{
				return null;
			}

			try{

				title = schedual.getTitle();
				type = schedual.getType();
				time = schedual.getEndtime();
				place = schedual.getPlace();

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
