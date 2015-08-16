package com.eventer.app.other;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
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
import com.eventer.app.widget.ListViewForScrollView;


public class Course_edit extends Activity  implements OnClickListener{


    private TextView add_commit,title;
    private EditText addkc_name,addkc_teacher;
    private LinearLayout ll_add_time;
    private ListViewForScrollView listview;
    private CourseTimeAdapter adapter;
	private ImageView back_img;
	private int id;
	private List<Course> mData;
	private Context context;
	public static Course_edit instance;
	private String teacher;
	private String c_name;
	private int classid;
	private Course course;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_info_edit);	    	    
	    context=Course_edit.this;	
	    instance=this;	    
	    id=getIntent().getIntExtra("CourseID", -1);
	    course=getIntent().getParcelableExtra("c_detail");
	    initView();
	  
	}
	

	


	private void initView() {
		// TODO Auto-generated method stub
		back_img=(ImageView)findViewById(R.id.course_edit_back_iv);
	    add_commit=(TextView)findViewById(R.id.addkc_ok);
	    addkc_name=(EditText)findViewById(R.id.addkc_name_edit);    
	    addkc_teacher=(EditText)findViewById(R.id.addkc_teacher_edit);   	    
		listview=(ListViewForScrollView)findViewById(R.id.listview);
		mData=new ArrayList<Course>();	
		title=(TextView)findViewById(R.id.course_edit_title);
		ll_add_time=(LinearLayout)findViewById(R.id.ll_add_time);
		ll_add_time.setOnClickListener(this);
		add_commit.setOnClickListener(this);
	    back_img.setOnClickListener(this);
	    if(id!=-1){
	    	initData();
	    	if(mData.size()!=0){
	    		Course c=mData.get(0);
	    		String name=c.getClassname();
	    		String teacher=c.getTeacher();
	    		if(!TextUtils.isEmpty(name)){
	    			title.setText(name);
					addkc_name.setText(name);
	    		}
	    		if(!TextUtils.isEmpty(teacher))
				addkc_teacher.setText(c.getTeacher());
	    	}
	    	
	    }
	    if(course!=null){	    	
	    	mData=getTimeList(course);
	    	title.setText(course.getClassname());
			addkc_name.setText(course.getClassname());
			addkc_teacher.setText(course.getTeacher());
	    }
	    adapter=new CourseTimeAdapter(context, R.layout.item_course_detail, mData);
		listview.setAdapter(adapter);
	}





	private List<Course> getTimeList(Course course) {
		// TODO Auto-generated method stub
		List<Course> list=new ArrayList<Course>();
		teacher=course.getTeacher();
    	c_name=course.getClassname();
    	classid=course.getClassid();  	
    	try {
			JSONObject json=new JSONObject(course.getInfo());
			Iterator<String> it=json.keys(); 
			int index=1;
        	while(it.hasNext()){
        		Course c=new Course();
        		JSONObject detail=json.getJSONObject(it.next().toString());
        		c.setClassname(c_name);
        		c.setTeacher(teacher);
        		c.setClassid(classid);
        		c.setLoction(detail.getString("place"));
        		c.setTime(detail.getString("time"));
        		c.setWeek(detail.getString("week"));
        		c.setDay(detail.getInt("day"));
        		c.setExtra_ID(index);
        		list.add(c);
        		index++;
        	}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}





	private void initData() {
		// TODO Auto-generated method stub	
		if(id!=-1){
			CourseDao dao=new CourseDao(context);
			mData=dao.getCourseList(id+"");
		}
	    
	}
	
	



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		    case R.id.course_edit_back_iv:
		    	Course_edit.this.finish();
			    break;
			case R.id.addkc_ok:
				List<Course> list=adapter.getData();
				List<Course> c_list=new ArrayList<Course>();
				String name=addkc_name.getText().toString();
				String c_teacher=addkc_teacher.getText().toString();
				if(TextUtils.isEmpty(name)){
					Toast.makeText(context, "请填写课程名！", Toast.LENGTH_LONG).show();
					return;
				}
				if(TextUtils.isEmpty(c_teacher)){
					c_teacher="";
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
				Fragment_Addkc_Search.instance.refresh();
				finish();
				break;

			case R.id.ll_add_time:
				Course course=new Course();
				course.setClassid(classid);
				course.setTeacher(teacher);
				course.setClassname(c_name);
				adapter.addItem(course);
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
		}
		
	}

	
}
