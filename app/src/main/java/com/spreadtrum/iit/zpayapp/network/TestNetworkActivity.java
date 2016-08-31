package com.spreadtrum.iit.zpayapp.network;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.spreadtrum.iit.zpayapp.Log.LogUtil;
import com.spreadtrum.iit.zpayapp.R;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothControl;
import com.spreadtrum.iit.zpayapp.network.bluetooth.BluetoothDialogFragment;
import com.spreadtrum.iit.zpayapp.network.bluetooth.SECallbackTSMListener;
import com.spreadtrum.iit.zpayapp.network.bluetooth.SEResponse;
import com.spreadtrum.iit.zpayapp.network.tcp.NetParameter;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPByteRequest;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPResponse;
import com.spreadtrum.iit.zpayapp.network.tcp.TCPSocket;

import java.io.IOException;

/**
 * Created by SPREADTRUM\ting.long on 16-8-9.
 */
public class TestNetworkActivity extends AppCompatActivity implements BluetoothDialogFragment.SelectBluetoothDeviceListener{
    private Button btnTestNetwork;
    private Button btnSelBleDEv;
    private Button btnTestTSMandSE;
    private Button btnDownloadApp;
    private Button btnDeleteApp;
    private Button btnTesthttp;
    private BluetoothControl bluetoothControl = null;
    BluetoothDialogFragment dialogFragment;
    private TCPSocket mTcpSocket = null;

