package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.db.DBManager;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.entity.Schedual;
import com.eventer.app.ui.base.BaseActivityTest;
import com.umeng.analytics.MobclickAgent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint({ "Recycle", "InflateParams" })
public class Calendar_ViewSchedual extends BaseActivityTest implements OnClickListener {

	public TextView eventtitle;
	ImageView iv_delete,iv_edit,iv_share,iv_finish;

	public ListView listview;
	private List<Map<String, Object>> mData;
	public static final String ARGUMENT_ID = "id";
	public static final String ARGUMENT_DATE = "date";
	public static final String ARGUMENT_TYPE = "type";
	public static final String ARGUMENT_LOC = "position";
	public static final String RESPONSE = "response";
	public static final int REQUEST_EDIT = 0x120;
	private String id;
	private String date;
	private Dialog mDialog;
	private Context context;
	private Schedual s=new Schedual();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_viewschedual);
		context=this;
		setBaseTitle(R.string.eventdetail);
		id=getIntent().getStringExtra(ARGUMENT_ID);
		SchedualDao dao=new SchedualDao(context);
		s=dao.getSchedual(id);
		initView();
		init();
	}
	/***
	 * 初始化控件
	 */
	private void initView() {
		// TODO Auto-generated method stub

		eventtitle=(TextView)findViewById(R.id.viewevent_title);
		listview=(ListView)findViewById(R.id.eventdetail_lv);
		iv_delete=(ImageView)findViewById(R.id.iv_delete);
		iv_edit=(ImageView)findViewById(R.id.iv_edit);
		iv_share=(ImageView)findViewById(R.id.iv_share);
		iv_finish=(ImageView)findViewById(R.id.iv_finish);



		iv_delete.setOnClickListener(this);
		iv_edit.setOnClickListener(this);
		iv_finish.setOnClickListener(this);
		iv_share.setOnClickListener(this);

		listview.setOverScrollMode(View.OVER_SCROLL_NEVER);
		if(s.getType()==2){
			setBaseTitle(R.string.schedual_detail);

		}else if(s.getType()==3){
			setBaseTitle(R.string.todo_detail);
		}
	}
	/***
	 * 加载数据
	 */
	@SuppressLint("SimpleDateFormat")
	public void init(){
		SimpleDateFormat  sDateFormat  = new   SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat  DateFormat  = new   SimpleDateFormat("yyyy-MM-dd");
		date=getIntent().getStringExtra(ARGUMENT_DATE);
		Log.e("1", date);
		String time =date;
		try {
			Date old = DateFormat.parse(date);
			time =sDateFormat.format(old);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mData=new ArrayList<Map<String,Object>>();

		String start=s.getStarttime();
		String end=s.getEndtime();
		String title=s.getTitle();
		String place=s.getPlace();
		String detail =s.getDetail();
		int remind=s.getRemind();
		int _f=s.getFrequency();
// 	        String friend= c.getString(c.getColumnIndex("companion"));
		Map<String, Object> map = new HashMap<String, Object>();
		String s_type="事件";
		if(s.getType()==2){
			s_type="日程";
		}else if(s.getType()==3){
			s_type="待办";
		}
		if(title.trim().length() != 0&&title!=null){
			eventtitle.setText(s_type+"-"+title);
		}else{
			eventtitle.setText(s_type+"-"+"(无标题)");
		}

		map = new HashMap<String, Object>();
		map.put("info", time);
		map.put("id", 1);
		mData.add(map);


		if(place.trim().length() != 0&&place!=null){
			map = new HashMap<String, Object>();
			map.put("info", place);
			map.put("id", 2);
			mData.add(map);
		}
		if(detail.trim().length() != 0&&detail!=null){
			map = new HashMap<String, Object>();
			map.put("info", detail);
			map.put("id", 3);
			mData.add(map);
		}

		if(_f!=0){
			TypedArray imgCountry = getResources().obtainTypedArray(R.array.eventrepeat);
			//imgCountry.getResourceId(0,0) ;
			String frequncy=imgCountry.getString(_f);
			map = new HashMap<String, Object>();
			map.put("info", frequncy);
			map.put("id", 4);
			mData.add(map);
		}

		TypedArray imgCountry = getResources().obtainTypedArray(R.array.eventalarm);
		//imgCountry.getResourceId(0,0) ;
		String alarm=imgCountry.getString(remind);
		map = new HashMap<String, Object>();
		map.put("info", alarm+"提醒");
		map.put("id", 5);
		mData.add(map);

//        }  
//        dbHelper.closeDatabase();
		MyAdapter adapter=new MyAdapter(this);
		listview.setAdapter(adapter);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
			case R.id.iv_finish:
			case R.id.iv_back:
				this.finish();
				break;
			case R.id.iv_edit:
				Intent intent=new Intent();
				intent.setClass(Calendar_ViewSchedual.this, Calendar_AddSchedual.class);
				Bundle bundle = new Bundle();                           //创建Bundle对象
				bundle.putLong(ARGUMENT_ID, Long.parseLong(id));     //装入数据
				bundle.putInt(ARGUMENT_TYPE, s.getType());
				bundle.putString(ARGUMENT_DATE, date);
				intent.putExtras(bundle);
				startActivityForResult(intent,REQUEST_EDIT);
				break;
			case R.id.iv_delete:
				DBManager dbHelper;
				dbHelper = new DBManager(this);
				dbHelper.openDatabase();
				dbHelper.delete("dbSchedule", "scheduleID=?", new String[]{id});
				dbHelper.closeDatabase();
				Intent intent2=new Intent();
				intent2.putExtra("IsChange", true);
				this.finish();
				break;
			case R.id.iv_share:
				if (mDialog == null) {
					mDialog = new Dialog(context, R.style.login_dialog);
					mDialog.setCanceledOnTouchOutside(true);
					Window win = mDialog.getWindow();
					LayoutParams params = new LayoutParams();
					params.width = LayoutParams.MATCH_PARENT;
					params.height = LayoutParams.WRAP_CONTENT;
					params.x = 0;
					params.y = 0;
					win.setAttributes(params);
					mDialog.setContentView(R.layout.dialog_share);
					mDialog.findViewById(R.id.share_by_chatroom).setOnClickListener(this);
					mDialog.findViewById(R.id.share_by_user).setOnClickListener(this);
					mDialog.findViewById(R.id.share_cancel).setOnClickListener(this);
					mDialog.findViewById(R.id.share_layout).setOnClickListener(this);
				}
				mDialog.show();
				break;
			case R.id.share_by_chatroom:
				startActivity(new Intent().setClass(context, ShareToGroupActivity.class)
						.putExtra("schedual_id", id)
						.putExtra("sharetype", ShareToSingleActivity.SHARE_SCHEDUAL));
				mDialog.dismiss();
				break;
			case R.id.share_by_user:
				startActivity(new Intent().setClass(context, ShareToSingleActivity.class)
						.putExtra("schedual_id", id)
						.putExtra("sharetype", ShareToSingleActivity.SHARE_SCHEDUAL));
				mDialog.dismiss();
				break;
			case R.id.share_cancel:
			case R.id.share_layout:
				mDialog.dismiss();
				break;
			default:
				break;
		}
	}


	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
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
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		//****************************************final方法
//注意原本getView方法中的int position变量是非final的，现在改为final  
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {

				holder=new ViewHolder();
				//可以理解为从vlist获取view  之后把view返回给ListView
				convertView = mInflater.inflate(R.layout.item_event_detail, null);
				holder.info = (TextView)convertView.findViewById(R.id.tv_detail);
				holder.title=(TextView)convertView.findViewById(R.id.tv_title);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder)convertView.getTag();
			}

			holder.info.setText((String)mData.get(position).get("info"));
			int id=(int)mData.get(position).get("id");
			switch (id) {
				case 1:
					if(s.getType()==2){
						holder.title.setText(R.string.eventtimestart);
					}else if(s.getType()==3){
						holder.title.setText(R.string.todo_time);
					}
					break;
				case 2:
					holder.title.setText(R.string.location);
					break;
				case 3:
					holder.title.setText(R.string.eventdetail);
					break;
				case 4:
					holder.title.setText(R.string.eventrepeat);
					break;
				case 5:
					holder.title.setText(R.string.eventalarm);
					break;
				default:
					break;
			}
			return convertView;
		}
	}
	//提取出来方便点
	public static class ViewHolder {
		public TextView info;
		public TextView title;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_EDIT&&data!=null){
			boolean status=data.getBooleanExtra(Calendar_AddSchedual.RESPONSE, false);
			if(status){
				Intent intent2=new Intent();
				intent2.putExtra("IsChange", true);
			}
			this.finish();
		}
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
	};

}
