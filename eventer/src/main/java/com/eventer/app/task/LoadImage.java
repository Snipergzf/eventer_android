package com.eventer.app.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.eventer.app.http.HttpUnit;
import com.eventer.app.util.BitmapCache;
import com.eventer.app.util.FileUtil;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片异步加载类
 *
 * @author Leslie.Fang
 *
 */
public class LoadImage {
    // 最大线程数
    private static final int MAX_THREAD_NUM = 5;
    // 一级内存缓存基于 LruCache
    private BitmapCache bitmapCache;
    // 二级文件缓存
    private FileUtil fileUtil;
    private int IMG_SCALE = 80;

    // 线程池
    private ExecutorService threadPools = null;

    public LoadImage(Context context, String local_image_path) {
        bitmapCache = new BitmapCache();
        fileUtil = new FileUtil(context, local_image_path);
        threadPools = Executors.newFixedThreadPool(MAX_THREAD_NUM);
        int density = (int)context.getResources().getDisplayMetrics().density;
        IMG_SCALE = density * IMG_SCALE;
    }


    @SuppressLint("HandlerLeak")
    public Bitmap loadImage(final ImageView imageView, final String imageUrl,
                            final ImageDownloadedCallBack imageDownloadedCallBack) {
        final String filename = imageUrl
                .substring(imageUrl.lastIndexOf("/") + 1)+"_e";
        final String filepath = fileUtil.getAbsolutePath() + filename;
        // 先从内存中拿
        Bitmap bitmap = bitmapCache.getBitmap(imageUrl);

        if (bitmap != null) {
            Log.e("1", "image exists in memory");
            return bitmap;
        }

        // 从文件中找
        if (fileUtil.isBitmapExists(filename)) {
            Log.e("1", "image exists in file" + filename);
            BitmapFactory.Options measureOptions = new BitmapFactory.Options();
            measureOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filepath, measureOptions);
            int scale = Math.min(measureOptions.outWidth, measureOptions.outHeight) / IMG_SCALE;
            scale = Math.max(scale, 1);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            bitmap = BitmapFactory.decodeFile(filepath, options);
            // 先缓存到内存
            bitmapCache.putBitmap(imageUrl, bitmap);
            return bitmap;

        }

        // 内存和文件中都没有再从网络下载
        if (!imageUrl.equals("")) {
            final Handler handler = new Handler() {
                @SuppressLint("HandlerLeak")
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 111 && imageDownloadedCallBack != null) {
                        Bitmap bitmap = (Bitmap) msg.obj;
                        imageDownloadedCallBack.onImageDownloaded(imageView,
                                bitmap,-1);
                    }else if(imageDownloadedCallBack != null){
                        imageDownloadedCallBack.onImageDownloaded(imageView,
                                null,msg.what);
                    }
                }
            };

            Thread thread = new Thread() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    Log.e("1", Thread.currentThread().getName()
                            + " is running--下载头像");
                    Map<String,Object> result= HttpUnit.
                                                     getStream(imageUrl);
                    if(result==null){
                        Log.e("1", "下载失败！");
                        return;
                    }
                    int status=(int)result.get("status");
                    if(status==200){
                        InputStream inputStream = (InputStream)result.get("inputstream");
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = false;
                        options.inSampleSize = 5; // width，hight设为原来的十分一
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream,
                                null, options);
//                        InputStream inputStream = (InputStream)result.get("inputstream");
////                        BitmapFactory.Options measureOptions = new BitmapFactory.Options();
////                        measureOptions.inJustDecodeBounds = true;
////                        BitmapFactory.decodeStream(inputStream,null, measureOptions);
////                        int scale = Math.min(measureOptions.outWidth, measureOptions.outHeight) / (IMG_SCALE * 2);
////                        scale = Math.max(scale, 1);
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inPreferredConfig = Bitmap.Config.RGB_565;
//                        options.inJustDecodeBounds = false;
//                        options.inSampleSize = 3;
//                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream,
//                                null, options);
                        // 图片下载成功后缓存并执行回调刷新界面
                        if (bitmap != null) {
                            // 先缓存到内存
                            bitmapCache.putBitmap(imageUrl, bitmap);
                            // 缓存到文件系统
                            fileUtil.saveBitmap(filename, bitmap);
                            Message msg = new Message();
                            msg.what = 111;
                            msg.obj = bitmap;
                            handler.sendMessage(msg);
                        }else{
                            Message msg = new Message();
                            msg.what = 400;
                            msg.obj = 400;
                            handler.sendMessage(msg);
                            Log.e("1","bitmap is null");
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
        void onImageDownloaded(ImageView imageView, Bitmap bitmap,int status);
    }



}
