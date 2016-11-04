package com.spreadtrum.iit.zpayapp.network;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction;
import com.spreadtrum.iit.zpayapp.bussiness.TransactionCallback;
import com.spreadtrum.iit.zpayapp.common.ByteUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.message.APDUInfo;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.TSMRequestData;
import com.spreadtrum.iit.zpayapp.message.TSMResponseData;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.SECallbackTSMListener;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.CustomStringRequest;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.RequestQueueUtils;
import com.spreadtrum.iit.zpayapp.network.webservice.WebserviceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

        RequestQueue requestQueue = RequestQueueUtils.getRequestQueue();//Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("version",versionName);
            jsonObject.put("logName",userName);
            jsonObject.put("logPwd",pwd);
        } catch (JSONException e) {
//            e.printStackTrace();
            callback.onFailed(e.getMessage());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, NetParameter.LOGIN_URL, jsonObject,
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
        requestQueue.add(request);
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
        final RequestQueue requestQueue = RequestQueueUtils.getRequestQueue();
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
        requestQueue.add(jsonObjectRequest);
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
        RequestQueue requestQueue = RequestQueueUtils.getRequestQueue();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("version",versionName);
            jsonObject.put("logName",userName);
            jsonObject.put("logPwd",pwd);
            jsonObject.put("checkCode",checkCode);
        } catch (JSONException e) {
            callback.onFailed(e.getMessage());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, NetParameter.REGISTER_URL, jsonObject,
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
        requestQueue.add(request);
    }

    /**
     * 获取应用列表（检查蓝牙设备地址，获取Seid,获取应用列表）
     * @param bleDevAddr
     * @param callback
     */
    public static void getListDataFromTSM(String bleDevAddr, final ResultCallback<List<AppInformation>> callback) {
//        if (callback == null)
//            return;
//        //打开蓝牙
//        final BluetoothControl bluetoothControl = BluetoothControl.getInstance(MyApplication.getContextObject(),
//                bleDevAddr);
//
//        bluetoothControl.setBlePreparedCallbackListener(new BLEPreparedCallbackListener() {
//            @Override
//            public void onBLEPrepared() {
//                WebserviceHelper.getListDataFromWebService(bluetoothControl, MyApplication.seId, callback);
//            }
//
//        });

        WebserviceHelper.getListDataFromWebService(null, MyApplication.seId, callback);

    }

    /**
     * 该函数的实现流程：与TSM和SE进行交互，交易流程：客户端向TSM发起请求（requestXml)->TSM给出响应（APDU指令集）->交给SE循环处理->SE返回最后结果->交给ResultCallback
     * 与TSM交易流程：上述返回结果->
     * @param bluetoothControl
     * @param requestXml
     * @param callback
     */
    public static void transactWithTSM(final BluetoothControl bluetoothControl, String requestXml, final ResultCallback<String> callback){
        final TSMRequestData requestData = MessageBuilder.getTSMRequestDataFromXml(requestXml);
        final String sessionId = requestData.getSessionId();
        final String taskId = requestData.getTaskId();
        final String url = NetParameter.TSM_URL;
        RequestQueue requestQueue = RequestQueueUtils.getRequestQueue();
        final CustomStringRequest stringRequest = new CustomStringRequest(Request.Method.POST,url,
                requestXml.getBytes(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.isEmpty()){
                    callback.onSuccess(response);
                }
                //解析xml，该xml中包含多条APDU指令
                final TSMResponseData responseData = MessageBuilder.parseBussinessResponseXml(response,sessionId,taskId);
                List<APDUInfo> apduInfoList = responseData.getApduInfoList();
                if(apduInfoList==null)
                    return;
                if(bluetoothControl==null)
                    return;
                //交给SE处理APDU指令，返回最后一条APDU指令处理结果
                new BussinessTransaction().handleApduList(bluetoothControl,apduInfoList,0, new TransactionCallback() {
                    @Override
                    public void onTransactionSuccess(APDUInfo apduInfo) {
                        String responseXml = MessageBuilder.message_Response_handle(MyApplication.seId,"","",sessionId,taskId,apduInfo,"0");
                        callback.onSuccess(responseXml);

                    }
                    @Override
                    public void onTransactionFailed(APDUInfo apduInfo) {
                        String responseXml = MessageBuilder.message_Response_handle(MyApplication.seId,"","",sessionId,taskId,apduInfo,"0");
                        callback.onFailed(responseXml);
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailed(error.getMessage());
            }
        });
        requestQueue.add(stringRequest);
    }

    public static void getTaskId(){

    }


}
