package com.eventer.app.other;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eventer.app.R;
import com.eventer.app.adapter.CourseTimeAdapter;
import com.eventer.app.db.CourseDao;
import com.eventer.app.entity.Course;
import com.eventer.app.ui.base.BaseActivity;
import com.eventer.app.widget.ListViewForScrollView;

import java.util.ArrayList;
import java.util.List;

public class Activity_AddCourse_Manually extends BaseActivity implements View.OnClickListener {

    private EditText addkc_name,addkc_teacher;
    private Context context;
    private CourseTimeAdapter adapter;
    private List<Course> mData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcourse_manually);
        setBaseTitle(getString(R.string.add_course_manually));
        context=this;
        initView();
    }

    private void initView() {
        addkc_name=(EditText) findViewById(R.id.addkc_name_edit);
        addkc_teacher=(EditText) findViewById(R.id.addkc_teacher_edit);
        TextView tv_finish = (TextView) findViewById(R.id.tv_add_finish);
        ListViewForScrollView listview = (ListViewForScrollView) findViewById(R.id.listview);
        LinearLayout ll_add_time = (LinearLayout) findViewById(R.id.ll_add_time);
        ll_add_time.setOnClickListener(this);
        tv_finish.setOnClickListener(this);

        Course course=new Course();
        mData.add(course);
        adapter=new CourseTimeAdapter(context, R.layout.item_course_detail, mData);
        listview.setAdapter(adapter);
    }

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
        finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ll_add_time:
                Course course=new Course();
                adapter.addItem(course);
                adapter.notifyDataSetChanged();
                break;
            case R.id.tv_add_finish:
                saveCourse();
                break;
            default:
                break;
        }
    }
}
