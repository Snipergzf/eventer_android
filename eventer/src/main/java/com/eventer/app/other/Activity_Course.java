package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.adapter.ClassNameAdapter;
import com.eventer.app.db.ClassInfoDao;
import com.eventer.app.db.DBManager;
import com.eventer.app.entity.ClassInfo;
import com.eventer.app.main.BaseActivity;
import com.eventer.app.view.CourseView;
import com.eventer.app.view.PopMenu;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hirondelle.date4j.DateTime;

/**
 * Created by LiuNana on 2015/12/25.
 * check whether the internet is available while the net changed
 */
@SuppressLint("SetTextI18n")
public class Activity_Course extends BaseActivity implements OnClickListener {
    private CourseView courseView;
    private Context context;
    private List<ClassInfo> classList;
    private List<ClassInfo> AllClassList;
    private int NowWeek = 1;
    private int inentWeek = -1;
    private int showWeek = 1;
    private DateTime startDay;
    public static int totalWeek;
    private int startWeekday,showType,classTotal;
    public static int COURSE_SETTING = 0x11;
    private LinearLayout  weekinfo_ll;
    public TextView weekinfo_tv;
    SimpleDateFormat sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        context=this;
        inentWeek = getIntent().getIntExtra("week",-1);
        initView();
    }


    /***
     * 初始化控件，给控件添加事件响应
     */
    private void initView() {
        courseView = (CourseView) findViewById(R.id.courseview);
        weekinfo_ll = (LinearLayout) findViewById(R.id.weekinfo_ll);
        ImageView iv_add = (ImageView) findViewById(R.id.iv_add);
        ImageView iv_setting = (ImageView) findViewById(R.id.iv_setting);
        weekinfo_tv=(TextView) findViewById(R.id.weekinfo_tv);
        weekinfo_ll.setOnClickListener(this);
        iv_add.setOnClickListener(this);
        iv_setting.setOnClickListener(this);
        weekinfo_tv.setText("第" + NowWeek + "周");
        showWeek = NowWeek;
        courseView
                .setOnItemClassClickListener(new CourseView.OnItemClassClickListener() {

                    @Override
                    public void onClick(List<ClassInfo> list) {
                        if (list != null) {
                            if (list.size() == 1) {
                                Intent intent = new Intent();
                                intent.setClass(context, Activity_Course_View.class);
                                intent.putExtra("CourseID", list.get(0).getClassid());
                                startActivity(intent);
                            } else if (list.size() > 1) {
                                showMyDialog(list);
                            }

                        }

                    }
                });


    }

    /**
     * 页面控件的点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //添加按钮
            case R.id.iv_add:
                Intent intent1=new Intent();
                intent1.setClass(context, Activity_AddCourse.class);
                startActivityForResult(intent1, COURSE_SETTING);
                break;
            //课程视图的课程设置
            case R.id.iv_setting:
                Intent intent=new Intent();
                intent.setClass(context, Activity_Course_Setting.class);
                startActivityForResult(intent, COURSE_SETTING);
                break;
            //课程视图的星期选中
            case R.id.weekinfo_ll:
                PopMenu addPopWindow = new PopMenu(context);
                for(int i=1; i<totalWeek;i++){
                    if(i!=NowWeek){
                        addPopWindow.addItem("第"+i+"周");
                    }else{
                        addPopWindow.addItem("第"+i+"周"+"(本周)");
                    }
                }
                addPopWindow.setChangeListener(new PopMenu.ChangeListener() {
                    @Override
                    public void onChange(int position) {
                        changeWeek(position);
                    }
                });
                addPopWindow.setCheckedItem(showWeek);
                addPopWindow.showAsDropDown(weekinfo_ll);
            default:
                break;
        }
    }

    /**
     * 刷新页面
     */
    private void refreshView() {
        initSetting();
        initClassData();
        getClassData();
        Map<String, Object> params=new HashMap<>();
        params.put("classTotal", classTotal);
        params.put("startWeekday", startWeekday);
        params.put("showType", showType);
        params.put("StartDay", startDay.toString());
        courseView.initSetting(params);
        courseView.setWeek(NowWeek);
        if(showWeek <= 0){
            showWeek = NowWeek;
        }
        if(inentWeek != -1){
            showWeek = inentWeek;
            inentWeek = -1;
        }
        courseView.setClassList(classList);// 将课程信息导入到课表中
        changeWeek(showWeek);
    }


    /***
     * 初始化课表设置
     * 如果是初次使用，初始化课表设置，将设置信息存入数据库，并初始化课表参数
     * 如果已经有课表设置，获取设置，初始化课表参数
     */
    private void initSetting(){
        DBManager dbHelper;
        dbHelper = new DBManager(context);
        dbHelper.openDatabase();
        Cursor c=dbHelper.findList(true, "dbCourseSetting", null,
                null, null, null, null,null,null);

        String   time = sDateFormat.format(new Date());
        DateTime Today = new DateTime(time);
        int weekday = Today.getWeekDay();
        boolean isNew = true;
        while (c.moveToNext()){
            isNew = false;
            String start = c.getString(c.getColumnIndex("StartDay"));
            startDay = new DateTime(start);
            showType = c.getInt(c.getColumnIndex("ShowType"));
            totalWeek = c.getInt(c.getColumnIndex("TotalWeek"));
            classTotal = c.getInt(c.getColumnIndex("MaxHour"));
            startWeekday = c.getInt(c.getColumnIndex("StartWeekday"));
            int diff = startDay.numDaysFrom(Today);
            if(diff >= 0){
                NowWeek = diff / 7 + 1;
                if(NowWeek > totalWeek){
                    totalWeek = NowWeek;
                }
                weekinfo_tv.setText("第" + NowWeek + "周");
            }
        }

        if(isNew){
            startDay = Today.minusDays( weekday - 2 );
            showType = 0;
            startWeekday = 1;
            classTotal = 12;
            totalWeek = 24;
            int month = startDay.getMonth();
            int year = startDay.getYear();
            String terminfo;
            if(month < 9){
                terminfo = ( year - 1 ) + "-" + year + " 春季学期";
            }else{
                terminfo = year + "-" + ( year + 1 ) + " 秋季学期";
            }
            ContentValues cv = new ContentValues();
            cv.put("TermInfo", terminfo);
            cv.put("StartDay", startDay.toString());
            cv.put("TotalWeek", 24);
            cv.put("MaxHour", 12);
            cv.put("ShowType", 0);
            cv.put("StartWeekday", 0);
            cv.put("Course_bg", "");
            dbHelper.insert("dbCourseSetting", cv);
        }
        dbHelper.closeDatabase();
        showWeek = NowWeek;
    }


    /***
     * 获取所有课程
     */
    private void initClassData() {
        AllClassList = new ArrayList<>();
        ClassInfoDao dao = new ClassInfoDao(context);
        AllClassList = dao.getClassInfoList();
    }

    /***
     * 获取当前选中的周数的课程
     */
    private void getClassData(){
        classList = new ArrayList<>();
        for (ClassInfo c_info : AllClassList) {
            if(c_info.getWeeks().contains( showWeek )){
                classList.add(c_info);
            }
        }
    }

    /***
     * 切换课表的周次
     * @param week week index
     */
    public void changeWeek(int week){
        courseView.setWeek(week);
        if(week == NowWeek){
            weekinfo_tv.setText("第"+week+"周");
        }else{
            weekinfo_tv.setText("第"+week+"周"+"(非本周)");
        }
        showWeek = week;
        getClassData();
        courseView.setClassList(classList);// 将课程信息导入到课表中

    }

    /***
     * 当点击的格子对应多门课程时，弹出Dialog
     * 显示多门课程
     * @param courseList 课程列表
     */
    private void showMyDialog(final List<ClassInfo> courseList) {

        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window window = dlg.getWindow();
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.click_class_list);
        ListView listView = (ListView) window.findViewById(R.id.list);
        ClassNameAdapter adapter = new ClassNameAdapter(context, courseList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassInfo classInfo = courseList.get(position);
                try {
                    dlg.cancel();
                    Intent intent = new Intent();
                    intent.setClass(context, Activity_Course_View.class);
                    intent.putExtra("CourseID", classInfo.getClassid());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CourseScreen");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CourseScreen");
        MobclickAgent.onResume(this);
        refreshView();
    }


    @Override
    public void onStart(){
        super.onStart();

    }

}
