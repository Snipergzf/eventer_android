package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.adapter.CourseAdapter;
import com.eventer.app.db.CourseDao;
import com.eventer.app.entity.Course;
import com.eventer.app.view.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class Activity_CourseTable_View extends SwipeBackActivity implements
		OnClickListener{
	private Context context;
	private List<Course> mData=new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coursetable_view);
		setBaseTitle(R.string.course_table_detail);
		context=Activity_CourseTable_View.this;
		initView();
	}

	/***
	 * 初始化控件，给控件添加事件响应
	 */
	private void initView() {
		TextView tv_add = (TextView) findViewById(R.id.tv_add_finish);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		ListView listview = (ListView) findViewById(R.id.listview);

		mData=getIntent().getParcelableArrayListExtra("courseList");
		CourseAdapter adapter = new CourseAdapter(context, R.layout.item_course_list, mData);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("c_detail", mData.get(position));
				intent.setClass(Activity_CourseTable_View.this, Activity_Course_Edit.class);
				startActivityForResult(intent, 11);

			}
		});

		String info = getIntent().getStringExtra("class");
		info = "以下是 <b><em>"+info+"</em></b> 的课表的课程列表";
		tv_title.setText(Html.fromHtml(info));
		tv_add.setOnClickListener(this);
	}

	/**
	 * 页面控件的点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_add_finish:
				showMyDialog("添加课程");
				break;
			default:
				break;
		}
	}

	/**
	 * 选择添加课表的方式
	 * 1.直接添加
	 * 2.删除以前的课表再添加
	 */
	private void showMyDialog(String title) {

		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		Window window = dlg.getWindow();
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.alertdialog);
		window.findViewById(R.id.ll_title).setVisibility(View.VISIBLE);

		TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
		tv_title.setText(title);
		TextView tv_content1 = (TextView) window.findViewById(R.id.tv_content1);
		tv_content1.setText("直接添加整个课表");

		tv_content1.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SdCardPath")
			public void onClick(View v) {

				CourseDao dao=new CourseDao(context);
				dao.saveCourseListByInfo(mData);
				dlg.cancel();
				startActivity(new Intent().setClass(context, Activity_Course.class));
				finish();
			}
		});
		TextView tv_content2 = (TextView) window.findViewById(R.id.tv_content2);
		tv_content2.setText("清空原有课程再添加课表");
		tv_content2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CourseDao dao=new CourseDao(context);
				dao.deleteAllCourse();
				dao.saveCourseListByInfo(mData);
				dlg.cancel();
                startActivity(new Intent().setClass(context, Activity_Course.class));
				finish();

			}
		});
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
