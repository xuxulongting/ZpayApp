package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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

/**
 * Created by SPREADTRUM\ting.long on 16-9-2.
 */
public class SpecialAppActivity extends AppCompatActivity {

    private TextView textViewAppName,textViewAppType,textViewSpName,textViewAppSize,textViewAppDesc;
    private ImageView imageViewIcon;
    private Button btnOpera;
//    private ProgressBar progressBar;
    private LinearLayout linearLayoutBar;
    private AppInformation appInformation;
private BroadcastReceiver bussinessUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        AppInformation appInfo = (AppInformation) intent.getSerializableExtra("BUSSINESS_UPDATE");
        if(!(appInfo.getIndex().equals(appInformation.getIndex())))
            return;
        appInformation.setAppinstalling(false);
        String bussinessType = intent.getStringExtra("BUSSINESS_TYPE");
        if(intent.getAction().equals(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS)) {
            if (bussinessType.equals("download")) {
                appInformation.setAppinstalled("yes");
                linearLayoutBar.setVisibility(View.INVISIBLE);
                btnOpera.setText("取消绑卡");
                btnOpera.setVisibility(View.VISIBLE);
            } else {
                appInformation.setAppinstalled("no");
                linearLayoutBar.setVisibility(View.INVISIBLE);
                btnOpera.setText("绑卡");
                btnOpera.setVisibility(View.VISIBLE);
            }
            //修改全局变量map中的值
//                MyApplication.appInstalling.put(appInformation.getIndex(), appInformation.isAppinstalling());
        }
        else
        {
            linearLayoutBar.setVisibility(View.INVISIBLE);
            btnOpera.setVisibility(View.VISIBLE);
        }
    }
};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialapp);
        Intent intent = getIntent();
        appInformation = (AppInformation) intent.getSerializableExtra("APP_PARAMETER");
        String appName = appInformation.getAppname();
        String appSize = appInformation.getAppsize();
        String appType = appInformation.getApptype();
        String spName = appInformation.getSpname();
        String appDesc = appInformation.getAppdesc();
//        Bitmap bitmap = appInformation.getBitmap();
        String localPicpath = appInformation.getLocalpicpath();
        final String appInstalled = appInformation.getAppinstalled();
//        int resourceIdIcon = appInformation.getIconviewid();

        boolean isInstalling = appInformation.isAppinstalling();

        textViewAppName = (TextView) findViewById(R.id.id_tv_appname);
        imageViewIcon = (ImageView) findViewById(R.id.id_iv_icon_large);
        textViewAppDesc = (TextView) findViewById(R.id.id_tv_appdesc);
        textViewAppSize = (TextView) findViewById(R.id.id_tv_appsize);
        textViewAppType = (TextView) findViewById(R.id.id_tv_apptype);
        textViewSpName = (TextView) findViewById(R.id.id_tv_spname);

        btnOpera = (Button) findViewById(R.id.id_btn_status);
        //progressBar = (ProgressBar) findViewById(R.id.id_pb_appProgressBar);
        linearLayoutBar  = (LinearLayout) findViewById(R.id.id_ll_downloading);
        textViewAppName.setText(appName);
        textViewAppType.setText(appType);
        textViewAppSize.setText(appSize);
        textViewSpName.setText(spName);
        textViewAppDesc.setText("\t\t"+appDesc);
//        imageViewIcon.setImageResource(resourceIdIcon);
        Bitmap bitmap = AppStoreFragment.getLoacalBitmap(localPicpath);
