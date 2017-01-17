package com.spreadtrum.iit.zpayapp.network.http;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.CustomStringRequest;
import com.spreadtrum.iit.zpayapp.network.volley_okhttp.RequestQueueUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Base64;


/**
 * Created by SPREADTRUM\ting.long on 16-8-26.
 */
public class HttpUtils {
    /**
     * 使用volley+okhttp发起网络请求，请求webservice服务
     * @param path
     * @param entity
     * @param listener
     * @param errorListener
     */
    public static void sendHttpRequestforWebservice(final String path, final byte[] entity,
                                                    Response.Listener listener,Response.ErrorListener errorListener){
        CustomStringRequest request = new CustomStringRequest(Request.Method.POST,path,entity,listener,errorListener);
        RequestQueueUtils.getInstance().addToRequestQueue(request);
    }
    /**
     * 通过HttpURLConnection发起网络请求，获取web service服务
     * @param path web service url
     * @param entity    soap协议的请求数据
     * @param listener  响应结果回调
     */
    public static void sendHttpRequestforWebservice(final String path, final byte[] entity, final HttpCallbackListener listener){

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) new URL(path).openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
//                    conn.setRequestProperty("Content-Type","application/soap+xml; charset=utf-8");
                    conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
                    conn.setRequestProperty("Content-Length",String.valueOf(entity.length));
                    conn.getOutputStream().write(entity);
                    int res = conn.getResponseCode();
                    if(listener!=null){
                        if(res==200){
                            InputStream inputStream = conn.getInputStream();
                            String response = inputStream.toString();
                            byte[] decodeByte = Base64.decode(response,Base64.DEFAULT);
                            String responseXml = new String(decodeByte);
                            LogUtil.debug(responseXml);
                            listener.onSuccess(inputStream);
                            //return parseSOAP(conn.getInputStream());
                        }
                        else
                            listener.onError("Connection failed!");
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                    if(listener!=null){
                        listener.onError(e.getMessage());
                    }
                }
            }
        }).start();

    }

    public static void sendHttpRequest(final String url, String xml,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
//                    InputStream in = conn.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
//                    out.write();
                    int res = conn.getResponseCode();
                    if(listener!=null){
                        if(res==200){
                            listener.onSuccess(conn.getInputStream());
                            //return parseSOAP(conn.getInputStream());
                        }
                        else
                            listener.onError("Connection failed!");
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                    if(listener!=null){
                        listener.onError(e.getMessage());
                    }
                }
            }
        }).start();
    }

    /**
     * 向http服务器发送xml请求，回调获取结果
     * @param url   url
     * @param xml   请求xml
     * @param callback  响应回调
     */
    public static void xmlStringRequest(final String url, final String xml,final HttpResponseCallback<String> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "error";
                HttpURLConnection httpConn = null;
                BufferedOutputStream bos = null;
                BufferedInputStream bis = null;
                ByteArrayOutputStream baos = null;
                String charset = "UTF-8";
                URL objUrl = null;
                try {
                    objUrl = new URL(url);
                    httpConn = (HttpURLConnection) objUrl.openConnection();
                    httpConn.setRequestMethod("POST");
                    httpConn.setDoOutput(true);
                    httpConn.setDoInput(true);
                    httpConn.setConnectTimeout(1000*15);
                    httpConn.setReadTimeout(1000*15);
                    httpConn.setUseCaches(false);
                    httpConn.setRequestProperty("Content-type", "text/xml");
                    httpConn.connect();
                    LogUtil.info("请求报文："+xml);
                    if (xml != null && !"".equals(xml)) {
                        httpConn.getOutputStream();
                        bos = new BufferedOutputStream(httpConn.getOutputStream());
                        bos.write(xml.getBytes(charset));
                        bos.flush();
                    }
                    LogUtil.debug("服务器返回的状态码为：" + httpConn.getResponseCode());
                    if (httpConn.getResponseCode() == 200) {
                        bis = new BufferedInputStream(httpConn.getInputStream());
                        baos = new ByteArrayOutputStream();
                        int c = 0;
                        byte[] buffer = new byte[8 * 1024];
                        while ((c = bis.read(buffer)) != -1) {
                            baos.write(buffer, 0, c);
                            baos.flush();
                        }
                        result = new String(baos.toByteArray(), charset);
                        LogUtil.info("响应报文："+result);

                    }

                } catch (Exception e) {
                    LogUtil.debug("访问网络异常：" + e.toString());
                    result = "error";
//            OSA.closeChannel();
                    LogUtil.debug("关闭通道！");
                } finally {
                    if (baos != null) {
                        try {
                            baos.close();
                        } catch (IOException e) {
                            LogUtil.debug("内存写入流关闭失败");
                            result = "error";
                        }
                    }
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            LogUtil.debug("字节缓冲输入流关闭失败");
                            result = "error";
                        }
                    }
                    if (bos != null) {
                        try {
                            bos.close();
                        } catch (IOException e) {
                            LogUtil.debug("字节缓冲输出流关闭失败");
                            result = "error";
                        }
                    }
                    if (httpConn != null) {
                        httpConn.disconnect();
                    }
                    if(result.equals("error")){
                        callback.onError(result);
                    }
                    else
                        callback.onSuccess(result);
                }
            }
        }).start();
    }
}
