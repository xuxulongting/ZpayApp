package com.spreadtrum.iit.zpayapp.bussiness;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.common.ByteUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.database.AppDisplayDatabaseHelper;
import com.spreadtrum.iit.zpayapp.message.APDUInfo;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.RequestTaskidEntity;
import com.spreadtrum.iit.zpayapp.message.TSMResponseData;
import com.spreadtrum.iit.zpayapp.message.TSMResponseEntity;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.NetParameter;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BLEPreparedCallbackListener;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.SECallbackTSMListener;
import com.spreadtrum.iit.zpayapp.network.heartbeat.TransactionCallback;
import com.spreadtrum.iit.zpayapp.network.http.HttpResponseCallback;
import com.spreadtrum.iit.zpayapp.network.http.HttpUtils;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPTransferData;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.CustomStringRequest;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.RequestQueueUtils;
import com.spreadtrum.iit.zpayapp.network.webservice.TSMAppInformationCallback;
import com.spreadtrum.iit.zpayapp.network.webservice.TSMPersonalizationWebservice;
import com.spreadtrum.iit.zpayapp.network.webservice.WebserviceHelper;

import java.util.List;

/**
 * Created by SPREADTRUM\ting.long on 16-9-19.
 */
public class BussinessTransaction{
    public static final String ACTION_BUSSINESS_EXECUTED_SUCCESS="com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS";
    public static final String ACTION_BUSSINESS_EXECUTED_FAILED="com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED";
    public static final String ACTION_BUSSINESS_NOT_EXECUTED="com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction.ACTION_BUSSINESS_NOT_EXECUTED";
    private AppDisplayDatabaseHelper dbHelper=null;

