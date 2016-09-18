package com.spreadtrum.iit.zpayapp.network.webservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by SPREADTRUM\ting.long on 16-8-26.
 */
public class HttpUtils {
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
                            listener.onSuccess(conn.getInputStream());
                            //return parseSOAP(conn.getInputStream());
                        }
                        else
                            listener.onError("Connection failed!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if(listener!=null){
                        listener.onError(e.getMessage());
                    }
                }
            }
        }).start();

    }

    public static void sendHttpRequest(final String url, final HttpCallbackListener listener){
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
                    e.printStackTrace();
                    if(listener!=null){
                        listener.onError(e.getMessage());
                    }
                }
            }
        }).start();
    }
}
