package com.eventer.app.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.easemob.util.ImageUtils;
import com.eventer.app.Constant;
import com.eventer.app.http.HttpUnit;

public class DecodeScaleImageTask extends AsyncTask<Object, Void, Bitmap> {
	String localFullSizePath = null;
	String thumbnailPath=null;


	@Override
	protected Bitmap doInBackground(Object... args) {
		localFullSizePath = (String) args[0];
		String filename=localFullSizePath.substring(localFullSizePath.lastIndexOf("/")+1);
		Bitmap image=null;
		Log.e("1", "tt====="+localFullSizePath);
		image=ImageUtils.decodeScaleImage(localFullSizePath, 160, 160);

		if (image != null) {
			Log.e("1", "保存图片");
			File f = new File(Environment.getExternalStorageDirectory()+"/Eventer/thumbnail/", filename);
			thumbnailPath=Environment.getExternalStorageDirectory()+"/Eventer/thumbnail/"+filename;
			if (f.exists()) {
				f.delete();
			}
			f.getParentFile().mkdirs();
			try {
				FileOutputStream out = new FileOutputStream(f);
				image.compress(Bitmap.CompressFormat.PNG, 90, out);
				out.flush();
				out.close();
				Log.e("1", "已经保存");
				Map<String, Object> param=new HashMap<String, Object>();
				param.put("uid",Constant.UID+"");
				param.put("token", Constant.TOKEN);
				String result=HttpUnit.sendPictureREquest(param,thumbnailPath,"upload");
				Map<String, String> p=new HashMap<String, String>();
				p.put("uid",Constant.UID+"");
				try {
					Map<String,Object> map=HttpUnit.sendGetAvatarRequest(p);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("1", result);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if (f.exists()) {
					f.delete();
				}
			}

		} else {
		}
		return null;
	}

	protected void onPostExecute(Bitmap image) {

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
}
