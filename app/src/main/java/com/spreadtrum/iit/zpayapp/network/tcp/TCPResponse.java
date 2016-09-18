package com.spreadtrum.iit.zpayapp.network.tcp;

/**
 * Created by SPREADTRUM\ting.long on 16-8-11.
 */
public class TCPResponse<T> {
    /** Called when a response is received. */
    public interface Listener<T>{
        /**
         * TSM正确响应回调方法
         * @param response
         * @param responseLen
         */
        public void onResponse(T response,int responseLen);

    }

    public interface ErrorListener<T>{
        /**
         * TSM错误响应回掉方法
         * @param response
         */
        void onErrorResponse(T response);
    }
}
