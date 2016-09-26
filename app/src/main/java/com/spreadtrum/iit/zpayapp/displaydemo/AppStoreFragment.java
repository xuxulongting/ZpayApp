package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.ByteUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.database.AppDisplayDatabaseHelper;
import com.spreadtrum.iit.zpayapp.display.Card;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.RequestTaskidEntity;
import com.spreadtrum.iit.zpayapp.message.TSMResponseEntity;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BLEPreparedCallbackListener;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.webservice.ApplyPersonalizationService;
import com.spreadtrum.iit.zpayapp.network.webservice.TSMAppInformationCallback;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
/**
 * Created by SPREADTRUM\ting.long on 16-9-1.
 */
public class AppStoreFragment extends Fragment {
    private GridLayoutManager gridLayoutManagerBus;
    private GridLayoutManager gridLayoutManagerBank;
    private CommonAdapter busAdapter= null;
    // private CommonAdapter bankAdapter;
    private List<Card> listCardParameter = new ArrayList<Card>();
    private List<AppInformation> appInformationList = new ArrayList<AppInformation>();
    List<AppInformation> appList=null;
    private Button btnOperaCard;
    //    private ProgressBar progressBar;
    private LinearLayout linearLayoutBar;
    //    private File cache;
    private AppDisplayDatabaseHelper dbHelper;
    public static final int REQUEST_SPECIAL_APP=2;
    public static final int RESULT_SPECIAL_APP=3;
    public static final String DB_APPINFO="info.db";
    public static final String seId="451000000000000020160328000000010005";
    public static final String TASK_TYPE_DOWNLOAD="D1";
    public static final String TASK_TYPE_DELETE="D2";
    public static final String TASK_TYPE_SYNC="DA";
    //private boolean appInfoPrepared = false;

    private BroadcastReceiver bussinessUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            AppInformation appInformation = (AppInformation) bundle.getSerializable("BUSSINESS_UPDATE");