//        imageViewIcon.setImageBitmap(bitmap);
        imageViewIcon.setImageBitmap(bitmap);
        if(isInstalling){
            btnOpera.setVisibility(View.INVISIBLE);
//            progressBar.setVisibility(View.VISIBLE);
            linearLayoutBar.setVisibility(View.VISIBLE);
        }
        else
        {
            if(appInstalled.equals("yes")) {
                btnOpera.setText("取消绑卡");
            }
            btnOpera.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.INVISIBLE);
            linearLayoutBar.setVisibility(View.INVISIBLE);
        }
        //注册receiver
        registerReceiver(bussinessUpdateReceiver,makeBussinessUpdateIntentFilter());
        btnOpera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //该点击事件完成绑卡或取消绑卡
                if(btnOpera.getText().equals("绑卡")){

                    //连接BLE
                    MyApplication app = (MyApplication) getApplication();
                    final String bluetoothDevAddr = app.getBluetoothDevAddr();
                    if(bluetoothDevAddr.isEmpty()){
                        new AlertDialog.Builder(SpecialAppActivity.this)
                                .setTitle("提示")
                                .setMessage("没有选择蓝牙设备，请到“设置”页面选择")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                        return;
                    }
                    //修改button
                    appInformation.setAppinstalling(true);  //为了与AppStoreFragment的button同步
                    //修改全局变量map中的值
                    MyApplication.appInstalling.put(appInformation.getIndex(),appInformation.isAppinstalling());
                    btnOpera.setVisibility(View.INVISIBLE);
//                    progressBar.setVisibility(View.VISIBLE);
                    linearLayoutBar.setVisibility(View.VISIBLE);
                    //获取蓝牙读写句柄
                    //if(bluetoothControl==null){
                    final BluetoothControl bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),bluetoothDevAddr);
                        bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
                            @Override
                            public void onBLEPrepared() {
                                //获取taskid
                                RequestTaskidEntity entity=new RequestTaskidEntity();
                                String appid = appInformation.getAppid();
                                byte[] bAppid = new byte[5];
                                byte[] data = ByteUtil.StringToByteArray(appid);
                                System.arraycopy(data,0,bAppid,5-data.length,data.length);
                                //下载应用
                                entity.setTasktype(AppStoreFragment.TASK_TYPE_DOWNLOAD);
                                String strCmd = AppStoreFragment.TASK_TYPE_DOWNLOAD+"05"+ByteUtil.bytesToString(bAppid,5);
                                entity.setTaskcommand(strCmd);
                                ApplyPersonalizationService.getTSMTaskid(AppStoreFragment.seId, "dbinsert", entity, new TSMAppInformationCallback() {
                                    @Override
                                    public void getAppInfo(String xml) {
                                        //解析xml
                                        TSMResponseEntity entity = MessageBuilder.parseDownLoadXml(xml);
                                        String taskId = entity.getTaskId();
                                        int dectask = ByteUtil.parseInt(taskId,10,0);
                                        byte[] data = ByteUtil.int2Bytes(dectask);
                                        byte[] bTaskId = new byte[20];
                                        System.arraycopy(data,0,bTaskId,20-data.length,data.length);
                                        //DownloadApplet(bTaskId);
                                        new BussinessTransaction().DownloadApplet(bluetoothControl,bTaskId,appInformation);;
                                    }
                                });
                            }
                        });
                    //}


                }else if (btnOpera.getText().equals("取消绑卡")){
                    new AlertDialog.Builder(SpecialAppActivity.this)
                            .setTitle("提示")
                            .setMessage("确认解除绑定?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    //连接BLE
                                    MyApplication app = (MyApplication) getApplication();
                                    final String bluetoothDevAddr = app.getBluetoothDevAddr();
                                    if(bluetoothDevAddr.isEmpty()){
                                        new AlertDialog.Builder(SpecialAppActivity.this)
                                                .setTitle("提示")
                                                .setMessage("没有选择蓝牙设备，请到“设置”页面选择")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                }).show();
                                        return;
                                    }
                                    //修改button
                                    appInformation.setAppinstalling(true);
                                    //修改全局变量map中的值
                                    MyApplication.appInstalling.put(appInformation.getIndex(),appInformation.isAppinstalling());
                                    btnOpera.setVisibility(View.INVISIBLE);
