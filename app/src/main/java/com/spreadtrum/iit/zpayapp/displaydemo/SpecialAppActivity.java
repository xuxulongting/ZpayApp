package com.spreadtrum.iit.zpayapp.displaydemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.display.Card;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BLEPreparedCallbackListener;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPTransferData;
import com.spreadtrum.iit.zpayapp.network.tcp.TsmTaskCompleteListener;

/**
 * Created by SPREADTRUM\ting.long on 16-9-2.
 */
public class SpecialAppActivity extends AppCompatActivity {

    private TextView textViewAppName;
    private ImageView imageViewIcon;
    private Button btnOpera;
    private ProgressBar progressBar;
    private BluetoothControl bluetoothControl=null;
    private AppInformation appInformation;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case AppStoreFragment.TSM_COMPLETE_SUCCESS:
                    new AlertDialog.Builder(MyApplication.getContextObject())
                            .setTitle("提示")
                            .setMessage("绑卡成功")
                            .setPositiveButton("确定",null).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    btnOpera.setText("取消绑卡");
                    btnOpera.setVisibility(View.VISIBLE);
                    break;
                case AppStoreFragment.TSM_COMPLETE_FAILED:
                    new AlertDialog.Builder(MyApplication.getContextObject())
                            .setTitle("错误")
                            .setMessage("绑卡失败")
                            .setPositiveButton("确定",null).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    btnOpera.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    private Handler deleteHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case AppStoreFragment.TSM_COMPLETE_SUCCESS:
                    new AlertDialog.Builder(SpecialAppActivity.this).setTitle("提示")
                            .setMessage("取消绑卡成功")
                            .setPositiveButton("确定",null).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    btnOpera.setText("绑卡");
                    btnOpera.setVisibility(View.VISIBLE);
                    break;
                case AppStoreFragment.TSM_COMPLETE_FAILED:
                    new AlertDialog.Builder(SpecialAppActivity.this)
                            .setTitle("错误")
                            .setMessage("取消绑卡失败")
                            .setPositiveButton("确定",null).show();

                    progressBar.setVisibility(View.INVISIBLE);
                    btnOpera.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialapp);
        Intent intent = getIntent();
//        Card card = (Card) intent.getSerializableExtra("APP_PARAMETER");
//        String cardName = card.getCardName();
//        int resourceIdIcon = card.getCardView();
        appInformation = (AppInformation) intent.getSerializableExtra("APP_PARAMETER");
        String appName = appInformation.getAppname();
        int resourceIdIcon = appInformation.getIconviewid();
        boolean isInstalling = appInformation.isAppinstalling();
        String appInstalled = appInformation.getAppinstalled();
        textViewAppName = (TextView) findViewById(R.id.id_tv_appname);
        imageViewIcon = (ImageView) findViewById(R.id.id_iv_icon_large);
        btnOpera = (Button) findViewById(R.id.id_btn_status);
        progressBar = (ProgressBar) findViewById(R.id.id_pb_appProgressBar);
        textViewAppName.setText(appName);
        imageViewIcon.setImageResource(resourceIdIcon);
        if(isInstalling){
            btnOpera.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            if(appInstalled.equals("yes")) {
                btnOpera.setText("取消绑卡");
            }
            btnOpera.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
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
                    appInformation.setAppinstalling(true);
                    btnOpera.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    //获取蓝牙读写句柄
                    if(bluetoothControl==null){
                        bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),bluetoothDevAddr);
                        bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
                            @Override
                            public void onBLEPrepared() {
                                //BLE准备好，开始发送数据
                                TCPTransferData tcpTransferData = new TCPTransferData();
                                //tcpTransferData.SyncApplet(bluetoothControl);
                                byte[] id = {0x2F, 0x7A};
                                byte[] taskId = new byte[20];
                                System.arraycopy(id,0,taskId,18,2);
                                tcpTransferData.SyncApplet(bluetoothControl, taskId);
                                //android 视图控件只能在主线程中去访问，用消息的方式
                                tcpTransferData.setTsmTaskCompleteListener(new TsmTaskCompleteListener() {
                                    @Override
                                    public void onTaskExecutedSuccess() {
                                        appInformation.setAppinstalling(false);
                                        appInformation.setAppinstalled("yes");
                                        bluetoothControl=null;
                                        handler.sendEmptyMessage(AppStoreFragment.TSM_COMPLETE_SUCCESS);

                                    }

                                    @Override
                                    public void onTaskExecutedFailed(){
                                        appInformation.setAppinstalling(false);
                                        appInformation.setAppinstalled("no");
                                        bluetoothControl=null;
                                        handler.sendEmptyMessage(AppStoreFragment.TSM_COMPLETE_FAILED);

                                    }
                                });
                            }
                        });
                    }


                }else if (btnOpera.getText().equals("取消绑卡")){
                    new AlertDialog.Builder(SpecialAppActivity.this)
                            .setTitle("提示")
                            .setMessage("确认解除绑定?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //修改button
                                    appInformation.setAppinstalling(true);
                                    btnOpera.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.VISIBLE);
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
                                    //获取蓝牙读写句柄
                                    if(bluetoothControl==null){
                                        bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),bluetoothDevAddr);
                                        bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
                                            @Override
                                            public void onBLEPrepared() {
                                                //BLE准备好，开始发送数据
                                                TCPTransferData tcpTransferData = new TCPTransferData();
                                                //tcpTransferData.SyncApplet(bluetoothControl);
                                                byte[] id = {0x2F, 0x7A};
                                                byte[] taskId = new byte[20];
                                                System.arraycopy(id,0,taskId,18,2);
                                                tcpTransferData.SyncApplet(bluetoothControl, taskId);
                                                //android 视图控件只能在主线程中去访问，用消息的方式
                                                tcpTransferData.setTsmTaskCompleteListener(new TsmTaskCompleteListener() {
                                                    @Override
                                                    public void onTaskExecutedSuccess() {

                                                        appInformation.setAppinstalling(false);
                                                        appInformation.setAppinstalled("no");
                                                        bluetoothControl=null;
                                                        deleteHandler.sendEmptyMessage(AppStoreFragment.TSM_COMPLETE_SUCCESS);
                                                    }

                                                    @Override
                                                    public void onTaskExecutedFailed(){
                                                        appInformation.setAppinstalling(false);
                                                        appInformation.setAppinstalled("yes");
                                                        bluetoothControl=null;
                                                        deleteHandler.sendEmptyMessage(AppStoreFragment.TSM_COMPLETE_FAILED);
                                                    }
                                                });
                                            }
                                        });
                                    }

                                }
                            })
                            .setNegativeButton("取消",null).show();

                }
            }
        });
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
    }
}
