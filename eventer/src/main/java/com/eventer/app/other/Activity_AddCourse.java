package com.eventer.app.other;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.eventer.app.main.BaseActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_AddCourse extends BaseActivity implements
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


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_add);
		setBaseTitle(getString(R.string.add_course));

		instance=this;
		context=this;
		initView();
	}



	/***
	 * 初始化控件，给控件添加事件响应
	 */
	private void initView() {

		et_search = (EditText) findViewById(R.id.et_search);
		listview = (ListView) findViewById(R.id.listview);
		btn_search = (Button) findViewById(R.id.btn_search);
		btn_add_table = (Button) findViewById(R.id.btn_add_table);
		btn_add_manually = (Button) findViewById(R.id.btn_add_manually);

		btn_add_table.setOnClickListener(this);
		btn_search.setOnClickListener(this);
		btn_add_manually.setOnClickListener(this);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				Intent intent = new Intent();
				intent.putExtra("c_detail", mData.get(position));
				intent.setClass(context, Activity_Course_Edit.class);
				startActivityForResult(intent, 11);

			}
		});

		CourseDao dao =new CourseDao(context);
		ClassIdList=dao.getCourseIdList();

		adapter = new CourseAdapter(context, R.layout.item_course_list, mData);
		listview.setAdapter(adapter);

		//管理输入法键盘
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

	}


	/**
	 * 页面控件的点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_search: //搜索课程
				String str=et_search.getText().toString();
				adapter.setHint(str);
				if(!str.equals("")){
					searchCourse(str);
				}
				break;
			case R.id.btn_add_table: //添加课表
				Intent intent=new Intent();
				intent.setClass(context, Activity_AddCourseTable.class);
				startActivityForResult(intent, REQUEST);
				break;
			case R.id.btn_add_manually: //手动添加
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
	 * 搜索课程
	 * 向服务器发送请求，获取课程信息
	 * @param str 课程搜索条件
	 */
	private void searchCourse(String str) {
		mData.clear();
		Map<String, String> params = new HashMap<>();
		params.put("uid", Constant.UID+"");
		params.put("search_name", str);
		getCourseByHTTP(params);
	}

	/**
	 * 执行异步任务
	 *
	 */
	public void getCourseByHTTP(final Object... params) {
		new AsyncTask<Object, Object,List<Course>>() {
			@SuppressWarnings("unchecked")
			@Override
			protected List<Course> doInBackground(Object... params) {
				List<Course> list;
				try {
					list= HttpUnit.sendCourseRequest((Map<String, String>) params[0]);
					return list;

				} catch (Throwable e) {
					e.printStackTrace();
					return null;
				}
			}
			protected void onPostExecute(List<Course> list) {
				if(list!=null){
					mData.addAll(list);
				}else{
					mData.clear();
				}

				adapter.notifyDataSetChanged();
				listview.setEmptyView(findViewById(R.id.tv_empty));
			}
		}.execute(params);}


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
