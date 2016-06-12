package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.util.HanziToPinyin;
import com.easemob.util.HanziToPinyin.Token;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.PhoneDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.Phone;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.task.Contact;
import com.eventer.app.task.LoadImage;
import com.eventer.app.task.LoadImage.ImageDownloadedCallBack;
import com.eventer.app.view.CircleProgressBar;
import com.eventer.app.view.refreshlist.IXListViewRefreshListener;
import com.eventer.app.view.refreshlist.XListView;
import com.eventer.app.view.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@SuppressLint("SetTextI18n")
public class LocalContactActivity extends SwipeBackActivity {

	private MyAdapter adapter;
	private List<Map<String, String>> SourceData=new ArrayList<>();
	private List<Phone> mData=new ArrayList<>();
	private Map<String,UserInfo> isExist=new HashMap<>();
	private XListView listView;
	private final int INIT_LIST = 1;
	private LinearLayout loading;

	public Context context;
	private LoadImage avatarLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_phone_contact);
		context=this;
		setBaseTitle(R.string.phone_contact);
		avatarLoader = new LoadImage(context, Constant.IMAGE_PATH);
		initView();


	}

	/***
	 * 初始化控件，给控件添加事件响应
	 */
	private void initView() {
		listView = (XListView) findViewById(R.id.list);

		loading=(LinearLayout)findViewById(R.id.ll_loading);
		CircleProgressBar progress=(CircleProgressBar)findViewById(R.id.progress);
		progress.setColorSchemeResources(android.R.color.holo_orange_light);


		adapter = new MyAdapter(context);
		listView.setAdapter(adapter);
		listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				String phone = mData.get(position).getTel();
				UserInfo u;
				if (isExist.containsKey(phone)) {
					u = isExist.get(phone);
					startActivity(new Intent().setClass(context, Activity_UserInfo.class).putExtra("user", u.getUsername()));

				}

			}
		});
		//listView的刷新
		listView.setPullRefreshEnable(new IXListViewRefreshListener() {

			@Override
			public void onRefresh() {
				handleTel(SourceData);
			}
		});
	}

	/**
	 * 获取联系人列表序
	 */
	private void getContactList() {
		SourceData.clear();
		mData.clear();
		Contact contact=new Contact(context);
		SourceData=contact.getPhoneContactsList();
		handleTel(SourceData);
	}



	// 刷新ui
	public void refresh() {
		try {
			this.runOnUiThread(new Runnable() {
				public void run() {
					PhoneDao dao=new PhoneDao(context);
					List<Phone> list=dao.getPhoneList();
					mData=new ArrayList<>();
					for (Phone phone : list) {
						if(isExist.containsKey(phone.getTel())){
							mData.add(phone);
						}
					}
					UserDao d=new UserDao(context);
					for (Phone p: mData) {
						String user=p.getUserId();
						if(!TextUtils.isEmpty(user)){
							UserInfo info=d.getInfo(user);
							isExist.put(p.getTel()+"", info);
						}
						Map<String, String> map=new HashMap<>();
						map.put("phone", p.getTel());
						map.put("name", p.getRelName());
						SourceData.add(map);
					}

					Collections.sort(mData, new FullPinyinComparator() {
					});
					adapter.notifyDataSetChanged();
					listView.stopRefresh();

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
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



	/**
	 * 执行异步任务
	 * 遍历通讯录中的手机列表，判断该手机号是否注册
	 *
	 */
	public void handleTel(final Object... params) {
		new AsyncTask<Object, Object,Boolean>() {
			@SuppressWarnings("unchecked")
			@Override
			protected Boolean doInBackground(Object... params) {
				try {
					List<Map<String, String>> list = (List<Map<String, String>>) params[0];
					Map<String,UserInfo> user = MyApplication.getInstance().getUserList();
					PhoneDao dao = new PhoneDao(context);
					for (Map<String, String> map1 : list) {
						String string = map1.get("phone");
						String realname = map1.get("name");
						if(!isExist.containsKey(string)){
							Map<String,String> map = new HashMap<>();
							map.put("search_name", string);
							map.put("uid", Constant.UID+"");
							map.put("token", Constant.TOKEN);
							Map<String,Object> info = HttpUnit.sendSerachFriendRequest(map);
							int status = (int) info.get("status");
							if(status == 0) {
								String s = (String) info.get("info");
								JSONObject jsonObject = new  JSONObject(s);
								String uid = jsonObject.getString("id");
								String avatar = jsonObject.getString("avatar");
								String name = jsonObject.getString("name");
								if(user.containsKey(uid)) {
									UserInfo userinfo = user.get(uid);
									isExist.put(string, userinfo);
								} else {
									UserInfo userinfo=new UserInfo();
									userinfo.setAvatar(avatar);
									userinfo.setNick(name);
									userinfo.setType(22);
									userinfo.setUsername(uid);
									MyApplication.getInstance().addUser(userinfo);
									isExist.put(string, userinfo);
								}
								Phone p=new Phone();
								p.setRelName(realname);
								p.setTel(string);
								p.setUserId(uid);
								dao.savePhone(p);
							}else{
								Phone p=new Phone();
								p.setRelName(realname);
								p.setTel(string);
								dao.savePhone(p);
							}
						}
					}
					return true;
				} catch (Throwable e) {
					e.printStackTrace();
					return false;
				}
			}
			protected void onPostExecute(Boolean result) {
				// 设置adapter
				if(!result){
					if(!Constant.isConnectNet){
						Toast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
					}
				}

				loading.setVisibility(View.GONE);
				refresh();

			}
		}.execute(params);}


	/***
	 * 手机通讯录的适配器
	 * @author LiuNana
	 *
	 */
	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ViewHolder")
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder=new ViewHolder();
				//可以理解为从vlist获取view  之后把view返回给ListView
				convertView = mInflater.inflate(R.layout.item_phone_contact_list, parent ,false);
				holder.name = (TextView)convertView.findViewById(R.id.tv_name);
				holder.phone = (TextView)convertView.findViewById(R.id.tv_eventer_id);
				holder.avatar=(ImageView)convertView.findViewById(R.id.iv_avatar);
				holder.add=(Button)convertView.findViewById(R.id.tv_add);
				holder.text=(TextView)convertView.findViewById(R.id.tv_text);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			holder.name.setText("");
			holder.add.setText("");
			holder.phone.setText("");
			holder.avatar.setImageResource(R.drawable.default_avatar);
			holder.text.setText("");
			String phone=mData.get(position).getTel();
			String name=mData.get(position).getRelName();
			holder.name.setText(name);
			holder.phone.setText(phone+"");
			boolean isEventer=false;
			UserInfo u;
			u=isExist.get(phone);
			int type=u.getType();
			if(type==1){
				holder.add.setVisibility(View.GONE);
				holder.text.setVisibility(View.VISIBLE);
				holder.text.setText("已添加");
			}else{
				holder.text.setVisibility(View.GONE);
				holder.add.setVisibility(View.VISIBLE);
				holder.add.setBackgroundResource(R.drawable.btn_blue_bg);
				holder.add.setText("添加");
				isEventer=true;
			}
			String avatar=u.getAvatar();
			showUserAvatar(holder.avatar, avatar);
			final boolean isTrue=isEventer;
			final UserInfo temp_user=u;
			holder.add.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isTrue){
						Intent intent=new Intent();
						intent.setClass(context, Activity_Friends_Add.class);
						intent.putExtra("id", temp_user.getUsername());
						intent.putExtra("avatar", temp_user.getAvatar());
						intent.putExtra("nick", temp_user.getNick());
						context.startActivity(intent);
					}
				}
			});
			return convertView;
		}
	}
	public final class ViewHolder {
		TextView name;
		TextView phone;
		ImageView avatar;
		Button add;
		TextView text;
	}


	/***
	 * 通过拼音对用户进行排序
	 * @author LiuNana
	 *
	 */
	public class FullPinyinComparator implements Comparator<Phone> {

		@Override
		public int compare(Phone o1, Phone o2) {

			String py1 = o1.getRelName();
			String py2 = o2.getRelName();
			py1=getPinYin(py1);
			py2=getPinYin(py2);
			// 判断是否为空""
			if (isEmpty(py1) && isEmpty(py2))
				return 0;
			if (isEmpty(py1))
				return -1;
			if (isEmpty(py2))
				return 1;
			try {
				py1 = py1.toUpperCase();
				py2 = py2.toUpperCase();
			} catch (Exception e) {
				System.out.println("某个str为\" \" 空");
			}
			return py1.compareTo(py2);
		}

		private boolean isEmpty(String str) {
			return "".equals(str.trim());
		}
	}

	public static String getPinYin(String input) {
		ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);

		StringBuilder sb = new StringBuilder();
		if (tokens != null && tokens.size() > 0) {
			for (Token token : tokens) {
				if (Token.PINYIN == token.type) {
					sb.append(token.target);
				} else {
					sb.append(token.source);
				}
			}
		}
		return sb.toString().toLowerCase();
	}

	Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case INIT_LIST:
					PhoneDao dao=new PhoneDao(context);
					mData=dao.getPhoneList();
					dao.getTelList();
					UserDao d=new UserDao(context);
					if(mData==null||mData.size()==0){
						loading.setVisibility(View.VISIBLE);
						refresh();
						getContactList();
					}else{
						loading.setVisibility(View.GONE);
						for (Phone p: mData) {
							String user=p.getUserId();
							if(!TextUtils.isEmpty(user)){
								UserInfo info=d.getInfo(user);
								isExist.put(p.getTel()+"", info);
							}
							Map<String, String> map=new HashMap<>();
							map.put("phone", p.getTel());
							map.put("name", p.getRelName());
							SourceData.add(map);
						}
						refresh();
						handleTel(SourceData);
					}
					break;
				default:
					break;
			}
			return false;
		}
	});

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				mHandler.sendEmptyMessageDelayed(INIT_LIST, 200);
			}
		}).start();
	}

}
