package com.spreadtrum.iit.zpayapp.network.volley_okhttp;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.spreadtrum.iit.zpayapp.common.MyApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SPREADTRUM\ting.long on 16-9-28.
 * 自定义带byte(String)类型参数的，自定义Header的Request
 */
public class CustomStringRequest extends StringRequest {
    //body内容,在这里是将xml转换成二进制格式
    private byte[] entity;

    /**
     * 创建通过POST传递二进制类型参数的request
     * @param method    POST
     * @param url   URL
     * @param entity    二进制参数，可由String类型getBytes()得到
     * @param listener  成功响应回调
     * @param errorListener 失败响应回调
     */
    public CustomStringRequest(int method, String url, byte[] entity,
                               Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.entity = entity;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return this.entity;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "text/xml; charset=utf-8");
        headers.put("Content-Length",String.valueOf(entity.length));
        //添加自定义字段
        SharedPreferences pref = MyApplication.getContextObject().getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = pref.getString("token","");
//        String token = "12132432aaa";
        headers.put("token",token);
        return headers;
    }

    @Override
    public String getBodyContentType() {
        //如果不返回null，则会默认的写一个
        // Content-type:application/x-www-form-urlencoded;charset=utf-8
        return null;
    }
}
