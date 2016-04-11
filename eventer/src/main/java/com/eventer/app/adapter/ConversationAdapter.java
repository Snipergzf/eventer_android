package com.eventer.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.alibaba.fastjson.JSONObject;
import com.easemob.util.DateUtils;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.ChatroomDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.entity.ChatRoom;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.other.Activity_Chat;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.util.SmileUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hirondelle.date4j.DateTime;

@SuppressLint("SetTextI18n")
public class ConversationAdapter extends BaseAdapter {
	private Context context;                        //运行上下文
	private List<ChatEntity> listItems;
	private LayoutInflater mInflater;            //视图容器
	private LoadUserAvatar avatarLoader;


	public ConversationAdapter(Context context,List<ChatEntity>  listItems) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);   //创建视图容器并设置上下文
		this.listItems = listItems;
		avatarLoader = new LoadUserAvatar(context, Constant.IMAGE_PATH);

	}
	public void setData(List<ChatEntity>  listItems){
		this.listItems = listItems;
	}

	public void addItem(ChatEntity item){
		listItems.add(item);
	}


	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return  listItems.size();
	}

	@Override
	public ChatEntity getItem(int position) {
		// TODO 自动生成的方法存根
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}
	//提取出来方便点
	public final class ViewHolder {
		public ImageView avatar;
		public TextView name;
		public TextView msg;
		public TextView time;
		public TextView unread;

	}

	public String getTime(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		String time;
		String date_str = sdf.format(new Date(now*1000));
		String date_str1 = sdf.format(new Date());

		DateTime date=new DateTime(date_str);
		DateTime today=new DateTime(date_str1);
		int year1=today.getYear();
		int year2=date.getYear();
		int days1=today.getDayOfYear();
		int days2=date.getDayOfYear();
		if(year1!=year2){
			time=date_str.substring(0,11);
		}else if(days1-days2==1){
			time="昨天";
		}else if(days1-days2==0){
			time=date_str.substring(11);
		}else if(today.getWeekDay()>date.getWeekDay()){
			if(days1-days2<7){
				int weekday=date.getWeekDay();
				time=context.getResources().getStringArray(R.array.weeks)[weekday];
			}else{
				time=date_str.substring(5,11);
			}
		}else{
			time=date_str.substring(5,11);
		}

		return time;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自动生成的方法存根
		ViewHolder holder;
		ChatEntity item =  listItems.get(position);
		ChatRoom room=new ChatRoom();
		int chatType=Activity_Chat.CHATTYPE_SINGLE;
		String from=item.getFrom();
		String name;
		User u=MyApplication.getInstance().getContactList().get(item.getFrom());

		if(!from.contains("@")){
			if(u==null){
				UserDao dao=new UserDao(context);
				u=dao.getUser(from);
			}
			if (u.getBeizhu()!=null&&!u.getBeizhu().equals("")) {
				name = u.getBeizhu();
			} else if (!TextUtils.isEmpty(u.getNick())){
				name=u.getNick();
			}else{
				name=u.getUsername();
			}
		}else{
			ChatroomDao dao=new ChatroomDao(context);
			room=dao.getRoom(from);
			chatType=Activity_Chat.CHATTYPE_GROUP;
			if(room!=null)
				name = room.getDefaultName();
			else
				name = "群组";
		}

		if(chatType==Activity_Chat.CHATTYPE_SINGLE){
			holder=new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_conversation_single, new LinearLayout(context),false);
			holder.avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
			holder.msg = (TextView) convertView.findViewById(R.id.tv_content);
			holder.time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.name=(TextView) convertView.findViewById(R.id.tv_name);
			holder.unread=(TextView) convertView.findViewById(R.id.tv_unread);
			handleTextMessage(item,holder,chatType);

			showUserAvatar(holder.avatar, u.getAvatar());
		}else{
			holder=new ViewHolder();
			int size=0;
			if(room!=null&&room.getMember()!=null){
				size=room.getMember().length;
			}
			if(size<1){
				size=1;
			}
			convertView = creatConvertView(size);
			holder.msg = (TextView) convertView.findViewById(R.id.tv_content);
			holder.time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.name=(TextView) convertView.findViewById(R.id.tv_name);
			holder.unread=(TextView) convertView.findViewById(R.id.tv_unread);
			final View view=convertView;
			final int[] avatar_id=new int[]{R.id.iv_avatar1,R.id.iv_avatar2,R.id.iv_avatar3,R.id.iv_avatar4};
			if(room!=null&&room.getMember()!=null){
				int memberNum=room.getMember().length;
				if(memberNum>0){
					String[] members=room.getMember();
					for( int i=0;i<memberNum&&i<4;i++){
						final int len=i;
						final String member=members[i];
						if(member.equals(Constant.UID)){
							String avatar=LocalUserInfo.getInstance(context).getUserInfo("avatar");
							showUserAvatar((ImageView) convertView
									.findViewById(avatar_id[i]), avatar);
						}else if(MyApplication.getInstance().getContactList().containsKey(member)){
							User user=MyApplication.getInstance().getContactList().get(member);
							String avatar=user.getAvatar();
							showUserAvatar((ImageView) convertView.findViewById(avatar_id[i]), avatar);
						}else if(MyApplication.getInstance().getUserList().containsKey(member)){
							UserInfo user=MyApplication.getInstance().getUserList().get(member);
							String avatar=user.getAvatar();
							showUserAvatar((ImageView) convertView
									.findViewById(avatar_id[i]), avatar);
						}else{
							Map<String,String> map=new HashMap<>();
							map.put("uid", member);
							LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_GET_USERINFO, map);
							task.getData(new DataCallBack() {
								@Override
								public void onDataCallBack(JSONObject data) {
									// TODO Auto-generated method stub
									try {
										int status=data.getInteger("status");
										switch (status) {
											case 0:
												JSONObject user_action=data.getJSONObject("user_action");
												JSONObject info=user_action.getJSONObject("info");
												String name=info.getString("name");
												String avatar=info.getString("avatar");
												showUserAvatar((ImageView) view.findViewById(avatar_id[len]), avatar);
												UserInfo user=new UserInfo();
												user.setAvatar(avatar);
												user.setNick(name);
												user.setType(22);
												user.setUsername(member);
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
					}
				}
			}
			handleTextMessage(item,holder,chatType);
		}


		holder.time.setText(DateUtils.getTimestampString(new Date(
				item.getMsgTime()*1000)));

		holder.name.setText(name);
		int unread=item.getNotRead();
		if(unread>0&&unread<100){
			holder.unread.setText(unread+"");
			holder.unread.setVisibility(View.VISIBLE);
		}else if(unread>99){
			holder.unread.setText(99+"+");
			holder.unread.setVisibility(View.VISIBLE);
		}else{
			holder.unread.setText("");
			holder.unread.setVisibility(View.GONE);
		}


		return convertView;
	}

	private void handleTextMessage(ChatEntity message, final ViewHolder holder,int chatType) {
		String txt = message.getContent();
		final int type=message.getType();
		if (message.getStatus()>1) {
			handleText(txt, type,holder,null);
		}else{
			String talker="";
			String nick;
			if(chatType==Activity_Chat.CHATTYPE_GROUP){
				int i=message.getContent().indexOf(":\n");
				txt="";
				if(i!=-1){
					txt=message.getContent().substring(i+2);
					talker=message.getContent().substring(0, i);
				}
				if(talker.equals(Constant.UID)){
					nick=LocalUserInfo.getInstance(context).getUserInfo("nick");
					handleText(txt, type,holder,nick);
				}else if(MyApplication.getInstance().getContactList().containsKey(talker)){
					User user=MyApplication.getInstance().getContactList().get(talker);
					nick=user.getNick();
					String beizhu=user.getBeizhu();
					if(!TextUtils.isEmpty(beizhu))
						nick=beizhu;
					handleText(txt, type,holder,nick);
				}else if(MyApplication.getInstance().getUserList().containsKey(talker)){
					UserInfo user=MyApplication.getInstance().getUserList().get(talker);
					nick=user.getNick();
					handleText(txt, type,holder,nick);
				}else{
					Map<String,String> map=new HashMap<>();
					map.put("uid", talker);
					final String uid=talker;
					final String content =txt;
					try{
						LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_GET_USERINFO, map);
						task.getData(new DataCallBack() {
							@Override
							public void onDataCallBack(JSONObject data) {
								// TODO Auto-generated method stub
								try {
									int status=data.getInteger("status");
									switch (status) {
										case 0:
											JSONObject user_action=data.getJSONObject("user_action");
											JSONObject info=user_action.getJSONObject("info");
											String name=info.getString("name");
											String avatar=info.getString("avatar");
											UserInfo user=new UserInfo();
											user.setAvatar(avatar);
											user.setNick(name);
											user.setType(22);
											user.setUsername(uid);
											MyApplication.getInstance().addUser(user);
											handleText(content,type, holder,name);
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
					}catch(Exception e){
						handleText(content, type,holder,null);
					}

				}

			} else{
				handleText(txt,type, holder,null);
			}
		}
	}


	private void handleText(String txt, int type,ViewHolder holder,String nick){
		switch (type) {
			case 1:
				if(!TextUtils.isEmpty(nick)){
					txt=nick+": "+txt;
				}
				Spannable span = SmileUtils
						.getSmiledString(txt);
				holder.msg.setText(span, BufferType.SPANNABLE);
				break;
			case 2:
				if(TextUtils.isEmpty(nick)){
					holder.msg.setText("[活动分享]");
				}else{
					holder.msg.setText(nick+":[活动分享]");
				}
				break;
			case 3:
				if(TextUtils.isEmpty(nick)){
					holder.msg.setText("[日程分享]");
				}else{
					holder.msg.setText(nick+":[日程分享]");
				}
				break;
			case 4:
			case 5:
				if(TextUtils.isEmpty(nick)){
					holder.msg.setText("[对日程进行了操作]");
				}else{
					holder.msg.setText(nick+":[对日程进行了操作]");
				}
				break;
			case Constant.GROUP_CREATED_NOTIFICATION:
				holder.msg.setText("建立了新的群组！");
				break;
			case Constant.GROUP_INVITE_NOTIFICATION:
				holder.msg.setText("有新人加入群组！");
				break;
			case Constant.GROUP_LEAVE_NOTIFICATION:
				holder.msg.setText("有人退出群组！");
				break;
			case Constant.GROUP_ACTIVITY_CREATE:
				if(TextUtils.isEmpty(nick)){
					holder.msg.setText("[发布日程]");
				}else{
					holder.msg.setText(nick+":[发布日程]");
				}
				break;
			case Constant.GROUP_ACTIVITY_JOIN:
				if(TextUtils.isEmpty(nick)){
					holder.msg.setText("[加入日程]");
				}else{
					holder.msg.setText(nick+":[加入日程]");
				}
				break;
			case Constant.GROUP_ACTIVITY_EXIT:
				if(TextUtils.isEmpty(nick)){
					holder.msg.setText("[退出日程]");
				}else{
					holder.msg.setText(nick+":[退出日程]");
				}
				break;
			case Constant.GROUP_ACTIVITY_DELETE:
				if(TextUtils.isEmpty(nick)){
					holder.msg.setText("[删除日程]");
				}else{
					holder.msg.setText(nick+":[删除日程]");
				}
				break;
			default:
				break;
		}
	}


	private void showUserAvatar(ImageView iamgeView, String avatar) {
		if(avatar==null||avatar.equals("")||avatar.equals("default")) return;
		final String url_avatar = avatar;
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


	private View creatConvertView( int size) {
		View convertView;
		switch (size) {
			case 1:
				convertView =  mInflater.inflate(R.layout.item_conversation_group_1, new LinearLayout(context),
						false);

				break;
			case 2:
				convertView =  mInflater.inflate(R.layout.item_conversation_group_2, new LinearLayout(context),
						false);
				break;
			case 3:
				convertView =  mInflater.inflate(R.layout.item_conversation_group_3, new LinearLayout(context),
						false);
				break;
			case 4:
				convertView =  mInflater.inflate(R.layout.item_conversation_group_4, new LinearLayout(context),
						false);
				break;

			default:
				convertView =  mInflater.inflate(R.layout.item_conversation_group_4, new LinearLayout(context),
						false);
				break;

		}
		return convertView;
	}

}