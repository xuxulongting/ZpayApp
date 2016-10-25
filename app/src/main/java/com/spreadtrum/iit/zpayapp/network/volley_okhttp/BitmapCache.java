package com.spreadtrum.iit.zpayapp.network.volley_okhttp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.libcore.io.DiskLruCache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by SPREADTRUM\ting.long on 16-9-28.
 * 使用volley下载图片，volley自带硬盘缓存的功能（DiskBasedCache），硬盘缓存最大为5MB
 *  该缓存是在/data/data/<application package>/cache，而不是在SD卡上
 *  只需要添加内存缓存即可，volley能通过设定图片最大宽度和高度对图片进行压缩处理，
 * 有效避免OOM，高效加载，节省流量
 */
public class BitmapCache implements ImageLoader.ImageCache {
    private LruCache<String,Bitmap> cache;
    private DiskLruCache mDiskCaches;
    public BitmapCache() {
//        int maxSize = 10 * 1024 * 1024;
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int maxSize = maxMemory/8;
        // LruCache通过构造函数传入缓存值，以KB为单位
        cache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount()/1024;
            }
        };


//        File cacheDir = getFileCache(MyApplication.getContextObject(), "image");
//        if (!cacheDir.exists()) {
//            cacheDir.mkdirs();
//        }
//        try {
//            mDiskCaches = DiskLruCache.open(cacheDir, 1, 1, maxSize);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    @Override
    public Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        //从内存缓存获取图片
        bitmap = cache.get(url);
//        if(bitmap!=null){
        return bitmap;
//        }
//        else{
//            //从硬盘获取图片
//            String key = toMD5String(url);
//            try {
//                DiskLruCache.Snapshot snapshot = mDiskCaches.get(key);
//                if(snapshot==null)
//                    bitmap=null;
//                else
//                {
//                    FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(0);
//                    FileDescriptor fileDescriptor = fileInputStream.getFD();
//                    if(fileDescriptor!=null){
//                        bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return bitmap;
//        }

    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        //通过LruCache存入内存缓存
        cache.put(url,bitmap);
//        //通过DiskLruCache存入SD卡中
//        String key = toMD5String(url);
//        try {
//            DiskLruCache.Editor editor = mDiskCaches.edit(key);
//            if(editor!=null){
//                OutputStream outputStream = editor.newOutputStream(0);
//                String picType = url.substring(url.lastIndexOf(".") + 1);
//                Bitmap.CompressFormat format;
//                if (picType.equals("png")) {
//                    format = Bitmap.CompressFormat.PNG;
//                } else {
//                    format = Bitmap.CompressFormat.JPEG;
//                }
//                bitmap.compress(format, 100, outputStream);
//                editor.commit();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private File getFileCache(Context context, String cacheFileName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + cacheFileName);
    }

    public String toMD5String(String key) {
        String cacheKey;
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            cacheKey = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
