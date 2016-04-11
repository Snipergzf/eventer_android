package com.eventer.app.db;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.eventer.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuNana on 2016/1/26.
 */
public class MajorDao {
    private DBManager dbHelper;
    Context context;
    public static final String TABLE_NAME = "class";
    public static final String COLUMN_NAME_YEAR = "year";
    public static final String COLUMN_NAME_SCHOOL = "school";
    public static final String COLUMN_NAME_MAJOR = "major";
    public static final String COLUMN_NAME_CLASS = "class";
    private String dbName = "major3";

    public MajorDao(Context context) {
        dbHelper = new DBManager(context);
        this.context = context;
        //dbHelper = DbOpenHelper.getInstance(context);
    }

    public List<String> getYear() {
        // TODO Auto-generated method stub
        List<String> list = new ArrayList<>();
        dbHelper.openDatabase(dbName, R.raw.major);
        Cursor c=dbHelper.findList(true, TABLE_NAME, new String[]{COLUMN_NAME_YEAR},
                null, null, null, null,COLUMN_NAME_YEAR,null);

        while (c.moveToNext()) {
            String year = c.getString(c.getColumnIndex(COLUMN_NAME_YEAR));
            if(!TextUtils.isEmpty(year)){

                if(!list.contains(year.trim())){
                    list.add(year.trim());
                    Log.e("1", list.size() + "-" + year.trim() + "-");
                }
            }
        }
        dbHelper.closeDatabase();
        return list;
    }

    public List<String> getSchool(String year) {
        // TODO Auto-generated method stub
        List<String> list = new ArrayList<>();
        dbHelper.openDatabase(dbName, R.raw.major);
        Cursor c=dbHelper.findList(true, TABLE_NAME, new String[]{COLUMN_NAME_SCHOOL},
                COLUMN_NAME_YEAR + " = ?", new String[]{year}, null, null,COLUMN_NAME_SCHOOL,null);

        while (c.moveToNext()) {
            String school = c.getString(c.getColumnIndex(COLUMN_NAME_SCHOOL));
            if(!TextUtils.isEmpty(school)){
                if(!list.contains(school.trim())){
                    list.add(school.trim());
                }
            }
        }
        dbHelper.closeDatabase();
        return list;
    }

    public List<String> getMajor(String year, String school) {
        // TODO Auto-generated method stub
        List<String> list = new ArrayList<>();
        dbHelper.openDatabase(dbName, R.raw.major);
        Cursor c=dbHelper.findList(true, TABLE_NAME, new String[]{COLUMN_NAME_MAJOR},
                COLUMN_NAME_YEAR + " = ? and " + COLUMN_NAME_SCHOOL + " = ?" ,
                new String[]{year, school}, null, null,COLUMN_NAME_MAJOR,null);

        while (c.moveToNext()) {
            String major = c.getString(c.getColumnIndex(COLUMN_NAME_MAJOR));
            if(!TextUtils.isEmpty(major)){
                if(!list.contains(major.trim())){
                    list.add(major.trim());
                }
            }
        }
        dbHelper.closeDatabase();
        return list;
    }

    public List<String> getClass(String year, String school, String major) {
        // TODO Auto-generated method stub
        List<String> list = new ArrayList<>();
        dbHelper.openDatabase(dbName, R.raw.major);
        Cursor c=dbHelper.findList(true, TABLE_NAME, new String[]{COLUMN_NAME_CLASS},
                COLUMN_NAME_YEAR + " = ? and " + COLUMN_NAME_SCHOOL + " = ? and " + COLUMN_NAME_MAJOR + " = ? ",
                new String[]{year, school, major}, null, null,COLUMN_NAME_CLASS,null);

        while (c.moveToNext()) {
            String mClass = c.getString(c.getColumnIndex(COLUMN_NAME_CLASS));
            if(!TextUtils.isEmpty(mClass)){
                String[] array = mClass.split(";");
                for (String str:
                     array) {
                    if(!list.contains(str.trim())){
                        list.add(str.trim());
                    }
                }
            }
        }
        dbHelper.closeDatabase();
        return list;
    }

}
