package com.spreadtrum.iit.zpayapp.bussiness;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.spreadtrum.iit.zpayapp.utils.LogUtil;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.NetParameter;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.message.AppInformation;
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
     * 注册
     * @param userName
     * @param pwd
     * @param versionName
     * @param checkCode
     * @param callback
     */
    public static void register(String userName, String pwd, String versionName, String checkCode,
                                final ResultCallback<JSONObject> callback){
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
     * 获取Applet信息列表
     * @param bleDevAddr 蓝牙设备地址，通过蓝牙接口，获取Seid
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
}
