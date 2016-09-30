package com.spreadtrum.iit.zpayapp.displaydemo;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.database.AppDisplayDatabaseHelper;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.SECallbackTSMListener;
import com.spreadtrum.iit.zpayapp.network.http.HttpResponseCallback;
import com.spreadtrum.iit.zpayapp.network.http.HttpUtils;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPTransferData;
import com.spreadtrum.iit.zpayapp.network.tcp.TsmTaskCompleteCallback;
import com.spreadtrum.iit.zpayapp.network.tcp.TsmTaskCompleteListener;

/**
 * Created by SPREADTRUM\ting.long on 16-9-19.
 */
public class BussinessTransaction{
    public static final String ACTION_BUSSINESS_EXECUTED_SUCCESS="com.spreadtrum.iit.zpayapp.displaydemo.BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS";
    public static final String ACTION_BUSSINESS_EXECUTED_FAILED="com.spreadtrum.iit.zpayapp.displaydemo.BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED";
    private AppDisplayDatabaseHelper dbHelper=null;

    /**
     *
     * @param bluetoothControl  与蓝牙通信实例
     * @param taskId    任务id
     * @param appInformation    应用信息
     * @param completeCallback  响应结果回调
     */
    public void SyncApplet(BluetoothControl bluetoothControl,byte[] taskId,
                            AppInformation appInformation,TsmTaskCompleteCallback completeCallback){
        //BLE准备好，开始发送数据
        TCPTransferData tcpTransferData = new TCPTransferData();
//        tcpTransferData.SyncApplet(bluetoothControl, taskId);
        tcpTransferData.SyncApplet(bluetoothControl,taskId,completeCallback);
    }

    /**
     *
     * @param bluetoothControl  与蓝牙通信实例
     * @param taskId    任务id
     * @param appInformation    应用信息
     * @param completeCallback  响应结果回调
     */
    public void DownloadApplet(BluetoothControl bluetoothControl, byte[] taskId,
                               final AppInformation appInformation, TsmTaskCompleteCallback completeCallback){
        //BLE准备好，开始发送数据
        TCPTransferData tcpTransferData = new TCPTransferData();
//        tcpTransferData.SyncApplet(bluetoothControl, taskId);
        tcpTransferData.DownloadApplet(bluetoothControl,taskId,completeCallback);
        //android 视图控件只能在主线程中去访问，用消息的方式
//        tcpTransferData.setTsmTaskCompleteListener(new TsmTaskCompleteListener() {
//            @Override
//            public void onTaskExecutedSuccess() {
//                broadcastUpdate(ACTION_BUSSINESS_EXECUTED_SUCCESS,appInformation,"download");
//                MyApplication.handler.sendEmptyMessage(MyApplication.DOWNLOAD_SUCCESS);
//            }
//
//            @Override
//            public void onTaskExecutedFailed(){
//                broadcastUpdate(ACTION_BUSSINESS_EXECUTED_FAILED,appInformation,"download");
//                MyApplication.handler.sendEmptyMessage(MyApplication.DOWNLOAD_FAILED);
//            }
//        });
    }

    /**
     *
     * @param bluetoothControl  与蓝牙通信实例
     * @param taskId    任务id
     * @param appInformation    应用信息
     * @param completeCallback  响应结果回调
     */
    public void DeleteApplet(BluetoothControl bluetoothControl, byte []taskId,
                                    final AppInformation appInformation,TsmTaskCompleteCallback completeCallback){
        //BLE准备好，开始发送数据
        TCPTransferData tcpTransferData = new TCPTransferData();
        //tcpTransferData.SyncApplet(bluetoothControl, taskId);
        tcpTransferData.DeleteApplet(bluetoothControl,taskId,completeCallback);
        //android 视图控件只能在主线程中去访问，用消息的方式
//        tcpTransferData.setTsmTaskCompleteListener(new TsmTaskCompleteListener() {
//            @Override
//            public void onTaskExecutedSuccess() {
//                broadcastUpdate(ACTION_BUSSINESS_EXECUTED_SUCCESS,appInformation,"delete");
//                MyApplication.handler.sendEmptyMessage(MyApplication.DELETE_SUCCESS);
//
//            }
//
//            @Override
//            public void onTaskExecutedFailed(){
//                broadcastUpdate(ACTION_BUSSINESS_EXECUTED_FAILED,appInformation,"delete");
//                MyApplication.handler.sendEmptyMessage(MyApplication.DELETE_FAILED);
//
//            }
//        });
    }

    /**
     * http方式连接TSM，下载应用
     * @param bluetoothControl  蓝牙连接实例
     * @param xml   请求xml
     * @param completeCallback  下载完成响应回调
     */
    public void DownloadApplet(final BluetoothControl bluetoothControl, String xml, TsmTaskCompleteCallback completeCallback) {
        String url="";
        HttpUtils.xmlStringRequest(url, xml, new HttpResponseCallback<String>() {
            @Override
            public void onSuccess(String response) {
                //解析xml，该xml中包含多条APDU指令
                byte[] data=null;
                int length=0;
                //调用蓝牙
                if(bluetoothControl!=null){
                    bluetoothControl.communicateWithJDSe(data,length);
                    bluetoothControl.setSeCallbackTSMListener(new SECallbackTSMListener() {
                        //回调TSM
                        @Override
                        public void callbackTSM(byte[] responseData, int responseLen) {

                        }

                        @Override
                        public void errorCallback() {

                        }
                    });
                }
            }

            @Override
            public void onError(String errResponse) {

            }
        });
    }

    /**
     * 应用下载/删除完成后，发送广播
     * @param action
     * @param appInformation
     * @param bussiness "download” or "delete"
     */
    public void broadcastUpdate(String action, AppInformation appInformation,String bussiness){
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
        dbHelper = new AppDisplayDatabaseHelper(MyApplication.getContextObject(),"info.db",null,1);
        if(action.equals(ACTION_BUSSINESS_EXECUTED_SUCCESS)){
            ContentValues contentValues = new ContentValues();
            if(bussiness.equals("download")){
                contentValues.put("appinstalled", "yes");
            }
            else
                contentValues.put("appinstalled","no");
            SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
            dbWrite.update(AppDisplayDatabaseHelper.TABLE_APPINFO,contentValues,"appname=?",new String[]{appInformation.getAppname()});
        }
//        dbHelper.getWritableDatabase();
//                SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
//                ContentValues contentValues = new ContentValues();
//                contentValues.put("appinstalled", "yes");
//                dbWrite.update(AppDisplayDatabaseHelper.TABLE_APPINFO, contentValues, "appname=?", new String[]{appInformation.getAppname()});
    }

    public static final String TASK_TYPE_DOWNLOAD="D1";
    public static final String TASK_TYPE_DELETE="D2";
    public static final String TASK_TYPE_SYNC="DA";

}