//                                    progressBar.setVisibility(View.VISIBLE);
                                    linearLayoutBar.setVisibility(View.VISIBLE);
                                    //获取蓝牙读写句柄
                                    //if(bluetoothControl==null){
                                    final BluetoothControl bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),bluetoothDevAddr);
                                        bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
                                            @Override
                                            public void onBLEPrepared() {
                                                //删除应用
                                                String appid = appInformation.getAppid();
                                                byte[] bAppid = new byte[5];
                                                byte[] data = ByteUtil.StringToByteArray(appid);
                                                System.arraycopy(data,0,bAppid,5-data.length,data.length);
                                                RequestTaskidEntity entity = new RequestTaskidEntity();
                                                entity.setTasktype(AppStoreFragment.TASK_TYPE_DELETE);
                                                String strCmd = AppStoreFragment.TASK_TYPE_DELETE+"05"+ ByteUtil.bytesToString(bAppid,5);
                                                entity.setTaskcommand(strCmd);
                                                ApplyPersonalizationService.getTSMTaskid(AppStoreFragment.seId, "dbinsert", entity, new TSMAppInformationCallback() {
                                                    @Override
                                                    public void getAppInfo(String xml) {
                                                        //解析xml
                                                        TSMResponseEntity entity = MessageBuilder.parseDownLoadXml(xml);
                                                        String taskId = entity.getTaskId();
                                                        int dectask = ByteUtil.parseInt(taskId,10,0);
                                                        byte[] data = ByteUtil.int2Bytes(dectask);
                                                        byte[] bTaskId = new byte[20];
                                                        System.arraycopy(data,0,bTaskId,20-data.length,data.length);
                                                        //DeleteApplet(bTaskId);
                                                        new BussinessTransaction().DeleteApplet(bluetoothControl,bTaskId,appInformation);
                                                    }
                                                });

                                            }
                                        });
                                    //}

                                }
                            })
                            .setNegativeButton("取消",null).show();

                }
            }
        });
    }

//    private void DownloadApplet(byte[] taskId){
//        //BLE准备好，开始发送数据
//        TCPTransferData tcpTransferData = new TCPTransferData();
//        //tcpTransferData.SyncApplet(bluetoothControl);
//        tcpTransferData.DownloadApplet(bluetoothControl,taskId);
//        //android 视图控件只能在主线程中去访问，用消息的方式
//        tcpTransferData.setTsmTaskCompleteListener(new TsmTaskCompleteListener() {
//            @Override
//            public void onTaskExecutedSuccess() {
//                appInformation.setAppinstalling(false);
//                appInformation.setAppinstalled("yes");
//                bluetoothControl=null;
//                handler.sendEmptyMessage(AppStoreFragment.TSM_COMPLETE_SUCCESS);
//
//            }
//
//            @Override
//            public void onTaskExecutedFailed(){
//                appInformation.setAppinstalling(false);
//                appInformation.setAppinstalled("no");
//                bluetoothControl=null;
//                handler.sendEmptyMessage(AppStoreFragment.TSM_COMPLETE_FAILED);
//
//            }
//        });
//    }
//
//    private void DeleteApplet(byte []taskId){
//        //BLE准备好，开始发送数据
//        TCPTransferData tcpTransferData = new TCPTransferData();
//        //tcpTransferData.SyncApplet(bluetoothControl, taskId);
//        tcpTransferData.DeleteApplet(bluetoothControl,taskId);
//        //android 视图控件只能在主线程中去访问，用消息的方式
//        tcpTransferData.setTsmTaskCompleteListener(new TsmTaskCompleteListener() {
//            @Override
//            public void onTaskExecutedSuccess() {
//
//                appInformation.setAppinstalling(false);
//                appInformation.setAppinstalled("no");
//                bluetoothControl=null;
//                deleteHandler.sendEmptyMessage(AppStoreFragment.TSM_COMPLETE_SUCCESS);
//            }
//
//            @Override
//            public void onTaskExecutedFailed(){
//                appInformation.setAppinstalling(false);
//                appInformation.setAppinstalled("yes");
//                bluetoothControl=null;
//                deleteHandler.sendEmptyMessage(AppStoreFragment.TSM_COMPLETE_FAILED);
//            }
//        });
//    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        LogUtil.debug("onBackPressed");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("APP_INFO",appInformation);
        intent.putExtras(bundle);
        setResult(AppStoreFragment.RESULT_SPECIAL_APP,intent);
        //finish();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.debug("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.debug("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.debug("onDestroy");
        unregisterReceiver(bussinessUpdateReceiver);
    }

    private IntentFilter makeBussinessUpdateIntentFilter(){
        IntentFilter bussinessUpdateIntentFilter = new IntentFilter();
        bussinessUpdateIntentFilter.addAction(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS);
        bussinessUpdateIntentFilter.addAction(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED);
        return bussinessUpdateIntentFilter;
    }
}
