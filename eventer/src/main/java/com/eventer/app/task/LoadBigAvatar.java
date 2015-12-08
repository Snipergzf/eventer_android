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
 * 图片异步加载类
 *
 * @author Leslie.Fang
 *
 */
public class LoadBigAvatar {
    // 最大线程数
    private static final int MAX_THREAD_NUM = 5;
    // 一级内存缓存基于 LruCache
    private BitmapCache bitmapCache;
    // 二级文件缓存
    private FileUtil fileUtil;

    // 线程池
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

        // 先从内存中拿
        Bitmap bitmap = bitmapCache.getBitmap("full_"+filename);

        if (bitmap != null) {
            Log.e("1", "image exists in memory");
            return bitmap;
        }

        // 从文件中找
        if (fileUtil.isBitmapExists("full_"+filename)) {
            Log.e("1", "image exists in file" + filename);
            bitmap = BitmapFactory.decodeFile(fileUtil.getAbsolutePath()+"full_"+filename);
            // 先缓存到内存
            if(bitmap!=null)
                bitmapCache.putBitmap("full_"+filename, bitmap);
            return bitmap;

        }

        // 内存和文件中都没有再从网络下载
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
                            + " is running--下载头像");
                    Map<String,Object> result=HTTPService.getInstance()
                            .getStream(imageUrl);
                    if(result==null){
                        Log.e("1", "下载失败！");
                        return;
                    }

                    int status=(int)result.get("status");
                    if(status==200){
                        InputStream inputStream = (InputStream)result.get("inputstream");
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        // 图片下载成功后缓存并执行回调刷新界面
                        if (bitmap != null) {
                            Log.e("1", "下载成功");
                            // 先缓存到内存
                            bitmapCache.putBitmap("full_"+filename, bitmap);
                            // 缓存到文件系统
                            fileUtil.saveBitmap("full_"+filename, bitmap);
                            Message msg = new Message();
                            msg.what = 111;
                            msg.obj = bitmap;
                            handler.sendMessage(msg);

                        }
                    }else{
                        //更新网址
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
     * 图片下载完成回调接口
     *
     */
    public interface ImageDownloadedCallBack {
        void onImageDownloaded(PhotoView imageView, Bitmap bitmap,int status);
    }



}