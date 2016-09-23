package com.spreadtrum.iit.zpayapp.displaydemo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.database.AppDisplayDatabaseHelper;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;

/**
 * Created by SPREADTRUM\ting.long on 16-9-22.
 */
public class ImageLoaderUtil {
    private Handler updatePicHandler=null;


    public ImageLoaderUtil(Handler handler){
        this.updatePicHandler = handler;

    }


    private boolean saveImage(File appDir,String fileName,Bitmap bmp){
//    private File saveImage(String url, Bitmap bmp) {
//        File appDir = new File(MyApplication.getContextObject().getExternalCacheDir(), "image");
//        if (!appDir.exists()) {
//            appDir.mkdir();
//        }
//        String fileName = System.currentTimeMillis() + ".jpg";
//        String fileName = url.substring(url.lastIndexOf("/")+1);
//        String picType = url.substring(url.lastIndexOf(".")+1);

        String picType = fileName.substring(fileName.lastIndexOf(".")+1);
        Bitmap.CompressFormat format;
        if(picType.equals("png")){
            format = Bitmap.CompressFormat.PNG;
        }else{
            format = Bitmap.CompressFormat.JPEG;
        }
//        LogUtil.debug(fileName);
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(format, 100, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return file;
        return false;
    }

    public static Bitmap getLoacalBitmap(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void DownloadImage(final String url, final ImageView imageView, final AppInformation item,
                              final View view){
        if(item.isPicdownloading())
            return;
        LogUtil.debug(url);
        OkHttpUtils
                .get()//
                .url(url)//
                .build()//
                .execute(new BitmapCallback()
                {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.debug(e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
//                        imageView.setImageBitmap(response);
                        ImageView imageViewByTag = (ImageView) view.findViewWithTag(url);
                        if(imageViewByTag!=null){
                            imageViewByTag.setImageBitmap(response);
                        }
                        String fileName = url.substring(url.lastIndexOf("/")+1);
                        saveImage(ImageFileCache.getAppDir(),fileName,response);
                        File file = new File(ImageFileCache.getAppDir(),fileName);
                        //发送消息，更新appList
                        item.setPicdownloading(false);
                        item.setLocalpicpath(file.getAbsolutePath());
                        Message msg = new Message();
                        msg.obj = item;
                        msg.what=0;
                        updatePicHandler.sendMessage(msg);
                        //更新数据库
                        AppDisplayDatabaseHelper dbHelper = new AppDisplayDatabaseHelper(MyApplication.getContextObject(),"info.db",null,1);
                        SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("localpicpath",file.getAbsolutePath());
                        dbwrite.update(AppDisplayDatabaseHelper.TABLE_APPINFO,contentValues,"picurl=?",new String[]{url});
                    }
                });
    }
}
