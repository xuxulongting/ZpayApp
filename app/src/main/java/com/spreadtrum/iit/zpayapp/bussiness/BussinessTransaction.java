package com.spreadtrum.iit.zpayapp.bussiness;

import android.util.Base64;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.spreadtrum.iit.zpayapp.common.AppGlobal;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.database.AppDisplayDatabaseHelper;
import com.spreadtrum.iit.zpayapp.message.APDUInfo;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.TSMRequestData;
import com.spreadtrum.iit.zpayapp.message.TSMResponseData;
import com.spreadtrum.iit.zpayapp.message.TSMResponseEntity;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BLEPreparedCallbackListener;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.SECallbackTSMListener;
import com.spreadtrum.iit.zpayapp.network.heartbeat.HeartBeatResultCallback;
import com.spreadtrum.iit.zpayapp.network.heartbeat.HeartBeatThread;
import com.spreadtrum.iit.zpayapp.network.heartbeat.TransactionCallback;
import com.spreadtrum.iit.zpayapp.network.http.HttpResponseCallback;
import com.spreadtrum.iit.zpayapp.network.http.HttpUtils;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPTransferData;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.CustomStringRequest;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.NetParameter;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.RequestQueueUtils;
import com.spreadtrum.iit.zpayapp.network.webservice.SoapXmlBuilder;
import com.spreadtrum.iit.zpayapp.network.webservice.TSMInformationCallback;
import com.spreadtrum.iit.zpayapp.network.webservice.WebserviceHelper;
import com.spreadtrum.iit.zpayapp.utils.ByteUtil;
import com.spreadtrum.iit.zpayapp.utils.LogUtil;

import java.util.List;

import static java.lang.Thread.currentThread;

/**
 * Created by SPREADTRUM\ting.long on 16-9-19.
 */
public class BussinessTransaction{
    public static final String ACTION_BUSSINESS_EXECUTED_SUCCESS="com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction.ACTION_BUSSINESS_EXECUTED_SUCCESS";
    public static final String ACTION_BUSSINESS_EXECUTED_FAILED="com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction.ACTION_BUSSINESS_EXECUTED_FAILED";
    public static final String ACTION_BUSSINESS_NOT_EXECUTED="com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction.ACTION_BUSSINESS_NOT_EXECUTED";
    private AppDisplayDatabaseHelper dbHelper=null;

    /**
     * 处理Applet下载/删除/同步/个人化等与applet相关的业务
     * @param item  applet信息
     * @param taskType  任务类型（下载/删除/同步/个人化）
     * @param completeCallback 执行结果回调
     */
    public void transactBussiness(final AppInformation item, String taskType,
                                  final TsmTaskCompleteCallback completeCallback){
        //判断当前是否有任务
        if (AppGlobal.isOperated == false)
            AppGlobal.isOperated = true;
        else {
            Toast.makeText(MyApplication.getContextObject(),"已有任务",Toast.LENGTH_LONG).show();
            completeCallback.onTaskNotExecuted();
            return;
        }
        //获取task id
//        RequestTaskidEntity entity=MessageBuilder.getRequestTaskidEntity(item, taskType);
        WebserviceHelper.getTSMTaskid(AppGlobal.seId, item,taskType, new TSMInformationCallback() {
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
//                MyApplication app = (MyApplication) MyApplication.getContextObject();
                final BluetoothControl bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),
                        AppGlobal.bluetoothDevAddr);
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
     * Apk与TSM和SE交互，处理心跳线程的任务（锁定/解锁Applet）
     * 该函数的实现流程：与TSM和SE进行交互，交易流程：客户端向TSM发起请求（requestXml)->TSM给出响应（APDU指令集）->交给SE循环处理->SE返回最后结果->交给ResultCallback
     * 与TSM交易流程：上述返回结果->
     * @param requestXml
     * @param callback
     */
    public void transactBussinessWithHeartBeat(String requestXml,
                                                      final HeartBeatResultCallback<String> callback){
        final TSMRequestData requestData = MessageBuilder.getTSMRequestDataFromXml(requestXml);
        final String sessionId = requestData.getSessionId();
        final String taskId = requestData.getTaskId();
        final String url = NetParameter.WEBSERVICE_RM_PATH;
        final String requestType = requestData.getType();
//        TSMPersonalizationWebservice.getRemoteManagementInfoFromWebservice(requestXml);
        //Base64编码
        String requestXmlBase64 = Base64.encodeToString(requestXml.getBytes(),Base64.DEFAULT);
        String soap = SoapXmlBuilder.readSoap("soap11_rm.xml");
        soap = soap.replace("123",requestXmlBase64);
        byte[] entity = soap.getBytes();
        LogUtil.debug("HEARTBEAT","CustomStringRequest ,thread id is:"+currentThread().getId());
        final CustomStringRequest stringRequest = new CustomStringRequest(Request.Method.POST,url,
                entity, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                /**
                 * volley的网络请求响应均返回到了主线程，所以不能在响应线程中做耗时操作
                 */
                LogUtil.debug("HEARTBEAT","CustomStringRequest onResponse,thread id is:"+currentThread().getId());
                LogUtil.debug("HEARTBEAT","response:"+response);
                String responseXml = SoapXmlBuilder.parseSOAP(response,"TSM_RMWS_InterfaceResult");
                if (responseXml.isEmpty()){
                    callback.onApduEmpty();
                    return;
                }
                //Base64解码
                byte[] decodeResponseXml = Base64.decode(responseXml.getBytes(),Base64.DEFAULT);
                String xmlResponse = new String(decodeResponseXml);
                //解析xml，该xml中包含多条APDU指令
                final TSMResponseData responseData = MessageBuilder.parseBussinessResponseXml(xmlResponse,sessionId,taskId);
                String finishFlag = responseData.getFinishFlag();
                //Tsm响应数据中finishFlag非0,则代表任务结束
                if (!finishFlag.equals("0")) {
                    //更新list
                    HeartBeatThread.broadcastRMUpdate(HeartBeatThread.ACTION_BUSSINESS_REMOTE_MANAGEMENT);
                    callback.onApduEmpty();
                    return;
                }
                final List<APDUInfo> apduInfoList = responseData.getApduInfoList();
                if(apduInfoList==null) {
                    callback.onApduEmpty();
                    return;
                }
                MyApplication app = (MyApplication) MyApplication.getContextObject();
                final BluetoothControl bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),
                        AppGlobal.bluetoothDevAddr);
                if (bluetoothControl==null){
                    callback.onApduEmpty();
                    return;
                }
                bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
                    @Override
                    public void onBLEPrepared() {
                        //交给SE处理APDU指令，返回最后一条APDU指令处理结果
                        handleApduList(bluetoothControl,apduInfoList,0, new TransactionCallback() {
                            @Override
                            public void onTransactionSuccess(APDUInfo apduInfo) {
                                LogUtil.debug("HEARTBEAT","onTransactionSuccess");
                                String responseXml = MessageBuilder.message_Response_handle(AppGlobal.seId,"","","4",sessionId,taskId,apduInfo,"0");
                                LogUtil.debug("HEARTBEAT",responseXml);
                                callback.onApduExcutedSuccess(responseXml);
                                //关闭蓝牙
                                bluetoothControl.disconnectBluetooth();

                            }
                            @Override
                            public void onTransactionFailed(APDUInfo apduInfo) {
                                LogUtil.debug("HEARTBEAT","onTransactionFailed");
                                String responseXml = MessageBuilder.message_Response_handle(AppGlobal.seId,"","","4",sessionId,taskId,apduInfo,"0");
                                LogUtil.debug("HEARTBEAT",responseXml);
                                callback.onApduExcutedFailed(responseXml);
                                //关闭蓝牙
                                if (bluetoothControl!=null)
                                    bluetoothControl.disconnectBluetooth();
                            }
                        });
                    }

