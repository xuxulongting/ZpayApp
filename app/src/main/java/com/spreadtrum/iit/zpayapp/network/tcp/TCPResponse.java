package com.spreadtrum.iit.zpayapp.network.tcp;

/**
 * Created by SPREADTRUM\ting.long on 16-8-11.
 */
public class TCPResponse<T> {
    public interface Listener<T>{
        /** Called when a response is received. */
        public void onResponse(T response,int responseLen);

    }

    public interface ErrorListener<T>{
        void onErrorResponse(T response);
    }
}
