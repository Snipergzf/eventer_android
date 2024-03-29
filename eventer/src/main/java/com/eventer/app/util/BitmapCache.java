package com.eventer.app.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapCache  {
    private LruCache<String, Bitmap> mCache;

    public BitmapCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();   
        int maxSize =maxMemory/8;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }
    private static BitmapCache imageCache = null;

	public static synchronized BitmapCache getInstance() {
		if (imageCache == null) {
			imageCache = new BitmapCache();
		}
		return imageCache;

	}
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    public void putBitmap(String url, Bitmap bitmap) {
        
        mCache.put(url, bitmap);
    }
}
