package com.spreadtrum.iit.zpayapp.network.webservice;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.database.AppDisplayDatabaseHelper;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.BitmapCache;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
//import org.ksoap2.transport.HttpTransportSE;
/**
 * Created by SPREADTRUM\ting.long on 16-9-7.
 */
public class WebserviceActivity extends AppCompatActivity{
    private Button btnTestWebService;
    private ImageView imageView;
    private File cache;
    private NetworkImageView networkImageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTestWebService = (Button) findViewById(R.id.id_test_webservice);
//        imageView = (ImageView) findViewById(R.id.id_iv_test);
//        networkImageView = (NetworkImageView) findViewById(R.id.network_image_view);
        btnTestWebService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String xml = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZyA9ICJVVEYtOCI/Pgo8dHNtZGJyZXF1ZXN0IHZlcnNpb249IjEuMCI+CiAgICAgICAgIDxTRUluZGV4PjQ1MTAwMDAwMDAwMDAwMDAyMDE2MDMyODAwMDAwMDAxMDAwMzwvU0VJbmRleD4KICAgICAgICAgPHJlcXR5cGU+ZGJpbnNlcnQ8L3JlcXR5cGU+CiAgICAgICAgIDxyZXFkYXRhPgogICAgICAgICAgICAgICAgICAgPHRhc2t0eXBlPkQ1PC90YXNrdHlwZT4KICAgICAgICAgICAgICAgICAgIDx0YXNrY29tbWFuZD5ENTAxMDE8L3Rhc2tjb21tYW5kPgogICAgICAgICA8L3JlcWRhdGE+CjwvdHNtZGJyZXF1ZXN0Pg==";
//                ApplyPersonalizationService.test(xml);
                RequestQueue queue = Volley.newRequestQueue(MyApplication.getContextObject());
                String url = "http://www.tjykt.com/templets/default/images/hl_z.png";
                networkImageView.setDefaultImageResId(R.drawable.refresh);
                networkImageView.setErrorImageResId(R.drawable.refresh);
                networkImageView.setImageUrl(url,new ImageLoader(queue,new BitmapCache()));
//                ApplyPersonalizationService.getTSMAppInformation(xml, new TSMAppInformationCallback() {
//                    @Override
//                    public void getAppInfo(String xml) {
//                        LogUtil.debug(xml);
//                    }
//                });
//                final String url = "http://www.tjykt.com/templets/default/images/hl_z.png";//item.getPicurl();
//                //ViewHolder viewHolder = new ViewHolder();
//               // AppInformation appInformation = new AppInformation();
//                //创建缓存目录，存放图片，添加允许访问存储设备权限 "/storage/emulated/0/cache111"
//                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//                cache = new File(Environment.getExternalStorageDirectory(), "cache111");
//                if(!cache.exists()){
//                    cache.mkdirs();
//                    LogUtil.debug(cache.getAbsolutePath());
//                }
////                DownloadImage(url,null,null);
//                String localpath = "/storage/emulated/0/Android/data/com.spreadtrum.iit.zpayapp/cache/aaaaa/hl_z.png";
//                Bitmap bitmap = getLoacalBitmap(localpath);
//                if(bitmap==null)
//                    return;
//                imageView.setImageBitmap(bitmap);
//                OkHttpUtils
//                        .get()//
//                        .url(url)//
//                        .build()//
//                        .execute(new BitmapCallback()
//                        {
//
//                            @Override
//                            public void onError(Call call, Exception e, int id) {
//                                LogUtil.debug(e.getMessage());
//                            }
//
//                            @Override
//                            public void onResponse(Bitmap response, int id) {
//                                //imageView.setImageBitmap(response);
//                                File file = saveImage(url,response);
//                                LogUtil.debug(file.getAbsolutePath());
//                                String path = "/storage/emulated/0/Android/data/com.spreadtrum.iit.zpayapp/cache/aaaaa/hl_z.png";
//                                Bitmap bitmap = getLoacalBitmap(file.getAbsolutePath());
//                                imageView.setImageBitmap(bitmap);
//                            }
//                        });
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //创建请求xml
//                        String seId="451000000000000020160328000000010003";
//                        String requestType = "dbquery";
//                        String requestData = "applistquery";
//                        String requestXml = "<?xml version=\"1.0\" encoding = \"UTF-8\"?>\n" +
//                                "<tsmdbrequest version=\"1.0\">\n" +
//                                "         <SEIndex>451000000000000020160328000000010003</SEIndex>\n" +
//                                "         <reqtype>dbquery</reqtype>\n" +
//                                "         <reqdata>applistquery</reqdata>\n" +
//                                "</tsmdbrequest>";
//                                // MessageBuilder.doBussinessRequest(seId,requestType,requestData);
//                        String requestXml2 = "<?xml version=\"1.0\" encoding = \"UTF-8\"?>\n" +
//                                "<tsmdbrequest version=\"1.0\">\n" +
//                                "         <SEIndex>451000000000000020160328000000010003</SEIndex>\n" +
//                                "         <reqtype>dbinsert</reqtype>\n" +
//                                "         <reqdata>\n" +
//                                "                   <tasktype>D5</tasktype>\n" +
//                                "                   <taskcommand>D50101</taskcommand>\n" +
//                                "         </reqdata>\n" +
//                                "</tsmdbrequest>";
//                        String xml1="PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZyA9ICJVVEYtOCI/Pgo8dHNtZGJyZXF1ZXN0IHZlcnNpb249IjEuMCI+CiAgICAgICAgIDxTRUluZGV4PjQ1MTAwMDAwMDAwMDAwMDAyMDE2MDMyODAwMDAwMDAxMDAwMzwvU0VJbmRleD4KICAgICAgICAgPHJlcXR5cGU+ZGJxdWVyeTwvcmVxdHlwZT4KICAgICAgICAgPHJlcWRhdGE+YXBwbGlzdHF1ZXJ5PC9yZXFkYXRhPgo8L3RzbWRicmVxdWVzdD4=";
//                        String xml2="PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZyA9ICJVVEYtOCI/Pgo8dHNtZGJyZXF1ZXN0IHZlcnNpb249IjEuMCI+CiAgICAgICAgIDxTRUluZGV4PjQ1MTAwMDAwMDAwMDAwMDAyMDE2MDMyODAwMDAwMDAxMDAwMzwvU0VJbmRleD4KICAgICAgICAgPHJlcXR5cGU+ZGJpbnNlcnQ8L3JlcXR5cGU+CiAgICAgICAgIDxyZXFkYXRhPgogICAgICAgICAgICAgICAgICAgPHRhc2t0eXBlPkQ1PC90YXNrdHlwZT4KICAgICAgICAgICAgICAgICAgIDx0YXNrY29tbWFuZD5ENTAxMDE8L3Rhc2tjb21tYW5kPgogICAgICAgICA8L3JlcWRhdGE+CjwvdHNtZGJyZXF1ZXN0Pg==";
//
//                        String xml = ApplyPersonalizationService.getTSMAppInformation(xml2, new TSMAppInformationCallback() {
//                            @Override
//                            public void getAppInfo(String xml) {
//                                //String taskIdDecode = new String(Base64.decode(xml.getBytes(),Base64.DEFAULT));
//                                //LogUtil.debug(xml);
//                            }
//                        });
//
////                        String taskid = new ApplyPersonalizationService().getTaskid("1234","12334","1213123");
////                        String taskIdDecode = new String(Base64.decode(taskid.getBytes(),Base64.DEFAULT));
////                        LogUtil.debug("taskid is:"+taskIdDecode);
//                    }
//                }).start();

            }
        });
    }

    private File saveImage(String url,Bitmap bmp) {
        File appDir = new File(getExternalCacheDir(), "aaaaa");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
//        String fileName = System.currentTimeMillis() + ".jpg";
        String fileName = url.substring(url.lastIndexOf("/")+1);
        String picType = url.substring(url.lastIndexOf(".")+1);
        Bitmap.CompressFormat format;
        if(picType.equals("png")){
             format = Bitmap.CompressFormat.PNG;
        }else{
            format = Bitmap.CompressFormat.JPEG;
        }
        LogUtil.debug(fileName);
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(format, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void DownloadImage(final String url, final ViewHolder viewHolder, final AppInformation item){
        String filename = "123";//url.substring(url.lastIndexOf("."));
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new FileCallBack(cache.getAbsolutePath(),filename) {//cache.getAbsolutePath()
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.debug(e.getMessage());
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        LogUtil.debug("onResponse:"+response.getAbsolutePath());
                        Uri uri = Uri.fromFile(response);
                        LogUtil.debug(uri.toString());
                        //显示
 //                       item.setImageUri(uri);
 //                       viewHolder.setImageUri(R.id.id_iv_appicon,uri);
//                        //更新数据库
//                        SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();
//                        ContentValues contentValues = new ContentValues();
//                        contentValues.put("imageuri",uri.toString());
//                        dbwrite.update(AppDisplayDatabaseHelper.TABLE_APPINFO,contentValues,"picurl=?",new String[]{url});
                    }
                });
    }
}
