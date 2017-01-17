package com.spreadtrum.iit.zpayapp.network.bluetooth;

/**
 * Created by SPREADTRUM\ting.long on 16-9-6.
 *
 */
public interface OpenSECallbackListener {
    /**
     * 打开SE通道成功回调
     */
    void onSEOpenedSuccess();

    /**
     * 打开SE通道失败回调
     */
    void onSEOpenedFailed();
}
