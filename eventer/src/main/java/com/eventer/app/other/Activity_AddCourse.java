package com.eventer.app.other;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.adapter.CourseAdapter;
import com.eventer.app.db.CourseDao;
import com.eventer.app.entity.Course;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.widget.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_AddCourse extends SwipeBackActivity implements
		OnClickListener {

	public static Activity_AddCourse instance;


	private EditText et_search;
	private InputMethodManager manager;
	ListView listview;
	Button btn_search, btn_add_table, btn_add_manually;

	private CourseAdapter adapter;
	private List<Course> mData = new ArrayList<>();
	public static List<String> ClassIdList;

	private Context context;
	public static int REQUEST=0x38;

	public Activity_AddCourse() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_add);
		setBaseTitle(getString(R.string.add_course));

		instance=this;
		context=this;
		initView();
	}

	public void back(){
		this.finish();
	}

	private void initView() {
		// TODO Auto-generated method stub

		et_search = (EditText) findViewById(R.id.et_search);
		listview = (ListView) findViewById(R.id.listview);
		btn_search = (Button) findViewById(R.id.btn_search);
		btn_add_table = (Button) findViewById(R.id.btn_add_table);
		btn_add_manually = (Button) findViewById(R.id.btn_add_manually);
		adapter = new CourseAdapter(context, R.layout.item_course_list, mData);
		listview.setAdapter(adapter);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		btn_search.setOnClickListener(this);
		btn_add_manually.setOnClickListener(this);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.putExtra("c_detail", mData.get(position));
				intent.setClass(context, Activity_Course_Edit.class);
				startActivityForResult(intent, 11);

			}
		});

		CourseDao dao =new CourseDao(context);
		ClassIdList=dao.getCourseIdList();
		btn_add_table.setOnClickListener(this);

	}

	/**
	 * 获取课程列表
	 */
	private void refresh(String str) {
		mData.clear();
		Map<String, String> params = new HashMap<>();
		params.put("uid", Constant.UID+"");
		params.put("search_name", str);
		GetCourseByHTTP(params);
	}

	public void refresh() {
		adapter.notifyDataSetChanged();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btn_search:
				String str=et_search.getText().toString();
				adapter.setHint(str);
				if(!str.equals("")){
					refresh(str);
				}
				break;
			case R.id.btn_add_table:
				Intent intent=new Intent();
				intent.setClass(context, Activity_AddCourseTable.class);
				startActivityForResult(intent, REQUEST);
				break;
			case R.id.btn_add_manually:
				Intent intent1=new Intent();
				intent1.setClass(context, Activity_AddCourse_Manually.class);
				startActivityForResult(intent1, REQUEST);
				break;
			default:
				break;
		}
		hideKeyboard();
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 执行异步任务
	 *
	 *
	 */
	public void GetCourseByHTTP(final Object... params) {
		new AsyncTask<Object, Object,List<Course>>() {
			@SuppressWarnings("unchecked")
			@Override
			protected List<Course> doInBackground(Object... params) {
				List<Course> list;
				try {
					list= HttpUnit.sendCourseRequest((Map<String, String>) params[0]);
					return list;

				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Log.e("search class error", e.toString());
					return null;
				}
			}
			protected void onPostExecute(List<Course> list) {
				if(list!=null){
					mData.addAll(list);
				}else{
					mData.clear();
				}

//				adapter.setData(mData);
				adapter.notifyDataSetChanged();
				listview.setEmptyView(findViewById(R.id.tv_empty));
			}
		}.execute(params);}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
