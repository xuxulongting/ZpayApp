package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.spreadtrum.iit.zpayapp.network.tcp.TCPTransferData;
import com.spreadtrum.iit.zpayapp.network.tcp.TsmTaskCompleteListener;
import com.spreadtrum.iit.zpayapp.network.webservice.ApplyPersonalizationService;
import com.spreadtrum.iit.zpayapp.network.webservice.TSMAppInformationCallback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

//import com.zhy.adapter.recyclerview.CommonAdapter;



/**
 * Created by SPREADTRUM\ting.long on 16-9-1.
 */
public class AppStoreFragment extends Fragment {
    private GridLayoutManager gridLayoutManagerBus;
    private GridLayoutManager gridLayoutManagerBank;
    private CommonAdapter busAdapter;
   // private CommonAdapter bankAdapter;
    private List<Card> listCardParameter = new ArrayList<Card>();
    private List<AppInformation> appInformationList = new ArrayList<AppInformation>();
    List<AppInformation> appList=null;
    private BluetoothControl bluetoothControl=null;
    private Button btnOperaCard;
    private ProgressBar progressBar;
    private File cache;
    private AppDisplayDatabaseHelper dbHelper;
    public static final int TSM_COMPLETE_SUCCESS=0;
    public static final int TSM_COMPLETE_FAILED=1;
    public static final int REQUEST_SPECIAL_APP=2;
    public static final int RESULT_SPECIAL_APP=3;
    public static final String DB_APPINFO="info.db";
    public static final String seId="451000000000000020160328000000010003";
    public static final String TASK_TYPE_DOWNLOAD="D1";
    public static final String TASK_TYPE_DELETE="D2";
    public static final String TASK_TYPE_SYNC="DA";
    private boolean appInfoPrepared = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //准备数据
        //getCardListData();
        getAppListData();
        //创建数据库
        dbHelper = new AppDisplayDatabaseHelper(getActivity(),"info.db",null,1);
        //创建缓存目录，存放图片，添加允许访问存储设备权限 "/storage/emulated/0/cache111"
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        cache = new File(Environment.getExternalStorageDirectory(), "cache111");
        if(!cache.exists()){
            cache.mkdirs();
        }
        //从网络获取数据
        String requestType = "dbquery";
        String requestData = "applistquery";
        getAppinfoFromWebservice(seId, requestType, requestData, new TSMAppInformationCallback() {
            @Override
            public void getAppInfo(String xml) {
                //解析xml
                TSMResponseEntity entity = MessageBuilder.parseDownLoadXml(xml);
                //获取List<AppInformation>
                LogUtil.debug("get applist");
                appList = entity.getAppInformationList();
                appInfoPrepared=true;
                //将数据写入数据库
                SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                insertDB(dbWrite,appList);
            }
        });
        while(!appInfoPrepared);

        //获取taskid
//        RequestTaskidEntity entity=new RequestTaskidEntity();
//        String appid = "1542";
//        byte[] bAppid = new byte[5];
//        byte[] data = ByteUtil.StringToByteArray(appid);
//        System.arraycopy(data,0,bAppid,5-data.length,data.length);
//        entity.setTasktype(TASK_TYPE_DOWNLOAD);
//        String strCmd = TASK_TYPE_DOWNLOAD+"05"+ByteUtil.bytesToString(bAppid,5);
//        entity.setTaskcommand(strCmd);
//        getTSMTaskid(seId, "dbinsert", entity, new TSMAppInformationCallback() {
//            @Override
//            public void getAppInfo(String xml) {
//                //解析xml
//                TSMResponseEntity entity = MessageBuilder.parseDownLoadXml(xml);
//                String taskId = entity.getTaskId();
//                //DownloadAppletFromTSMToSE(taskId.getBytes(),handler);
//            }
//        });

        String taskId = "12205";
        int task = ByteUtil.parseInt(taskId,10,0);
        byte[] data = ByteUtil.int2Bytes(task);
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

    /**
     *
     * @param seId
     * @param requestType
     * @param requestData
     * @param dbWrite
     */
    private void getAppInformationFromWebservice(String seId, String requestType, String requestData, final SQLiteDatabase dbWrite) {
        //创建请求xml
        String requestXml = MessageBuilder.doBussinessRequest(seId,requestType,requestData);
        //base64加密
        String requestXmlBase64 = Base64.encodeToString(requestXml.getBytes(),Base64.DEFAULT);
        //发送soap请求，并获取xml结果
        ApplyPersonalizationService.getTSMAppInformation(requestXmlBase64, new TSMAppInformationCallback() {
            @Override
            public void getAppInfo(String responseXml) {
                //解析xml
                TSMResponseEntity entity = MessageBuilder.parseDownLoadXml(responseXml);
                //获取List<AppInformation>
                appList = entity.getAppInformationList();
                insertDB(dbWrite,appList);
            }
        });
    }

