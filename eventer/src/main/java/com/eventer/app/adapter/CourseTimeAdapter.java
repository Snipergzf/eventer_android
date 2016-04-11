package com.eventer.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.db.DBManager;
import com.eventer.app.entity.Course;
import com.eventer.app.main.MainActivity;
import com.eventer.app.util.WheelDialogClassHourShowUtil;
import com.eventer.app.util.WheelDialogTwoShowUtil;
import com.eventer.app.view.DialogView_ClassHour.onWheelBtnPosClick;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简单的好友Adapter实现
 *
 */
@SuppressLint("SetTextI18n")
public class CourseTimeAdapter  extends BaseAdapter {

	private Context context;                        //运行上下文
	private List<Course> listItems;
	private LayoutInflater mInflater;            //视图容器
	private int res;
	private Integer index = -1;


	public CourseTimeAdapter(Context context,int resource,List<Course>  listItems) {
		super();
		this.res = resource;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);   //创建视图容器并设置上下文
		this.listItems = listItems;

	}
	public void setData(List<Course>  listItems){
		this.listItems = listItems;
	}

	public List<Course> getData(){
		return listItems;
	}

	public void addItem(Course item){
		listItems.add(item);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return  listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}
	//提取出来方便点  
	public final class ViewHolder {
		public LinearLayout cancel;
		public TextView index;
		public TextView loction;
		public TextView time;
		public TextView week;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自动生成的方法存根		
		ViewHolder holder ;
		Course item =  listItems.get(position);
		final int loc = position;
		if (convertView == null) {
			convertView = mInflater.inflate(res, null);
			holder=new ViewHolder();
			holder.cancel=(LinearLayout)convertView.findViewById(R.id.ll_cancel);
			holder.time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.week=(TextView) convertView.findViewById(R.id.tv_week);
			holder.loction=(TextView) convertView.findViewById(R.id.tv_location);
			holder.index=(TextView)convertView.findViewById(R.id.tv_index);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder)convertView.getTag();
//			holder.loction.setTag(position);
		}
		holder.loction.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					index = (Integer) v.getTag();
				}
				return false;
			}
		});
		class MyTextWatcher implements TextWatcher {
			public MyTextWatcher(ViewHolder holder) {
				mHolder = holder;
			}
			private ViewHolder mHolder;
			@Override
			public void onTextChanged(CharSequence s, int start,
									  int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start,
										  int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if (s != null && !"".equals(s.toString())) {
					int position = (Integer) mHolder.loction.getTag();
					listItems.get(position).setLoction(s.toString());
				}
			}
		}
		holder.loction.addTextChangedListener(new MyTextWatcher(holder));
		holder.loction.setTag(position);
		final View view = convertView;
		if(position==0){
			holder.cancel.setVisibility(View.GONE);
		}else{
			holder.cancel.setVisibility(View.VISIBLE);
		}
		holder.index.setText("其他时段"+(position));
		String c_place=item.getLoction();
		int c_day=item.getDay();
		String c_time=item.getTime();
		String c_week=item.getWeek();
		if(!TextUtils.isEmpty(c_time)&&!TextUtils.isEmpty(c_week)){
			String time;
			String week="";
			String[] weeklist=c_week.split(",");
			for (String aWeeklist : weeklist) {
				week += aWeeklist + "周 ";
			}
			String day=context.getResources().getStringArray(R.array.weeks)[c_day];
			time=day+" "+c_time+"节";
			if(!TextUtils.isEmpty(c_place)){
				holder.loction.setText(c_place);
			}
			holder.time.setText(time);
			holder.week.setText(week);
		}else{
			holder.loction.setText("");
			holder.time.setText("");
			holder.time.setHint("请选择上课周数");
			holder.week.setText("");
			holder.week.setHint("请选择上课节数");
		}