    /**
     *执行下载/删除/同步/个人化等与applet相关的任务
     * @param item  applet信息
     * @param taskType  任务类型
     * @param completeCallback 执行结果回调
     */
    public void transactBussiness(final AppInformation item, String taskType,
                                  final TsmTaskCompleteCallback completeCallback){
        //判断当前是否有任务
        if (MyApplication.isOperated == false)
            MyApplication.isOperated = true;
        else {
            Toast.makeText(MyApplication.getContextObject(),"已有任务",Toast.LENGTH_LONG).show();
            completeCallback.onTaskNotExecuted();
            return;
        }
        //获取task id
        RequestTaskidEntity entity=MessageBuilder.getRequestTaskidEntity(item, taskType);
        WebserviceHelper.getTSMTaskid(MyApplication.seId, "dbinsert", entity, new TSMAppInformationCallback() {
            @Override
            public void getAppInfo(String xml) {
                //解析xml
                TSMResponseEntity entity = MessageBuilder.parseDownLoadXml(xml);
                String taskId = entity.getTaskId();
                int dectask = ByteUtil.parseInt(taskId,10,0);
                byte[] data = ByteUtil.int2Bytes(dectask);
                final byte[] bTaskId = new byte[20];
                System.arraycopy(data,0,bTaskId,20-data.length,data.length);
//                item.setIndexForlistview(position);//标识在listview中的位置
                //开始任务
                MyApplication app = (MyApplication) MyApplication.getContextObject();
                final BluetoothControl bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),
                        app.getBluetoothDevAddr());
                if (bluetoothControl==null){
                    completeCallback.onTaskNotExecuted();
                    return;
                }
                bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
                    @Override
                    public void onBLEPrepared() {
                        TCPTransferData tcpTransferData = new TCPTransferData();
                        tcpTransferData.handleTaskOfApplet(bluetoothControl, bTaskId, completeCallback);
                    }

                    @Override
                    public void onBLEPrepareFailed() {

                    }
                });

            }
        });
    }

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
        if (MyApplication.isOperated == false)
            MyApplication.isOperated = true;
        else {
            Toast.makeText(MyApplication.getContextObject(),"已有任务",Toast.LENGTH_LONG).show();
            completeCallback.onTaskNotExecuted();
            return;
        }
        //BLE准备好，开始发送数据
        TCPTransferData tcpTransferData = new TCPTransferData();
        tcpTransferData.DownloadApplet(bluetoothControl,taskId,completeCallback);

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
        if (MyApplication.isOperated == false)
            MyApplication.isOperated = true;
        else {
            Toast.makeText(MyApplication.getContextObject(),"已有任务",Toast.LENGTH_LONG).show();
            completeCallback.onTaskNotExecuted();
            return;
        }
        //BLE准备好，开始发送数据
        TCPTransferData tcpTransferData = new TCPTransferData();
        tcpTransferData.DeleteApplet(bluetoothControl,taskId,completeCallback);
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
     * volley-okhttp方式连接TSM，处理下载任务
     * @param bluetoothControl
     * @param sessionId
     * @param taskId
     * @param xml   客户端业务发起请求数据XML，通过buildBussinessRequestXml()获取
     * @param callback
     */
    public void DownloadApplet(final BluetoothControl bluetoothControl,final String sessionId, final String taskId,
                               String xml, final TsmTaskCompleteCallback callback){

        String url = NetParameter.TSM_URL;
//        RequestQueue requestQueue = RequestQueueUtils.getRequestQueue();
        CustomStringRequest stringRequest = new CustomStringRequest(Request.Method.POST,url,xml.getBytes(), new Listener<String>() {
            @Override
            public void onResponse(String response) {
                //解析xml，该xml中包含多条APDU指令
                TSMResponseData tsmResponseData = MessageBuilder.parseBussinessResponseXml(response,sessionId,taskId);
                List<APDUInfo> apduInfoList = tsmResponseData.getApduInfoList();
                if(apduInfoList==null)
                    return;
                if(bluetoothControl==null)
                    return;
//                handleApduList(bluetoothControl,apduInfoList,0,callback);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueueUtils.getInstance().addToRequestQueue(stringRequest);
    }

    /**
     * 通过蓝牙发送给SE，循环处理APDU指令
     * @param bluetoothControl
     * @param apduInfoList
     * @param i List中第i个APDUInfo
     * @param callback
     */
    public void handleApduList(final BluetoothControl bluetoothControl, final List<APDUInfo> apduInfoList,
                               final int i, final TransactionCallback callback){
        LogUtil.debug("handleApduList");
        final APDUInfo apduInfo = apduInfoList.get(i);
        final int index = ByteUtil.parseInt(apduInfo.getIndex(),10,0);
        String apduStr = apduInfo.getAPDU();
        byte []apdu = ByteUtil.StringToByteArray(apduStr);
        bluetoothControl.communicateWithJDSe(apdu,apdu.length);
        bluetoothControl.setSeCallbackTSMListener(new SECallbackTSMListener() {
            @Override
            public void callbackTSM(byte[] responseData, int responseLen) {
                //APDU执行结果与期望结果一致
                APDUInfo info = new APDUInfo();
                info.setIndex(String.valueOf(index));
                String apdu = ByteUtil.bytesToHexString(responseData,responseLen-2);
                byte[] byteOfsw = new byte[2];
                System.arraycopy(responseData,responseLen-2,byteOfsw,0,2);
                String sw = ByteUtil.bytesToHexString(byteOfsw,2);
                info.setAPDU(apdu);
                info.setSW(sw);
                if(info.getSW().equals(apduInfo.getSW())){
                    if((i+1)==apduInfoList.size()){
                        callback.onTransactionSuccess(info);
                    }
                    else
                        handleApduList(bluetoothControl,apduInfoList,i+1,callback);

                }
                else
                    callback.onTransactionFailed(info);
            }

            @Override
            public void errorCallback() {
                callback.onTransactionFailed(apduInfo);
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
    }

    public static final String TASK_TYPE_DOWNLOAD="D1";
    public static final String TASK_TYPE_DELETE="D2";
    public static final String TASK_TYPE_PERSONALIZE="D4";
    public static final String TASK_TYPE_SYNC="DA";


}