            //AppInformation appInformation = (AppInformation) intent.getSerializableExtra("BUSSINESS_UPDATE");
            appInformation.setAppinstalling(false);
            if(intent.getAction().equals(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS)) {

                //String bussinessType = intent.getStringExtra("BUSSINESS_TYPE");
                String bussinessType = bundle.getString("BUSSINESS_TYPE");
                if (bussinessType.equals("download")) {
                    appInformation.setAppinstalled("yes");
                } else {
                    appInformation.setAppinstalled("no");
                }
                //修改全局变量map中的值
//                MyApplication.appInstalling.put(appInformation.getIndex(), appInformation.isAppinstalling());

            }
            else
            {
                //不需要更新appinstalled状态
            }
            //更新applist
            appList.set(appInformation.getIndexForlistview(),appInformation);
            //刷新Listview
            busAdapter.notifyDataSetChanged();
        }
    };

    private IntentFilter makeBussinessUpdateIntentFilter(){
        IntentFilter bussinessUpdateIntentFilter = new IntentFilter();
        bussinessUpdateIntentFilter.addAction(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS);
        bussinessUpdateIntentFilter.addAction(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED);
        return bussinessUpdateIntentFilter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //准备数据
        //getCardListData();
        //getAppListData();
        //创建数据库
        dbHelper = new AppDisplayDatabaseHelper(MyApplication.getContextObject(),"info.db",null,1);
//        //创建缓存目录，存放图片，添加允许访问存储设备权限 "/storage/emulated/0/cache111"
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//        cache = new File(Environment.getExternalStorageDirectory(), "cache111");
//        if(!cache.exists()){
//            cache.mkdirs();
//        }
    }

    //插入数据库,删除原来数据，再重新添加，使用原子操作
    private void insertDB(SQLiteDatabase db,List<AppInformation> appInformationList){
        //先删除所有行，开启事务
        db.beginTransaction();
        db.delete(AppDisplayDatabaseHelper.TABLE_APPINFO,null,null);
        for(int i=0;i<appInformationList.size();i++){
            ContentValues contentValues = new ContentValues();
            AppInformation info = appInformationList.get(i);
            contentValues.put("appindex",info.getIndex());
            contentValues.put("picurl",info.getPicurl());
            contentValues.put("appname",info.getAppname());
            contentValues.put("appsize",info.getAppsize());
            contentValues.put("apptype",info.getApptype());
            contentValues.put("spname",info.getSpname());
            contentValues.put("appdesc",info.getAppdesc());
            contentValues.put("appinstalled",info.getAppinstalled());
            contentValues.put("appid",info.getAppid());
            //如果更新失败，则插入？影响效率，直接删除，再插入
            //if(db.update(AppDisplayDatabaseHelper.TABLE_APPINFO,contentValues,"appindex=?",new String[]{info.getIndex()})==0)
            db.insert(AppDisplayDatabaseHelper.TABLE_APPINFO,null,contentValues);
            contentValues.clear();
        }
        //事务已经执行成功
        db.setTransactionSuccessful();
        //关闭事务
        db.endTransaction();
    }

    private File saveImage(String url,Bitmap bmp) {
        File appDir = new File(MyApplication.getContextObject().getExternalCacheDir(), "image");
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
//        LogUtil.debug(fileName);
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

    public static Bitmap getLoacalBitmap(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void DownloadImage(final String url, final ImageView imageView, final AppInformation item, final ListView listView){
        //避免重复的url请求
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
                        ImageView imageViewByTag = (ImageView) listView.findViewWithTag(url);
                        if(imageViewByTag!=null){
                            imageViewByTag.setImageResource(R.drawable.refresh);
                        }
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        //防止图片出现乱序
                        ImageView imageViewByTag = (ImageView) listView.findViewWithTag(url);
                        if(imageViewByTag!=null){
                            imageViewByTag.setImageBitmap(response);
                        }
                        File file = saveImage(url,response);
                        //发送消息，更新appList
                        item.setPicdownloading(false);
                        item.setLocalpicpath(file.getAbsolutePath());
                        Message msg = new Message();
                        msg.obj = item;
                        msg.what=0;
                        updatePicHandler.sendMessage(msg);
                        //更新数据库
                        SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("localpicpath",file.getAbsolutePath());
                        dbwrite.update(AppDisplayDatabaseHelper.TABLE_APPINFO,contentValues,"picurl=?",new String[]{url});
                    }
                });
    }

    //更新appList
    private Handler updatePicHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==0){
                AppInformation appInfo = (AppInformation) msg.obj;
                for(int i=0;i<appList.size();i++){
                    if (appList.get(i).getIndex().equals(appInfo.getIndex())){
                        appList.set(i,appInfo);
                    }
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_appstore,container,false);
        final ListView listViewAppStore = (ListView) view.findViewById(R.id.id_listview_bus);
        final LinearLayout loading= (LinearLayout) view.findViewById(R.id.id_ll_loading);
        listViewAppStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppInformation appInformation = appList.get(i);//appInformationList.get(i);
                appInformation.setIndexForlistview(i);
                Intent intent = new Intent(view.getContext(),SpecialAppActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("APP_PARAMETER", (Serializable) appInformation);
                intent.putExtras(bundle);
                startActivityForResult(intent,REQUEST_SPECIAL_APP);
            }
        });

        loading.setVisibility(View.VISIBLE);
        final Handler handler = new Handler(){
            public void handleMessage(Message msg){
                if(msg.what==0){

                    busAdapter = new CommonAdapter<AppInformation>(view.getContext(), R.layout.list_item_appstore, appList){//appInformationList) {

                        @Override
                        protected void convert(final ViewHolder viewHolder, final AppInformation item, final int position) {
                            //显示应用名称
                            viewHolder.setText(R.id.id_tv_appname,item.getAppname());
                            //显示图片
                            ImageView imageView = viewHolder.getView(R.id.id_iv_appicon);
                            imageView.setImageResource(R.drawable.refresh);//空白图片记得加上，否则，在图片下载成功之前，会显示重复利用的itemview的图片
                            String url = item.getPicurl();
                            imageView.setTag(url);
//                            LogUtil.debug("setTag:"+url);
                            if(item.getLocalpicpath()==null){
                                if(item.isPicdownloading()==false) {
                                    //网络下载图片保存并显示

                                    DownloadImage(url, imageView, item, listViewAppStore);
                                    item.setPicdownloading(true);//主要是为了防止图片重复下载，另外，增加这个约束以后，图片也不会出现乱序的问题了（现在还不知道原因）
                                }

                            }
                            else{
                                //查找本地图片
                                Bitmap bitmap = getLoacalBitmap(item.getLocalpicpath());
                                if(bitmap==null){
                                    //本地图片缓存被清空
                                    DownloadImage(url,imageView,item,listViewAppStore);
                                }
                                else
                                    viewHolder.setImageBitmap(R.id.id_iv_appicon,bitmap);
                            }

                            btnOperaCard = viewHolder.getView(R.id.id_btn_appopra);
//                            progressBar = viewHolder.getView(R.id.id_pb_appProgressBar);
                            linearLayoutBar  =viewHolder.getView(R.id.id_ll_downloading);
                            //LogUtil.debug("isAppinstalling is:"+item.isAppinstalling());
                            if(item.isAppinstalling(item.getIndex())==true){
                                btnOperaCard.setVisibility(View.INVISIBLE);
//                                progressBar.setVisibility(View.VISIBLE);
                                linearLayoutBar.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                if(item.getAppinstalled().equals("yes")){
                                    btnOperaCard.setText("已绑卡");
                                    btnOperaCard.setEnabled(false);
                                }
                                else
                                {
                                    btnOperaCard.setText("绑卡");
                                    btnOperaCard.setEnabled(true);
                                }
                                btnOperaCard.setVisibility(View.VISIBLE);
//                                progressBar.setVisibility(View.INVISIBLE);
                                linearLayoutBar.setVisibility(View.INVISIBLE);
                            }
                            btnOperaCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //连接BLE
                                    MyApplication app = (MyApplication) getActivity().getApplication();
                                    final String bluetoothDevAddr = app.getBluetoothDevAddr();
                                    if(bluetoothDevAddr.isEmpty()){
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("提示")
                                                .setMessage("没有选择蓝牙设备，请到“设置”页面选择")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                }).show();
                                        return;
                                    }
                                    //修改listview中button视图，修改item的值，就相当于修改了appList变量
                                    item.setAppinstalling(true);
                                    //修改全局变量map中的值
                                    MyApplication.appInstalling.put(item.getIndex(),true);
                                    //刷新Listview
                                    notifyDataSetChanged();
                                    //获取蓝牙读写句柄
                                    //if(bluetoothControl==null){
                                    final BluetoothControl bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),bluetoothDevAddr);
                                    bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
                                        @Override
                                        public void onBLEPrepared() {
                                            //BLE已连接上，且SE通道已打开
                                            LogUtil.debug("onBLEPrepared");
                                            //获取taskid
                                            RequestTaskidEntity entity=new RequestTaskidEntity();
                                            String appid = item.getAppid();
                                            byte[] bAppid = new byte[5];
                                            byte[] data = ByteUtil.StringToByteArray(appid);
                                            System.arraycopy(data,0,bAppid,5-data.length,data.length);
                                            //下载应用
                                            entity.setTasktype(TASK_TYPE_DOWNLOAD);
                                            String strCmd = TASK_TYPE_DOWNLOAD+"05"+ByteUtil.bytesToString(bAppid,5);
                                            entity.setTaskcommand(strCmd);
                                            //同步应用
//                                    entity.setTasktype(TASK_TYPE_SYNC);
//                                    entity.setTaskcommand("DA0103");
                                            ApplyPersonalizationService.getTSMTaskid(seId, "dbinsert", entity, new TSMAppInformationCallback() {
                                                @Override
                                                public void getAppInfo(String xml) {
                                                    //解析xml
                                                    TSMResponseEntity entity = MessageBuilder.parseDownLoadXml(xml);
                                                    String taskId = entity.getTaskId();
                                                    int dectask = ByteUtil.parseInt(taskId,10,0);
                                                    byte[] data = ByteUtil.int2Bytes(dectask);
                                                    byte[] bTaskId = new byte[20];
                                                    System.arraycopy(data,0,bTaskId,20-data.length,data.length);
                                                    item.setIndexForlistview(position);//标识在listview中的位置
                                                    new BussinessTransaction().DownloadApplet(bluetoothControl,bTaskId,item);
                                                    //DeleteApplet(bTaskId,handler);
                                                }
                                            });

                                        }
                                    });
                                    //}
                                }
                            });
                        }


                    };
                    listViewAppStore.setAdapter(busAdapter);
                    loading.setVisibility(View.INVISIBLE);
                }
            }
        };
        //获取AppInformation List
        if(appList!=null){
            handler.sendEmptyMessage(0);
        }
        else {
            //从数据库获取appInformation
            // 查询Book表中所有的数据
            if (MyApplication.dataFromNet) {
                appList = new ArrayList<AppInformation>();
                SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
                Cursor cursor = dbRead.query("appinfo", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        String appindex = cursor.getString(cursor.
                                getColumnIndex("appindex"));
                        String picurl = cursor.getString(cursor.
                                getColumnIndex("picurl"));
                        String appname = cursor.getString(cursor.
                                getColumnIndex("appname"));
                        String appsize = cursor.getString(cursor.
                                getColumnIndex("appsize"));
                        String apptype = cursor.getString(cursor.
                                getColumnIndex("apptype"));
                        String spname = cursor.getString(cursor.
                                getColumnIndex("spname"));
                        String appdesc = cursor.getString(cursor.
                                getColumnIndex("appdesc"));
                        String appinstalled = cursor.getString(cursor.
                                getColumnIndex("appinstalled"));
                        String appid = cursor.getString(cursor.
                                getColumnIndex("appid"));
                        String localpicpath = cursor.getString(cursor.
                                getColumnIndex("localpicpath"));
                        AppInformation appInformation = new AppInformation(appindex,picurl,appname,appsize,apptype,
                                spname,appdesc,appinstalled,appid,false,-1,localpicpath);
                        appList.add(appInformation);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                //获取全局变量map中的值给appList
                for (Map.Entry<String, Boolean> entry : MyApplication.appInstalling.entrySet()) {
                    String index = entry.getKey();
                    Boolean installing = entry.getValue();
                    for (int i = 0; i < appList.size(); i++) {
                        AppInformation appInfo = appList.get(i);
                        if (appInfo.getIndex().equals(index)) {
                            appInfo.setAppinstalling(installing);
                        }
                    }
                }
                handler.sendEmptyMessage(0);
            }
            else {
                //从网络获取appInformation
                String requestType = "dbquery";
                String requestData = "applistquery";
                ApplyPersonalizationService.getAppinfoFromWebservice(seId, requestType, requestData, new TSMAppInformationCallback() {
                    @Override
                    public void getAppInfo(String xml) {
                        //解析xml
                        TSMResponseEntity entity = MessageBuilder.parseDownLoadXml(xml);
                        //获取List<AppInformation>
                        LogUtil.debug("get applist");
                        appList = entity.getAppInformationList();
                        //appInfoPrepared=true;
                        //获取全局变量map中的值给appList
                        for (Map.Entry<String, Boolean> entry : MyApplication.appInstalling.entrySet()) {
                            String index = entry.getKey();
                            Boolean installing = entry.getValue();
                            for (int i = 0; i < appList.size(); i++) {
                                AppInformation appInfo = appList.get(i);
                                if (appInfo.getIndex().equals(index)) {
                                    appInfo.setAppinstalling(installing);
                                }
                            }
                        }
                        handler.sendEmptyMessage(0);
                        //将数据写入数据库
                        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                        insertDB(dbWrite, appList);
                        MyApplication.dataFromNet = true;
                    }
                });
            }
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //注册receiver
        getActivity().registerReceiver(bussinessUpdateReceiver,makeBussinessUpdateIntentFilter());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_SPECIAL_APP && resultCode==RESULT_SPECIAL_APP){
            AppInformation appInformation = (AppInformation) data.getSerializableExtra("APP_INFO");
//            appInformationList.set(appInformation.getIndexForlistview(),appInformation);
            appList.set(appInformation.getIndexForlistview(),appInformation);
            busAdapter.notifyDataSetChanged();
        }
    }
