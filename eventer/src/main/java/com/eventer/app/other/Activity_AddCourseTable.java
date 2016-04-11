package com.eventer.app.other;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.MajorDao;
import com.eventer.app.entity.Course;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.widget.AbstractSpinerAdapter;
import com.eventer.app.widget.SpinerPopWindow;
import com.eventer.app.widget.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_AddCourseTable extends SwipeBackActivity implements
		OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener {
	private TextView  tv_year,tv_school,tv_major,tv_class;
	private TextView[] tv_list;
	private Context context;
	public static Activity_AddCourseTable instance;
	private int index;
	private List<String> valueList = new ArrayList<>();
	private List<String> yearList = new ArrayList<>();
	private String year;
	private String school;
	private String major;
	private String mclass;
	private MajorDao dao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addcoursetable);
		setBaseTitle("添加课表");
		context=Activity_AddCourseTable.this;
		instance=this;
		dao = new MajorDao(context);
		initData();
		initView();
	}

	private void initData() {
		LocalUserInfo userInfo = LocalUserInfo.getInstance(context);
		year = userInfo.getUserInfo("grade");
		if(TextUtils.isEmpty(year)){
			year="";
		}
		major = userInfo.getUserInfo("major");
		if(TextUtils.isEmpty(major)){
			major="";
		}
		school = userInfo.getUserInfo("school");
		if(TextUtils.isEmpty(school)){
			school="";
		}
		mclass = userInfo.getUserInfo("class");
		if(TextUtils.isEmpty(mclass)){
			mclass="";
		}
	}


	private void initView() {
		// TODO Auto-generated method stub
		tv_class=(TextView)findViewById(R.id.tv_class);
		tv_major=(TextView)findViewById(R.id.tv_major);
		tv_school=(TextView)findViewById(R.id.tv_school);
		tv_year=(TextView)findViewById(R.id.tv_year);
		tv_list=new TextView[]{tv_year,tv_school,tv_major,tv_class};
		Button btn_search = (Button) findViewById(R.id.btn_search_ctable);
		tv_class.setOnClickListener(this);
		tv_major.setOnClickListener(this);
		tv_school.setOnClickListener(this);
		tv_year.setOnClickListener(this);
		btn_search.setOnClickListener(this);
		tv_class.setText(mclass);
		tv_major.setText(major);
		tv_school.setText(school);
		tv_year.setText(year);
		String[] grade = getResources().getStringArray(R.array.grade);
		Collections.addAll(yearList, grade);
		mSpinerPopWindow = new SpinerPopWindow(this);
		mSpinerPopWindow.refreshData(valueList, 0);
		mSpinerPopWindow.setItemListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.tv_year:
				index = 0;
				valueList = yearList;
				mSpinerPopWindow.refreshData(valueList, 0);
				break;
			case R.id.tv_school:
				year = tv_year.getText().toString().trim();
				if (!TextUtils.isEmpty(year)){
					valueList = dao.getSchool(year);
					mSpinerPopWindow.refreshData(valueList, 0);
					index = 1;
				} else {
					Toast.makeText(context, "请先选择年级~", Toast.LENGTH_SHORT).show();
					index = -1;
				}

				break;
			case R.id.tv_major:

				year = tv_year.getText().toString().trim();
				school = tv_school.getText().toString().trim();
				if (!TextUtils.isEmpty(year) && !TextUtils.isEmpty(school)){
					valueList = dao.getMajor(year, school);
					mSpinerPopWindow.refreshData(valueList, 0);
					index=2;
				}  else {
					Toast.makeText(context, "请先选择年级和学院~", Toast.LENGTH_SHORT).show();
					index=-1;
				}
				break;
			case R.id.tv_class:

				year = tv_year.getText().toString().trim();
				school = tv_school.getText().toString().trim();
				major = tv_major.getText().toString().trim();
				if (!TextUtils.isEmpty(year) && !TextUtils.isEmpty(school) && !TextUtils.isEmpty(major)){
					valueList = dao.getClass(year, school, major);
					mSpinerPopWindow.refreshData(valueList, 0);
					index=3;
				}  else {
					Toast.makeText(context, "请先选择年级、学院和专业~", Toast.LENGTH_SHORT).show();
					index=-1;
				}
				break;
			case R.id.btn_search_ctable:

				index=-1;
				Map<String, String> params = new HashMap<>();
				year=tv_year.getText().toString();
				school = tv_school.getText().toString();
				major=tv_major.getText().toString();
				mclass=tv_class.getText().toString();
				if(!TextUtils.isEmpty(year)&&!TextUtils.isEmpty(school)&&!TextUtils.isEmpty(major)&&!TextUtils.isEmpty(mclass)){
					params.put("uid", Constant.UID+"");
					params.put("s_grade", year);
					params.put("s_faculty", school);
					params.put("s_specialty", major);
					params.put("s_class", mclass);
					GetCourseTableByHTTP(params);
				}else{
					Toast.makeText(context, "请完善课表信息！", Toast.LENGTH_LONG).show();
					index=-1;
				}

				break;
			default:
				index=-1;
				break;
		}
		if(index!=-1){
			showSpinWindow();
		}
	}

	private SpinerPopWindow mSpinerPopWindow;
	private void showSpinWindow(){
		Log.e("", "showSpinWindow");
		mSpinerPopWindow.setWidth(tv_list[index].getWidth());
		mSpinerPopWindow.showAsDropDown(tv_list[index]);
		mSpinerPopWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				tv_list[index].setSelected(false);
			}
		});
	}

	@Override
	public void onItemClick(int pos) {
		// TODO Auto-generated method stub
		if (pos >= 0 && pos <= valueList.size()){
			String value = valueList.get(pos);
			tv_list[index].setText(value);
			for(int i = index + 1; i < 4; i++){
				tv_list[i].setText("");
			}
		}
	}

	/**
	 * 执行异步任务
	 * 通过Http请求获取课表信息
	 * @param params course info
	 *
	 */
	public void GetCourseTableByHTTP(final Object... params) {
		new AsyncTask<Object, Object,List<Course>>() {
			@SuppressWarnings("unchecked")
			@Override
			protected List<Course> doInBackground(Object... params) {
				List<Course> list;
				try {
					list=HttpUnit.sendCourseTableRequest((Map<String, String>) params[0]);
					return list;
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Log.e("1", e.toString());
					return null;
				}
			}
			protected void onPostExecute(List<Course> list) {
				if(list!=null){
					Intent intent=new Intent();
					intent.setClass(Activity_AddCourseTable.this, Activity_CourseTable_View.class);
					intent.putParcelableArrayListExtra("courseList", (ArrayList<? extends Parcelable>) list);
					String info= major+" "+year+"级"+mclass;
					Log.e("1",school);
					intent.putExtra("class", info);
					startActivity(intent);
				}else{
					if(!Constant.isConnectNet){
						Toast.makeText(context,getText(R.string.no_network),Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(context,"找不到课表~\n当前课表可能还在输入中~",Toast.LENGTH_SHORT).show();
					}
				}

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

	public void back() {
		finish();
	}
}
