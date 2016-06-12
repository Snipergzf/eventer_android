package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.db.CourseDao;
import com.eventer.app.entity.Course;
import com.eventer.app.main.BaseActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

@SuppressLint("SetTextI18n")

public class Activity_Course_View extends BaseActivity implements OnClickListener{

	public static int REQUEST=0x34;
	private String courseid;
	private Context	context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_info_view);
		setBaseTitle(R.string.course_info);
		context = Activity_Course_View.this;
		courseid = getIntent().getStringExtra("CourseID");
		if(!TextUtils.isEmpty( courseid )){
			initView();
		} else{
			finish();
		}

	}

	/***
	 * 初始化控件，给控件添加事件响应
	 */
	private void initView(){

		TextView kc_name = (TextView) findViewById(R.id.view_kcname_tv);
		TextView kc_teacher = (TextView) findViewById(R.id.view_kcteacher_tv);
		LinearLayout ll_timeblock = (LinearLayout) findViewById(R.id.ll_timeblock);
		TextView kc_delete = (TextView) findViewById(R.id.kc_delete_tv);
		Button edit_course_btn = (Button) findViewById(R.id.edit_course_info);

		kc_delete.setOnClickListener(this);
		edit_course_btn.setOnClickListener(this);
		ll_timeblock.removeAllViews();

		//从数据库中读取课程信息，并加载数据
		CourseDao dao=new CourseDao(context);
		List<Course> mData=dao.getCourseList(courseid + "");
		if(mData != null && mData.size() > 0){
			Course course=mData.get(0);
			String name= course.getClassname();
			String teacher= course.getTeacher();

			if(!TextUtils.isEmpty(name)) {
				kc_name.setText(name);
				setBaseTitle(name);
			}
			if(!TextUtils.isEmpty(teacher)) {
				kc_teacher.setText(teacher);
			} else {
				kc_teacher.setText("--");
			}
			//在LinearLayout中实现类似于ListView的效果，动态加载View
			for (int i = 0;i < mData.size();i++) {

				View item = LayoutInflater.from(context)
						.inflate(R.layout.item_course_detail_view, null);
				LinearLayout info = (LinearLayout) item.findViewById(R.id.ll_info);
				if(i == 0) {
					info.setVisibility(View.GONE);
				} else {
					info.setVisibility(View.VISIBLE);
				}
				TextView tv_loc = (TextView) item.findViewById(R.id.tv_location);
				TextView tv_week = (TextView) item.findViewById(R.id.tv_week);
				TextView tv_time = (TextView) item.findViewById(R.id.tv_time);

                Course course1 = mData.get(i);
				String week = course1.getWeek();
				String time = course1.getTime();
				String loc = course1.getLoction();
				int day = course1.getDay();
				if(!TextUtils.isEmpty(loc)){
					tv_loc.setText(loc);
				}else{
					tv_loc.setText("--");
				}
				if(!TextUtils.isEmpty(week)){
					tv_week.setText(week+"周");
				}
				if(!TextUtils.isEmpty(time)){
					String[] weeks=context.getResources().getStringArray(R.array.weeks);
					tv_time.setText(weeks[day]+" "+time+"节");
				}
				ll_timeblock.addView(item);
			}
		}



	}

	/**
	 * 页面控件的点击事件
	 */
	@Override
	public void onClick(View v) {

		switch(v.getId()){
			case R.id.kc_delete_tv: //删除课程
				CourseDao dao=new CourseDao(context);
				dao.deleteCourse(courseid);
				this.finish();
				break;
			case R.id.edit_course_info: //编辑课程
				Intent intent=new Intent();
				intent.setClass(context, Activity_Course_Edit.class);
				intent.putExtra("CourseID", courseid);
				startActivityForResult(intent, REQUEST);
				break;
			default:
				break;
		}

	}

	/**
	 * 处理页面返回值
	 * 处理课程编辑页面的返回值，如果课程被编辑则刷新页面
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST && data != null)
		{
			String id = data.getStringExtra("ID");
			if(courseid.equals(id)){
				initView();
			}
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
	}


}
