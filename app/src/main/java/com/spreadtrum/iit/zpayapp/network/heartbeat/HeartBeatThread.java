package com.spreadtrum.iit.zpayapp.network.heartbeat;

import android.content.Intent;

import com.spreadtrum.iit.zpayapp.common.AppGlobal;
import com.spreadtrum.iit.zpayapp.utils.LogUtil;
import com.spreadtrum.iit.zpayapp.bussiness.BussinessTransaction;
import com.spreadtrum.iit.zpayapp.utils.ByteUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.message.MessageBuilder;
import com.spreadtrum.iit.zpayapp.message.TSMRequestData;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;

import java.util.Random;

/**
 * Created by SPREADTRUM\ting.long on 16-12-16.
 */

public class HeartBeatThread extends Thread implements Runnable {
    private TSMRequestData requestData;
    private boolean bEnded = false;
    private boolean bContinued = false;
    private String requestXml;

    /**
     * 生成随机数作为session ID
     * @param byteOfLen
     * @return
     */
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
//        LogUtil.debug("HEARTBEAT","heartbeat thread");
        while (true) {
            if (AppGlobal.seId.isEmpty())
                continue;
            requestData = new TSMRequestData();
            requestData.setSeId(AppGlobal.seId);
            requestData.imei="";
            requestData.phone="";
            requestData.taskId="";
            requestData.type = "3";
            break;
        }
        while (true){
//            LogUtil.debug("HEARTBEAT","heartbeat thread start");
            if (!bEnded) {
                LogUtil.debug("HEARTBEAT","heartbeat thread start");
                if(!bContinued) {//bContinued为false，开始发送一次新的心跳包；为true，表示上一次心跳包还没有结束，将SE的响应重新组合responseXml发送给TSM
                    //创建request xml
                    byte[] byteOfRandom = generateSessionId(10);
                    requestData.setSessionId(ByteUtil.bytesToString(byteOfRandom,10));
                    requestXml = MessageBuilder.buildBussinessRequestXml(requestData.seId, requestData.imei, requestData.phone,requestData.type,
                            requestData.sessionId, requestData.taskId,"");
                }
                bEnded = true;
                LogUtil.debug("HEARTBEAT","HeartBeatThread,thread id is:"+currentThread().getId());
                new BussinessTransaction().transactBussinessWithHeartBeat(requestXml, new HeartBeatResultCallback<String>() {
                    @Override
                    public void onApduExcutedSuccess(String responseXml) {
                        requestXml = responseXml;
                        bEnded = false;
                        bContinued = true;
                    }

                    @Override
                    public void onApduExcutedFailed(String errorResponse) {
                        requestXml = errorResponse;
                        bEnded = false;
                        bContinued = true;
                    }

                    @Override
                    public void onApduEmpty() {
//                        broadcastRMUpdate(HeartBeatThread.ACTION_BUSSINESS_REMOTE_MANAGEMENT);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.debug("HEARTBEAT","onApduEmpty,thread id is:"+currentThread().getId());
                                try {
                                    //等待300s,发送下一次心跳包
                                    Thread.sleep(100000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                bEnded = false;
                                bContinued = false;
                            }
                        }).start();
                    }

                    @Override
                    public void onNetworkError(String errMsg) {
//                        bEnded = false;
//                        bContinued = false;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.debug("HEARTBEAT","onApduEmpty,thread id is:"+currentThread().getId());
                                try {
                                    //等待300s,发送下一次心跳包
                                    Thread.sleep(100000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                bEnded = false;
                                bContinued = false;
                            }
                        }).start();
                    }
                });
            }
        }
    }

    /**
     * 执行完任务（锁定/解锁）后，广播消息，通知应用列表进行刷新
     * @param action
     */
    public static void broadcastRMUpdate(String action){
        Intent intent = new Intent();
        intent.setAction(action);
        MyApplication.getContextObject().sendBroadcast(intent);
    }

    public static final String ACTION_BUSSINESS_REMOTE_MANAGEMENT=
            "com.spreadtrum.iit.zpayapp.network.heartbeat.HeartBeatThread.ACTION_BUSSINESS_EXECUTED_SUCCESS";
}
