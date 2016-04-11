package com.eventer.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.eventer.app.Constant;
import com.eventer.app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
@SuppressWarnings({"UnusedDeclaration"})
public class DBManager {


    public DBManager() {
        // TODO Auto-generated constructor stub
    }

    final int BUFFER_SIZE = 8192;
    public static final String DB_NAME = Constant.UID+".db"; //保存的数据库文件名
    public static final String PACKAGE_NAME = "com.eventer.app";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME+"/databases";  //在手机里存放数据库的位置

    private SQLiteDatabase database;


    private static DBManager instance;

    // private static DBManager instance;



    private Context context;

    public DBManager(Context context) throws SQLException{
        this.context = context;
    }

    public static synchronized void initializeInstance(Context context) {
        if (instance == null) {
            instance = new DBManager(context);
        }
    }

    public static synchronized DBManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DBManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

//    public synchronized void openDatabase() {
//        if(mOpenCounter.incrementAndGet() == 1) {
//            // Opening new database
//        	database = this.openDatabase(DB_PATH + "/" + DB_NAME);;
//        }
//    }
// 
//    public synchronized void closeDatabase() {
//        if(mOpenCounter.decrementAndGet() == 0&&database != null && database.isOpen()) {
//            // Closing database
//        	database .close();
//        }
//    }
//    
//    public synchronized SQLiteDatabase getWritableDatabase() {
//		// TODO Auto-generated method stub
//    	if(mOpenCounter.incrementAndGet() == 1) {
//            // Opening new database
//    		return this.openDatabase(DB_PATH + "/" + DB_NAME);
//        }
//    	return database;		
//	}


    public SQLiteDatabase getWritableDatabase() {
        // TODO Auto-generated method stub
        return this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    public void closeDatabase() {
        if (this.database != null && this.database.isOpen())
        {
            this.database.close();
        }
    }

    public void openDatabase() {
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    public void openDatabase(String db, int raw) {
        this.database = this.openDatabaseByRaw(DB_PATH + "/" + db, raw);
    }

    private SQLiteDatabase openDatabase(String dbfile) {
        try {
            if (!(new File(dbfile).exists())) {//判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                InputStream is = this.context.getResources().openRawResource(
                        R.raw.event); //欲导入的数据库
                Log.e("1", "File11");
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count ;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
                fos.close();
                is.close();
            }
            return SQLiteDatabase.openOrCreateDatabase(dbfile,
                    null);
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private SQLiteDatabase openDatabaseByRaw(String dbfile, int raw) {
        try {
            if (!(new File(dbfile).exists())) {//判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                InputStream is = this.context.getResources().openRawResource(
                        raw); //欲导入的数据库
                Log.e("1", "File11");
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count ;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
                fos.close();
                is.close();
            }
            return SQLiteDatabase.openOrCreateDatabase(dbfile,
                    null);
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 删除数据库

     */
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DB_PATH + "/" + DB_NAME);
    }

    /**
     * 插入数据 参数
     * @param tableName 表名
     * @param initialValues 要插入的列对应值

     */
    public long insert(String tableName, ContentValues initialValues) {
        return database.insert(tableName, null, initialValues);
    }

    /**
     * 删除数据
     * @param tableName 表名
     * @param deleteCondition 条件
     * @param deleteArgs 条件对应的值（如果deleteCondition中有“？”号，将用此数组中的值替换，一一对应）
     */
    public boolean delete(String tableName, String deleteCondition, String[] deleteArgs) {
        return database.delete(tableName, deleteCondition, deleteArgs) > 0;
    }

    /**
     * 更新数据
     * @param tableName 表名
     * @param initialValues 要更新的列
     * @param selection 更新的条件
     * @param selectArgs 更新条件中的“？”对应的值

     */
    public boolean update(String tableName, ContentValues initialValues, String selection, String[] selectArgs) {
        return database.update(tableName, initialValues, selection, selectArgs) > 0;
    }

    /**
     * 取得一个列表
     * @param distinct 是否去重复
     * @param tableName 表名
     * @param columns 要返回的列
     * @param selection 条件
     * @param selectionArgs 条件中“？”的参数值
     * @param groupBy 分组
     * @param having 分组过滤条件
     * @param orderBy 排序

     */
    public Cursor findList(boolean distinct, String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {

        return database.query(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    /**
     * 取得单行记录
     * @param tableName 表名
     * @param columns 获取的列数组
     * @param selection 条件
     * @param selectionArgs 条件中“？”对应的值
     * @param groupBy 分组
     * @param having 分组条件
     * @param orderBy 排序
     * @param limit 数据区间
     * @param distinct 是否去重复

     * @throws SQLException
     */
    public Cursor findOne(boolean distinct, String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) throws SQLException {

        Cursor mCursor = findList(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * 执行SQL(带参数)

     * @param args SQL中“？”参数值
     */
    public void execSQL(String sql, Object[] args) {
        database.execSQL(sql, args);

    }

    /**
     * 执行SQL

     */
    public void execSQL(String sql) {
        database.execSQL(sql);

    }

    public Cursor rawQuery(String sql,String[] args){
        Cursor mCursor=database.rawQuery(sql, args);
    	if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * 判断某张表是否存在

     */
    public boolean isTableExist(String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }

        try {
            Cursor cursor;
            String sql = "select count(1) as c from sqlite_master where type ='table' and name ='" + tableName.trim() + "'";
            cursor = database.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
            cursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断某张表中是否存在某字段(注，该方法无法判断表是否存在，因此应与isTableExist一起应用)
     * @param columnName 列名

     */
    public boolean isColumnExist(String tableName, String columnName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }

        try {
            Cursor cursor;
            String sql = "select count(1) as c from sqlite_master where type ='table' and name ='" + tableName.trim() + "' and sql like '%" + columnName.trim() + "%'";
            cursor = database.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

            cursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
