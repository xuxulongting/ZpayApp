package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPTransferData;
import com.spreadtrum.iit.zpayapp.network.tcp.TsmTaskCompleteListener;

/**
 * Created by SPREADTRUM\ting.long on 16-9-19.
 */
public class BussinessTransaction{
    public static final String ACTION_BUSSINESS_EXECUTED_SUCCESS="com.spreadtrum.iit.zpayapp.displaydemo.BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS";
    public static final String ACTION_BUSSINESS_EXECUTED_FAILED="com.spreadtrum.iit.zpayapp.displaydemo.BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED";

//    private Handler handler = new Handler(){
//        public void handleMessage(Message msg){
//           switch (msg.what){
//               case DOWNLOAD_SUCCESS:
//                   Toast.makeText(MyApplication.getContextObject(),"绑卡成功",Toast.LENGTH_LONG).show();
//                   break;
//               case DOWNLOAD_FAILED:
//                   Toast.makeText(MyApplication.getContextObject(),"绑卡失败",Toast.LENGTH_LONG).show();
//                   break;
//               case DELETE_SUCCESS:
//                   Toast.makeText(MyApplication.getContextObject(),"取消绑卡成功",Toast.LENGTH_LONG).show();
//                   break;
//               case DELETE_FAILED:
//                   Toast.makeText(MyApplication.getContextObject(),"取消绑卡失败",Toast.LENGTH_LONG).show();
//                   break;
//           }
//        }
//    };

    public void DownloadApplet(BluetoothControl bluetoothControl, byte[] taskId,
                                      final AppInformation appInformation){
        //BLE准备好，开始发送数据
        TCPTransferData tcpTransferData = new TCPTransferData();
//        tcpTransferData.SyncApplet(bluetoothControl, taskId);
        tcpTransferData.DownloadApplet(bluetoothControl,taskId);
        //android 视图控件只能在主线程中去访问，用消息的方式
        tcpTransferData.setTsmTaskCompleteListener(new TsmTaskCompleteListener() {
            @Override
            public void onTaskExecutedSuccess() {
                broadcastUpdate(ACTION_BUSSINESS_EXECUTED_SUCCESS,appInformation,"download");
                //handler.sendEmptyMessage(DOWNLOAD_SUCCESS);
            }

            @Override
            public void onTaskExecutedFailed(){
                broadcastUpdate(ACTION_BUSSINESS_EXECUTED_FAILED,appInformation,"download");
                MyApplication.handler.sendEmptyMessage(MyApplication.DOWNLOAD_FAILED);
            }
        });
    }

    public void DeleteApplet(BluetoothControl bluetoothControl, byte []taskId,
                                    final AppInformation appInformation){
        //BLE准备好，开始发送数据
        TCPTransferData tcpTransferData = new TCPTransferData();
        //tcpTransferData.SyncApplet(bluetoothControl, taskId);
        tcpTransferData.DeleteApplet(bluetoothControl,taskId);
        //android 视图控件只能在主线程中去访问，用消息的方式
        tcpTransferData.setTsmTaskCompleteListener(new TsmTaskCompleteListener() {
            @Override
            public void onTaskExecutedSuccess() {
                broadcastUpdate(ACTION_BUSSINESS_EXECUTED_SUCCESS,appInformation,"delete");
                //handler.sendEmptyMessage(DELETE_SUCCESS);

            }

            @Override
            public void onTaskExecutedFailed(){
                broadcastUpdate(ACTION_BUSSINESS_EXECUTED_FAILED,appInformation,"delete");
                //handler.sendEmptyMessage(DELETE_FAILED);

            }
        });
    }

    private static void broadcastUpdate(String action, AppInformation appInformation,String bussiness){
        Intent intent = new Intent();
        intent.setAction(action);
        Bundle bundle = new Bundle();
        bundle.putSerializable("BUSSINESS_UPDATE",appInformation);
        bundle.putString("BUSSINESS_TYPE",bussiness);
        intent.putExtras(bundle);
        MyApplication.getContextObject().sendBroadcast(intent);
        //修改全局变量appInstalling
        MyApplication.appInstalling.put(appInformation.getIndex(),false);
        //更新数据库
//                SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
//                ContentValues contentValues = new ContentValues();
//                contentValues.put("appinstalled", "yes");
//                dbWrite.update(AppDisplayDatabaseHelper.TABLE_APPINFO, contentValues, "appname=?", new String[]{appInformation.getAppname()});
    }

    public static final int TSM_COMPLETE_SUCCESS=0;
    public static final int TSM_COMPLETE_FAILED=1;


}
