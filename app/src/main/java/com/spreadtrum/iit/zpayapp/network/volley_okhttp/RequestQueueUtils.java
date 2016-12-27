package com.spreadtrum.iit.zpayapp.network.volley_okhttp;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.spreadtrum.iit.zpayapp.common.MyApplication;

/**
 * Created by SPREADTRUM\ting.long on 16-10-11.
 */

public class RequestQueueUtils {
    public static final String REQUEST_TAG = "Volley_Pattern";
    private static RequestQueue requestQueue;
    // 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载
    private static RequestQueueUtils requestQueueUtils=null;
    // 私有构造方法，防止被实例化
    private RequestQueueUtils(){

    }
    public static RequestQueueUtils getInstance(){
        //解决线程不安全的问题
        synchronized (RequestQueueUtils.class) {
            if (requestQueueUtils == null) {
                requestQueueUtils = new RequestQueueUtils();
            }
            return requestQueueUtils;
        }
    }
    public RequestQueue getRequestQueue()
    {
        if(requestQueue==null){
            requestQueue = Volley.newRequestQueue(MyApplication.getContextObject(),new OkHttpStack());
        }
        return requestQueue;
    }

    public void addToRequestQueue(Request request,String tag){
        request.setTag(TextUtils.isEmpty(tag)?REQUEST_TAG:tag);
        getRequestQueue().add(request);
    }

    public void addToRequestQueue(Request request){
        request.setTag(REQUEST_TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequest(String tag){
        if(requestQueue!=null){
            requestQueue.cancelAll(tag);
        }
    }
}
