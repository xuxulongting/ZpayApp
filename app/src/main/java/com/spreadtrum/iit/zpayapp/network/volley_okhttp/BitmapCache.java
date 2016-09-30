package com.spreadtrum.iit.zpayapp.network.volley_okhttp;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by SPREADTRUM\ting.long on 16-9-28.
 */
public class BitmapCache implements ImageLoader.ImageCache {
    private LruCache<String,Bitmap> cache;
    public BitmapCache() {
        int maxSize = 10 * 1024 * 1024;
        cache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }
    @Override
    public Bitmap getBitmap(String url) {
        return cache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        cache.put(url,bitmap);
    }
}
