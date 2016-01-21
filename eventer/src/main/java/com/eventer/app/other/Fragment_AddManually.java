package com.eventer.app.other;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eventer.app.R;
import com.eventer.app.adapter.CourseTimeAdapter;
import com.eventer.app.db.CourseDao;
import com.eventer.app.entity.Course;
import com.eventer.app.widget.ListViewForScrollView;

import java.util.ArrayList;
import java.util.List;


public  class Fragment_AddManually extends Fragment implements OnClickListener{

	private RelativeLayout title;
	private EditText addkc_name,addkc_teacher;
	private Context context;
	private LinearLayout ll_add_time;
	private ListViewForScrollView listview;
	private CourseTimeAdapter adapter;
	private List<Course> mData;

	public static Fragment_AddManually instance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.course_info_edit, container, false);
		instance=this;
		context=getActivity();
		initView(rootView);
		return rootView;
	}

	public void initView(View view){

		addkc_name=(EditText)view.findViewById(R.id.addkc_name_edit);
		addkc_teacher=(EditText)view.findViewById(R.id.addkc_teacher_edit);
		listview=(ListViewForScrollView)view.findViewById(R.id.listview);
		mData=new ArrayList<>();
		title=(RelativeLayout)view.findViewById(R.id.rl_title);
		ll_add_time=(LinearLayout)view.findViewById(R.id.ll_add_time);
		ll_add_time.setOnClickListener(this);

		title.setVisibility(View.GONE);
		Course course=new Course();
		mData.add(course);
		adapter=new CourseTimeAdapter(context, R.layout.item_course_detail, mData);
		listview.setAdapter(adapter);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	//将课程保存数据库
	public void saveCourse(){
		List<Course> list=adapter.getData();
		// TODO Auto-generated method stub
		List<Course> c_list=new ArrayList<>();
		int classid=(int)(System.currentTimeMillis()/1000);
		String name=addkc_name.getText().toString();
		String teacher=addkc_teacher.getText().toString();
		if(TextUtils.isEmpty(name)){
			Toast.makeText(context, "请填写课程名！", Toast.LENGTH_LONG).show();
			return;
		}
		if(TextUtils.isEmpty(teacher)){
			teacher="";
		}
		for (Course course : list) {
			if(!TextUtils.isEmpty(course.getTime())&&!TextUtils.isEmpty(course.getWeek()))
			{
				course.setClassid(classid);
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
		dao.saveCourseList(c_list);
		getActivity().finish();
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub		
		switch(v.getId()){
			case R.id.ll_add_time:
				Course course=new Course();
				adapter.addItem(course);
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
		}
	}

}
  		
     
