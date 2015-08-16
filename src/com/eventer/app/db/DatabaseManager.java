package com.eventer.app.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.eventer.app.Constant;
import com.eventer.app.R;

public class DatabaseManager {


	public DatabaseManager() {
		// TODO Auto-generated constructor stub
	}
	
	private final int BUFFER_SIZE = 8192;
    public static final String DB_NAME = Constant.UID+".db"; //��������ݿ��ļ���
    public static final String PACKAGE_NAME = "com.eventer.app";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;  //���ֻ��������ݿ��λ��
 
    private SQLiteDatabase database;
    private AtomicInteger mOpenCounter = new AtomicInteger();
    
    private static DatabaseManager instance;
 
   // private static DBManager instance;

    private static final int      DATABASE_VERSION = 1;

    private Context context;
 
    public DatabaseManager(Context context) throws SQLException{
        this.context = context;
    }
    
    public static synchronized void initializeInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
    }
 
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
 
        return instance;
    }
 
    public synchronized void openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
        	database = this.openDatabase(DB_PATH + "/" + DB_NAME);;
        }
    }
 
    public synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0) {
            // Closing database
        	database .close();
 
        }
    }
    
    public synchronized SQLiteDatabase getWritableDatabase() {
		// TODO Auto-generated method stub
    	if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
    		return this.openDatabase(DB_PATH + "/" + DB_NAME);
        }
    	return database;		
	}
    

    
 
    private SQLiteDatabase openDatabase(String dbfile) {
        try {
            if (!(new File(dbfile).exists())) {//�ж����ݿ��ļ��Ƿ���ڣ�����������ִ�е��룬����ֱ�Ӵ����ݿ�
                InputStream is = this.context.getResources().openRawResource(
                       R.raw.event); //����������ݿ�
                Log.e("1", "File11");
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
                fos.close();
                is.close();
            }
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,
                    null);
            return db;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }

   
    /**
     * ɾ�����ݿ�
     * @param context
     * @return
     */
    public boolean deleteDatabase(Context context) {  
         return context.deleteDatabase(DB_PATH + "/" + DB_NAME);  
    }  
    
    /**
     * �������� ����
     * @param tableName ����
     * @param initialValues Ҫ������ж�Ӧֵ
     * @return
     */
    public long insert(String tableName, ContentValues initialValues) {
        return database.insert(tableName, null, initialValues);
    }

    /**
     * ɾ������
     * @param tableName ����
     * @param deleteCondition ����
     * @param deleteArgs ������Ӧ��ֵ�����deleteCondition���С������ţ����ô������е�ֵ�滻��һһ��Ӧ��
     * @return
     */
    public boolean delete(String tableName, String deleteCondition, String[] deleteArgs) {
        return database.delete(tableName, deleteCondition, deleteArgs) > 0;
    }

    /**
     * ��������
     * @param tableName ����
     * @param initialValues Ҫ���µ���
     * @param selection ���µ�����
     * @param selectArgs ���������еġ�������Ӧ��ֵ
     * @return
     */
    public boolean update(String tableName, ContentValues initialValues, String selection, String[] selectArgs) {
        return database.update(tableName, initialValues, selection, selectArgs) > 0;
    }

    /**
     * ȡ��һ���б�
     * @param distinct �Ƿ�ȥ�ظ�
     * @param tableName ����
     * @param columns Ҫ���ص���
     * @param selection ����
     * @param selectionArgs �����С������Ĳ���ֵ
     * @param groupBy ����
     * @param having �����������
     * @param orderBy ����
     * @return
     */
    public Cursor findList(boolean distinct, String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {

        return database.query(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    /**
     * ȡ�õ��м�¼
     * @param tableName ����
     * @param columns ��ȡ��������
     * @param selection ����
     * @param selectionArgs �����С�������Ӧ��ֵ
     * @param groupBy ����
     * @param having ��������
     * @param orderBy ����
     * @param limit ��������
     * @param distinct �Ƿ�ȥ�ظ�
     * @return
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
     * ִ��SQL(������)
     * @param sql
     * @param args SQL�С���������ֵ
     */
    public void execSQL(String sql, Object[] args) {
        database.execSQL(sql, args);

    }

    /**
     * ִ��SQL
     * @param sql
     */
    public void execSQL(String sql) {
        database.execSQL(sql);

    }

    public Cursor rawQuery(String sql,String[] args){
    	Cursor mCursor=database.rawQuery(sql, args);
//    	if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
        return mCursor;
    }
    
    /**
     * �ж�ĳ�ű��Ƿ����
     * @param tabName ����
     * @return
     */
    public boolean isTableExist(String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }

        try {
            Cursor cursor = null;
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
        }
        return result;
    }

    /**
     * �ж�ĳ�ű����Ƿ����ĳ�ֶ�(ע���÷����޷��жϱ��Ƿ���ڣ����Ӧ��isTableExistһ��Ӧ��)
     * @param tabName ����
     * @param columnName ����
     * @return
     */
    public boolean isColumnExist(String tableName, String columnName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }

        try {
            Cursor cursor = null;
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
        }
        return result;
    }
}
