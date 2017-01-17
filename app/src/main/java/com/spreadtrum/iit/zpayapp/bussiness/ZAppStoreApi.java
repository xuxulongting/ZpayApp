package com.spreadtrum.iit.zpayapp.bussiness;

import android.util.Base64;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.common.ByteUtil;
import com.spreadtrum.iit.zpayapp.message.RequestTaskidEntity;
import com.spreadtrum.iit.zpayapp.message.TSMResponseEntity;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPTransferData;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.NetParameter;
import com.spreadtrum.iit.zpayapp.network.heartbeat.TransactionCallback;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.message.APDUInfo;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.TSMRequestData;
import com.spreadtrum.iit.zpayapp.message.TSMResponseData;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BLEPreparedCallbackListener;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.heartbeat.HeartBeatResultCallback;
import com.spreadtrum.iit.zpayapp.network.heartbeat.HeartBeatThread;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.CustomStringRequest;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.RequestQueueUtils;
import com.spreadtrum.iit.zpayapp.network.webservice.SoapXmlBuilder;
import com.spreadtrum.iit.zpayapp.network.webservice.TSMAppInformationCallback;
import com.spreadtrum.iit.zpayapp.network.webservice.WebserviceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static java.lang.Thread.currentThread;

/**
 * Created by SPREADTRUM\ting.long on 16-11-3.
 */

public class ZAppStoreApi {

    /**
     * 登录
     * @param userName
     * @param pwd
     * @param versionName
     * @param callback
     */
    public static void login(String userName, String pwd, String versionName, final ResultCallback<JSONObject> callback){
        if (callback==null)
            return;
        if (userName==null || pwd==null || versionName==null ||userName.isEmpty() || pwd.isEmpty() || versionName.isEmpty() ) {
            callback.onFailed("登录信息不完整");
            return;
        }
        callback.onPreStart();

//        RequestQueue requestQueue = RequestQueueUtils.getRequestQueue();//Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("version",versionName);
            jsonObject.put("logName",userName);
            jsonObject.put("logPwd",pwd);
        } catch (JSONException e) {
//            e.printStackTrace();
            callback.onFailed(e.getMessage());
        }
        try {
            LogUtil.debug("password:"+jsonObject.getString("logPwd"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, NetParameter.LOGIN_URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailed(error.getMessage());
            }
        });
        RequestQueueUtils.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    /**
     * 退出登录
     * @param user
     * @param token
     * @param versionName
     * @param callback
     */
    public static void logout(String user, String token, String versionName, final ResultCallback<JSONObject> callback){
        if (callback==null)
            return;
        if (user==null || token==null || versionName==null ||user.isEmpty() || token.isEmpty() || versionName.isEmpty() ) {
            callback.onFailed("信息不完整");
            return;
        }
        if(token.equals("")){
            Toast.makeText(MyApplication.getContextObject(),"您还没有登录",Toast.LENGTH_LONG).show();
            return;
        }
        //网络调用之前完成的操作
        callback.onPreStart();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("version",versionName);
            jsonObject.put("logName",user);
            jsonObject.put("token",token);

        } catch (JSONException e) {
//            e.printStackTrace();
            callback.onFailed(e.getMessage());
        }
//        final RequestQueue requestQueue = RequestQueueUtils.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, NetParameter.LOGOUT_URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailed(error.getMessage());

            }
        });
//        requestQueue.add(jsonObjectRequest);
        RequestQueueUtils.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    /**
     *注册
     * @param userName
     * @param pwd
     * @param versionName
     * @param checkCode
     * @param callback
     */
    public static void register(String userName, String pwd, String versionName, String checkCode, final ResultCallback<JSONObject> callback){
        if (callback==null)
            return;
        if (userName==null || pwd==null || versionName==null ||userName.isEmpty() || pwd.isEmpty() || versionName.isEmpty() ) {
            callback.onFailed("注册信息不完整");
            return;
        }
//        RequestQueue requestQueue = RequestQueueUtils.getRequestQueue();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("version",versionName);
            jsonObject.put("logName",userName);
            jsonObject.put("logPwd",pwd);
            jsonObject.put("checkCode",checkCode);
        } catch (JSONException e) {
            callback.onFailed(e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, NetParameter.REGISTER_URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailed(error.getMessage());
            }
        });
//        requestQueue.add(request);
        RequestQueueUtils.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    /**
     * 获取应用列表（检查蓝牙设备地址，获取Seid,获取应用列表）
     * @param bleDevAddr
     * @param callback
     */
    public static void getListDataFromTSM(String bleDevAddr, final ResultCallback<List<AppInformation>> callback) {
        if (callback == null)
            return;
        if(MyApplication.seId.isEmpty()) {
            LogUtil.debug("seid is empty.");
            WebserviceHelper.getListDataWithoutSeid(bleDevAddr, callback);
        }
        else
            WebserviceHelper.getListDataWithSeid(MyApplication.seId,callback);
//        WebserviceHelper.getListDataFromWebService(bleDevAddr, MyApplication.seId, callback);
    }

    public static void stopTransactWithTSM(String requestTag){
        RequestQueueUtils.getInstance().cancelPendingRequest(requestTag);
    }

    public static void stopTransactWithSE(BluetoothControl bluetoothControl){
        if (bluetoothControl!=null)
            bluetoothControl.setbStopTransferApdu(true);
    }