                    @Override
                    public void onBLEPrepareFailed() {

                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.debug("HEARTBEAT","CustomStringRequest onResponse,thread id is:"+currentThread().getId());
                callback.onNetworkError(error.getMessage());
            }
        });
        RequestQueueUtils.getInstance().addToRequestQueue(stringRequest);
    }

    /**
     * 通过okhttp与TSM交互
     * @param requestData
     * @param requestTag
     * @param callback
     */
    public void transactWithTSM(TSMRequestData requestData, String requestTag,final ResultCallback callback){
        //创建request xml
        String requestXml = MessageBuilder.buildBussinessRequestXml(requestData.seId,requestData.imei,requestData.phone,requestData.type,
                requestData.sessionId,requestData.taskId,"reserved");
        final String sessionId = requestData.getSessionId();
        final String taskId = requestData.getTaskId();
        final String url = NetParameter.TSM_URL;
//        RequestQueue requestQueue = RequestQueueUtils.getRequestQueue();
        final CustomStringRequest stringRequest = new CustomStringRequest(Request.Method.POST, url,
                requestXml.getBytes(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (callback==null){
                    return;
                }
                if (response.isEmpty()){
                    callback.onFailed(response);
                }
                //解析xml，该xml中包含多条APDU指令
                final TSMResponseData responseData = MessageBuilder.parseBussinessResponseXml(response,sessionId,taskId);
                List<APDUInfo> apduInfoList = responseData.getApduInfoList();
                callback.onSuccess(apduInfoList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailed(error.getMessage());
            }
        });
        //添加到全局RequestQueue
//        requestQueue.add(stringRequest);
        RequestQueueUtils.getInstance().addToRequestQueue(stringRequest,requestTag);
    }

    /**
     * SE执行APDU List
     * @param bluetoothControl
     * @param apduInfoList
     * @param requestData
     * @param callback
     */
    public void transactWithSE(BluetoothControl bluetoothControl, List<APDUInfo> apduInfoList,TSMRequestData requestData,
                               final ResultCallback<String> callback){
        final String sessionId = requestData.getSessionId();
        final String taskId = requestData.getTaskId();
        final String type = requestData.getType();
        handleApduList(bluetoothControl,apduInfoList,0, new TransactionCallback() {
            @Override
            public void onTransactionSuccess(APDUInfo apduInfo) {
                String responseXml = MessageBuilder.message_Response_handle(AppGlobal.seId,"","",type,sessionId,taskId,apduInfo,"0");
                callback.onSuccess(responseXml);

            }
            @Override
            public void onTransactionFailed(APDUInfo apduInfo) {
                String responseXml = MessageBuilder.message_Response_handle(AppGlobal.seId,"","",type,sessionId,taskId,apduInfo,"0");
                callback.onFailed(responseXml);
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
        if (AppGlobal.isOperated == false)
            AppGlobal.isOperated = true;
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
        if (AppGlobal.isOperated == false)
            AppGlobal.isOperated = true;
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
    private void handleApduList(final BluetoothControl bluetoothControl, final List<APDUInfo> apduInfoList,
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
//                callback.onTransactionFailed(apduInfo);
            }
        });
    }
    public static final String TASK_TYPE_DOWNLOAD="D1";
    public static final String TASK_TYPE_DELETE="D2";
    public static final String TASK_TYPE_PERSONALIZE="D4";
    public static final String TASK_TYPE_SYNC="DA";
}
