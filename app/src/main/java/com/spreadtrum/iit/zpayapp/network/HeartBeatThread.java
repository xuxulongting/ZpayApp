package com.spreadtrum.iit.zpayapp.network;

import com.spreadtrum.iit.zpayapp.common.ByteUtil;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.TSMRequestData;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;

import java.util.Random;

/**
 * Created by SPREADTRUM\ting.long on 16-12-16.
 */

public class HeartBeatThread implements Runnable {
    private BluetoothControl bluetoothControl;
    private TSMRequestData requestData;
    private boolean bEnded = false;
    private boolean bContinued = false;
    private String requestXml;
    public HeartBeatThread(BluetoothControl bluetoothControl,TSMRequestData requestData){
        this.bluetoothControl = bluetoothControl;
        this.requestData = requestData;
    }

    private byte[] generateSessionId(int byteOfLen){
        byte[] byteOfRandom = new byte[byteOfLen];
        Random ra =new Random();
        for(int i=0;i<byteOfLen;i++){
            byteOfRandom[i] = (byte) ra.nextInt(255);
        }
        return byteOfRandom;
    }

    @Override
    public void run() {
        while (true) {
            if (!bEnded) {
                if(!bContinued) {//bContinued为false，开始发送一次新的心跳包；为true，表示上一次心跳包还没有结束，将SE的响应重新组合responseXml发送给TSM
                    //创建request xml
                    byte[] byteOfRandom = generateSessionId(10);
                    requestData.setSessionId(ByteUtil.bytesToString(byteOfRandom,10));
                    requestXml = MessageBuilder.buildBussinessRequestXml(requestData.seId, requestData.imei, requestData.phone,
                            requestData.sessionId, requestData.taskId);
                }
                ZAppStoreApi.transactWithTSMAndSE(bluetoothControl, requestXml, new HeartBeatResultCallback<String>() {
                    @Override
                    public void onApduExcutedSuccess(String responseXml) {
                        requestXml = responseXml;
                        bEnded = true;
                        bContinued = true;
                    }

                    @Override
                    public void onApduExcutedFailed(String errorResponse) {
                        requestXml = errorResponse;
                        bEnded = true;
                        bContinued = true;
                    }

                    @Override
                    public void onApduEmpty() {
                        try {
                            //等待3s,发送下一次心跳包
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        bEnded = true;
                        bContinued = false;
                    }

                    @Override
                    public void onNetworkError(String errMsg) {
                        bEnded = false;
                        bContinued = false;
                    }
                });
            }
            else
                break;
        }
    }
}
