package com.spreadtrum.iit.zpayapp.network.tcp;

import com.spreadtrum.iit.zpayapp.utils.LogUtil;
import com.spreadtrum.iit.zpayapp.bussiness.TsmTaskCompleteCallback;
import com.spreadtrum.iit.zpayapp.utils.ByteUtil;
import com.spreadtrum.iit.zpayapp.common.MyApplication;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.SECallbackTSMListener;

import java.util.Random;

/**
 * Created by SPREADTRUM\ting.long on 16-9-5.
 */
public class TCPTransferData {
    private BluetoothControl bluetoothControl = null;
    private TCPSocket mTcpSocket = null;

    /**
     * 处理下载/删除/个人化/同步任务，各任务通过taskId来区分
     * @param bluetoothControl 与蓝牙通信实例
     * @param taskId    TSM定义的Taskid
     * @param callback 执行结果回调
     */
    public void handleTaskOfApplet(final BluetoothControl bluetoothControl, final byte[] taskId,
                                   final TsmTaskCompleteCallback callback){
        if(bluetoothControl==null) {
            LogUtil.debug("bluetoothControl is null");
            callback.onTaskNotExecuted();
            return;
        }
        this.bluetoothControl=bluetoothControl;
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte []bSeNO = MyApplication.seId.getBytes();
//                byte[] id = {0x2F, 0x7A};
//                byte[] taskId = new byte[20];
//                System.arraycopy(id,0,taskId,18,2);
                byte[] randomNum = generateRandom(12);
                byte[] input = genAppRequestByte(randomNum,bSeNO,taskId);
                if (mTcpSocket == null) {
                    mTcpSocket = TCPSocket.getInstance(TCPNetParameter.IPAddress, TCPNetParameter.Port);
                    if(mTcpSocket==null){
                        if(callback!=null){
                            callback.onTaskExecutedFailed();
                            //关闭蓝牙连接
                            bluetoothControl.disconnectBluetooth();
                        }
                    }
                    else
                        appRequest(mTcpSocket,input,randomNum,callback);
                }

            }
        }).start();
    }

    public void SyncApplet(BluetoothControl bluetoothControl, final byte[] taskId, final TsmTaskCompleteCallback tsmTaskCompleteCallback){//,TCPSocket tcpSocket, byte[] request, int length){
        if(bluetoothControl==null) {
            //bluetoothControl = new BluetoothControl(context, bluetoothDev);
            LogUtil.debug("bluetoothControl is null");
            return;
        }
        this.bluetoothControl=bluetoothControl;
        //this.tsmTaskCompleteCallback=callback;
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte []bSeNO = MyApplication.seId.getBytes();
//                byte[] id = {0x2E, 0x6E};
//                byte[] taskId = new byte[20];
//                System.arraycopy(id,0,taskId,18,2);
                byte[] randomNum = generateRandom(12);
                byte[] input = genAppRequestByte(randomNum,bSeNO,taskId);
                if (mTcpSocket == null) {
                    mTcpSocket = TCPSocket.getInstance(TCPNetParameter.IPAddress, TCPNetParameter.Port);
                    if(mTcpSocket==null){
                        if(tsmTaskCompleteCallback!=null)
                            tsmTaskCompleteCallback.onTaskExecutedFailed();
                    }
                    else
                        appRequest(mTcpSocket,input,randomNum,tsmTaskCompleteCallback);
                }

            }
        }).start();

    }

    public void DownloadApplet(BluetoothControl bluetoothControl, final byte[] taskId,
                               final TsmTaskCompleteCallback callback){//},TCPSocket tcpSocket, byte[] request, int length){
        if(bluetoothControl==null) {
            LogUtil.debug("bluetoothControl is null");
            return;
        }

        this.bluetoothControl=bluetoothControl;
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte []bSeNO = MyApplication.seId.getBytes();
//                byte[] id = {0x2F, 0x7A};
//                byte[] taskId = new byte[20];
//                System.arraycopy(id,0,taskId,18,2);
                byte[] randomNum = generateRandom(12);
                byte[] input = genAppRequestByte(randomNum,bSeNO,taskId);
                if (mTcpSocket == null) {
                    mTcpSocket = TCPSocket.getInstance(TCPNetParameter.IPAddress, TCPNetParameter.Port);
                    if(mTcpSocket==null){
                        if(callback!=null){
                            callback.onTaskExecutedFailed();
                        }
                    }
                    else
                        appRequest(mTcpSocket,input,randomNum,callback);
                }

            }
        }).start();
    }

    public void DeleteApplet(BluetoothControl bluetoothControl, final byte[] taskId, final TsmTaskCompleteCallback tsmTaskCompleteCallback){
        if(bluetoothControl==null){
            LogUtil.debug("bluetoothControl is null");
            return;
        }
        this.bluetoothControl=bluetoothControl;
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte []bSeNO = MyApplication.seId.getBytes();
                byte[] randomNum = generateRandom(12);
                byte[] input = genAppRequestByte(randomNum,bSeNO,taskId);
                if(mTcpSocket==null){
                    mTcpSocket = TCPSocket.getInstance(TCPNetParameter.IPAddress,TCPNetParameter.Port);
                    if(mTcpSocket==null){
                        if(tsmTaskCompleteCallback!=null)
                            tsmTaskCompleteCallback.onTaskExecutedFailed();
                    }
                    else
                        appRequest(mTcpSocket,input,randomNum,tsmTaskCompleteCallback);

                }
            }
        }).start();
    }

    /**
     * 给TSM发送请求，获取响应结果；分析响应结果
     * @param tcpSocket tcpSocket实例
     * @param input 业务请求数据
     * @param tsmTaskCompleteCallback 业务执行结果回调
     */
    public void handleTSMData(final TCPSocket tcpSocket, byte []input, final TsmTaskCompleteCallback tsmTaskCompleteCallback){
        LogUtil.debug("input:"+ByteUtil.bytesToHexString(input,input.length));
        if (tcpSocket==null) {
            LogUtil.debug("socket is null");
            tsmTaskCompleteCallback.onTaskExecutedFailed();
            //关闭蓝牙连接
            bluetoothControl.disconnectBluetooth();
            return;
        }
        new TCPRequest().TCPByteRequest(tcpSocket, input, new TCPResponse.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response, int responseLen) {
                ///////////////////TEST/////////////
                if(false) {
                    LogUtil.debug("TEST onResponse");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LogUtil.debug("success");
                    tcpSocket.closeSocket();
                    mTcpSocket = null;
//                    tsmTaskCompleteListener.onTaskExecutedSuccess();
                    tsmTaskCompleteCallback.onTaskExecutedSuccess();
                    //关闭蓝牙连接
                    bluetoothControl.disconnectBluetooth();
                    return;
                }
                ///////////////////////////////
//                LogUtil.debug("TCP",response);
                int cmdParaLen = 0;
                cmdParaLen = (response[15] << 8) + response[16];
                //byte类型是有符号数据，范围-128~127，当大于127时，需要取补
                if(cmdParaLen<0)
                    cmdParaLen+=256;
                //cmdParaLen += cmdParaLen + ;
                if (responseLen == (cmdParaLen + 17))
                    LogUtil.warn( "TCP response correct");
                else {
                    LogUtil.warn( "TCP response error");
//                    tsmTaskCompleteListener.onTaskExecutedFailed();
                    tsmTaskCompleteCallback.onTaskExecutedFailed();
                    //关闭蓝牙连接
                    bluetoothControl.disconnectBluetooth();
                    return;
                }
                if(response[14]==CMD_SERVER_APDU){
                    LogUtil.warn("CMD_SERVER_APDU");
                    LogUtil.debug( ByteUtil.bytesToHexString(response,responseLen));
                    //将APDU指令发给SE处理，并通过回调获取返回结果
                    if(bluetoothControl!=null){
                        byte[] seData = new byte[responseLen - 17];
                        System.arraycopy(response, 17, seData, 0, responseLen - 17);
                        //LogUtil.debug(TAG,"send to SE:"+bytesToHexString(seData,seData.length));
                        bluetoothControl.communicateWithJDSe(seData,seData.length);
                    }

                }else if(response[14]==CMD_SERVER_END){
                    LogUtil.debug("CMD_SERVER_END");
                    if(cmdParaLen==1 && (response[17]==0x00)){
                        LogUtil.debug("success");
                        tcpSocket.closeSocket();
                        mTcpSocket = null;
//                        tsmTaskCompleteListener.onTaskExecutedSuccess();
                        tsmTaskCompleteCallback.onTaskExecutedSuccess();
                        //关闭蓝牙连接
                        bluetoothControl.disconnectBluetooth();
                    }
                    else {
                        tcpSocket.closeSocket();
                        mTcpSocket = null;
//                        LogUtil.debug( ByteUtil.bytesToHexString(response,responseLen));
                        LogUtil.debug( "failed");
//                        tsmTaskCompleteListener.onTaskExecutedFailed();
                        tsmTaskCompleteCallback.onTaskExecutedFailed();
                        //关闭蓝牙连接
                        bluetoothControl.disconnectBluetooth();
                    }
                    return;

                }else if(response[14]==CMD_SERVER_RESET){
                    LogUtil.warn("CMD_SERVER_RESET");
                    tcpSocket.closeSocket();
                    mTcpSocket=null;
                }
                else
                {
                    LogUtil.warn("unknown cmd");
                    tcpSocket.closeSocket();
                    mTcpSocket=null;
//                    tsmTaskCompleteListener.onTaskExecutedFailed();
                    tsmTaskCompleteCallback.onTaskExecutedFailed();
                    //关闭蓝牙连接
                    bluetoothControl.disconnectBluetooth();
                }
                //LogUtil.debug(TAG,bytesToHexString(response,responseLen));
            }
        }, new TCPResponse.ErrorListener() {
            @Override
            public void onErrorResponse(Object response) {
                LogUtil.debug((String)response);
//                tsmTaskCompleteListener.onTaskExecutedFailed();
                tsmTaskCompleteCallback.onTaskExecutedFailed();
                //关闭蓝牙连接
                bluetoothControl.disconnectBluetooth();
            }
        });
    }

    /**
     * 有关applet的业务请求（
     * @param tcpSocket tcpSocket实例
     * @param input 发送个给TSM的业务请求数据,根据不同的input，执行不同的任务
     * @param randomNum 随机数
     * @param tsmTaskCompleteCallback 业务执行结果回调
     */
    public void appRequest(TCPSocket tcpSocket, byte[] input, final byte[] randomNum, final TsmTaskCompleteCallback tsmTaskCompleteCallback){
        handleTSMData(tcpSocket,input,tsmTaskCompleteCallback);
        if(bluetoothControl!=null){
            bluetoothControl.setSeCallbackTSMListener(new SECallbackTSMListener() {
                @Override
                public void callbackTSM(byte[] responseData, int responseLen) {
                    LogUtil.debug("callbackTSM");//TAG,"response from SE:"+bytesToHexString(responseData,responseLen));
                    byte[] response = new byte[17 + responseLen];
                    response[0] = 0x12;
                    response[1] = (byte) 0xab;
                    System.arraycopy(randomNum, 0, response, 2, randomNum.length);
                    response[14] = CMD_SE_RESPONSE;
                    response[15] = (byte) (responseLen >> 8);
                    response[16] = (byte) (responseLen);
                    System.arraycopy(responseData, 0, response, 17, responseLen);
                    handleTSMData(mTcpSocket,response,tsmTaskCompleteCallback);
                }

                @Override
                public void errorCallback() {
//                    tsmTaskCompleteListener.onTaskExecutedFailed();
                    tsmTaskCompleteCallback.onTaskExecutedFailed();
                    //关闭蓝牙连接
                    bluetoothControl.disconnectBluetooth();
                    LogUtil.debug("tsmTaskCompleteCallback.onTaskExecutedFailed();");
                }
            });
        }
    }

    /**
     * 根据《华虹电信卡管理平台(HCMP)_POS化发行详细实现方案.pdf》的通信协议生成请求数据
     * @param randomNum
     * @param bSeId
     * @param taskId
     * @return
     */
    public byte[] genAppRequestByte(byte[] randomNum,byte[] bSeId,byte[] taskId){
        byte[] input = new byte[38+bSeId.length];
        byte[] cmd = {0x01, 0x00, 0x39, 0x24};
        byte[] bSeNO = MyApplication.seId.getBytes();
        int paraLength = 1+bSeId.length+20;
        System.arraycopy(maskId, 0, input, 0, maskId.length);
        System.arraycopy(randomNum, 0, input, maskId.length, randomNum.length);
        input[maskId.length+randomNum.length]=CMD_APP_REQUEST;
        input[maskId.length+randomNum.length+1]=(byte)((paraLength>>8) & 0xFF);
        input[maskId.length+randomNum.length+2]=(byte)(paraLength & 0xFF);
        input[maskId.length+randomNum.length+3]=(byte)bSeId.length;
        System.arraycopy(bSeNO, 0, input, 14 + 1+2+1, bSeNO.length);
        System.arraycopy(taskId, 0, input, 14 + 1+2+1+bSeNO.length, taskId.length);
        return input;

    }

    private byte[] generateRandom(int byteOfLen){
        byte[] byteOfRandom = new byte[byteOfLen];
        Random ra =new Random();
        for(int i=0;i<byteOfLen;i++){
            byteOfRandom[i] = (byte) ra.nextInt(255);
        }
        return byteOfRandom;
    }

    public static byte CMD_APP_REQUEST = 0x01;
    public static byte CMD_SERVER_APDU = 0x02;
    public static byte CMD_SE_RESPONSE = 0x03;
    public static byte CMD_SERVER_END = 0x04;
    public static byte CMD_SERVER_RESET = 0x05;
    byte[] maskId = {0x12, (byte) 0xab};
//    public byte[] randomNum = {0x01, 0x02, 0x03, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc};
//    private void generateRandomData() {
//        randomNum = new byte[12];
//    }
}
