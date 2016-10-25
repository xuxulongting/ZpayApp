package com.spreadtrum.iit.zpayapp.network.http;

/**
 * Created by SPREADTRUM\ting.long on 16-10-12.
 */

public class HttpResponse {
    /** Called when a response is received. */
    public interface Listener<T>{
        /**
         * TSM正确响应回调方法
         * @param response
         */
        public void onResponse(T response);

    }

    public interface ErrorListener<T>{
        /**
         * TSM错误响应回掉方法
         * @param response
         */
        void onErrorResponse(T response);
    }
}