    private void getAppinfoFromWebservice(String seId,String requestType,String requestData,TSMAppInformationCallback callback){
        //创建请求xml
        String requestXml = MessageBuilder.doBussinessRequest(seId,requestType,requestData);
        //base64加密
        String requestXmlBase64 = Base64.encodeToString(requestXml.getBytes(),Base64.DEFAULT);
        //发送soap请求，并获取xml结果
        ApplyPersonalizationService.getTSMAppInformation(requestXmlBase64,callback);
    }

    private void getTSMTaskid(String seId, String requestType, RequestTaskidEntity entity,TSMAppInformationCallback callback){
        //创建请求xml
        String requestXml = MessageBuilder.doBussinessRequest(seId,requestType,entity);
        //base64加密
        String requestXmlBase64 = Base64.encodeToString(requestXml.getBytes(),Base64.DEFAULT);
        //发送soap请求，并获取xml结果
        ApplyPersonalizationService.getTSMAppInformation(requestXmlBase64,callback);
    }

    private void getImageUri(final String url, final ViewHolder viewHolder, final AppInformation item){
        String filename = url.substring(url.lastIndexOf("."));
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new FileCallBack("",filename) {//cache.getAbsolutePath()
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(File response, int id) {
                        LogUtil.debug("onResponse:"+response.getAbsolutePath());
                        Uri uri = Uri.fromFile(response);
                        //显示
                        item.setImageUri(uri);
                        viewHolder.setImageUri(R.id.id_iv_appicon,uri);
                        //更新数据库
                        SQLiteDatabase dbwrite = dbHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("imageuri",uri.toString());
                        dbwrite.update(AppDisplayDatabaseHelper.TABLE_APPINFO,contentValues,"picurl=?",new String[]{url});
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_appstore,container,false);
        ListView listViewAppStore = (ListView) view.findViewById(R.id.id_listview_bus);
        listViewAppStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(view.getContext(),"CLICK",Toast.LENGTH_LONG).show();
                //Card card = listCardParameter.get(i);
                AppInformation appInformation = appInformationList.get(i);
                appInformation.setIndexForlistview(i);
                Intent intent = new Intent(view.getContext(),SpecialAppActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("APP_PARAMETER", (Serializable) appInformation);
                intent.putExtras(bundle);
                startActivityForResult(intent,REQUEST_SPECIAL_APP);
            }
        });

