package com.spreadtrum.iit.zpayapp.displaydemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPNetParameter;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPSocket;
import com.spreadtrum.iit.zpayapp.utils.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.bussiness.TsmTaskCompleteCallback;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.BitmapCache;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.RequestQueueUtils;

/**
 * Created by SPREADTRUM\ting.long on 16-9-2.
 */
public class SpecialAppActivity extends BaseActivity {

    private TextView textViewAppName,textViewAppType,textViewSpName,textViewAppSize,textViewAppDesc;
    private ImageView imageViewIcon;
    private Button btnOpera;
    private LinearLayout linearLayoutBar;
    private AppInformation appInformation;
    private String bussinessType;   //下载/删除业务
    /**
     * 接收来自应用下载或者删除完成的广播消息，主要是为了更新变量appinstalling，appinstalled状态
     */
    private BroadcastReceiver bussinessUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        AppInformation appInfo = (AppInformation) intent.getSerializableExtra("BUSSINESS_UPDATE");
        if(!(appInfo.getIndex().equals(appInformation.getIndex())))
            return;
        appInformation.setAppinstalling(false);

        String bussinessType = intent.getStringExtra("BUSSINESS_TYPE");
        if(intent.getAction().equals(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS)) {
            MyApplication.isOperated = false;
            if (bussinessType.equals("download")) {
                appInformation.setAppinstalled("yes");
                linearLayoutBar.setVisibility(View.INVISIBLE);
                btnOpera.setText("解绑");
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
            if (intent.getAction().equals(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED))
                MyApplication.isOperated = false;
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
        String appIndex = appInformation.getIndex();
        String appName = appInformation.getAppname();
        String appSize = appInformation.getAppsize();
        String appType = appInformation.getApptype();
        String spName = appInformation.getSpname();
        String appDesc = appInformation.getAppdesc();
        String picUrl = appInformation.getPicurl();
//        String localPicpath = appInformation.getLocalpicpath();
        final String appInstalled = appInformation.getAppinstalled();
        String appLocked = appInformation.getApplocked();
        boolean isInstalling = appInformation.isAppinstalling(appIndex);

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
        //使用volley下载图片，并使用LruCache进行缓存
        RequestQueue requestQueue = RequestQueueUtils.getInstance().getRequestQueue();
        ImageLoader imageLoader = new ImageLoader(requestQueue,new BitmapCache());
        ImageLoader.ImageListener imageListener = imageLoader.getImageListener(imageViewIcon,R.drawable.refresh,R.drawable.refresh);
        int maxImageViewWidth = imageViewIcon.getMaxWidth();//获取imageview最大宽度和高度
        int maxImageViewHeight = imageViewIcon.getMaxHeight();
        imageLoader.get(picUrl,imageListener,maxImageViewWidth,maxImageViewHeight);//通过ImageView的最大宽度和高度对图片进行压缩

        if(isInstalling){
            btnOpera.setVisibility(View.INVISIBLE);
//            progressBar.setVisibility(View.VISIBLE);
            linearLayoutBar.setVisibility(View.VISIBLE);
        }
        else
        {
            if (appLocked.equals("yes")){
                btnOpera.setText("已锁定");
            }
            else if(appInstalled.equals("yes")) {
                btnOpera.setText("解绑");
            }
            btnOpera.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.INVISIBLE);
            linearLayoutBar.setVisibility(View.INVISIBLE);
        }
        //注册receiver
        registerReceiver(bussinessUpdateReceiver,makeBussinessUpdateIntentFilter());
        //下载/删除Applet
        btnOpera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //该点击事件完成绑卡或取消绑卡
                if(btnOpera.getText().equals("绑卡")){
                    bussinessType = "download";
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
                    //修改UI
                    //修改button
                    appInformation.setAppinstalling(true);  //为了与AppStoreFragment的button同步
                    //修改全局变量map中的值
                    MyApplication.appInstalling.put(appInformation.getIndex(),true);
                    btnOpera.setVisibility(View.INVISIBLE);
                    linearLayoutBar.setVisibility(View.VISIBLE);
                    final BussinessTransaction transaction = new BussinessTransaction();
                    transaction.transactBussiness(appInformation, BussinessTransaction.TASK_TYPE_DOWNLOAD,
                            new TsmTaskCompleteCallback() {
                                @Override
                                public void onTaskExecutedSuccess() {
                                    MyApplication.isOperated = false;
                                    new BussinessBroadcast().broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS,appInformation,"download");
                                    MyApplication.handler.sendEmptyMessage(MyApplication.DOWNLOAD_SUCCESS);
                                }

                                @Override
                                public void onTaskExecutedFailed() {
                                    //如果当前没有下载/删除动作，则该广播无效，主要是蓝牙主动断开连接（下载完成）或意外断开连接
                                    if (MyApplication.isOperated==false)
                                        return;
                                    MyApplication.isOperated = false;
                                    new BussinessBroadcast().broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED,appInformation,"download");
                                    MyApplication.handler.sendEmptyMessage(MyApplication.DOWNLOAD_FAILED);
                                }

                                @Override
                                public void onTaskNotExecuted() {
                                    MyApplication.isOperated = false;
                                    new BussinessBroadcast().broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_NOT_EXECUTED,appInformation,"notexecuted");
                                }
                            });
                }else if (btnOpera.getText().equals("解绑")){
                    bussinessType = "delete";
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
                                    //修改UI
                                    //修改button
                                    appInformation.setAppinstalling(true);
                                    //修改全局变量map中的值
//                                    MyApplication.appInstalling.put(appInformation.getIndex(),appInformation.isAppinstalling());
                                    MyApplication.appInstalling.put(appInformation.getIndex(),true);
                                    btnOpera.setVisibility(View.INVISIBLE);
                                    linearLayoutBar.setVisibility(View.VISIBLE);
                                    //开始任务
                                    final BussinessTransaction transaction = new BussinessTransaction();
                                    transaction.transactBussiness(appInformation, BussinessTransaction.TASK_TYPE_DELETE,
                                            new TsmTaskCompleteCallback() {
                                                @Override
                                                public void onTaskExecutedSuccess() {
                                                    MyApplication.isOperated=false;
                                                    new BussinessBroadcast().broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS,appInformation,"delete");
                                                    MyApplication.handler.sendEmptyMessage(MyApplication.DELETE_SUCCESS);
                                                    //java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
//                                                    Toast.makeText(MyApplication.getContextObject(),"解绑成功",Toast.LENGTH_LONG).show();
                                                }

                                                @Override
                                                public void onTaskExecutedFailed() {
                                                    if (MyApplication.isOperated==false)
                                                        return;
                                                    MyApplication.isOperated=false;
                                                    new BussinessBroadcast().broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED,appInformation,"delete");
                                                    MyApplication.handler.sendEmptyMessage(MyApplication.DELETE_FAILED);

                                                }

                                                @Override
                                                public void onTaskNotExecuted() {
                                                    MyApplication.isOperated=false;
                                                    new BussinessBroadcast().broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_NOT_EXECUTED,appInformation,"notexecuted");

                                                }
                                            });
                                }
                            })
                            .setNegativeButton("取消",null).show();

                }
            }
        });

        //取消业务
        linearLayoutBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭tsm连接
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TCPSocket tcpSocket =  TCPSocket.getInstance(TCPNetParameter.IPAddress, TCPNetParameter.Port);
                        tcpSocket.closeSocket();
                        if (bussinessType.equals("download"))
                            new BussinessBroadcast().broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED,
                                appInformation,"download");
                        else if (bussinessType.equals("delete"))
                            new BussinessBroadcast().broadcastUpdate(BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED,
                                    appInformation,"delete");

                        MyApplication.isOperated = false;
                    }
                }).start();
//                //关闭蓝牙连接
//                MyApplication app = (MyApplication) mContext.getApplicationContext();
//                BluetoothControl bluetoothControl = BluetoothControl.getInstance(mContext,
//                        app.getBluetoothDevAddr());
//                bluetoothControl.disconnectBluetooth();


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

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
        bussinessUpdateIntentFilter.addAction(BussinessTransaction.ACTION_BUSSINESS_NOT_EXECUTED);
        return bussinessUpdateIntentFilter;
    }
}
