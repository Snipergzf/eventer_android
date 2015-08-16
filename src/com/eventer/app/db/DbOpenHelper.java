/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eventer.app.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.eventer.app.Constant;
import com.eventer.app.R;
 

public class DbOpenHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static DbOpenHelper instance;
	public static final String PACKAGE_NAME = "com.eventer.app";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;  //在手机里存放数据库的位置
    private Context context;
    private final int BUFFER_SIZE = 8192;
	 
		
			
	
	private DbOpenHelper(Context context) {
		super(context, getUserDatabaseName(), null, DATABASE_VERSION);
		this.context=context;
	}
	
	public static DbOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbOpenHelper(context.getApplicationContext());
		}
		return instance;
	}
	
	private static String getUserDatabaseName() {
        return  Constant.UID + "_glufine.db";
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String dbfile=DB_PATH+"/"+Constant.UID + "_glufine.db";
		if (!(new File(dbfile).exists())) {//判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库           
			try {
				InputStream is = this.context.getResources().openRawResource(
		                   R.raw.event); //欲导入的数据库
		            Log.e("1", "File11");
		            FileOutputStream fos;
				fos = new FileOutputStream(dbfile);
			
	            byte[] buffer = new byte[BUFFER_SIZE];
	            int count = 0;
	            while ((count = is.read(buffer)) > 0) {
	                fos.write(buffer, 0, count);
	            }
	            fos.flush();
	            fos.close();
	            is.close();
	            db = SQLiteDatabase.openOrCreateDatabase(dbfile,
	                    null);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (IOException e) {

	            e.printStackTrace();
	        }
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public void closeDB() {
	    if (instance != null) {
	        try {
	            SQLiteDatabase db = instance.getWritableDatabase();
	            db.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        instance = null;
	    }
	}
	
}
