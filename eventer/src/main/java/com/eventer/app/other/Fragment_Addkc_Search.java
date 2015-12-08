package com.eventer.app.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.adapter.CourseAdapter;
import com.eventer.app.db.CourseDao;
import com.eventer.app.entity.Course;
import com.eventer.app.http.HttpUnit;


public  class Fragment_Addkc_Search extends Fragment implements OnClickListener{

	private EditText et_search;
	private ListView listview;
	private Button btn_search,btn_add_table;

	private CourseAdapter adapter;
	private List<Course>  mData;
	public static List<Integer> ClassIdList;

	private Context context;
	public static Fragment_Addkc_Search instance;
	public static int REQUEST=0x38;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView=inflater.inflate(R.layout.fragment_course_search, container, false);
		mData=new ArrayList<Course>();
		context=getActivity();
		instance=this;
		initView(rootView);
		return rootView;
	}



	private void initView(View rootView) {
		// TODO Auto-generated method stub
		et_search=(EditText) rootView.findViewById(R.id.et_search);
		listview=(ListView)rootView.findViewById(R.id.listview);
		btn_search=(Button)rootView.findViewById(R.id.btn_search);
		btn_add_table=(Button)rootView.findViewById(R.id.btn_add_table);
		adapter=new CourseAdapter(context, R.layout.item_course_list, mData);
		listview.setAdapter(adapter);
		btn_search.setOnClickListener(this);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.putExtra("c_detail", mData.get(position));
				intent.setClass(getActivity(), Activity_Course_Edit.class);
				startActivityForResult(intent, 11);

			}
		});

		CourseDao dao =new CourseDao(context);
		ClassIdList=dao.getCourseIdList();
		btn_add_table.setOnClickListener(this);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	/**
	 * 获取课程列表
	 */
	private void refresh(String str) {
		mData.clear();;
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Constant.UID+"");
		params.put("search_name", str);
		GetCourseByHTTP(params);
	}

	public void refresh() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btn_search:
				String str=et_search.getText().toString();
				adapter.setHint(str);
				if(str!=null&&str!=""){
					refresh(str);
				}
				break;
			case R.id.btn_add_table:
				Intent intent=new Intent();
				intent.setClass(getActivity(), Activity_AddCourseTable.class);
				startActivityForResult(intent, REQUEST);
				break;

			default:
				break;
		}
	}


	/**
	 * 执行异步任务
	 *
	 * @param params
	 *
	 */
	public void GetCourseByHTTP(final Object... params) {
		new AsyncTask<Object, Object,List<Course>>() {
			@SuppressWarnings("unchecked")
			@Override
			protected List<Course> doInBackground(Object... params) {
				List<Course> list=new ArrayList<Course>();
				try {
					list=HttpUnit.sendCourseRequest((Map<String, String>) params[0]);
					return list;

				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Log.e("1", e.toString());
					return null;
				}
			}
			protected void onPostExecute(List<Course> list) {
				if(list!=null){
					mData=list;
				}else{
					mData=new ArrayList<Course>();
				}

				adapter.setData(mData);
				adapter.notifyDataSetChanged();
			}
		}.execute(params);}


}
  		
     
