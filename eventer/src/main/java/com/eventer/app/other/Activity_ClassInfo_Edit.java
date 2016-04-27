package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.MajorDao;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.view.AbstractSpinerAdapter;
import com.eventer.app.view.SpinerPopWindow;
import com.eventer.app.view.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_ClassInfo_Edit extends SwipeBackActivity implements View.OnClickListener
        , AbstractSpinerAdapter.IOnItemSelectListener {
    private TextView  tv_year,tv_school,tv_major,tv_class;
    private TextView[] tv_list;
    private List<String> valueList = new ArrayList<>();
    private List<String> yearList = new ArrayList<>();
    private MajorDao dao;
    private String year;
    private String major;
    private String mclass;
    private String school;
    private int index;
    private Context context;
    Button btn_commit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editclassinfo);
        setBaseTitle(getString(R.string.edit_class_info));
        context=this;
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
        tv_class=(TextView)findViewById(R.id.tv_class);
        tv_major=(TextView)findViewById(R.id.tv_major);
        tv_school=(TextView)findViewById(R.id.tv_school);
        tv_year=(TextView)findViewById(R.id.tv_year);
        tv_list=new TextView[]{tv_year,tv_school,tv_major,tv_class};
        btn_commit=(Button)findViewById(R.id.btn_commit);
        tv_class.setOnClickListener(this);
        tv_major.setOnClickListener(this);
        tv_school.setOnClickListener(this);
        tv_year.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
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
            case R.id.btn_commit:

                index=-1;
                year=tv_year.getText().toString();
                school = tv_school.getText().toString();
                major=tv_major.getText().toString();
                mclass=tv_class.getText().toString();
                if(!TextUtils.isEmpty(year)&&!TextUtils.isEmpty(school)&&!TextUtils.isEmpty(major)&&!TextUtils.isEmpty(mclass)){
                    updateClassInfo();
                }else{
                    Toast.makeText(context, "请完善班级信息！", Toast.LENGTH_LONG).show();
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
        mSpinerPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                tv_list[index].setSelected(false);
            }
        });
    }

    @Override
    public void onItemClick(int pos) {
        if (pos >= 0 && pos <= valueList.size()){
            String value = valueList.get(pos);
            tv_list[index].setText(value);
        }
    }

    public void updateClassInfo() {
        Map<String, String> map = new HashMap<>();

        map.put("sex", "");
        map.put("uid", Constant.UID+"");
        map.put("token", Constant.TOKEN);
        map.put("name","");
        map.put("email", "");
        map.put("grade",year);
        map.put("school", school);
        map.put("major", major);
        map.put("class", mclass);
        map.put("user_rank", "0");
        LoadDataFromHTTP task = new LoadDataFromHTTP(
                context, Constant.URL_UPDATE_SELFINFO, map);

        task.getData(new com.eventer.app.http.LoadDataFromHTTP.DataCallBack() {

            @SuppressLint("ShowToast")
            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    int code = data.getInteger("status");
                    if (code == 0) {
                        Toast.makeText(context, "班级信息修改成功~",
                                Toast.LENGTH_SHORT).show();
                        LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("grade", year);
                        LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("school", school);
                        LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("class", mclass);
                        LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("major", major);
                        setResult(MyUserInfoActivity.EDIT_CLASS,new Intent().putExtra("isEdit",true));
                        finish();
                    } else if (code == 17) {
                        Toast.makeText(context, "更新失败,请稍后重试！",
                                Toast.LENGTH_SHORT).show();
                    } else {

                        if(!Constant.isConnectNet){
                            Toast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "服务器繁忙请重试...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                }catch (Exception e) {
                    // TODO: handle exception
                    if(!Constant.isConnectNet){
                        Toast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "服务器繁忙请重试...",
                                Toast.LENGTH_SHORT).show();
                    }
                }

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