//    public static void transactWithTSM(TSMRequestData requestData, String requestTag,final ResultCallback callback){
//
////        final TSMRequestData requestData = MessageBuilder.getTSMRequestDataFromXml(requestXml);
//        //创建request xml
//        String requestXml = MessageBuilder.buildBussinessRequestXml(requestData.seId,requestData.imei,requestData.phone,
//                requestData.sessionId,requestData.taskId);
//        final String sessionId = requestData.getSessionId();
//        final String taskId = requestData.getTaskId();
//        final String url = NetParameter.TSM_URL;
////        RequestQueue requestQueue = RequestQueueUtils.getRequestQueue();
//        final CustomStringRequest stringRequest = new CustomStringRequest(Request.Method.POST, url,
//                requestXml.getBytes(), new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                if (callback==null){
//                    return;
//                }
//                if (response.isEmpty()){
//                    callback.onFailed(response);
//                }
//                //解析xml，该xml中包含多条APDU指令
//                final TSMResponseData responseData = MessageBuilder.parseBussinessResponseXml(response,sessionId,taskId);
//                List<APDUInfo> apduInfoList = responseData.getApduInfoList();
//                callback.onSuccess(apduInfoList);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                callback.onFailed(error.getMessage());
//            }
//        });
//        //添加到全局RequestQueue
////        requestQueue.add(stringRequest);
//        RequestQueueUtils.getInstance().addToRequestQueue(stringRequest,requestTag);
//    }
//
//    public void transactWithSE(BluetoothControl bluetoothControl, List<APDUInfo> apduInfoList,TSMRequestData requestData,
//                               final ResultCallback<String> callback){
//        final String sessionId = requestData.getSessionId();
//        final String taskId = requestData.getTaskId();
//        new BussinessTransaction().handleApduList(bluetoothControl,apduInfoList,0, new TransactionCallback() {
//            @Override
//            public void onTransactionSuccess(APDUInfo apduInfo) {
//                String responseXml = MessageBuilder.message_Response_handle(MyApplication.seId,"","",sessionId,taskId,apduInfo,"0");
//                callback.onSuccess(responseXml);
//
//            }
//            @Override
//            public void onTransactionFailed(APDUInfo apduInfo) {
//                String responseXml = MessageBuilder.message_Response_handle(MyApplication.seId,"","",sessionId,taskId,apduInfo,"0");
//                callback.onFailed(responseXml);
//            }
//        });
//    }

    /**
     * 执行心跳线程的任务，锁定/解锁
     * TSM和SE交互，执行applet相关任务
     * 该函数的实现流程：与TSM和SE进行交互，交易流程：客户端向TSM发起请求（requestXml)->TSM给出响应（APDU指令集）->交给SE循环处理->SE返回最后结果->交给ResultCallback
     * 与TSM交易流程：上述返回结果->
     * @param requestXml
     * @param callback
     */
    public static void transactWithTSMAndSE(String requestXml,
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
                        app.getBluetoothDevAddr());
                if (bluetoothControl==null){
                    callback.onApduEmpty();
                    return;
                }
                bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
                    @Override
                    public void onBLEPrepared() {
                        //交给SE处理APDU指令，返回最后一条APDU指令处理结果
                        new BussinessTransaction().handleApduList(bluetoothControl,apduInfoList,0, new TransactionCallback() {
                            @Override
                            public void onTransactionSuccess(APDUInfo apduInfo) {
                                LogUtil.debug("HEARTBEAT","onTransactionSuccess");
                                String responseXml = MessageBuilder.message_Response_handle(MyApplication.seId,"","","4",sessionId,taskId,apduInfo,"0");
                                LogUtil.debug("HEARTBEAT",responseXml);
                                callback.onApduExcutedSuccess(responseXml);
                                //关闭蓝牙
                                bluetoothControl.disconnectBluetooth();

                            }
                            @Override
                            public void onTransactionFailed(APDUInfo apduInfo) {
                                LogUtil.debug("HEARTBEAT","onTransactionFailed");
                                String responseXml = MessageBuilder.message_Response_handle(MyApplication.seId,"","","0",sessionId,taskId,apduInfo,"0");
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
     *执行下载/删除/同步/个人化等与applet相关的任务
     * @param item  applet信息
     * @param taskType  任务类型
     * @param completeCallback 执行结果回调
     */
    public static void transactBussiness(final AppInformation item, String taskType,
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

    public static final String TASK_TYPE_DOWNLOAD="D1";
    public static final String TASK_TYPE_DELETE="D2";
    public static final String TASK_TYPE_PERSONALIZE="D4";
    public static final String TASK_TYPE_SYNC="DA";

}
