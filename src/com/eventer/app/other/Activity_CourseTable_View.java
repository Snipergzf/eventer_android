package com.eventer.app.other;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.eventer.app.R;
import com.eventer.app.adapter.CourseAdapter;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.CourseDao;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.entity.Course;
import com.eventer.app.view.TitleBar;
import com.umeng.analytics.MobclickAgent;

public class Activity_CourseTable_View extends Activity implements
		OnClickListener{
    private ImageView iv_back;
    private TextView tv_add,tv_title;  
    private Context context;
    private List<Course> mData=new ArrayList<Course>();
    private ListView listview;
    private CourseAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coursetable_view);		
		context=Activity_CourseTable_View.this;
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		tv_add=(TextView)findViewById(R.id.tv_add_finish);
		tv_title=(TextView)findViewById(R.id.tv_title);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        listview=(ListView)findViewById(R.id.listview);
        mData=getIntent().getParcelableArrayListExtra("courseList");
        adapter=new CourseAdapter(context, R.layout.item_course_list, mData);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.putExtra("c_detail", mData.get(position));
				intent.setClass(Activity_CourseTable_View.this, Activity_Course_Edit.class);
				startActivityForResult(intent, 11);
				
			}
		});
		String info=getIntent().getStringExtra("class");
		info="������ <b><em>"+info+"</em></b> �Ŀα�Ŀγ��б�";
		tv_title.setText(Html.fromHtml(info));
		iv_back.setOnClickListener(this);
		tv_add.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_add_finish:
			showMyDialog("��ӿγ�");	    
		    break;
		case R.id.iv_back:
			finish();
			break;
		default:
			break;
		}
	}
	
	private void showMyDialog(String title) {

        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window window = dlg.getWindow();
        // ���ô��ڵ�����ҳ��,shrew_exit_dialog.xml�ļ��ж���view����
        window.setContentView(R.layout.alertdialog);
        window.findViewById(R.id.ll_title).setVisibility(View.VISIBLE);

        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText(title);
        TextView tv_content1 = (TextView) window.findViewById(R.id.tv_content1);
         tv_content1.setText("ֱ����������α�");


        tv_content1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {

                CourseDao dao=new CourseDao(context);
                dao.saveCourseListByInfo(mData);
                dlg.cancel();
                Activity_AddCourseTable.instance.back();
                Activity_AddCourse.instance.back();
                finish();
            }
        });
        TextView tv_content2 = (TextView) window.findViewById(R.id.tv_content2);
        tv_content2.setText("���ԭ�пγ�����ӿα�");
        tv_content2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	 CourseDao dao=new CourseDao(context);
            	 dao.deleteAllCourse();
                 dao.saveCourseListByInfo(mData);
                 dlg.cancel();
                 Activity_AddCourseTable.instance.back();
                 Activity_AddCourse.instance.back();
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
