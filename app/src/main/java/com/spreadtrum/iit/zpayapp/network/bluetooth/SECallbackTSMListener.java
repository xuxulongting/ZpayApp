package com.spreadtrum.iit.zpayapp.network.bluetooth;

/**
 * Created by SPREADTRUM\ting.long on 16-8-12.
 * 调用communicateWithJDSe（），通过setSeCallbackTSMListener()监听回调结果
 */
public interface SECallbackTSMListener{
    /**
     *当SE成功执行APDU指令
     * @param responseData
     * @param responseLen
     */
    void callbackTSM(byte[] responseData,int responseLen);

    /**
     *当SE执行APDU指令失败
     */
    void errorCallback();
}
