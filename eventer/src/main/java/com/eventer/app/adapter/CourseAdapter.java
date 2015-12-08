package com.eventer.app.adapter;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.db.CourseDao;
import com.eventer.app.entity.Course;
import com.eventer.app.other.Fragment_Addkc_Search;

/**
 * 简单的好友Adapter实现
 *
 */
public class CourseAdapter  extends BaseAdapter {

	private Context context;                        //运行上下文
	private List<Course> listItems;
	private LayoutInflater mInflater;            //视图容器
	private boolean[] hasChecked;
	private int res;
	private String hint="";


	public CourseAdapter(Context context,int resource,List<Course>  listItems) {
		super();
		this.res = resource;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);   //创建视图容器并设置上下文
		this.listItems = listItems;

	}
	public void setData(List<Course>  listItems){
		this.listItems = listItems;
	}

	public void addItem(Course item){
		listItems.add(item);
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return  listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return null;
	}

	public void setHint(String hint){
		this.hint=hint;
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return 0;
	}
	//提取出来方便点  
	public final class ViewHolder {
		public TextView classname;
		public TextView teacher;
		public TextView loction;
		public TextView time;
		public TextView week;
		public TextView mclass;
		public Button addCourse;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自动生成的方法存根		
		ViewHolder holder = null;
		Course item =  listItems.get(position);
		final int loc=position;
		if (convertView == null) {
			convertView = mInflater.inflate(res, null);

		}else {
			holder = (ViewHolder)convertView.getTag();
		}
		final View view = convertView;
		if(holder==null){
			holder=new ViewHolder();
			holder.classname = (TextView) view.findViewById(R.id.title);
			holder.teacher = (TextView) view.findViewById(R.id.tv_teacher);
			holder.time = (TextView) view.findViewById(R.id.tv_classtime);
			holder.week=(TextView) view.findViewById(R.id.tv_week);
			holder.loction=(TextView) view.findViewById(R.id.tv_location);
			holder.addCourse=(Button)view.findViewById(R.id.btn_addcourse);
			holder.mclass=(TextView)view.findViewById(R.id.tv_class);
			view.setTag(holder);
		}

		String name=item.getClassname();
		String teacher=item.getTeacher();
		if(hint!=null&&hint!=""){
			if(name!=null){
				name=name.replace(hint, "<font color=" + "\"" + "#F89012" + "\">"   +hint + "</font>" );
			}else{
				name="";
			}
			name+="("+item.getMajor()+")";
			if(teacher!=null){
				teacher=teacher.replace(hint, "<font color=" + "\"" + "#F89012" + "\">"   +hint + "</font>" );
			}else{
				teacher="";
			}

		}
		holder.classname.setText(Html.fromHtml(name));

		holder.teacher.setText(Html.fromHtml(teacher));
		holder.mclass.setText(item.getS_class());
		final int id=item.getClassid();
		String detail=item.getInfo();
		String time="";
		String week="";
		String place="";
		try {
			JSONObject json=new JSONObject(detail);
			Iterator<String> it=json.keys();
			int index=0;
			while(it.hasNext()){
				if(index==0){
					JSONObject info=json.getJSONObject(it.next().toString());
					String[] weeklist=info.getString("week").split(",");
					for(int i=0;i<weeklist.length;i++){
						week+=weeklist[i]+"周 ";
					}
					String day=context.getResources().getStringArray(R.array.weeks)[info.getInt("day")];
					time=day+" "+info.getString("time")+"节";
					place=info.getString("place");
					index++;
				}else{
					week+="...";
					time+="...";
					place+="...";
					break;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		holder.time.setText(time);
		holder.week.setText(week);
		holder.loction.setText(place);

		if(Fragment_Addkc_Search.ClassIdList.contains(id)){
			holder.addCourse.setText("退出课程");
			holder.addCourse.setBackgroundResource(R.drawable.button_gray);
		}else{
			holder.addCourse.setText("加入课程");
			holder.addCourse.setBackgroundResource(R.drawable.button_blue);
		}
		holder.addCourse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!Fragment_Addkc_Search.ClassIdList.contains(id)){
					CourseDao dao=new CourseDao(context);
					dao.saveCourseByInfo(listItems.get(loc));
					Fragment_Addkc_Search.ClassIdList.add(id);
				}else{
					CourseDao dao=new CourseDao(context);
					dao.deleteCourse(listItems.get(loc).getClassid());
					Fragment_Addkc_Search.ClassIdList.remove((Object)id);
				}
				notifyDataSetChanged();
			}
		});
		return view;
	}

}