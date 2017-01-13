package com.spreadtrum.iit.zpayapp.network.bluetooth;

/**
 * Created by SPREADTRUM\ting.long on 16-9-6.
 */


public interface BLEPreparedCallbackListener {
    /**
     * 蓝牙准备好后，异步调用
     * BLE发现设备，找到指定服务，开启通知，打开SE通道，这时TSM才能与SE正常交互
     */
    void onBLEPrepared();
    void onBLEPrepareFailed();
}