//    public void getAppListData(){
//        AppInformation app1 = new AppInformation();
//        AppInformation app2 = new AppInformation();
//        AppInformation app3 = new AppInformation();
//        AppInformation app4 = new AppInformation();
//        AppInformation app5 = new AppInformation();
//        AppInformation app6 = new AppInformation();
//        AppInformation app7 = new AppInformation();
//
//        app1.setAppname("北京公交一卡通");
//        app1.setIconviewid(R.drawable.bjgj);
//        app1.setAppinstalling(false);
//        appInformationList.add(app1);
//
//        app2.setAppname("上海公交一卡通");
//        app2.setIconviewid(R.drawable.shgj);
//        app2.setAppinstalling(false);
//        appInformationList.add(app2);
//
//        app3.setAppname("天津公交一卡通");
//        app3.setIconviewid(R.drawable.tjgj);
//        app3.setAppinstalling(false);
//        appInformationList.add(app3);
//
//        app4.setAppname("中国银行");
//        app4.setIconviewid(R.drawable.china_bank);
//        app4.setAppinstalling(false);
//        appInformationList.add(app4);
//
//        app5.setAppname("中国农业银行");
//        app5.setIconviewid(R.drawable.abc_bank);;
//        app5.setAppinstalling(false);
//        appInformationList.add(app5);
//
//        app6.setAppname("中国建设银行");
//        app6.setIconviewid(R.drawable.ccb_bank);
//        app6.setAppinstalling(false);
//        appInformationList.add(app6);
//
//        app7.setAppname("中国工商银行");
//        app7.setIconviewid(R.drawable.icbc_bank);
//        app7.setAppinstalling(false);
//        appInformationList.add(app7);
//
//
//    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.debug("AppStoreFragment onPause");
        if(appList==null)
            LogUtil.debug("appList is null");
        //

    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.debug("AppStoreFragment onStop");
        if(appList==null)
            LogUtil.debug("appList is null");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.debug("AppStoreFragment onDestroy");
        if(appList==null)
            LogUtil.debug("appList is null");
        //取消receiver注册
        getActivity().unregisterReceiver(bussinessUpdateReceiver);
    }
}
