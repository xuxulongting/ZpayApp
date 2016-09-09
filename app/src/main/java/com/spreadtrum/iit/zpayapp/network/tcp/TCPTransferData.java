package com.spreadtrum.iit.zpayapp.network.tcp;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.common.ByteUtil;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.SECallbackTSMListener;

import java.io.IOException;

/**
 * Created by SPREADTRUM\ting.long on 16-9-5.
 */
public class TCPTransferData {
    private BluetoothControl bluetoothControl = null;
    private TCPSocket mTcpSocket = null;
    private TSMTaskCompleteCallback tsmTaskCompleteCallback=null;
    private TsmTaskCompleteListener tsmTaskCompleteListener=null;
    public void setTsmTaskCompleteListener(TsmTaskCompleteListener listener){
        tsmTaskCompleteListener = listener;
    }

    public void SyncApplet(BluetoothControl bluetoothControl,final byte[] taskId){//,TCPSocket tcpSocket, byte[] request, int length){
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
                byte []bSeNO = strSeId.getBytes();
//                byte[] id = {0x2E, 0x6E};
//                byte[] taskId = new byte[20];
//                System.arraycopy(id,0,taskId,18,2);
                byte[] input = genAppRequestByte(randomNum,bSeNO,taskId);
                if (mTcpSocket == null) {
                    try {
                        mTcpSocket = TCPSocket.getInstance(NetParameter.IPAddress, NetParameter.Port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                appRequest(mTcpSocket,input);
            }
        }).start();

    }

    public void DownloadApplet(BluetoothControl bluetoothControl,final byte[] taskId){//},TCPSocket tcpSocket, byte[] request, int length){
        if(bluetoothControl==null) {
//            bluetoothControl = new BluetoothControl(context, bluetoothDev);
            LogUtil.debug("bluetoothControl is null");
            return;
        }
        this.bluetoothControl=bluetoothControl;
//        this.tsmTaskCompleteCallback = callback;
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte []bSeNO = strSeId.getBytes();
//                byte[] id = {0x2F, 0x7A};
//                byte[] taskId = new byte[20];
//                System.arraycopy(id,0,taskId,18,2);
                byte[] input = genAppRequestByte(randomNum,bSeNO,taskId);
                if (mTcpSocket == null) {
                    try {
                        mTcpSocket = TCPSocket.getInstance(NetParameter.IPAddress, NetParameter.Port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                appRequest(mTcpSocket,input);
            }
        }).start();
    }

    public void DeleteApplet(BluetoothControl bluetoothControl, final byte[] taskId){
        if(bluetoothControl==null){
            LogUtil.debug("bluetoothControl is null");
            return;
        }
        this.bluetoothControl=bluetoothControl;
//        this.tsmTaskCompleteCallback=callback;
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte []bSeNO = strSeId.getBytes();
                byte[] input = genAppRequestByte(randomNum,bSeNO,taskId);
                if(mTcpSocket==null){
                    try {
                        mTcpSocket = TCPSocket.getInstance(NetParameter.IPAddress,NetParameter.Port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    appRequest(mTcpSocket,input);
                }
            }
        }).start();
    }

    public void InstallApplet(){

    }

    public void handleTSMData(final TCPSocket tcpSocket,byte []input){
        //testSeCmdCount++;
        LogUtil.debug("input:"+ByteUtil.bytesToHexString(input,input.length));
        TCPByteRequest request = new TCPByteRequest(tcpSocket, input, new TCPResponse.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response, int responseLen) {
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
                    tsmTaskCompleteListener.onTaskExecutedFailed();
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
                        LogUtil.warn("success");
                        tsmTaskCompleteListener.onTaskExecutedSuccess();
//                        if(tsmTaskCompleteCallback!=null) {
//                            tsmTaskCompleteCallback.onSuccess();
//                            tsmTaskCompleteCallback=null;
//                        }
                    }
                    else {
                        tcpSocket.closeSocket();
                        mTcpSocket = null;
                        LogUtil.warn( ByteUtil.bytesToHexString(response,responseLen));
                        LogUtil.warn( "failed");
                        tsmTaskCompleteListener.onTaskExecutedFailed();
//                        if(tsmTaskCompleteCallback!=null) {
//                            tsmTaskCompleteCallback.onFailed();
//                            tsmTaskCompleteCallback = null;
//                        }
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
                    tsmTaskCompleteListener.onTaskExecutedFailed();
                }
                //LogUtil.debug(TAG,bytesToHexString(response,responseLen));
            }
        }, new TCPResponse.ErrorListener() {
            @Override
            public void onErrorResponse(Object response) {
                LogUtil.debug((String)response);
                tsmTaskCompleteListener.onTaskExecutedFailed();
            }
        });
        request.start();
    }

    public void appRequest(TCPSocket tcpSocket,byte[] input){
        handleTSMData(tcpSocket,input);
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
                    handleTSMData(mTcpSocket,response);
                }
            });
        }
    }

    public byte[] genAppRequestByte(byte[] random,byte[] bSeId,byte[] taskId){
        byte[] input = new byte[38+bSeId.length];
        byte[] cmd = {0x01, 0x00, 0x39, 0x24};
        byte[] bSeNO = strSeId.getBytes();
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

    public static byte CMD_APP_REQUEST = 0x01;
    public static byte CMD_SERVER_APDU = 0x02;
    public static byte CMD_SE_RESPONSE = 0x03;
    public static byte CMD_SERVER_END = 0x04;
    public static byte CMD_SERVER_RESET = 0x05;
    public byte[] randomNum = {0x01, 0x02, 0x03, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc};
    String strSeId = "451000000000000020160328000000010005";
    byte[] maskId = {0x12, (byte) 0xab};
    private void generateRandomData() {
        randomNum = new byte[12];
    }
}
