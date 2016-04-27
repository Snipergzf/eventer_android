package com.eventer.app.other;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eventer.app.R;
import com.eventer.app.adapter.CourseTimeAdapter;
import com.eventer.app.db.CourseDao;
import com.eventer.app.entity.Course;
import com.eventer.app.view.ListViewForScrollView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Activity_Course_Edit extends Activity  implements OnClickListener{

	private EditText addkc_name,addkc_teacher;
	private CourseTimeAdapter adapter;
	private String id;
	private List<Course> mData;
	private Context context;
	public static Activity_Course_Edit instance;
	private String teacher;
	private String c_name;
	private Course course;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_info_edit);
		context=Activity_Course_Edit.this;
		instance=this;
		id=getIntent().getStringExtra("CourseID");
		course=getIntent().getParcelableExtra("c_detail");
		initView();

	}
	//初始化界面
	private void initView() {
		// TODO Auto-generated method stub
		ImageView back_img = (ImageView) findViewById(R.id.course_edit_back_iv);
		TextView add_commit = (TextView) findViewById(R.id.addkc_ok);
		addkc_name=(EditText)findViewById(R.id.addkc_name_edit);
		addkc_teacher=(EditText)findViewById(R.id.addkc_teacher_edit);
		ListViewForScrollView listview = (ListViewForScrollView) findViewById(R.id.listview);
		mData=new ArrayList<>();
		TextView title = (TextView) findViewById(R.id.course_edit_title);
		LinearLayout ll_add_time = (LinearLayout) findViewById(R.id.ll_add_time);
		ll_add_time.setOnClickListener(this);
		add_commit.setOnClickListener(this);
		back_img.setOnClickListener(this);

		if(!TextUtils.isEmpty(id)){
			initData();
			if(mData.size()!=0){
				Course c=mData.get(0);
//				String name=c.getClassname();
//				String teacher=c.getTeacher();
				teacher=c.getTeacher();
				c_name=c.getClassname();
				if(!TextUtils.isEmpty(c_name)){
					title.setText(c_name);
					addkc_name.setText(c_name);
				}
				if(!TextUtils.isEmpty(teacher))
					addkc_teacher.setText(c.getTeacher());
			}

		}
		if(course!=null){
			mData=getTimeList(course);
			title.setText(course.getClassname());
			String name=course.getClassname();
			if(!TextUtils.isEmpty(name)){
				addkc_name.setText(course.getClassname());
			}

			addkc_teacher.setText(course.getTeacher());
		}
		//设置课程时段的listview的adapter
		adapter=new CourseTimeAdapter(context, R.layout.item_course_detail, mData);
		listview.setAdapter(adapter);
	}

	//获取课程的详情
	//将课程根据时段分成多个Course对象
	private List<Course> getTimeList(Course course) {
		// TODO Auto-generated method stub
		List<Course> list=new ArrayList<>();
		teacher=course.getTeacher();
		c_name=course.getClassname();
		try {
			JSONArray json=new JSONArray(course.getInfo());
			for (int i = 0; i < json.length(); i++) {
				Course c=new Course();
				JSONObject detail=json.getJSONObject(i);
				c.setClassname(c_name);
				c.setTeacher(teacher);
				c.setClassid(id);
				c.setLoction(detail.getString("place"));
				c.setTime(detail.getString("time"));
				c.setWeek(detail.getString("week"));
				c.setDay(detail.getInt("day"));
				c.setExtra_ID(i);
				list.add(c);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	private void initData() {
		// TODO Auto-generated method stub	
		if(!TextUtils.isEmpty(id)){
			CourseDao dao=new CourseDao(context);
			mData=dao.getCourseList(id);
		}

	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.course_edit_back_iv:
				Activity_Course_Edit.this.finish();
				break;
			case R.id.addkc_ok://完成课程的添加
				List<Course> list=adapter.getData();
				List<Course> c_list=new ArrayList<>();
				String name=addkc_name.getText().toString();
				String c_teacher=addkc_teacher.getText().toString();
				if(TextUtils.isEmpty(name)){
					Toast.makeText(context, "请填写课程名！", Toast.LENGTH_LONG).show();
					return;
				}
				if(TextUtils.isEmpty(c_teacher)){
					teacher="";
				}
				for (Course course : list) {
					if(!TextUtils.isEmpty(course.getTime())&&!TextUtils.isEmpty(course.getWeek()))
					{
						course.setClassid(id);
						course.setClassname(name);
						course.setTeacher(teacher);
						c_list.add(course);
					}
				}
				if(c_list.size()==0){
					Toast.makeText(context, "请完善课程信息！", Toast.LENGTH_LONG).show();
					return;
				}
				CourseDao dao=new CourseDao(context);
				dao.updateCourseList(c_list, id + "");
				if(Fragment_Addkc_Search.instance!=null)
				   Fragment_Addkc_Search.instance.refresh();
				setResult(Activity_Course_View.REQUEST, new Intent().putExtra("ID",id));
				finish();
				break;

			case R.id.ll_add_time://添加时段
				Course course=new Course();
				course.setClassid(id);
				course.setTeacher(teacher);
				course.setClassname(c_name);
				adapter.addItem(course);
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
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