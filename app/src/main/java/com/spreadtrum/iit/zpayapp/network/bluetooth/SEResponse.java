package com.spreadtrum.iit.zpayapp.network.bluetooth;

/**
 * Created by SPREADTRUM\ting.long on 16-8-11.
 */

public class SEResponse<T>{
    public interface Listener<T>{
        void callbackTSM(T response,int responseLen);
    }
}
