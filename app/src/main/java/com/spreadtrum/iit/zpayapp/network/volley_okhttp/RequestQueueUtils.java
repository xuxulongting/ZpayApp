package com.spreadtrum.iit.zpayapp.network.volley_okhttp;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.spreadtrum.iit.zpayapp.common.MyApplication;

/**
 * Created by SPREADTRUM\ting.long on 16-10-11.
 */

public class RequestQueueUtils {
    private static RequestQueue requestQueue;
    public static RequestQueue getRequestQueue()
    {
        if(requestQueue==null){
            requestQueue = Volley.newRequestQueue(MyApplication.getContextObject(),new OkHttpStack());
        }
        return requestQueue;
    }
}
