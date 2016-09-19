package com.spreadtrum.iit.zpayapp.network.bluetooth;

/**
 * Created by SPREADTRUM\ting.long on 16-8-12.
 */
public interface SECallbackTSMListener{
    void callbackTSM(byte[] responseData,int responseLen);
    void errorCallback();
}
