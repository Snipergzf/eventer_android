package com.eventer.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.other.Activity_AddCourse;
import com.eventer.app.other.Activity_Course_Edit;
import com.eventer.app.other.Activity_Course_Setting;
import com.eventer.app.view.PopMenu;
import com.umeng.analytics.MobclickAgent;


public  class ScheduleFragment extends Fragment  implements OnClickListener{

	private Fragment[] fragments;
	public CalendarFragment calendarfragment;
	private CourseFragment coursefragment;
	private int index;
	private int currentIndex;
	private Spinner view_index;
	private RelativeLayout weekinfo_rl;
	private LinearLayout weekinfo_ll;
	private ImageView iv_add,iv_setting;
	public TextView weekinfo_tv;
	private LinearLayout ll_list;
	public static ScheduleFragment instance=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_schedual, container, false);
		instance=this;
		initView(rootView);
		return rootView;
	}

	private void initView(View view){
		//初始化日程视图和课程视图
		calendarfragment = new CalendarFragment();
		coursefragment = new CourseFragment();

		fragments = new Fragment[] {calendarfragment,coursefragment};

		// 添加显示第一个fragment
		getChildFragmentManager().beginTransaction()
				.add(R.id.fragment_container, fragments[0])
				.add(R.id.fragment_container, fragments[1])
				.hide(fragments[1]).show(fragments[0]).commit();

		view_index=(Spinner)view.findViewById(R.id.spinner_view);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.view_schedual,R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		view_index.setAdapter(adapter);
		view_index.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
				index=arg2;
				arg0.setVisibility(View.VISIBLE);
				if (currentIndex != index) {
					changeViewByIndex(index);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		view_index.setSelection(0);

		weekinfo_ll=(LinearLayout)view.findViewById(R.id.weekinfo_ll);
		weekinfo_rl=(RelativeLayout)view.findViewById(R.id.weekinfo_rl);
		iv_add=(ImageView)view.findViewById(R.id.iv_add);
		iv_setting=(ImageView)view.findViewById(R.id.iv_setting);
		weekinfo_tv=(TextView)view.findViewById(R.id.weekinfo_tv);
		ll_list=(LinearLayout)view.findViewById(R.id.ll_list);
		weekinfo_ll.setOnClickListener(this);
		iv_add.setOnClickListener(this);
		iv_setting.setOnClickListener(this);

	}
	/**
	 * 日程视图和课程视图的切换
	 * @param index2
	 */
	protected void changeViewByIndex(int index2) {
		// TODO Auto-generated method stub
		switch (index2) {
			case 0:
				weekinfo_rl.setVisibility(View.GONE);
				iv_setting.setVisibility(View.GONE);
				iv_add.setVisibility(View.GONE);
				ll_list.setVisibility(View.GONE);
				break;
			case 1:
				weekinfo_rl.setVisibility(View.VISIBLE);
				iv_setting.setVisibility(View.VISIBLE);
				iv_add.setVisibility(View.VISIBLE);
				ll_list.setVisibility(View.GONE);
				break;
			default:
				break;
		}
		FragmentTransaction trx = getChildFragmentManager()
				.beginTransaction();
		trx.hide(fragments[currentIndex]);
		if (!fragments[index].isAdded()) {
			trx.add(R.id.fragment_container, fragments[index]);
		}
		trx.show(fragments[index]).commit();

		currentIndex=index;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			//添加按钮
			case R.id.iv_add:
				if(index==1){//添加课程
					Intent intent1=new Intent();
					intent1.setClass(getActivity(), Activity_AddCourse.class);
					startActivityForResult(intent1, CourseFragment.COURSE_SETTING);
				}
				break;
			//课程视图的课程设置
			case R.id.iv_setting:
				Intent intent=new Intent();
				intent.setClass(getActivity(), Activity_Course_Setting.class);
				startActivityForResult(intent, CourseFragment.COURSE_SETTING);
				break;
			//课程视图的星期选中
			case R.id.weekinfo_ll:
				PopMenu addPopWindow = new PopMenu(getActivity());
				for(int i=1; i<CourseFragment.totalWeek;i++){
					if(i!=CourseFragment.NowWeek){
						addPopWindow.addItem("第"+i+"周");
					}else{
						addPopWindow.addItem("第"+i+"周"+"(本周)");
					}
				}
				addPopWindow.setCheckedItem(CourseFragment.showWeek);
				addPopWindow.showAsDropDown(weekinfo_rl);
			default:
				break;
		}
		if (currentIndex != index) {
			FragmentTransaction trx = getChildFragmentManager()
					.beginTransaction();
			trx.hide(fragments[currentIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();

			currentIndex=index;
		}
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MainScreen"); //统计页面
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}
}
  		
     