        busAdapter = new CommonAdapter<AppInformation>(view.getContext(), R.layout.list_item_appstore, appList){//appInformationList) {

            @Override
            protected void convert(final ViewHolder viewHolder, final AppInformation item, final int position) {
                //显示应用名称
                viewHolder.setText(R.id.id_tv_appname,item.getAppname());
                //显示图片
                viewHolder.setImageResource(R.id.id_iv_appicon,R.drawable.bjgj);//item.getIconviewid());
//                if(item.getImageUri()==null){
//                    //网络下载图片
//                    String url = item.getPicurl();
//                    getImageUri(url,viewHolder,item);
//                }
//                else{
//                    //本地图片
//                    viewHolder.setImageUri(R.id.id_iv_appicon,item.getImageUri());
//                }

                btnOperaCard = viewHolder.getView(R.id.id_btn_appopra);
                progressBar = viewHolder.getView(R.id.id_pb_appProgressBar);
                if(item.isAppinstalling()==true){
                    btnOperaCard.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                }
                else
                {
                    if(item.getAppinstalled().equals("yes")){
                        btnOperaCard.setText("已绑卡");
                        btnOperaCard.setEnabled(false);
                    }
                    btnOperaCard.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                btnOperaCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Handler handler = new Handler(){
                            public void handleMessage(Message msg){
                                switch (msg.what){
                                    case AppStoreFragment.TSM_COMPLETE_SUCCESS:
                                        item.setAppinstalling(false);
                                        item.setAppinstalled("yes");
                                        //更新数据库
                                        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("appinstalled","yes");
                                        dbWrite.update(AppDisplayDatabaseHelper.TABLE_APPINFO,contentValues,"appinstalled=?",new String[]{item.getAppname()});
                                        //刷新listview
                                        notifyDataSetChanged();
                                        break;
                                    case TSM_COMPLETE_FAILED:
                                        item.setAppinstalling(false);
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("错误")
                                                .setMessage("绑卡失败")
                                                .setPositiveButton("确定",null)
                                                .show();
                                        notifyDataSetChanged();
                                        break;
                                }
                            }
                        };
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
                        //修改listview中button视图
                        item.setAppinstalling(true);
                        notifyDataSetChanged();
                        //获取蓝牙读写句柄
                        if(bluetoothControl==null){
                            bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),bluetoothDevAddr);
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
                                    //删除应用
//                                    entity.setTasktype(TASK_TYPE_DELETE);
//                                    String strCmd = TASK_TYPE_DELETE+"05"+ByteUtil.bytesToString(bAppid,5);
//                                    entity.setTaskcommand(strCmd);

                                    getTSMTaskid(seId, "dbinsert", entity, new TSMAppInformationCallback() {
                                        @Override
                                        public void getAppInfo(String xml) {
                                            //解析xml
                                            TSMResponseEntity entity = MessageBuilder.parseDownLoadXml(xml);
                                            String taskId = entity.getTaskId();
                                            int dectask = ByteUtil.parseInt(taskId,10,0);
                                            byte[] data = ByteUtil.int2Bytes(dectask);
                                            byte[] bTaskId = new byte[20];
                                            System.arraycopy(data,0,bTaskId,20-data.length,data.length);
                                            DownloadAppletFromTSMToSE(bTaskId,handler);
                                            //DeleteApplet(bTaskId,handler);
                                        }
                                    });

                                }
                            });
                        }
                    }
                });
            }


        };
        listViewAppStore.setAdapter(busAdapter);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_SPECIAL_APP && resultCode==RESULT_SPECIAL_APP){
            AppInformation appInformation = (AppInformation) data.getSerializableExtra("APP_INFO");
            appInformationList.set(appInformation.getIndexForlistview(),appInformation);
            busAdapter.notifyDataSetChanged();
        }
    }

    public void DeleteApplet(byte[] taskId, final Handler handler){
        TCPTransferData tcpTransferData = new TCPTransferData();
        tcpTransferData.DeleteApplet(bluetoothControl,taskId);
        tcpTransferData.setTsmTaskCompleteListener(new TsmTaskCompleteListener() {
            @Override
            public void onTaskExecutedSuccess() {
                handler.sendEmptyMessage(TSM_COMPLETE_SUCCESS);
                bluetoothControl=null;
            }

            @Override
            public void onTaskExecutedFailed() {
                handler.sendEmptyMessage(TSM_COMPLETE_FAILED);
                bluetoothControl=null;
            }
        });
    }

    public void DownloadAppletFromTSMToSE(byte[] taskId,final Handler handler){
        //BLE准备好，开始发送数据
        TCPTransferData tcpTransferData = new TCPTransferData();
        //tcpTransferData.SyncApplet(bluetoothControl);
//        byte[] id = {0x2F, 0x7A};
//        byte[] taskId = new byte[20];
//        System.arraycopy(id,0,taskId,18,2);
        tcpTransferData.SyncApplet(bluetoothControl, taskId);
        //android 视图控件只能在主线程中去访问，用消息的方式
        tcpTransferData.setTsmTaskCompleteListener(new TsmTaskCompleteListener() {
            @Override
            public void onTaskExecutedSuccess() {
                handler.sendEmptyMessage(TSM_COMPLETE_SUCCESS);
                bluetoothControl=null;
            }

            @Override
            public void onTaskExecutedFailed(){
                handler.sendEmptyMessage(TSM_COMPLETE_FAILED);
                bluetoothControl=null;
            }
        });
    }
    public void getCardListData() {
        Card busCard1 = new Card("旅行交通",R.drawable.bjgj,"北京公交一卡通");
        Card busCard2 = new Card("旅行交通",R.drawable.shgj,"上海公交一卡通");
        Card busCard3 = new Card("旅行交通",R.drawable.tjgj,"天津公交一卡通");
        listCardParameter.add(busCard1);
        listCardParameter.add(busCard2);
        listCardParameter.add(busCard3);
        Card bankCard1 = new Card("金融",R.drawable.china_bank,"中国银行");
        Card bankCard2 = new Card("金融",R.drawable.abc_bank,"中国农业银行");
        Card bankCard3 = new Card("金融",R.drawable.ccb_bank,"中国建设银行");
        Card bankCard4 = new Card("金融",R.drawable.icbc_bank,"中国工商银行");
        listCardParameter.add(bankCard1);
        listCardParameter.add(bankCard2);
        listCardParameter.add(bankCard3);
        listCardParameter.add(bankCard4);
    }

    public void getAppListData(){
        AppInformation app1 = new AppInformation();
        AppInformation app2 = new AppInformation();
        AppInformation app3 = new AppInformation();
        AppInformation app4 = new AppInformation();
        AppInformation app5 = new AppInformation();
        AppInformation app6 = new AppInformation();
        AppInformation app7 = new AppInformation();

        app1.setAppname("北京公交一卡通");
        app1.setIconviewid(R.drawable.bjgj);
        app1.setAppinstalling(false);
        appInformationList.add(app1);

        app2.setAppname("上海公交一卡通");
        app2.setIconviewid(R.drawable.shgj);
        app2.setAppinstalling(false);
        appInformationList.add(app2);

        app3.setAppname("天津公交一卡通");
        app3.setIconviewid(R.drawable.tjgj);
        app3.setAppinstalling(false);
        appInformationList.add(app3);

        app4.setAppname("中国银行");
        app4.setIconviewid(R.drawable.china_bank);
        app4.setAppinstalling(false);
        appInformationList.add(app4);

        app5.setAppname("中国农业银行");
        app5.setIconviewid(R.drawable.abc_bank);;
        app5.setAppinstalling(false);
        appInformationList.add(app5);

        app6.setAppname("中国建设银行");
        app6.setIconviewid(R.drawable.ccb_bank);
        app6.setAppinstalling(false);
        appInformationList.add(app6);

        app7.setAppname("中国工商银行");
        app7.setIconviewid(R.drawable.icbc_bank);
        app7.setAppinstalling(false);
        appInformationList.add(app7);


    }
}
