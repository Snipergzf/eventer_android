package com.eventer.app.other;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.db.CourseDao;
import com.eventer.app.db.DBManager;
import com.eventer.app.entity.Course;
import com.umeng.analytics.MobclickAgent;


public class Activity_Course_View extends Activity  implements OnClickListener{

	private ImageView back_img;
	private TextView kc_delete,kc_name,kc_teacher,kc_info;
	private Button edit_course_btn;
	private LinearLayout ll_timeblock;
	private List<Course> mData=new ArrayList<Course>();
	public static int REQUEST=0x34;
	private int courseid;
	private Context	context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_info_view);
		courseid=getIntent().getIntExtra("CourseID", -1);
		back_img=(ImageView)findViewById(R.id.kc_view_back);
		kc_delete=(TextView)findViewById(R.id.kc_delete_tv);
		kc_name=(TextView)findViewById(R.id.view_kcname_tv);
		kc_teacher=(TextView)findViewById(R.id.view_kcteacher_tv);
		kc_info=(TextView)findViewById(R.id.view_kcinfo_tv);
		edit_course_btn=(Button)findViewById(R.id.edit_course_info);
		ll_timeblock=(LinearLayout)findViewById(R.id.ll_timeblock);
		context=Activity_Course_View.this;
		if(courseid!=-1){
			initView();
		}
		back_img.setOnClickListener(this);
		kc_delete.setOnClickListener(this);
		edit_course_btn.setOnClickListener(this);
	}


	private void initView(){
		CourseDao dao=new CourseDao(context);
		mData=dao.getCourseList(courseid+"");
		if(mData.size()>0){
			String name=mData.get(0).getClassname();
			String teacher=mData.get(0).getTeacher();
			if(!TextUtils.isEmpty(name)){
				kc_name.setText(name);
				kc_info.setText(name);
			}
			if(!TextUtils.isEmpty(teacher)){
				kc_teacher.setText(teacher);
			}
			for (int i=0;i<mData.size();i++) {
				View item =LayoutInflater.from(Activity_Course_View.this).inflate(R.layout.item_course_detail_view, null);
				LinearLayout info=(LinearLayout)item.findViewById(R.id.ll_info);
				if(i==0){
					info.setVisibility(View.GONE);
				}else{
					info.setVisibility(View.VISIBLE);
				}
				TextView tv_loc = (TextView) item.findViewById(R.id.tv_location);
				TextView tv_week = (TextView) item.findViewById(R.id.tv_week);
				TextView tv_time = (TextView) item.findViewById(R.id.tv_time);

				String week=mData.get(i).getWeek();
				String time=mData.get(i).getTime();
				String loc=mData.get(i).getLoction();
				int day=mData.get(i).getDay();
				if(!TextUtils.isEmpty(loc)){
					tv_loc.setText(loc);
				}
				if(!TextUtils.isEmpty(week)){
					tv_week.setText(week+"周");
				}
				if(!TextUtils.isEmpty(loc)){
					String[] weeks=context.getResources().getStringArray(R.array.weeks);
					tv_time.setText(weeks[day]+" "+time+"节");
				}
				ll_timeblock.addView(item);
			}
		}



	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch(v.getId()){
			case R.id.kc_view_back:
				this.finish();
				break;
			case R.id.kc_delete_tv:
				CourseDao dao=new CourseDao(context);
				dao.deleteCourse(courseid);
				this.finish();
				break;
			case R.id.edit_course_info:
				Intent intent=new Intent();
				intent.setClass(context, Activity_Course_Edit.class);
				intent.putExtra("CourseID", courseid);
				startActivityForResult(intent, REQUEST);
				break;
			default:
				break;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST&&data!=null)
		{
			int id=data.getIntExtra("ID", -1);
			Log.e("1", "138--view--"+id);
			if(id!=-1){
				courseid=id;
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
