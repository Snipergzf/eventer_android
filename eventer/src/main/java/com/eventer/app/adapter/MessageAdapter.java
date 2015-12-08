package com.eventer.app.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.util.DateUtils;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.other.Activity_Chat;
import com.eventer.app.other.Activity_EventDetail;
import com.eventer.app.other.Activity_UserInfo;
import com.eventer.app.other.MyUserInfoActivity;
import com.eventer.app.other.ShareSchedualActivity;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.util.SmileUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

@SuppressLint({ "SdCardPath", "InflateParams" })
public class MessageAdapter extends BaseAdapter {

	private static final int MESSAGE_TYPE_TXT = 1;
	private static final int MESSAGE_TYPE_EVENT = 2;
	private static final int MESSAGE_TYPE_SCHEDUAL = 3;
	private static final int GROUP_CREATED_NOTIFICATION = 6;
	private static final int GROUP_INVITE_NOTIFICATION=8;
	private LayoutInflater inflater;
	private LoadUserAvatar avatarLoader;
	private List<ChatEntity> msglist = new ArrayList<ChatEntity>();
	private Context context;
	private int chatType;

	public MessageAdapter(Context context, String username,
						  List<ChatEntity> msglist, int chatType) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.msglist = msglist;
		this.chatType = chatType;
		avatarLoader = new LoadUserAvatar(context, Constant.IMAGE_PATH);
	}

	/**
	 * 获取item数
	 */
	public int getCount() {
		return msglist.size();
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		notifyDataSetChanged();
	}

	public ChatEntity getItem(int position) {
		return msglist.get(position);
	}

	public void addItem(ChatEntity msg) {
		msglist.add(msg);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	private View createViewByMessage(ChatEntity message) {
		if (message.getStatus() != -1) { // 需要控制getStatus=-1的情况，在加入mData之前处理掉
			switch (message.getType()) {
				case 4:
				case 5:
					return inflater.inflate(R.layout.row_share_message, null);
				case GROUP_CREATED_NOTIFICATION:
				case GROUP_INVITE_NOTIFICATION:
					return inflater.inflate(R.layout.row_share_message, null);
				default:
					// 语音电话
					return message.getStatus() > 1 ? inflater.inflate(
							R.layout.row_sent_message, null) : inflater.inflate(
							R.layout.row_received_message, null);
			}
		}
		return null;
	}

	@SuppressLint("NewApi")
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ChatEntity message = getItem(msglist.size() - position - 1);
		String toChat = "";
		if (chatType == Activity_Chat.CHATTYPE_SINGLE) {
			toChat = message.getFrom();
		} else {
			if (message.getContent() != null
					&& !message.getContent().equals("")) {
				int i = message.getContent().indexOf(":\n");
				if (i != -1) {
					toChat = message.getContent().substring(0, i);
				}
			}
		}
		final String speaker = toChat;
		if (message.getFrom().equals("admin")) {
			return convertView;
		} else {
			ViewHolder holder;
			holder = new ViewHolder();
			convertView = createViewByMessage(message);
			final View view = convertView;
			switch (message.getType()) {
				case 4:
				case 5:
					holder.tv_sharemsg = (TextView) convertView
							.findViewById(R.id.tv_share_msg);
					holder.tv_sharemsg.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							context.startActivity(new Intent()
									.setClass(context, ShareSchedualActivity.class)
									.putExtra("groupId", message.getFrom())
									.putExtra("shareId", message.getShareId()));
						}
					});
					break;
				case GROUP_CREATED_NOTIFICATION:
				case GROUP_INVITE_NOTIFICATION:
					holder.tv_sharemsg = (TextView) convertView.findViewById(R.id.tv_share_msg);
					break;
				default:
					try {
						holder.pb = (ProgressBar) convertView
								.findViewById(R.id.pb_sending);
						holder.head_iv = (ImageView) convertView
								.findViewById(R.id.iv_userhead);
						// 这里是文字内容
						holder.tv = (TextView) convertView
								.findViewById(R.id.tv_chatcontent);
					} catch (Exception e) {
					}

					holder.tv.setOnLongClickListener(new OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							// TODO Auto-generated method stub
							showMyDialog("消息", message, position);
							return true;
						}
					});

					holder.tv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							String txt = message.getContent();
							Log.e("1", txt);
							if (message.getStatus() > 1) {
							} else {
								if (chatType == Activity_Chat.CHATTYPE_GROUP) {
									int i = message.getContent().indexOf(":\n");
									txt = "";
									if (i != -1) {
										txt = message.getContent().substring(i + 2);
									}
								}
							}
							int type = message.getType();
							switch (type) {
								case MESSAGE_TYPE_EVENT:
									try {
										JSONObject json = JSONObject.parseObject(txt);
										String event_id = json.getString("event_id");
										Intent intent = new Intent();
										intent.setClass(context,
												Activity_EventDetail.class);
										intent.putExtra("event_id", event_id);
										context.startActivity(intent);
									} catch (Exception e) {

									}
									break;
								case MESSAGE_TYPE_SCHEDUAL:
									context.startActivity(new Intent()
											.setClass(context,
													ShareSchedualActivity.class)
											.putExtra("groupId", message.getFrom())
											.putExtra("shareId", message.getShareId()));
									break;
								default:
									break;

							}

						}
					});
					TextView timestamp = (TextView) convertView
							.findViewById(R.id.timestamp);
					if (position == 0) {
						timestamp.setText(DateUtils.getTimestampString(new Date(
								message.getMsgTime() * 1000)));
						timestamp.setVisibility(View.VISIBLE);
					} else {
						// 两条消息时间离得如果稍长，显示时间
						if (DateUtils.isCloseEnough(message.getMsgTime() * 1000,
								msglist.get(position - 1).getMsgTime() * 1000)) {
							timestamp.setVisibility(View.GONE);
						} else {
							timestamp.setText(DateUtils
									.getTimestampString(new Date(message
											.getMsgTime() * 1000)));
							timestamp.setVisibility(View.VISIBLE);
						}
					}

					if (message.getStatus() > 1) {
						toChat = Constant.UID;
						final String avatar = LocalUserInfo.getInstance(context)
								.getUserInfo("avatar");
						holder.head_iv.setTag(avatar);
						if (avatar != null && !avatar.equals("")
								&& !avatar.equals("default")) {
							Bitmap bitmap = avatarLoader.loadImage(holder.head_iv,
									avatar, new ImageDownloadedCallBack() {
										@Override
										public void onImageDownloaded(
												ImageView imageView, Bitmap bitmap,
												int status) {
											if (imageView.getTag() == avatar
													&& status == -1) {
												imageView.setImageBitmap(bitmap);
											}
										}
									});
							if (bitmap != null) {
								holder.head_iv.setImageBitmap(bitmap);
							}
						}
					} else {
						if (speaker.equals(Constant.UID)) {
							String avatar = LocalUserInfo.getInstance(context)
									.getUserInfo("avatar");
							showUserAvatar(holder.head_iv, avatar);
						} else if (MyApplication.getInstance().getContactList()
								.containsKey(speaker)) {
							User u = MyApplication.getInstance().getContactList()
									.get(speaker);
							String avatar = u.getAvatar();
							showUserAvatar(holder.head_iv, avatar);
						} else if (MyApplication.getInstance().getUserList()
								.containsKey(speaker)) {
							UserInfo u = MyApplication.getInstance().getUserList()
									.get(speaker);
							String avatar = u.getAvatar();
							showUserAvatar(holder.head_iv, avatar);
						} else {
							Map<String, String> map = new HashMap<String, String>();
							map.put("uid", speaker);
							LoadDataFromHTTP task = new LoadDataFromHTTP(context,
									Constant.URL_GET_USERINFO, map);
							task.getData(new DataCallBack() {
								@Override
								public void onDataCallBack(JSONObject data) {
									// TODO Auto-generated method stub
									try {
										int status = data.getInteger("status");
										switch (status) {
											case 0:
												JSONObject user_action = data
														.getJSONObject("user_action");
												JSONObject info = user_action
														.getJSONObject("info");
												String name = info.getString("name");
												String avatar = info.getString("avatar");
												showUserAvatar(((ImageView) view
																.findViewById(R.id.iv_userhead)),
														avatar);
												UserInfo user = new UserInfo();
												user.setAvatar(avatar);
												user.setNick(name);
												user.setType(22);
												user.setUsername(speaker);
												MyApplication.getInstance().addUser(user);
												break;
											default:
												// Toast.makeText(context, "获取用户信息失败！",
												// Toast.LENGTH_SHORT).show();
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
					final String user = toChat;
					holder.head_iv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (!user.equals(Constant.UID)) {
								Intent intent = new Intent();
								intent.putExtra("user", user);
								intent.setClass(context, Activity_UserInfo.class);
								context.startActivity(intent);
							} else {
								Intent intent = new Intent();
								intent.setClass(context, MyUserInfoActivity.class);
								context.startActivity(intent);
							}

						}
					});
					handleTextMessage(message, holder);

					break;
			}
			final int type = message.getType();
			if (type == 4 || type == 5 ) {
				final String title = getSchedualTitle(message);
				if (message.getStatus() > 1) {
					if (type == 4) {
						holder.tv_sharemsg.setText("我参加了--" + title);
					} else{
						holder.tv_sharemsg.setText("我取消参加了--" + title);
					}
				} else {
					if (MyApplication.getInstance().getContactList()
							.containsKey(toChat)) {
						User u = MyApplication.getInstance().getContactList()
								.get(toChat);
						String nick = u.getNick();
						String beizhu = u.getBeizhu();
						if (!TextUtils.isEmpty(beizhu)) {
							nick = beizhu;
						}
						if (type == 4) {
							holder.tv_sharemsg.setText(nick + "参加了--" + title);
						} else {
							holder.tv_sharemsg.setText(nick + "取消参加了--" + title);
						}

					} else if (MyApplication.getInstance().getUserList()
							.containsKey(toChat)) {
						UserInfo u = MyApplication.getInstance().getUserList()
								.get(toChat);
						String nick = u.getNick();
						if (type == 4) {
							holder.tv_sharemsg.setText(nick + "参加了--" + title);
						} else {
							holder.tv_sharemsg
									.setText(nick + "取消参加了--" + title);
						}
					} else {
						Map<String, String> map = new HashMap<String, String>();
						map.put("uid", toChat);
						LoadDataFromHTTP task = new LoadDataFromHTTP(context,
								Constant.URL_GET_USERINFO, map);
						task.getData(new DataCallBack() {
							@Override
							public void onDataCallBack(JSONObject data) {
								// TODO Auto-generated method stub
								try {
									int status = data.getInteger("status");
									switch (status) {
										case 0:
											JSONObject user_action = data
													.getJSONObject("user_action");
											JSONObject info = user_action
													.getJSONObject("info");
											String name = info.getString("name");
											String avatar = info.getString("avatar");
											UserInfo user = new UserInfo();
											user.setAvatar(avatar);
											user.setNick(name);
											user.setType(22);
											user.setUsername(speaker);
											MyApplication.getInstance().addUser(user);
											TextView tv = (TextView) view
													.findViewById(R.id.tv_share_msg);
											if (type == 4) {
												tv.setText(name + "参加了--" + title);
											} else {
												tv.setText(name + "取消参加了--" + title);
											}
											break;
										default:
											// Toast.makeText(context, "获取用户信息失败！",
											// Toast.LENGTH_SHORT).show();
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

			}else if (type == GROUP_CREATED_NOTIFICATION) {
				StringBuffer display=new StringBuffer();
				try {
					String bodyString = message.getContent();
					int loc = bodyString.indexOf(":\n");
					if (loc != -1) {
						bodyString = bodyString.substring(loc + 2);
					}
					JSONObject groupObject = JSONObject.parseObject(bodyString);
					JSONArray displayJsonArray = groupObject.getJSONArray("displaylist");
					int size = displayJsonArray.size();
					for(int i =0;i<size-1;i++){
						display.append(displayJsonArray.get(i).toString()+", ");
					}
					display.append(displayJsonArray.get(size-1).toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!TextUtils.isEmpty(display)){
					if(Constant.UID.equals(speaker)){
						holder.tv_sharemsg.setText("你创建了群聊，群成员包括:"+display);
					}else{
						UserDao uDao=new UserDao(context);
						String owner_nick =uDao.getNick(speaker);
						if(TextUtils.isEmpty(owner_nick)){
							owner_nick="好友";
						}
						holder.tv_sharemsg.setText(owner_nick+"创建了群聊，群成员包括:"+display);
					}
				}

			}else if(type == GROUP_INVITE_NOTIFICATION){
				StringBuffer display=new StringBuffer();
				try {
					String bodyString = message.getContent();
					int loc = bodyString.indexOf(":\n");
					if (loc != -1) {
						bodyString = bodyString.substring(loc + 2);
					}
					JSONObject groupObject = JSONObject.parseObject(bodyString);
					JSONArray displayJsonArray = groupObject.getJSONArray("newMemberList");
					int size = displayJsonArray.size();
					for(int i =0;i<size-1;i++){
						display.append(displayJsonArray.get(i).toString()+", ");
					}
					display.append(displayJsonArray.get(size-1).toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!TextUtils.isEmpty(display)){
					if(Constant.UID.equals(speaker)){
						holder.tv_sharemsg.setText("你邀请了"+display+"加入群聊");
					}else{
						UserDao uDao=new UserDao(context);
						String owner_nick =uDao.getNick(speaker);
						if(TextUtils.isEmpty(owner_nick)){
							owner_nick="好友";
						}
						holder.tv_sharemsg.setText(owner_nick+"邀请了"+display+"加入群聊");
					}
				}

			}
		}
		return convertView;
	}

	private void showMyDialog(String title, final ChatEntity message,
							  final int position) {

		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		Window window = dlg.getWindow();
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.alertdialog);
		window.findViewById(R.id.ll_title).setVisibility(View.VISIBLE);

		TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
		tv_title.setText(title);
		TextView tv_content1 = (TextView) window.findViewById(R.id.tv_content1);
		// 是否已经置顶
		// tv_content1.setText("置顶聊天");
		tv_content1.setVisibility(View.GONE);
		TextView tv_content2 = (TextView) window.findViewById(R.id.tv_content2);
		tv_content2.setText("删除该消息");
		tv_content2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ChatEntityDao dao = new ChatEntityDao(context);
				dao.deleteMessage(message.getMsgID() + "");
				msglist.remove(msglist.size() - 1 - position);
				refresh();
				dlg.cancel();

			}
		});

	}

	/**
	 * 文本消息
	 *
	 * @param message
	 * @param holder
	 */
	private void handleTextMessage(ChatEntity message, ViewHolder holder) {
		String txt = message.getContent();
		if (message.getStatus() > 1) {
		} else {
			if (chatType == Activity_Chat.CHATTYPE_GROUP) {
				int i = message.getContent().indexOf(":\n");
				txt = "";
				if (i != -1) {
					txt = message.getContent().substring(i + 2);
				}
			}
		}
		Spannable span;
		switch (message.getType()) {
			case MESSAGE_TYPE_TXT:
				span = SmileUtils.getSmiledText(context, txt);
				holder.tv.setText(span, BufferType.SPANNABLE);
				break;
			case MESSAGE_TYPE_EVENT:
				try {
					JSONObject json = JSONObject.parseObject(txt);
					String event_title = json.getString("event_title");
					String share_txt = "<font color=" + "\"" + "#AAAAAA" + "\">"
							+ "【活动分享】" + "</font><br/>" + "<font color=" + "\""
							+ "#666666" + "\">" + event_title + "</font>";
					holder.tv.setText(Html.fromHtml(share_txt));
				} catch (Exception e) {
				}
				break;
			case MESSAGE_TYPE_SCHEDUAL:
				try {
					JSONObject json = JSONObject.parseObject(txt);
					String title = json.getString("schedule_title");
					String share_txt = "<font color=" + "\"" + "#AAAAAA" + "\">"
							+ "【日程分享】" + "</font><br/>" + "<font color=" + "\""
							+ "#666666" + "\">" + title + "</font>";
					holder.tv.setText(Html.fromHtml(share_txt));
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;

			default:
				span = SmileUtils.getSmiledText(context, txt);
				holder.tv.setText(span, BufferType.SPANNABLE);
				break;
		}
		if (holder.pb != null)
			holder.pb.setVisibility(View.GONE);

	}

	private String getSchedualTitle(ChatEntity message) {
		String txt = message.getContent();
		if (message.getStatus() > 1) {
		} else {
			if (chatType == Activity_Chat.CHATTYPE_GROUP) {
				int i = message.getContent().indexOf(":\n");
				txt = "";
				if (i != -1) {
					txt = message.getContent().substring(i + 2);
				}
			}
		}
		try {
			JSONObject json = JSONObject.parseObject(txt);
			String title = json.getString("schedule_title");
			return title;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	private void showUserAvatar(ImageView iamgeView, String avatar) {
		if (avatar == null || avatar.equals("") || avatar.equals("default"))
			return;
		final String url_avatar = avatar;
		iamgeView.setTag(url_avatar);

		Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
				new ImageDownloadedCallBack() {

					@Override
					public void onImageDownloaded(ImageView imageView,
												  Bitmap bitmap, int status) {
						if (status == -1) {
							if (imageView.getTag() == url_avatar) {
								imageView.setImageBitmap(bitmap);
							}
						}
					}
				});
		if (bitmap != null)
			iamgeView.setImageBitmap(bitmap);

	}

	public static class ViewHolder {
		ImageView iv;
		TextView tv;
		ProgressBar pb;
		ImageView staus_iv;
		ImageView head_iv;
		TextView tv_userId;
		TextView tv_sharemsg;
	}

}