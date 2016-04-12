package com.eventer.app.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

@SuppressWarnings({"UnusedDeclaration"})
public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();
    private String local_image_path;


    public FileUtil(Context context,String local_image_path) {
        this.local_image_path = local_image_path;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 保存图片到制定路径
     *
     */
    public void saveBitmap(String filename, Bitmap bitmap) {
        if (!isExternalStorageWritable()) {
            Log.i(TAG, "SD卡不可用，保存失败");
            return;
        }

        if (bitmap == null) {
            return;
        }

        try {


            File file = new File(local_image_path,filename);
            Log.e("png_file","save");
            FileOutputStream outputstream = new FileOutputStream(file);
            if((filename.contains("png"))||(filename.contains("PNG")))
            {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputstream);
            }  else{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputstream);
            }

            outputstream.flush();
            outputstream.close();

        }  catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }



    /**
     * 返回当前应用 SD 卡的绝对路径 like
     * /storage/sdcard0/Android/data/com.example.test/files
     */
    @SuppressLint("SdCardPath")
    public String getAbsolutePath() {
        File root = new File(local_image_path);
        if(!root.exists()){
            root.mkdirs();
        }
        return local_image_path;


    }

    @SuppressLint("SdCardPath")
    public boolean isBitmapExists(String filename) {
        File dir =new File(local_image_path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        //context.getExternalFilesDir(null);
        File file = new File(dir, filename);

        return file.exists();
    }


}