    private SEResponse.Listener seListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testnetwork);
        btnTestNetwork = (Button) findViewById(R.id.id_btn_interact);
        btnSelBleDEv = (Button) findViewById(R.id.id_btn_sel_ble);
        btnTestTSMandSE = (Button) findViewById(R.id.id_btn_tsm_and_se);
        btnDownloadApp = (Button) findViewById(R.id.id_btn_downloadapp);
        btnDeleteApp = (Button) findViewById(R.id.id_btn_deleteapp);
        btnTesthttp = (Button) findViewById(R.id.id_btn_testhttp);
        //测试http
        btnTesthttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //下载应用
        btnDownloadApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] id = {0x2F, 0x7A};
                        byte []bSeNO = strSeId.getBytes();
                        byte[] taskId = new byte[20];
                        System.arraycopy(id,0,taskId,18,2);
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
        });
        //删除应用
        btnDeleteApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] id = {0x2F, 0x79};
                        byte []bSeNO = strSeId.getBytes();
                        byte[] taskId = new byte[20];
                        System.arraycopy(id,0,taskId,18,2);
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
        });
        //选择蓝牙设备
        btnSelBleDEv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFragment = new BluetoothDialogFragment();
                dialogFragment.show(getFragmentManager(), "BLE DialogFragment");
                LogUtil.debug(TAG,"Show dialogfragment.");
            }
        });
        //连接TSM与SE
        btnTestTSMandSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mTcpSocket == null) {
                            try {
                                mTcpSocket = TCPSocket.getInstance(NetParameter.IPAddress, NetParameter.Port);
                            } catch (IOException e) {
                                //e.printStackTrace();
                                Toast.makeText(TestNetworkActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                        //准备发给TSM的数据
                        String strSeId = "451000000000000020160328000000010003";
                        byte[] maskId = {0x12, (byte) 0xab};
                        //byte[] randomNum = {0x01,0x02,0x03,0x4,0x5,0x6,0x7,0x8,0x9,0xa,0xb,0xc};
                        byte[] cmd = {0x01, 0x00, 0x39, 0x24};
                        byte[] bSeNO = strSeId.getBytes();
                        byte[] id = {0x2E, 0x6E};
                        byte[] input = new byte[2 + 12 + 1 + 2 + 57];
                        byte[] taskId = new byte[20];
                        System.arraycopy(id, 0, taskId, 18, id.length);
                        System.arraycopy(maskId, 0, input, 0, maskId.length);
                        System.arraycopy(randomNum, 0, input, maskId.length, randomNum.length);
                        System.arraycopy(cmd, 0, input, maskId.length + randomNum.length, cmd.length);
                        System.arraycopy(bSeNO, 0, input, maskId.length + randomNum.length + cmd.length, bSeNO.length);
                        System.arraycopy(taskId, 0, input, maskId.length + randomNum.length + cmd.length + bSeNO.length, taskId.length);
                        //发给TSM处理，并通过回调获取返回结果
                        //testSeCmdCount++;
                        handleTSMData(mTcpSocket,input);
                        if(bluetoothControl!=null) {
                            //SE返回结果，再调用TSM
                            bluetoothControl.setSeCallbackTSMListener(new SECallbackTSMListener() {
                                @Override
                                public void callbackTSM(byte[] responseData,int responseLen) {
                                    //LogUtil.debug(TAG,"response from SE:"+bytesToHexString(responseData,responseLen));
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
                }).start();
            }
        });


    }

    public void appRequest(TCPSocket tcpSocket,byte[] input){
        handleTSMData(tcpSocket,input);
        if(bluetoothControl!=null){
            bluetoothControl.setSeCallbackTSMListener(new SECallbackTSMListener() {
                @Override
                public void callbackTSM(byte[] responseData, int responseLen) {
                    //LogUtil.debug(TAG,"response from SE:"+bytesToHexString(responseData,responseLen));
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

    public void handleTSMData(final TCPSocket tcpSocket,byte []input){
            //testSeCmdCount++;
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
                    LogUtil.warn(TAG, "TCP response correct");
                else {
                    LogUtil.warn(TAG, "TCP response error");
                    return;
                }
                if(response[14]==CMD_SERVER_APDU){
                    LogUtil.warn(TAG,"CMD_SERVER_APDU");
                    //将APDU指令发给SE处理，并通过回调获取返回结果
                    if(bluetoothControl!=null){
                        byte[] seData = new byte[responseLen - 17];
                        System.arraycopy(response, 17, seData, 0, responseLen - 17);
                        //LogUtil.debug(TAG,"send to SE:"+bytesToHexString(seData,seData.length));
                        bluetoothControl.communicateWithSe(seData,seData.length);
                    }

                }else if(response[14]==CMD_SERVER_END){
                    LogUtil.debug(TAG,"CMD_SERVER_END");
                    if(cmdParaLen==1 && (response[17]==0x00)){
                        LogUtil.warn(TAG,"success");
                    }
                    else {
                        tcpSocket.closeSocket();
                        mTcpSocket = null;
                        LogUtil.warn(TAG,bytesToHexString(response,responseLen));
                        LogUtil.warn(TAG, "failed");
                    }
                    return;

                }else if(response[14]==CMD_SERVER_RESET){
                    LogUtil.warn(TAG,"CMD_SERVER_RESET");
                    tcpSocket.closeSocket();
                    mTcpSocket=null;
                }
                else
                {
                    LogUtil.warn(TAG,"unknown cmd");
                    tcpSocket.closeSocket();
                    mTcpSocket=null;
                }
                //LogUtil.debug(TAG,bytesToHexString(response,responseLen));
            }
        }, new TCPResponse.ErrorListener() {
            @Override
            public void onErrorResponse(Object response) {
                LogUtil.debug(TAG,(String)response);
            }
        });
        request.start();
    }

    @Override
    public void onBluetoothDeviceSelected(String devAddr) {
        LogUtil.debug(TAG, devAddr);
        LogUtil.debug(TAG, "onBluetoothDeviceSelected: " + devAddr);
        bluetoothControl = new BluetoothControl(this, devAddr);
        dialogFragment.dismiss();
    }

    public static String TAG = "BLE";
    public static byte CMD_APP_REQUEST = 0x01;
    public static byte CMD_SERVER_APDU = 0x02;
    public static byte CMD_SE_RESPONSE = 0x03;
    public static byte CMD_SERVER_END = 0x04;
    public static byte CMD_SERVER_RESET = 0x05;
    public byte[] randomNum = {0x01, 0x02, 0x03, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc};
    String strSeId = "451000000000000020160328000000010003";
    byte[] maskId = {0x12, (byte) 0xab};
    private void generateRandomData() {
        randomNum = new byte[12];
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

    public static String bytesToHexString(byte[] bytes,int byteOfLength) {
        String result = "";
        for (int i = 0; i < byteOfLength; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }


    @Override
    protected void onPause() {
        super.onPause();
        //if(bluetoothControl!=null)
        //    bluetoothControl.bluetoothUnregisterReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bluetoothControl!=null) {
            bluetoothControl.bluetoothUnregisterReceiver();
            bluetoothControl.bluetoothUnbindService();
        }
    }
}
