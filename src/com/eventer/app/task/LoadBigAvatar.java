package com.eventer.app.task;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.eventer.app.http.HTTPService;
import com.eventer.app.util.BitmapCache;
import com.eventer.app.util.FileUtil;
import com.eventer.app.widget.photoview.PhotoView;

/**
 * ͼƬ�첽������
 * 
 * @author Leslie.Fang
 * 
 */
public class LoadBigAvatar {
    // ����߳���
    private static final int MAX_THREAD_NUM = 5;
    // һ���ڴ滺����� LruCache
    private BitmapCache bitmapCache;
    // �����ļ�����
    private FileUtil fileUtil;
 
    // �̳߳�
    private ExecutorService threadPools = null;

    public LoadBigAvatar(Context context, String local_image_path) {
        bitmapCache = new BitmapCache();
        fileUtil = new FileUtil(context, local_image_path);
        threadPools = Executors.newFixedThreadPool(MAX_THREAD_NUM); 
    }
    

    @SuppressLint("HandlerLeak")
    public Bitmap loadImage(final PhotoView imageView, final String imageUrl,
            final ImageDownloadedCallBack imageDownloadedCallBack) {
        final String filename = imageUrl
                .substring(imageUrl.lastIndexOf("/") + 1);
        final String filepath = fileUtil.getAbsolutePath() + filename;

        // �ȴ��ڴ�����
        Bitmap bitmap = bitmapCache.getBitmap("full_"+filename);

        if (bitmap != null) {
            Log.e("1", "image exists in memory");
            return bitmap;
        }

        // ���ļ�����
        if (fileUtil.isBitmapExists("full_"+filename)) {
            Log.e("1", "image exists in file" + filename);
            bitmap = BitmapFactory.decodeFile(fileUtil.getAbsolutePath()+"full_"+filename);
            // �Ȼ��浽�ڴ�
            if(bitmap!=null)
                   bitmapCache.putBitmap("full_"+filename, bitmap);
            return bitmap;

        }

        // �ڴ���ļ��ж�û���ٴ���������
        if (imageUrl != null && !imageUrl.equals("")) {
            final Handler handler = new Handler() {
                @SuppressLint("HandlerLeak")
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 111 && imageDownloadedCallBack != null) {
                        Bitmap bitmap = (Bitmap) msg.obj;
                        imageDownloadedCallBack.onImageDownloaded(imageView,
                                bitmap,-1);
                    }else if(msg.what == 404 && imageDownloadedCallBack != null){
                    	imageDownloadedCallBack.onImageDownloaded(imageView,
                                null,404);
                    }
                }
            };

            Thread thread = new Thread() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    Log.e("1", Thread.currentThread().getName()
                            + " is running--����ͷ��");
                    Map<String,Object> result=HTTPService.getInstance()
                            .getStream(imageUrl);
                    if(result==null){
                    	Log.e("1", "����ʧ�ܣ�");
                    	return;
                    }
                    
                    int status=(int)result.get("status");
                    if(status==200){
                    	InputStream inputStream = (InputStream)result.get("inputstream");
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                     // ͼƬ���سɹ��󻺴沢ִ�лص�ˢ�½���
                        if (bitmap != null) {
                            Log.e("1", "���سɹ�");
                            // �Ȼ��浽�ڴ�
                            bitmapCache.putBitmap("full_"+filename, bitmap);
                            // ���浽�ļ�ϵͳ
                            fileUtil.saveBitmap("full_"+filename, bitmap);
                            Message msg = new Message();
                            msg.what = 111;
                            msg.obj = bitmap;
                            handler.sendMessage(msg);

                        }
                    }else{
                    	//������ַ
                    	Message msg = new Message();
                        msg.what = 404;
                        msg.obj = 404;
                        handler.sendMessage(msg);
                    }
                    
                }
            };

            threadPools.execute(thread);
        }

        return null;
    }

 
    /**
     * ͼƬ������ɻص��ӿ�
     * 
     */
    public interface ImageDownloadedCallBack {
        void onImageDownloaded(PhotoView imageView, Bitmap bitmap,int status);
    }

   
 
}