//		holder.loction.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				if(v.hasFocus()){
//					focus_index=loc;
//					Log.e("1",loc+"focus-------------------");
//				}
//			}
//		});
//		holder.loction.addTextChangedListener(new TextWatcher() {
//			String text="";
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//				text=s.toString();
//			}
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				Log.e("1",loc+"focus-------------------"+focus_index+s.toString());
//				if(focus_index==loc){
//					final TextView tv=(TextView) view.findViewById(R.id.tv_location);
//					listItems.get(loc).setLoction(tv.getText().toString());
//				}
//			}
//		});

		holder.time.setOnClickListener(new OnClickListener() {
			int index1=0,index2=0,index3=0;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String[] weeks= context.getResources().getStringArray(R.array.weeks);
				DBManager dbHelper1;
				dbHelper1 = new DBManager(context);
				dbHelper1.openDatabase();
				Cursor c=dbHelper1.findOne(true, "dbCourseSetting", new String[]{"MaxHour"}, null, null, null, null, null, null);
				int classTotal=c.getInt(c.getColumnIndex("MaxHour"));
				String[] FromClassNum=new String[classTotal];
				String[] ToClassNum=new String[classTotal];
				for(int i=0;i<classTotal;i++){
					FromClassNum[i]="第"+(i+1)+"节";
				}
				for(int i=0;i<classTotal;i++){
					ToClassNum[i]="到"+(i+1)+"节";
				}
				dbHelper1.closeDatabase();

				Map<Integer,String[]> data=new HashMap<>();
				data.put(1, weeks);
				data.put(2, FromClassNum);
				data.put(3, ToClassNum);
				final WheelDialogClassHourShowUtil wheelUtil1;
				wheelUtil1= new WheelDialogClassHourShowUtil(context,MainActivity.instance.getWindowManager().getDefaultDisplay(),data, "编辑课程的上课时间");
				index1=0;
				index2=0;
				index3=0;
				String time=listItems.get(loc).getTime();
				int day=listItems.get(loc).getDay();

				if(!TextUtils.isEmpty(time)&&day>-1){
					index1=day;
					String[] nums=time.split("-");
					index2=Integer.parseInt(nums[0])-1;
					index3=Integer.parseInt(nums[1])-1;
				}
				wheelUtil1.setWheelHint(index1,index2,index3);
				wheelUtil1.dialogView.setBtnPosClick(new onWheelBtnPosClick() {
					@Override
					public void onClick(String[] text, int[] position) {
						// TODO Auto-generated method stub
						wheelUtil1.dissmissWheel();
						int day=position[0];
						String time=(position[1]+1)+"-"+(position[2]+1);
						listItems.get(loc).setDay(day);
						listItems.get(loc).setTime(time);
						final TextView tv=(TextView) view.findViewById(R.id.tv_time);
						tv.setText(context.getResources().getStringArray(R.array.weeks)[day]+" "+time+"节");
						tv.setHint("");
						//notifyDataSetChanged();	
					}
				});
				wheelUtil1.showWheel();
			}
		});

		holder.week.setOnClickListener(new OnClickListener() {
			int index1=0,index2=0;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DBManager dbHelper2;
				dbHelper2 = new DBManager(context);
				dbHelper2.openDatabase();
				Cursor c2=dbHelper2.findOne(true, "dbCourseSetting", new String[]{"TotalWeek"}, null, null, null, null, null, null);
				int weekNum=c2.getInt(c2.getColumnIndex("TotalWeek"));
				dbHelper2.closeDatabase();

				String[] FromWeekNum=new String[weekNum];
				String[] ToWeekNum=new String[weekNum];
				for(int i=0;i<weekNum;i++){
					FromWeekNum[i]="第"+(i+1)+"周";
				}
				for(int i=0;i<weekNum;i++){
					ToWeekNum[i]="到"+(i+1)+"周";
				}

				Map<Integer,String[]> data1=new HashMap<>();
				data1.put(1, FromWeekNum);
				data1.put(2, ToWeekNum);
				final WheelDialogTwoShowUtil wheelUtil2;
				wheelUtil2= new WheelDialogTwoShowUtil(context,MainActivity.instance.getWindowManager().getDefaultDisplay(),data1, "选择课程的周数");
				String week=listItems.get(loc).getWeek();
				if(!TextUtils.isEmpty(week)){

					String Num=week.split(",")[0];
					String[] nums=Num.split("-");
					if(nums.length==2){
						index1=Integer.parseInt(nums[0])-1;
						index2=Integer.parseInt(nums[1])-1;
					}else if(nums.length==1){
						index1=Integer.parseInt(nums[0])-1;
						index2=Integer.parseInt(nums[0])-1;
					}

				}
				wheelUtil2.setWheelHint(index1,index2);
				wheelUtil2.setConnectable(true);
				wheelUtil2.dialogView.setBtnPosClick(new com.eventer.app.view.DialogView_Two.onWheelBtnPosClick() {
					@Override
					public void onClick(String text, int[] position) {
						// TODO Auto-generated method stub
						wheelUtil2.dissmissWheel();
						String week=(position[0]+1)+"-"+(position[1]+1);
						listItems.get(loc).setWeek(week);
						final TextView tv=(TextView)view.findViewById(R.id.tv_week);
						tv.setText(week+"周");
						tv.setHint("");
						//notifyDataSetChanged();			
					}
				});
				wheelUtil2.showWheel();
			}
		});

		holder.cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(listItems.size() > loc+1){
					listItems.remove(loc);
				}

				notifyDataSetChanged();
			}
		});
		return view;
	}



}
